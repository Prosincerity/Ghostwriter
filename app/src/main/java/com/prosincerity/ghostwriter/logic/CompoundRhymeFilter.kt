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
        val seen = mutableMapOf<String, MutableList<Pronounced>>()
        query.mapNotNull(::pronounced).forEach { seen.getOrPut(it.spelling) { mutableListOf() } += it }
        val excluded = mutableSetOf<String>()
        results.mapNotNull(::pronounced).forEach { candidate ->
            val repeatedEnding = (1 until candidate.spelling.length).any { start ->
                seen[candidate.spelling.substring(start)].orEmpty().any base@{ base ->
                    val ending = base.rime ?: return@base false
                    candidate.phonemes.size > ending.size && candidate.phonemes.takeLast(ending.size) == ending &&
                        IpaSearchKeys.hasVowel(candidate.phonemes.dropLast(ending.size), language)
                }
            }
            if (repeatedEnding) excluded += candidate.entry.word
            seen.getOrPut(candidate.spelling) { mutableListOf() } += candidate
        }
        return excluded
    }

    private data class Pronounced(
        val entry: Entry,
        val spelling: String,
        val phonemes: List<String>,
        val rime: List<String>?,
    )
}
