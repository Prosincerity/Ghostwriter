package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.runtime.Composable
import com.prosincerity.ghostwriter.data.WaveformMarker
import com.prosincerity.ghostwriter.ui.components.WaveformMarkerDialog
import com.prosincerity.ghostwriter.logic.replaceLoopMarker
import com.prosincerity.ghostwriter.logic.indexOfSelectedMarker

/** Marker dialog actions use the current list so edits cannot overwrite a newer marker change. */
@Composable
internal fun EditorMarkerDialogs(
    markers: List<WaveformMarker>,
    positionToAdd: Long?,
    markerToEdit: WaveformMarker?,
    onMarkersChange: (List<WaveformMarker>, String, String) -> Unit,
    onAddDismiss: () -> Unit,
    onEditDismiss: () -> Unit,
    sampleRate: Int = 0,
) {
    positionToAdd?.let { positionMs ->
        WaveformMarkerDialog(
            title = "Add marker",
            initialLabel = "",
            positionMs = positionMs,
            onSave = { label, role ->
                val added = markers + WaveformMarker(label, positionMs, role).withSampleRate(sampleRate)
                onMarkersChange(
                    replaceLoopMarker(added, added.lastIndex, added.last()),
                    "Marker added",
                    "Couldn't save marker",
                )
                onAddDismiss()
            },
            onDelete = null,
            onDismiss = onAddDismiss,
        )
    }

    markerToEdit?.let { marker ->
        WaveformMarkerDialog(
            title = "Edit marker",
            initialLabel = marker.label,
            positionMs = marker.positionMs,
            initialLoopRole = marker.loopRole,
            onSave = { label, role ->
                val markerIndex = indexOfSelectedMarker(markers, marker)
                if (markerIndex >= 0) {
                    val updatedMarkers = replaceLoopMarker(markers, markerIndex, markers[markerIndex].copy(label = label, loopRole = role))
                    onMarkersChange(updatedMarkers, "Marker saved", "Couldn't save marker")
                }
                onEditDismiss()
            },
            onDelete = {
                val markerIndex = indexOfSelectedMarker(markers, marker)
                if (markerIndex >= 0) {
                    val updatedMarkers = markers.toMutableList().apply { removeAt(markerIndex) }
                    onMarkersChange(updatedMarkers, "Marker deleted", "Couldn't save marker")
                }
                onEditDismiss()
            },
            onDismiss = onEditDismiss,
        )
    }
}
