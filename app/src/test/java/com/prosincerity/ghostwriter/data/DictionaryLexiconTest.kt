package com.prosincerity.ghostwriter.data

import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionaryLexiconTest {
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
