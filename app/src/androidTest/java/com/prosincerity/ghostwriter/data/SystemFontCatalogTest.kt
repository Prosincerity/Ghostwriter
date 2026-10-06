package com.prosincerity.ghostwriter.data

import android.graphics.fonts.Font
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.prosincerity.ghostwriter.ui.components.toTextStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import androidx.test.platform.app.InstrumentationRegistry
import com.prosincerity.ghostwriter.R
import java.io.File
import org.junit.runner.RunWith
import java.nio.ByteBuffer
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class SystemFontCatalogTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    @get:Rule val folder = TemporaryFolder(context.cacheDir)

    private fun fontFile(): File = File(folder.root, "Roboto-Regular.ttf").apply {
        if (!exists()) {
            context.resources.openRawResource(R.font.roboto_regular).use { input ->
                outputStream().use { input.copyTo(it) }
            }
        }
    }

    private fun fixtureFont(weight: Int = 400): Font = Font.Builder(fontFile()).setWeight(weight).build()

    private fun fixtureOptions(): List<LyricFont> =
        SystemFontCatalog.availableFonts(Locale.ENGLISH) { setOf(fixtureFont()) }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun savedFaceLabels_handleMissingFilesDifferentFacesAndFamilyMatches() {
        val regular = fixtureOptions().filterIsInstance<SystemFontFile>().single()
        assertEquals("System default", SystemFontCatalog.selectionLabel(LyricFontFamily.SYSTEM_DEFAULT, emptyList()))
        val missing = regular.copy(path = File(folder.root, "missing.ttf").absolutePath)
        assertEquals("${missing.label} (unavailable)", SystemFontCatalog.selectionLabel(missing, emptyList()))
        assertEquals(regular.label, SystemFontCatalog.selectionLabel(regular, emptyList()))
        val otherFace = regular.copy(ttcIndex = regular.ttcIndex + 1, familyName = "Another family")
        assertEquals(regular.label, SystemFontCatalog.selectionLabel(regular, listOf(otherFace, regular)))
        val sameFamily = regular.copy(path = "/other/file.ttf")
        assertEquals(regular.label, SystemFontCatalog.selectionLabel(regular, listOf(sameFamily)))
        assertFalse(SystemFontCatalog.matchesLanguage("und-Qaaa", Locale.ENGLISH))
    }
    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun discoveryFailure_keepsOnlySystemDefault() {
        assertEquals(listOf(LyricFontFamily.SYSTEM_DEFAULT), SystemFontCatalog.availableFonts(Locale.ENGLISH) {
            throw IllegalStateException("System font discovery unavailable")
        })
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun memoryOnlyFont_isExcludedBecauseItHasNoPersistableFilePath() {
        val saved = fixtureOptions().filterIsInstance<SystemFontFile>().single()
        val bytes = java.io.File(saved.path).readBytes()
        val buffer = ByteBuffer.allocateDirect(bytes.size).apply { put(bytes); flip() }
        val font = Font.Builder(buffer).setTtcIndex(saved.ttcIndex).build()
        assertNull(font.file)
        assertEquals(listOf(LyricFontFamily.SYSTEM_DEFAULT), SystemFontCatalog.availableFonts(Locale.ENGLISH) { setOf(font) })
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun discovery_groupsFixtureFacesAndRetainsRepresentativeMetadata() {
        val regular = fixtureFont()
        val bold = fixtureFont(weight = 700)
        val options = SystemFontCatalog.availableFonts(Locale.ENGLISH) { setOf(bold, regular) }
        val actual = options.filterIsInstance<SystemFontFile>()
        assertEquals(listOf(LyricFontFamily.SYSTEM_DEFAULT, actual.single()), options)
        assertEquals(fontFile().absolutePath, actual.single().path)
        assertEquals("Roboto", actual.single().familyName)
        assertEquals(0, actual.single().ttcIndex)
        assertEquals(400, actual.single().weight)
        assertFalse(actual.single().italic)
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun discovery_withNoFontFilesKeepsSystemDefault() {
        assertEquals(listOf(LyricFontFamily.SYSTEM_DEFAULT),
            SystemFontCatalog.availableFonts(Locale.ENGLISH) { emptySet() })
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun discovery_ignoresAFontWhoseBackingFileWasRemoved() {
        val font = fixtureFont()
        assertTrue(fontFile().delete())
        assertEquals(listOf(LyricFontFamily.SYSTEM_DEFAULT),
            SystemFontCatalog.availableFonts(Locale.ENGLISH) { setOf(font) })
    }

    @Test
    fun languageTags_matchWritingScriptsAndRespectRegionalChineseAndSerbianScripts() {
        assertTrue(SystemFontCatalog.matchesLanguage("und-Latn", Locale.GERMAN))
        assertTrue(SystemFontCatalog.matchesLanguage("und-Arab", Locale.forLanguageTag("ar")))
        assertFalse(SystemFontCatalog.matchesLanguage("und-Arab", Locale.ENGLISH))
        assertFalse(SystemFontCatalog.matchesLanguage("und-Hebr", Locale.ENGLISH))
        assertFalse(SystemFontCatalog.matchesLanguage("und-Xsux", Locale.ENGLISH))
        assertFalse(SystemFontCatalog.matchesLanguage("und-Zsye", Locale.ENGLISH))
        assertFalse(SystemFontCatalog.matchesLanguage("und-Zsym", Locale.ENGLISH))
        assertTrue(SystemFontCatalog.matchesLanguage("zh-Hant", Locale.forLanguageTag("zh-TW")))
        assertFalse(SystemFontCatalog.matchesLanguage("zh-Hans", Locale.forLanguageTag("zh-TW")))
        assertTrue(SystemFontCatalog.matchesLanguage("und-Latn", Locale.forLanguageTag("sr-Latn")))
        assertFalse(SystemFontCatalog.matchesLanguage("und-Latn", Locale.forLanguageTag("sr-Cyrl")))
        assertTrue(SystemFontCatalog.matchesLanguage("und-Hani", Locale.JAPANESE))
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun discovery_rejectsFacesWithoutGlyphsForTheRequestedLanguage() {
        val latin = fixtureFont()
        for (language in listOf("ar", "he")) {
            assertEquals(
                listOf(LyricFontFamily.SYSTEM_DEFAULT),
                SystemFontCatalog.availableFonts(Locale.forLanguageTag(language)) { setOf(latin) },
            )
        }
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun fixtureFace_loadsForEditorAndPreviewWithSavedStyle() {
        val font = fixtureOptions().filterIsInstance<SystemFontFile>().single()
        val typeface = SystemFontCatalog.typeface(font)
        assertNotNull(typeface)
        assertEquals(font.weight, typeface!!.weight)
        assertEquals(font.italic, typeface.isItalic)
        assertNotEquals(FontFamily.Monospace, LyricTextSettings(fontFamily = font).toTextStyle(TextStyle()).fontFamily)
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun legacySavedBoldFace_hasTheDiscoveredFamilyLabelWithoutChangingItsStyle() {
        val options = fixtureOptions()
        val regular = options.filterIsInstance<SystemFontFile>().first()
        val legacy = regular.copy(familyName = "", weight = 700)
        assertEquals(regular.label, SystemFontCatalog.selectionLabel(legacy, options))
        assertEquals(700, legacy.weight)
        assertEquals("", legacy.familyName)
    }

    @Test
    fun missingSavedFile_fallsBackToMonospace() {
        val settings = LyricTextSettings(fontFamily = SystemFontFile(File(folder.root, "missing.ttf").absolutePath))
        assertEquals(FontFamily.Monospace, settings.toTextStyle(TextStyle()).fontFamily)
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun malformedVariation_fallsBackAndDoesNotPoisonTheNextValidFace() {
        val valid = fixtureOptions().filterIsInstance<SystemFontFile>().single()
        val malformed = valid.copy(variationSettings = "'wght' not-a-number")
        assertNull(SystemFontCatalog.typeface(malformed))
        assertNull(SystemFontCatalog.typeface(malformed))
        assertEquals(FontFamily.Monospace, LyricTextSettings(fontFamily = malformed).toTextStyle(TextStyle()).fontFamily)
        assertNotNull(SystemFontCatalog.typeface(valid))
    }

    @Test
    @SdkSuppress(maxSdkVersion = 28)
    fun olderAndroid_keepsOnlySystemDefaultAndStillSupportsSavedGenericFamilies() {
        assertEquals(listOf(LyricFontFamily.SYSTEM_DEFAULT), SystemFontCatalog.availableFonts(Locale.ENGLISH))
        assertNull(SystemFontCatalog.typeface(SystemFontFile(fontFile().absolutePath)))
    }
}
