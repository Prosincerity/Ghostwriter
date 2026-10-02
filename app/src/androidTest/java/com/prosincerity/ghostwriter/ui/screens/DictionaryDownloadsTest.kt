package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.SemanticsMatcher
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onAllNodesWithContentDescription
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.data.DictionaryArchiveSource
import com.prosincerity.ghostwriter.data.DictionaryInstaller
import com.prosincerity.ghostwriter.data.DictionarySource
import com.prosincerity.ghostwriter.data.dictionaryArchive
import com.prosincerity.ghostwriter.data.withDictionaryTestContext
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.InputStream
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicInteger
import java.util.zip.GZIPOutputStream
import kotlin.random.Random
import kotlinx.coroutines.test.StandardTestDispatcher

@RunWith(AndroidJUnit4::class)
class DictionaryDownloadsTest {
    // Queue resumptions after IO instead of running recomposition on the IO worker.
    @get:Rule val composeRule = createComposeRule(StandardTestDispatcher())

    @Test
    fun languageSectionsAndSourceNoticesRemainReachableWithLargeText() = withDictionaryTestContext { context ->
        val attempts = AtomicInteger()
        val installer = DictionaryInstaller(context, DictionaryArchiveSource {
            attempts.incrementAndGet()
            error("Downloads must be explicitly requested")
        })
        composeRule.setContent {
            val density = LocalDensity.current
            CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 1.5f)) {
                GhostwriterTheme {
                    DictionaryDownloads(
                        installer,
                        Modifier.width(320.dp).height(480.dp).verticalScroll(rememberScrollState()),
                    )
                }
            }
        }

        waitForDownloadButton()
        for (language in listOf("English", "German", "Turkish")) {
            composeRule.onNodeWithText(language).performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithContentDescription("Download Wiktionary Kaikki for $language")
                .performScrollTo().assertIsDisplayed()
            composeRule.onNodeWithContentDescription("Download eSpeak NG generated for $language")
                .performScrollTo().assertIsDisplayed()
        }
        composeRule.onNodeWithText("Dictionary sources").performScrollTo().assertIsDisplayed()
        composeRule.onNodeWithText("Data sources, attribution, and licensing are listed in Settings → About Ghostwriter.")
            .performScrollTo().assertIsDisplayed()
        assertEquals(0, attempts.get())
    }

    @Test
    fun failedDownloadCanBeRetriedAndMarksInstalledSource() = withDictionaryTestContext { context ->
        val valid = dictionaryArchive(context, listOf("word" to "/wɜːd/"))
        val invalid = ByteArrayOutputStream().use { output ->
            GZIPOutputStream(output).use { it.write("not a database".toByteArray()) }
            output.toByteArray()
        }
        val attempts = AtomicInteger()
        val installer = DictionaryInstaller(context, DictionaryArchiveSource {
            ByteArrayInputStream(if (attempts.incrementAndGet() == 1) invalid else valid)
        })
        composeRule.setContent { GhostwriterTheme { DictionaryDownloads(installer) } }

        waitForDownloadButton()
        composeRule.onNodeWithContentDescription(DOWNLOAD_ENGLISH_WIKTIONARY).performClick()
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithText("Could not download Wiktionary Kaikki for English: Downloaded dictionary is invalid")
                .fetchSemanticsNodes().isNotEmpty()
        }
        waitForDownloadButton()
        composeRule.onNodeWithContentDescription(DOWNLOAD_ENGLISH_WIKTIONARY).performClick()
        composeRule.waitUntil(5_000) {
            composeRule.onAllNodesWithContentDescription(INSTALLED_ENGLISH_WIKTIONARY)
                .fetchSemanticsNodes().isNotEmpty()
        }

        assertEquals(2, attempts.get())
        assertNotNull(installer.installedDatabase("en", DictionarySource.WIKTIONARY))
        composeRule.onNodeWithText("Installed · Available offline").assertExists()
        composeRule.onNodeWithText("Could not download Wiktionary Kaikki for English: Downloaded dictionary is invalid")
            .assertDoesNotExist()
    }

    @Test
    fun cancelDownloadRestoresControlsWithoutInstalling() = withDictionaryTestContext { context ->
        val valid = dictionaryArchive(context, listOf("word" to "/wɜːd/"))
        val started = CountDownLatch(1)
        val release = CountDownLatch(1)
        val installer = DictionaryInstaller(context, DictionaryArchiveSource {
            started.countDown()
            release.await(10, TimeUnit.SECONDS)
            ByteArrayInputStream(valid)
        })
        composeRule.setContent { GhostwriterTheme { DictionaryDownloads(installer) } }

        try {
            waitForDownloadButton()
            composeRule.onNodeWithContentDescription(DOWNLOAD_ENGLISH_WIKTIONARY).performClick()
            composeRule.waitForIdle()
            assertTrue(started.await(5, TimeUnit.SECONDS))
            composeRule.onNodeWithText("Downloading Wiktionary Kaikki...").assertExists()
            composeRule.onNode(SemanticsMatcher.expectValue(
                SemanticsProperties.ProgressBarRangeInfo, ProgressBarRangeInfo.Indeterminate,
            )).assertExists()
            composeRule.onNodeWithContentDescription("Download eSpeak NG generated for English")
                .assertIsNotEnabled()
            composeRule.onNodeWithText("Cancel download").performClick()
        } finally {
            release.countDown()
        }

        waitForDownloadButton()
        composeRule.onNodeWithText("Cancel download").assertDoesNotExist()
        composeRule.onNodeWithContentDescription(INSTALLED_ENGLISH_WIKTIONARY).assertDoesNotExist()
        assertNull(installer.installedDatabase("en", DictionarySource.WIKTIONARY))
    }

    @Test
    fun showsProgressUntilDownloadIsCanceled() = withDictionaryTestContext { context ->
        val archive = ByteArrayOutputStream().use { output ->
            GZIPOutputStream(output).use { it.write(Random(7).nextBytes(500_000)) }
            output.toByteArray()
        }
        val paused = CountDownLatch(1)
        val release = CountDownLatch(1)
        val installer = DictionaryInstaller(context, DictionaryArchiveSource {
            object : InputStream() {
                private var offset = 0

                override fun read(): Int {
                    val byte = ByteArray(1)
                    return if (read(byte, 0, 1) < 0) -1 else byte[0].toInt() and 0xff
                }

                override fun read(buffer: ByteArray, bufferOffset: Int, length: Int): Int {
                    if (length == 0) return 0
                    if (offset >= archive.size) return -1
                    if (offset >= 380_000) {
                        paused.countDown()
                        release.await(10, TimeUnit.SECONDS)
                    }
                    val beforePause = if (offset < 380_000) 380_000 - offset else length
                    val count = minOf(length, archive.size - offset, beforePause)
                    archive.copyInto(buffer, bufferOffset, offset, offset + count)
                    offset += count
                    return count
                }
            }
        })
        composeRule.setContent { GhostwriterTheme { DictionaryDownloads(installer) } }

        try {
            waitForDownloadButton()
            composeRule.onNodeWithContentDescription(DOWNLOAD_ENGLISH_WIKTIONARY).performClick()
            composeRule.waitForIdle()
            assertTrue(paused.await(5, TimeUnit.SECONDS))
            composeRule.waitUntil(5_000) {
                composeRule.onAllNodesWithText("%", substring = true).fetchSemanticsNodes().isNotEmpty()
            }
            val indicator = composeRule.onAllNodes(SemanticsMatcher.keyIsDefined(SemanticsProperties.ProgressBarRangeInfo))
                .fetchSemanticsNodes().single().config[SemanticsProperties.ProgressBarRangeInfo]
            assertTrue(indicator.current > 0f && indicator.current <= 1f)
            composeRule.onNodeWithText("Cancel download").performClick()
        } finally {
            release.countDown()
        }

        waitForDownloadButton()
        assertNull(installer.installedDatabase("en", DictionarySource.WIKTIONARY))
    }

    private fun waitForDownloadButton() {
        composeRule.waitUntil(5_000) {
            val node = composeRule.onAllNodesWithContentDescription(DOWNLOAD_ENGLISH_WIKTIONARY)
                .fetchSemanticsNodes().singleOrNull()
            node != null && !node.config.contains(SemanticsProperties.Disabled)
        }
    }

    private companion object {
        const val DOWNLOAD_ENGLISH_WIKTIONARY = "Download Wiktionary Kaikki for English"
        const val INSTALLED_ENGLISH_WIKTIONARY = "Wiktionary Kaikki installed for English"
    }
}
