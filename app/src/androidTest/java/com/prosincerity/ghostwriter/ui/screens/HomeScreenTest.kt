package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
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

    @Test
    fun existingProject_opensRenamesAndDeletesThroughItsMenu() {
        var openedProject: String? = null
        var renamedProject: Pair<String, String>? = null
        var deletedProject: String? = null

        composeRule.setContent {
            GhostwriterTheme {
                HomeScreen(
                    existingProjects = listOf("Existing Track"),
                    onCreateProject = {},
                    onOpenProject = { openedProject = it },
                    onDeleteProject = { deletedProject = it },
                    onRenameProject = { current, renamed ->
                        renamedProject = current to renamed
                    },
                    onOpenSettings = {},
                )
            }
        }

        composeRule.onNodeWithText("Recent").assertExists()
        composeRule.onNodeWithText("Existing Track").performClick()

        composeRule.onNodeWithContentDescription("Project options for Existing Track")
            .performClick()
        composeRule.onNodeWithText("Rename").performClick()
        composeRule.onNodeWithText("Rename track").assertExists()
        composeRule.onNode(hasSetTextAction()).performTextReplacement("Renamed Track")
        composeRule.onNodeWithText("Rename").performClick()

        composeRule.onNodeWithContentDescription("Project options for Existing Track")
            .performClick()
        composeRule.onNodeWithText("Delete").performClick()
        composeRule.onNodeWithText("Delete project?").assertExists()
        composeRule.onNodeWithText("Delete").performClick()

        composeRule.runOnIdle {
            assertEquals("Existing Track", openedProject)
            assertEquals("Existing Track" to "Renamed Track", renamedProject)
            assertEquals("Existing Track", deletedProject)
        }
    }
}
