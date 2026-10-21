package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Test

class GermanDecompounderTest {
    private val lexicon = setOf("straße", "sperre", "herr", "schuh", "wissen", "schaft", "kolleg",
        "hund", "hütte", "hand", "hinter", "ziehung", "arbeit", "zimmer", "kind", "garten",
        "wissenschaft", "hinterziehung", "hinterziehen", "anwalt", "anwaltschaft",
        "leidenschaft", "leiden", "verwandtschaft", "verwandt", "freiheit", "frei",
        "freundlichkeit", "freundlich", "rechnung", "rechnen",
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

    @Test fun dictionaryBackedDerivationsStayWhole() {
        // Source-checked derivations: see documentation/DICTIONARIES.md, regression examples.
        for (word in listOf("Hinterziehung", "Wissenschaft", "Anwaltschaft", "Leidenschaft", "Verwandtschaft", "Freiheit", "Freundlichkeit", "Rechnung")) {
            assertEquals(listOf(word), splitter.decompound(word).map { it.surface })
        }
    }

    @Test fun suffixHomographWithIndependentStressRemainsACompound() {
        val words = lexicon + setOf("gewehr", "gewehrschaft")
        val pronunciations = mapOf("gewehrschaft" to listOf("/ɡəˈveːɐ̯ˌʃaft/"), "schaft" to listOf("/ʃaft/"))
        val splitter = GermanDecompounder(pronunciations = { pronunciations[it].orEmpty() }) { it in words }
        assertEquals(listOf("Gewehr", "schaft"), splitter.decompound("Gewehrschaft").map { it.surface })
    }

    @Test fun derivedHeadStaysWholeInsideALargerCompound() {
        val splitter = GermanDecompounder { it in lexicon || it == "steuer" }
        assertEquals(listOf("Steuer", "hinterziehung"), splitter.decompound("Steuerhinterziehung").map { it.surface })
    }

    @Test fun boundCompoundHeadRequiresItsAttestedSourceVerb() {
        val splitter = GermanDecompounder { it in setOf("geisel", "nehmen") }
        assertEquals(listOf("Geisel", "nahme"), splitter.decompound("Geiselnahme").map { it.surface })
        assertEquals(listOf("Geiselnahme"), GermanDecompounder { it == "geisel" }
            .decompound("Geiselnahme").map { it.surface })
    }

    @Test fun recursivelySplitsModifiersAndKeepsTheLongestHead() {
        assertEquals(listOf("Geburts", "tags", "feier"), splitter.decompound("Geburtstagsfeier").map { it.surface })
        val longest = GermanDecompounder { it in lexicon || it == "dampfschifffahrt" }
        assertEquals(listOf("Donau", "dampfschifffahrt"), longest.decompound("Donaudampfschifffahrt").map { it.surface })
    }

    @Test fun handschuhIsACompoundAsDocumentedByWiktionary() {
        // https://de.wiktionary.org/wiki/Handschuh (Herkunft)
        assertEquals(listOf("Hand", "schuh"), splitter.decompound("Handschuh").map { it.surface })
    }

    @Test fun supportsTruncatedStems() {
        assertEquals(listOf("schule", "fest"), splitter.decompound("Schulfest").map { it.lemma })
    }

    @Test fun linkingMorphemesBelongToModifiersAndNotTheFinalHead() {
        val splitter = GermanDecompounder { it in setOf("band", "bande", "straße") }
        val parts = splitter.decompound("Straßenbande")
        assertEquals(listOf("straße", "bande"), parts.map { it.lemma })
        assertEquals(listOf("n", ""), parts.map { it.linking })
    }

    @Test fun requiresCompleteLexicalEvidenceAndBoundsInput() {
        for (word in listOf("", "Unbekanntschuh", "xxschuh", "Hund Hütte", "a".repeat(97))) {
            assertEquals(listOf(word), splitter.decompound(word).map { it.surface })
        }
    }
}
