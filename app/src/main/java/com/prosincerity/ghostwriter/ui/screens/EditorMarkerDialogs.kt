package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.runtime.Composable
import com.prosincerity.ghostwriter.data.WaveformMarker
import com.prosincerity.ghostwriter.ui.components.WaveformMarkerDialog

/** Marker dialog actions use the current list so edits cannot overwrite a newer marker change. */
@Composable
internal fun EditorMarkerDialogs(
    markers: List<WaveformMarker>,
    positionToAdd: Long?,
    markerToEdit: WaveformMarker?,
    onMarkersChange: (List<WaveformMarker>, String, String) -> Unit,
    onAddDismiss: () -> Unit,
    onEditDismiss: () -> Unit,
) {
    positionToAdd?.let { positionMs ->
        WaveformMarkerDialog(
            title = "Add marker",
            initialLabel = "",
            positionMs = positionMs,
            onSave = { label ->
                onMarkersChange(
                    markers + WaveformMarker(label, positionMs),
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
            onSave = { label ->
                val markerIndex = markers.indexOfFirst { it === marker }
                if (markerIndex >= 0) {
                    val updatedMarkers = markers.toMutableList().apply {
                        this[markerIndex] = WaveformMarker(label, marker.positionMs)
                    }
                    onMarkersChange(updatedMarkers, "Marker renamed", "Couldn't save marker")
                }
                onEditDismiss()
            },
            onDelete = {
                val markerIndex = markers.indexOfFirst { it === marker }
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
