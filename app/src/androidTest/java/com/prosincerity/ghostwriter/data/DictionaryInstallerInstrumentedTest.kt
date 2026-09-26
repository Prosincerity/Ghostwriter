package com.prosincerity.ghostwriter.data

import android.content.Context
import android.content.ContextWrapper
import android.database.sqlite.SQLiteDatabase
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.async
import kotlinx.coroutines.cancel
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
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
        val wiki = archive(context, listOf("hammer" to "/ˈhæmə/", "hammer" to "/ˈhæmɚ/",
            "d'accord" to "/dakɔʁ/"))
        val espeak = archive(context, listOf("hammer" to "/hamɚ/", "fallback" to "/fɔlbæk/"))
        var opens = 0
        val installer = DictionaryInstaller(context, DictionaryArchiveSource { url ->
            opens++
            ByteArrayInputStream(if ("_espeak_" in url) espeak else wiki)
        })

        assertTrue(installer.availableLanguages().isEmpty())
        runBlocking { installer.install("en", DictionarySource.ESPEAK) }
        assertEquals(1, opens)
        assertEquals(listOf("en"), installer.availableLanguages())
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
        assertEquals(PronunciationSource.WIKTIONARY, runBlocking { withFallback.lookup("d’accord", "en") }?.source)
        assertEquals(0, generatedCalls)
        assertNull(runBlocking { withFallback.lookup("two words", "en") })
        assertNull(runBlocking { withFallback.lookup("x", "en") })
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
        assertEquals(listOf("tr"), installer.availableLanguages())
        val failure = runCatching {
            runBlocking { installer.install("tr", DictionarySource.ESPEAK) }
        }.exceptionOrNull()
        assertTrue(failure is IOException)
        assertNull(installer.installedDatabase("tr", DictionarySource.ESPEAK))
        assertTrue(installer.installedDatabase("tr", DictionarySource.WIKTIONARY) != null)
        assertEquals(PronunciationSource.WIKTIONARY, lookup(installer, "word", "tr")?.source)
    }

    @Test
    fun invalidDatabaseArchiveIsRejectedAndCanBeRetried() = withTestContext { context ->
        val valid = archive(context, listOf("word" to "/wɜːd/"))
        val invalid = ByteArrayOutputStream().use { output ->
            GZIPOutputStream(output).use { it.write("not a SQLite database".toByteArray()) }
            output.toByteArray()
        }
        var opens = 0
        val installer = DictionaryInstaller(context, DictionaryArchiveSource {
            ByteArrayInputStream(if (++opens == 1) invalid else valid)
        })

        val failure = runCatching {
            runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }
        }.exceptionOrNull()
        assertTrue(failure != null)
        assertNull(installer.installedDatabase("en", DictionarySource.WIKTIONARY))
        assertTrue(installer.availableLanguages().isEmpty())

        runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }
        assertEquals(2, opens)
        assertEquals(PronunciationSource.WIKTIONARY, lookup(installer, "word", "en")?.source)
    }

    @Test
    fun corruptInstalledDatabaseCanBeReplaced() = withTestContext { context ->
        val valid = archive(context, listOf("word" to "/wɜːd/"))
        var opens = 0
        val installer = DictionaryInstaller(context, DictionaryArchiveSource {
            opens++
            ByteArrayInputStream(valid)
        })
        val version = File(context.filesDir, "dictionaries/en/${installer.release.tag}")
        version.mkdirs()
        File(version, "wiktionary.db").writeText("not a SQLite database")

        assertNull(installer.installedDatabase("en", DictionarySource.WIKTIONARY))
        assertTrue(installer.availableLanguages().isEmpty())
        runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }
        assertEquals(1, opens)
        assertEquals(PronunciationSource.WIKTIONARY, lookup(installer, "word", "en")?.source)
    }

    @Test
    fun cancellationAfterReadingArchiveDoesNotActivateDictionary() = withTestContext { context ->
        val valid = archive(context, listOf("word" to "/wɜːd/"))
        var cancelDownload: () -> Unit = {}
        val installer = DictionaryInstaller(context, DictionaryArchiveSource {
            object : ByteArrayInputStream(valid) {
                override fun close() {
                    super.close()
                    cancelDownload()
                }
            }
        })

        val failure = runCatching {
            runBlocking {
                val downloadContext = coroutineContext
                cancelDownload = { downloadContext.cancel() }
                installer.install("en", DictionarySource.WIKTIONARY)
            }
        }.exceptionOrNull()

        assertTrue(failure is CancellationException)
        assertNull(installer.installedDatabase("en", DictionarySource.WIKTIONARY))
        val version = File(context.filesDir, "dictionaries/en/${installer.release.tag}")
        assertTrue(version.listFiles().orEmpty().isEmpty())

        cancelDownload = {}
        runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }
        assertEquals(PronunciationSource.WIKTIONARY, lookup(installer, "word", "en")?.source)
    }

    @Test
    fun installationWaitsForProgressDelivery() = withTestContext { context ->
        val valid = archive(context, listOf("word" to "/wɜːd/"))
        val installer = DictionaryInstaller(context, DictionaryArchiveSource { ByteArrayInputStream(valid) })

        runBlocking {
            withTimeout(5_000) {
                val progressStarted = CompletableDeferred<DictionaryDownloadProgress>()
                val finishDelivery = CompletableDeferred<Unit>()
                var delivered = false
                val download = async {
                    installer.install("en", DictionarySource.WIKTIONARY) { progress ->
                        progressStarted.complete(progress)
                        finishDelivery.await()
                        delivered = true
                    }
                }

                val progress = progressStarted.await()
                assertEquals(progress.totalBytes, progress.downloadedBytes)
                assertFalse(download.isCompleted)
                finishDelivery.complete(Unit)
                assertTrue(download.await().isFile)
                assertTrue(delivered)
            }
        }
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
