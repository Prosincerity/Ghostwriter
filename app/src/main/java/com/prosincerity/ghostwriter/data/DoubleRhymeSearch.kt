package com.prosincerity.ghostwriter.data

import com.prosincerity.ghostwriter.logic.DoubleRhymePhonetics
import com.prosincerity.ghostwriter.logic.GermanDecompounder
import kotlinx.coroutines.ensureActive
import kotlin.coroutines.coroutineContext

/** German two-anchor search. Attested compounds reserve their components before free pairs. */
internal class DoubleRhymeSearch(private val data: DictionarySearchData) {
    suspend fun search(input: String, page: Int): DictionarySearchResult {
        val lexicon = DictionaryLexicon(data, "de")
        val tokens = input.split(Regex("\\s+")).filter(String::isNotEmpty)
        val anchors = when (tokens.size) {
            1 -> {
                lexicon.prepare(tokens)
                val parts = lexicon.decompounder.decompound(input)
                if (parts.size < 2) return DictionarySearchResult(null, emptyList())
                listOf(parts.dropLast(1).joinToString("") { it.surface }, parts.last().surface)
            }
            2 -> tokens
            else -> return DictionarySearchResult(null, emptyList())
        }
        val pronunciations = mutableMapOf<String, PronunciationResult?>()
        suspend fun pronounce(word: String): PronunciationResult? {
            val key = GermanDecompounder.key(word)
            if (key !in pronunciations) {
                lexicon.load(listOf(word))
                val attested = lexicon.entries(word)
                val first = attested.firstOrNull()
                pronunciations[key] = if (first != null) {
                    PronunciationResult(attested.filter { it.source == first.source }.map { it.ipa }.distinct(), first.source)
                } else data.lookup(word, "de")
            }
            return pronunciations[key]
        }
        val leftIpa = pronounce(anchors[0]) ?: return DictionarySearchResult(null, emptyList())
        val rightIpa = pronounce(anchors[1]) ?: return DictionarySearchResult(null, emptyList())
        val combined = leftIpa.ipa.flatMap { left -> rightIpa.ipa.map { right -> "$left $right" } }
        val pronunciation = PronunciationResult(combined, weakerSource(leftIpa.source, rightIpa.source))
        val used = mutableSetOf<String>()
        val matches = mutableListOf<DictionaryMatch>()
        fun reserve(match: DictionaryMatch): Boolean {
            val words = match.components.map(GermanDecompounder::key)
            if (words.any { it in used } || words.distinct().size != words.size) return false
            used += words
            matches += match
            return true
        }
        fun identity(parts: List<String>): Boolean = parts.indices.all { index ->
            lexicon.decompounder.decompound(parts[index]).last().lemma ==
                lexicon.decompounder.decompound(anchors[index]).last().lemma
        }
        val compounds = collect(combined)
        lexicon.prepare(compounds.map { it.word })
        val attested = mutableListOf<Pair<Int, DictionaryMatch>>()
        for (compound in compounds) {
            coroutineContext.ensureActive()
            val parts = lexicon.decompounder.decompound(compound.word)
            if (parts.size < 2) continue
            val surfaces = listOf(parts.dropLast(1).joinToString("") { it.surface }, parts.last().surface)
            if (identity(surfaces)) continue
            val first = pronounce(surfaces[0]) ?: continue
            val second = pronounce(surfaces[1]) ?: continue
            val leftScore = first.ipa.mapNotNull { DoubleRhymePhonetics.distance(leftIpa.ipa, it) }.minOrNull() ?: continue
            val rightScore = second.ipa.mapNotNull { DoubleRhymePhonetics.distance(rightIpa.ipa, it) }.minOrNull() ?: continue
            attested += leftScore + rightScore to compound.copy(components = surfaces)
        }
        attested.sortedWith(compareBy<Pair<Int, DictionaryMatch>> { it.first }
            .thenBy { it.second.source.ordinal }.thenBy { it.second.word })
            .distinctBy { GermanDecompounder.key(it.second.word) }.forEach { reserve(it.second) }

        fun ranked(rows: List<DictionaryMatch>, ipa: List<String>, anchor: String): List<DictionaryMatch> = rows
            .mapNotNull { match -> DoubleRhymePhonetics.distance(ipa, match.ipa)?.let { it to match } }
            .sortedWith(compareBy<Pair<Int, DictionaryMatch>> { it.first }
                .thenBy { it.second.source.ordinal }.thenBy { it.second.word }.thenBy { it.second.ipa })
            .map { it.second }.distinctBy { GermanDecompounder.key(it.word) }
            .filterNot { GermanDecompounder.key(it.word) == GermanDecompounder.key(anchor) }
        val left = ranked(collect(leftIpa.ipa), leftIpa.ipa, anchors[0])
        val right = ranked(collect(rightIpa.ipa), rightIpa.ipa, anchors[1])
        // Pair the strongest remaining alternatives without constructing a Cartesian product.
        for (first in left) {
            coroutineContext.ensureActive()
            if (GermanDecompounder.key(first.word) in used) continue
            val second = right.firstOrNull {
                GermanDecompounder.key(it.word) !in used &&
                    GermanDecompounder.key(it.word) != GermanDecompounder.key(first.word)
            } ?: break
            reserve(DictionaryMatch("${first.word} + ${second.word}", "${first.ipa} + ${second.ipa}",
                weakerSource(first.source, second.source), listOf(first.word, second.word)))
        }
        val offset = page.toLong() * PAGE_SIZE
        return DictionarySearchResult(pronunciation, matches.drop(offset.toInt()).take(PAGE_SIZE),
            matches.size.toLong() > offset + PAGE_SIZE)
    }

    private suspend fun collect(ipa: List<String>): List<DictionaryMatch> {
        val keys = DoubleRhymePhonetics.indexKeys(ipa).toList()
        if (keys.isEmpty()) return emptyList()
        val vowels = ipa.mapNotNull { DoubleRhymePhonetics.keys(it)?.assonance }.toSet()
        val matches = mutableListOf<DictionaryMatch>()
        for (source in DictionarySource.entries) {
            data.withSource("de", source) { rows ->
                var offset = 0
                do {
                    coroutineContext.ensureActive()
                    val count = rows.read(
                        "SELECT word, ipa FROM dictionary WHERE assonance_reversed IN (${keys.joinToString(",") { "?" }}) " +
                            "ORDER BY word, ipa LIMIT 200 OFFSET $offset", keys.toTypedArray(),
                    ) { word, candidate ->
                        if (DoubleRhymePhonetics.keys(candidate)?.assonance in vowels) {
                            matches += DictionaryMatch(word, candidate, source.pronunciationSource)
                        }
                    }
                    offset += count
                } while (count == 200)
            }
        }
        return matches.distinctBy { it.word to it.ipa }
    }

    private fun weakerSource(first: PronunciationSource, second: PronunciationSource): PronunciationSource =
        if (first.ordinal >= second.ordinal) first else second

    private companion object { const val PAGE_SIZE = 60 }
}
