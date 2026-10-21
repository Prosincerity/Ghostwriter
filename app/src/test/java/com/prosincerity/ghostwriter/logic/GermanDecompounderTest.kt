package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Test

class GermanDecompounderTest {
    private val lexicon = setOf("straße", "sperre", "herr", "schuh", "wissen", "schaft", "kolleg",
        "hund", "hütte", "hand", "hinter", "ziehung", "arbeit", "zimmer", "kind", "garten",
        "geburt", "tag", "feier", "schule", "fest", "donau", "dampf", "schiff", "fahrt")
    private val splitter = GermanDecompounder { it in lexicon }

    @Test fun preservesSurfaceAndSeparatesLinkingMorphemes() {
        for ((word, surfaces, lemmas, links) in listOf(
            listOf("Straßensperre", "Straßen|sperre", "straße|sperre", "n|"),
            listOf("Herrenschuh", "Herren|schuh", "herr|schuh", "en|"),
            listOf("Wissenschaftskolleg", "Wissenschafts|kolleg", "wissenschaft|kolleg", "s|"),
            listOf("Hundehütte", "Hunde|hütte", "hund|hütte", "e|"),
            listOf("Arbeitszimmer", "Arbeits|zimmer", "arbeit|zimmer", "s|"),
            listOf("Kindergarten", "Kinder|garten", "kind|garten", "er|"),
        )) {
            val parts = splitter.decompound(word)
            assertEquals(word, surfaces, parts.joinToString("|") { it.surface })
            assertEquals(word, lemmas, parts.joinToString("|") { it.lemma })
            assertEquals(word, links, parts.joinToString("|") { it.linking })
            assertEquals(word, parts.joinToString("") { it.surface })
        }
    }

    @Test fun lexicalizedWordsAndDerivationsStayWhole() {
        for (word in listOf("Handschuh", "Hinterziehung", "Wissenschaft", "Anwaltschaft")) {
            assertEquals(listOf(word), splitter.decompound(word).map { it.surface })
        }
    }

    @Test fun recursivelySplitsModifiersAndKeepsTheLongestHead() {
        assertEquals(listOf("Geburts", "tags", "feier"), splitter.decompound("Geburtstagsfeier").map { it.surface })
        val longest = GermanDecompounder { it in lexicon || it == "dampfschifffahrt" }
        assertEquals(listOf("Donau", "dampfschifffahrt"), longest.decompound("Donaudampfschifffahrt").map { it.surface })
    }

    @Test fun supportsTruncatedStems() {
        assertEquals(listOf("schule", "fest"), splitter.decompound("Schulfest").map { it.lemma })
    }

    @Test fun requiresCompleteLexicalEvidenceAndBoundsInput() {
        for (word in listOf("", "Unbekanntschuh", "xxschuh", "Hund Hütte", "a".repeat(97))) {
            assertEquals(listOf(word), splitter.decompound(word).map { it.surface })
        }
        assertEquals(emptySet<String>(), GermanDecompounder.vocabulary("a".repeat(97)))
    }
}
