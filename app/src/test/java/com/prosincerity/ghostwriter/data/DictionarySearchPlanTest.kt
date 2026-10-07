package com.prosincerity.ghostwriter.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionarySearchPlanTest {
    @Test
    fun rhymesPreferLongerRimesThenLongerEncodedKeys() {
        val plan = plan(DictionarySearchMode.RHYME, "/ˈkat/", "/ˈkaːt/", "/ˈkata/", "/ˈbat/")

        assertEquals(listOf("ata", "taː", "ta"), plan.prefixes.map { it.value })
        assertEquals(listOf("a", "t", "a"), plan.prefixes.first().tokens)
        assertTrue(plan.assonanceFallbacks.isEmpty())
    }

    @Test
    fun suffixesMergePronunciationsIntoLongestFirstDistinctStages() {
        val plan = plan(DictionarySearchMode.WORD_SUFFIX, "/ˈkatɪŋab/", "/katɪˈŋab/", "/ab/")

        assertEquals(listOf("baŋɪt", "baŋ", "ba"), plan.prefixes.map { it.value })
        assertEquals(listOf("t", "ɪ", "ŋ", "a", "b"), plan.prefixes.first().tokens)
        assertTrue(plan.assonanceFallbacks.isEmpty())
    }

    @Test
    fun exactAssonanceUsesShorterKeysBeforeLongerKeysAndIgnoresConsonantOnlyIpa() {
        val plan = plan(DictionarySearchMode.ASSONANCE, "/a.e.i.o/", "/e.i.o/", "/oː/", "/o/", "/b/")

        assertEquals(listOf("o", "oː", "o i e", "o i e a"), plan.prefixes.map { it.value })
    }

    @Test
    fun assonanceFallbacksMergeDistinctTiersAndStopAtTwoVowels() {
        val plan = plan(DictionarySearchMode.ASSONANCE, "/a.e.i.o.u/", "/e.i.o.u/", "/o.u/", "/u/")

        assertEquals(listOf("u o i e", "u o i", "u o"), plan.assonanceFallbacks)
        assertEquals(listOf("u", "u o", "u o i e", "u o i e a"), plan.prefixes.map { it.value })
    }

    @Test
    fun equalRankedAssonanceKeysKeepPronunciationOrder() {
        val plan = plan(DictionarySearchMode.ASSONANCE, "/a.e.i/", "/a.e.o/", "/a.e.i/")

        assertEquals(listOf("i e a", "o e a"), plan.prefixes.map { it.value })
        assertEquals(listOf("i e", "o e"), plan.assonanceFallbacks)
    }

    @Test
    fun wordPrefixesNeedNoPhoneticStages() {
        val plan = plan(DictionarySearchMode.WORD_PREFIX, "/a.e.i.o/", "/ˈkat/")

        assertTrue(plan.prefixes.isEmpty())
        assertTrue(plan.assonanceFallbacks.isEmpty())
    }

    @Test
    fun missingOrInvalidPronunciationsProduceNoStages() {
        DictionarySearchMode.entries.forEach { mode ->
            listOf(emptyList(), listOf("", "/☃/", "/ˈ/")).forEach { ipa ->
                val plan = DictionarySearchPlan.fromIpa(ipa, "de", mode)
                assertTrue("$mode $ipa", plan.prefixes.isEmpty())
                assertTrue("$mode $ipa", plan.assonanceFallbacks.isEmpty())
            }
        }
    }

    private fun plan(mode: DictionarySearchMode, vararg ipa: String): DictionarySearchPlan =
        DictionarySearchPlan.fromIpa(ipa.toList(), "de", mode)
}
