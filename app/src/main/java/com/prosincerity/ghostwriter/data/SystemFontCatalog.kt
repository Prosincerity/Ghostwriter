package com.prosincerity.ghostwriter.data

import android.graphics.Typeface
import android.graphics.fonts.Font
import android.graphics.fonts.FontStyle
import android.graphics.fonts.FontVariationAxis
import android.graphics.fonts.SystemFonts
import android.os.Build
import java.io.File

/** Uses public Android APIs only. Call discovery off the UI thread. */
internal object SystemFontCatalog {
    fun availableFonts(loadFonts: (() -> Set<Font>)? = null): List<LyricFont> {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return LyricFontFamily.entries
        return try {
            systemFontOptions((loadFonts?.invoke() ?: SystemFonts.getAvailableFonts()).mapNotNull { font ->
                val file = font.file?.takeIf { it.isFile && it.canRead() } ?: return@mapNotNull null
                SystemFontFile(
                    path = file.absolutePath,
                    ttcIndex = font.ttcIndex,
                    weight = font.style.weight,
                    italic = font.style.slant == FontStyle.FONT_SLANT_ITALIC,
                    variationSettings = FontVariationAxis.toFontVariationSettings(font.axes) ?: "",
                )
            })
        } catch (_: RuntimeException) {
            LyricFontFamily.entries
        }
    }

    // Keep only the current face cached; slider changes must not reload the font file.
    private var cachedFont: SystemFontFile? = null
    private var cachedTypeface: Typeface? = null

    @Synchronized
    fun typeface(font: SystemFontFile): Typeface? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return null
        if (cachedFont == font) return cachedTypeface
        cachedFont = font
        cachedTypeface = try {
            val file = File(font.path)
            if (file.isFile && file.canRead()) {
                Typeface.Builder(file)
                    .setTtcIndex(font.ttcIndex)
                    .setWeight(font.weight)
                    .setItalic(font.italic)
                    .setFontVariationSettings(font.variationSettings)
                    .build()
            } else null
        } catch (_: RuntimeException) {
            null
        }
        return cachedTypeface
    }
}
