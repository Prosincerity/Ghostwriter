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
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.unit.Density
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsDisplayed
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
import androidx.test.platform.app.InstrumentationRegistry
import com.prosincerity.ghostwriter.media.BeatLoopMode
import com.prosincerity.ghostwriter.media.BeatPlayer
import com.prosincerity.ghostwriter.data.MarkerLoopRole
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
    fun playbackControls_givePlaybackMoreRoomAndKeepLabelsVisibleAndPlayCentered() {
        val player = BeatPlayer()
        val width = mutableStateOf(320.dp)
        val fontScale = mutableStateOf(1f)
        val playing = mutableStateOf(false)
        composeRule.setContent {
            val density = LocalDensity.current.density
            CompositionLocalProvider(LocalDensity provides Density(density, fontScale.value)) {
                GhostwriterTheme {
                    Box(Modifier.width(width.value)) {
                        BeatPlaybackControls(
                            beatPlayer = player, isPlaying = playing.value,
                            onPlayFromStart = {}, onTogglePlayback = {}, onReassignBeat = {},
                            isReassigningBeat = false,
                        )
                    }
                }
            }
        }
        for ((rowWidth, textScale) in listOf(280.dp to 1f, 280.dp to 1.5f, 320.dp to 1f, 360.dp to 1f)) {
            composeRule.runOnIdle {
                width.value = rowWidth
                fontScale.value = textScale
            }
            for (mode in BeatLoopMode.entries) {
                composeRule.runOnIdle { player.setLoopMode(mode) }
                for (isPlaying in listOf(false, true)) {
                    composeRule.runOnIdle { playing.value = isPlaying }
                    val layoutDescription = "width=$rowWidth, fontScale=$textScale, mode=$mode, playing=$isPlaying"
                    val controls = composeRule.onNodeWithTag("Beat playback controls")
                        .fetchSemanticsNode().boundsInRoot
                    val groups = listOf("Beat reassign group", "Beat transport group", "Beat volume group")
                        .map { composeRule.onNodeWithTag(it).fetchSemanticsNode().boundsInRoot }
                    for (group in groups) {
                        assertEquals(controls.center.y, group.center.y, 1f)
                    }
                    assertEquals("Balanced sides must keep Play centered", groups[0].width, groups[2].width, 1f)
                    assertTrue("Playback must get more than a third of the row", groups[1].width > controls.width / 3f)
                    val reassign = composeRule.onNodeWithTag("Beat reassign button").fetchSemanticsNode().boundsInRoot
                    assertTrue("Reassign should be compact", reassign.width < groups[0].width)
                    assertEquals(controls.center.y, reassign.center.y, 1f)
                    val context = InstrumentationRegistry.getInstrumentation().targetContext
                    for (label in listOf("Reassign", context.getString(mode.shortLabelRes))) {
                        composeRule.onNodeWithText(label, useUnmergedTree = true)
                            .assertIsDisplayed()
                            .performSemanticsAction(SemanticsActions.GetTextLayoutResult) { getLayout ->
                                val results = mutableListOf<TextLayoutResult>()
                                assertTrue(getLayout(results))
                                val textBounds = results.map {
                                    "size=${it.size}, fontSize=${it.layoutInput.style.fontSize}, " +
                                        "overflowWidth=${it.didOverflowWidth}, overflowHeight=${it.didOverflowHeight}"
                                }
                                assertTrue(
                                    "$label must fit without clipping ($layoutDescription): $textBounds",
                                    results.isNotEmpty() && results.all { !it.hasVisualOverflow },
                                )
                            }
                    }
                    assertEquals(controls.left, groups.first().left, 1f)
                    assertEquals(controls.right, groups.last().right, 1f)
                    groups.zipWithNext { left, right -> assertEquals(left.right, right.left, 1f) }

                    val restart = composeRule.onNodeWithContentDescription("Play from start")
                        .fetchSemanticsNode().boundsInRoot
                    val play = composeRule.onNodeWithContentDescription(if (isPlaying) "Pause" else "Play")
                        .fetchSemanticsNode().boundsInRoot
                    val loopLabel = InstrumentationRegistry.getInstrumentation().targetContext.getString(mode.labelRes)
                    val loop = composeRule.onNodeWithContentDescription(loopLabel)
                        .fetchSemanticsNode().boundsInRoot
                    assertEquals("Play must be at the center of the full row", controls.center.x, play.center.x, 1f)
                    assertEquals(play.center.x - restart.center.x, loop.center.x - play.center.x, 1f)
                    // Material centers a constrained button inside its minimum interactive
                    // size. Odd pixel differences can shift its bounds by one pixel.
                    val edgeTolerancePx = 1f
                    for ((name, button) in listOf("Restart" to restart, "Play/Pause" to play, "Loop" to loop)) {
                        assertEquals(controls.center.y, button.center.y, 1f)
                        assertTrue(
                            "$name $button must fit playback group ${groups[1]} ($layoutDescription)",
                            button.left >= groups[1].left - edgeTolerancePx &&
                                button.right <= groups[1].right + edgeTolerancePx,
                        )
                    }
                    assertTrue(
                        "Playback buttons must not overlap: restart=$restart, play=$play, loop=$loop ($layoutDescription)",
                        restart.right <= play.left + edgeTolerancePx && play.right <= loop.left + edgeTolerancePx,
                    )

                    val mute = composeRule.onNodeWithContentDescription("Mute").fetchSemanticsNode().boundsInRoot
                    // Use the slider slot because Slider expands its accessibility bounds.
                    val slider = composeRule.onNodeWithTag("Beat volume slider slot").fetchSemanticsNode().boundsInRoot
                    val volumeGroup = groups[2]
                    assertEquals(controls.center.y, mute.center.y, 1f)
                    assertEquals(controls.center.y, slider.center.y, 1f)
                    val gaps = listOf(mute.left - volumeGroup.left, slider.left - mute.right, volumeGroup.right - slider.right)
                    assertTrue("Volume controls must have evenly distributed space: $gaps ($layoutDescription)", gaps.min() >= -edgeTolerancePx && gaps.max() - gaps.min() <= 2f)
                }
            }
        }
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
        composeRule.onNodeWithContentDescription("Loop: Markers").assertExists()
        composeRule.runOnIdle { assertEquals(BeatLoopMode.MARKERS, player.loopMode) }
        composeRule.onNodeWithContentDescription("Loop: Markers").performClick()
        composeRule.onNodeWithContentDescription("Loop: Off").assertExists()
        composeRule.runOnIdle { assertTrue(!player.isLooping) }
        composeRule.onNodeWithContentDescription("Loop: Off").performClick()
        composeRule.onNodeWithContentDescription("Loop: Whole beat").assertExists()
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
    fun unreadyPlayer_offersCancellationWhileLoadingAndRetryOrRemovalAfterFailure() {
        val loading = mutableStateOf(true)
        var cancelled = false
        var retried = false
        var removed = false
        val player = BeatPlayer()
        composeRule.setContent {
            GhostwriterTheme {
                BeatPlayerPanel(
                    beatPlayer = player,
                    isBeatReady = false,
                    isImporting = false,
                    beatDisplayName = "",
                    onImportBeat = {},
                    onReassignBeat = { removed = true },
                    isReassigningBeat = false,
                    waveformAmplitudes = intArrayOf(),
                    isWaveformLoading = loading.value,
                    markers = emptyList(),
                    onAddMarker = {},
                    onMarkerClick = {},
                    onMarkerMove = { _, _ -> },
                    onCancelWaveformPreparation = { cancelled = true },
                    cancelRemovesImportedBeat = true,
                    waveformPreparationCancelled = false,
                    waveformPreparationFailed = !loading.value,
                    onRetryWaveformPreparation = { retried = true },
                )
            }
        }

        composeRule.onNodeWithText("Preparing waveform…").assertExists()
        composeRule.onNodeWithText("Import beat").assertDoesNotExist()
        composeRule.onNodeWithText("Retry").assertDoesNotExist()
        composeRule.onNodeWithText("Remove beat").assertDoesNotExist()
        composeRule.onNodeWithText("Cancel import").performClick()
        composeRule.runOnIdle {
            assertTrue(cancelled)
            loading.value = false
        }
        composeRule.onNodeWithText("Preparing waveform…").assertDoesNotExist()
        composeRule.onNodeWithText("Cancel import").assertDoesNotExist()
        composeRule.onNodeWithText("Retry").performClick()
        composeRule.onNodeWithText("Remove beat").performClick()
        composeRule.runOnIdle {
            assertTrue(retried)
            assertTrue(removed)
        }
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
        composeRule.onNodeWithContentDescription("Play").assertIsEnabled()
        composeRule.onNodeWithContentDescription("Play from start").assertIsEnabled()
        composeRule.onNodeWithText("Cancel preparation").performClick()
        composeRule.onNodeWithText("Waveform preparation canceled").assertExists()
        composeRule.onNodeWithContentDescription("Play").assertIsEnabled()
        val messageBounds = composeRule.onNodeWithText("Waveform preparation canceled")
            .fetchSemanticsNode().boundsInRoot
        val retryBounds = composeRule.onNodeWithText("Retry")
            .fetchSemanticsNode().boundsInRoot
        assertEquals(messageBounds.center.x, retryBounds.center.x, 1f)
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
    fun beatPlayerReadyState_controlsVolumeAndReassignment() {
        val beatPlayer = BeatPlayer()
        val showPanel = mutableStateOf(true)
        var reassigned = false
        composeRule.setContent {
            GhostwriterTheme {
                if (showPanel.value) BeatPlayerPanel(
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

        composeRule.onNodeWithContentDescription("Mute").performClick()
        composeRule.runOnIdle { showPanel.value = false }
        composeRule.onNodeWithText("Midnight instrumental").assertDoesNotExist()
        composeRule.runOnIdle { showPanel.value = true }
        composeRule.onNodeWithContentDescription("Unmute").performClick()
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
