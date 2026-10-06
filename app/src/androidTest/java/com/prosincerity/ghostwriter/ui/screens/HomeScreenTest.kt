package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.test.performImeAction
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import com.prosincerity.ghostwriter.data.ProjectSummary
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test fun stableIdsUseDisplayTitlesAndActionsKeepTheirIdentity() {
        val id = "2ecbf67a-9271-459f-9e31-77b40b15e1ba"
        var opened: String? = null
        var renamed: Pair<String, String>? = null
        composeRule.setContent {
            GhostwriterTheme {
                HomeScreen(listOf(id), {}, { opened = it }, {}, { old, new -> renamed = old to new }, {},
                    projectTitles = mapOf(id to "Verse / chorus"))
            }
        }
        composeRule.onNodeWithText(id).assertDoesNotExist()
        composeRule.onNodeWithText("Verse / chorus").performClick()
        composeRule.runOnIdle { assertEquals(id, opened) }
        composeRule.onNodeWithContentDescription("Project options for Verse / chorus").performClick()
        composeRule.onNodeWithText("Rename").performClick()
        composeRule.onNode(hasSetTextAction()).performTextReplacement("New / title")
        composeRule.onNodeWithText("Rename").performClick()
        composeRule.runOnIdle { assertEquals(id to "New / title", renamed) }
    }

    @Test fun duplicateNamesAndDialogCancellation_preserveProjects() {
        var created: String? = null
        var renamed: Pair<String, String>? = null
        var deleted: String? = null
        composeRule.setContent {
            GhostwriterTheme {
                HomeScreen(listOf("Alpha", "Beta"), { created = it }, {}, { deleted = it },
                    { old, new -> renamed = old to new }, {})
            }
        }
        composeRule.onNodeWithText("New project").performClick()
        composeRule.onNode(hasSetTextAction()).performTextReplacement("alpha")
        composeRule.onNodeWithText("Track already exists (will open existing)").assertExists()
        composeRule.onNode(hasSetTextAction()).performImeAction()
        composeRule.runOnIdle { assertEquals("Alpha", created) }

        composeRule.onNodeWithText("New project").performClick()
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onNodeWithText("Name this track").assertDoesNotExist()
        composeRule.onNodeWithContentDescription("Project options for Alpha").performClick()
        composeRule.onNodeWithText("Rename").performClick()
        composeRule.onNode(hasSetTextAction()).performTextReplacement("beta")
        composeRule.onNodeWithText("A project with this name already exists").assertExists()
        composeRule.onNodeWithText("Rename").assertIsNotEnabled()
        composeRule.onNode(hasSetTextAction()).performImeAction()
        composeRule.runOnIdle { assertEquals(null, renamed) }
        composeRule.onNode(hasSetTextAction()).performTextReplacement("Gamma")
        composeRule.onNode(hasSetTextAction()).performImeAction()
        composeRule.runOnIdle { assertEquals("Alpha" to "Gamma", renamed) }

        composeRule.onNodeWithContentDescription("Project options for Alpha").performClick()
        composeRule.onNodeWithText("Rename").performClick()
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.onNodeWithContentDescription("Project options for Alpha").performClick()
        composeRule.onNodeWithText("Delete").performClick()
        composeRule.onNodeWithText("Cancel").performClick()
        composeRule.runOnIdle { assertEquals(null, deleted) }
    }

    @Test fun summaryUpdates_resortProjectsAndMissingSummariesSortAlphabetically() {
        var summaries by mutableStateOf(emptyMap<String, ProjectSummary>())
        composeRule.setContent {
            GhostwriterTheme { HomeScreen(listOf("Zulu", "Alpha"), {}, {}, {}, { _, _ -> }, {}, summaries) }
        }
        fun top(title: String) = composeRule.onNodeWithText(title).fetchSemanticsNode().boundsInRoot.top
        assertTrue(top("Alpha") < top("Zulu"))
        composeRule.runOnIdle { summaries = mapOf("Zulu" to ProjectSummary("Zulu", 2000)) }
        assertTrue(top("Zulu") < top("Alpha"))
    }

    @Test
    fun emptyHome_createsNamedProjectAndOpensSettings() {
        var createdProject: String? = null
        var openedSettings = false

        composeRule.setContent {
            GhostwriterTheme {
                HomeScreen(
                    existingProjects = emptyList(),
                    onCreateProject = { createdProject = it },
                    onOpenProject = {},
                    onDeleteProject = {},
                    onRenameProject = { _, _ -> },
                    onOpenSettings = { openedSettings = true },
                )
            }
        }

        composeRule.onNodeWithText("Import (coming soon)").assertDoesNotExist()
        composeRule.onNodeWithText("Start your next track").assertExists()
        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.onNodeWithText("New project").performClick()
        composeRule.onNodeWithText("Name this track").assertExists()
        composeRule.onNode(hasSetTextAction()).performTextInput("New Track")
        composeRule.onNodeWithText("Create").performClick()

        composeRule.runOnIdle {
            assertTrue(openedSettings)
            assertEquals("New Track", createdProject)
        }
    }

    @Test
    fun projectRowsShowMusicalDetailsAndSortByLatestEdit() {
        composeRule.setContent {
            GhostwriterTheme {
                HomeScreen(
                    existingProjects = listOf("Older", "Newest"),
                    projectSummaries = mapOf(
                        "Older" to ProjectSummary("Older", 1000),
                        "Newest" to ProjectSummary("Newest", 2000, 92, "C minor"),
                    ),
                    onCreateProject = {}, onOpenProject = {}, onDeleteProject = {},
                    onRenameProject = { _, _ -> }, onOpenSettings = {},
                )
            }
        }
        composeRule.onNodeWithText("92 BPM", substring = true).assertExists()
        composeRule.onNodeWithText("C minor", substring = true).assertExists()
        val newest = composeRule.onNodeWithText("Newest").fetchSemanticsNode().boundsInRoot
        val older = composeRule.onNodeWithText("Older").fetchSemanticsNode().boundsInRoot
        assertTrue(newest.top < older.top)
    }

}
