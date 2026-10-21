package com.prosincerity.ghostwriter.logic

import java.util.Locale

/** Dictionary-backed longest-head analysis. Surface parts always reconstruct the input. */
internal class GermanDecompounder(
    private val pronunciations: (String) -> List<String> = { emptyList() },
    private val contains: (String) -> Boolean,
) {
    data class Part(val surface: String, val lemma: String, val linking: String = "")

    fun decompound(input: String): List<Part> {
        val word = DictionaryHeadword.normalize(input)
        if (word.length > MAX_LENGTH || word.any { !it.isLetter() }) return listOf(Part(word, key(word)))
        val memo = mutableMapOf<Pair<String, Boolean>, List<Part>?>()
        fun split(text: String, modifier: Boolean): List<Part>? {
            val cacheKey = text to modifier
            if (memo.containsKey(cacheKey)) return memo[cacheKey]
            val atom = atom(text, modifier)
            if (atom == null || !isDerivation(atom.lemma)) {
                // Try the longest head first, but backtrack if the entire modifier cannot be analysed.
                for (boundary in 2..text.length - 2) {
                    val head = text.substring(boundary)
                    val headPart = atom(head, modifier) ?: continue
                    val left = split(text.substring(0, boundary), true) ?: continue
                    val result = left + headPart
                    memo[cacheKey] = result
                    return result
                }
            }
            val result = atom?.let(::listOf)
            memo[cacheKey] = result
            return result
        }
        return split(word, false) ?: listOf(Part(word, key(word)))
    }

    private fun known(word: String): Boolean = contains(key(word))

    private fun atom(text: String, modifier: Boolean): Part? {
        val normalized = key(text)
        // Keep the linking material on the modifier, while retaining its dictionary lemma.
        for (link in if (modifier) LINKS else emptyList()) {
            val base = normalized.removeSuffix(link)
            if (base.length >= 2 && base != normalized && known(base)) return Part(text, base, link)
        }
        if (known(text)) return Part(text, normalized)
        // A bound compound lexeme is licensed by its independently attested source verb.
        if (BOUND_HEADS[normalized]?.let(::known) == true) return Part(text, normalized)
        // Schulfest, Blumengarten: productive loss of a lemma's final -e.
        if (modifier && normalized.length >= 3 && known(normalized + "e")) return Part(text, normalized + "e")
        return null
    }

    private fun isDerivation(word: String): Boolean {
        if (!known(word)) return false
        for (suffix in DERIVATIONAL_SUFFIXES) {
            if (!word.endsWith(suffix)) continue
            val stem = word.removeSuffix(suffix)
            if (stem.length < 3 || stemForms(stem).none(::known)) continue
            // Schaft is also a noun: an independently stressed head licenses Gewehr|schaft.
            val headRimes = pronunciations(suffix).mapNotNull { ipa ->
                IpaSearchKeys.fromIpa(ipa, "de")?.let { IpaSearchKeys.rimeTokens(it, "de") }
            }.map(IpaSearchKeys::phonemeTokens)
            val independentHead = pronunciations(word).any { ipa ->
                val keys = IpaSearchKeys.fromIpa(ipa, "de") ?: return@any false
                val stress = keys.tokens.indexOfLast { it == "ˈ" || it == "ˌ" }
                if (stress < 0) return@any false
                IpaSearchKeys.rimeTokens(keys.copy(tokens = keys.tokens.drop(stress)), "de")
                    ?.let(IpaSearchKeys::phonemeTokens) in headRimes
            }
            if (!independentHead) return true
        }
        return false
    }

    companion object {
        const val MAX_LENGTH = 96
        private val LINKS = listOf("ens", "ns", "es", "en", "er", "s", "n", "e")
        // Productive morphemes, not word exceptions. Sources and limits: DICTIONARIES.md.
        private val DERIVATIONAL_SUFFIXES = listOf("schaft", "heit", "keit", "ung", "nis", "tum", "ling")
        private val BOUND_HEADS = mapOf("nahme" to "nehmen")
        private fun stemForms(stem: String): List<String> =
            listOf(stem, stem + "e", stem + "en", stem + "n", stem.removeSuffix("l") + "eln")
        fun key(word: String): String = DictionaryHeadword.normalize(word).lowercase(Locale.GERMAN)

        /** Possible dictionary atoms, for batched indexed lookups rather than a full lexicon scan. */
        fun vocabulary(word: String): Set<String> = buildSet {
            if (word.length > MAX_LENGTH) return@buildSet
            addAll(BOUND_HEADS.values)
            for (start in word.indices) for (end in start + 2..word.length) {
                val atom = key(word.substring(start, end))
                add(atom)
                add(atom + "e")
                for (suffix in DERIVATIONAL_SUFFIXES) {
                    if (atom.endsWith(suffix)) addAll(stemForms(atom.removeSuffix(suffix)))
                }
            }
        }
    }
}
