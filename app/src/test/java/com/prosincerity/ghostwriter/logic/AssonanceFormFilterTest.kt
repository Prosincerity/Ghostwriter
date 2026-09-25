package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Test

class AssonanceFormFilterTest {
    @Test
    fun groupsReleasedGermanFormsToShortestBaseRegardlessOfResultOrder() {
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
            AssonanceFormFilter.Entry("erborg", "[ɛɐ̯ˈbɔʁk]"),
            AssonanceFormFilter.Entry("erblond", "[ɛɐ̯ˈblɔnt]"),
        )

        assertEquals(setOf("erblonde", "erblondendem", "erblondende", "erblonden",
            "erborgendem", "erborgende", "erborgtem", "erborgte", "erborge",
            "erborget", "erborgtet"),
            AssonanceFormFilter.excludedWords(results, "de"))
    }

    @Test
    fun groupsOtherGermanFamiliesByTheSameRule() {
        val results = listOf(
            AssonanceFormFilter.Entry("verkaufe", "/fɛɐkaʊfə/"),
            AssonanceFormFilter.Entry("Verkauf", "/fɛɐkaʊf/"),
        )

        assertEquals(setOf("verkaufe"), AssonanceFormFilter.excludedWords(results, "de"))
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
            AssonanceFormFilter.Entry("verdoppel", "[fɛɐ̯ˈdɔpl̩]"),
            AssonanceFormFilter.Entry("verborgen", "[fɛɐ̯ˈbɔʁɡn̩]"),
            AssonanceFormFilter.Entry("erfolg", "[ɛɐ̯ˈfɔlk]"),
        )

        assertEquals(setOf("erdrosselnde", "erdrosselte", "verdoppelnde",
            "verborgenste", "erfolgende"), AssonanceFormFilter.excludedWords(results, "de"))
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
    fun keepsEnglishBaseWhenInflectedFormsArePresent() {
        val results = listOf(
            AssonanceFormFilter.Entry("walked", "/wɔkt/"),
            AssonanceFormFilter.Entry("walking", "/wɔkɪŋ/"),
            AssonanceFormFilter.Entry("walk", "/wɔk/"),
        )

        assertEquals(setOf("walked", "walking"), AssonanceFormFilter.excludedWords(results, "en"))
    }
}
