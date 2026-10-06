package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.R
import com.prosincerity.ghostwriter.data.ProjectStorage
import com.prosincerity.ghostwriter.data.ProjectSummary
import com.prosincerity.ghostwriter.ui.theme.GhostButtonShape
import java.text.DateFormat
import java.util.Date
import java.util.Locale

/** Local projects, ordered by their most recent lyric or metadata edit. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun HomeScreen(
    existingProjects: List<String>,
    onCreateProject: (String) -> Unit,
    onOpenProject: (String) -> Unit,
    onDeleteProject: (String) -> Unit,
    onRenameProject: (currentTitle: String, renamedTitle: String) -> Unit,
    onOpenSettings: () -> Unit,
    projectSummaries: Map<String, ProjectSummary> = emptyMap(),
    projectTitles: Map<String, String> = emptyMap(),
    onChooseLyricsFolder: () -> Unit = {},
    lyricsFolderReady: Boolean = true,
    storageLoading: Boolean = false,
    storageError: String? = null,
) {
    var showTitleDialog by remember { mutableStateOf(false) }
    var projectMenuFor by remember { mutableStateOf<String?>(null) }
    var projectToDelete by remember { mutableStateOf<String?>(null) }
    var projectToRename by remember { mutableStateOf<String?>(null) }

    val recentProjects = remember(existingProjects, projectSummaries, projectTitles) {
        existingProjects.sortedWith(
            compareByDescending<String> { projectSummaries[it]?.lastEditedAt ?: 0L }
                .thenBy { (projectTitles[it] ?: it).lowercase(Locale.ROOT) },
        )
    }
    val dateFormat = remember { DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT) }
    val projectsEnabled = lyricsFolderReady && !storageLoading

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(stringResource(R.string.app_name))
                        Text(
                            text = stringResource(R.string.app_tagline),
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                },
                actions = {
                    IconButton(shape = GhostButtonShape, onClick = onOpenSettings) {
                        Icon(Icons.Filled.Settings, contentDescription = "Settings")
                    }
                },
            )
        },
        bottomBar = {
            Column(
                modifier = Modifier.fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom))
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                val folderMessage = storageError ?: if (!lyricsFolderReady && !storageLoading) {
                    "Choose a lyric folder to keep your lyrics after uninstall."
                } else null
                folderMessage?.let {
                    Text(
                        it, style = MaterialTheme.typography.bodySmall,
                        color = if (storageError != null) MaterialTheme.colorScheme.error
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (storageLoading) {
                        CircularProgressIndicator(modifier = Modifier.size(16.dp), strokeWidth = 2.dp)
                        Text(
                            "Preparing your projects…", style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.weight(1f).padding(horizontal = 8.dp),
                        )
                    }
                    OutlinedButton(
                        onClick = onChooseLyricsFolder,
                        enabled = !storageLoading,
                        shape = GhostButtonShape,
                        contentPadding = PaddingValues(horizontal = 12.dp, vertical = 8.dp),
                    ) {
                        Icon(Icons.Filled.FolderOpen, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(6.dp))
                        Text(if (lyricsFolderReady) "Change folder" else "Choose folder", style = MaterialTheme.typography.labelMedium)
                    }
                }
            }
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 16.dp),
        ) {
            Button(
                shape = GhostButtonShape,
                onClick = { showTitleDialog = true },
                enabled = projectsEnabled,
                modifier = Modifier.fillMaxWidth().height(52.dp),
            ) {
                Icon(Icons.Filled.Add, contentDescription = null)
                Spacer(Modifier.width(8.dp))
                Text("New project")
            }

            if (existingProjects.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth().weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Surface(
                        shape = MaterialTheme.shapes.large,
                        color = MaterialTheme.colorScheme.secondaryContainer,
                    ) {
                        Icon(
                            Icons.Filled.MusicNote, contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSecondaryContainer,
                            modifier = Modifier.padding(20.dp).size(32.dp),
                        )
                    }
                    Spacer(Modifier.height(20.dp))
                    Text("Start your next track", style = MaterialTheme.typography.headlineSmall, textAlign = TextAlign.Center)
                    Text(
                        "Create a project for your lyrics and beat.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                }
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text("Recent", style = MaterialTheme.typography.titleMedium)
                    Text(
                        "${existingProjects.size}", style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                    )
                }
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    items(recentProjects, key = { it }) { title ->
                        val summary = projectSummaries[title]
                        val displayTitle = projectTitles[title] ?: title
                        Row(
                            modifier = Modifier.fillMaxWidth().clickable(enabled = projectsEnabled) { onOpenProject(title) }
                                .padding(vertical = 12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                        ) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = MaterialTheme.colorScheme.surfaceContainerHigh,
                            ) {
                                Icon(
                                    Icons.Filled.Description, contentDescription = null,
                                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                                    modifier = Modifier.padding(10.dp).size(20.dp),
                                )
                            }
                            Column(Modifier.weight(1f)) {
                                Text(
                                    displayTitle, style = MaterialTheme.typography.titleMedium,
                                    maxLines = 1, overflow = TextOverflow.Ellipsis,
                                )
                                summary?.let {
                                    val details = buildList {
                                        if (it.lastEditedAt > 0) add("Edited ${dateFormat.format(Date(it.lastEditedAt))}")
                                        it.bpm?.let { bpm -> add("$bpm BPM") }
                                        it.key?.let { key -> add(key) }
                                    }
                                    if (details.isNotEmpty()) Text(
                                        details.joinToString(" · "),
                                        style = MaterialTheme.typography.bodySmall,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                                        maxLines = 2, overflow = TextOverflow.Ellipsis,
                                        modifier = Modifier.padding(top = 4.dp),
                                    )
                                }
                            }
                            Box {
                                IconButton(shape = GhostButtonShape, enabled = projectsEnabled, onClick = { projectMenuFor = title }) {
                                    Icon(Icons.Filled.MoreVert, contentDescription = "Project options for $displayTitle")
                                }
                                DropdownMenu(
                                    expanded = projectMenuFor == title,
                                    onDismissRequest = { projectMenuFor = null },
                                ) {
                                    DropdownMenuItem(
                                        text = { Text("Rename") },
                                        onClick = { projectMenuFor = null; projectToRename = title },
                                    )
                                    DropdownMenuItem(
                                        text = { Text("Delete", color = MaterialTheme.colorScheme.error) },
                                        onClick = { projectMenuFor = null; projectToDelete = title },
                                    )
                                }
                            }
                        }
                        HorizontalDivider(modifier = Modifier.padding(start = 52.dp))
                    }
                }
            }
        }
    }

    if (showTitleDialog) {
        NewProjectDialog(
            existingProjects = existingProjects.map { projectTitles[it] ?: it },
            onConfirm = { title ->
                showTitleDialog = false
                onCreateProject(title)
            },
            onDismiss = { showTitleDialog = false },
        )
    }

    projectToRename?.let { title ->
        RenameProjectDialog(
            projectTitle = projectTitles[title] ?: title,
            existingProjects = existingProjects.map { projectTitles[it] ?: it },
            onConfirm = { renamedTitle ->
                projectToRename = null
                onRenameProject(title, renamedTitle)
            },
            onDismiss = { projectToRename = null },
        )
    }

    projectToDelete?.let { title ->
        AlertDialog(
            onDismissRequest = { projectToDelete = null },
            title = { Text("Delete project?") },
            text = {
                Text(
                    "Delete \"${projectTitles[title] ?: title}\" and all of its lyrics, autosaves, project info, and beat? This cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    shape = GhostButtonShape,
                    onClick = {
                        onDeleteProject(title)
                        projectToDelete = null
                    },
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(shape = GhostButtonShape, onClick = { projectToDelete = null }) {
                    Text("Cancel")
                }
            },
        )
    }
}

@Composable
private fun RenameProjectDialog(
    projectTitle: String,
    existingProjects: List<String>,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember(projectTitle) { mutableStateOf(projectTitle) }
    val candidate = ProjectStorage.normalizeTitle(text)
    val alreadyExists = existingProjects.any { existingTitle ->
        existingTitle != projectTitle && existingTitle.equals(candidate, ignoreCase = true)
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Rename track") },
        text = {
            OutlinedTextField(
                value = text,
                onValueChange = { text = it },
                singleLine = true,
                supportingText = if (alreadyExists) {
                    { Text("A project with this name already exists") }
                } else null,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                keyboardActions = KeyboardActions(
                    onDone = { if (!alreadyExists) onConfirm(candidate) },
                ),
            )
        },
        confirmButton = {
            TextButton(shape = GhostButtonShape, onClick = { onConfirm(candidate) }, enabled = !alreadyExists) {
                Text("Rename")
            }
        },
        dismissButton = {
            TextButton(shape = GhostButtonShape, onClick = onDismiss) { Text("Cancel") }
        },
    )
}

@Composable
private fun NewProjectDialog(
    existingProjects: List<String>,
    onConfirm: (String) -> Unit,
    onDismiss: () -> Unit,
) {
    var text by remember { mutableStateOf("") }
    val candidate = ProjectStorage.resolveProjectTitle(text.trim().ifBlank { "Untitled" }, existingProjects)
    val isDuplicate = candidate in existingProjects

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
            TextButton(shape = GhostButtonShape, onClick = { onConfirm(candidate) }) {
                Text(if (isDuplicate) "Open" else "Create")
            }
        },
        dismissButton = {
            TextButton(shape = GhostButtonShape, onClick = onDismiss) { Text("Cancel") }
        },
    )
}
