package com.prosincerity.ghostwriter.dictionary.domain

/** Two independent rimes; vowel agreement admits the loose rhymes used in rap. */
internal object DoubleRhymePhonetics {
    private val syllabicConsonant = Regex("([nlm])̩")
    fun keys(ipa: String): IpaSearchKeys.Keys? = IpaSearchKeys.fromIpa(
        ipa.replace(syllabicConsonant, "ə$1").replace("̯", "").replace('ɑ', 'a'), "de",
    )

    /** 0: exact rime, 1: voicing difference, 2: vowel match, null: no match. */
    fun distance(input: List<String>, candidate: String): Int? = Matcher(input).distance(candidate)

    /** Compile query pronunciations once and analyse each distinct candidate IPA once per anchor. */
    class Matcher(input: List<String>, private val parse: (String) -> IpaSearchKeys.Keys? = ::keys) {
        private data class Profile(val vowels: String, val rime: List<String>?, val devoiced: List<String>?)
        private val profiles = mutableMapOf<String, Profile?>()
        private val queries = input.distinct().mapNotNull(::profile)
        private val vowelKeys = queries.mapTo(mutableSetOf()) { it.vowels }

        private fun profile(ipa: String): Profile? {
            if (!profiles.containsKey(ipa)) {
                profiles[ipa] = parse(ipa)?.let { keys ->
                    val rime = IpaSearchKeys.rimeTokens(keys, "de")?.let(IpaSearchKeys::phonemeTokens)
                    Profile(keys.assonance, rime, rime?.map(::devoice))
                }
            }
            return profiles[ipa]
        }

        fun matchesVowels(candidate: String): Boolean = profile(candidate)?.vowels in vowelKeys

        fun distance(candidate: String): Int? {
            val other = profile(candidate) ?: return null
            if (other.rime == null) return null
            var best: Int? = null
            for (query in queries) {
                if (query.vowels != other.vowels || query.rime == null) continue
                if (query.rime == other.rime) return 0
                val score = if (query.devoiced == other.devoiced) 1 else 2
                if (best == null || score < best) best = score
            }
            return best
        }
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
