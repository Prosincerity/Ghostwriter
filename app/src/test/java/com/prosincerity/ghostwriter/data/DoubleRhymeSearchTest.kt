package com.prosincerity.ghostwriter.data

import com.prosincerity.ghostwriter.logic.IpaSearchKeys
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class DoubleRhymeSearchTest {
    @Test fun aFullCompoundPageWithLookaheadDoesNotScanStandaloneAlternatives() = runBlocking {
        val compounds = (0..69).flatMap { index ->
            val suffix = "${('a'.code + index / 26).toChar()}${('a'.code + index % 26).toChar()}"
            val left = "links$suffix"
            val right = "rechts$suffix"
            listOf(row(left, "/ˈʃʁankɐ/"), row(right, "/ˈʁoːtə/"), row(left + right, "/ˈʃʁankɐˌʁoːtə/"))
        }
        val fixture = Fixture(listOf(row("Anker", "/ˈankɐ/"), row("Bote", "/ˈboːtə/")) + compounds)
        var candidateReads = 0
        val data = object : DictionarySearchData by fixture {
            override suspend fun withSource(language: String, source: DictionarySource, scan: suspend (DictionarySearchRows) -> Unit) {
                fixture.withSource(language, source) { rows ->
                    scan(DictionarySearchRows { sql, args, onRow ->
                        if ("WHERE assonance_reversed" in sql) candidateReads++
                        rows.read(sql, args, onRow)
                    })
                }
            }
        }
        val result = DictionarySearch(data).search("Anker Bote", "de", DictionarySearchMode.DOUBLE_RHYME)
        assertEquals(compounds.filter { it.word.contains("rechts") && it.word.startsWith("links") }
            .sortedBy { it.word }.take(60).map { it.word }, result.matches.map { it.word })
        assertTrue(result.hasNext)
        assertEquals("Only the compound bucket is needed across the two sources", 2, candidateReads)
    }
    @Test fun pairingSkipsTheSameSpellingAndReservesWordsAcrossBothPositions() = runBlocking {
        // Artificial homograph rows isolate pairing order without asserting linguistic facts.
        val rows = listOf(row("Anker", "/ˈankɐ/"), row("Bote", "/ˈboːtə/"),
            row("shared", "/ˈʃʁankɐ/"), row("shared", "/ˈʁoːtə/"),
            row("zleft", "/ˈʃʁankɐ/"), row("zright", "/ˈʁoːtə/"))
        val result = DictionarySearch(Fixture(rows)).search("Anker Bote", "de", DictionarySearchMode.DOUBLE_RHYME)
        assertEquals(listOf("shared + zright"), result.matches.map { it.word })
        assertFalse(result.hasNext)
    }
    @Test fun scansEachCandidateBucketOnceInsteadOfRepeatingSortedOffsets() = runBlocking {
        val rows = listOf(row("Anker", "/ˈankɐ/"), row("Bote", "/ˈboːtə/")) +
            (0..204).flatMap { listOf(row("links$it", "/ˈʃʁankɐ/"), row("rechts$it", "/ˈʁoːtə/")) }
        val fixture = Fixture(rows)
        var candidateReads = 0
        val data = object : DictionarySearchData by fixture {
            override suspend fun withSource(language: String, source: DictionarySource, scan: suspend (DictionarySearchRows) -> Unit) {
                fixture.withSource(language, source) { database ->
                    scan(DictionarySearchRows { sql, args, onRow ->
                        if ("WHERE assonance_reversed" in sql) candidateReads++
                        database.read(sql, args, onRow)
                    })
                }
            }
        }
        val result = DictionarySearch(data).search("Anker Bote", "de", DictionarySearchMode.DOUBLE_RHYME)
        assertEquals(60, result.matches.size)
        assertTrue(result.hasNext)
        assertEquals("Three buckets across two sources need six indexed scans", 6, candidateReads)
    }
    private fun row(word: String, ipa: String) = DictionaryMatch(word, ipa, PronunciationSource.WIKTIONARY)
    private val street = listOf(
        row("Straße", "/ˈʃtʁaːsə/"), row("Straßen", "/ˈʃtʁaːsən/"), row("Bande", "/ˈbandə/"),
        row("Straßenbande", "/ˈʃtʁaːsənˌbandə/"), row("Nase", "/ˈnaːzə/"), row("Nasen", "/ˈnaːzən/"),
        row("Kante", "/ˈkantə/"), row("Nasenkante", "/ˈnaːzənˌkantə/"),
        row("Phasen", "/ˈfaːzən/"), row("Vasen", "/ˈvaːzən/"),
        row("Schande", "/ˈʃandə/"), row("Tante", "/ˈtantə/"),
    )
    private val phrase = listOf(
        row("bleibe", "/ˈblaɪbə/"), row("Straße", "/ˈʃtʁaːsə/"), row("Keine", "/ˈkaɪnə/"),
        row("Frage", "/ˈfʁaːɡə/"), row("Geisel", "/ˈɡaɪzl̩/"), row("nehmen", "/ˈneːmən/"),
        row("Geiselnahme", "/ˈɡaɪzl̩ˌnaːmə/"), row("Seife", "/ˈzaɪfə/"), row("Seifen", "/ˈzaɪfn̩/"),
        row("Blase", "/ˈblaːzə/"), row("Seifenblase", "/ˈzaɪfn̩ˌblaːzə/"), row("Seite", "/ˈzaɪtə/"),
        row("Seiten", "/ˈzaɪtn̩/"), row("Seitenstraße", "/ˈzaɪtn̩ˌʃtʁaːsə/"),
    )

    @Test fun prioritizesNasenkanteAndPairsRemainingWordsOnce() = runBlocking {
        val result = DictionarySearch(Fixture(street)).search("Straßenbande", "de", DictionarySearchMode.DOUBLE_RHYME)
        assertEquals(listOf("Nasenkante", "Phasen + Schande", "Vasen + Tante"), result.matches.map { it.word })
        assertEquals(listOf("Nasen", "kante"), result.matches.first().components)
        assertFalse(result.hasNext)
    }

    @Test fun phraseFindsCompoundsBeforeNonrepeatingFreePairs() = runBlocking {
        val result = DictionarySearch(Fixture(phrase, generated = mapOf("nahme" to listOf("nˈɑːmə"))))
            .search("bleibe Straße", "de", DictionarySearchMode.DOUBLE_RHYME)
        assertEquals(setOf("Seitenstraße", "Geiselnahme", "Seifenblase", "Keine + Frage"), result.matches.map { it.word }.toSet())
        assertTrue(result.matches.take(3).all { !it.word.contains(" + ") })
        val components = result.matches.flatMap { it.components }.map { it.lowercase() }
        assertEquals(components.size, components.distinct().size)
        assertNotNull(result.pronunciation)
    }

    @Test fun bothAnchorsMustMatchVowelsEvenForLooseRhymes() = runBlocking {
        val rows = listOf(row("Hund", "/hʊnt/"), row("Hunde", "/ˈhʊndə/"), row("Hütte", "/ˈhʏtə/"),
            row("Hundehütte", "/ˈhʊndəˌhʏtə/"), row("bunte", "/ˈbʊntə/"), row("Mitte", "/ˈmɪtə/"),
            row("Tüte", "/ˈtyːtə/"), row("Sünde", "/ˈzʏndə/"))
        val result = DictionarySearch(Fixture(rows)).search("Hundehütte", "de", DictionarySearchMode.DOUBLE_RHYME)
        assertEquals(listOf("bunte + Sünde"), result.matches.map { it.word })
        assertTrue(result.matches.none { "Mitte" in it.components || "Tüte" in it.components })
    }

    @Test fun pagesAreStableAndDoNotReuseComponentsAcrossPagesOrSources() = runBlocking {
        val rows = listOf(row("Anker", "/ˈankɐ/"), row("Bote", "/ˈboːtə/")) +
            (0..124).flatMap { listOf(row("links$it", "/ˈʃʁankɐ/"), row("rechts$it", "/ˈʁoːtə/")) }
        val search = DictionarySearch(Fixture(rows, rows.reversed()))
        val pages = (0..2).map { search.search("Anker Bote", "de", DictionarySearchMode.DOUBLE_RHYME, it) }
        assertEquals(listOf(60, 60, 5), pages.map { it.matches.size })
        assertEquals(listOf(true, true, false), pages.map { it.hasNext })
        val words = pages.flatMap { it.matches }.flatMap { it.components }
        assertEquals(250, words.toSet().size)
        assertEquals(pages[1], search.search("Anker Bote", "de", DictionarySearchMode.DOUBLE_RHYME, 1))
        assertTrue(pages.flatMap { it.matches }.all { it.source == PronunciationSource.WIKTIONARY })
    }

    @Test fun rejectsUnsplittableAndMalformedInputsAndMissingPronunciations() = runBlocking {
        val search = DictionarySearch(Fixture(street))
        for (input in listOf("Straße", "Hinterziehung", "one two three", "fehlend Straße", "")) {
            assertTrue(search.search(input, "de", DictionarySearchMode.DOUBLE_RHYME).matches.isEmpty())
        }
    }

    internal class Fixture(
        private val wiki: List<DictionaryMatch>, private val espeak: List<DictionaryMatch> = emptyList(),
        private val generated: Map<String, List<String>> = emptyMap(),
    ) : DictionarySearchData {
        override val languages = setOf("de")
        override suspend fun lookup(word: String, language: String): PronunciationResult? =
            wiki.filter { it.word == word }.map { it.ipa }.takeIf { it.isNotEmpty() }
                ?.let { PronunciationResult(it, PronunciationSource.WIKTIONARY) }
                ?: generated[word]?.let { PronunciationResult(it, PronunciationSource.ESPEAK_GENERATED) }
        override suspend fun withSource(language: String, source: DictionarySource, scan: suspend (DictionarySearchRows) -> Unit) {
            scan(DictionarySearchRows { sql, args, onRow ->
                val rows = if (source == DictionarySource.WIKTIONARY) wiki else espeak
                val bounds = Regex("LIMIT (\\d+) OFFSET (\\d+)$").find(sql)?.groupValues
                val batch = rows.filter {
                    when {
                        "WHERE word IN" in sql -> it.word in args
                        "WHERE assonance_reversed IN" in sql -> IpaSearchKeys.fromIpa(it.ipa, language)?.assonance in args
                        else -> error(sql)
                    }
                }.sortedWith(compareBy<DictionaryMatch> { it.word }.thenBy { it.ipa })
                    .drop(bounds?.get(2)?.toInt() ?: 0).take(bounds?.get(1)?.toInt() ?: Int.MAX_VALUE)
                batch.forEach { onRow(it.word, it.ipa) }
                batch.size
            })
        }
    }
}
