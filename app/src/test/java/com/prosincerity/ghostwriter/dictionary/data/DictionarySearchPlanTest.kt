package com.prosincerity.ghostwriter.dictionary.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionarySearchPlanTest {
    @Test
    fun shortMonosyllablesKeepShortStagesInEveryOrdinaryPhoneticMode() {
        for (language in listOf("en", "de", "tr")) {
            for ((mode, expected) in listOf(
                DictionarySearchMode.RHYME to "ta",
                DictionarySearchMode.WORD_SUFFIX to "tak",
                DictionarySearchMode.ASSONANCE to "a",
            )) {
                val plan = DictionarySearchPlan.fromIpa(listOf("/ˈkat/"), language, mode)
                assertEquals("$language $mode", listOf(expected), plan.prefixes.map { it.value })
            }
        }
    }

    @Test
    fun multiVowelAssonanceNeverFallsBackToOneVowel() {
        for (language in listOf("en", "de", "tr")) {
            val plan = DictionarySearchPlan.fromIpa(listOf("/a.e.i.o/"), language, DictionarySearchMode.ASSONANCE)
            assertEquals(listOf("o i e a"), plan.prefixes.map { it.value })
            assertEquals(listOf("o i e", "o i"), plan.assonanceFallbacks)
        }
    }

    @Test
    fun longInputsCannotCreateTinyRhymeStages() {
        for (language in listOf("en", "de", "tr")) {
            for (ipa in listOf("/kaˈta/", "/kaˈtat/", "/ˈʃtʁap/")) {
                val plan = DictionarySearchPlan.fromIpa(listOf(ipa), language, DictionarySearchMode.RHYME)
                assertTrue("$language $ipa", plan.prefixes.isEmpty())
            }
            assertEquals(listOf("ata"), DictionarySearchPlan.fromIpa(
                listOf("/ˈkata/"), language, DictionarySearchMode.RHYME,
            ).prefixes.map { it.value })
        }
    }

    @Test
    fun multiVowelSuffixStagesKeepAtLeastTwoVowels() {
        val plan = plan(DictionarySearchMode.WORD_SUFFIX, "/ˈkatɪŋab/")

        assertEquals(listOf("baŋɪt", "baŋɪ"), plan.prefixes.map { it.value })
        assertEquals(listOf("ŋɪtaɪ"), DictionarySearchPlan.fromIpa(
            listOf("/ˈɹaɪtɪŋ/"), "en", DictionarySearchMode.WORD_SUFFIX,
        ).prefixes.map { it.value })
    }

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

        assertEquals(listOf("baŋɪt", "baŋɪ", "ba"), plan.prefixes.map { it.value })
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
