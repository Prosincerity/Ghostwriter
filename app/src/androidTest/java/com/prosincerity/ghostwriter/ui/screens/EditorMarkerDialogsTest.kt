package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.data.MarkerLoopRole
import com.prosincerity.ghostwriter.data.WaveformMarker
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class EditorMarkerDialogsTest {
    private companion object {
        val staticMarkers = mutableStateOf<List<WaveformMarker>>(emptyList())
        val staticEditing = mutableStateOf<WaveformMarker?>(null)
    }

    @get:Rule val composeRule = createComposeRule()
    private val requestedFeedback = mutableListOf<HapticFeedbackType?>()

    @Test fun staticHandlers_saveAndDeleteTheCurrentMarker() {
        val marker = WaveformMarker("Original", 500)
        staticMarkers.value = listOf(marker)
        staticEditing.value = marker
        composeRule.setContent {
            GhostwriterTheme {
                EditorMarkerDialogs(
                    staticMarkers.value, null, staticEditing.value,
                    { updated, _, _, _ -> staticMarkers.value = updated },
                    {}, { staticEditing.value = null },
                )
            }
        }
        composeRule.onNodeWithText("Marker name").performTextReplacement("Renamed")
        composeRule.onNodeWithText("Save").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(marker.copy(label = "Renamed")), staticMarkers.value)
            assertNull(staticEditing.value)
            staticEditing.value = staticMarkers.value.single()
        }
        composeRule.onNodeWithText("Delete marker").performClick()
        composeRule.runOnIdle {
            assertEquals(emptyList<WaveformMarker>(), staticMarkers.value)
            assertNull(staticEditing.value)
        }
    }

    @Test fun staleSelection_saveAndDeleteDoNotMutateNewerMarkers() {
        val selected = WaveformMarker("Gone", 1000)
        val remaining = WaveformMarker("Keep", 2000)
        val markers = mutableStateOf(listOf(remaining))
        val editing = mutableStateOf<WaveformMarker?>(selected)
        render(markers, editing)
        composeRule.onNodeWithText("Save").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(remaining), markers.value)
            assertNull(editing.value)
            editing.value = selected
        }
        composeRule.onNodeWithText("Delete marker").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(remaining), markers.value)
            assertNull(editing.value)
            assertEquals(emptyList<HapticFeedbackType?>(), requestedFeedback)
        }
    }

    @Test fun addingMarkerWithDefaultSampleRate_transfersLoopRoleAndDismisses() {
        val markers = mutableStateOf(listOf(WaveformMarker("Old start", 100, MarkerLoopRole.START)))
        val position = mutableStateOf<Long?>(500)
        composeRule.setContent {
            GhostwriterTheme {
                EditorMarkerDialogs(
                    markers = markers.value, positionToAdd = position.value, markerToEdit = null,
                    onMarkersChange = { updated, _, _, feedback ->
                        markers.value = updated
                        requestedFeedback += feedback
                    },
                    onAddDismiss = { position.value = null }, onEditDismiss = {},
                )
            }
        }
        composeRule.onNodeWithText("Marker name").performTextReplacement("New start")
        composeRule.onNodeWithText("Looping").performClick()
        composeRule.onNodeWithText("Start").performClick()
        composeRule.onNodeWithText("Add").performClick()
        composeRule.runOnIdle {
            assertNull(position.value)
            assertEquals(MarkerLoopRole.NONE, markers.value.first().loopRole)
            assertEquals(WaveformMarker("New start", 500, MarkerLoopRole.START), markers.value.last())
            assertEquals(listOf(HapticFeedbackType.SegmentTick), requestedFeedback)
        }
    }

    @Test fun saveAfterFrameMigration_keepsExactPositionAndEndMarker() {
        val selected = WaveformMarker("Start", 1000, MarkerLoopRole.START)
        val markers = mutableStateOf(listOf(selected, WaveformMarker("End", 2000, MarkerLoopRole.END)))
        val editing = mutableStateOf<WaveformMarker?>(selected)
        render(markers, editing)
        composeRule.onNodeWithText("Marker name").performTextReplacement("Renamed start")
        composeRule.runOnIdle { markers.value = markers.value.map { it.withSampleRate(44100) } }
        composeRule.onNodeWithText("Save").performClick()
        composeRule.runOnIdle {
            assertEquals("Renamed start", markers.value[0].label)
            assertEquals(44100L, markers.value[0].frameIndex)
            assertEquals(MarkerLoopRole.START, markers.value[0].loopRole)
            assertEquals(88200L, markers.value[1].frameIndex)
            assertEquals(MarkerLoopRole.END, markers.value[1].loopRole)
            assertNull(editing.value)
            assertEquals(listOf<HapticFeedbackType?>(null), requestedFeedback)
        }
    }

    @Test fun deleteAfterFrameMigration_removesSelectedEndOnly() {
        val selected = WaveformMarker("End", 2000, MarkerLoopRole.END)
        val start = WaveformMarker("Start", 1000, MarkerLoopRole.START)
        val markers = mutableStateOf(listOf(start, selected))
        val editing = mutableStateOf<WaveformMarker?>(selected)
        render(markers, editing)
        composeRule.runOnIdle { markers.value = markers.value.map { it.withSampleRate(44100) } }
        composeRule.onNodeWithText("Delete marker").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(start.withSampleRate(44100)), markers.value)
            assertNull(editing.value)
            assertEquals(listOf(HapticFeedbackType.LongPress), requestedFeedback)
        }
    }

    private fun render(markers: MutableState<List<WaveformMarker>>, editing: MutableState<WaveformMarker?>) {
        composeRule.setContent {
            GhostwriterTheme {
                EditorMarkerDialogs(
                    markers = markers.value,
                    positionToAdd = null,
                    markerToEdit = editing.value,
                    onMarkersChange = { updated, _, _, feedback ->
                        markers.value = updated
                        requestedFeedback += feedback
                    },
                    onAddDismiss = {},
                    onEditDismiss = { editing.value = null },
                    sampleRate = 44100,
                )
            }
        }
    }
}
