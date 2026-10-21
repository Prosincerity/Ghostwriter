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
    @get:Rule val composeRule = createComposeRule()
    private val requestedFeedback = mutableListOf<HapticFeedbackType?>()

    @Test
    fun addingAfterPositionAndRateChange_usesCurrentFramesAndCallbacks() {
        val markers = mutableStateOf<List<WaveformMarker>>(emptyList())
        val position = mutableStateOf<Long?>(250)
        val rate = mutableStateOf(8000)
        var oldChanges = 0
        var oldDismissals = 0
        var newDismissals = 0
        val change = mutableStateOf<(List<WaveformMarker>, String, String, HapticFeedbackType?) -> Unit>(
            { _, _, _, _ -> oldChanges++ },
        )
        val dismiss = mutableStateOf<() -> Unit>({ oldDismissals++ })
        composeRule.setContent {
            GhostwriterTheme {
                EditorMarkerDialogs(markers.value, position.value, null, change.value,
                    dismiss.value, {}, rate.value)
            }
        }
        composeRule.runOnIdle {
            position.value = 1500
            rate.value = 44100
            markers.value = listOf(WaveformMarker("Existing end", 2000, MarkerLoopRole.END))
            change.value = { updated, success, failure, feedback ->
                assertEquals("Marker added", success)
                assertEquals("Couldn't save marker", failure)
                markers.value = updated
                requestedFeedback += feedback
            }
            dismiss.value = { newDismissals++; position.value = null }
        }
        composeRule.onNodeWithText("Marker name").performTextReplacement("New end")
        composeRule.onNodeWithText("Looping").performClick()
        composeRule.onNodeWithText("End").performClick()
        composeRule.onNodeWithText("Add").performClick()
        composeRule.runOnIdle {
            assertEquals(0, oldChanges)
            assertEquals(0, oldDismissals)
            assertEquals(1, newDismissals)
            assertNull(position.value)
            assertEquals(MarkerLoopRole.NONE, markers.value.first().loopRole)
            assertEquals(WaveformMarker("New end", 1500, MarkerLoopRole.END).withSampleRate(44100), markers.value.last())
            assertEquals(listOf(HapticFeedbackType.SegmentTick), requestedFeedback)
        }
    }

    @Test
    fun changingEditSelectionAndHandlers_savesWithCurrentOwner() {
        val first = WaveformMarker("First", 500)
        val second = WaveformMarker("Second", 1000)
        val markers = mutableStateOf(listOf(first, second))
        val editing = mutableStateOf<WaveformMarker?>(first)
        var oldChanges = 0
        var oldDismissals = 0
        var newDismissals = 0
        val change = mutableStateOf<(List<WaveformMarker>, String, String, HapticFeedbackType?) -> Unit>(
            { _, _, _, _ -> oldChanges++ },
        )
        val dismiss = mutableStateOf<() -> Unit>({ oldDismissals++ })
        composeRule.setContent {
            GhostwriterTheme {
                EditorMarkerDialogs(markers.value, null, editing.value, change.value, {}, dismiss.value)
            }
        }
        composeRule.runOnIdle {
            editing.value = second
            change.value = { updated, _, _, feedback ->
                markers.value = updated
                requestedFeedback += feedback
            }
            dismiss.value = { newDismissals++; editing.value = null }
        }
        composeRule.onNodeWithText("Marker name").performTextReplacement("Renamed second")
        composeRule.onNodeWithText("Save").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(first, second.copy(label = "Renamed second")), markers.value)
            assertNull(editing.value)
            assertEquals(0, oldChanges)
            assertEquals(0, oldDismissals)
            assertEquals(1, newDismissals)
            assertEquals(listOf<HapticFeedbackType?>(null), requestedFeedback)
        }
    }

    @Test
    fun changingEditSelectionAndHandlers_deletesWithCurrentOwner() {
        val first = WaveformMarker("First", 500)
        val second = WaveformMarker("Second", 1000)
        val markers = mutableStateOf(listOf(first, second))
        val editing = mutableStateOf<WaveformMarker?>(first)
        var oldChanges = 0
        var oldDismissals = 0
        var newDismissals = 0
        val change = mutableStateOf<(List<WaveformMarker>, String, String, HapticFeedbackType?) -> Unit>(
            { _, _, _, _ -> oldChanges++ },
        )
        val dismiss = mutableStateOf<() -> Unit>({ oldDismissals++ })
        composeRule.setContent {
            GhostwriterTheme {
                EditorMarkerDialogs(markers.value, null, editing.value, change.value, {}, dismiss.value)
            }
        }
        composeRule.runOnIdle {
            editing.value = second
            change.value = { updated, _, _, feedback ->
                markers.value = updated
                requestedFeedback += feedback
            }
            dismiss.value = { newDismissals++; editing.value = null }
        }
        composeRule.onNodeWithText("Delete marker").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(first), markers.value)
            assertNull(editing.value)
            assertEquals(0, oldChanges)
            assertEquals(0, oldDismissals)
            assertEquals(1, newDismissals)
            assertEquals(listOf(HapticFeedbackType.LongPress), requestedFeedback)
        }
    }

    @Test fun staleSelection_saveDoesNotMutateNewerMarkers() {
        val selected = WaveformMarker("Gone", 1000)
        val remaining = WaveformMarker("Keep", 2000)
        val markers = mutableStateOf(listOf(remaining))
        val editing = mutableStateOf<WaveformMarker?>(selected)
        render(markers, editing)
        composeRule.onNodeWithText("Save").performClick()
        composeRule.runOnIdle {
            assertEquals(listOf(remaining), markers.value)
            assertNull(editing.value)
            assertEquals(emptyList<HapticFeedbackType?>(), requestedFeedback)
        }
    }

    @Test fun staleSelection_deleteDoesNotMutateNewerMarkers() {
        val selected = WaveformMarker("Gone", 1000)
        val remaining = WaveformMarker("Keep", 2000)
        val markers = mutableStateOf(listOf(remaining))
        val editing = mutableStateOf<WaveformMarker?>(selected)
        render(markers, editing)
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
