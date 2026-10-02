package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.hapticfeedback.HapticFeedback
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.semantics.SemanticsProperties
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.click
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performMouseInput
import androidx.compose.ui.test.performTouchInput
import androidx.compose.ui.test.swipe
import androidx.compose.ui.unit.dp
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.data.MarkerLoopRole
import com.prosincerity.ghostwriter.data.WaveformMarker
import com.prosincerity.ghostwriter.logic.WaveformViewport
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.Assert.assertSame
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class WaveformViewTest {
    @get:Rule
    val composeRule = createComposeRule()

    private val feedback = mutableListOf<HapticFeedbackType>()
    private val haptics = object : HapticFeedback {
        override fun performHapticFeedback(hapticFeedbackType: HapticFeedbackType) {
            feedback += hapticFeedbackType
        }
    }

    @Test fun waveformLongPress_ticksOnceAndRequestsMarker() {
        var addedAt: Long? = null
        renderWaveform(mutableStateOf(emptyList()), onAdd = { addedAt = it })
        composeRule.onNodeWithTag("Waveform").performTouchInput {
            longClick(Offset(width * 0.5f, height * 0.75f))
        }
        composeRule.runOnIdle {
            assertEquals(5000f, addedAt!!.toFloat(), 2f)
            assertEquals(listOf(HapticFeedbackType.SegmentTick), feedback)
        }
    }

    @Test fun scrubbingAcrossMarker_ticksInBothDirections() {
        val markers = mutableStateOf(listOf(marker("Middle", 5000, MarkerLoopRole.NONE)))
        renderWaveform(markers)
        drag(0.1f, 0.9f)
        drag(0.9f, 0.1f)
        composeRule.runOnIdle {
            assertEquals(List(2) { HapticFeedbackType.SegmentFrequentTick }, feedback)
        }
    }

    @Test fun playbackUpdatesTapsAndZoomedPanningAreSilent() {
        val position = mutableStateOf(0L)
        val markers = mutableStateOf(listOf(marker("Middle", 3000, MarkerLoopRole.NONE)))
        renderWaveform(markers, initialViewport = WaveformViewport(zoom = 2f), position = position)
        composeRule.runOnIdle { position.value = 6000 }
        composeRule.onNodeWithTag("Waveform").performTouchInput {
            click(Offset(width * 0.2f, height * 0.75f))
        }
        drag(0.1f, 0.9f)
        composeRule.runOnIdle { assertTrue(feedback.isEmpty()) }
    }

    @Test fun scrubWithoutCrossingMarker_isSilent() {
        renderWaveform(mutableStateOf(listOf(marker("End", 9000, MarkerLoopRole.NONE))))
        drag(0.1f, 0.6f)
        composeRule.runOnIdle { assertTrue(feedback.isEmpty()) }
    }

    @Test
    fun startMarker_remainsDraggableAndEditableAfterAddingAndMovingEnd() {
        val start = marker("Start", 3000, MarkerLoopRole.START)
        val markers = mutableStateOf(listOf(start))
        var clicked: WaveformMarker? = null
        renderWaveform(markers) { clicked = it }

        composeRule.runOnIdle {
            markers.value = (markers.value + marker("End", 8000, MarkerLoopRole.END))
                .map { it.withSampleRate(SAMPLE_RATE) }
        }
        drag(0.8f, 0.9f)
        composeRule.runOnIdle {
            assertSame(start, markers.value[0])
            assertTrue(markers.value[1].positionMs > 8500)
        }
        drag(0.3f, 0.4f)
        composeRule.runOnIdle { assertTrue(markers.value[0].positionMs > 3500) }

        val positionFraction = composeRule.runOnIdle {
            markers.value[0].positionMs.toFloat() / DURATION_MS
        }
        composeRule.onNodeWithTag("Waveform").performTouchInput {
            longClick(Offset(center.x * 2 * positionFraction, center.y * 1.5f))
        }
        composeRule.runOnIdle { assertSame(markers.value[0], clicked) }
    }

    @Test
    fun closeMarkers_bothAcceptDragsOnTheirOwnLines() {
        val start = marker("Start", 4000, MarkerLoopRole.START)
        val end = marker("End", 4300, MarkerLoopRole.END)
        val markers = mutableStateOf(listOf(start, end))
        renderWaveform(markers)

        drag(0.4f, 0.2f)
        composeRule.runOnIdle {
            assertTrue(markers.value[0].positionMs < 2500)
            assertSame(end, markers.value[1])
        }
        drag(0.43f, 0.6f)
        composeRule.runOnIdle { assertTrue(markers.value[1].positionMs > 5500) }
    }

    @Test
    fun drag_includesTheMovementThatFirstCrossesTouchSlop() {
        val markers = mutableStateOf(listOf(marker("Marker", 3000, MarkerLoopRole.NONE)))
        renderWaveform(markers)

        composeRule.onNodeWithTag("Waveform").performTouchInput {
            down(Offset(width * 0.3f, height * 0.75f))
            // A single large first move must not become the drag's anchor.
            moveTo(Offset(width * 0.6f, height * 0.75f))
        }
        assertMarkerPreviewPosition(6000f)
        composeRule.runOnIdle { assertEquals(3000L, markers.value.single().positionMs) }
        composeRule.onNodeWithTag("Waveform").performTouchInput { up() }

        composeRule.runOnIdle { assertEquals(6000f, markers.value.single().positionMs.toFloat(), 2f) }
    }

    @Test
    fun mouseDrag_preservesGrabOffsetThroughDirectionChanges() {
        val markers = mutableStateOf(listOf(marker("Marker", 3000, MarkerLoopRole.NONE)))
        renderWaveform(markers)

        composeRule.onNodeWithTag("Waveform").performMouseInput {
            val grabOffsetPx = 12f
            moveTo(Offset(width * 0.3f + grabOffsetPx, height * 0.75f))
            press()
            moveTo(Offset(width * 0.7f + grabOffsetPx, height * 0.75f))
        }
        assertMarkerPreviewPosition(7000f)
        composeRule.onNodeWithTag("Waveform").performMouseInput {
            val grabOffsetPx = 12f
            moveTo(Offset(width * 0.5f + grabOffsetPx, height * 0.75f))
        }
        assertMarkerPreviewPosition(5000f)
        composeRule.onNodeWithTag("Waveform").performMouseInput { release() }

        composeRule.runOnIdle { assertEquals(5000f, markers.value.single().positionMs.toFloat(), 2f) }
    }

    @Test
    fun zoomedDrag_tracksThePointerAfterMovingBeyondBeatStartAndBack() {
        val markers = mutableStateOf(listOf(marker("Marker", 3000, MarkerLoopRole.NONE)))
        renderWaveform(markers, initialViewport = WaveformViewport(zoom = 2f))

        composeRule.onNodeWithTag("Waveform").performTouchInput {
            down(Offset(width * 0.6f, height * 0.75f))
            moveTo(Offset(width * 0.8f, height * 0.75f))
            moveTo(Offset(-width * 0.1f, height * 0.75f))
        }
        assertMarkerPreviewPosition(0f)
        composeRule.onNodeWithTag("Waveform").performTouchInput {
            moveTo(Offset(width * 0.4f, height * 0.75f))
        }
        assertMarkerPreviewPosition(2000f)
        composeRule.onNodeWithTag("Waveform").performTouchInput { up() }

        composeRule.runOnIdle { assertEquals(2000f, markers.value.single().positionMs.toFloat(), 2f) }
    }

    @Test
    fun cancelledDrag_discardsThePreviewWithoutMovingTheMarker() {
        val markers = mutableStateOf(listOf(marker("Marker", 3000, MarkerLoopRole.NONE)))
        renderWaveform(markers)

        composeRule.onNodeWithTag("Waveform").performTouchInput {
            down(Offset(width * 0.3f, height * 0.75f))
            moveTo(Offset(width * 0.6f, height * 0.75f))
        }
        assertMarkerPreviewPosition(6000f)
        composeRule.onNodeWithTag("Waveform").performTouchInput { cancel() }

        assertMarkerPreviewPosition(3000f)
        composeRule.runOnIdle { assertEquals(3000L, markers.value.single().positionMs) }
    }

    @Test
    fun labelTap_editsWhileTapBelowLabelSeeksToMarker() {
        val marker = marker("Long marker label", 3000, MarkerLoopRole.START)
        val markers = mutableStateOf(listOf(marker))
        var clicked: WaveformMarker? = null
        val seeks = mutableListOf<Long>()
        renderWaveform(markers, onSeek = { seeks += it }, onClick = { clicked = it })
        assertMarkerBoundsContainLine(marker)
        composeRule.onNodeWithTag("Waveform marker Long marker label").performTouchInput {
            click(Offset(right - 1f, height * 0.05f))
        }
        composeRule.runOnIdle {
            assertSame(marker, clicked)
            assertTrue(seeks.isEmpty())
            clicked = null
        }
        composeRule.onNodeWithTag("Waveform marker Long marker label").performTouchInput {
            click(center)
        }
        composeRule.runOnIdle {
            assertNull(clicked)
            assertEquals(listOf(3000L), seeks)
        }
    }

    @Test
    fun labelTap_afterNeighborClipsLeftSideStillEditsCorrectMarker() {
        val start = marker("Start", 4000, MarkerLoopRole.START)
        val end = marker("End label", 4300, MarkerLoopRole.END)
        val markers = mutableStateOf(listOf(start, end))
        var clicked: WaveformMarker? = null
        renderWaveform(markers) { clicked = it }
        assertMarkerBoundsContainLine(start)
        assertMarkerBoundsContainLine(end)
        composeRule.onNodeWithTag("Waveform marker End label").performTouchInput {
            click(Offset(right - 1f, height * 0.05f))
        }
        composeRule.runOnIdle { assertSame(end, clicked) }
    }

    private fun assertMarkerPreviewPosition(expectedPositionMs: Float) {
        val rangeInfo = composeRule.onNodeWithTag("Waveform marker Marker")
            .fetchSemanticsNode().config[SemanticsProperties.ProgressBarRangeInfo]
        assertEquals(expectedPositionMs, rangeInfo.current, 2f)
    }

    private fun assertMarkerBoundsContainLine(marker: WaveformMarker) {
        val waveformBounds = composeRule.onNodeWithTag("Waveform").fetchSemanticsNode().boundsInRoot
        val markerBounds = composeRule.onNodeWithTag("Waveform marker ${marker.label}")
            .fetchSemanticsNode().boundsInRoot
        val lineX = waveformBounds.left + waveformBounds.width * marker.positionMs.toFloat() / DURATION_MS
        assertTrue(
            "${marker.label} semantics $markerBounds must contain its line at x=$lineX",
            markerBounds.contains(Offset(lineX, waveformBounds.center.y)),
        )
    }

    private fun renderWaveform(
        markers: MutableState<List<WaveformMarker>>,
        onSeek: (Long) -> Unit = {},
        initialViewport: WaveformViewport = WaveformViewport(),
        position: MutableState<Long> = mutableStateOf(0L),
        onAdd: (Long) -> Unit = {},
        onClick: (WaveformMarker) -> Unit = {},
    ) {
        val viewport = mutableStateOf(initialViewport)
        composeRule.setContent {
            CompositionLocalProvider(LocalHapticFeedback provides haptics) {
                GhostwriterTheme {
                    // Keep the label at the top of the waveform below system bars
                    // in the edge-to-edge test activity, as in the editor's Scaffold.
                    Box(Modifier.fillMaxSize().safeDrawingPadding()) {
                        WaveformView(
                            amplitudes = intArrayOf(1000, 2000, 1000),
                            durationMs = DURATION_MS,
                            currentPositionMs = position.value,
                            markers = markers.value,
                            onSeekFinished = onSeek,
                            onAddMarker = onAdd,
                            onMarkerClick = onClick,
                            onMarkerMoveFinished = { marker, position ->
                                // Use the editor's reference lookup and list normalization.
                                val index = markers.value.indexOfFirst { it === marker }
                                if (index >= 0) {
                                    markers.value = markers.value.toMutableList().apply {
                                        this[index] = marker.atPositionMs(position, SAMPLE_RATE)
                                    }.map { it.withSampleRate(SAMPLE_RATE) }
                                }
                            },
                            viewport = viewport.value,
                            onViewportChange = { viewport.value = it },
                            modifier = Modifier.fillMaxWidth().height(100.dp),
                        )
                    }
                }
            }
        }
    }

    private fun drag(fromFraction: Float, toFraction: Float) {
        composeRule.onNodeWithTag("Waveform").performTouchInput {
            swipe(
                start = Offset(center.x * 2 * fromFraction, center.y * 1.5f),
                end = Offset(center.x * 2 * toFraction, center.y * 1.5f),
                durationMillis = 300,
            )
        }
    }

    private fun marker(label: String, position: Long, role: MarkerLoopRole) =
        WaveformMarker(label, position, role).withSampleRate(SAMPLE_RATE)

    private companion object {
        const val SAMPLE_RATE = 44100
        const val DURATION_MS = 10000L
    }
}
