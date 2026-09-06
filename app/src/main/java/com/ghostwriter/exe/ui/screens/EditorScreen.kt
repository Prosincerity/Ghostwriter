package com.ghostwriter.exe.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.ghostwriter.exe.R
import com.ghostwriter.exe.data.ProjectMetadata
import com.ghostwriter.exe.data.ProjectStorage
import com.ghostwriter.exe.data.Settings as AppSettings
import com.ghostwriter.exe.media.BeatPlayer
import java.io.File
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * The Phase 1 notepad, now with a title bar (back + settings) and a
 * background autosave loop.
 *
 * Text is kept in rememberSaveable so it survives recomposition/rotation
 * without a disk round-trip; the disk copy is only touched on the
 * autosave tick and when leaving the screen.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    projectTitle: String,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val projectDir = remember(projectTitle) { ProjectStorage.projectDir(context, projectTitle) }

    var lyrics by rememberSaveable(projectTitle) {
        mutableStateOf(ProjectStorage.loadLatest(projectDir))
    }

    var intervalSeconds by remember { mutableIntStateOf(AppSettings.getAutosaveIntervalSeconds(context)) }
    var keepCount by remember { mutableIntStateOf(AppSettings.getAutosaveCount(context)) }

    var metadata by remember(projectTitle) {
        mutableStateOf(ProjectStorage.loadMetadata(projectDir, projectTitle))
    }
    var showInfoDialog by rememberSaveable { mutableStateOf(false) }

    // The temporary first player integration loads only the conventional
    // project-local beat.mp3. Beat assignment/library UI will replace this
    // direct lookup once that feature is added.
    val beatPlayer = remember(projectTitle) { BeatPlayer() }
    var isBeatReady by remember(projectTitle) { mutableStateOf(false) }
    val beatFile = remember(projectTitle) { File(projectDir, "beat.mp3") }
    LaunchedEffect(projectTitle) {
        isBeatReady = beatPlayer.load(beatFile)
    }

    // MediaPlayer owns native audio resources, so it must be released when
    // this editor is left or a different project is opened.
    DisposableEffect(beatPlayer) {
        onDispose { beatPlayer.release() }
    }

    // Background autosave loop. Settings are re-read every cycle so a
    // change made in the Settings screen takes effect from the next tick
    // onward (the cycle already in progress finishes on its old interval).
    LaunchedEffect(projectTitle) {
        while (true) {
            delay(intervalSeconds.seconds)
            intervalSeconds = AppSettings.getAutosaveIntervalSeconds(context)
            keepCount = AppSettings.getAutosaveCount(context)
            withContext(Dispatchers.IO) {
                ProjectStorage.rotateAndSave(projectDir, lyrics, keepCount)
            }
        }
    }

    // Save immediately on leaving the screen, so nothing is lost between
    // autosave ticks (e.g. navigating to Settings or back to Home).
    DisposableEffect(projectTitle) {
        onDispose {
            ProjectStorage.rotateAndSave(projectDir, lyrics, keepCount)
        }
    }

    if (showInfoDialog) {
        ProjectInfoDialog(
            metadata = metadata,
            onDismiss = { showInfoDialog = false },
            onSave = { updatedMeta ->
                coroutineScope.launch(Dispatchers.IO) {
                    ProjectStorage.saveMetadata(projectDir, updatedMeta)
                    metadata = updatedMeta
                    withContext(Dispatchers.Main) {
                        Toast.makeText(context, "Project info saved", Toast.LENGTH_SHORT).show()
                    }
                }
                showInfoDialog = false
            },
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = projectTitle,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showInfoDialog = true }) {
                        Icon(Icons.Filled.Info, contentDescription = "Project Info")
                    }
                    IconButton(onClick = {
                        coroutineScope.launch(Dispatchers.IO) {
                            ProjectStorage.saveManual(projectDir, projectTitle, lyrics, keepCount)
                            withContext(Dispatchers.Main) {
                                Toast.makeText(
                                    context,
                                    "Saved ${ProjectStorage.sanitizeTitle(projectTitle)}.txt",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_save),
                            contentDescription = "Save",
                        )
                    }
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
                .imePadding()
                .padding(horizontal = 16.dp),
        ) {
            BeatPlayerPanel(
                beatPlayer = beatPlayer,
                isBeatReady = isBeatReady,
                beatFileName = beatFile.name,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 12.dp),
            )

            TextField(
                value = lyrics,
                onValueChange = { lyrics = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 8.dp),
                placeholder = { Text("Start writing...") },
                textStyle = MaterialTheme.typography.bodyLarge,
                keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.background,
                    unfocusedContainerColor = MaterialTheme.colorScheme.background,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                ),
            )
        }
    }
}

@Composable
private fun BeatPlayerPanel(
    beatPlayer: BeatPlayer,
    isBeatReady: Boolean,
    beatFileName: String,
    modifier: Modifier = Modifier,
) {
    var isPlaying by remember(beatPlayer) { mutableStateOf(false) }
    var currentPositionMs by remember(beatPlayer) { mutableIntStateOf(0) }
    var durationMs by remember(beatPlayer) { mutableIntStateOf(0) }
    var isSeeking by remember(beatPlayer) { mutableStateOf(false) }
    var pendingSeekPositionMs by remember(beatPlayer) { mutableFloatStateOf(0f) }
    var volume by remember(beatPlayer) { mutableFloatStateOf(beatPlayer.volume) }
    var isLooping by remember(beatPlayer) { mutableStateOf(beatPlayer.isLooping) }

    // MediaPlayer has no Compose-observable position state. Poll only while
    // this screen owns a successfully loaded player so the slider and clock
    // stay in sync with playback.
    LaunchedEffect(beatPlayer, isBeatReady) {
        if (!isBeatReady) return@LaunchedEffect

        while (true) {
            if (!isSeeking) {
                currentPositionMs = beatPlayer.currentPositionMs
            }
            durationMs = beatPlayer.durationMs
            isPlaying = beatPlayer.isPlaying
            delay(250)
        }
    }

    Card(modifier = modifier.height(104.dp)) {
        if (!isBeatReady) {
            Text(
                text = "beat.mp3 not found",
                modifier = Modifier.padding(16.dp),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            return@Card
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            Text(
                text = beatFileName,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Slider(
                value = (if (isSeeking) pendingSeekPositionMs else currentPositionMs.toFloat())
                    .coerceIn(0f, durationMs.coerceAtLeast(1).toFloat()),
                onValueChange = { position ->
                    isSeeking = true
                    pendingSeekPositionMs = position
                },
                onValueChangeFinished = {
                    beatPlayer.seekTo(pendingSeekPositionMs.toInt())
                    currentPositionMs = pendingSeekPositionMs.toInt()
                    isSeeking = false
                },
                valueRange = 0f..durationMs.coerceAtLeast(1).toFloat(),
                modifier = Modifier.height(20.dp),
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier.align(Alignment.Center),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            beatPlayer.seekTo(0)
                            beatPlayer.play()
                            currentPositionMs = 0
                            isPlaying = beatPlayer.isPlaying
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.SkipPrevious,
                            contentDescription = "Play from start",
                        )
                    }
                    IconButton(
                        onClick = {
                            beatPlayer.togglePlayPause()
                            isPlaying = beatPlayer.isPlaying
                        },
                    ) {
                        Icon(
                            imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                            contentDescription = if (isPlaying) "Pause" else "Play",
                        )
                    }
                    IconButton(
                        onClick = {
                            isLooping = beatPlayer.toggleLoop()
                        },
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Loop,
                            contentDescription = if (isLooping) "Disable loop" else "Enable loop",
                            tint = if (isLooping) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurfaceVariant
                            },
                        )
                    }
                }
                Slider(
                    value = volume,
                    onValueChange = { newVolume ->
                        beatPlayer.setVolume(newVolume)
                        volume = beatPlayer.volume
                    },
                    valueRange = 0f..1f,
                    modifier = Modifier
                        .align(Alignment.CenterEnd)
                        .height(16.dp)
                        .width(64.dp),
                )
            }
        }
    }
}

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
