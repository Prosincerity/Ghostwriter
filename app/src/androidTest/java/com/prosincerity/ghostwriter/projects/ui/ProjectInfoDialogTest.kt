package com.prosincerity.ghostwriter.projects.ui

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.projects.data.ProjectMetadata
import com.prosincerity.ghostwriter.core.ui.theme.GhostwriterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProjectInfoDialogTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun save_filtersBpmAndNormalizesOptionalText() {
        var saved: ProjectMetadata? = null
        val original = ProjectMetadata(
            title = "Track",
            bpm = 80,
            key = "C Minor",
            timeSignature = "4/4",
            notes = "Draft",
        )
        composeRule.setContent {
            GhostwriterTheme {
                ProjectInfoDialog(metadata = original, onDismiss = {}, onSave = { saved = it })
            }
        }

        composeRule.onNodeWithText("BPM (optional)").performTextClearance()
        composeRule.onNodeWithText("BPM (optional)").performTextInput("9a0")
        composeRule.onNodeWithText("Musical Key (optional)").performTextClearance()
        composeRule.onNodeWithText("Musical Key (optional)").performTextInput("  F# Major  ")
        composeRule.onNodeWithText("Time Signature (optional)").performTextClearance()
        composeRule.onNodeWithText("Time Signature (optional)").performTextInput("   ")
        composeRule.onNodeWithText("Notes (optional)").performTextClearance()
        composeRule.onNodeWithText("Save").performClick()

        composeRule.runOnIdle {
            val result = checkNotNull(saved)
            assertEquals(90, result.bpm)
            assertEquals("F# Major", result.key)
            assertNull(result.timeSignature)
            assertNull(result.notes)
            assertEquals(original.title, result.title)
            assertEquals(original.createdAt, result.createdAt)
            assertTrue(result.markers.isEmpty())
        }
    }

    @Test
    fun save_rejectsZeroAndOverflowBpmInsteadOfClearingTheExistingValue() {
        var saved: ProjectMetadata? = null
        composeRule.setContent {
            GhostwriterTheme {
                ProjectInfoDialog(
                    metadata = ProjectMetadata(title = "Track", bpm = 80),
                    onDismiss = {},
                    onSave = { saved = it },
                )
            }
        }

        val bpmField = composeRule.onNodeWithText("BPM (optional)")
        for (invalidBpm in listOf("0", "99999999999999999999")) {
            bpmField.performTextReplacement(invalidBpm)
            composeRule.onNodeWithText("Enter a positive BPM").assertExists()
            composeRule.onNodeWithText("Save").assertIsNotEnabled()
            composeRule.runOnIdle { assertNull(saved) }
        }

        bpmField.performTextReplacement("90")
        composeRule.onNodeWithText("Save").assertIsEnabled().performClick()
        composeRule.runOnIdle { assertEquals(90, saved?.bpm) }
    }

    @Test
    fun replacingSaveHandler_keepsDraftAndUsesCurrentCallback() {
        val original = ProjectMetadata(title = "Track", bpm = 80, createdAt = 10, updatedAt = 20)
        var oldSaves = 0
        var saved: ProjectMetadata? = null
        val onSave = mutableStateOf<(ProjectMetadata) -> Unit>({ oldSaves++ })
        composeRule.setContent {
            GhostwriterTheme {
                ProjectInfoDialog(original, {}, onSave.value)
            }
        }
        composeRule.onNodeWithText("BPM (optional)").performTextReplacement("95")
        composeRule.onNodeWithText("Musical Key (optional)").performTextReplacement(" D Minor ")
        composeRule.onNodeWithText("Time Signature (optional)").performTextReplacement(" 3/4 ")
        composeRule.onNodeWithText("Notes (optional)").performTextReplacement(" Draft lyrics ")

        composeRule.runOnIdle { onSave.value = { saved = it } }
        composeRule.onNodeWithText("Save").performClick()

        composeRule.runOnIdle {
            assertEquals(0, oldSaves)
            assertEquals(original.copy(bpm = 95, key = "D Minor", timeSignature = "3/4", notes = "Draft lyrics"), saved)
        }
    }

    @Test
    fun changingMetadata_resetsAllDraftFieldsToCurrentProject() {
        val metadata = mutableStateOf(ProjectMetadata(
            title = "First", bpm = 80, key = "C Minor", timeSignature = "4/4", notes = "Original",
            createdAt = 10, updatedAt = 20,
        ))
        var saved: ProjectMetadata? = null
        composeRule.setContent {
            GhostwriterTheme {
                ProjectInfoDialog(metadata.value, {}, { saved = it })
            }
        }
        composeRule.onNodeWithText("BPM (optional)").performTextReplacement("95")
        composeRule.onNodeWithText("Musical Key (optional)").performTextReplacement("D Minor")
        composeRule.onNodeWithText("Time Signature (optional)").performTextReplacement("6/8")
        composeRule.onNodeWithText("Notes (optional)").performTextReplacement("Unsaved draft")

        val next = ProjectMetadata(title = "Second", timeSignature = "3/4", createdAt = 30, updatedAt = 40)
        composeRule.runOnIdle { metadata.value = next }
        composeRule.onNodeWithText("Save").performClick()

        composeRule.runOnIdle { assertEquals(next, saved) }
    }
}
