package com.prosincerity.ghostwriter.data

import androidx.test.ext.junit.runners.AndroidJUnit4
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
import java.io.File
import java.io.IOException
import java.io.ByteArrayOutputStream
import java.util.zip.GZIPOutputStream

@RunWith(AndroidJUnit4::class)
class DictionaryInstallerInstrumentedTest {
    @Test
    fun installsSourcesSeparatelyAndUsesWiktionaryFirst() = withDictionaryTestContext { context ->
        val wiki = dictionaryArchive(context, listOf("hammer" to "/ˈhæmə/", "hammer" to "/ˈhæmɚ/",
            "d'accord" to "/dakɔʁ/"))
        val espeak = dictionaryArchive(context, listOf("hammer" to "/hamɚ/", "fallback" to "/fɔlbæk/"))
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
    fun failedArchiveDoesNotRemoveInstalledOtherSource() = withDictionaryTestContext { context ->
        val wiki = dictionaryArchive(context, listOf("word" to "/wɜːd/"))
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
    fun invalidDatabaseArchiveIsRejectedAndCanBeRetried() = withDictionaryTestContext { context ->
        val valid = dictionaryArchive(context, listOf("word" to "/wɜːd/"))
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
    fun corruptInstalledDatabaseCanBeReplaced() = withDictionaryTestContext { context ->
        val valid = dictionaryArchive(context, listOf("word" to "/wɜːd/"))
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
    fun cancellationAfterReadingArchiveDoesNotActivateDictionary() = withDictionaryTestContext { context ->
        val valid = dictionaryArchive(context, listOf("word" to "/wɜːd/"))
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
    fun installationWaitsForProgressDelivery() = withDictionaryTestContext { context ->
        val valid = dictionaryArchive(context, listOf("word" to "/wɜːd/"))
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
    fun generatedIpaIsUsedOnlyAfterBothDatabasesMiss() = withDictionaryTestContext { context ->
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
}
