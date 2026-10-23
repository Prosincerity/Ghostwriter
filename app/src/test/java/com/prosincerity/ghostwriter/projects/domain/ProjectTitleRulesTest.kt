package com.prosincerity.ghostwriter.projects.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class ProjectTitleRulesTest {
    @Test fun normalize_blankInputUsesUntitled() {
        for (input in listOf("", " \t\n")) {
            assertEquals("Untitled", ProjectTitleRules.normalize(input))
        }
    }

    @Test fun normalize_trimsOnlyOuterWhitespaceAndPreservesDisplayCharacters() {
        val title = "Verse / chorus: 🎵 " + "字".repeat(300)
        assertEquals(title, ProjectTitleRules.normalize(" \t$title\n "))
    }

    @Test fun resolve_usesFirstMatchingTitleWithItsOriginalCasing() {
        assertEquals("Alpha", ProjectTitleRules.resolve(" alpha ", listOf("Alpha", "ALPHA")))
    }

    @Test fun resolve_unmatchedTitleKeepsItsNormalizedDisplayText() {
        assertEquals("New / track", ProjectTitleRules.resolve(" New / track ", listOf("Other")))
    }

    @Test fun creation_blankInputOpensExistingUntitledProject() {
        assertEquals(ProjectTitleState.Existing("UNTITLED"),
            ProjectTitleRules.forCreation(" \t", listOf("UNTITLED")))
    }

    @Test fun creation_emptyProjectListOffersUntitled() {
        assertEquals(ProjectTitleState.Available("Untitled"), ProjectTitleRules.forCreation("", emptyList()))
    }

    @Test fun creation_duplicateOpensExistingTitleWithItsOriginalCasing() {
        assertEquals(ProjectTitleState.Existing("Alpha"),
            ProjectTitleRules.forCreation(" alpha ", listOf("Alpha", "Beta")))
    }

    @Test fun creation_partialMatchesRemainAvailable() {
        assertEquals(ProjectTitleState.Available("Alpha"),
            ProjectTitleRules.forCreation("Alpha", listOf("Alpha two", "Alphabet")))
    }

    @Test fun rename_excludesCurrentTitleAndAllowsChangingItsCasing() {
        assertEquals(ProjectTitleState.Available("alpha"),
            ProjectTitleRules.forRename(" alpha ", "Alpha", listOf("Alpha", "Beta")))
    }

    @Test fun rename_unchangedTitleRemainsAvailable() {
        assertEquals(ProjectTitleState.Available("Alpha"),
            ProjectTitleRules.forRename("Alpha", "Alpha", listOf("Alpha")))
    }

    @Test fun rename_rejectsAnotherTitleRegardlessOfCasing() {
        assertEquals(ProjectTitleState.Existing("beta"),
            ProjectTitleRules.forRename(" beta ", "Alpha", listOf("Alpha", "Beta")))
    }

    @Test fun rename_excludesOnlyExactCurrentTitleWhenCaseVariantsExist() {
        assertEquals(ProjectTitleState.Existing("alpha"),
            ProjectTitleRules.forRename("alpha", "Alpha", listOf("Alpha", "ALPHA")))
    }

    @Test fun rename_blankInputChecksUntitledForConflicts() {
        assertEquals(ProjectTitleState.Existing("Untitled"),
            ProjectTitleRules.forRename(" \t", "Alpha", listOf("Alpha", "Untitled")))
    }

    @Test fun rename_newTitlePreservesDisplayCharacters() {
        assertEquals(ProjectTitleState.Available("Verse / chorus"),
            ProjectTitleRules.forRename(" Verse / chorus ", "Alpha", emptyList()))
    }
}
