package com.prosincerity.ghostwriter.data

import android.content.res.Resources
import android.graphics.Typeface
import android.graphics.fonts.Font
import android.graphics.fonts.FontStyle
import android.graphics.fonts.FontVariationAxis
import android.graphics.fonts.SystemFonts
import android.icu.lang.UScript
import android.icu.util.ULocale
import android.os.Build
import java.io.File
import java.util.Locale

/** Uses public Android APIs only. Call discovery off the UI thread. */
internal object SystemFontCatalog {
    fun availableFonts(
        locale: Locale = Resources.getSystem().configuration.locales[0],
        loadFonts: (() -> Set<Font>)? = null,
    ): List<LyricFont> {
        val defaultOnly = listOf(LyricFontFamily.SYSTEM_DEFAULT)
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.Q) return defaultOnly
        return try {
            val samples = languageSamples(locale)
            val metadata = mutableMapOf<Pair<String, Int>, Pair<String, Boolean>?>()
            systemFontOptions((loadFonts?.invoke() ?: SystemFonts.getAvailableFonts()).mapNotNull { font ->
                val file = font.file?.takeIf { it.isFile && it.canRead() } ?: return@mapNotNull null
                if (!matchesLanguage(font.localeList.toLanguageTags(), locale)) return@mapNotNull null
                val details = metadata.getOrPut(file.absolutePath to font.ttcIndex) {
                    val reader = OpenTypeFontMetadata(font.buffer, font.ttcIndex)
                    reader.familyName()?.let { it to reader.covers(samples) }
                } ?: return@mapNotNull null
                if (!details.second) return@mapNotNull null
                SystemFontFile(
                    path = file.absolutePath,
                    ttcIndex = font.ttcIndex,
                    weight = font.style.weight,
                    italic = font.style.slant == FontStyle.FONT_SLANT_ITALIC,
                    variationSettings = FontVariationAxis.toFontVariationSettings(font.axes),
                    familyName = details.first,
                )
            })
        } catch (_: RuntimeException) {
            defaultOnly
        }
    }

    internal fun matchesLanguage(languageTags: String, locale: Locale): Boolean {
        if (languageTags.isBlank()) return true // General fonts still require direct glyph coverage.
        val target = ULocale.addLikelySubtags(ULocale.forLocale(locale))
        val targetScripts = UScript.getCode(locale)?.toSet().orEmpty()
        return languageTags.split(',').any { tag ->
            val fontLocale = Locale.forLanguageTag(tag)
            val script = ULocale.addLikelySubtags(ULocale.forLocale(fontLocale)).script
            script == target.script ||
                (fontLocale.language.isEmpty() &&
                    UScript.getCode(fontLocale)?.any { it in targetScripts } == true)
        }
    }

    private fun languageSamples(locale: Locale): List<Int> {
        // ICU supplies representative characters for each writing script, including
        // all three scripts used by Japanese. No language/font filename allowlist.
        val samples = mutableListOf<Int>()
        for (script in UScript.getCode(locale) ?: intArrayOf()) {
            val sample = UScript.getSampleString(script)
            var index = 0
            while (index < sample.length) {
                val cp = sample.codePointAt(index)
                samples += cp
                index += Character.charCount(cp)
            }
        }
        return samples
    }

    fun selectionLabel(font: LyricFont, options: List<LyricFont>): String {
        if (font !is SystemFontFile) return font.label // Keep existing generic preferences readable.
        if (!File(font.path).isFile) return "${font.label} (unavailable)"
        val discovered = options.filterIsInstance<SystemFontFile>().firstOrNull {
            (it.path == font.path && it.ttcIndex == font.ttcIndex) || it.label == font.label
        }
        return discovered?.label ?: font.label
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
