package com.prosincerity.ghostwriter.data

import org.junit.Assert.assertEquals
import org.junit.Test

class LyricTextSettingsTest {
    @Test
    fun normalization_boundsSizesAndSpacingWithoutChangingFontOrAlignment() {
        val tooSmall = LyricTextSettings(
            fontFamily = LyricFontFamily.SERIF,
            fontSizeSp = -1,
            lineHeightMultiplier = 0.2f,
            letterSpacingSp = -10f,
            alignment = LyricTextAlignment.RIGHT,
        ).normalized()
        assertEquals(12, tooSmall.fontSizeSp)
        assertEquals(0.5f, tooSmall.lineHeightMultiplier)
        assertEquals(-2f, tooSmall.letterSpacingSp)
        assertEquals(LyricFontFamily.SERIF, tooSmall.fontFamily)
        assertEquals(LyricTextAlignment.RIGHT, tooSmall.alignment)

        val tooLarge = LyricTextSettings(fontSizeSp = 500, lineHeightMultiplier = 50f, letterSpacingSp = 50f)
            .normalized()
        assertEquals(32, tooLarge.fontSizeSp)
        assertEquals(4f, tooLarge.lineHeightMultiplier)
        assertEquals(10f, tooLarge.letterSpacingSp)
    }

    @Test
    fun normalization_replacesNonfiniteValuesAndPreservesValidChoices() {
        for (invalid in listOf(Float.NaN, Float.POSITIVE_INFINITY, Float.NEGATIVE_INFINITY)) {
            assertEquals(
                LyricTextSettings(),
                LyricTextSettings(lineHeightMultiplier = invalid, letterSpacingSp = invalid).normalized(),
            )
        }
        val settings = LyricTextSettings(LyricFontFamily.CURSIVE, 24, 1.75f, 0.25f, LyricTextAlignment.CENTER)
        assertEquals(settings, settings.normalized())
    }

    @Test
    fun expandedRanges_preserveOldPreferencesAndNewIntermediateValues() {
        for ((height, spacing) in listOf(1.15f to 0.25f, 0.5f to -2f, 3.33f to 7.77f, 4f to 10f)) {
            val settings = LyricTextSettings(lineHeightMultiplier = height, letterSpacingSp = spacing)
            assertEquals(settings, settings.normalized())
        }
    }
}
