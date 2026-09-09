package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun ReassignBeatDialog(
    onConfirm: () -> Unit,
    onDismiss: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Reassign beat?") },
        text = {
            Text(
                "This permanently deletes the current beat file, its waveform cache, " +
                    "and all markers from this project. Your lyrics will not be deleted.",
            )
        },
        confirmButton = {
            TextButton(onClick = onConfirm) { Text("Reassign") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
internal fun LongBeatWarningDialog(
    durationMs: Long,
    cancelRemovesImportedBeat: Boolean,
    onProcess: () -> Unit,
    onCancel: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = { /* Require an explicit choice. */ },
        title = { Text("Long audio file") },
        text = {
            Text(
                "This beat is ${formatPlaybackTime(durationMs)} long. " +
                    "Processing its waveform can take a long time."
            )
        },
        confirmButton = {
            TextButton(onClick = onProcess) { Text("Process anyway") }
        },
        dismissButton = {
            TextButton(onClick = onCancel) {
                Text(if (cancelRemovesImportedBeat) "Cancel import" else "Cancel preparation")
            }
        },
    )
}

@Composable
internal fun WaveformMarkerDialog(
    title: String,
    initialLabel: String,
    positionMs: Long,
    onSave: (String) -> Unit,
    onDelete: (() -> Unit)?,
    onDismiss: () -> Unit,
) {
    var label by remember(title, initialLabel, positionMs) { mutableStateOf(initialLabel) }
    val trimmedLabel = label.trim()

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                OutlinedTextField(
                    value = label,
                    onValueChange = { label = it },
                    label = { Text("Marker name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Text(
                    text = "Position: ${formatPlaybackTime(positionMs)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
                if (onDelete != null) {
                    TextButton(
                        onClick = onDelete,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Text("Delete marker")
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onSave(trimmedLabel) },
                enabled = trimmedLabel.isNotEmpty(),
            ) {
                Text(if (onDelete == null) "Add" else "Rename")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
