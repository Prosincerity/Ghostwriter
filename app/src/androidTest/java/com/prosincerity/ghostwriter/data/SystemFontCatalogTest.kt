package com.prosincerity.ghostwriter.data

import android.graphics.fonts.FontStyle
import android.graphics.fonts.Font
import android.graphics.fonts.FontVariationAxis
import android.graphics.fonts.SystemFonts
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SdkSuppress
import com.prosincerity.ghostwriter.ui.components.toTextStyle
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertSame
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertFalse
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.ByteBuffer
import java.util.Locale

@RunWith(AndroidJUnit4::class)
class SystemFontCatalogTest {
    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun discoveryFailure_keepsOnlySystemDefault() {
        assertEquals(listOf(LyricFontFamily.SYSTEM_DEFAULT), SystemFontCatalog.availableFonts {
            throw IllegalStateException("System font discovery unavailable")
        })
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun memoryOnlyFont_isExcludedBecauseItHasNoPersistableFilePath() {
        val saved = SystemFontCatalog.availableFonts().filterIsInstance<SystemFontFile>().first()
        val bytes = java.io.File(saved.path).readBytes()
        val buffer = ByteBuffer.allocateDirect(bytes.size).apply { put(bytes); flip() }
        val font = Font.Builder(buffer).setTtcIndex(saved.ttcIndex).build()
        assertNull(font.file)
        assertEquals(listOf(LyricFontFamily.SYSTEM_DEFAULT), SystemFontCatalog.availableFonts { setOf(font) })
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun discovery_groupsInstalledFamiliesAndRetainsRepresentativeFaceMetadata() {
        val expected = SystemFonts.getAvailableFonts().mapNotNull { font ->
            val file = font.file?.takeIf { it.isFile && it.canRead() } ?: return@mapNotNull null
            SystemFontFile(
                file.absolutePath,
                font.ttcIndex,
                font.style.weight,
                font.style.slant == FontStyle.FONT_SLANT_ITALIC,
                FontVariationAxis.toFontVariationSettings(font.axes),
            )
        }.toSet()
        val options = SystemFontCatalog.availableFonts(Locale.ENGLISH)
        val actual = options.filterIsInstance<SystemFontFile>()
        assertEquals(LyricFontFamily.SYSTEM_DEFAULT, options.first())
        assertEquals(1, options.filterIsInstance<LyricFontFamily>().size)
        assertTrue(actual.isNotEmpty())
        assertTrue(actual.all { it.copy(familyName = "") in expected })
        assertEquals(actual.size, actual.map { it.label.lowercase(Locale.ROOT) }.distinct().size)
        assertTrue(actual.all { it.familyName.isNotBlank() })
        assertTrue(actual.none { it.label.contains("Arabic") || it.label.contains("Hebrew") ||
            it.label.contains("Emoji") || it.label.contains("Cuneiform") || it.label.endsWith(" UI") })
        android.util.Log.i("FontCatalogTest", "English families: ${actual.map { it.label }}")
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
    fun discovery_includesArabicAndHebrewOnlyForTheirLanguages() {
        val arabic = SystemFontCatalog.availableFonts(Locale.forLanguageTag("ar"))
            .filterIsInstance<SystemFontFile>()
        val hebrew = SystemFontCatalog.availableFonts(Locale.forLanguageTag("he"))
            .filterIsInstance<SystemFontFile>()
        assertTrue(arabic.any { it.label.contains("Arabic") })
        assertTrue(hebrew.any { it.label.contains("Hebrew") })
        assertFalse(arabic.any { it.label.contains("Hebrew") || it.label == "Droid Sans Mono" })
        assertFalse(hebrew.any { it.label.contains("Arabic") })
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun discoveredFace_loadsForEditorAndPreviewAndIsCached() {
        val font = SystemFontCatalog.availableFonts().filterIsInstance<SystemFontFile>().first()
        val typeface = SystemFontCatalog.typeface(font)
        assertNotNull(typeface)
        assertSame(typeface, SystemFontCatalog.typeface(font))
        assertEquals(font.weight, typeface!!.weight)
        assertEquals(font.italic, typeface.isItalic)
        assertNotEquals(FontFamily.Monospace, LyricTextSettings(fontFamily = font).toTextStyle(TextStyle()).fontFamily)
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun legacySavedBoldFace_hasTheDiscoveredFamilyLabelWithoutChangingItsStyle() {
        val options = SystemFontCatalog.availableFonts()
        val regular = options.filterIsInstance<SystemFontFile>().first()
        val legacy = regular.copy(familyName = "", weight = 700)
        assertEquals(regular.label, SystemFontCatalog.selectionLabel(legacy, options))
        assertEquals(700, legacy.weight)
        assertEquals("", legacy.familyName)
    }

    @Test
    fun missingSavedFile_fallsBackToMonospace() {
        val settings = LyricTextSettings(fontFamily = SystemFontFile("/missing/device-font.ttf"))
        assertEquals(FontFamily.Monospace, settings.toTextStyle(TextStyle()).fontFamily)
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun malformedVariation_fallsBackAndDoesNotPoisonTheNextValidFace() {
        val valid = SystemFontCatalog.availableFonts().filterIsInstance<SystemFontFile>().first()
        val malformed = valid.copy(variationSettings = "'wght' not-a-number")
        assertNull(SystemFontCatalog.typeface(malformed))
        assertNull(SystemFontCatalog.typeface(malformed))
        assertEquals(FontFamily.Monospace, LyricTextSettings(fontFamily = malformed).toTextStyle(TextStyle()).fontFamily)
        assertNotNull(SystemFontCatalog.typeface(valid))
    }

    @Test
    @SdkSuppress(maxSdkVersion = 28)
    fun olderAndroid_keepsOnlySystemDefaultAndStillSupportsSavedGenericFamilies() {
        assertEquals(listOf(LyricFontFamily.SYSTEM_DEFAULT), SystemFontCatalog.availableFonts())
        assertNull(SystemFontCatalog.typeface(SystemFontFile("/system/fonts/Roboto-Regular.ttf")))
    }
}
