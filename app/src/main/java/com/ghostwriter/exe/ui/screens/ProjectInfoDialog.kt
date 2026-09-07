package com.ghostwriter.exe.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
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
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.ghostwriter.exe.data.ProjectMetadata

@Composable
fun ProjectInfoDialog(
    metadata: ProjectMetadata,
    onDismiss: () -> Unit,
    onSave: (ProjectMetadata) -> Unit,
) {
    var editBpm by remember(metadata) { mutableStateOf(metadata.bpm?.toString() ?: "") }
    var editKey by remember(metadata) { mutableStateOf(metadata.key ?: "") }
    var editTimeSignature by remember(metadata) { mutableStateOf(metadata.timeSignature ?: "") }
    var editNotes by remember(metadata) { mutableStateOf(metadata.notes ?: "") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Project Information") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
            ) {
                Text(
                    text = "Title: ${metadata.title}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )

                Spacer(Modifier.height(8.dp))

                val beatDisplay = metadata.beatOriginalName ?: metadata.beatFile ?: "None assigned"
                Text(
                    text = "Assigned Beat: $beatDisplay",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )

                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = editBpm,
                    onValueChange = { input ->
                        editBpm = input.filter { it.isDigit() }
                    },
                    label = { Text("BPM (optional)") },
                    placeholder = { Text("e.g. 90") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = editKey,
                    onValueChange = { editKey = it },
                    label = { Text("Musical Key (optional)") },
                    placeholder = { Text("e.g. C Minor, F# Major") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = editTimeSignature,
                    onValueChange = { editTimeSignature = it },
                    label = { Text("Time Signature (optional)") },
                    placeholder = { Text("e.g. 4/4") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = editNotes,
                    onValueChange = { editNotes = it },
                    label = { Text("Notes (optional)") },
                    placeholder = { Text("Vibe, references, structure notes...") },
                    minLines = 3,
                    maxLines = 5,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val updated = metadata.copy(
                        bpm = editBpm.trim().toIntOrNull(),
                        key = editKey.trim().ifBlank { null },
                        timeSignature = editTimeSignature.trim().ifBlank { null },
                        notes = editNotes.trim().ifBlank { null },
                    )
                    onSave(updated)
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
