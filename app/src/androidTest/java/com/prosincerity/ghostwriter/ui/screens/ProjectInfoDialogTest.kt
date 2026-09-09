package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextClearance
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.data.ProjectMetadata
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
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
}
