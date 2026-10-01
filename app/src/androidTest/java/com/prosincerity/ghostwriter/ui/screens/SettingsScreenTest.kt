package com.prosincerity.ghostwriter.ui.screens

import android.content.Context
import android.content.ContextWrapper
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.ComposeTimeoutException
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasScrollToIndexAction
import androidx.compose.ui.test.hasText
import androidx.compose.ui.test.isRoot
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performScrollToNode
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.printToString
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import androidx.test.platform.app.InstrumentationRegistry
import com.prosincerity.ghostwriter.data.LyricFontFamily
import com.prosincerity.ghostwriter.data.LyricTextAlignment
import com.prosincerity.ghostwriter.data.LyricTextSettings
import com.prosincerity.ghostwriter.data.Settings
import com.prosincerity.ghostwriter.data.SystemFontCatalog
import com.prosincerity.ghostwriter.data.SystemFontFile
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SettingsScreenTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val appContext = InstrumentationRegistry.getInstrumentation().targetContext
    private val testContext = object : ContextWrapper(appContext) {
        override fun getApplicationContext(): Context = this
        override fun getSharedPreferences(name: String, mode: Int) =
            appContext.getSharedPreferences("${name}_typography_test", mode)
    }

    @Before
    fun setUp() {
        testContext.getSharedPreferences("ghostwriter_settings", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @After
    fun tearDown() {
        testContext.getSharedPreferences("ghostwriter_settings", Context.MODE_PRIVATE).edit().clear().commit()
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun discoveredFont_canBeSelectedAndSurvivesPreferencesReload() {
        val font = SystemFontCatalog.availableFonts().filterIsInstance<SystemFontFile>().first()
        Settings.setLyricTextSettings(testContext, LyricTextSettings(fontFamily = font))
        assertEquals(font, Settings.getLyricTextSettings(testContext).fontFamily)
        composeRule.setContent {
            CompositionLocalProvider(LocalContext provides testContext) {
                GhostwriterTheme {
                    SettingsScreen(onBack = {}, onOpenAbout = {}, onOpenDictionaryDownloads = {})
                }
            }
        }
        select("Font family", "Serif")
        composeRule.runOnIdle {
            assertEquals(LyricFontFamily.SERIF, Settings.getLyricTextSettings(testContext).fontFamily)
        }

        composeRule.onNodeWithContentDescription("Font family").performScrollTo().performClick()
        // Discovery runs on an external dispatcher, and offscreen lazy rows are not
        // composed yet. Wait by scrolling the actual picker to the requested face.
        var lastScrollFailure: AssertionError? = null
        try {
            composeRule.waitUntil(10_000) {
                try {
                    composeRule.onNode(hasScrollToIndexAction()).performScrollToNode(hasText(font.label))
                    true
                } catch (failure: AssertionError) {
                    lastScrollFailure = failure
                    false
                }
            }
        } catch (timeout: ComposeTimeoutException) {
            val roots = composeRule.onAllNodes(isRoot(), useUnmergedTree = true)
            val tree = roots.fetchSemanticsNodes().indices.joinToString("\n") { roots[it].printToString() }
            throw AssertionError(
                "Device font ${font.label} was not found in the picker. " +
                    "Last scroll failure: ${lastScrollFailure?.message}\n$tree",
                timeout,
            )
        }
        composeRule.onNodeWithText(font.label).performClick()
        composeRule.runOnIdle {
            assertEquals(font, Settings.getLyricTextSettings(testContext).fontFamily)
        }
        composeRule.onNodeWithText(font.label).assertExists()
    }

    @Test
    fun typographyControls_saveAllChoicesAndLeaveAutosaveSettingsIntact() {
        Settings.setAutosaveIntervalSeconds(testContext, 120)
        Settings.setAutosaveCount(testContext, 4)
        composeRule.setContent {
            CompositionLocalProvider(LocalContext provides testContext) {
                GhostwriterTheme {
                    SettingsScreen(onBack = {}, onOpenAbout = {}, onOpenDictionaryDownloads = {})
                }
            }
        }

        select("Font family", "Serif")
        select("Font size", "24 sp")
        setSlider("Line height", 3.25f)
        setSlider("Letter spacing", 7.5f)
        select("Text alignment", "Center")
        composeRule.onNodeWithText("Find the rhythm in the words\nLet the next line carry the beat")
            .performScrollTo().assertExists()

        composeRule.runOnIdle {
            assertEquals(
                LyricTextSettings(LyricFontFamily.SERIF, 24, 3.25f, 7.5f, LyricTextAlignment.CENTER),
                Settings.getLyricTextSettings(testContext),
            )
            assertEquals(120, Settings.getAutosaveIntervalSeconds(testContext))
            assertEquals(4, Settings.getAutosaveCount(testContext))
        }
    }

    @Test
    fun savedTypography_isShownWhenSettingsOpens() {
        Settings.setLyricTextSettings(
            testContext,
            LyricTextSettings(LyricFontFamily.CURSIVE, 22, 2f, 1.5f, LyricTextAlignment.RIGHT),
        )
        composeRule.setContent {
            CompositionLocalProvider(LocalContext provides testContext) {
                GhostwriterTheme {
                    SettingsScreen(onBack = {}, onOpenAbout = {}, onOpenDictionaryDownloads = {})
                }
            }
        }
        for (value in listOf("Cursive", "22 sp", "2×", "1.5 sp", "Right")) {
            composeRule.onNodeWithText(value).performScrollTo().assertExists()
        }
    }

    @Test
    fun preferences_defaultUnknownEnumsAndNormalizeInvalidNumbers() {
        assertEquals(LyricTextSettings(), Settings.getLyricTextSettings(testContext))
        testContext.getSharedPreferences("ghostwriter_settings", Context.MODE_PRIVATE).edit()
            .putString("lyric_font_family", "REMOVED_FONT")
            .putString("lyric_text_alignment", "UNKNOWN")
            .putInt("lyric_font_size_sp", 500)
            .putFloat("lyric_line_height_multiplier", Float.NaN)
            .putFloat("lyric_letter_spacing_sp", Float.POSITIVE_INFINITY)
            .commit()
        assertEquals(LyricTextSettings(fontSizeSp = 32), Settings.getLyricTextSettings(testContext))

        Settings.setLyricTextSettings(testContext, LyricTextSettings(fontSizeSp = -100, letterSpacingSp = -20f))
        assertEquals(
            LyricTextSettings(fontSizeSp = 12, letterSpacingSp = -2f),
            Settings.getLyricTextSettings(testContext),
        )
    }

    @Test
    fun justifiedAlignment_usesSafePreviewSpacingAndRestoresTheSavedSpacing() {
        Settings.setLyricTextSettings(
            testContext,
            LyricTextSettings(letterSpacingSp = -2f, alignment = LyricTextAlignment.JUSTIFY),
        )
        composeRule.setContent {
            CompositionLocalProvider(LocalContext provides testContext) {
                GhostwriterTheme {
                    SettingsScreen(onBack = {}, onOpenAbout = {}, onOpenDictionaryDownloads = {})
                }
            }
        }
        composeRule.onNodeWithContentDescription("Letter spacing").performScrollTo().assertIsNotEnabled()
        val spacing = composeRule.onNodeWithContentDescription("Letter spacing").fetchSemanticsNode()
            .config[SemanticsProperties.ProgressBarRangeInfo]
        assertEquals(0f, spacing.current, 0f)
        composeRule.onNodeWithText(
            "Justified text uses normal letter spacing. Your spacing is kept for other alignments.",
        ).performScrollTo().assertExists()

        val preview = composeRule.onNodeWithText("Find the rhythm in the words\nLet the next line carry the beat")
        val results = mutableListOf<TextLayoutResult>()
        preview.performScrollTo().performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
        assertEquals(TextAlign.Justify, results.single().layoutInput.style.textAlign)
        assertEquals(0.sp, results.single().layoutInput.style.letterSpacing)
        composeRule.runOnIdle {
            assertEquals(-2f, Settings.getLyricTextSettings(testContext).letterSpacingSp, 0f)
        }

        select("Text alignment", "Start")
        composeRule.onNodeWithContentDescription("Letter spacing").performScrollTo().assertIsEnabled()
        val restored = composeRule.onNodeWithContentDescription("Letter spacing").fetchSemanticsNode()
            .config[SemanticsProperties.ProgressBarRangeInfo]
        assertEquals(-2f, restored.current, 0f)
        composeRule.runOnIdle {
            assertEquals(-2f, Settings.getLyricTextSettings(testContext).letterSpacingSp, 0f)
            assertEquals(LyricTextAlignment.START, Settings.getLyricTextSettings(testContext).alignment)
        }
    }

    private fun select(control: String, option: String) {
        composeRule.onNodeWithContentDescription(control).performScrollTo().performClick()
        composeRule.onNodeWithText(option).performClick()
    }

    @Test
    fun sliders_exposeExpandedRangesAndPersistTheirEndpoints() {
        composeRule.setContent {
            CompositionLocalProvider(LocalContext provides testContext) {
                GhostwriterTheme {
                    SettingsScreen(onBack = {}, onOpenAbout = {}, onOpenDictionaryDownloads = {})
                }
            }
        }
        val lineRange = composeRule.onNodeWithContentDescription("Line height").fetchSemanticsNode()
            .config[SemanticsProperties.ProgressBarRangeInfo].range
        val letterRange = composeRule.onNodeWithContentDescription("Letter spacing").fetchSemanticsNode()
            .config[SemanticsProperties.ProgressBarRangeInfo].range
        assertEquals(0.5f..4f, lineRange)
        assertEquals(-2f..10f, letterRange)

        for ((height, spacing) in listOf(0.5f to -2f, 4f to 10f)) {
            setSlider("Line height", height)
            setSlider("Letter spacing", spacing)
            composeRule.runOnIdle {
                val saved = Settings.getLyricTextSettings(testContext)
                assertEquals(height, saved.lineHeightMultiplier)
                assertEquals(spacing, saved.letterSpacingSp)
            }
        }
    }

    private fun setSlider(control: String, value: Float) {
        composeRule.onNodeWithContentDescription(control).performScrollTo()
            .performSemanticsAction(SemanticsActions.SetProgress) { it(value) }
    }
}
