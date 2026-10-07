package com.prosincerity.ghostwriter.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SlimSliderTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun reconfiguredSlider_preservesInputUsesLatestOwnerAndHonorsDisabledState() {
        var value by mutableStateOf(0.25f)
        var range by mutableStateOf(0f..1f)
        var enabled by mutableStateOf(true)
        var explicit by mutableStateOf(false)
        var owner = "first"
        var callback by mutableStateOf<(Float) -> Unit>({ value = it })
        composeRule.setContent {
            GhostwriterTheme {
                if (explicit) {
                    SlimSlider(value, callback, Modifier.testTag("slider"), range, enabled)
                } else {
                    SlimSlider(value, callback, Modifier.testTag("slider"))
                }
            }
        }
        composeRule.onNodeWithTag("slider")
            .performSemanticsAction(SemanticsActions.SetProgress) { it(0.75f) }
        composeRule.runOnIdle {
            assertEquals(0.75f, value)
            explicit = true
        }
        composeRule.onNodeWithTag("slider")
            .performSemanticsAction(SemanticsActions.SetProgress) { it(0.5f) }
        composeRule.runOnIdle {
            assertEquals(0.5f, value)
            range = -2f..10f
            callback = { value = it; owner = "second" }
        }
        composeRule.onNodeWithTag("slider")
            .performSemanticsAction(SemanticsActions.SetProgress) { it(8f) }
        composeRule.runOnIdle {
            assertEquals(8f, value)
            assertEquals("second", owner)
            enabled = false
        }
        composeRule.onNodeWithTag("slider").assertIsNotEnabled()
        composeRule.runOnIdle { enabled = true }
        composeRule.onNodeWithTag("slider")
            .performSemanticsAction(SemanticsActions.SetProgress) { it(-1f) }
        composeRule.runOnIdle { assertEquals(-1f, value) }
    }
}
