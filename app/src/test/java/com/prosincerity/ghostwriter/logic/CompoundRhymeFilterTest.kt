package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Test

class CompoundRhymeFilterTest {
    private fun entry(word: String, ipa: String) = CompoundRhymeFilter.Entry(word, ipa)

    @Test
    fun removesCompoundWhenStandaloneEndingIsAlsoReturned() {
        val results = listOf(
            entry("point", "/ˈpɔɪnt/"),
            entry("checkpoint", "/ˈtʃɛkpɔɪnt/"),
            entry("joint", "/ˈdʒɔɪnt/"),
            entry("at", "/æt/"),
            entry("bat", "/bæt/"),
        )

        assertEquals(setOf("checkpoint"), CompoundRhymeFilter.excludedWords(results, emptyList(), "en"))
        assertEquals(emptySet<String>(), CompoundRhymeFilter.excludedWords(
            listOf(results[1]), emptyList(), "en"))
    }

    @Test
    fun queryWordAlsoCountsAsStandaloneEnding() {
        assertEquals(setOf("checkpoint"), CompoundRhymeFilter.excludedWords(
            listOf(entry("checkpoint", "/ˈtʃɛkpɔɪnt/")),
            listOf(entry("point", "/ˈpɔɪnt/")), "en"))
    }

    @Test
    fun laterMatchesDoNotChangeEarlierPageExclusions() {
        val firstPage = listOf(entry("checkpoint", "/ˈtʃɛkpɔɪnt/"))
        assertEquals(emptySet<String>(), CompoundRhymeFilter.excludedWords(firstPage, emptyList(), "en"))
        assertEquals(emptySet<String>(), CompoundRhymeFilter.excludedWords(
            firstPage + entry("point", "/ˈpɔɪnt/"), emptyList(), "en"))
    }

    @Test
    fun germanSuffixMatchesRegardlessOfNounCapitalization() {
        assertEquals(setOf("Anwaltschaft", "Wissenschaft"), CompoundRhymeFilter.excludedWords(
            listOf(
                entry("Schaft", "/ʃaft/"),
                entry("Anwaltschaft", "/ˈanvaltʃaft/"),
                entry("Wissenschaft", "/ˈvɪsn̩ʃaft/"),
            ), emptyList(), "de"))
    }
}
