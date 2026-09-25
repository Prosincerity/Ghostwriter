package com.prosincerity.ghostwriter.logic

import java.text.Normalizer

/**
 * Adapted from Ghostwriter-Dict's generate_rhyme_db.py token rules for
 * release kaikki-v20260920. Copyright (c) 2026 Ghostwriter Dictionary
 * contributors; MIT license. See assets/licenses/Ghostwriter-Dict-MIT.txt.
 */
internal object IpaSearchKeys {
    private val vowels = mapOf(
        "en" to words("a e i o u y æ ɑ ɒ ɔ ə ɛ ɚ ɘ ɜ ɝ ɞ ɤ ɨ ɪ ɯ ɵ ʉ ʊ ʌ ʏ ø œ ɐ ᵊ ᵻ ᵿ aɪ aʊ eɪ oʊ ɔɪ əʊ ɪə eə ʊə"),
        "de" to words("a e i o u y æ ɑ ɒ ɔ ə ɛ ɐ ɘ ɜ ɝ ɚ ɤ ɨ ɪ ɯ ɶ ɵ ʉ ʊ ʌ ʏ ø œ ᵊ aɪ aʊ ɔʏ oʏ ʊɪ"),
        "tr" to words("a e i o u y ä æ ɑ ɒ ɔ ə ɛ ɐ ɜ ɚ ɞ ɤ ɨ ɪ ɯ ɶ ʊ ʌ ʏ ø œ"),
    )
    private val consonants = mapOf(
        "en" to words("b c d f g h j k l m n p r q s t v w x z ç ð β ɓ ɕ ɖ ɗ ɟ ɢ ɡ ɣ ɥ ɦ ɫ ɬ ɭ ɱ ɲ ɳ ɸ ɹ ɺ ɻ ɽ ɾ ʀ ʁ ʂ ʃ ʈ ʋ ʎ ʑ ʒ ʔ ʕ ʝ ʟ ʙ ʍ θ χ ŋ ł ǀ ǁ ǃ tʃ dʒ t͡ʃ d͡ʒ t͜ʃ d͜ʒ ʤ"),
        "de" to words("b c d f g h j k l m n p q r s t v w x z ç ð β ɓ ɕ ɟ ɡ ɣ ɥ ɦ ɫ ɬ ɮ ɱ ɲ ɴ ɸ ɹ ɺ ɽ ɾ ʀ ʁ ʂ ʃ ʈ ʋ ʎ ʑ ʒ ʔ ʕ ʝ ʟ ʙ θ χ ŋ pf ts ħ ɰ tʃ dʒ p͡f t͡s t͡ʃ d͡ʒ p͜f t͜s t͜ʃ d͜ʒ ʦ ʧ ǀ ǁ ǃ"),
        "tr" to words("b c d f g h j k l m n p q r s t v w x z ç β ɕ ɟ ɡ ɣ ɥ ɦ ɫ ɰ ɱ ɲ ɳ ɸ ɹ ɾ ł ʀ ʁ ʃ ʈ ʋ ʎ ʑ ʐ ʒ ʔ ʕ ʝ θ χ ŋ tʃ dʒ t͡ʃ d͡ʒ t͜ʃ d͜ʒ ʧ ʤ ǀ ǁ ǃ"),
    )
    private val candidates = vowels.keys.associateWith { language ->
        (vowels.getValue(language) + consonants.getValue(language))
            .sortedWith(compareByDescending<String> { it.length }.thenBy { it })
    }
    private val postfix = words(": ː ˑ ̆ ̯ ̃ ̥ ̬ ̩ ̪ ̺ ̻ ̝ ̞ ̘ ̙ ̚ ̰ ̤ ̹ ̜ ̟ ̠ ̼ ̽ ˀ ˔ ˭ ʰ ʱ ʲ ˠ ˤ ʴ ʷ ⁿ ˡ ˞ ʳ ʵ ʶ ˣ ˕ ˖ ᵈ ᵏ ᵐ ᵝ ᶦ ᶴ")
    private val prefix = words("ˀ ʰ ʱ ʲ ˠ ˤ ʷ ⁿ ˡ ᵈ ᵏ ᵐ")
    private val prosody = words("˥ ˦ ˧ ˨ ˩ ¹ ² ³ ⁴ ⁵ ⁻ ↗ ↘ ↑ ↓ ꜛ ꜜ")
    private val stress = setOf("ˈ", "ˌ")
    private val ignored = " ./[]()⟨⟩⁽⁾|-‿_⁀‖⫽︎"

    data class Keys(val tokens: List<String>, val reversed: String, val assonance: String)

    fun fromIpa(ipa: String, language: String): Keys? {
        val inventory = candidates[language] ?: return null
        val input = Normalizer.normalize(ipa.trim(), Normalizer.Form.NFC)
        val tokens = mutableListOf<String>()
        val singleBases = inventory.filter { it.length == 1 }.toSet()
        var position = 0
        var atBoundary = true
        while (position < input.length) {
            val character = input[position]
            val value = character.toString()
            if (character in ignored) {
                position++
                atBoundary = true
                continue
            }
            if (value in stress || value in prosody) {
                tokens += value
                position++
                atBoundary = true
                continue
            }
            if (isPostfix(character)) {
                val start = position
                while (position < input.length && isPostfix(input[position])) position++
                val modifiers = input.substring(start, position)
                val following = inventory.firstOrNull { input.startsWith(it, position) }
                if (atBoundary && value in prefix && following != null) {
                    position += following.length
                    val token = StringBuilder(modifiers).append(following)
                    while (position < input.length && isPostfix(input[position])) token.append(input[position++])
                    tokens += token.toString()
                } else if (tokens.isNotEmpty() && tokens.last() !in stress && tokens.last() !in prosody) {
                    tokens[tokens.lastIndex] += modifiers
                } else {
                    return null
                }
                atBoundary = false
                continue
            }
            var match = inventory.firstOrNull { input.startsWith(it, position) }
            if (match == null) {
                val decomposed = Normalizer.normalize(value, Normalizer.Form.NFD)
                if (decomposed.length <= 1 || decomposed.take(1) !in singleBases ||
                    decomposed.drop(1).any { !isPostfix(it) }
                ) return null
                match = value
            }
            position += match.length
            val token = StringBuilder(match)
            while (position < input.length && isPostfix(input[position])) token.append(input[position++])
            tokens += token.toString()
            atBoundary = false
        }
        if (tokens.none { it !in stress && it !in prosody }) return null
        return Keys(
            tokens = tokens,
            reversed = tokens.asReversed().joinToString(""),
            assonance = tokens.filter { isVowel(it, language) }.asReversed().joinToString(" "),
        )
    }

    /** Phonemes from the final primary-stressed vowel through the end of the word. */
    fun rimeTokens(keys: Keys, language: String): List<String>? {
        if (language !in vowels) return null
        val tokens = keys.tokens
        val stressIndex = tokens.indexOfLast { it == "ˈ" }.takeIf { it >= 0 }
            ?: tokens.indexOfLast { it == "ˌ" }.takeIf { it >= 0 }
        val vowelIndex = if (stressIndex != null) {
            (stressIndex + 1 until tokens.size).firstOrNull { isVowel(tokens[it], language) }
        } else {
            tokens.indices.filter { isVowel(tokens[it], language) }.singleOrNull()
        } ?: return null
        return tokens.drop(vowelIndex)
    }

    /** Suffix lengths x-2, x-4, ...; short words use two phonemes, never one. */
    fun phonemeSuffixes(keys: Keys): List<List<String>> {
        val phonemes = phonemeTokens(keys)
        if (phonemes.size < 4) {
            return if (phonemes.size >= 2) listOf(phonemes.takeLast(2)) else emptyList()
        }
        val lengths = mutableListOf<Int>()
        var length = phonemes.size - 2
        while (length > 2) {
            lengths += length
            length -= 2
        }
        return lengths.map(phonemes::takeLast)
    }

    fun phonemeTokens(keys: Keys): List<String> = phonemeTokens(keys.tokens)

    fun phonemeTokens(tokens: List<String>): List<String> = tokens.filter { it !in stress && it !in prosody }

    fun hasVowel(tokens: List<String>, language: String): Boolean =
        language in vowels && tokens.any { isVowel(it, language) }

    fun assonancePrefixes(keys: Keys): List<String> {
        val vowelsOnly = keys.assonance.split(' ').filter { it.isNotEmpty() }
        return (vowelsOnly.size downTo 1).map { length -> vowelsOnly.take(length).joinToString(" ") }
    }

    private fun isVowel(token: String, language: String): Boolean {
        val base = token.dropWhile { it.toString() in prefix }
        val decomposed = Normalizer.normalize(base, Normalizer.Form.NFD)
        return vowels.getValue(language).any { vowel ->
            base.startsWith(vowel) || decomposed.startsWith(Normalizer.normalize(vowel, Normalizer.Form.NFD))
        }
    }

    private fun isPostfix(character: Char): Boolean =
        character.toString() in postfix || Character.getType(character) == Character.NON_SPACING_MARK.toInt() ||
            Character.getType(character) == Character.ENCLOSING_MARK.toInt()

    private fun words(text: String): Set<String> = text.split(' ').filter { it.isNotEmpty() }.toSet()
}
