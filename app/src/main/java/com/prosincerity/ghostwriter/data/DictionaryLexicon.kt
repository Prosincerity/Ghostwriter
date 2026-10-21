package com.prosincerity.ghostwriter.data

import com.prosincerity.ghostwriter.logic.GermanDecompounder
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

/** Per-search cache of attested words. Generated IPA is never evidence that a word exists. */
internal class DictionaryLexicon(private val data: DictionarySearchData, private val language: String) {
    private val checked = mutableSetOf<String>()
    private val words = mutableMapOf<String, MutableList<DictionaryMatch>>()
    private val decompositions = mutableMapOf<String, List<GermanDecompounder.Part>>()
    private var probes: MutableSet<String>? = null
    val decompounder = GermanDecompounder(
        pronunciations = { word -> request(word); words[word].orEmpty().map { it.ipa } },
        contains = { request(it); it in words },
    )

    suspend fun prepare(inputs: Collection<String>) {
        var pending = inputs.distinct().filterNot { it in decompositions }
        while (pending.isNotEmpty()) {
            val required = mutableSetOf<String>()
            val unresolved = mutableListOf<String>()
            for (word in pending) {
                coroutineContext.ensureActive()
                val missing = mutableSetOf<String>()
                probes = missing
                val parts = try { decompounder.decompound(word) } finally { probes = null }
                if (missing.isEmpty()) decompositions[word] = parts else {
                    required += missing
                    unresolved += word
                }
            }
            // Resolve only atoms visited by the splitter. Unknown heads prune their
            // modifiers; no eager enumeration of every interior substring is needed.
            load(required)
            pending = unresolved
        }
    }

    fun decompound(word: String): List<GermanDecompounder.Part> =
        decompositions[word] ?: decompounder.decompound(word)

    private fun request(key: String) {
        if (key !in checked) probes?.add(key)
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
