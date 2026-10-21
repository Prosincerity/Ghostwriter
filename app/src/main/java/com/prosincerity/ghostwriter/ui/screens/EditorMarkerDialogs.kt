package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import com.prosincerity.ghostwriter.data.WaveformMarker
import com.prosincerity.ghostwriter.logic.MarkerEdit
import com.prosincerity.ghostwriter.logic.editMarkers
import com.prosincerity.ghostwriter.ui.components.WaveformMarkerDialog

/** Marker dialog actions use the current list so edits cannot overwrite a newer marker change. */
@Composable
internal fun EditorMarkerDialogs(
    markers: List<WaveformMarker>,
    positionToAdd: Long?,
    markerToEdit: WaveformMarker?,
    onMarkersChange: (List<WaveformMarker>, String, String, HapticFeedbackType?) -> Unit,
    onAddDismiss: () -> Unit,
    onEditDismiss: () -> Unit,
    sampleRate: Int = 0,
) {
    MarkerDialogContent(
        positionToAdd = positionToAdd,
        markerToEdit = markerToEdit,
        onEdit = { edit ->
            val updated = editMarkers(markers, edit, sampleRate)
            if (updated != null) {
                when (edit) {
                    is MarkerEdit.Add -> onMarkersChange(
                        updated, "Marker added", "Couldn't save marker", HapticFeedbackType.SegmentTick,
                    )
                    is MarkerEdit.Update -> onMarkersChange(updated, "Marker saved", "Couldn't save marker", null)
                    is MarkerEdit.Delete -> onMarkersChange(
                        updated, "Marker deleted", "Couldn't save marker", HapticFeedbackType.LongPress,
                    )
                }
            }
        },
        onAddDismiss = onAddDismiss,
        onEditDismiss = onEditDismiss,
    )
}

@Composable
private fun MarkerDialogContent(
    positionToAdd: Long?,
    markerToEdit: WaveformMarker?,
    onEdit: (MarkerEdit) -> Unit,
    onAddDismiss: () -> Unit,
    onEditDismiss: () -> Unit,
) {
    positionToAdd?.let { positionMs ->
        WaveformMarkerDialog(
            title = "Add marker",
            initialLabel = "",
            positionMs = positionMs,
            onSave = { label, role ->
                onEdit(MarkerEdit.Add(positionMs, label, role))
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
                onEdit(MarkerEdit.Update(marker, label, role))
                onEditDismiss()
            },
            onDelete = {
                onEdit(MarkerEdit.Delete(marker))
                onEditDismiss()
            },
            onDismiss = onEditDismiss,
        )
    }
}
