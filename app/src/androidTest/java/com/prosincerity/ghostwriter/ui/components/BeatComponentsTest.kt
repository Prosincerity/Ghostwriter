package com.prosincerity.ghostwriter.ui.components

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.media.BeatPlayer
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class BeatComponentsTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun reassignDialog_warnsAboutDataLossAndInvokesConfirmation() {
        var confirmed = false
        composeRule.setContent {
            GhostwriterTheme {
                ReassignBeatDialog(onConfirm = { confirmed = true }, onDismiss = {})
            }
        }

        composeRule.onNodeWithText("Reassign beat?").assertExists()
        composeRule.onNodeWithText("current beat file", substring = true).assertExists()
        composeRule.onNodeWithText("all markers", substring = true).assertExists()
        composeRule.onNodeWithText("lyrics will not be deleted", substring = true).assertExists()
        composeRule.onNodeWithText("Reassign").performClick()

        composeRule.runOnIdle { assertTrue(confirmed) }
    }

    @Test
    fun longBeatWarning_showsDurationAndUsesImportCancellationLabel() {
        var processed = false
        var cancelled = false
        composeRule.setContent {
            GhostwriterTheme {
                LongBeatWarningDialog(
                    durationMs = 5 * 60_000L,
                    cancelRemovesImportedBeat = true,
                    onProcess = { processed = true },
                    onCancel = { cancelled = true },
                )
            }
        }

        composeRule.onNodeWithText("This beat is 5:00 long", substring = true)
            .assertTextContains("Processing its waveform can take a long time", substring = true)
        composeRule.onNodeWithText("Process anyway").performClick()
        composeRule.onNodeWithText("Cancel import").performClick()

        composeRule.runOnIdle {
            assertTrue(processed)
            assertTrue(cancelled)
        }
    }

    @Test
    fun markerDialog_requiresNonBlankNameAndTrimsSavedValue() {
        var savedLabel: String? = null
        composeRule.setContent {
            GhostwriterTheme {
                WaveformMarkerDialog(
                    title = "Add marker",
                    initialLabel = "",
                    positionMs = 65_000L,
                    onSave = { savedLabel = it },
                    onDelete = null,
                    onDismiss = {},
                )
            }
        }

        composeRule.onNodeWithText("Position: 1:05").assertExists()
        composeRule.onNodeWithText("Add").assertIsNotEnabled()
        composeRule.onNodeWithText("Marker name").performTextInput("  Hook  ")
        composeRule.onNodeWithText("Add").assertIsEnabled().performClick()

        composeRule.runOnIdle { assertEquals("Hook", savedLabel) }
    }

    @Test
    fun beatPlayerLoadingState_onlyOffersCancellation() {
        var cancelled = false
        composeRule.setContent {
            GhostwriterTheme {
                BeatPlayerPanel(
                    beatPlayer = BeatPlayer(),
                    isBeatReady = false,
                    isImporting = false,
                    beatDisplayName = "",
                    onImportBeat = {},
                    onReassignBeat = {},
                    isReassigningBeat = false,
                    waveformAmplitudes = intArrayOf(),
                    isWaveformLoading = true,
                    markers = emptyList(),
                    onAddMarker = {},
                    onMarkerClick = {},
                    onMarkerMove = { _, _ -> },
                    onCancelWaveformPreparation = { cancelled = true },
                    cancelRemovesImportedBeat = true,
                    waveformPreparationCancelled = false,
                    waveformPreparationFailed = false,
                    onRetryWaveformPreparation = {},
                )
            }
        }

        composeRule.onNodeWithText("Preparing waveform…").assertExists()
        composeRule.onNodeWithText("Import beat").assertDoesNotExist()
        composeRule.onNodeWithText("Cancel import").performClick()
        composeRule.runOnIdle { assertTrue(cancelled) }
    }

    @Test
    fun beatPlayerFailureState_offersRetryAndBeatRemoval() {
        var retried = false
        var removed = false
        composeRule.setContent {
            GhostwriterTheme {
                BeatPlayerPanel(
                    beatPlayer = BeatPlayer(),
                    isBeatReady = false,
                    isImporting = false,
                    beatDisplayName = "",
                    onImportBeat = {},
                    onReassignBeat = { removed = true },
                    isReassigningBeat = false,
                    waveformAmplitudes = intArrayOf(),
                    isWaveformLoading = false,
                    markers = emptyList(),
                    onAddMarker = {},
                    onMarkerClick = {},
                    onMarkerMove = { _, _ -> },
                    onCancelWaveformPreparation = {},
                    cancelRemovesImportedBeat = false,
                    waveformPreparationCancelled = false,
                    waveformPreparationFailed = true,
                    onRetryWaveformPreparation = { retried = true },
                )
            }
        }

        composeRule.onNodeWithText("Retry").performClick()
        composeRule.onNodeWithText("Remove beat").performClick()
        composeRule.runOnIdle {
            assertTrue(retried)
            assertTrue(removed)
        }
    }
}
