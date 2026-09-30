package com.prosincerity.ghostwriter.data

import com.prosincerity.ghostwriter.logic.IpaSearchKeys

/** Ordered phonetic search stages, independent of database access and pagination. */
internal data class DictionarySearchPlan(
    val prefixes: List<Prefix>,
    val assonanceFallbacks: List<String>,
) {
    data class Prefix(val value: String, val tokenCount: Int, val tokens: List<String>? = null)

    companion object {
        fun fromIpa(ipa: List<String>, language: String, mode: DictionarySearchMode): DictionarySearchPlan {
            val keys = ipa.mapNotNull { IpaSearchKeys.fromIpa(it, language) }
            val prefixes = when (mode) {
                DictionarySearchMode.RHYME -> keys.mapNotNull { key ->
                    val rime = IpaSearchKeys.rimeTokens(key, language) ?: return@mapNotNull null
                    Prefix(rime.asReversed().joinToString(""), rime.size, rime)
                }
                DictionarySearchMode.WORD_SUFFIX -> keys.flatMap { key ->
                    IpaSearchKeys.phonemeSuffixes(key).map { suffix ->
                        Prefix(suffix.asReversed().joinToString(""), suffix.size, suffix)
                    }
                }
                DictionarySearchMode.ASSONANCE -> keys.filter { it.assonance.isNotEmpty() }
                    .map { key -> Prefix(key.assonance, key.assonance.split(' ').size) }
                DictionarySearchMode.WORD_PREFIX -> emptyList()
            }
            // Short exact vowel keys come first; rhyme and suffix searches prefer
            // longer overlaps. Keep token boundaries when deduplicating phonemes.
            val order = if (mode == DictionarySearchMode.ASSONANCE) {
                compareBy<Prefix> { it.tokenCount }.thenBy { it.value.length }
            } else {
                compareByDescending<Prefix> { it.tokenCount }.thenByDescending { it.value.length }
            }
            val fallbacks = if (mode == DictionarySearchMode.ASSONANCE) {
                keys.flatMap(IpaSearchKeys::assonanceFallbacks).distinct()
                    .sortedByDescending { it.split(' ').size }
            } else emptyList()
            return DictionarySearchPlan(
                prefixes.sortedWith(order).distinctBy { it.tokens ?: it.value },
                fallbacks,
            )
        }
    }
}
