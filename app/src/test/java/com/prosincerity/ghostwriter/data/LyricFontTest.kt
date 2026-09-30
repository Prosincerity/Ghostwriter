package com.prosincerity.ghostwriter.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class LyricFontTest {
    @Test
    fun preferences_preserveExistingGenericChoices() {
        for (font in LyricFontFamily.entries) {
            assertEquals(font.name, font.toPreferenceValue())
            assertEquals(font, lyricFontFromPreference(font.name))
        }
    }

    @Test
    fun preferences_roundTripCollectionFacesAndVariableFontSettings() {
        val font = SystemFontFile(
            path = "/product/fonts/DeviceFont.ttc",
            ttcIndex = 2,
            weight = 650,
            italic = true,
            variationSettings = "'wght' 650,'wdth' 90",
        )
        val restored = lyricFontFromPreference(font.toPreferenceValue())
        assertEquals(font, restored)
        assertEquals(font, LyricTextSettings(fontFamily = restored).normalized().fontFamily)
        assertTrue(font.label.contains("DeviceFont"))
        assertTrue(font.label.contains("face 3"))
    }

    @Test
    fun preferences_defaultMissingUnknownAndMalformedChoices() {
        val invalidFont = SystemFontFile("/system/fonts/Test.ttf", ttcIndex = -1)
        val invalidValues = listOf(
            null, "REMOVED_FONT", "{", "{}", invalidFont.toPreferenceValue(),
            SystemFontFile("").toPreferenceValue(),
            SystemFontFile("   ").toPreferenceValue(),
            SystemFontFile("/test.ttf", weight = 0).toPreferenceValue(),
            SystemFontFile("/test.ttf", weight = 1001).toPreferenceValue(),
        )
        for (value in invalidValues) {
            assertEquals(LyricFontFamily.MONOSPACE, lyricFontFromPreference(value))
        }
    }

    @Test
    fun preferences_acceptBothWeightLimitsAndPreserveLargeCollectionIndices() {
        for (weight in listOf(1, 1000)) {
            val font = SystemFontFile("/fonts/Collection.ttc", ttcIndex = 99, weight = weight)
            assertEquals(font, lyricFontFromPreference(font.toPreferenceValue()))
        }
    }

    @Test
    fun options_sortDeviceFontsAndDeduplicateWithoutLosingFacesOrVariations() {
        val zed = SystemFontFile("/system/fonts/Zed.ttf")
        val alpha = SystemFontFile("/product/fonts/alpha.ttc")
        val secondFace = alpha.copy(ttcIndex = 1)
        val variable = alpha.copy(weight = 600, variationSettings = "'wght' 600")
        val fonts = listOf(zed, variable, secondFace, alpha, zed, alpha)
        val options = systemFontOptions(fonts)

        assertEquals(LyricFontFamily.entries, options.take(LyricFontFamily.entries.size))
        assertEquals(listOf(alpha, secondFace, variable, zed), options.drop(LyricFontFamily.entries.size))
        assertEquals(options, systemFontOptions(fonts.reversed()))
        assertEquals(LyricFontFamily.entries, systemFontOptions(emptyList()))
    }
}
