package com.prosincerity.ghostwriter.logic

/** Two independent rimes; vowel agreement admits the loose rhymes used in rap. */
internal object DoubleRhymePhonetics {
    fun keys(ipa: String): IpaSearchKeys.Keys? = IpaSearchKeys.fromIpa(
        ipa.replace(Regex("([nlm])̩"), "ə$1").replace("̯", "").replace('ɑ', 'a'), "de",
    )

    /** 0: exact rime, 1: voicing difference, 2: vowel match, null: no match. */
    fun distance(input: List<String>, candidate: String): Int? {
        val other = keys(candidate) ?: return null
        return input.mapNotNull { ipa ->
            val query = keys(ipa) ?: return@mapNotNull null
            if (query.assonance != other.assonance) return@mapNotNull null
            val left = IpaSearchKeys.rimeTokens(query, "de")?.let(IpaSearchKeys::phonemeTokens)
            val right = IpaSearchKeys.rimeTokens(other, "de")?.let(IpaSearchKeys::phonemeTokens)
            when {
                left == null || right == null -> null
                left == right -> 0
                left.map(::devoice) == right.map(::devoice) -> 1
                else -> 2
            }
        }.minOrNull()
    }

    /** Stored indexes can omit syllabic schwas and mark diphthongs as nonsyllabic. */
    fun indexKeys(ipa: List<String>): Set<String> = buildSet {
        for (pronunciation in ipa) {
            val key = keys(pronunciation)?.assonance ?: continue
            var variants = listOf("")
            for (vowel in key.split(' ').filter(String::isNotEmpty)) {
                val choices = when {
                    vowel == "ə" -> listOf("ə", "")
                    vowel in setOf("aɪ", "aʊ", "ɔʏ", "oʏ") -> listOf(vowel, vowel + "̯")
                    vowel == "a" || vowel == "aː" -> listOf(vowel, vowel.replace('a', 'ɑ'))
                    else -> listOf(vowel)
                }
                variants = variants.flatMap { prefix -> choices.map { "$prefix $it".trim() } }.distinct().take(256)
            }
            addAll(variants.filter(String::isNotEmpty))
        }
    }

    private fun devoice(token: String): String = when (token) {
        "b" -> "p"; "d" -> "t"; "ɡ", "g" -> "k"; "z" -> "s"; "v" -> "f"; else -> token
    }
}
