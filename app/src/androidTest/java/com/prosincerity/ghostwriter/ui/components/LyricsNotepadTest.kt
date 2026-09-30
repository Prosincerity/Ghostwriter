package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.data.LyricFontFamily
import com.prosincerity.ghostwriter.data.LyricTextAlignment
import com.prosincerity.ghostwriter.data.LyricTextSettings
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
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
    fun emptyNotepad_showsExistingPlaceholder() {
        composeRule.setContent {
            GhostwriterTheme {
                LyricsNotepad(lyrics = "", onLyricsChange = {})
            }
        }

        composeRule.onNodeWithText("Start writing...").assertExists()
    }

    @Test
    fun notepad_displaysAndUpdatesHoistedLyrics() {
        var latestLyrics = "Opening line"
        composeRule.setContent {
            var lyrics by remember { mutableStateOf(latestLyrics) }
            GhostwriterTheme {
                LyricsNotepad(
                    lyrics = lyrics,
                    onLyricsChange = {
                        lyrics = it
                        latestLyrics = it
                    },
                )
            }
        }

        composeRule.onNodeWithText("Opening line").performTextReplacement("Updated line")

        composeRule.onNodeWithText("Opening line").assertDoesNotExist()
        composeRule.onNodeWithText("Updated line").assertExists()
        composeRule.runOnIdle { assertEquals("Updated line", latestLyrics) }
    }

    @Test
    fun notepad_appliesTypographyAndRecomposesWithoutChangingLyrics() {
        var settings by mutableStateOf(
            LyricTextSettings(LyricFontFamily.SERIF, 20, 1.75f, 0.5f, LyricTextAlignment.CENTER),
        )
        composeRule.setContent {
            GhostwriterTheme {
                LyricsNotepad(
                    lyrics = "First line\nSecond line",
                    onLyricsChange = {},
                    modifier = Modifier.fillMaxWidth(),
                    textSettings = settings,
                )
            }
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

    private fun textLayout(): TextLayoutResult {
        val results = mutableListOf<TextLayoutResult>()
        composeRule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
            it(results)
        }
        return results.single()
    }
}
