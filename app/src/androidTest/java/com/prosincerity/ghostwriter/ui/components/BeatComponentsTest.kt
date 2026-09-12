package com.prosincerity.ghostwriter.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
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
    fun beatPlayerCancelledState_centersRetryContent() {
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
                    isWaveformLoading = false,
                    markers = emptyList(),
                    onAddMarker = {},
                    onMarkerClick = {},
                    onMarkerMove = { _, _ -> },
                    onCancelWaveformPreparation = {},
                    cancelRemovesImportedBeat = false,
                    waveformPreparationCancelled = true,
                    waveformPreparationFailed = false,
                    onRetryWaveformPreparation = {},
                )
            }
        }

        val messageBounds = composeRule.onNodeWithText("Waveform preparation canceled")
            .fetchSemanticsNode().boundsInRoot
        val retryBounds = composeRule.onNodeWithText("Retry")
            .fetchSemanticsNode().boundsInRoot

        assertEquals(messageBounds.center.x, retryBounds.center.x, 1f)
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

    @Test
    fun beatPlayerEmptyState_importsBeatAndDisablesTransientActions() {
        val isImporting = mutableStateOf(false)
        val isReassigning = mutableStateOf(false)
        var imports = 0
        composeRule.setContent {
            GhostwriterTheme {
                BeatPlayerPanel(
                    beatPlayer = BeatPlayer(),
                    isBeatReady = false,
                    isImporting = isImporting.value,
                    beatDisplayName = "",
                    onImportBeat = { imports++ },
                    onReassignBeat = {},
                    isReassigningBeat = isReassigning.value,
                    waveformAmplitudes = intArrayOf(),
                    isWaveformLoading = false,
                    markers = emptyList(),
                    onAddMarker = {},
                    onMarkerClick = {},
                    onMarkerMove = { _, _ -> },
                    onCancelWaveformPreparation = {},
                    cancelRemovesImportedBeat = false,
                    waveformPreparationCancelled = false,
                    waveformPreparationFailed = false,
                    onRetryWaveformPreparation = {},
                )
            }
        }

        composeRule.onNodeWithText("No beat selected").assertExists()
        composeRule.onNodeWithText("Import beat").assertIsEnabled().performClick()
        composeRule.runOnIdle {
            assertEquals(1, imports)
            isImporting.value = true
        }

        composeRule.onNodeWithText("Importing…").assertIsNotEnabled()
        composeRule.runOnIdle {
            isImporting.value = false
            isReassigning.value = true
        }

        composeRule.onNodeWithText("Removing beat…").assertExists()
        composeRule.onNodeWithText("Removing…").assertIsNotEnabled()
    }

    @Test
    fun beatPlayerReadyState_controlsPlaybackLoopVolumeAndReassignment() {
        val beatPlayer = BeatPlayer()
        var reassigned = false
        composeRule.setContent {
            GhostwriterTheme {
                BeatPlayerPanel(
                    beatPlayer = beatPlayer,
                    isBeatReady = true,
                    isImporting = false,
                    beatDisplayName = "Midnight instrumental",
                    onImportBeat = {},
                    onReassignBeat = { reassigned = true },
                    isReassigningBeat = false,
                    waveformAmplitudes = intArrayOf(0, 8_000, 16_000),
                    isWaveformLoading = false,
                    markers = emptyList(),
                    onAddMarker = {},
                    onMarkerClick = {},
                    onMarkerMove = { _, _ -> },
                    onCancelWaveformPreparation = {},
                    cancelRemovesImportedBeat = false,
                    waveformPreparationCancelled = false,
                    waveformPreparationFailed = false,
                    onRetryWaveformPreparation = {},
                )
            }
        }

        composeRule.onNodeWithText("Midnight instrumental").assertExists()
        composeRule.onNodeWithText("0:00/0:00").assertExists()
        composeRule.onNodeWithContentDescription("Play from start").performClick()
        composeRule.onNodeWithContentDescription("Play").performClick()

        composeRule.onNodeWithContentDescription("Disable loop").performClick()
        composeRule.onNodeWithContentDescription("Enable loop").assertExists()
        composeRule.runOnIdle { assertTrue(!beatPlayer.isLooping) }

        composeRule.onNodeWithContentDescription("Mute").performClick()
        composeRule.onNodeWithContentDescription("Unmute").assertExists()
        composeRule.runOnIdle { assertEquals(0f, beatPlayer.volume) }
        composeRule.onNodeWithContentDescription("Unmute").performClick()
        composeRule.onNodeWithContentDescription("Mute").assertExists()
        composeRule.runOnIdle { assertEquals(1f, beatPlayer.volume) }

        composeRule.onNode(
            hasProgressBarRangeInfo(ProgressBarRangeInfo(1f, 0f..1f)),
        ).performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
            setProgress(0.35f)
        }
        composeRule.runOnIdle { assertEquals(0.35f, beatPlayer.volume) }

        composeRule.onNodeWithText("Reassign").performClick()
        composeRule.runOnIdle { assertTrue(reassigned) }
    }

    @Test
    fun beatPlayerReadyState_zoomButtonsTrackViewportLimits() {
        composeRule.setContent {
            GhostwriterTheme {
                BeatPlayerPanel(
                    beatPlayer = BeatPlayer(),
                    isBeatReady = true,
                    isImporting = false,
                    beatDisplayName = "Beat",
                    onImportBeat = {},
                    onReassignBeat = {},
                    isReassigningBeat = false,
                    waveformAmplitudes = intArrayOf(1_000, 2_000, 3_000),
                    isWaveformLoading = false,
                    markers = emptyList(),
                    onAddMarker = {},
                    onMarkerClick = {},
                    onMarkerMove = { _, _ -> },
                    onCancelWaveformPreparation = {},
                    cancelRemovesImportedBeat = false,
                    waveformPreparationCancelled = false,
                    waveformPreparationFailed = false,
                    onRetryWaveformPreparation = {},
                )
            }
        }

        composeRule.onNodeWithContentDescription("Zoom out waveform").assertIsNotEnabled()
        composeRule.onNodeWithContentDescription("Zoom in waveform").assertIsEnabled().performClick()
        composeRule.onNodeWithContentDescription("Zoom out waveform").assertIsEnabled().performClick()
        composeRule.onNodeWithContentDescription("Zoom out waveform").assertIsNotEnabled()
    }
}
