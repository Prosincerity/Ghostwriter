package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertNull
import org.junit.Test

class IpaSearchKeysTest {
    @Test
    fun matchesProducerTokenFixtures() {
        val fixtures = listOf(
            Triple("en", "[ˈtʃɝːtʃ]", listOf("ˈ", "tʃ", "ɝː", "tʃ")),
            Triple("en", "/ˈkʰæt̚/", listOf("ˈ", "kʰ", "æ", "t̚")),
            Triple("de", "/ˈkat͡sə/", listOf("ˈ", "k", "a", "t͡s", "ə")),
            Triple("de", "/tyːɐ̯/", listOf("t", "yː", "ɐ̯")),
            Triple("tr", "/t͡ʃoˈd͡ʒuːk/", listOf("t͡ʃ", "o", "ˈ", "d͡ʒ", "uː", "k")),
            Triple("tr", "/áː/", listOf("áː")),
            Triple("tr", "/ʃe.ɾi.ˈˤat/", listOf("ʃ", "e", "ɾ", "i", "ˈ", "ˤa", "t")),
            Triple("en", "[ˈkɪtn ̩]", listOf("ˈ", "k", "ɪ", "t", "n̩")),
            Triple("de", "[kɪnt⁀ʊnt ‖ ↗a]", listOf("k", "ɪ", "n", "t", "ʊ", "n", "t", "↗", "a")),
            Triple("en", "⫽tai̯²⁴⁻²¹ p⁽ʲ⁾il↗︎⫽", listOf("t", "a", "i̯", "²", "⁴", "⁻", "²", "¹", "pʲ", "i", "l", "↗")),
        )
        fixtures.forEach { (language, ipa, expected) ->
            assertEquals("$language $ipa", expected, IpaSearchKeys.fromIpa(ipa, language)?.tokens)
        }
    }

    @Test
    fun reversedKeysMatchProducerFixtures() {
        val examples = listOf(
            Triple("en", "/ˈkæt/", "tækˈ" to "æ"),
            Triple("de", "/ˈhaʊs/", "saʊhˈ" to "aʊ"),
            Triple("tr", "/biˈlec/", "celˈib" to "e i"),
            Triple("en", "/ˈfoʊtoʊˈgræf/", "færgˈoʊtoʊfˈ" to "æ oʊ oʊ"),
        )
        examples.forEach { (language, ipa, expected) ->
            val keys = IpaSearchKeys.fromIpa(ipa, language)
            assertEquals(expected.first, keys?.reversed)
            assertEquals(expected.second, keys?.assonance)
        }
    }

    @Test
    fun rimeStartsAtPrimaryStressedVowel() {
        val keys = IpaSearchKeys.fromIpa("/ˈkæt/", "en")!!
        assertEquals(listOf("æ", "t"), IpaSearchKeys.rimeTokens(keys, "en"))
        assertEquals(listOf("aɪ", "t", "ɪ", "ŋ"),
            IpaSearchKeys.rimeTokens(IpaSearchKeys.fromIpa("/ˈɹaɪtɪŋ/", "en")!!, "en"))
        assertEquals(listOf("ɪ", "ŋ"),
            IpaSearchKeys.rimeTokens(IpaSearchKeys.fromIpa("/ɹaɪˈtɪŋ/", "en")!!, "en"))
        assertEquals(emptyList<String>(), IpaSearchKeys.assonanceFallbacks(keys))
        assertEquals(listOf("æ", "t"),
            IpaSearchKeys.rimeTokens(IpaSearchKeys.fromIpa("/kæt/", "en")!!, "en"))
        assertNull(IpaSearchKeys.rimeTokens(IpaSearchKeys.fromIpa("/kætɪŋ/", "en")!!, "en"))
        assertNull(IpaSearchKeys.fromIpa("/ˈa☃̃/", "en"))
        assertNull(IpaSearchKeys.fromIpa("/a/", "fr"))
    }

    @Test
    fun exactRimeRejectsSharedUnstressedEnding() {
        fun rime(ipa: String) = IpaSearchKeys.rimeTokens(IpaSearchKeys.fromIpa(ipa, "en")!!, "en")

        assertEquals(rime("/ˈɹaɪtɪŋ/"), rime("/ˈlaɪtɪŋ/"))
        assertNotEquals(rime("/ˈɹaɪtɪŋ/"), rime("/ˈsɪŋ/"))
        assertNotEquals(rime("/ˈɹaɪtɪŋ/"), rime("/ˈbeɪtɪŋ/"))
    }

    @Test
    fun unstressedGermanMonosyllablesUseTheirOnlyVowel() {
        fun rime(ipa: String) = IpaSearchKeys.rimeTokens(IpaSearchKeys.fromIpa(ipa, "de")!!, "de")

        assertEquals(listOf("a", "f", "t"), rime("/kʁaft/"))
        assertEquals(rime("/kʁaft/"), rime("/zaft/"))
        assertEquals(rime("/kʁaft/"), rime("[ʃaft]"))
        assertEquals(rime("/kʁaft/"), rime("[paft]"))
    }

    @Test
    fun suffixStagesKeepWholeShortWordsAndStopBeforeTwoForLongWords() {
        val writing = IpaSearchKeys.fromIpa("/ˈɹaɪtɪŋ/", "en")!!
        assertEquals(listOf(listOf("t", "ɪ", "ŋ")), IpaSearchKeys.phonemeSuffixes(writing))
        val longWord = IpaSearchKeys.fromIpa("/ˈkatɪŋab/", "en")!!
        assertEquals(listOf(5, 3), IpaSearchKeys.phonemeSuffixes(longWord).map { it.size })
        assertEquals(listOf(listOf("a")), IpaSearchKeys.phonemeSuffixes(IpaSearchKeys.fromIpa("/a/", "en")!!))
        assertEquals(listOf(listOf("æ", "t")), IpaSearchKeys.phonemeSuffixes(IpaSearchKeys.fromIpa("/æt/", "en")!!))
        assertEquals(listOf(listOf("p", "ɔɪ", "n", "t")), IpaSearchKeys.phonemeSuffixes(
            IpaSearchKeys.fromIpa("/pɔɪnt/", "en")!!))
        assertEquals(listOf(listOf("f", "l", "æ", "t")), IpaSearchKeys.phonemeSuffixes(
            IpaSearchKeys.fromIpa("/flæt/", "en")!!))
        assertEquals(listOf(listOf("k", "æ", "t")), IpaSearchKeys.phonemeSuffixes(
            IpaSearchKeys.fromIpa("/kæt/", "en")!!))
        assertEquals(listOf(listOf("ʊ", "n", "t")), IpaSearchKeys.phonemeSuffixes(
            IpaSearchKeys.fromIpa("/ʊnt/", "de")!!))
    }

    @Test
    fun assonanceFallbacksDropOneVowelAtATimeThroughTwo() {
        fun fallbacks(ipa: String) =
            IpaSearchKeys.assonanceFallbacks(IpaSearchKeys.fromIpa(ipa, "en")!!)

        assertEquals(emptyList<String>(), fallbacks("/b/"))
        assertEquals(emptyList<String>(), fallbacks("/a/"))
        assertEquals(emptyList<String>(), fallbacks("/ae/"))
        assertEquals(listOf("i e"), fallbacks("/aei/"))
        assertEquals(listOf("o i e", "o i"), fallbacks("/aeio/"))
        assertEquals(listOf("u o i e", "u o i", "u o"), fallbacks("/aeiou/"))
        assertEquals(listOf("æ u o i e", "æ u o i", "æ u o", "æ u"), fallbacks("/aeiouæ/"))
        assertEquals(listOf("ɒ æ u o i e", "ɒ æ u o i", "ɒ æ u o", "ɒ æ u", "ɒ æ"),
            fallbacks("/aeiouæɒ/"))
    }

    @Test
    fun assonanceFallbacksKeepDiphthongsAndVowelModifiersIntact() {
        val keys = IpaSearchKeys.fromIpa("/a.eɪ.oʊ.uː/", "en")!!
        assertEquals(listOf("uː oʊ eɪ", "uː oʊ"), IpaSearchKeys.assonanceFallbacks(keys))
    }
}
