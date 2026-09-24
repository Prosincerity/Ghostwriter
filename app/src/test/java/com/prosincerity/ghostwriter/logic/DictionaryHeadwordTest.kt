package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionaryHeadwordTest {
    @Test
    fun appliesProducerNormalization() {
        assertEquals("d'accord", DictionaryHeadword.normalize("d’accord"))
        assertEquals("May-December", DictionaryHeadword.normalize("May–December"))
        assertEquals("CO2", DictionaryHeadword.normalize("CO₂"))
        assertEquals("software", DictionaryHeadword.normalize("soft\u00adware"))
        assertEquals("", DictionaryHeadword.normalize("\u00ad"))
    }

    @Test
    fun acceptsProducerEligibleHeadwords() {
        listOf("t.b.a", "rock&roll", "'cause", "'Merica", "Hawaiʻian", "AC/DC", "100%", "C++",
            "mother-in-law", "don't", "losin'", "7").forEach {
            assertTrue(it, DictionaryHeadword.eligible(it))
        }
    }

    @Test
    fun rejectsProducerIneligibleHeadwords() {
        listOf("", "ü", "ß", "zyg-", "Dr.", "a..b", "a-7", "rock&", "tw*t", "email@example.com",
            "AC/", "/DC", "100%off", "%100", "C+17", "+C", "two words", "snow☃man").forEach {
            assertFalse(it, DictionaryHeadword.eligible(it))
        }
    }
}
