package com.prosincerity.ghostwriter.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import java.io.FilterInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import java.util.zip.GZIPInputStream
import kotlin.coroutines.coroutineContext

internal data class DictionaryAsset(val file: String, val sizeBytes: Long, val url: String)
internal data class DictionaryLanguage(val wiktionary: DictionaryAsset, val espeak: DictionaryAsset) {
    val downloadBytes: Long get() = wiktionary.sizeBytes + espeak.sizeBytes
}

/** The app pins release URLs; it never needs to query GitHub while editing. */
internal class DictionaryRelease private constructor(
    val tag: String,
    val languages: Map<String, DictionaryLanguage>,
) {
    companion object {
        fun load(context: Context): DictionaryRelease {
            val json = JSONObject(
                context.assets.open("dictionary_release.json").bufferedReader().use { it.readText() },
            )
            val tag = json.getString("tag")
            require(tag.matches(Regex("[A-Za-z0-9._-]+")))
            val source = json.getJSONObject("languages")
            val languages = listOf("en", "de", "tr").associateWith { code ->
                val pair = source.getJSONObject(code)
                DictionaryLanguage(asset(pair.getJSONObject("wiktionary")), asset(pair.getJSONObject("espeak")))
            }
            return DictionaryRelease(tag, languages)
        }

        private fun asset(json: JSONObject): DictionaryAsset {
            val file = json.getString("file")
            val url = json.getString("url")
            require(file.matches(Regex("[A-Za-z0-9._-]+\\.db\\.gz")))
            require(url.startsWith("https://github.com/Prosincerity/Ghostwriter-Dict/releases/download/"))
            require(url.endsWith("/$file"))
            return DictionaryAsset(file, json.getLong("sizeBytes"), url)
        }
    }
}

internal fun interface DictionaryArchiveSource {
    fun open(url: String): InputStream
}

private val releaseSource = DictionaryArchiveSource { address ->
    val connection = URL(address).openConnection() as HttpURLConnection
    connection.connectTimeout = 15_000
    connection.readTimeout = 30_000
    connection.instanceFollowRedirects = true
    try {
        if (connection.responseCode != HttpURLConnection.HTTP_OK) {
            error("Dictionary download failed: HTTP ${connection.responseCode}")
        }
        object : FilterInputStream(connection.inputStream) {
            override fun close() {
                try {
                    super.close()
                } finally {
                    connection.disconnect()
                }
            }
        }
    } catch (error: Exception) {
        connection.disconnect()
        throw error
    }
}

internal data class DictionaryDownloadProgress(
    val downloadedBytes: Long,
    val totalBytes: Long,
)

/** Installs both sources for one language before exposing either database. */
internal class DictionaryInstaller(
    context: Context,
    private val source: DictionaryArchiveSource = releaseSource,
) {
    private val appContext = context.applicationContext
    val release = DictionaryRelease.load(appContext)
    private val root = File(appContext.filesDir, "dictionaries")

    fun installedDatabases(language: String): Pair<File, File>? {
        require(language in release.languages)
        val pair = File(File(root, language), release.tag)
        return databasePair(pair)
    }

    fun availableDatabases(language: String): Pair<File, File>? {
        installedDatabases(language)?.let { return it }
        val languageDir = File(root, language)
        return languageDir.listFiles()
            ?.asSequence()
            ?.filter { it.isDirectory && !it.name.startsWith(".") }
            ?.sortedByDescending { it.name }
            ?.mapNotNull(::databasePair)
            ?.firstOrNull()
    }

    private fun databasePair(pair: File): Pair<File, File>? {
        val wiktionary = File(pair, "wiktionary.db")
        val espeak = File(pair, "espeak.db")
        return if (wiktionary.isFile && espeak.isFile) wiktionary to espeak else null
    }

    suspend fun install(
        language: String,
        onProgress: (DictionaryDownloadProgress) -> Unit = {},
    ): Pair<File, File> = withContext(Dispatchers.IO) {
        require(language in release.languages)
        installedDatabases(language)?.let { return@withContext it }
        val pair = release.languages.getValue(language)
        val languageDir = File(root, language)
        check(languageDir.mkdirs() || languageDir.isDirectory) { "Cannot create dictionary directory" }
        val staging = File(languageDir, ".${release.tag}-${System.nanoTime()}.part")
        check(staging.mkdir()) { "Cannot create temporary dictionary directory" }
        try {
            var completedBytes = 0L
            for ((asset, name) in listOf(pair.wiktionary to "wiktionary.db", pair.espeak to "espeak.db")) {
                var received = 0L
                source.open(asset.url).use { input ->
                    val counted = object : FilterInputStream(input) {
                        override fun read(): Int {
                            val value = super.read()
                            if (value >= 0) received++
                            return value
                        }

                        override fun read(buffer: ByteArray, offset: Int, length: Int): Int {
                            val count = super.read(buffer, offset, length)
                            if (count > 0) received += count
                            return count
                        }
                    }
                    GZIPInputStream(counted).use { archive ->
                        File(staging, name).outputStream().buffered().use { output ->
                            val buffer = ByteArray(64 * 1024)
                            var lastReported = 0L
                            while (true) {
                                coroutineContext.ensureActive()
                                val count = archive.read(buffer)
                                if (count < 0) break
                                output.write(buffer, 0, count)
                                if (received - lastReported >= 256 * 1024) {
                                    onProgress(DictionaryDownloadProgress(
                                        (completedBytes + received).coerceAtMost(pair.downloadBytes),
                                        pair.downloadBytes,
                                    ))
                                    lastReported = received
                                }
                            }
                        }
                    }
                }
                completedBytes += received
            }
            val installed = File(languageDir, release.tag)
            check(!installed.exists() || installedDatabases(language) != null) {
                "Incomplete dictionary installation already exists"
            }
            if (installed.exists()) {
                installedDatabases(language)!!
            } else {
                check(staging.renameTo(installed)) { "Cannot activate downloaded dictionaries" }
                onProgress(DictionaryDownloadProgress(pair.downloadBytes, pair.downloadBytes))
                installedDatabases(language)!!
            }
        } finally {
            staging.deleteRecursively()
        }
    }

    fun openReadOnly(language: String): Pair<SQLiteDatabase, SQLiteDatabase>? {
        val (wiktionary, espeak) = availableDatabases(language) ?: return null
        val first = SQLiteDatabase.openDatabase(wiktionary.path, null, SQLiteDatabase.OPEN_READONLY)
        try {
            return first to SQLiteDatabase.openDatabase(espeak.path, null, SQLiteDatabase.OPEN_READONLY)
        } catch (error: Exception) {
            first.close()
            throw error
        }
    }
}
