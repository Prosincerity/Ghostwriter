package com.prosincerity.ghostwriter.dictionary.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionaryLexiconTest {
    @Test fun stagedLexiconLoadingRetainsTheLongestHeadAndCachesItsCompleteAnalysis() = runBlocking {
        val words = setOf("Donau", "Dampfschifffahrt", "Dampf", "Schiff", "Fahrt")
        var queries = 0
        val data = object : DictionarySearchData {
            override val languages = setOf("de")
            override suspend fun lookup(word: String, language: String): PronunciationResult? = error("No generation")
            override suspend fun withSource(language: String, source: DictionarySource, scan: suspend (DictionarySearchRows) -> Unit) {
                scan(DictionarySearchRows { _, args, onRow ->
                    queries++
                    val found = words.filter { it in args }
                    found.forEach { onRow(it, "/a/") }
                    found.size
                })
            }
        }
        val lexicon = DictionaryLexicon(data, "de")
        val input = "Donaudampfschifffahrt"
        lexicon.prepare(listOf(input))
        assertEquals(listOf("Donau", "dampfschifffahrt"), lexicon.decompound(input).map { it.surface })
        val before = queries
        lexicon.prepare(listOf(input, "a".repeat(97)))
        assertEquals(before, queries)
        assertEquals(listOf("a".repeat(97)), lexicon.decompound("a".repeat(97)).map { it.surface })
    }
    @Test fun unknownHeadsDoNotRequireEveryInteriorSubstring() = runBlocking {
        val probed = mutableSetOf<String>()
        val data = object : DictionarySearchData {
            override val languages = setOf("de")
            override suspend fun lookup(word: String, language: String): PronunciationResult? = error("No generation")
            override suspend fun withSource(language: String, source: DictionarySource, scan: suspend (DictionarySearchRows) -> Unit) {
                scan(DictionarySearchRows { _, args, _ -> probed += args.map { it.lowercase() }; 0 })
            }
        }
        val word = "Unbekannteswunderzeichen"
        val lexicon = DictionaryLexicon(data, "de")
        lexicon.prepare(listOf(word))
        assertEquals(listOf(word), lexicon.decompounder.decompound(word).map { it.surface })
        assertTrue("Unknown heads should prune interior vocabulary: ${probed.size} probes", probed.size <= word.length * 3)
    }
    @Test fun usesAttestedCaseVariantsAndCachesMissingAtoms() = runBlocking {
        var reads = 0
        val data = object : DictionarySearchData {
            override val languages = setOf("de")
            override suspend fun lookup(word: String, language: String): PronunciationResult? = error("Must not generate lexicon words")
            override suspend fun withSource(language: String, source: DictionarySource, scan: suspend (DictionarySearchRows) -> Unit) {
                scan(DictionarySearchRows { _, args, onRow ->
                    reads++
                    val words = if (source == DictionarySource.WIKTIONARY) listOf("Hund", "Hütte") else listOf("Hund")
                    val found = words.filter { it in args }
                    found.forEach { onRow(it, "/hʊnt/") }
                    found.size
                })
            }
        }
        val lexicon = DictionaryLexicon(data, "de")
        lexicon.prepare(listOf("Hundehütte"))
        assertEquals(listOf("Hunde", "hütte"), lexicon.decompounder.decompound("Hundehütte").map { it.surface })
        assertEquals(PronunciationSource.WIKTIONARY, lexicon.entries("hund").first().source)
        assertTrue(lexicon.entries("unbekannt").isEmpty())
        val before = reads
        lexicon.prepare(listOf("Hundehütte"))
        assertEquals(before, reads)
    }
}
