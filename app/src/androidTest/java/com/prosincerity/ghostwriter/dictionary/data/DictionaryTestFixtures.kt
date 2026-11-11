package com.prosincerity.ghostwriter.dictionary.data

import android.content.Context
import android.content.ContextWrapper
import android.database.sqlite.SQLiteDatabase
import androidx.test.platform.app.InstrumentationRegistry
import java.io.ByteArrayOutputStream
import java.io.File
import org.junit.rules.TemporaryFolder
import java.util.zip.GZIPOutputStream
import java.io.ByteArrayInputStream
import java.security.MessageDigest
import org.json.JSONArray
import org.json.JSONObject

internal fun <T> withDictionaryTestContext(block: (Context) -> T): T {
    val baseContext = InstrumentationRegistry.getInstrumentation().targetContext
    val folder = TemporaryFolder(baseContext.cacheDir).apply { create() }
    val root = folder.root
    val files = File(root, "files").apply { mkdirs() }
    val cache = File(root, "cache").apply { mkdirs() }
    val context = object : ContextWrapper(baseContext) {
        override fun getApplicationContext(): Context = this
        override fun getFilesDir(): File = files
        override fun getCacheDir(): File = cache
    }
    return try {
        block(context)
    } finally {
        folder.delete()
    }
}

internal fun dictionaryArchive(context: Context, entries: List<Pair<String, String>>): ByteArray {
    val file = File.createTempFile("dictionary-fixture-", ".db", context.cacheDir)
    try {
        SQLiteDatabase.openOrCreateDatabase(file, null).use { database ->
            database.beginTransaction()
            try {
                database.execSQL(
                    "CREATE TABLE dictionary (word TEXT NOT NULL, ipa TEXT NOT NULL, " +
                        "ipa_reversed TEXT NOT NULL, assonance_reversed TEXT NOT NULL, " +
                        "PRIMARY KEY (word, ipa)) WITHOUT ROWID",
                )
                entries.forEach { (word, ipa) ->
                    database.execSQL("INSERT INTO dictionary VALUES (?, ?, '', '')", arrayOf(word, ipa))
                }
                database.setTransactionSuccessful()
            } finally {
                database.endTransaction()
            }
        }
        return ByteArrayOutputStream().use { output ->
            GZIPOutputStream(output).use { gzip -> file.inputStream().use { it.copyTo(gzip) } }
            output.toByteArray()
        }
    } finally {
        file.delete()
    }
}

internal fun dictionaryFixtureRelease(
    context: Context,
    archive: ByteArray,
    espeak: ByteArray = archive,
    tag: String = DictionaryRelease.load(context).tag,
): DictionaryRelease {
    val json = DictionaryRelease.load(context).toJson().put("tag", tag)
    for (language in listOf("en", "de", "tr")) {
        for (source in DictionarySource.entries) {
            val bytes = if (source == DictionarySource.ESPEAK) espeak else archive
            val name = "${language}_${if (source == DictionarySource.ESPEAK) "espeak_" else ""}$tag.db.gz"
            json.getJSONObject("languages").getJSONObject(language).getJSONObject(source.name.lowercase())
                .put("file", name).put("sizeBytes", bytes.size)
                .put("url", "https://github.com/Prosincerity/Ghostwriter-Dict/releases/download/$tag/$name")
                .put("sha256", MessageDigest.getInstance("SHA-256").digest(bytes).joinToString("") { "%02x".format(it) })
        }
    }
    return DictionaryRelease.parse(json)
}

internal fun dictionaryFixtureMetadata(release: DictionaryRelease): String {
    val assets = JSONArray()
    release.languages.values.forEach { pair ->
        DictionarySource.entries.forEach { source ->
            val asset = pair.asset(source)
            assets.put(JSONObject().put("name", asset.file).put("size", asset.sizeBytes)
                .put("browser_download_url", asset.url).put("digest", "sha256:${asset.sha256}"))
        }
    }
    assets.put(JSONObject().put("name", "SHA256SUMS")
        .put("browser_download_url", "https://github.com/Prosincerity/Ghostwriter-Dict/releases/download/${release.tag}/SHA256SUMS"))
    return JSONObject().put("tag_name", release.tag).put("draft", false).put("prerelease", false)
        .put("assets", assets).toString()
}

internal fun dictionaryFixtureChecksums(release: DictionaryRelease): String =
    release.languages.flatMap { (language, pair) ->
        DictionarySource.entries.map { pair.asset(it).let { asset -> "${asset.sha256}  $language/${asset.file}" } }
    }.joinToString("\n")

internal fun dictionaryTestInstaller(
    context: Context,
    archive: ByteArray,
    espeak: ByteArray = archive,
    source: DictionaryArchiveSource,
): DictionaryInstaller {
    val release = dictionaryFixtureRelease(context, archive, espeak)
    return DictionaryInstaller(context, source, release, DictionaryArchiveSource { url ->
        ByteArrayInputStream((if (url == DictionaryReleaseCatalog.LATEST_URL) dictionaryFixtureMetadata(release)
            else dictionaryFixtureChecksums(release)).toByteArray())
    })
}
