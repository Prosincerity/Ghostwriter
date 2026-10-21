package com.prosincerity.ghostwriter.data

import org.junit.Assert.assertEquals
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
            familyName = "Device Font",
        )
        val restored = lyricFontFromPreference(font.toPreferenceValue())
        assertEquals(font, restored)
        assertEquals(font, LyricTextSettings(fontFamily = restored).normalized().fontFamily)
        assertEquals("Device Font", (restored as SystemFontFile).label)
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
    fun legacySavedFace_keepsMetadataAndGetsACleanFallbackLabel() {
        val old = """{"path":"/system/fonts/NotoNaskhArabicUI-Bold.ttf","ttcIndex":0,"weight":700,"italic":false,"variationSettings":""}"""
        val restored = lyricFontFromPreference(old) as SystemFontFile
        assertEquals("Noto Naskh Arabic", restored.label)
        assertEquals(700, restored.weight)
        assertEquals("", restored.familyName)
    }
}
