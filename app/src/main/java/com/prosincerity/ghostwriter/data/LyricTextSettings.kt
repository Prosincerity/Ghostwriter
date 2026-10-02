package com.prosincerity.ghostwriter.data

/** Generic platform families remain available on every supported Android version. */
enum class LyricFontFamily(override val label: String) : LyricFont {
    SYSTEM_DEFAULT("System default"),
    SANS_SERIF("Sans serif"),
    SERIF("Serif"),
    MONOSPACE("Monospace"),
    CURSIVE("Cursive"),
}

enum class LyricTextAlignment(val label: String) {
    START("Start"),
    CENTER("Center"),
    END("End"),
    LEFT("Left"),
    RIGHT("Right"),
    JUSTIFY("Justified"),
}

/** Global lyric-pad typography. Sizes are stored before Android's font scaling. */
data class LyricTextSettings(
    val fontFamily: LyricFont = LyricFontFamily.MONOSPACE,
    val fontSizeSp: Int = 16,
    val lineHeightMultiplier: Float = 1.5f,
    val letterSpacingSp: Float = 0f,
    val alignment: LyricTextAlignment = LyricTextAlignment.START,
) {
    fun normalized(): LyricTextSettings = copy(
        fontSizeSp = fontSizeSp.coerceIn(12, 32),
        lineHeightMultiplier = if (lineHeightMultiplier.isFinite()) {
            lineHeightMultiplier.coerceIn(LINE_HEIGHT_RANGE)
        } else 1.5f,
        letterSpacingSp = if (letterSpacingSp.isFinite()) {
            letterSpacingSp.coerceIn(LETTER_SPACING_RANGE)
        } else 0f,
    )

    companion object {
        val FONT_SIZE_OPTIONS = listOf(12, 14, 16, 18, 20, 22, 24, 28, 32)
        val LINE_HEIGHT_RANGE = 0.5f..4f
        val LETTER_SPACING_RANGE = -2f..10f
    }
}
