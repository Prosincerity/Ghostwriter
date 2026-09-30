package com.prosincerity.ghostwriter.data

import com.prosincerity.ghostwriter.logic.IpaSearchKeys
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

/** Runs the production search loop with offline rows in place of Android SQLite. */
class DictionarySearchTest {
    @Test
    fun shorterExactAssonanceFromEspeakPrecedesLongerWiktionaryMatches() = runBlocking {
        val data = AssonanceData(listOf("/aʊ/", "/aʊ.ə/"), mapOf(
            DictionarySource.WIKTIONARY to (0 until 200).map {
                row("word${it.toString().padStart(3, '0')}", "/baʊ.ə/")
            },
            DictionarySource.ESPEAK to listOf(row("base", "/baʊ/")),
        ))

        val result = DictionarySearch(data).search("query", "de", DictionarySearchMode.ASSONANCE)

        assertEquals("base", result.matches.first().word)
        assertEquals(PronunciationSource.ESPEAK_DATABASE, result.matches.first().source)
        assertEquals(60, result.matches.size)
        assertTrue(result.hasNext)
    }

    @Test
    fun laterInfinitiveDoesNotReplaceAFormAlreadyShownOnAnEarlierPage() = runBlocking {
        val wiki = listOf(row("kauf", "/kaʊf/")) +
            (0 until 199).map { row("word${it.toString().padStart(3, '0')}", "/baʊ/") }
        val data = AssonanceData(listOf("/aʊ/"), mapOf(
            DictionarySource.WIKTIONARY to wiki,
            DictionarySource.ESPEAK to listOf(row("kaufen", "/kaʊfn̩/")),
        ))
        val search = DictionarySearch(data)

        val pages = (0..3).map { search.search("query", "de", DictionarySearchMode.ASSONANCE, it) }

        assertEquals(listOf(60, 60, 60, 20), pages.map { it.matches.size })
        assertEquals(listOf(true, true, true, false), pages.map { it.hasNext })
        assertEquals(wiki.map { it.word }, pages.flatMap { it.matches }.map { it.word })
        // Direct and reverse page requests must rebuild the same sequence.
        assertEquals(pages[3], DictionarySearch(data).search("query", "de", DictionarySearchMode.ASSONANCE, 3))
        assertEquals(pages[0], search.search("query", "de", DictionarySearchMode.ASSONANCE))
    }

    @Test
    fun infinitiveStillWinsWhenFamilyIsFoundBeforeTheFirstPageFills() = runBlocking {
        val data = AssonanceData(listOf("/aʊ/"), mapOf(
            DictionarySource.WIKTIONARY to listOf(row("kauf", "/kaʊf/")),
            DictionarySource.ESPEAK to listOf(row("kaufen", "/kaʊfn̩/")),
        ))

        val result = DictionarySearch(data).search("query", "de", DictionarySearchMode.ASSONANCE)

        assertEquals(listOf("kaufen"), result.matches.map { it.word })
        assertFalse(result.hasNext)
    }

    @Test
    fun fullPageWithoutLookaheadStillPrefersLaterInfinitive() = runBlocking {
        val data = AssonanceData(listOf("/aʊ/"), mapOf(
            DictionarySource.WIKTIONARY to listOf(row("kauf", "/kaʊf/")) +
                (0 until 59).map { row("word${it.toString().padStart(3, '0')}", "/baʊ/") },
            DictionarySource.ESPEAK to listOf(row("kaufen", "/kaʊfn̩/")),
        ))

        val result = DictionarySearch(data).search("query", "de", DictionarySearchMode.ASSONANCE)

        assertEquals(60, result.matches.size)
        assertTrue(result.matches.any { it.word == "kaufen" })
        assertFalse(result.matches.any { it.word == "kauf" })
        assertFalse(result.hasNext)
    }

    @Test
    fun laterBatchInTheSameSourcePreservesEarlierPages() = runBlocking {
        val initialBatch = listOf(row("Kauf", "/kaʊf/")) +
            (0 until 199).map { row("item${it.toString().padStart(3, '0')}", "/baʊ/") }
        val data = AssonanceData(listOf("/aʊ/"), mapOf(
            DictionarySource.WIKTIONARY to initialBatch + row("kaufen", "/kaʊfn̩/"),
        ))
        val search = DictionarySearch(data)

        val pages = (0..3).map { search.search("query", "de", DictionarySearchMode.ASSONANCE, it) }

        assertEquals(initialBatch.map { it.word }, pages.flatMap { it.matches }.map { it.word })
        assertFalse(pages.last().hasNext)
    }

    @Test
    fun sameExactTierKeepsWiktionaryPronunciationAndExcludesQueryWord() = runBlocking {
        val data = AssonanceData(listOf("/aʊ/"), mapOf(
            DictionarySource.WIKTIONARY to listOf(row("query", "/aʊ/"), row("match", "/baʊ/")),
            DictionarySource.ESPEAK to listOf(row("match", "/paʊ/")),
        ))

        val result = DictionarySearch(data).search("query", "de", DictionarySearchMode.ASSONANCE)

        assertEquals(listOf(DictionaryMatch("match", "/baʊ/", PronunciationSource.WIKTIONARY)), result.matches)
        assertFalse(result.hasNext)
    }

    private fun row(word: String, ipa: String) = DictionaryMatch(word, ipa, PronunciationSource.WIKTIONARY)

    /** Supplies exact-key fixture rows; production still decides tiers, batches, filtering, and pages. */
    private class AssonanceData(
        private val ipa: List<String>,
        private val rows: Map<DictionarySource, List<DictionaryMatch>>,
    ) : DictionarySearchData {
        override val languages = setOf("de")

        override suspend fun lookup(word: String, language: String) =
            PronunciationResult(ipa, PronunciationSource.WIKTIONARY)

        override suspend fun withSource(
            language: String,
            source: DictionarySource,
            scan: suspend (DictionarySearchRows) -> Unit,
        ) {
            scan(DictionarySearchRows { sql, args, onRow ->
                check(sql.startsWith("SELECT word, ipa FROM dictionary WHERE assonance_reversed = ? ORDER BY word, ipa"))
                val bounds = Regex("LIMIT (\\d+) OFFSET (\\d+)$").find(sql)!!.groupValues
                val batch = rows[source].orEmpty()
                    .filter { IpaSearchKeys.fromIpa(it.ipa, language)?.assonance == args.single() }
                    .sortedWith(compareBy<DictionaryMatch> { it.word }.thenBy { it.ipa })
                    .drop(bounds[2].toInt()).take(bounds[1].toInt())
                batch.forEach { onRow(it.word, it.ipa) }
                batch.size
            })
        }
    }
}
