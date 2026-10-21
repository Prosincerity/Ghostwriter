package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Test

class CompoundRhymeFilterTest {
    private fun entry(word: String, ipa: String) = CompoundRhymeFilter.Entry(word, ipa)

    @Test
    fun unsupportedGermanSplitsAreNotUsedForIdentityFiltering() {
        assertEquals(emptySet<String>(), CompoundRhymeFilter.excludedWords(
            listOf(entry("Handschuh", "/ˈhantˌʃuː/")),
            listOf(entry("Schuh", "/ˈʃuː/")), "de"))
    }

    @Test
    fun handschuhIsAnIdentityCompoundWhenItsPartsAreAttested() {
        val splitter = GermanDecompounder { it in setOf("hand", "schuh") }
        assertEquals(setOf("Handschuh"), CompoundRhymeFilter.excludedWords(
            listOf(entry("Handschuh", "/ˈhantˌʃuː/")),
            listOf(entry("Schuh", "/ˈʃuː/")), "de", splitter::decompound))
    }

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
    fun germanDerivationsAreNotCompoundsDespiteMatchingSuffixes() {
        assertEquals(emptySet<String>(), CompoundRhymeFilter.excludedWords(
            listOf(
                entry("Schaft", "/ʃaft/"),
                entry("Anwaltschaft", "/ˈanvaltʃaft/"),
                entry("Wissenschaft", "/ˈvɪsn̩ʃaft/"),
            ), emptyList(), "de"))
    }

    @Test
    fun germanCompoundsWithDifferentModifiersShareAnIdentityHead() {
        val splitter = GermanDecompounder { it in setOf("straße", "seite", "land") }
        assertEquals(setOf("Seitenstraße"), CompoundRhymeFilter.excludedWords(
            listOf(entry("Seitenstraße", "/ˈzaɪtn̩ˌʃtʁaːsə/")),
            listOf(entry("Landstraße", "/ˈlantˌʃtʁaːsə/")), "de", splitter::decompound))
    }

    @Test
    fun invalidPronunciationsDoNotExcludeOtherwiseValidCandidates() {
        assertEquals(emptySet<String>(), CompoundRhymeFilter.excludedWords(
            listOf(entry("checkpoint", "/ˈtʃɛkpɔɪnt/"), entry("invalid", "/☃/")),
            listOf(entry("point", "/☃/")), "en"))
    }

    @Test
    fun endingWithoutARimeDoesNotExcludeACompound() {
        assertEquals(emptySet<String>(), CompoundRhymeFilter.excludedWords(
            listOf(entry("ohmm", "/ˈohm/")), listOf(entry("hmm", "/hm/")), "en"))
    }

    @Test
    fun sharedSpellingWithADifferentPronunciationIsKept() {
        assertEquals(emptySet<String>(), CompoundRhymeFilter.excludedWords(
            listOf(entry("checkpoint", "/ˈtʃɛkpɔɪnt/")),
            listOf(entry("point", "/ˈpɪnt/")), "en"))
    }

    @Test
    fun prefixMustContainAVowelToCountAsARepeatedCompoundEnding() {
        assertEquals(emptySet<String>(), CompoundRhymeFilter.excludedWords(
            listOf(entry("repoint", "/ˈpɔɪnt/"), entry("overat", "/æt/")),
            listOf(entry("point", "/ˈpɔɪnt/"), entry("at", "/æt/")), "en"))
    }

    @Test
    fun anyMatchingPronunciationOfTheStandaloneEndingCanExcludeACompound() {
        assertEquals(setOf("checkpoint"), CompoundRhymeFilter.excludedWords(
            listOf(entry("checkpoint", "/ˈtʃɛkpɔɪnt/")),
            listOf(entry("point", "/ˈpɪnt/"), entry("point", "/ˈpɔɪnt/")), "en"))
    }
}
