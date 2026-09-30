package com.prosincerity.ghostwriter.ui.components

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.prosincerity.ghostwriter.data.LyricFontFamily
import com.prosincerity.ghostwriter.data.LyricTextAlignment
import com.prosincerity.ghostwriter.data.LyricTextSettings
import com.prosincerity.ghostwriter.data.SystemFontCatalog
import com.prosincerity.ghostwriter.data.SystemFontFile

internal fun LyricTextSettings.toTextStyle(
    base: TextStyle,
    resolveSystemFont: (SystemFontFile) -> FontFamily? = { font ->
        SystemFontCatalog.typeface(font)?.let { FontFamily(it) }
    },
): TextStyle {
    val settings = normalized()
    return base.copy(
        fontFamily = when (val font = settings.fontFamily) {
            is SystemFontFile -> resolveSystemFont(font) ?: FontFamily.Monospace
            is LyricFontFamily -> when (font) {
                LyricFontFamily.SYSTEM_DEFAULT -> FontFamily.Default
                LyricFontFamily.SANS_SERIF -> FontFamily.SansSerif
                LyricFontFamily.SERIF -> FontFamily.Serif
                LyricFontFamily.MONOSPACE -> FontFamily.Monospace
                LyricFontFamily.CURSIVE -> FontFamily.Cursive
            }
        },
        fontSize = settings.fontSizeSp.sp,
        // Relative line height follows the scaled font, including Android's nonlinear scaling.
        lineHeight = settings.lineHeightMultiplier.em,
        letterSpacing = settings.letterSpacingSp.sp,
        textAlign = when (settings.alignment) {
            LyricTextAlignment.START -> TextAlign.Start
            LyricTextAlignment.CENTER -> TextAlign.Center
            LyricTextAlignment.END -> TextAlign.End
            LyricTextAlignment.LEFT -> TextAlign.Left
            LyricTextAlignment.RIGHT -> TextAlign.Right
            LyricTextAlignment.JUSTIFY -> TextAlign.Justify
        },
    )
}
