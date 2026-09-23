package com.prosincerity.ghostwriter.data

import android.content.Context
import android.content.ContextWrapper
import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.IOException
import java.util.zip.GZIPOutputStream

@RunWith(AndroidJUnit4::class)
class DictionaryInstallerInstrumentedTest {
    private val baseContext: Context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun installsSourcesSeparatelyAndUsesWiktionaryFirst() = withTestContext { context ->
        val wiki = archive(context, listOf("hammer" to "/ˈhæmə/", "hammer" to "/ˈhæmɚ/"))
        val espeak = archive(context, listOf("hammer" to "/hamɚ/", "fallback" to "/fɔlbæk/"))
        var opens = 0
        val installer = DictionaryInstaller(context, DictionaryArchiveSource { url ->
            opens++
            ByteArrayInputStream(if ("_espeak_" in url) espeak else wiki)
        })

        runBlocking { installer.install("en", DictionarySource.ESPEAK) }
        assertEquals(1, opens)
        assertNull(installer.installedDatabase("en", DictionarySource.WIKTIONARY))
        assertTrue(installer.installedDatabase("en", DictionarySource.ESPEAK) != null)
        assertEquals(
            PronunciationResult(listOf("/hamɚ/"), PronunciationSource.ESPEAK_DATABASE),
            lookup(installer, "hammer", "en"),
        )
        runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }
        assertEquals(2, opens)
        assertEquals(
            PronunciationResult(listOf("/ˈhæmə/", "/ˈhæmɚ/"), PronunciationSource.WIKTIONARY),
            lookup(installer, "hammer", "en"),
        )
        assertEquals(
            PronunciationResult(listOf("/fɔlbæk/"), PronunciationSource.ESPEAK_DATABASE),
            lookup(installer, "fallback", "en"),
        )
        var generatedCalls = 0
        val withFallback = DictionaryPronunciations(installer, IpaGenerator { _, _ ->
            generatedCalls++
            "ˈfallback"
        })
        assertEquals(PronunciationSource.WIKTIONARY, runBlocking { withFallback.lookup("hammer", "en") }?.source)
        assertEquals(PronunciationSource.ESPEAK_DATABASE, runBlocking { withFallback.lookup("fallback", "en") }?.source)
        assertEquals(0, generatedCalls)
        assertEquals(PronunciationSource.ESPEAK_GENERATED, runBlocking { withFallback.lookup("absent", "en") }?.source)
        assertEquals(1, generatedCalls)
        assertNull(lookup(installer, "absent", "en"))
        runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }
        assertEquals(2, opens)
    }

    @Test
    fun failedArchiveDoesNotRemoveInstalledOtherSource() = withTestContext { context ->
        val wiki = archive(context, listOf("word" to "/wɜːd/"))
        val installer = DictionaryInstaller(context, DictionaryArchiveSource { url ->
            if ("_espeak_" in url) throw IOException("interrupted download")
            ByteArrayInputStream(wiki)
        })

        runBlocking { installer.install("tr", DictionarySource.WIKTIONARY) }
        val failure = runCatching {
            runBlocking { installer.install("tr", DictionarySource.ESPEAK) }
        }.exceptionOrNull()
        assertTrue(failure is IOException)
        assertNull(installer.installedDatabase("tr", DictionarySource.ESPEAK))
        assertTrue(installer.installedDatabase("tr", DictionarySource.WIKTIONARY) != null)
        assertEquals(PronunciationSource.WIKTIONARY, lookup(installer, "word", "tr")?.source)
    }

    @Test
    fun generatedIpaIsUsedOnlyAfterBothDatabasesMiss() = withTestContext { context ->
        val installer = DictionaryInstaller(context)
        val generated = DictionaryPronunciations(installer, IpaGenerator { _, _ -> "ˈɪpa" })
        assertEquals(
            PronunciationResult(listOf("ˈɪpa"), PronunciationSource.ESPEAK_GENERATED),
            runBlocking { generated.lookup("invented", "en") },
        )
    }

    private fun lookup(installer: DictionaryInstaller, word: String, language: String): PronunciationResult? =
        runBlocking {
            DictionaryPronunciations(installer, IpaGenerator { _, _ -> null }).lookup(word, language)
        }

    private fun archive(context: Context, entries: List<Pair<String, String>>): ByteArray {
        val file = File(context.cacheDir, "dictionary-fixture-${System.nanoTime()}.db")
        try {
            SQLiteDatabase.openOrCreateDatabase(file, null).use { database ->
                database.execSQL(
                    "CREATE TABLE dictionary (word TEXT NOT NULL, ipa TEXT NOT NULL, " +
                        "ipa_reversed TEXT NOT NULL, assonance_reversed TEXT NOT NULL, " +
                        "PRIMARY KEY (word, ipa)) WITHOUT ROWID",
                )
                entries.forEach { (word, ipa) ->
                    database.execSQL(
                        "INSERT INTO dictionary VALUES (?, ?, '', '')",
                        arrayOf(word, ipa),
                    )
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

    private inline fun withTestContext(block: (Context) -> Unit) {
        val root = File(baseContext.cacheDir, "dictionary-install-${System.nanoTime()}")
        val files = File(root, "files")
        val cache = File(root, "cache")
        files.mkdirs()
        cache.mkdirs()
        val context = object : ContextWrapper(baseContext) {
            override fun getApplicationContext(): Context = this
            override fun getFilesDir(): File = files
            override fun getCacheDir(): File = cache
        }
        try {
            block(context)
        } finally {
            root.deleteRecursively()
        }
    }
}
