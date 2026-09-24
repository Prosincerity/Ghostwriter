package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
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
    fun prefixesRespectWholeTokensAndVowelBoundaries() {
        val keys = IpaSearchKeys.fromIpa("/ˈkæt/", "en")!!
        assertEquals(listOf("tækˈ", "tæk", "tæ"), IpaSearchKeys.rhymePrefixes(keys, "en"))
        assertEquals(listOf("æ"), IpaSearchKeys.assonancePrefixes(keys))
        assertNull(IpaSearchKeys.fromIpa("/ˈa☃̃/", "en"))
        assertNull(IpaSearchKeys.fromIpa("/a/", "fr"))
    }
}
