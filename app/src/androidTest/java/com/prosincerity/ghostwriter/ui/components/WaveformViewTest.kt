package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.click
import androidx.compose.ui.test.longClick
import androidx.compose.ui.test.onNodeWithTag
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
        onClick: (WaveformMarker) -> Unit = {},
    ) {
        val viewport = mutableStateOf(WaveformViewport())
        composeRule.setContent {
            GhostwriterTheme {
                // Keep the label at the top of the waveform below system bars
                // in the edge-to-edge test activity, as in the editor's Scaffold.
                Box(Modifier.fillMaxSize().safeDrawingPadding()) {
                    WaveformView(
                        amplitudes = intArrayOf(1000, 2000, 1000),
                        durationMs = DURATION_MS,
                        currentPositionMs = 0,
                        markers = markers.value,
                        onSeekFinished = onSeek,
                        onAddMarker = {},
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
