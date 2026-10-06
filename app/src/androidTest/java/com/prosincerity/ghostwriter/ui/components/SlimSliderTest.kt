package com.prosincerity.ghostwriter.ui.components

import androidx.compose.runtime.currentComposer
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
import org.junit.Assert.assertTrue
import org.junit.Assert.assertEquals
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SlimSliderTest {
    @get:Rule val composeRule = createComposeRule()

    @Test
    fun restartingSliders_preservesDefaultAndExplicitRangesAndCallbacks() {
        val probe = RecompositionProbe()
        var defaultValue by mutableStateOf(0.25f)
        var explicitValue by mutableStateOf(5f)
        var partialValue by mutableStateOf(0.5f)
        try {
            composeRule.setContent {
                GhostwriterTheme {
                    Column {
                        probe.captureNext(currentComposer)
                        SlimSlider(defaultValue, { defaultValue = it })
                        probe.captureNext(currentComposer)
                        SlimSlider(explicitValue, { explicitValue = it }, Modifier.testTag("explicit"),
                            0f..10f, true, Color.Magenta)
                        probe.captureNext(currentComposer)
                        SlimSlider(partialValue, { partialValue = it }, Modifier.testTag("partial"),
                            accentColor = Color.Magenta)
                    }
                }
            }
            composeRule.runOnIdle {
                assertEquals(3, probe.scopes.size)
                probe.scopes.forEach { it.invalidate() }
            }
            composeRule.runOnIdle {
                probe.scopes.take(3).forEach { assertTrue(probe.entries.getValue(it) > 1) }
            }
            composeRule.onNodeWithTag("partial")
                .performSemanticsAction(SemanticsActions.SetProgress) { it(1f) }
            composeRule.onNode(hasProgressBarRangeInfo(androidx.compose.ui.semantics.ProgressBarRangeInfo(0.25f, 0f..1f)))
                .performSemanticsAction(SemanticsActions.SetProgress) { it(0.75f) }
            composeRule.onNodeWithTag("explicit")
                .performSemanticsAction(SemanticsActions.SetProgress) { it(8f) }
            composeRule.runOnIdle { probe.scopes.take(3).forEach { it.invalidate() } }
            composeRule.onNode(hasProgressBarRangeInfo(androidx.compose.ui.semantics.ProgressBarRangeInfo(0.75f, 0f..1f))).assertExists()
            composeRule.onNode(hasProgressBarRangeInfo(androidx.compose.ui.semantics.ProgressBarRangeInfo(8f, 0f..10f))).assertExists()
            composeRule.runOnIdle { assertEquals(0.75f, defaultValue); assertEquals(8f, explicitValue); assertEquals(1f, partialValue) }
        } finally {
            composeRule.runOnIdle { probe.dispose() }
        }
    }

    @Test
    fun changingRangeAndTheme_preservesValueAndUsesLatestOwner() {
        var primary by mutableStateOf(Color.Magenta)
        var value by mutableStateOf(0.25f)
        var range by mutableStateOf(0f..1f)
        var enabled by mutableStateOf(true)
        var owner = "first"
        var callback by mutableStateOf<(Float) -> Unit>({ value = it })
        composeRule.setContent {
            GhostwriterTheme {
                MaterialTheme(colorScheme = GhostColorScheme.copy(primary = primary)) {
                    SlimSlider(value, callback, Modifier.testTag("changing"), range, enabled)
                }
            }
        }
        composeRule.runOnIdle { primary = Color.Cyan }
        composeRule.onNodeWithTag("changing")
            .performSemanticsAction(SemanticsActions.SetProgress) { it(0.5f) }
        composeRule.runOnIdle {
            assertEquals(0.5f, value)
            range = -2f..10f
            callback = { value = it; owner = "second" }
        }
        composeRule.onNodeWithTag("changing")
            .performSemanticsAction(SemanticsActions.SetProgress) { it(8f) }
        composeRule.runOnIdle { assertEquals(8f, value); assertEquals("second", owner); enabled = false }
        composeRule.onNodeWithTag("changing").assertIsNotEnabled()
        composeRule.runOnIdle { enabled = true; primary = Color.Yellow }
        composeRule.onNodeWithTag("changing")
            .performSemanticsAction(SemanticsActions.SetProgress) { it(-1f) }
        composeRule.runOnIdle { assertEquals(-1f, value) }
    }

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
