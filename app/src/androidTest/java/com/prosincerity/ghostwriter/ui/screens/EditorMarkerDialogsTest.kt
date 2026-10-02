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
