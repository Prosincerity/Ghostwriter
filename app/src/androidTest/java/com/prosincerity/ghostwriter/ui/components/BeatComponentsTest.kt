package com.prosincerity.ghostwriter.ui.components

import androidx.compose.runtime.mutableStateOf
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.width
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.assertTextContains
import androidx.compose.ui.test.hasProgressBarRangeInfo
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.media.BeatLoopMode
import com.prosincerity.ghostwriter.media.BeatPlayer
import com.prosincerity.ghostwriter.data.MarkerLoopRole
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import kotlin.math.abs

@RunWith(AndroidJUnit4::class)
class BeatComponentsTest {

    @get:Rule
    val composeRule = createComposeRule()

    @Test
    fun playbackControls_evenlySpaceActionsAndShortVolumeSliderOnOneRow() {
        val player = BeatPlayer()
        composeRule.setContent {
            GhostwriterTheme {
                Box(Modifier.width(320.dp)) {
                    BeatPlaybackControls(
                        beatPlayer = player, isPlaying = false,
                        onPlayFromStart = {}, onTogglePlayback = {}, onReassignBeat = {},
                        isReassigningBeat = false,
                    )
                }
            }
        }
        val controls = composeRule.onNodeWithTag("Beat playback controls").fetchSemanticsNode().boundsInRoot
        val volume = composeRule.onNodeWithContentDescription("Beat volume").fetchSemanticsNode().boundsInRoot
        val buttons = listOf(
            composeRule.onNodeWithText("Reassign"),
            composeRule.onNodeWithContentDescription("Play from start"),
            composeRule.onNodeWithContentDescription("Play"),
            composeRule.onNodeWithContentDescription("Loop: Whole beat"),
            composeRule.onNodeWithContentDescription("Mute"),
        )
        // Material sliders extend their accessibility bounds beyond the visible track.
        // Measure button gaps and containment separately from those expanded bounds.
        val bounds = buttons.map { it.fetchSemanticsNode().boundsInRoot }
        for (control in bounds) {
            assertTrue("All controls should share the volume slider's row", abs(control.center.y - volume.center.y) < 2f)
            assertTrue("Button $control should fit inside row $controls", control.left >= controls.left && control.right <= controls.right)
        }
        val gaps = bounds.zipWithNext { left, right -> right.left - left.right }
        assertTrue("Controls should have space between them", gaps.all { it > 0f })
        assertTrue("All button gaps should be equal: $gaps", gaps.max() - gaps.min() < 2f)
        assertTrue("Volume slider should stay short", volume.width < controls.width / 4f)
    }

    @Test
    fun playbackButtons_clickOncePerActionAndStateUpdatesAreSilent() {
        val feedback = mutableListOf<HapticFeedbackType>()
        val playing = mutableStateOf(false)
        val player = BeatPlayer()
        val haptics = object : HapticFeedback {
            override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
                feedback += hapticFeedbackType
            }
        }
        composeRule.setContent {
            CompositionLocalProvider(LocalHapticFeedback provides haptics) {
                GhostwriterTheme {
                    BeatPlaybackControls(
                        beatPlayer = player,
                        isPlaying = playing.value,
                        onPlayFromStart = {},
                        onTogglePlayback = { playing.value = !playing.value },
                        onReassignBeat = {},
                        isReassigningBeat = false,
                    )
                }
            }
        }
        composeRule.runOnIdle { playing.value = true }
        composeRule.runOnIdle { assertTrue(feedback.isEmpty()) }
        composeRule.onNodeWithContentDescription("Pause").performClick()
        composeRule.onNodeWithContentDescription("Play").performClick()
        composeRule.onNodeWithContentDescription("Loop: Whole beat").performClick()
        composeRule.onNodeWithText("Markers").assertExists()
        composeRule.runOnIdle { assertEquals(BeatLoopMode.MARKERS, player.loopMode) }
        composeRule.onNodeWithContentDescription("Loop: Markers").performClick()
        composeRule.onNodeWithText("Off").assertExists()
        composeRule.runOnIdle { assertTrue(!player.isLooping) }
        composeRule.onNodeWithContentDescription("Loop: Off").performClick()
        composeRule.onNodeWithText("Beat").assertExists()
        composeRule.runOnIdle { assertEquals(BeatLoopMode.WHOLE_BEAT, player.loopMode) }
        composeRule.onNodeWithContentDescription("Play from start").performClick()
        composeRule.onNodeWithContentDescription("Mute").performClick()
        composeRule.runOnIdle { assertEquals(List(7) { HapticFeedbackType.ContextClick }, feedback) }
        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(player.volume, 0f..1f)))
            .performSemanticsAction(SemanticsActions.SetProgress) { it(0.5f) }
        composeRule.runOnIdle { assertEquals(7, feedback.size) }
    }

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
                    onSave = { label, _ -> savedLabel = label },
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
    fun markerDialog_loopDropdownSavesEachRole() {
        var savedRole: MarkerLoopRole? = null
        composeRule.setContent {
            GhostwriterTheme {
                WaveformMarkerDialog(
                    title = "Edit marker", initialLabel = "Hook", positionMs = 1000,
                    initialLoopRole = MarkerLoopRole.START,
                    onSave = { _, role -> savedRole = role }, onDelete = {}, onDismiss = {},
                )
            }
        }
        composeRule.onNodeWithText("Start").assertExists()
        for (role in listOf(MarkerLoopRole.END, MarkerLoopRole.NONE, MarkerLoopRole.START)) {
            composeRule.onNodeWithText("Looping").performClick()
            composeRule.onNodeWithText(role.displayName).performClick()
            composeRule.onNodeWithText("Save").performClick()
            composeRule.runOnIdle { assertEquals(role, savedRole) }
        }
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
    fun readyPlayer_keepsControlsDuringWaveformPreparationCancellationAndFailure() {
        val loading = mutableStateOf(true)
        val cancelled = mutableStateOf(false)
        val failed = mutableStateOf(false)
        val player = BeatPlayer()
        composeRule.setContent {
            GhostwriterTheme {
                BeatPlayerPanel(
                    beatPlayer = player,
                    isBeatReady = true,
                    isImporting = false,
                    beatDisplayName = "beat.wav",
                    onImportBeat = {},
                    onReassignBeat = {},
                    isReassigningBeat = false,
                    waveformAmplitudes = intArrayOf(),
                    isWaveformLoading = loading.value,
                    markers = emptyList(),
                    onAddMarker = {},
                    onMarkerClick = {},
                    onMarkerMove = { _, _ -> },
                    onCancelWaveformPreparation = {
                        loading.value = false
                        cancelled.value = true
                    },
                    cancelRemovesImportedBeat = false,
                    waveformPreparationCancelled = cancelled.value,
                    waveformPreparationFailed = failed.value,
                    onRetryWaveformPreparation = {
                        cancelled.value = false
                        failed.value = false
                        loading.value = true
                    },
                )
            }
        }
        composeRule.onNodeWithText("Preparing waveform…").assertExists()
        composeRule.onNodeWithContentDescription("Play").assertIsEnabled().performClick()
        composeRule.onNodeWithContentDescription("Play from start").assertIsEnabled()
        composeRule.onNodeWithText("Cancel preparation").performClick()
        composeRule.onNodeWithText("Waveform preparation canceled").assertExists()
        composeRule.onNodeWithContentDescription("Play").assertIsEnabled()
        composeRule.onNodeWithText("Retry").performClick()
        composeRule.onNodeWithContentDescription("Play").assertIsEnabled()
        composeRule.runOnIdle {
            loading.value = false
            failed.value = true
        }
        composeRule.onNodeWithText("Couldn't create waveform").assertExists()
        composeRule.onNodeWithContentDescription("Play").assertIsEnabled()
        composeRule.onNodeWithContentDescription("Mute").performClick()
        composeRule.runOnIdle { assertEquals(0f, player.volume) }
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
        composeRule.onNodeWithText("0:00 / 0:00").assertExists()
        composeRule.onNodeWithContentDescription("Play from start").performClick()
        composeRule.onNodeWithContentDescription("Play").performClick()

        composeRule.onNodeWithContentDescription("Loop: Whole beat").performClick()
        composeRule.onNodeWithContentDescription("Loop: Markers").assertExists()
        composeRule.runOnIdle { assertEquals(BeatLoopMode.MARKERS, beatPlayer.loopMode) }
        composeRule.onNodeWithContentDescription("Loop: Markers").performClick()
        composeRule.onNodeWithContentDescription("Loop: Off").assertExists()
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
    fun playbackControls_reflectLoopChangesFromOutsideTheEditor() {
        val beatPlayer = BeatPlayer()
        composeRule.setContent {
            GhostwriterTheme {
                BeatPlaybackControls(
                    beatPlayer = beatPlayer,
                    isPlaying = false,
                    onPlayFromStart = {},
                    onTogglePlayback = {},
                    onReassignBeat = {},
                    isReassigningBeat = false,
                )
            }
        }

        composeRule.onNodeWithContentDescription("Loop: Whole beat").assertExists()
        composeRule.runOnIdle { beatPlayer.toggleLoop() }
        composeRule.onNodeWithContentDescription("Loop: Markers").assertExists()
        composeRule.runOnIdle { beatPlayer.toggleLoop() }
        composeRule.onNodeWithContentDescription("Loop: Off").assertExists()
        composeRule.onNodeWithContentDescription("Loop: Off").performClick()
        composeRule.runOnIdle { assertTrue(beatPlayer.isLooping) }
        composeRule.onNodeWithContentDescription("Loop: Whole beat").assertExists()
    }

    @Test
    fun playbackControls_restoresMutedVolumeAfterControlsAreRecreated() {
        val beatPlayer = BeatPlayer()
        val showControls = mutableStateOf(true)
        composeRule.setContent {
            GhostwriterTheme {
                if (showControls.value) {
                    BeatPlaybackControls(
                        beatPlayer = beatPlayer,
                        isPlaying = false,
                        onPlayFromStart = {},
                        onTogglePlayback = {},
                        onReassignBeat = {},
                        isReassigningBeat = false,
                    )
                }
            }
        }

        composeRule.onNode(hasProgressBarRangeInfo(ProgressBarRangeInfo(1f, 0f..1f)))
            .performSemanticsAction(SemanticsActions.SetProgress) { setProgress ->
                setProgress(0.35f)
            }
        composeRule.onNodeWithContentDescription("Mute").performClick()
        composeRule.runOnIdle { showControls.value = false }
        composeRule.runOnIdle { showControls.value = true }
        composeRule.onNodeWithContentDescription("Unmute").performClick()

        composeRule.runOnIdle { assertEquals(0.35f, beatPlayer.volume) }
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
