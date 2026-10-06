package com.prosincerity.ghostwriter

import android.content.ActivityNotFoundException
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.toArgb
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.data.LyricTextSettings
import com.prosincerity.ghostwriter.data.ProjectStorage
import com.prosincerity.ghostwriter.data.Settings
import com.prosincerity.ghostwriter.ui.screens.GHOSTWRITER_REPOSITORY_URL
import com.prosincerity.ghostwriter.ui.theme.GhostColorScheme
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
    fun activityWindow_usesStudioBackground() {
        composeRule.runOnIdle {
            val theme = composeRule.activity.theme
            val background = TypedValue()
            assertTrue(theme.resolveAttribute(android.R.attr.windowBackground, background, true))
            val drawable = composeRule.activity.getDrawable(background.resourceId) as ColorDrawable
            assertEquals(GhostColorScheme.background.toArgb(), drawable.color)
            val lightTheme = TypedValue()
            assertTrue(theme.resolveAttribute(android.R.attr.isLightTheme, lightTheme, true))
            assertEquals(0, lightTheme.data)
        }
    }

    @Test
    fun returningFromSettingsAndDictionary_keepsLatestLyrics() {
        val title = uniqueProjectTitle("Editor round trips")
        try {
            createProject(title)
            for (destination in listOf("Settings", "Dictionary", "Settings", "Dictionary")) {
                val lyrics = "Latest lyrics before $destination ${System.nanoTime()}"
                composeRule.onNode(hasSetTextAction()).performTextReplacement(lyrics)
                composeRule.onNodeWithContentDescription(destination).performClick()
                composeRule.onNodeWithContentDescription("Back").performClick()
                waitUntilTextExists(title)
                composeRule.onNodeWithText(lyrics).assertExists()
            }
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitUntilTextExists(title)
        } finally {
            ProjectStorage.deleteProject(composeRule.activity, title)
        }
    }

    @Test
    fun navigation_homeToAboutAndBack_returnsHome() {
        composeRule.onNodeWithText("New project").assertExists()

        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.onNodeWithText("Settings").assertExists()

        composeRule.onNodeWithText("About Ghostwriter").performScrollTo().performClick()
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
        composeRule.onNodeWithText("New project").assertExists()
    }

    @Test
    fun settings_dictionaryDownloadsOpensListAndReturns() {
        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.onNodeWithText("Download Dictionaries").performScrollTo().performClick()
        composeRule.onNodeWithText("Data sources, attribution, and licensing are listed in Settings → About Ghostwriter.").assertExists()
        composeRule.onNode(
            hasContentDescription("Download Wiktionary Kaikki for English") or
                hasContentDescription("Wiktionary Kaikki installed for English"),
        ).assertExists()
        composeRule.onNode(
            hasContentDescription("Download eSpeak NG generated for English") or
                hasContentDescription("eSpeak NG generated installed for English"),
        ).assertExists()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("About Ghostwriter").assertExists()
    }

    @Test
    fun changingTypographyInSettings_appliesWhenReturningToExistingLyrics() {
        val projectTitle = uniqueProjectTitle("Typography")
        val originalSettings = Settings.getLyricTextSettings(composeRule.activity)
        try {
            Settings.setLyricTextSettings(composeRule.activity, LyricTextSettings())
            createProject(projectTitle)
            composeRule.onNode(hasSetTextAction()).performTextInput("Keep these lyrics")
            composeRule.onNodeWithContentDescription("Settings").performClick()
            composeRule.onNodeWithContentDescription("Font family").performScrollTo().performClick()
            composeRule.onNodeWithText("System default").performClick()
            composeRule.onNodeWithContentDescription("Font size").performScrollTo().performClick()
            composeRule.onNodeWithText("24 sp").performClick()
            composeRule.onNodeWithContentDescription("Text alignment").performScrollTo().performClick()
            composeRule.onNodeWithText("Center").performClick()
            composeRule.onNodeWithContentDescription("Back").performClick()

            composeRule.onNodeWithText("Keep these lyrics").assertExists()
            val results = mutableListOf<TextLayoutResult>()
            composeRule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
                it(results)
            }
            val style = results.single().layoutInput.style
            assertEquals(FontFamily.Default, style.fontFamily)
            assertEquals(24.sp, style.fontSize)
            assertEquals(TextAlign.Center, style.textAlign)
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitUntilTextExists(projectTitle)
        } finally {
            Settings.setLyricTextSettings(composeRule.activity, originalSettings)
            ProjectStorage.deleteProject(composeRule.activity, projectTitle)
        }
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
            composeRule.onNodeWithText("Recent").assertExists()
            composeRule.onNodeWithText(projectTitle).performClick()
            composeRule.onNodeWithText(projectTitle).assertExists()
            composeRule.onNodeWithContentDescription("Back").performClick()

            composeRule.onNodeWithContentDescription("Project options for $projectTitle").performClick()
            composeRule.onNodeWithText("Rename").performClick()
            composeRule.onNodeWithText("Rename track").assertExists()
            composeRule.onNode(hasSetTextAction()).performTextReplacement(renamedTitle)
            composeRule.onNodeWithText("Rename").performClick()
            waitUntilTextExists(renamedTitle)

            composeRule.onNodeWithContentDescription("Project options for $renamedTitle").performClick()
            composeRule.onNodeWithText("Delete").performClick()
            composeRule.onNodeWithText("Delete project?").assertExists()
            composeRule.onNodeWithText("Delete").performClick()
            waitUntilTextDoesNotExist(renamedTitle)

            assertFalse(
                ProjectStorage.listProjects(composeRule.activity).any { id ->
                    ProjectStorage.loadMetadata(ProjectStorage.projectDir(composeRule.activity, id), "").title == renamedTitle
                },
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
        composeRule.onNodeWithText("New project").performClick()
        composeRule.onNodeWithText("Name this track").assertExists()
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
