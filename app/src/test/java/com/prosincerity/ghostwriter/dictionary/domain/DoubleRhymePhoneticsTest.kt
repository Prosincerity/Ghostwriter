package com.prosincerity.ghostwriter.dictionary.domain

import org.junit.Assert.*
import org.junit.Test

class DoubleRhymePhoneticsTest {
    @Test fun matcherParsesEachPronunciationOnlyOnceIncludingInvalidInput() {
        val calls = mutableMapOf<String, Int>()
        val query = "/ˈbandə/"
        val candidate = "/ˈkantə/"
        val matcher = DoubleRhymePhonetics.Matcher(listOf(query, query)) {
            calls[it] = calls.getOrDefault(it, 0) + 1
            DoubleRhymePhonetics.keys(it)
        }
        repeat(3) {
            assertTrue(matcher.matchesVowels(candidate))
            assertEquals(1, matcher.distance(candidate))
            assertFalse(matcher.matchesVowels("☃"))
            assertNull(matcher.distance("☃"))
        }
        assertEquals(mapOf(query to 1, candidate to 1, "☃" to 1), calls)
    }
    @Test fun ranksExactBeforeVoicingBeforeLooseMatches() {
        assertEquals(0, DoubleRhymePhonetics.distance(listOf("/ˈbandə/"), "/ˈʃandə/"))
        assertEquals(1, DoubleRhymePhonetics.distance(listOf("/ˈʃtʁaːsən/"), "/ˈnaːzən/"))
        assertEquals(1, DoubleRhymePhonetics.distance(listOf("/ˈbandə/"), "/ˈkantə/"))
        assertEquals(2, DoubleRhymePhonetics.distance(listOf("/ˈbandə/"), "/ˈkapə/"))
        assertNull(DoubleRhymePhonetics.distance(listOf("/ˈbandə/"), "/ˈbʊntə/"))
    }
    @Test fun recognizesSyllabicConsonantsAndDiphthongVariants() {
        assertEquals(2, DoubleRhymePhonetics.distance(listOf("/ˈblaɪ̯bə/"), "/ˈzaɪtn̩/"))
        assertTrue("aɪ̯" in DoubleRhymePhonetics.indexKeys(listOf("/ˈblaɪbə/")))
        assertTrue("ə aɪ" in DoubleRhymePhonetics.indexKeys(listOf("/ˈzaɪtn̩/")))
    }
    @Test fun rejectsInvalidOrUnstressedAmbiguousPronunciations() {
        assertNull(DoubleRhymePhonetics.distance(listOf("☃", "bandə"), "/ˈkantə/"))
        assertNull(DoubleRhymePhonetics.distance(emptyList(), "/ˈkantə/"))
        assertNull(DoubleRhymePhonetics.distance(listOf("/ˈbandə/"), "☃"))
        assertEquals(emptySet<String>(), DoubleRhymePhonetics.indexKeys(listOf("☃")))
    }

    @Test fun matchesEspeakOpenAWithWiktionaryOpenA() {
        assertEquals(2, DoubleRhymePhonetics.distance(listOf("/ˈʃtʁaːsə/"), "nˈɑːmə"))
        val keys = DoubleRhymePhonetics.indexKeys(listOf("/ˈʃtʁaːsə/"))
        assertTrue("ə aː" in keys)
        assertTrue("ə ɑː" in keys)
    }
}
