package com.prosincerity.ghostwriter.projects.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.imePadding
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
import com.prosincerity.ghostwriter.projects.data.ProjectMetadata
import com.prosincerity.ghostwriter.projects.domain.ProjectInfoEdit
import com.prosincerity.ghostwriter.projects.domain.ProjectInfoForm
import com.prosincerity.ghostwriter.core.ui.theme.GhostButtonShape

@Composable
fun ProjectInfoDialog(
    metadata: ProjectMetadata,
    onDismiss: () -> Unit,
    onSave: (ProjectMetadata) -> Unit,
) {
    var form by remember(metadata) { mutableStateOf(ProjectInfoForm.fromMetadata(metadata)) }
    ProjectInfoDialogContent(
        metadata = metadata,
        form = form,
        onEdit = { form = form.edit(it) },
        onDismiss = onDismiss,
        onSave = { form.applyTo(metadata)?.let(onSave) },
    )
}

@Composable
private fun ProjectInfoDialogContent(
    metadata: ProjectMetadata,
    form: ProjectInfoForm,
    onEdit: (ProjectInfoEdit) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
) {
    val invalidBpm = form.isBpmInvalid

    AlertDialog(
        onDismissRequest = onDismiss,
        modifier = Modifier.imePadding(),
        title = { Text("Project Information") },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    // Keep the action buttons outside this scrollable area so
                    // they remain reachable above the on-screen keyboard.
                    .heightIn(max = 320.dp)
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
                    value = form.bpmInput,
                    onValueChange = { onEdit(ProjectInfoEdit.BpmChanged(it)) },
                    label = { Text("BPM (optional)") },
                    placeholder = { Text("e.g. 90") },
                    isError = invalidBpm,
                    supportingText = if (invalidBpm) {
                        { Text("Enter a positive BPM") }
                    } else null,
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = form.keyInput,
                    onValueChange = { onEdit(ProjectInfoEdit.KeyChanged(it)) },
                    label = { Text("Musical Key (optional)") },
                    placeholder = { Text("e.g. C Minor, F# Major") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = form.timeSignatureInput,
                    onValueChange = { onEdit(ProjectInfoEdit.TimeSignatureChanged(it)) },
                    label = { Text("Time Signature (optional)") },
                    placeholder = { Text("e.g. 4/4") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )

                Spacer(Modifier.height(12.dp))

                OutlinedTextField(
                    value = form.notesInput,
                    onValueChange = { onEdit(ProjectInfoEdit.NotesChanged(it)) },
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
                shape = GhostButtonShape,
                onClick = onSave,
                enabled = !invalidBpm,
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(shape = GhostButtonShape, onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
