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
import org.junit.Test
import org.junit.runner.RunWith
import java.nio.ByteBuffer

@RunWith(AndroidJUnit4::class)
class SystemFontCatalogTest {
    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun discoveryFailure_keepsAllGenericFontChoices() {
        assertEquals(LyricFontFamily.entries, SystemFontCatalog.availableFonts {
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
        assertEquals(LyricFontFamily.entries, SystemFontCatalog.availableFonts { setOf(font) })
    }

    @Test
    @SdkSuppress(minSdkVersion = 29)
    fun discovery_reflectsReadableDeviceFontsAndRetainsFaceMetadata() {
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
        val actual = SystemFontCatalog.availableFonts().filterIsInstance<SystemFontFile>()
        assertEquals(expected, actual.toSet())
        assertEquals(expected.size, actual.size)
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
    fun olderAndroid_keepsGenericFamilies() {
        assertEquals(LyricFontFamily.entries, SystemFontCatalog.availableFonts())
        assertNull(SystemFontCatalog.typeface(SystemFontFile("/system/fonts/Roboto-Regular.ttf")))
    }
}
