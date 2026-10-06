package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.currentComposer
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.data.LyricFontFamily
import com.prosincerity.ghostwriter.data.LyricTextAlignment
import com.prosincerity.ghostwriter.data.LyricTextSettings
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import com.prosincerity.ghostwriter.ui.theme.GhostColorScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class LyricsNotepadTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun restartingNotepads_preservesDefaultAndExplicitSettingsAndEditing() {
        val probe = RecompositionProbe()
        var defaultLyrics by mutableStateOf("")
        var explicitLyrics by mutableStateOf("Explicit verse")
        val settings = LyricTextSettings(fontSizeSp = 24, alignment = LyricTextAlignment.RIGHT)
        try {
            composeRule.setContent {
                GhostwriterTheme {
                    Column {
                        probe.captureNext(currentComposer)
                        LyricsNotepad(defaultLyrics, { defaultLyrics = it })
                        probe.captureNext(currentComposer)
                        LyricsNotepad(explicitLyrics, { explicitLyrics = it }, Modifier, settings)
                    }
                }
            }
            composeRule.runOnIdle {
                assertEquals(2, probe.scopes.size)
                probe.scopes.forEach { it.invalidate() }
            }
            composeRule.onNodeWithText("Start writing...").assertExists()
            composeRule.onNodeWithText("Explicit verse").performTextReplacement("Updated verse")
            composeRule.runOnIdle { probe.scopes.take(2).forEach { it.invalidate() } }
            composeRule.onNodeWithText("Updated verse").assertExists()
            composeRule.onNodeWithText("Start writing...").assertExists()
            val results = mutableListOf<TextLayoutResult>()
            composeRule.onNodeWithText("Updated verse")
                .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { it(results) }
            assertEquals(24.sp, results.first().layoutInput.style.fontSize)
            assertEquals(TextAlign.Right, results.first().layoutInput.style.textAlign)
            composeRule.runOnIdle { assertEquals("Updated verse", explicitLyrics) }
        } finally {
            composeRule.runOnIdle { probe.dispose() }
        }
    }

    @Test fun themeChanges_preserveDefaultAndExplicitNotepadTextAndEditing() {
        var color by mutableStateOf(GhostColorScheme.background)
        var defaultLyrics by mutableStateOf("Default verse")
        var explicitLyrics by mutableStateOf("Explicit verse")
        val defaultCallback: (String) -> Unit = { defaultLyrics = it }
        val explicitCallback: (String) -> Unit = { explicitLyrics = it }
        composeRule.setContent {
            GhostwriterTheme {
                MaterialTheme(colorScheme = GhostColorScheme.copy(background = color)) {
                    Column {
                        LyricsNotepad(defaultLyrics, defaultCallback)
                        LyricsNotepad(explicitLyrics, explicitCallback, Modifier, LyricTextSettings())
                    }
                }
            }
        }
        composeRule.runOnIdle { color = Color.DarkGray }
        composeRule.onNodeWithText("Default verse").performTextReplacement("Default edited")
        composeRule.onNodeWithText("Explicit verse").performTextReplacement("Explicit edited")
        composeRule.runOnIdle {
            assertEquals("Default edited", defaultLyrics)
            assertEquals("Explicit edited", explicitLyrics)
            color = Color.Black
        }
        composeRule.onNodeWithText("Default edited").assertExists()
        composeRule.onNodeWithText("Explicit edited").assertExists()
    }

    @Test
    fun replacingEditingCallback_keepsTextAndUsesLatestOwner() {
        var lyrics by mutableStateOf("First")
        var owner = "first"
        var callback by mutableStateOf<(String) -> Unit>({ lyrics = it; owner = "first" })
        composeRule.setContent {
            GhostwriterTheme { LyricsNotepad(lyrics, callback) }
        }
        composeRule.runOnIdle { callback = { lyrics = it; owner = "second" } }
        composeRule.onNodeWithText("First").performTextReplacement("Second")
        composeRule.runOnIdle { assertEquals("Second", lyrics); assertEquals("second", owner) }
    }

    @Test
    fun notepad_typographyChangesPreserveLyricsAndEditingUpdatesHoistedState() {
        var lyrics by mutableStateOf("")
        var latestLyrics = lyrics
        var useDefaults by mutableStateOf(true)
        var settings by mutableStateOf(
            LyricTextSettings(LyricFontFamily.SERIF, 20, 1.75f, 0.5f, LyricTextAlignment.CENTER),
        )
        val onLyricsChange: (String) -> Unit = {
            lyrics = it
            latestLyrics = it
        }
        composeRule.setContent {
            GhostwriterTheme {
                if (useDefaults) {
                    LyricsNotepad(lyrics = lyrics, onLyricsChange = onLyricsChange)
                } else {
                    LyricsNotepad(
                        lyrics = lyrics,
                        onLyricsChange = onLyricsChange,
                        modifier = Modifier.fillMaxWidth(),
                        textSettings = settings,
                    )
                }
            }
        }

        // Keep default-argument coverage when folding the empty-notepad test here.
        composeRule.onNodeWithText("Start writing...").assertExists()
        composeRule.runOnIdle {
            lyrics = "First line\nSecond line"
            useDefaults = false
        }
        val initial = textLayout().layoutInput.style
        assertEquals(FontFamily.Serif, initial.fontFamily)
        assertEquals(20.sp, initial.fontSize)
        assertEquals(1.75.em, initial.lineHeight)
        assertEquals(0.5.sp, initial.letterSpacing)
        assertEquals(TextAlign.Center, initial.textAlign)

        composeRule.runOnIdle {
            settings = settings.copy(fontFamily = LyricFontFamily.MONOSPACE, alignment = LyricTextAlignment.RIGHT)
        }
        val changed = textLayout().layoutInput.style
        assertEquals(FontFamily.Monospace, changed.fontFamily)
        assertEquals(TextAlign.Right, changed.textAlign)
        composeRule.onNodeWithText("First line\nSecond line").assertExists()

        composeRule.onNodeWithText("First line\nSecond line").performTextReplacement("Updated line")
        composeRule.onNodeWithText("First line\nSecond line").assertDoesNotExist()
        composeRule.onNodeWithText("Updated line").assertExists()
        composeRule.runOnIdle { assertEquals("Updated line", latestLyrics) }
    }

    @Test
    fun notepad_followsFontScalingAndKeepsLineHeightProportional() {
        var fontScale by mutableStateOf(1f)
        composeRule.setContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, fontScale)) {
                GhostwriterTheme {
                    LyricsNotepad(
                        lyrics = "One\nTwo",
                        onLyricsChange = {},
                        modifier = Modifier.fillMaxWidth(),
                        textSettings = LyricTextSettings(fontSizeSp = 20, lineHeightMultiplier = 2f),
                    )
                }
            }
        }
        val normal = textLayout()
        composeRule.runOnIdle { fontScale = 2f }
        val scaled = textLayout()
        assertEquals(2, scaled.lineCount)
        assertTrue("Increasing font scale should increase text height", scaled.size.height > normal.size.height)
        val normalFontSizePx = with(normal.layoutInput.density) { normal.layoutInput.style.fontSize.toPx() }
        val scaledFontSizePx = with(scaled.layoutInput.density) { scaled.layoutInput.style.fontSize.toPx() }
        val normalBaselineGap = normal.getLineBaseline(1) - normal.getLineBaseline(0)
        val scaledBaselineGap = scaled.getLineBaseline(1) - scaled.getLineBaseline(0)
        // Compare relative line height with the converted font size, including nonlinear scaling.
        assertEquals("Normal line spacing", normalFontSizePx * 2f, normalBaselineGap, 2f)
        assertEquals("Scaled line spacing", scaledFontSizePx * 2f, scaledBaselineGap, 2f)
        assertEquals(1f, normal.layoutInput.density.fontScale, 0f)
        assertEquals(2f, scaled.layoutInput.density.fontScale, 0f)
        assertEquals(20.sp, scaled.layoutInput.style.fontSize)
        composeRule.onNodeWithText("One\nTwo").assertExists()
    }

    @Test
    fun justifiedNotepad_keepsWrappedWordsInsideTheFieldWithTightAndWideSpacing() {
        val lyrics = "Find the rhythm in the words and let the next line carry the beat. ".repeat(6) +
            "\nA final line"
        var settings by mutableStateOf(
            LyricTextSettings(fontSizeSp = 20, letterSpacingSp = -2f, alignment = LyricTextAlignment.JUSTIFY),
        )
        var fontScale by mutableStateOf(1f)
        composeRule.setContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, fontScale)) {
                GhostwriterTheme {
                    LyricsNotepad(
                        lyrics = lyrics,
                        onLyricsChange = {},
                        modifier = Modifier.width(220.dp),
                        textSettings = settings,
                    )
                }
            }
        }

        for (scale in listOf(1f, 2f)) {
            for (spacing in listOf(-2f, 0f, 5f)) {
                composeRule.runOnIdle {
                    fontScale = scale
                    settings = settings.copy(letterSpacingSp = spacing)
                }
                val layout = textLayout()
                assertEquals(TextAlign.Justify, layout.layoutInput.style.textAlign)
                assertEquals(0.sp, layout.layoutInput.style.letterSpacing)
                assertTrue("Fixture must wrap", layout.lineCount > 2)
                for (offset in lyrics.indices.filter { !lyrics[it].isWhitespace() }) {
                    val bounds = layout.getBoundingBox(offset)
                    assertTrue("Character $offset overflows left: $bounds", bounds.left >= -1f)
                    assertTrue(
                        "Character $offset overflows right: $bounds (width ${layout.size.width})",
                        bounds.right <= layout.size.width + 1f,
                    )
                }
            }
        }

        composeRule.runOnIdle { settings = settings.copy(alignment = LyricTextAlignment.START, letterSpacingSp = -2f) }
        assertEquals((-2).sp, textLayout().layoutInput.style.letterSpacing)
        composeRule.onNodeWithText(lyrics).assertExists()
    }

    private fun textLayout(): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
            it(results)
        }
        return results.single()
    }
}
