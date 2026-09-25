package com.prosincerity.ghostwriter.logic

import java.util.Locale

/** Keeps the shortest returned spelling among forms with a shared inflection stem and vowel key. */
internal object AssonanceFormFilter {
    data class Entry(val word: String, val ipa: String)

    private val germanEndings = setOf(
        "e", "en", "em", "er", "es", "n", "s", "t", "st", "est", "et",
        "te", "ten", "tem", "ter", "tes", "test", "tet",
        "ete", "eten", "etem", "eter", "etes", "etest", "etet",
        "end", "ende", "enden", "endem", "ender", "endes",
        "nd", "nde", "nden", "ndem", "nder", "ndes",
        "de", "den", "dem", "der", "des",
        "ste", "sten", "stem", "ster", "stes",
    )
    private val englishEndings = setOf("s", "es", "d", "ed", "ing", "er", "est")

    fun excludedWords(results: List<Entry>, language: String): Set<String> {
        val endings = when (language) {
            "de" -> germanEndings
            "en" -> englishEndings
            else -> return emptySet()
        }
        val locale = Locale.forLanguageTag(language)
        val pronounced = results.mapIndexedNotNull { index, entry ->
            val keys = IpaSearchKeys.fromIpa(entry.ipa, language) ?: return@mapIndexedNotNull null
            val spelling = entry.word.lowercase(locale)
            Pronounced(entry, spelling, keys.assonance, IpaSearchKeys.phonemeTokens(keys), index)
        }.sortedWith(compareBy<Pronounced> { it.spelling.length }.thenBy { it.index })
        val kept = mutableMapOf<String, MutableList<Pronounced>>()
        val excluded = mutableSetOf<String>()
        for (candidate in pronounced) {
            val inflected = endings.any { ending ->
                val spelling = candidate.spelling
                spelling.endsWith(ending) && spelling.length - ending.length >= 4 &&
                    kept[spelling.dropLast(ending.length)].orEmpty()
                        .any { base -> compatible(base, candidate, language) }
            }
            if (inflected) excluded += candidate.entry.word
            else kept.getOrPut(candidate.spelling) { mutableListOf() } += candidate
        }
        return excluded
    }

    private fun compatible(base: Pronounced, candidate: Pronounced, language: String): Boolean {
        if (base.assonance == candidate.assonance) return true
        if (language == "en") {
            return candidate.phonemes.size > base.phonemes.size &&
                candidate.phonemes.take(base.phonemes.size) == base.phonemes
        }
        if (base.assonance.isEmpty() || candidate.assonance.isEmpty()) return false
        if (base.assonance != "ə ${candidate.assonance}" &&
            candidate.assonance != "ə ${base.assonance}") return false
        val root = if (base.phonemes.lastOrNull()?.startsWith("ə") == true) {
            base.phonemes.dropLast(1)
        } else base.phonemes
        if (root.isEmpty() || candidate.phonemes.size <= root.size) return false
        if (candidate.phonemes.take(root.size) == root) return true
        return root.dropLast(1) == candidate.phonemes.take(root.size - 1) &&
            when (root.last() to candidate.phonemes[root.lastIndex]) {
                "k" to "ɡ", "t" to "d", "p" to "b" -> true
                else -> false
            }
    }

    private data class Pronounced(
        val entry: Entry,
        val spelling: String,
        val assonance: String,
        val phonemes: List<String>,
        val index: Int,
    )
}
