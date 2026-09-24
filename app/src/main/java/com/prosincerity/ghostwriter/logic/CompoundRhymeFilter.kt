package com.prosincerity.ghostwriter.logic

import java.util.Locale

/** Removes a compound from rhyme results when its standalone ending is also in the search. */
internal object CompoundRhymeFilter {
    data class Entry(val word: String, val ipa: String)

    fun excludedWords(results: List<Entry>, query: List<Entry>, language: String): Set<String> {
        val locale = Locale.forLanguageTag(language)
        fun pronounced(entry: Entry): Pronounced? {
            val keys = IpaSearchKeys.fromIpa(entry.ipa, language) ?: return null
            return Pronounced(entry, entry.word.lowercase(locale), IpaSearchKeys.phonemeTokens(keys),
                IpaSearchKeys.rimeTokens(keys, language)?.let(IpaSearchKeys::phonemeTokens))
        }
        val resultEntries = results.mapNotNull(::pronounced)
        val entries = resultEntries + query.mapNotNull(::pronounced)
        return resultEntries.filter { candidate ->
            entries.any { base ->
                val ending = base.rime ?: return@any false
                candidate.entry.word != base.entry.word && candidate.spelling.length > base.spelling.length &&
                    candidate.spelling.endsWith(base.spelling) &&
                    candidate.phonemes.size > ending.size && candidate.phonemes.takeLast(ending.size) == ending &&
                    IpaSearchKeys.hasVowel(candidate.phonemes.dropLast(ending.size), language)
            }
        }.mapTo(mutableSetOf()) { it.entry.word }
    }

    private data class Pronounced(
        val entry: Entry,
        val spelling: String,
        val phonemes: List<String>,
        val rime: List<String>?,
    )
}
