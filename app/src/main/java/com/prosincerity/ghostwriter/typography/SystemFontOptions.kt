package com.prosincerity.ghostwriter.typography

import java.util.Locale
import kotlin.math.abs

private val fontWidthPattern = Regex("'wdth'\\s+([\\d.]+)")

private val representativeFaceOrder = compareBy<SystemFontFile> { it.familyName.trim().endsWith(" UI") }
    .thenBy { it.italic }
    .thenBy { abs(it.weight - 400) }
    .thenBy { it.widthDistanceFromNormal() }
    .thenBy { it.path }
    .thenBy { it.ttcIndex }
    .thenBy { it.variationSettings }

private val fontFamilyOrder = compareBy<SystemFontFile, String>(String.CASE_INSENSITIVE_ORDER) { it.label }
    .thenBy { it.path }

/** One representative face per family, with System default pinned above the sorted families. */
internal fun systemFontOptions(fonts: Iterable<SystemFontFile>): List<LyricFont> =
    listOf(LyricFontFamily.SYSTEM_DEFAULT) + fonts
        .groupBy { it.label.lowercase(Locale.ROOT) }
        .values.map { family -> family.minWith(representativeFaceOrder) }
        .sortedWith(fontFamilyOrder)

/** Label a saved selection without requiring Android font discovery or filesystem access. */
internal fun systemFontSelectionLabel(
    font: LyricFont,
    options: List<LyricFont>,
    isFileAvailable: (String) -> Boolean,
): String {
    if (font !is SystemFontFile) return font.label
    if (!isFileAvailable(font.path)) return "${font.label} (unavailable)"
    val discovered = options.filterIsInstance<SystemFontFile>().firstOrNull {
        (it.path == font.path && it.ttcIndex == font.ttcIndex) || it.label == font.label
    }
    return discovered?.label ?: font.label
}

private fun SystemFontFile.widthDistanceFromNormal(): Float {
    val width = fontWidthPattern.find(variationSettings)?.groupValues?.get(1)?.toFloatOrNull() ?: 100f
    return abs(width - 100f)
}
