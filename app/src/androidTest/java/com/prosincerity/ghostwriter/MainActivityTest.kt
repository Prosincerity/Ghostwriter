package com.prosincerity.ghostwriter

import android.content.ActivityNotFoundException
import android.content.Intent
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.data.ProjectStorage
import com.prosincerity.ghostwriter.ui.screens.GHOSTWRITER_REPOSITORY_URL
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun navigation_homeToAboutAndBack_returnsHome() {
        composeRule.onNodeWithText("New file").assertExists()

        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.onNodeWithText("Settings").assertExists()

        composeRule.onNodeWithText("About Ghostwriter").performClick()
        composeRule.onNodeWithText("About").assertExists()

        var launchedIntent: Intent? = null
        composeRule.runOnIdle {
            composeRule.activity.externalIntentLauncher = { intent -> launchedIntent = intent }
        }
        composeRule.onNodeWithText("Source code").performClick()
        composeRule.runOnIdle {
            assertEquals(Intent.ACTION_VIEW, launchedIntent?.action)
            assertEquals(GHOSTWRITER_REPOSITORY_URL, launchedIntent?.dataString)
        }

        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Settings").assertExists()

        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("New file").assertExists()
    }

    @Test
    fun projectLifecycle_createNavigateRenameAndDelete() {
        val projectTitle = uniqueProjectTitle("Main activity project")
        val renamedTitle = "$projectTitle renamed"

        try {
            createProject(projectTitle)
            composeRule.onNodeWithText(projectTitle).assertExists()

            composeRule.onNodeWithContentDescription("Settings").performClick()
            composeRule.onNodeWithText("Settings").assertExists()
            composeRule.onNodeWithContentDescription("Back").performClick()
            composeRule.onNodeWithText(projectTitle).assertExists()

            composeRule.onNodeWithContentDescription("Back").performClick()
            waitUntilTextExists(projectTitle)
            composeRule.onNodeWithText(projectTitle).performClick()
            composeRule.onNodeWithText(projectTitle).assertExists()
            composeRule.onNodeWithContentDescription("Back").performClick()

            composeRule.onNodeWithContentDescription("Project options for $projectTitle").performClick()
            composeRule.onNodeWithText("Rename").performClick()
            composeRule.onNode(hasSetTextAction()).performTextReplacement(renamedTitle)
            composeRule.onNodeWithText("Rename").performClick()
            waitUntilTextExists(renamedTitle)

            composeRule.onNodeWithContentDescription("Project options for $renamedTitle").performClick()
            composeRule.onNodeWithText("Delete").performClick()
            composeRule.onNodeWithText("Delete project?").assertExists()
            composeRule.onNodeWithText("Delete").performClick()
            waitUntilTextDoesNotExist(renamedTitle)

            assertFalse(
                ProjectStorage.listProjects(composeRule.activity).contains(
                    ProjectStorage.sanitizeTitle(renamedTitle),
                ),
            )
        } finally {
            ProjectStorage.deleteProject(composeRule.activity, projectTitle)
            ProjectStorage.deleteProject(composeRule.activity, renamedTitle)
        }
    }

    @Test
    fun renameFailure_keepsOriginalProjectVisible() {
        val projectTitle = uniqueProjectTitle("Main activity rename failure")
        val renamedTitle = "$projectTitle renamed"

        try {
            createProject(projectTitle)
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitUntilTextExists(projectTitle)

            composeRule.onNodeWithContentDescription("Project options for $projectTitle").performClick()
            composeRule.onNodeWithText("Rename").performClick()
            composeRule.onNode(hasSetTextAction()).performTextReplacement(renamedTitle)
            assertTrue(ProjectStorage.deleteProject(composeRule.activity, projectTitle))
            composeRule.onNodeWithText("Rename").performClick()

            waitUntilTextDoesNotExist("Rename track")
            composeRule.onNodeWithText(projectTitle).assertExists()
        } finally {
            ProjectStorage.deleteProject(composeRule.activity, projectTitle)
            ProjectStorage.deleteProject(composeRule.activity, renamedTitle)
        }
    }

    @Test
    fun deleteFailure_keepsProjectVisible() {
        val projectTitle = uniqueProjectTitle("Main activity delete failure")

        try {
            createProject(projectTitle)
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitUntilTextExists(projectTitle)

            composeRule.onNodeWithContentDescription("Project options for $projectTitle").performClick()
            composeRule.onNodeWithText("Delete").performClick()
            assertTrue(ProjectStorage.deleteProject(composeRule.activity, projectTitle))
            composeRule.onNodeWithText("Delete").performClick()

            waitUntilTextDoesNotExist("Delete project?")
            composeRule.onNodeWithText(projectTitle).assertExists()
        } finally {
            ProjectStorage.deleteProject(composeRule.activity, projectTitle)
        }
    }

    @Test
    fun openExternalLink_whenLauncherFails_returnsFalse() {
        var result = true
        composeRule.runOnIdle {
            result = openExternalLink(composeRule.activity, "https://example.invalid") {
                throw ActivityNotFoundException("No browser")
            }
        }

        assertFalse(result)
    }

    private fun createProject(projectTitle: String) {
        composeRule.onNodeWithText("New file").performClick()
        composeRule.onNode(hasSetTextAction()).performTextInput(projectTitle)
        composeRule.onNodeWithText("Create").performClick()
        composeRule.onNodeWithText(projectTitle).assertExists()
    }

    private fun waitUntilTextExists(text: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitUntilTextDoesNotExist(text: String) {
        composeRule.waitUntil(timeoutMillis = 5_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty()
        }
    }

    private fun uniqueProjectTitle(prefix: String): String =
        "$prefix ${System.nanoTime()}"
}
