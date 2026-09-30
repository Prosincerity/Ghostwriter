package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Test

class AssonanceFormFilterTest {
    @Test
    fun equallyShortInfinitivesKeepTheFirstReturnedSpelling() {
        val results = listOf(
            AssonanceFormFilter.Entry("Kaufen", "/kaʊfən/"),
            AssonanceFormFilter.Entry("kaufen", "/kaʊfən/"),
            AssonanceFormFilter.Entry("kauf", "/kaʊf/"),
        )

        assertEquals(setOf("kaufen", "kauf"), AssonanceFormFilter.excludedWords(results, "de"))
        assertEquals(setOf("Kaufen", "kauf"), AssonanceFormFilter.excludedWords(results.reversed(), "de"))
    }

    @Test
    fun unsupportedLanguagesAndInvalidPronunciationsStayUnfiltered() {
        val results = listOf(
            AssonanceFormFilter.Entry("work", "/wɜrk/"),
            AssonanceFormFilter.Entry("working", "/wɜrkɪŋ/"),
            AssonanceFormFilter.Entry("works", "☃"),
        )

        assertEquals(setOf("working"), AssonanceFormFilter.excludedWords(results, "en"))
        assertEquals(emptySet<String>(), AssonanceFormFilter.excludedWords(results, "tr"))
    }

    @Test
    fun keepsPronouncedBaseOfGermanPrefixFamilyRegardlessOfResultOrder() {
        val results = listOf(
            AssonanceFormFilter.Entry("erblondendem", "[ɛɐ̯ˈblɔndn̩dəm]"),
            AssonanceFormFilter.Entry("erblondende", "[ɛɐ̯ˈblɔndn̩də]"),
            AssonanceFormFilter.Entry("erblonden", "[ɛɐ̯ˈblɔndn̩]"),
            AssonanceFormFilter.Entry("erblonde", "[ɛɐ̯ˈblɔndə]"),
            AssonanceFormFilter.Entry("erborgendem", "[ɛɐ̯ˈbɔʁɡn̩dəm]"),
            AssonanceFormFilter.Entry("erborgende", "[ɛɐ̯ˈbɔʁɡn̩də]"),
            AssonanceFormFilter.Entry("erborgtem", "[ɛɐ̯ˈbɔʁktəm]"),
            AssonanceFormFilter.Entry("erborgte", "[ɛɐ̯ˈbɔʁktə]"),
            AssonanceFormFilter.Entry("erborge", "[ɛɐ̯ˈbɔʁɡə]"),
            AssonanceFormFilter.Entry("erborget", "[ɛɐ̯ˈbɔʁɡət]"),
            AssonanceFormFilter.Entry("erborgtet", "[ɛɐ̯ˈbɔʁktət]"),
            AssonanceFormFilter.Entry("erborgen", "[ɛɐ̯ˈbɔʁɡn̩]"),
            AssonanceFormFilter.Entry("erborg", "[ɛɐ̯ˈbɔʁk]"),
            AssonanceFormFilter.Entry("erblond", "[ɛɐ̯ˈblɔnt]"),
        )

        val excluded = setOf("erblond", "erblonde", "erblondendem", "erblondende",
            "erborgendem", "erborgende", "erborgtem", "erborgte", "erborge",
            "erborget", "erborgtet", "erborg")
        assertEquals(excluded, AssonanceFormFilter.excludedWords(results, "de"))
        assertEquals(excluded, AssonanceFormFilter.excludedWords(results.reversed(), "de"))
    }

    @Test
    fun keepsGermanInfinitiveWhenImperativeAndOtherFormsAreReturned() {
        val results = listOf(
            AssonanceFormFilter.Entry("kaufe", "/kaʊfə/"),
            AssonanceFormFilter.Entry("kauf", "/kaʊf/"),
            AssonanceFormFilter.Entry("kaufen", "/kaʊfən/"),
        )

        assertEquals(setOf("kaufe", "kauf"), AssonanceFormFilter.excludedWords(results, "de"))
        assertEquals(setOf("kaufe", "kauf"), AssonanceFormFilter.excludedWords(results.reversed(), "de"))
    }

    @Test
    fun keepsShortIrregularGermanInfinitives() {
        val results = listOf(
            AssonanceFormFilter.Entry("tu", "/tuː/"),
            AssonanceFormFilter.Entry("tun", "/tuːn/"),
            AssonanceFormFilter.Entry("sei", "/zaɪ/"),
            AssonanceFormFilter.Entry("sein", "/zaɪn/"),
        )

        assertEquals(setOf("tu", "sei"), AssonanceFormFilter.excludedWords(results, "de"))
    }

    @Test
    fun groupsReleasedLndeLteNsteAndNdeForms() {
        val results = listOf(
            AssonanceFormFilter.Entry("erdrosselnde", "[ɛɐ̯ˈdʁɔsl̩ndə]"),
            AssonanceFormFilter.Entry("erdrosselte", "[ɛɐ̯ˈdʁɔsl̩tə]"),
            AssonanceFormFilter.Entry("verdoppelnde", "[fɛɐ̯ˈdɔpl̩ndə]"),
            AssonanceFormFilter.Entry("verborgenste", "[fɛɐ̯ˈbɔʁɡn̩stə]"),
            AssonanceFormFilter.Entry("erfolgende", "[ɛɐ̯ˈfɔlɡn̩də]"),
            AssonanceFormFilter.Entry("erdrossel", "[ɛɐ̯ˈdʁɔsl̩]"),
            AssonanceFormFilter.Entry("erdrosseln", "[ɛɐ̯ˈdʁɔsl̩n]"),
            AssonanceFormFilter.Entry("verdoppel", "[fɛɐ̯ˈdɔpl̩]"),
            AssonanceFormFilter.Entry("verdoppeln", "[fɛɐ̯ˈdɔpl̩n]"),
            AssonanceFormFilter.Entry("verborgen", "[fɛɐ̯ˈbɔʁɡn̩]"),
            AssonanceFormFilter.Entry("erfolg", "[ɛɐ̯ˈfɔlk]"),
            AssonanceFormFilter.Entry("erfolgen", "[ɛɐ̯ˈfɔlɡn̩]"),
        )

        assertEquals(setOf("erdrosselnde", "erdrosselte", "verdoppelnde",
            "verborgenste", "erfolgende", "erdrossel", "verdoppel", "erfolg"),
            AssonanceFormFilter.excludedWords(results, "de"))
    }

    @Test
    fun keepsDifferentWordsAndPronunciations() {
        val results = listOf(
            AssonanceFormFilter.Entry("Leite", "/laɪtə/"),
            AssonanceFormFilter.Entry("Leiter", "/laɪtɐ/"),
            AssonanceFormFilter.Entry("Haus", "/haʊs/"),
            AssonanceFormFilter.Entry("Hausen", "/haʊzən/"),
            AssonanceFormFilter.Entry("erblondehaus", "/ɛɐblɔndəhaʊs/"),
            AssonanceFormFilter.Entry("erblonde", "/ɛɐblɔndə/"),
        )

        assertEquals(emptySet<String>(), AssonanceFormFilter.excludedWords(results, "de"))
    }

    @Test
    fun keepsShortestAvailableFormWhenTheBaseIsAbsent() {
        val results = listOf(
            AssonanceFormFilter.Entry("erblondendem", "[ɛɐ̯ˈblɔndn̩dəm]"),
            AssonanceFormFilter.Entry("erblondende", "[ɛɐ̯ˈblɔndn̩də]"),
            AssonanceFormFilter.Entry("erblonden", "[ɛɐ̯ˈblɔndn̩]"),
        )

        assertEquals(setOf("erblondendem", "erblondende"),
            AssonanceFormFilter.excludedWords(results, "de"))
    }

    @Test
    fun doesNotTreatLongerParticipialEndingAsAnInfinitive() {
        val results = listOf(
            AssonanceFormFilter.Entry("erblondenden", "[ɛɐ̯ˈblɔndn̩dən]"),
            AssonanceFormFilter.Entry("erblondende", "[ɛɐ̯ˈblɔndn̩də]"),
        )

        assertEquals(setOf("erblondenden"), AssonanceFormFilter.excludedWords(results, "de"))
    }

    @Test
    fun keepsUnrelatedWordsThatOnlyShareShortPrefixes() {
        val results = listOf(
            AssonanceFormFilter.Entry("erfolgen", "/ɛɐfɔlɡən/"),
            AssonanceFormFilter.Entry("erborgen", "/ɛɐbɔʁɡən/"),
            AssonanceFormFilter.Entry("erbrochen", "/ɛɐbʁɔxən/"),
        )

        assertEquals(emptySet<String>(), AssonanceFormFilter.excludedWords(results, "de"))
    }

    @Test
    fun recognizesReturnedPrefixWithoutAListedInflectionEnding() {
        val results = listOf(
            AssonanceFormFilter.Entry("walkwise", "/wɔkwaɪz/"),
            AssonanceFormFilter.Entry("walk", "/wɔk/"),
        )

        assertEquals(setOf("walkwise"), AssonanceFormFilter.excludedWords(results, "en"))
    }

    @Test
    fun keepsEnglishBaseWhenInflectedFormsArePresent() {
        val results = listOf(
            AssonanceFormFilter.Entry("working", "/wɜrkɪŋ/"),
            AssonanceFormFilter.Entry("works", "/wɜrks/"),
            AssonanceFormFilter.Entry("work", "/wɜrk/"),
        )

        assertEquals(setOf("working", "works"), AssonanceFormFilter.excludedWords(results, "en"))
        assertEquals(setOf("working", "works"), AssonanceFormFilter.excludedWords(results.reversed(), "en"))
    }
}
