package com.prosincerity.ghostwriter.logic

import org.junit.Assert.*
import org.junit.Test

class DoubleRhymePhoneticsTest {
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
