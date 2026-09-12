package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prosincerity.ghostwriter.data.ProjectStorage
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditorScreenTest {

    @get:Rule
    val composeRule = createComposeRule()

    private val context
        get() = InstrumentationRegistry.getInstrumentation().targetContext

    @Test
    fun emptyEditor_showsCoreControlsAndInvokesNavigation() {
        val projectTitle = uniqueProjectTitle("Editor controls")
        val showEditor = mutableStateOf(true)
        var openedSettings = false

        try {
            composeRule.setContent {
                if (showEditor.value) {
                    GhostwriterTheme {
                        EditorScreen(
                            projectTitle = projectTitle,
                            onBack = { showEditor.value = false },
                            onOpenSettings = { openedSettings = true },
                        )
                    }
                }
            }

            composeRule.onNodeWithText(projectTitle).assertExists()
            composeRule.onNodeWithText("Import beat").assertExists()
            composeRule.onNodeWithText("Start writing...").assertExists()
            composeRule.onNodeWithContentDescription("Project Info").assertExists()
            composeRule.onNodeWithContentDescription("Save").assertExists()

            composeRule.onNodeWithContentDescription("Settings").performClick()
            composeRule.runOnIdle { assertTrue(openedSettings) }

            composeRule.onNodeWithContentDescription("Back").performClick()
            composeRule.onNodeWithText(projectTitle).assertDoesNotExist()
            composeRule.runOnIdle { assertFalse(showEditor.value) }
        } finally {
            disposeEditorAndDeleteProject(showEditor, projectTitle)
        }
    }

    @Test
    fun editingLyricsAndProjectInfo_persistsBothWhenLeaving() {
        val projectTitle = uniqueProjectTitle("Editor persistence")
        val projectDir = ProjectStorage.projectDir(context, projectTitle)
        val showEditor = mutableStateOf(true)
        val lyrics = "Opening line\nSecond line"

        try {
            composeRule.setContent {
                if (showEditor.value) {
                    GhostwriterTheme {
                        EditorScreen(
                            projectTitle = projectTitle,
                            onBack = { showEditor.value = false },
                            onOpenSettings = {},
                        )
                    }
                }
            }

            composeRule.onNodeWithContentDescription("Project Info").performClick()
            composeRule.onNodeWithText("Project Information").assertExists()
            composeRule.onNodeWithText("Title: $projectTitle").assertExists()
            composeRule.onNodeWithText("BPM (optional)").performTextInput("96")
            composeRule.onNodeWithText("Musical Key (optional)").performTextInput("C Minor")
            composeRule.onNodeWithText("Save").performClick()

            composeRule.waitUntil(timeoutMillis = 5_000) {
                val metadata = ProjectStorage.loadMetadata(projectDir, projectTitle)
                metadata.bpm == 96 && metadata.key == "C Minor"
            }

            composeRule.onNode(hasSetTextAction()).performTextInput(lyrics)
            composeRule.onNodeWithContentDescription("Back").performClick()
            composeRule.waitForIdle()

            assertEquals(lyrics, ProjectStorage.loadLatest(projectDir))
        } finally {
            disposeEditorAndDeleteProject(showEditor, projectTitle)
        }
    }

    private fun disposeEditorAndDeleteProject(
        showEditor: MutableState<Boolean>,
        projectTitle: String,
    ) {
        composeRule.runOnIdle { showEditor.value = false }
        composeRule.waitForIdle()
        ProjectStorage.deleteProject(context, projectTitle)
    }

    private fun uniqueProjectTitle(prefix: String): String =
        "$prefix ${System.nanoTime()}"
}
