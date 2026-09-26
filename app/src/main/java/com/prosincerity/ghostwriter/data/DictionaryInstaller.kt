package com.prosincerity.ghostwriter.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
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
    fun asset(source: DictionarySource): DictionaryAsset = when (source) {
        DictionarySource.WIKTIONARY -> wiktionary
        DictionarySource.ESPEAK -> espeak
    }
}

/** Declaration order is the lookup priority: curated pronunciations before generated data. */
internal enum class DictionarySource(val fileName: String, val pronunciationSource: PronunciationSource) {
    WIKTIONARY("wiktionary.db", PronunciationSource.WIKTIONARY),
    ESPEAK("espeak.db", PronunciationSource.ESPEAK_DATABASE),
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

/** Installs each source independently in versioned app-private storage. */
internal class DictionaryInstaller(
    context: Context,
    private val source: DictionaryArchiveSource = releaseSource,
) {
    internal val appContext = context.applicationContext
    val release = DictionaryRelease.load(appContext)
    private val root = File(appContext.filesDir, "dictionaries")

    fun installedDatabase(language: String, source: DictionarySource): File? {
        require(language in release.languages)
        return File(File(File(root, language), release.tag), source.fileName)
            .takeIf(::isUsableDatabase)
    }

    fun availableDatabase(language: String, source: DictionarySource): File? {
        installedDatabase(language, source)?.let { return it }
        val languageDir = File(root, language)
        return languageDir.listFiles()
            ?.asSequence()
            ?.filter { it.isDirectory && !it.name.startsWith(".") }
            ?.sortedByDescending { it.name }
            ?.map { File(it, source.fileName) }
            ?.filter(::isUsableDatabase)
            ?.firstOrNull()
    }

    fun availableLanguages(): List<String> = release.languages.keys.filter { language ->
        DictionarySource.entries.any { source -> availableDatabase(language, source) != null }
    }

    suspend fun install(
        language: String,
        dictionarySource: DictionarySource,
        onProgress: (DictionaryDownloadProgress) -> Unit = {},
    ): File = withContext(Dispatchers.IO) {
        require(language in release.languages)
        installedDatabase(language, dictionarySource)?.let { return@withContext it }
        val asset = release.languages.getValue(language).asset(dictionarySource)
        val languageDir = File(root, language)
        check(languageDir.mkdirs() || languageDir.isDirectory) { "Cannot create dictionary directory" }
        val versionDir = File(languageDir, release.tag)
        check(versionDir.mkdirs() || versionDir.isDirectory) { "Cannot create version directory" }
        val staging = File(versionDir, ".${dictionarySource.fileName}-${System.nanoTime()}.part")
        try {
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
                    staging.outputStream().buffered().use { output ->
                        val buffer = ByteArray(64 * 1024)
                        var lastReported = 0L
                        while (true) {
                            coroutineContext.ensureActive()
                            val count = archive.read(buffer)
                            if (count < 0) break
                            output.write(buffer, 0, count)
                            if (received - lastReported >= 256 * 1024) {
                                onProgress(DictionaryDownloadProgress(
                                    received.coerceAtMost(asset.sizeBytes), asset.sizeBytes,
                                ))
                                lastReported = received
                            }
                        }
                    }
                }
            }
            coroutineContext.ensureActive()
            check(isUsableDatabase(staging)) { "Downloaded dictionary is invalid" }
            coroutineContext.ensureActive()
            val installed = File(versionDir, dictionarySource.fileName)
            if (installed.isFile) {
                if (isUsableDatabase(installed)) return@withContext installed
                check(installed.delete()) { "Cannot replace invalid dictionary" }
            }
            check(staging.renameTo(installed)) { "Cannot activate downloaded dictionary" }
            onProgress(DictionaryDownloadProgress(asset.sizeBytes, asset.sizeBytes))
            installed
        } finally {
            staging.delete()
        }
    }

    fun openReadOnly(language: String, source: DictionarySource): SQLiteDatabase? =
        availableDatabase(language, source)?.let {
            SQLiteDatabase.openDatabase(it.path, null, SQLiteDatabase.OPEN_READONLY)
        }

    private fun isUsableDatabase(file: File): Boolean {
        if (!file.isFile) return false
        return try {
            SQLiteDatabase.openDatabase(file.path, null, SQLiteDatabase.OPEN_READONLY).use { database ->
                database.rawQuery(
                    "SELECT word, ipa, ipa_reversed, assonance_reversed FROM dictionary LIMIT 1",
                    null,
                ).use { cursor -> cursor.moveToFirst() }
            }
            true
        } catch (_: SQLiteException) {
            false
        }
    }
}
