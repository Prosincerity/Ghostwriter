package com.prosincerity.ghostwriter.data

import org.json.JSONObject
import java.io.File
import java.util.Locale
import kotlin.math.abs

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
    val familyName: String = "",
) : LyricFont {
    override val label: String
        get() = cleanFontFamilyName(familyName.ifBlank {
            // Only for legacy saved selections. Discovery reads the OpenType name table.
            File(path).nameWithoutExtension.substringBefore('-')
                .replace(Regex("([a-z])([A-Z])"), "$1 $2")
        })
}

internal fun cleanFontFamilyName(name: String): String =
    name.trim().replace(Regex("\\s+"), " ").removeSuffix(" UI")

internal fun LyricFont.toPreferenceValue(): String = when (this) {
    is LyricFontFamily -> name
    is SystemFontFile -> JSONObject()
        .put("path", path)
        .put("ttcIndex", ttcIndex)
        .put("weight", weight)
        .put("italic", italic)
        .put("variationSettings", variationSettings)
        .put("familyName", familyName)
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
            familyName = json.optString("familyName"),
        ).takeIf { it.path.isNotBlank() && it.ttcIndex >= 0 && it.weight in 1..1000 }
            ?: LyricFontFamily.MONOSPACE
    } catch (_: Exception) {
        LyricFontFamily.MONOSPACE
    }
}

internal fun systemFontOptions(fonts: Iterable<SystemFontFile>): List<LyricFont> =
    listOf(LyricFontFamily.SYSTEM_DEFAULT) + fonts
        .groupBy { it.label.lowercase(Locale.ROOT) }
        .values.map { family ->
            family.minWith(compareBy<SystemFontFile> { it.familyName.trim().endsWith(" UI") }
                .thenBy { it.italic }
                .thenBy { abs(it.weight - 400) }
                .thenBy { font ->
                    val width = Regex("'wdth'\\s+([\\d.]+)")
                        .find(font.variationSettings)?.groupValues?.get(1)?.toFloatOrNull() ?: 100f
                    abs(width - 100f)
                }
                .thenBy { it.path }.thenBy { it.ttcIndex }.thenBy { it.variationSettings })
        }.sortedWith(
            compareBy<SystemFontFile, String>(String.CASE_INSENSITIVE_ORDER) { it.label }
                .thenBy { it.path },
        )
