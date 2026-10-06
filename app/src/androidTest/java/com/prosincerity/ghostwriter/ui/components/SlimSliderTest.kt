package com.prosincerity.ghostwriter.ui.components

import androidx.compose.runtime.getValue
import androidx.compose.foundation.layout.Column
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performSemanticsAction
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import com.prosincerity.ghostwriter.ui.theme.GhostColorScheme
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SlimSliderTest {
    @get:Rule val composeRule = createComposeRule()

    @Test fun themeChanges_preserveDefaultAndExplicitSliderValuesAndCallbacks() {
        var color by mutableStateOf(GhostColorScheme.surfaceContainerHighest)
        var value by mutableStateOf(0.25f)
        val callback: (Float) -> Unit = { value = it }
        composeRule.setContent {
            GhostwriterTheme {
                MaterialTheme(colorScheme = GhostColorScheme.copy(surfaceContainerHighest = color)) {
                    Column {
                        SlimSlider(value, callback)
                        SlimSlider(0.75f, {}, Modifier.testTag("explicit"), 0f..1f, false, Color.Magenta)
                    }
                }
            }
        }
        composeRule.runOnIdle { color = Color.DarkGray }
        composeRule.onNode(hasProgressBarRangeInfo(androidx.compose.ui.semantics.ProgressBarRangeInfo(0.25f, 0f..1f)))
            .performSemanticsAction(SemanticsActions.SetProgress) { it(0.5f) }
        composeRule.runOnIdle { assertEquals(0.5f, value); color = Color.Black }
        composeRule.onNodeWithTag("explicit").assertIsNotEnabled()
    }

    @Test fun defaultAndExplicitOptions_updateValueAndHonorDisabledState() {
        var value by mutableStateOf(0.25f)
        var explicit by mutableStateOf(false)
        var enabled by mutableStateOf(true)
        var callback by mutableStateOf<(Float) -> Unit>({ value = it })
        composeRule.setContent {
            GhostwriterTheme {
                if (explicit) {
                    SlimSlider(value, callback, Modifier.testTag("slider"), 0f..10f, enabled, Color.Magenta)
                } else {
                    SlimSlider(value, callback, Modifier.testTag("slider"))
                }
            }
        }
        composeRule.onNodeWithTag("slider").performSemanticsAction(SemanticsActions.SetProgress) { it(0.75f) }
        composeRule.runOnIdle {
            assertEquals(0.75f, value)
            explicit = true
            callback = { value = it }
        }
        composeRule.onNodeWithTag("slider").performSemanticsAction(SemanticsActions.SetProgress) { it(5f) }
        composeRule.runOnIdle { assertEquals(5f, value); enabled = false }
        composeRule.onNodeWithTag("slider").assertIsNotEnabled()
    }
}
