package com.prosincerity.ghostwriter.ui

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.prosincerity.ghostwriter.data.LyricFontFamily
import com.prosincerity.ghostwriter.data.LyricTextAlignment
import com.prosincerity.ghostwriter.data.LyricTextSettings
import com.prosincerity.ghostwriter.data.SystemFontFile
import com.prosincerity.ghostwriter.ui.components.toTextStyle
import com.prosincerity.ghostwriter.ui.theme.GhostTypography
import org.junit.Assert.assertEquals
import org.junit.Test

class LyricTextStyleTest {
    @Test
    fun discoveredFont_usesTheSelectedFaceInSharedTextStyleMapping() {
        val font = SystemFontFile("/product/fonts/DeviceFont.ttc", ttcIndex = 2)
        val style = LyricTextSettings(fontFamily = font).toTextStyle(TextStyle()) { selected ->
            assertEquals(font, selected)
            FontFamily.Serif
        }
        assertEquals(FontFamily.Serif, style.fontFamily)
    }

    @Test
    fun unavailableFont_fallsBackWhilePreservingOtherTypography() {
        val style = LyricTextSettings(
            fontFamily = SystemFontFile("/removed/font.ttf"),
            fontSizeSp = 24,
            alignment = LyricTextAlignment.CENTER,
        ).toTextStyle(TextStyle()) { null }
        assertEquals(FontFamily.Monospace, style.fontFamily)
        assertEquals(24.sp, style.fontSize)
        assertEquals(TextAlign.Center, style.textAlign)
    }

    @Test
    fun sizeAndSpacing_keepScalableUnitsAndRelativeLineHeight() {
        val style = LyricTextSettings(fontSizeSp = 24, lineHeightMultiplier = 1.75f, letterSpacingSp = 0.5f)
            .toTextStyle(TextStyle(fontWeight = FontWeight.Normal))
        assertEquals(24.sp, style.fontSize)
        assertEquals(1.75.em, style.lineHeight)
        assertEquals(0.5.sp, style.letterSpacing)
        assertEquals(FontWeight.Normal, style.fontWeight)
    }

    @Test
    fun justifiedText_usesMatchingZeroSpacingWithoutDiscardingSavedSpacing() {
        for (spacing in listOf(-2f, -0.25f, 0f, 0.5f, 10f)) {
            val settings = LyricTextSettings(letterSpacingSp = spacing, alignment = LyricTextAlignment.JUSTIFY)
            val justified = settings.toTextStyle(TextStyle(letterSpacing = 1.sp))
            assertEquals(TextAlign.Justify, justified.textAlign)
            assertEquals(0.sp, justified.letterSpacing)
            assertEquals(spacing, settings.letterSpacingSp, 0f)

            for (alignment in LyricTextAlignment.entries.filter { it != LyricTextAlignment.JUSTIFY }) {
                val restored = settings.copy(alignment = alignment).toTextStyle(TextStyle())
                assertEquals("Spacing for $alignment", spacing.sp, restored.letterSpacing)
            }
        }
    }

    @Test
    fun fontChoices_resolveToPlatformFamilies() {
        val expected = mapOf(
            LyricFontFamily.SYSTEM_DEFAULT to FontFamily.Default,
            LyricFontFamily.SANS_SERIF to FontFamily.SansSerif,
            LyricFontFamily.SERIF to FontFamily.Serif,
            LyricFontFamily.MONOSPACE to FontFamily.Monospace,
            LyricFontFamily.CURSIVE to FontFamily.Cursive,
        )
        for ((choice, family) in expected) {
            assertEquals(
                family,
                LyricTextSettings(fontFamily = choice).toTextStyle(GhostTypography.bodyLarge).fontFamily,
            )
        }
    }

    @Test
    fun alignment_preservesPhysicalAndDirectionAwareChoices() {
        val expected = mapOf(
            LyricTextAlignment.START to TextAlign.Start,
            LyricTextAlignment.CENTER to TextAlign.Center,
            LyricTextAlignment.END to TextAlign.End,
            LyricTextAlignment.LEFT to TextAlign.Left,
            LyricTextAlignment.RIGHT to TextAlign.Right,
            LyricTextAlignment.JUSTIFY to TextAlign.Justify,
        )
        for ((choice, alignment) in expected) {
            assertEquals(alignment, LyricTextSettings(alignment = choice).toTextStyle(TextStyle()).textAlign)
        }
    }
}
