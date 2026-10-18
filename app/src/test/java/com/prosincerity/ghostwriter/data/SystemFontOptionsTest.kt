package com.prosincerity.ghostwriter.data

import org.junit.Assert.assertEquals
import org.junit.Test

class SystemFontOptionsTest {
    private val saved = SystemFontFile("/fonts/Device.ttc", ttcIndex = 1, familyName = "Old family")

    @Test
    fun genericChoicesKeepTheirLabelsWithoutCheckingFileAvailability() {
        for (font in LyricFontFamily.entries) {
            assertEquals(font.label, systemFontSelectionLabel(font, emptyList()) {
                error("Generic fonts do not have backing files")
            })
        }
    }

    @Test
    fun unavailableFontKeepsItsSavedLabelEvenWhenTheFaceWasDiscovered() {
        val discovered = saved.copy(familyName = "New family")

        assertEquals("Old family (unavailable)", systemFontSelectionLabel(saved, listOf(discovered)) { path ->
            assertEquals(saved.path, path)
            false
        })
    }

    @Test
    fun matchingCollectionFaceUsesItsDiscoveredFamilyLabel() {
        val otherFace = saved.copy(ttcIndex = 0, familyName = "Another family")
        val discovered = saved.copy(familyName = "New family", weight = 700, italic = true)

        assertEquals("New family", systemFontSelectionLabel(saved, listOf(otherFace, discovered)) { true })
    }

    @Test
    fun missingMatchesKeepTheSavedLabel() {
        val otherFace = saved.copy(ttcIndex = 0, familyName = "Another family")
        val unrelated = saved.copy(path = "/fonts/Other.ttf", familyName = "Other family")

        for (options in listOf(emptyList(), listOf(otherFace), listOf(unrelated))) {
            assertEquals(saved.label, systemFontSelectionLabel(saved, options) { true })
        }
    }

    @Test
    fun firstFamilyOrFaceMatchKeepsDiscoveryOrder() {
        val sameFamily = saved.copy(path = "/fonts/Other.ttf")
        val sameFace = saved.copy(familyName = "Renamed family")

        assertEquals(saved.label, systemFontSelectionLabel(saved, listOf(sameFamily, sameFace)) { true })
        assertEquals(sameFace.label, systemFontSelectionLabel(saved, listOf(sameFace, sameFamily)) { true })
    }

    @Test
    fun familyGroupingIgnoresCaseWhitespaceAndUiSuffixes() {
        val regular = SystemFontFile("/fonts/a.ttf", familyName = " Device  Font ")
        val upperCase = regular.copy(path = "/fonts/b.ttf", familyName = "DEVICE FONT")
        val ui = regular.copy(path = "/fonts/0.ttf", familyName = "device Font UI")

        assertEquals(listOf(LyricFontFamily.SYSTEM_DEFAULT, regular), systemFontOptions(listOf(ui, upperCase, regular)))
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
    fun options_prioritizeFamilyFaceThenUprightStyleWeightAndWidth() {
        val regular = SystemFontFile("/fonts/Device.ttf", familyName = "Device")
        val preferredPairs = listOf(
            regular.copy(italic = true, weight = 700) to regular.copy(familyName = "Device UI"),
            regular.copy(weight = 700) to regular.copy(italic = true),
            regular.copy(weight = 500, variationSettings = "'wdth' 75") to regular.copy(weight = 700),
            regular to regular.copy(variationSettings = "'wdth' 75"),
        )
        for ((preferred, other) in preferredPairs) {
            val expected = listOf(LyricFontFamily.SYSTEM_DEFAULT, preferred)
            assertEquals(expected, systemFontOptions(listOf(other, preferred)))
            assertEquals(expected, systemFontOptions(listOf(preferred, other)))
        }
    }

    @Test
    fun equivalentFaces_chooseByPathCollectionIndexThenVariationSettings() {
        val regular = SystemFontFile("/fonts/a.ttc", familyName = "Device")
        val preferredPairs = listOf(
            regular.copy(ttcIndex = 2) to regular.copy(path = "/fonts/b.ttf"),
            regular to regular.copy(ttcIndex = 1),
            regular.copy(variationSettings = "'opsz' 12") to regular.copy(variationSettings = "'wght' 400"),
        )
        for ((preferred, other) in preferredPairs) {
            val expected = listOf(LyricFontFamily.SYSTEM_DEFAULT, preferred)
            assertEquals(expected, systemFontOptions(listOf(other, preferred)))
            assertEquals(expected, systemFontOptions(listOf(preferred, other)))
        }
    }

}
