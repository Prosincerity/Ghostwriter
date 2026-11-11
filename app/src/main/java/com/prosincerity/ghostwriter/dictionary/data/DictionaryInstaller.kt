package com.prosincerity.ghostwriter.dictionary.data

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import org.json.JSONObject
import java.io.File
import java.io.FilterInputStream
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import kotlin.coroutines.coroutineContext

internal data class DictionaryAsset(val file: String, val sizeBytes: Long, val url: String, val sha256: String)
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

/** Validated release metadata, loaded locally while editing. */
internal class DictionaryRelease private constructor(
    val tag: String,
    val languages: Map<String, DictionaryLanguage>,
) {
    companion object {
        fun load(context: Context): DictionaryRelease = parse(
            JSONObject(
                context.assets.open("dictionary_release.json").bufferedReader().use { it.readText() },
            ),
        )

        internal fun parse(json: JSONObject): DictionaryRelease {
            val tag = json.getString("tag")
            require(tag.matches(Regex("[A-Za-z0-9._-]+")))
            // Fallback discovery skips hidden directories; dot components also escape the version directory.
            require(!tag.startsWith(".")) { "Release tag must name a visible version directory" }
            val source = json.getJSONObject("languages")
            val languages = listOf("en", "de", "tr").associateWith { code ->
                val pair = source.getJSONObject(code)
                DictionaryLanguage(asset(pair.getJSONObject("wiktionary"), tag), asset(pair.getJSONObject("espeak"), tag))
            }
            return DictionaryRelease(tag, languages)
        }

        private fun asset(json: JSONObject, tag: String): DictionaryAsset {
            val file = json.getString("file")
            val url = json.getString("url")
            require(file.matches(Regex("[A-Za-z0-9._-]+\\.db\\.gz")))
            require(url == "https://github.com/Prosincerity/Ghostwriter-Dict/releases/download/$tag/$file")
            val sizeBytes = json.getLong("sizeBytes")
            require(sizeBytes > 0) { "Dictionary archive size must be positive" }
            val sha256 = json.getString("sha256").lowercase()
            require(sha256.matches(Regex("[a-f0-9]{64}"))) { "Invalid dictionary SHA-256" }
            return DictionaryAsset(file, sizeBytes, url, sha256)
        }
    }

    fun toJson(): JSONObject = JSONObject().put("tag", tag).put("languages", JSONObject().also { result ->
        languages.forEach { (language, pair) ->
            result.put(language, JSONObject().also { sources ->
                DictionarySource.entries.forEach { source ->
                    val asset = pair.asset(source)
                    sources.put(source.name.lowercase(), JSONObject()
                        .put("file", asset.file).put("sizeBytes", asset.sizeBytes)
                        .put("url", asset.url).put("sha256", asset.sha256))
                }
            })
        }
    })
}

internal fun interface DictionaryArchiveSource {
    fun open(url: String): InputStream
}

internal val releaseSource = DictionaryArchiveSource { address ->
    openDictionaryArchive(URL(address).openConnection() as HttpURLConnection)
}

/** Owns the connection until the returned archive stream is closed. */
internal fun openDictionaryArchive(connection: HttpURLConnection): InputStream {
    connection.connectTimeout = 15_000
    connection.readTimeout = 30_000
    connection.instanceFollowRedirects = true
    connection.setRequestProperty("User-Agent", "Ghostwriter")
    try {
        val responseCode = connection.responseCode
        if (responseCode != HttpURLConnection.HTTP_OK) {
            error("Dictionary download failed: HTTP $responseCode")
        }
        return object : FilterInputStream(connection.inputStream) {
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
    bundledRelease: DictionaryRelease = DictionaryRelease.load(context),
    metadataSource: DictionaryArchiveSource = releaseSource,
) {
    internal val appContext = context.applicationContext
    private val root = File(appContext.filesDir, "dictionaries")
    private val catalog = DictionaryReleaseCatalog(File(root, "release.json"), bundledRelease, metadataSource)
    private val mutations = Mutex()
    val release: DictionaryRelease get() = catalog.release

    suspend fun refreshRelease(): DictionaryRelease = catalog.refresh()

    fun installedDatabase(language: String, source: DictionarySource): File? {
        require(language in release.languages)
        return File(root, "$language/${release.tag}/${source.fileName}")
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
            ?.firstOrNull(::isUsableDatabase)
    }

    fun availableLanguages(): List<String> = release.languages.keys.filter { language ->
        DictionarySource.entries.any { source -> availableDatabase(language, source) != null }
    }

    fun hasStoredDictionary(language: String, source: DictionarySource): Boolean =
        storedFiles(language, source).isNotEmpty()

    /** Remove every version of this source so fallback discovery cannot restore an older copy. */
    suspend fun remove(language: String, source: DictionarySource) = withContext(Dispatchers.IO) {
        mutations.withLock {
            val files = storedFiles(language, source)
            for (file in files) {
                coroutineContext.ensureActive()
                check(file.delete()) { "Cannot remove dictionary ${file.name}" }
            }
            files.mapNotNull { it.parentFile }.distinct().forEach { directory ->
                if (directory.listFiles()?.isEmpty() == true) directory.delete()
            }
            File(root, language).let { if (it.listFiles()?.isEmpty() == true) it.delete() }
        }
    }

    private fun storedFiles(language: String, source: DictionarySource): List<File> {
        require(language in release.languages)
        val directory = File(root, language)
        if (!directory.exists()) return emptyList()
        val versions = checkNotNull(directory.listFiles()) { "Cannot read installed dictionaries" }
        val sidecars = setOf("${source.fileName}-journal", "${source.fileName}-wal", "${source.fileName}-shm")
        return versions.filter { it.isDirectory }.flatMap { version ->
            checkNotNull(version.listFiles()) { "Cannot read dictionary version ${version.name}" }.filter { file ->
                file.isFile && (file.name == source.fileName || file.name in sidecars ||
                    (file.name.startsWith(".${source.fileName}-") && file.name.endsWith(".part")))
            }
        }
    }

    suspend fun install(
        language: String,
        dictionarySource: DictionarySource,
        onProgress: suspend (DictionaryDownloadProgress) -> Unit = {},
    ): File = withContext(Dispatchers.IO) {
        mutations.withLock {
            val release = release
            require(language in release.languages)
            installedDatabase(language, dictionarySource)?.let { return@withLock it }
            val asset = release.languages.getValue(language).asset(dictionarySource)
            val languageDir = File(root, language)
            check(languageDir.mkdirs() || languageDir.isDirectory) { "Cannot create dictionary directory" }
            val versionDir = File(languageDir, release.tag)
            check(versionDir.mkdirs() || versionDir.isDirectory) { "Cannot create version directory" }
            val staging = File(versionDir, ".${dictionarySource.fileName}-${System.nanoTime()}.part")
            try {
                unpackDictionaryArchive(source, asset, staging, onProgress)
                coroutineContext.ensureActive()
                check(isUsableDatabase(staging)) { "Downloaded dictionary is invalid" }
                coroutineContext.ensureActive()
                val installed = File(versionDir, dictionarySource.fileName)
                if (installed.isFile) {
                    if (isUsableDatabase(installed)) return@withLock installed
                    check(installed.delete()) { "Cannot replace invalid dictionary" }
                }
                check(staging.renameTo(installed)) { "Cannot activate downloaded dictionary" }
                onProgress(DictionaryDownloadProgress(asset.sizeBytes, asset.sizeBytes))
                installed
            } finally {
                staging.delete()
            }
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
