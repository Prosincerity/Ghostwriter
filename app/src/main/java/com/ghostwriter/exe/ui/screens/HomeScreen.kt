package com.ghostwriter.exe.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp

/**
 * Phase 1's screen becomes the editor; this is the new entry point.
 * Just "New file", a disabled "Import" placeholder for later, and a
 * recent-projects list so autosaved work is actually reachable again.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    existingProjects: List<String>,
    onCreateProject: (String) -> Unit,
    onOpenProject: (String) -> Unit,
    onOpenSettings: () -> Unit,
) {
    var showTitleDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Gh0stwrit3r.exe") },
                actions = {
                    IconButton(onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Spacer(Modifier.height(32.dp))

            Button(onClick = { showTitleDialog = true }, modifier = Modifier.fillMaxWidth()) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("New file")
            }

            Spacer(Modifier.height(12.dp))

            OutlinedButton(
                onClick = { /* Import — implemented in a later phase */ },
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Import (coming soon)")
            }

            if (existingProjects.isNotEmpty()) {
                Spacer(Modifier.height(32.dp))
                Text(
                    "Recent",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.fillMaxWidth(),
                )
                Spacer(Modifier.height(8.dp))
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f, fill = false)) {
                    items(existingProjects) { title ->
                        ListItem(
                            headlineContent = { Text(title) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { onOpenProject(title) },
                        )
                    }
                }
            }
        }
    }

    if (showTitleDialog) {
        NewProjectDialog(
            existingProjects = existingProjects,
            onConfirm = { title ->
                showTitleDialog = false
                onCreateProject(title)
            },
            onDismiss = { showTitleDialog = false },
        )
    }
}

@Composable
private fun NewProjectDialog(
    existingProjects: List<String>,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    val candidate = text.trim().ifBlank { "Untitled" }
    val isDuplicate = existingProjects.any { it.equals(candidate, ignoreCase = true) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Name this track") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                placeholder = { Text("Untitled") },
                supportingText = if (isDuplicate) {
                    { Text("Track already exists (will open existing)") }
                } else null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { onConfirm(candidate) }
                ),
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(candidate) }) {
                Text(if (isDuplicate) "Open" else "Create")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        },
    )
}
