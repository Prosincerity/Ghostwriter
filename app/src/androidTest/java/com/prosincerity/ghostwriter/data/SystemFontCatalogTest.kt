package com.prosincerity.ghostwriter.data

import android.graphics.fonts.FontStyle
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
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SystemFontCatalogTest {
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
                FontVariationAxis.toFontVariationSettings(font.axes) ?: "",
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
    @SdkSuppress(maxSdkVersion = 28)
    fun olderAndroid_keepsGenericFamilies() {
        assertEquals(LyricFontFamily.entries, SystemFontCatalog.availableFonts())
    }
}
