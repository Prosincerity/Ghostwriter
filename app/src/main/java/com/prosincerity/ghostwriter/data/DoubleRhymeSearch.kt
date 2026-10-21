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
                val parts = lexicon.decompound(input)
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
        val leftMatcher = DoubleRhymePhonetics.Matcher(leftIpa.ipa)
        val rightMatcher = DoubleRhymePhonetics.Matcher(rightIpa.ipa)
        val used = mutableSetOf<String>()
        val matches = mutableListOf<DictionaryMatch>()
        val offset = page.toLong() * PAGE_SIZE
        fun pageResult() = DictionarySearchResult(pronunciation, matches.drop(offset.toInt()).take(PAGE_SIZE),
            matches.size.toLong() > offset + PAGE_SIZE)
        fun reserve(match: DictionaryMatch): Boolean {
            val words = match.components.map(GermanDecompounder::key)
            if (words.any { it in used } || words.distinct().size != words.size) return false
            used += words
            matches += match
            return true
        }
        fun identity(parts: List<String>): Boolean = parts.indices.all { index ->
            lexicon.decompound(parts[index]).last().lemma ==
                lexicon.decompound(anchors[index]).last().lemma
        }
        val compounds = collect(combined)
        lexicon.prepare(compounds.map { it.word })
        val attested = mutableListOf<Pair<Int, DictionaryMatch>>()
        for (compound in compounds) {
            coroutineContext.ensureActive()
            val parts = lexicon.decompound(compound.word)
            if (parts.size < 2) continue
            val surfaces = listOf(parts.dropLast(1).joinToString("") { it.surface }, parts.last().surface)
            if (identity(surfaces)) continue
            val first = pronounce(surfaces[0]) ?: continue
            val second = pronounce(surfaces[1]) ?: continue
            val leftScore = first.ipa.mapNotNull(leftMatcher::distance).minOrNull() ?: continue
            val rightScore = second.ipa.mapNotNull(rightMatcher::distance).minOrNull() ?: continue
            attested += leftScore + rightScore to compound.copy(components = surfaces)
        }
        attested.sortedWith(compareBy<Pair<Int, DictionaryMatch>> { it.first }
            .thenBy { it.second.source.ordinal }.thenBy { it.second.word }.thenBy { it.second.ipa })
            .distinctBy { GermanDecompounder.key(it.second.word) }.forEach {
                reserve(it.second)
                // All compounds precede free pairs. A full page plus one real result
                // already determines both the page and hasNext, so stop here.
                if (matches.size.toLong() > offset + PAGE_SIZE) return pageResult()
            }

        fun ranked(rows: List<DictionaryMatch>, matcher: DoubleRhymePhonetics.Matcher, anchor: String): List<DictionaryMatch> = rows
            .mapNotNull { match -> matcher.distance(match.ipa)?.let { it to match } }
            .sortedWith(compareBy<Pair<Int, DictionaryMatch>> { it.first }
                .thenBy { it.second.source.ordinal }.thenBy { it.second.word }.thenBy { it.second.ipa })
            .map { it.second }.distinctBy { GermanDecompounder.key(it.word) }
            .filterNot { GermanDecompounder.key(it.word) == GermanDecompounder.key(anchor) }
        val left = ranked(collect(leftIpa.ipa, leftMatcher), leftMatcher, anchors[0])
        val right = ranked(collect(rightIpa.ipa, rightMatcher), rightMatcher, anchors[1])
        val remaining = right.associateByTo(linkedMapOf()) { GermanDecompounder.key(it.word) }
        used.forEach(remaining::remove)
        // Pair the strongest remaining alternatives without constructing a Cartesian product.
        for (first in left) {
            coroutineContext.ensureActive()
            val firstKey = GermanDecompounder.key(first.word)
            if (firstKey in used) continue
            val choices = remaining.entries.iterator()
            if (!choices.hasNext()) break
            var choice = choices.next()
            if (choice.key == firstKey) {
                if (!choices.hasNext()) break
                choice = choices.next()
            }
            val second = choice.value
            remaining.remove(firstKey)
            remaining.remove(choice.key)
            reserve(DictionaryMatch("${first.word} + ${second.word}", "${first.ipa} + ${second.ipa}",
                weakerSource(first.source, second.source), listOf(first.word, second.word)))
        }
        return pageResult()
    }

    private suspend fun collect(
        ipa: List<String>, matcher: DoubleRhymePhonetics.Matcher = DoubleRhymePhonetics.Matcher(ipa),
    ): List<DictionaryMatch> {
        val keys = DoubleRhymePhonetics.indexKeys(ipa).toList()
        if (keys.isEmpty()) return emptyList()
        val matches = mutableListOf<DictionaryMatch>()
        for (source in DictionarySource.entries) {
            data.withSource("de", source) { rows ->
                coroutineContext.ensureActive()
                // This mode consumes the entire bucket. One covering-index cursor avoids
                // sorting and revisiting the bucket for each 200-row OFFSET page.
                rows.read(
                    "SELECT word, ipa FROM dictionary WHERE assonance_reversed IN (${keys.joinToString(",") { "?" }}) " +
                        "ORDER BY assonance_reversed, word, ipa", keys.toTypedArray(),
                ) { word, candidate ->
                    if (matcher.matchesVowels(candidate)) {
                        matches += DictionaryMatch(word, candidate, source.pronunciationSource)
                    }
                }
            }
        }
        return matches.distinctBy { it.word to it.ipa }
    }

    private fun weakerSource(first: PronunciationSource, second: PronunciationSource): PronunciationSource =
        if (first.ordinal >= second.ordinal) first else second

    private companion object { const val PAGE_SIZE = 60 }
}
