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
        val blockedContext = withStagingFault(context) { staging ->
            val languages = File(staging, "espeak-ng-data/lang")
            if (languages.isDirectory) check(languages.deleteRecursively())
            languages.writeText("blocked directory")
        }

        assertThrows(Exception::class.java) { EspeakIpa(blockedContext).installData() }

        assertTrue(File(context.filesDir, "espeak-ng").listFiles()!!.isEmpty())
    }

    @Test
    fun readOnlyIncompleteVersionIsPreservedWhenReplacementFails() = withDictionaryTestContext { context ->
        val version = File(context.filesDir, "espeak-ng/1.52.0").apply { mkdirs() }
        val previous = File(version, "keep").apply { writeText("existing data") }
        check(version.setWritable(false))
        try {
            assertThrows(IllegalStateException::class.java) { EspeakIpa(context).installData() }
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
        val corruptingContext = withStagingFault(context) { staging ->
            // Corrupt already copied files regardless of which asset is copied next.
            staging.walkTopDown().filter { it.isFile }.forEach { it.writeBytes(byteArrayOf()) }
        }

        assertThrows(IllegalStateException::class.java) { EspeakIpa(corruptingContext).installData() }

        assertTrue(File(context.filesDir, "espeak-ng").listFiles()!!.isEmpty())
    }

    @Test
    fun activationFailureDoesNotLeaveAPartiallyInstalledVersion() = withDictionaryTestContext { context ->
        val root = File(context.filesDir, "espeak-ng")
        val blockedContext = withStagingFault(context) {
            // Copying inside staging remains possible; publishing its directory is denied.
            check(root.setWritable(false))
        }
        try {
            assertThrows(IllegalStateException::class.java) { EspeakIpa(blockedContext).installData() }
            assertTrue(!File(root, "1.52.0").exists())
        } finally {
            check(root.setWritable(true))
        }
        assertTrue(EspeakIpa(context).installData().isDirectory)
    }

    private fun withStagingFault(context: Context, fault: (File) -> Unit): Context = object : ContextWrapper(context) {
        override fun getApplicationContext(): Context = this
        override fun getAssets(): AssetManager {
            val root = File(filesDir, "espeak-ng")
            try {
                fault(root.listFiles()!!.single { it.name.endsWith(".part") })
            } catch (failure: Exception) {
                throw AssertionError("Could not apply the installation fault", failure)
            }
            return context.assets
        }
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

        assertThrows(IllegalStateException::class.java) { EspeakIpa(context).installData() }
        assertEquals("keep", root.readText())
    }
}
