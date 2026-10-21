package com.prosincerity.ghostwriter.data

import com.prosincerity.ghostwriter.logic.GermanDecompounder
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

/** Per-search cache of attested words. Generated IPA is never evidence that a word exists. */
internal class DictionaryLexicon(private val data: DictionarySearchData, private val language: String) {
    private val checked = mutableSetOf<String>()
    private val words = mutableMapOf<String, MutableList<DictionaryMatch>>()
    val decompounder = GermanDecompounder { it in words }

    suspend fun prepare(inputs: Collection<String>) {
        load(inputs.flatMap { GermanDecompounder.vocabulary(it) }.toSet())
    }

    suspend fun load(inputs: Collection<String>) {
        val missing = inputs.map(GermanDecompounder::key).distinct().filterNot { it in checked }
        if (missing.isEmpty()) return
        val spellings = missing.flatMap { listOf(it, it.replaceFirstChar(Char::titlecase)) }.distinct()
        for (source in DictionarySource.entries) {
            data.withSource(language, source) { rows ->
                for (batch in spellings.chunked(400)) {
                    coroutineContext.ensureActive()
                    rows.read("SELECT word, ipa FROM dictionary WHERE word IN (${batch.joinToString(",") { "?" }}) ORDER BY word, ipa",
                        batch.toTypedArray()) { word, ipa ->
                        val entries = words.getOrPut(GermanDecompounder.key(word)) { mutableListOf() }
                        if (entries.none { it.word == word && it.ipa == ipa }) {
                            entries += DictionaryMatch(word, ipa, source.pronunciationSource)
                        }
                    }
                }
            }
        }
        checked += missing
    }

    fun entries(word: String): List<DictionaryMatch> = words[GermanDecompounder.key(word)].orEmpty()
}
