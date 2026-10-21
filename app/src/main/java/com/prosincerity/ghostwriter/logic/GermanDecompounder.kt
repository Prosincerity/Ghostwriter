package com.prosincerity.ghostwriter.logic

import java.util.Locale

/** Dictionary-backed longest-head analysis. Surface parts always reconstruct the input. */
internal class GermanDecompounder(private val contains: (String) -> Boolean) {
    data class Part(val surface: String, val lemma: String, val linking: String = "")

    fun decompound(input: String): List<Part> {
        val word = DictionaryHeadword.normalize(input)
        if (word.length > MAX_LENGTH || word.any { !it.isLetter() }) return listOf(Part(word, key(word)))
        val memo = mutableMapOf<String, List<Part>?>()
        fun split(text: String): List<Part>? {
            val normalized = key(text)
            if (memo.containsKey(text)) return memo[text]
            val atom = atom(text)
            if (atom?.lemma !in ATOMS) {
                // Try the longest head first, but backtrack if the entire modifier cannot be analysed.
                for (boundary in 2..text.length - 2) {
                    val head = text.substring(boundary)
                    val headPart = atom(head) ?: continue
                    val modifier = text.substring(0, boundary)
                    val left = split(modifier) ?: continue
                    val result = left + headPart
                    memo[text] = result
                    return result
                }
            }
            val result = atom?.let(::listOf)
            memo[text] = result
            return result
        }
        return split(word) ?: listOf(Part(word, key(word)))
    }

    private fun known(word: String): Boolean = key(word) in ATOMS || contains(key(word))

    private fun atom(text: String): Part? {
        val normalized = key(text)
        if (normalized in ATOMS) return Part(text, normalized)
        // Keep the linking material on the modifier, while retaining its dictionary lemma.
        for (link in LINKS) {
            val base = normalized.removeSuffix(link)
            if (base.length >= 2 && base != normalized && known(base)) return Part(text, base, link)
        }
        if (known(text)) return Part(text, normalized)
        // Schulfest, Blumengarten: productive loss of a lemma's final -e.
        if (normalized.length >= 3 && known(normalized + "e")) return Part(text, normalized + "e")
        return null
    }

    companion object {
        const val MAX_LENGTH = 96
        private val LINKS = listOf("ens", "ns", "es", "en", "er", "s", "n", "e")
        // Lexicalized/derived atoms cannot be inferred from the spelling-only release schema.
        private val ATOMS = setOf("handschuh", "hinterziehung", "wissenschaft", "anwaltschaft")
        fun key(word: String): String = DictionaryHeadword.normalize(word).lowercase(Locale.GERMAN)

        /** Possible dictionary atoms, for batched indexed lookups rather than a full lexicon scan. */
        fun vocabulary(word: String): Set<String> = buildSet {
            if (word.length > MAX_LENGTH) return@buildSet
            for (start in word.indices) for (end in start + 2..word.length) {
                val atom = key(word.substring(start, end))
                add(atom)
                add(atom + "e")
            }
        }
    }
}
