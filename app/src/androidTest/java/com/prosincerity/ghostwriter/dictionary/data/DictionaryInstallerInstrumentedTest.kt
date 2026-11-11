package com.prosincerity.ghostwriter.dictionary.data

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
    fun removalDeletesAllVersionsOfOneSourceAndKeepsOtherDictionaries() = withDictionaryTestContext { context ->
        val bytes = dictionaryArchive(context, listOf("word" to "/wɜːd/"))
        var downloadsAllowed = true
        val installer = dictionaryTestInstaller(context, bytes, source = DictionaryArchiveSource {
            check(downloadsAllowed) { "Removal must work offline" }
            ByteArrayInputStream(bytes)
        })
        val wiki = runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }
        val espeak = runBlocking { installer.install("en", DictionarySource.ESPEAK) }
        val turkish = runBlocking { installer.install("tr", DictionarySource.WIKTIONARY) }
        val older = File(context.filesDir, "dictionaries/en/previous").apply { mkdirs() }
        val oldWiki = wiki.copyTo(File(older, "wiktionary.db"))
        val oldEspeak = espeak.copyTo(File(older, "espeak.db"))
        val staged = File(older, ".wiktionary.db-abandoned.part").apply { writeText("partial") }
        val journal = File(older, "wiktionary.db-journal").apply { writeText("journal") }
        downloadsAllowed = false

        runBlocking { installer.remove("en", DictionarySource.WIKTIONARY) }

        assertFalse(wiki.exists())
        assertFalse(oldWiki.exists())
        assertFalse(staged.exists())
        assertFalse(journal.exists())
        assertFalse(installer.hasStoredDictionary("en", DictionarySource.WIKTIONARY))
        assertNull(installer.availableDatabase("en", DictionarySource.WIKTIONARY))
        assertTrue(espeak.exists())
        assertTrue(oldEspeak.exists())
        assertTrue(turkish.exists())
        assertEquals(PronunciationSource.ESPEAK_DATABASE, lookup(installer, "word", "en")?.source)
        runBlocking { installer.remove("en", DictionarySource.WIKTIONARY) }
        downloadsAllowed = true
        assertTrue(runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }.isFile)
    }

    @Test
    fun unusableAndPartialDictionariesCanBeRemovedWithoutAnInstalledDatabase() = withDictionaryTestContext { context ->
        val installer = DictionaryInstaller(context)
        val oldVersion = File(context.filesDir, "dictionaries/de/previous").apply { mkdirs() }
        File(oldVersion, "wiktionary.db").writeText("damaged")
        File(oldVersion, ".wiktionary.db-abandoned.part").writeText("partial")
        assertNull(installer.availableDatabase("de", DictionarySource.WIKTIONARY))
        assertTrue(installer.hasStoredDictionary("de", DictionarySource.WIKTIONARY))

        runBlocking { installer.remove("de", DictionarySource.WIKTIONARY) }

        assertFalse(installer.hasStoredDictionary("de", DictionarySource.WIKTIONARY))
        assertFalse(oldVersion.exists())
    }

    @Test
    fun newReleaseChecksHashesAndKeepsOlderDictionaryUntilVerified() = withDictionaryTestContext { context ->
        val old = dictionaryArchive(context, listOf("older" to "/oʊld/"))
        val latest = dictionaryArchive(context, listOf("newer" to "/njuː/"))
        val oldRelease = dictionaryFixtureRelease(context, old, tag = "previous")
        val latestRelease = dictionaryFixtureRelease(context, latest, tag = "upcoming")
        var bytes = old
        val installer = DictionaryInstaller(context, DictionaryArchiveSource { ByteArrayInputStream(bytes) },
            oldRelease, DictionaryArchiveSource { url ->
                ByteArrayInputStream((if (url == DictionaryReleaseCatalog.LATEST_URL) dictionaryFixtureMetadata(latestRelease)
                    else dictionaryFixtureChecksums(latestRelease)).toByteArray())
            })
        val previous = runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }

        runBlocking { installer.refreshRelease() }
        assertEquals("upcoming", installer.release.tag)
        val failure = runCatching { runBlocking { installer.install("en", DictionarySource.WIKTIONARY) } }.exceptionOrNull()
        assertTrue(failure?.message.orEmpty().contains("SHA-256"))
        assertEquals(previous, installer.availableDatabase("en", DictionarySource.WIKTIONARY))
        assertTrue(File(context.filesDir, "dictionaries/en/upcoming").listFiles().orEmpty().isEmpty())

        bytes = latest
        runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }
        assertEquals(PronunciationSource.WIKTIONARY, lookup(installer, "newer", "en")?.source)
        val offline = DictionaryInstaller(context, DictionaryArchiveSource { error("Must stay offline") })
        assertEquals("upcoming", offline.release.tag)
        assertEquals(PronunciationSource.WIKTIONARY, lookup(offline, "newer", "en")?.source)
    }

    @Test
    fun matchingHashStillRequiresAValidDictionarySchema() = withDictionaryTestContext { context ->
        val bytes = ByteArrayOutputStream().use { output ->
            GZIPOutputStream(output).use { it.write("not a SQLite database".toByteArray()) }
            output.toByteArray()
        }
        val installer = dictionaryTestInstaller(context, bytes, source = DictionaryArchiveSource { ByteArrayInputStream(bytes) })

        val failure = runCatching { runBlocking { installer.install("en", DictionarySource.WIKTIONARY) } }.exceptionOrNull()

        assertEquals("Downloaded dictionary is invalid", failure?.message)
        assertTrue(installer.availableLanguages().isEmpty())
    }

    @Test
    fun installsSourcesSeparatelyAndUsesWiktionaryFirst() = withDictionaryTestContext { context ->
        val wiki = dictionaryArchive(context, listOf("hammer" to "/ˈhæmə/", "hammer" to "/ˈhæmɚ/",
            "d'accord" to "/dakɔʁ/"))
        val espeak = dictionaryArchive(context, listOf("hammer" to "/hamɚ/", "fallback" to "/fɔlbæk/"))
        var downloadsAllowed = true
        val installer = dictionaryTestInstaller(context, wiki, espeak, source = DictionaryArchiveSource { url ->
            check(downloadsAllowed) { "An installed source must remain available without downloading again" }
            ByteArrayInputStream(if ("_espeak_" in url) espeak else wiki)
        })
        var generatedCalls = 0
        val withFallback = DictionaryPronunciations(installer, IpaGenerator { _, _ ->
            generatedCalls++
            "ˈfallback"
        })

        assertTrue(installer.availableLanguages().isEmpty())
        val generated = PronunciationResult(listOf("ˈfallback"), PronunciationSource.ESPEAK_GENERATED)
        assertEquals(generated, runBlocking { withFallback.lookup("absent", "en") })
        assertEquals(1, generatedCalls)
        runBlocking { installer.install("en", DictionarySource.ESPEAK) }
        assertEquals(listOf("en"), installer.availableLanguages())
        assertNull(installer.installedDatabase("en", DictionarySource.WIKTIONARY))
        assertTrue(installer.installedDatabase("en", DictionarySource.ESPEAK) != null)
        assertEquals(
            PronunciationResult(listOf("/hamɚ/"), PronunciationSource.ESPEAK_DATABASE),
            lookup(installer, "hammer", "en"),
        )
        runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }
        assertEquals(
            PronunciationResult(listOf("/ˈhæmə/", "/ˈhæmɚ/"), PronunciationSource.WIKTIONARY),
            lookup(installer, "hammer", "en"),
        )
        assertEquals(
            PronunciationResult(listOf("/fɔlbæk/"), PronunciationSource.ESPEAK_DATABASE),
            lookup(installer, "fallback", "en"),
        )
        assertEquals(PronunciationSource.WIKTIONARY, runBlocking { withFallback.lookup("hammer", "en") }?.source)
        assertEquals(PronunciationSource.ESPEAK_DATABASE, runBlocking { withFallback.lookup("fallback", "en") }?.source)
        assertEquals(PronunciationSource.WIKTIONARY, runBlocking { withFallback.lookup("d’accord", "en") }?.source)
        assertEquals(1, generatedCalls)
        assertNull(runBlocking { withFallback.lookup("two words", "en") })
        assertNull(runBlocking { withFallback.lookup("x", "en") })
        assertEquals(1, generatedCalls)
        assertEquals(generated, runBlocking { withFallback.lookup("absent", "en") })
        assertEquals(2, generatedCalls)
        assertNull(lookup(installer, "absent", "en"))
        downloadsAllowed = false
        runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }
        assertEquals(PronunciationSource.WIKTIONARY, lookup(installer, "hammer", "en")?.source)
    }

    @Test
    fun failedArchiveDoesNotRemoveInstalledOtherSource() = withDictionaryTestContext { context ->
        val wiki = dictionaryArchive(context, listOf("word" to "/wɜːd/"))
        val installer = dictionaryTestInstaller(context, wiki, source = DictionaryArchiveSource { url ->
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
        var archive = invalid
        val installer = dictionaryTestInstaller(context, valid, source = DictionaryArchiveSource {
            ByteArrayInputStream(archive)
        })

        val failure = runCatching {
            runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }
        }.exceptionOrNull()
        assertTrue(failure != null)
        assertNull(installer.installedDatabase("en", DictionarySource.WIKTIONARY))
        assertTrue(installer.availableLanguages().isEmpty())

        archive = valid
        runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }
        assertEquals(PronunciationSource.WIKTIONARY, lookup(installer, "word", "en")?.source)
    }

    @Test
    fun corruptInstalledDatabaseCanBeReplaced() = withDictionaryTestContext { context ->
        val valid = dictionaryArchive(context, listOf("word" to "/wɜːd/"))
        val installer = dictionaryTestInstaller(context, valid, source = DictionaryArchiveSource {
            ByteArrayInputStream(valid)
        })
        val version = File(context.filesDir, "dictionaries/en/${installer.release.tag}")
        version.mkdirs()
        File(version, "wiktionary.db").writeText("not a SQLite database")

        assertNull(installer.installedDatabase("en", DictionarySource.WIKTIONARY))
        assertTrue(installer.availableLanguages().isEmpty())
        runBlocking { installer.install("en", DictionarySource.WIKTIONARY) }
        assertEquals(PronunciationSource.WIKTIONARY, lookup(installer, "word", "en")?.source)
    }

    @Test
    fun cancellationAfterReadingArchiveDoesNotActivateDictionary() = withDictionaryTestContext { context ->
        val valid = dictionaryArchive(context, listOf("word" to "/wɜːd/"))
        var cancelDownload: () -> Unit = {}
        val installer = dictionaryTestInstaller(context, valid, source = DictionaryArchiveSource {
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
        val installer = dictionaryTestInstaller(context, valid, source = DictionaryArchiveSource { ByteArrayInputStream(valid) })

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

    private fun lookup(installer: DictionaryInstaller, word: String, language: String): PronunciationResult? =
        runBlocking {
            DictionaryPronunciations(installer, IpaGenerator { _, _ -> null }).lookup(word, language)
        }
}
