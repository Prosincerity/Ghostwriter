package com.prosincerity.ghostwriter.logic

import java.text.Normalizer

/**
 * Adapted from Ghostwriter-Dict's clean_rhyme_wordlist.py product cleanup v11.
 * Copyright (c) 2026 Ghostwriter Dictionary contributors; MIT license.
 * See assets/licenses/Ghostwriter-Dict-MIT.txt.
 */
internal object DictionaryHeadword {
    private const val LETTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz" +
        "ÄÖÜẞäöüßÂÇĞÎİÖŞÛÜâçğîıöşûü" +
        "ÀÂÆÇÈÉÊËÎÏÔŒÙÛÜŸàâæçèéêëîïôœùûüÿ" +
        "ÁÃÅÐÍÌÑÒÓÕØÚÝÞáãåðíìñòóõøúýþ" +
        "ĀĂĄĆĈČĎĐĒĖĘĢĤĪĮĶĹĻĽŁŃŅŇ" +
        "ŐŔŖŘŚŜŠŢŤŪŬŰŲŴŶŹŻŽƏ" +
        "āăąćĉčďđēėęģĥīįķĺļľłńņň" +
        "őŕŗřśŝšţťūŭűųŵŷźżžəʻ"
    private const val DIGITS = "0123456789"
    private const val DASHES = "‐‑‒–—−"
    private const val APOSTROPHES = "’‘ʼ"

    fun normalize(input: String): String = Normalizer.normalize(buildString {
        input.forEach { character ->
            append(when {
                character == '\u00ad' -> return@forEach
                character in APOSTROPHES -> '\''
                character in DASHES -> '-'
                character in '₀'..'₉' -> ('0'.code + character.code - '₀'.code).toChar()
                else -> character
            })
        }
    }, Normalizer.Form.NFC)

    fun eligible(word: String): Boolean {
        if (word.isEmpty() || word.length == 1 && word[0] in LETTERS) return false
        for (position in word.indices) {
            val character = word[position]
            val before = word.getOrNull(position - 1)
            val after = word.getOrNull(position + 1)
            when {
                character in LETTERS || character in DIGITS -> Unit
                character in ".-&/" -> {
                    if (!isLetter(before) || !isLetter(after)) return false
                }
                character == '\'' -> {
                    if (before == '\'' || after == '\'' ||
                        !isAlphanumeric(before) && !isAlphanumeric(after)
                    ) return false
                }
                character == '%' -> {
                    if (position != word.lastIndex || before == null || before !in DIGITS) return false
                }
                character == '+' -> {
                    // A plus is valid only in a trailing run after a letter. Validate
                    // that run once; the preceding characters have already been checked.
                    return isLetter(before) && (position until word.length).all { word[it] == '+' }
                }
                else -> return false
            }
        }
        return true
    }

    fun normalizedEligible(input: String): String? = normalize(input).takeIf(::eligible)

    private fun isLetter(character: Char?): Boolean = character != null && character in LETTERS
    private fun isAlphanumeric(character: Char?): Boolean =
        character != null && (character in LETTERS || character in DIGITS)
}
