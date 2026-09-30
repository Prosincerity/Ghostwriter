package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExposedDropdownMenuAnchorType
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.ui.theme.GhostButtonShape
import com.prosincerity.ghostwriter.data.MarkerLoopRole

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
            TextButton(shape = GhostButtonShape, onClick = onConfirm) { Text("Reassign") }
        },
        dismissButton = {
            TextButton(shape = GhostButtonShape, onClick = onDismiss) { Text("Cancel") }
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
            TextButton(shape = GhostButtonShape, onClick = onProcess) { Text("Process anyway") }
        },
        dismissButton = {
            TextButton(shape = GhostButtonShape, onClick = onCancel) {
                Text(if (cancelRemovesImportedBeat) "Cancel import" else "Cancel preparation")
            }
        },
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun WaveformMarkerDialog(
    title: String,
    initialLabel: String,
    positionMs: Long,
    onSave: (String, MarkerLoopRole) -> Unit,
    onDelete: (() -> Unit)?,
    onDismiss: () -> Unit,
    initialLoopRole: MarkerLoopRole = MarkerLoopRole.NONE,
) {
    var label by remember(title, initialLabel, positionMs) { mutableStateOf(initialLabel) }
    var loopRole by remember(title, initialLabel, positionMs, initialLoopRole) { mutableStateOf(initialLoopRole) }
    var loopExpanded by remember { mutableStateOf(false) }
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
                ExposedDropdownMenuBox(
                    expanded = loopExpanded,
                    onExpandedChange = { loopExpanded = it },
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    OutlinedTextField(
                        value = loopRole.displayName,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Looping") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(loopExpanded) },
                        modifier = Modifier.fillMaxWidth()
                            .menuAnchor(ExposedDropdownMenuAnchorType.PrimaryNotEditable),
                    )
                    ExposedDropdownMenu(expanded = loopExpanded, onDismissRequest = { loopExpanded = false }) {
                        MarkerLoopRole.entries.forEach { role ->
                            DropdownMenuItem(
                                text = { Text(role.displayName) },
                                onClick = { loopRole = role; loopExpanded = false },
                            )
                        }
                    }
                }
                Text(
                    text = "Position: ${formatPlaybackTime(positionMs)}",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
                if (onDelete != null) {
                    TextButton(
                        shape = GhostButtonShape,
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
                shape = GhostButtonShape,
                onClick = { onSave(trimmedLabel, loopRole) },
                enabled = trimmedLabel.isNotEmpty(),
            ) {
                Text(if (onDelete == null) "Add" else "Save")
            }
        },
        dismissButton = {
            TextButton(shape = GhostButtonShape, onClick = onDismiss) { Text("Cancel") }
        },
    )
}
