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
    fun options_sortFamiliesAndChooseRegularUprightNormalWidthFaces() {
        val zed = SystemFontFile("/system/fonts/Zed.ttf", familyName = "Zed")
        val alpha = SystemFontFile("/product/fonts/alpha.ttc", familyName = "Alpha")
        val secondFace = alpha.copy(ttcIndex = 1)
        val variable = alpha.copy(weight = 600, variationSettings = "'wght' 600")
        val italic = alpha.copy(italic = true)
        val ui = alpha.copy(path = "/fonts/AlphaUI.ttf", familyName = "Alpha UI")
        val condensed = alpha.copy(variationSettings = "'wdth' 75")
        val fonts = listOf(zed, variable, secondFace, italic, ui, condensed, alpha, zed, alpha)
        val options = systemFontOptions(fonts)

        assertEquals(listOf(LyricFontFamily.SYSTEM_DEFAULT, alpha, zed), options)
        assertEquals(options, systemFontOptions(fonts.reversed()))
        assertEquals(listOf(LyricFontFamily.SYSTEM_DEFAULT), systemFontOptions(emptyList()))
    }

    @Test
    fun options_keepDistinctCollectionFamiliesAndUseNearestWeightWhenRegularIsMissing() {
        val mono = SystemFontFile("/fonts/Device.ttc", familyName = "Droid Sans Mono", weight = 500)
        val serif = mono.copy(ttcIndex = 1, familyName = "Noto Serif")
        assertEquals(listOf(LyricFontFamily.SYSTEM_DEFAULT, mono, serif),
            systemFontOptions(listOf(mono.copy(weight = 700), serif, mono)))
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
