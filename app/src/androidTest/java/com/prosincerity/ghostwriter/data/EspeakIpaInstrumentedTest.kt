package com.prosincerity.ghostwriter.data

import android.content.Context
import android.content.ContextWrapper
import android.content.res.AssetManager
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertSame
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.io.IOException

@RunWith(AndroidJUnit4::class)
class EspeakIpaInstrumentedTest {
    @Test
    fun blockedLanguageDirectory_abortsInstallationAndCleansStaging() = withDictionaryTestContext { context ->
        var openedAssets = 0
        val blockedContext = object : ContextWrapper(context) {
            override fun getApplicationContext(): Context = this
            override fun getAssets(): AssetManager {
                if (++openedAssets == 7) {
                    val staging = File(filesDir, "espeak-ng").listFiles()!!.single { it.name.endsWith(".part") }
                    File(staging, "espeak-ng-data/lang").writeText("blocked directory")
                }
                return context.assets
            }
        }
        assertThrows(IllegalStateException::class.java) { EspeakIpa(blockedContext).installData() }
        assertEquals(emptyList<File>(), File(context.filesDir, "espeak-ng").listFiles()!!.toList())
    }
    @Test
    fun readOnlyIncompleteVersionIsPreservedWhenReplacementFails() = withDictionaryTestContext { context ->
        val version = File(context.filesDir, "espeak-ng/1.52.0").apply { mkdirs() }
        val previous = File(version, "keep").apply { writeText("existing data") }
        check(version.setWritable(false))
        try {
            val failure = assertThrows(IllegalStateException::class.java) { EspeakIpa(context).installData() }
            assertEquals("Cannot replace incomplete eSpeak NG data", failure.message)
            assertEquals("existing data", previous.readText())
            assertEquals(listOf(version), version.parentFile!!.listFiles()!!.toList())
        } finally {
            check(version.setWritable(true))
        }
        assertEquals(version, EspeakIpa(context).installData())
    }

    @Test fun punctuationWithoutPhonemes_returnsNoPronunciation() = runBlocking {
        assertNull(generator.ipa(".", "en"))
    }
    @Test
    fun incompleteStagingIsRejectedAndCleanedBeforeActivation() = withDictionaryTestContext { context ->
        var openedAssets = 0
        val corruptingContext = object : ContextWrapper(context) {
            override fun getApplicationContext(): Context = this
            override fun getAssets(): AssetManager {
                if (++openedAssets == 10) {
                    val staging = File(filesDir, "espeak-ng").listFiles()!!.single { it.name.endsWith(".part") }
                    File(staging, "espeak-ng-data/phondata").writeBytes(byteArrayOf())
                }
                return context.assets
            }
        }
        val failure = assertThrows(IllegalStateException::class.java) { EspeakIpa(corruptingContext).installData() }
        assertEquals("Incomplete eSpeak NG data", failure.message)
        assertEquals(emptyList<File>(), File(context.filesDir, "espeak-ng").listFiles()!!.toList())
    }

    @Test
    fun activationFailureDoesNotLeaveAPartiallyInstalledVersion() = withDictionaryTestContext { context ->
        val root = File(context.filesDir, "espeak-ng")
        var openedAssets = 0
        val blockedContext = object : ContextWrapper(context) {
            override fun getApplicationContext(): Context = this
            override fun getAssets(): AssetManager {
                if (++openedAssets == 10) check(root.setWritable(false))
                return context.assets
            }
        }
        try {
            val failure = assertThrows(IllegalStateException::class.java) { EspeakIpa(blockedContext).installData() }
            assertEquals("Cannot activate eSpeak NG data", failure.message)
            assertTrue(!File(root, "1.52.0").exists())
        } finally {
            check(root.setWritable(true))
        }
        // Restoring access permits a subsequent installation to recover.
        assertTrue(EspeakIpa(context).installData().isDirectory)
    }
    private val generator: EspeakIpa
        get() = EspeakIpa(InstrumentationRegistry.getInstrumentation().targetContext)

    @Test
    fun matchesPinnedCliForThreeLanguages() = runBlocking {
        assertEquals("ɡˈəʊstɹaɪtə", generator.ipa("ghostwriter", "en"))
        assertEquals("ˌyːbɜmˈuːt", generator.ipa("Übermut", "de"))
        assertEquals("ɯʃˈɯk", generator.ipa("ışık", "tr"))
    }

    @Test
    fun rejectsUnsupportedAndEmptyInput() = runBlocking {
        assertNull(generator.ipa("word", "fr"))
        assertNull(generator.ipa(" ", "en"))
        assertNull(generator.ipa("two words", "en"))
    }

    @Test
    fun rejectsOversizedNullTerminatedAndWhitespaceSeparatedWords() = runBlocking {
        listOf("a".repeat(129), "word\u0000suffix", "two\twords", "two\nwords", "two\u00a0words")
            .forEach { word -> assertNull(word, generator.ipa(word, "en")) }
    }

    @Test
    fun canonicallyEquivalentSpellingsHaveTheSamePronunciation() = runBlocking {
        assertEquals(generator.ipa("Übermut", "de"), generator.ipa("U\u0308bermut", "de"))
    }

    @Test
    fun completeDataIsReusedWithoutReplacingItsDirectory() = withDictionaryTestContext { context ->
        val generator = EspeakIpa(context)
        val installed = generator.installData()
        val marker = File(installed, "reuse-marker").apply { writeText("keep") }

        assertEquals(installed, generator.installData())
        assertEquals("keep", marker.readText())
        assertTrue(installed.parentFile!!.listFiles().orEmpty().none { it.name.endsWith(".part") })
    }

    @Test
    fun incompleteDataIsReplacedWithAllPackagedLanguages() = withDictionaryTestContext { context ->
        val version = File(context.filesDir, "espeak-ng/1.52.0").apply { mkdirs() }
        val previous = File(version, "incomplete-marker").apply { writeText("old") }
        File(version, "espeak-ng-data").mkdirs()
        File(version, "espeak-ng-data/phondata").writeBytes(byteArrayOf())

        assertEquals(version, EspeakIpa(context).installData())

        assertTrue(!previous.exists())
        listOf("phondata", "phonindex", "phontab", "intonations", "en_dict", "de_dict", "tr_dict",
            "lang/gmw/en", "lang/gmw/de", "lang/trk/tr").forEach { relativePath ->
            val file = File(version, "espeak-ng-data/$relativePath")
            assertTrue("Missing packaged data: $relativePath", file.isFile && file.length() > 0)
        }
        assertTrue(version.parentFile!!.listFiles().orEmpty().none { it.name.endsWith(".part") })
    }

    @Test
    fun failedAssetCopyCleansStagingAndPreservesExistingData() = withDictionaryTestContext { context ->
        val version = File(context.filesDir, "espeak-ng/1.52.0").apply { mkdirs() }
        val previous = File(version, "incomplete-marker").apply { writeText("old") }
        val error = IOException("cannot read assets")
        val failingContext = object : ContextWrapper(context) {
            override fun getApplicationContext(): Context = this
            override fun getAssets(): AssetManager = throw error
        }

        assertSame(error, assertThrows(IOException::class.java) { EspeakIpa(failingContext).installData() })
        assertEquals("old", previous.readText())
        assertEquals(listOf(version), version.parentFile!!.listFiles()!!.toList())
    }

    @Test
    fun regularFileAtDataRootIsRejectedWithoutDeletingIt() = withDictionaryTestContext { context ->
        val root = File(context.filesDir, "espeak-ng").apply { writeText("keep") }

        val failure = assertThrows(IllegalStateException::class.java) { EspeakIpa(context).installData() }

        assertEquals("Cannot create eSpeak NG data directory", failure.message)
        assertEquals("keep", root.readText())
    }
}
