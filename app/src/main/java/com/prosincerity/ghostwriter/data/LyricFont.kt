package com.prosincerity.ghostwriter.data

import org.json.JSONObject
import java.io.File

sealed interface LyricFont {
    val label: String
}

/** Identifies a device font face, including collection and variable-font settings. */
data class SystemFontFile(
    val path: String,
    val ttcIndex: Int = 0,
    val weight: Int = 400,
    val italic: Boolean = false,
    val variationSettings: String = "",
) : LyricFont {
    override val label: String
        get() = buildString {
            append(File(path).nameWithoutExtension)
            append(" · $weight")
            if (italic) append(" Italic")
            if (ttcIndex > 0) append(" · face ${ttcIndex + 1}")
            if (variationSettings.isNotEmpty()) append(" · $variationSettings")
        }
}

internal fun LyricFont.toPreferenceValue(): String = when (this) {
    is LyricFontFamily -> name
    is SystemFontFile -> JSONObject()
        .put("path", path)
        .put("ttcIndex", ttcIndex)
        .put("weight", weight)
        .put("italic", italic)
        .put("variationSettings", variationSettings)
        .toString()
}

internal fun lyricFontFromPreference(value: String?): LyricFont {
    LyricFontFamily.entries.firstOrNull { it.name == value }?.let { return it }
    if (value == null) return LyricFontFamily.MONOSPACE
    return try {
        val json = JSONObject(value)
        SystemFontFile(
            path = json.getString("path"),
            ttcIndex = json.getInt("ttcIndex"),
            weight = json.getInt("weight"),
            italic = json.getBoolean("italic"),
            variationSettings = json.getString("variationSettings"),
        ).takeIf { it.path.isNotBlank() && it.ttcIndex >= 0 && it.weight in 1..1000 }
            ?: LyricFontFamily.MONOSPACE
    } catch (_: Exception) {
        LyricFontFamily.MONOSPACE
    }
}

internal fun systemFontOptions(fonts: Iterable<SystemFontFile>): List<LyricFont> =
    LyricFontFamily.entries + fonts.distinct().sortedWith(
        compareBy<SystemFontFile, String>(String.CASE_INSENSITIVE_ORDER) { it.label }
            .thenBy { it.path },
    )
