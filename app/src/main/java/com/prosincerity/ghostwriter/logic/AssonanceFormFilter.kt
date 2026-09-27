package com.prosincerity.ghostwriter.logic

import java.util.Locale

/** Keeps one returned form per compatible spelling-prefix family. */
internal object AssonanceFormFilter {
    data class Entry(val word: String, val ipa: String)

    fun excludedWords(results: List<Entry>, language: String): Set<String> {
        if (language != "de" && language != "en") return emptySet()
        val locale = Locale.forLanguageTag(language)
        val pronounced = results.mapIndexedNotNull { index, entry ->
            val keys = IpaSearchKeys.fromIpa(entry.ipa, language) ?: return@mapIndexedNotNull null
            val spelling = entry.word.lowercase(locale)
            Pronounced(entry, spelling, keys.assonance, IpaSearchKeys.phonemeTokens(keys), index)
        }
        val byIndex = pronounced.associateBy { it.index }
        val root = PrefixNode()
        pronounced.forEach { candidate ->
            var node = root
            candidate.spelling.forEach { letter ->
                node = node.children.getOrPut(letter) { PrefixNode() }
            }
            node.entries += candidate.index
        }
        val parents = IntArray(results.size) { it }
        val exactExtensions = IntArray(results.size)
        fun representative(index: Int): Int {
            var current = index
            while (parents[current] != current) {
                parents[current] = parents[parents[current]]
                current = parents[current]
            }
            return current
        }
        for (candidate in pronounced) {
            var node = root
            for (letter in candidate.spelling) {
                node = node.children.getValue(letter)
                for (baseIndex in node.entries) {
                    if (baseIndex == candidate.index) continue
                    val base = byIndex.getValue(baseIndex)
                    val shortInfinitive = language == "de" &&
                        (candidate.spelling == "tun" || candidate.spelling == "sein") &&
                        base.spelling == candidate.spelling.dropLast(1)
                    if ((base.spelling.length < 4 && !shortInfinitive) ||
                        base.spelling.length == candidate.spelling.length ||
                        !compatible(base, candidate, language)) continue
                    parents[representative(candidate.index)] = representative(base.index)
                    if (candidate.phonemes.size > base.phonemes.size &&
                        candidate.phonemes.take(base.phonemes.size) == base.phonemes) {
                        exactExtensions[base.index]++
                    }
                }
            }
        }
        val families = pronounced.groupBy { representative(it.index) }
        val excluded = mutableSetOf<String>()
        for (family in families.values) {
            val spellings = family.mapTo(mutableSetOf()) { it.spelling }
            // A returned German stem plus -en or -n identifies an infinitive among the forms.
            val germanInfinitives = if (language == "de") family.filter {
                val stem = when {
                    it.spelling.endsWith("en") -> it.spelling.dropLast(2)
                    it.spelling.endsWith("n") -> it.spelling.dropLast(1)
                    else -> null
                }
                stem != null && stem in spellings
            } else emptyList()
            // Prefer the form that actually starts the most returned pronunciations.
            // A spelling-only shortest form can end in a different sound (erblond/erblonden).
            val hasExactExtensions = family.any { exactExtensions[it.index] > 0 }
            val kept = if (germanInfinitives.isNotEmpty()) {
                germanInfinitives.minWith(compareBy<Pronounced> { it.spelling.length }.thenBy { it.index })
            } else if (hasExactExtensions) {
                family.maxWith(compareBy<Pronounced> { exactExtensions[it.index] }
                    .thenBy { it.spelling.length }.thenByDescending { it.index })
            } else {
                family.minWith(compareBy<Pronounced> { it.spelling.length }.thenBy { it.index })
            }
            family.filter { it !== kept }.forEach { excluded += it.entry.word }
        }
        return excluded
    }

    private class PrefixNode {
        val children = mutableMapOf<Char, PrefixNode>()
        val entries = mutableListOf<Int>()
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
