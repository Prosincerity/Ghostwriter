package com.prosincerity.ghostwriter.ui.screens

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.ui.Alignment
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TextButton
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
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.R
import com.prosincerity.ghostwriter.data.ProjectMetadata
import com.prosincerity.ghostwriter.data.ProjectStorage
import com.prosincerity.ghostwriter.data.WaveformMarker
import com.prosincerity.ghostwriter.data.Settings as AppSettings
import com.prosincerity.ghostwriter.logic.WaveformExtractor
import com.prosincerity.ghostwriter.logic.WaveformViewport
import com.prosincerity.ghostwriter.media.BeatPlayer
import com.prosincerity.ghostwriter.ui.components.WaveformView
import android.net.Uri
import android.provider.OpenableColumns
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlin.time.Duration.Companion.milliseconds
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import kotlinx.coroutines.withContext
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

private const val LONG_BEAT_WARNING_MS = 5 * 60 * 1_000L

private data class PendingBeatPreparation(
    val file: File,
    val durationMs: Long,
    val removeBeatOnCancel: Boolean,
)

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

    val beatPlayer = remember(projectTitle) { BeatPlayer() }
    var isBeatReady by remember(projectTitle) { mutableStateOf(false) }
    var beatFile by remember(projectTitle) {
        mutableStateOf(ProjectStorage.getProjectBeatFile(projectDir, metadata))
    }
    var isImporting by remember(projectTitle) { mutableStateOf(false) }
    var isReassigningBeat by remember(projectTitle) { mutableStateOf(false) }
    var waveformAmplitudes by remember(projectTitle) { mutableStateOf(IntArray(0)) }
    var isWaveformLoading by remember(projectTitle) { mutableStateOf(false) }
    var waveformRevision by remember(projectTitle) { mutableIntStateOf(0) }
    var waveformCancellation by remember(projectTitle) { mutableStateOf<AtomicBoolean?>(null) }
    var cancellationRequested by remember(projectTitle) { mutableStateOf(false) }
    var waveformPreparationCancelled by remember(projectTitle) { mutableStateOf(false) }
    var waveformPreparationFailed by remember(projectTitle) { mutableStateOf(false) }
    var autoPlayWhenWaveformReady by remember(projectTitle) { mutableStateOf(false) }
    var isImportedBeatPreparation by remember(projectTitle) { mutableStateOf(false) }
    var approvedLongBeatPath by remember(projectTitle) { mutableStateOf<String?>(null) }
    var pendingLongBeatPreparation by remember(projectTitle) {
        mutableStateOf<PendingBeatPreparation?>(null)
    }
    var showReassignConfirmation by rememberSaveable(projectTitle) { mutableStateOf(false) }
    var markerPositionToAdd by remember(projectTitle) { mutableStateOf<Long?>(null) }
    var markerToEdit by remember(projectTitle) { mutableStateOf<WaveformMarker?>(null) }
    val latestLyrics = rememberUpdatedState(lyrics)
    val latestKeepCount = rememberUpdatedState(keepCount)
    val latestWaveformCancellation = rememberUpdatedState(waveformCancellation)
    val projectMutationMutex = remember(projectTitle) { Mutex() }
    val projectMutationRevision = remember(projectTitle) { AtomicLong(0L) }

    fun persistMetadataUpdate(
        updatedMetadata: ProjectMetadata,
        successMessage: String?,
        failureMessage: String,
    ) {
        // Update Compose state immediately so a following marker operation is
        // based on this change rather than an older metadata snapshot.
        metadata = updatedMetadata
        val revision = projectMutationRevision.incrementAndGet()
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            val saved = projectMutationMutex.withLock {
                withContext(Dispatchers.IO) {
                    ProjectStorage.saveMetadata(projectDir, updatedMetadata)
                }
            }
            if (saved == true && revision == projectMutationRevision.get()) {
                successMessage?.let { message ->
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            } else if (saved == false && revision == projectMutationRevision.get()) {
                metadata = withContext(Dispatchers.IO) {
                    ProjectStorage.loadMetadata(projectDir, projectTitle)
                }
                Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Decoding a full beat can take noticeable time, so it happens once for
    // each assigned/reassigned beat on IO. Playback waits for this work so the
    // player never appears before waveform.dat has been written.
    LaunchedEffect(beatFile, waveformRevision) {
        val currentBeat = beatFile
        if (currentBeat == null) {
            waveformAmplitudes = IntArray(0)
            isWaveformLoading = false
            isBeatReady = false
            isImportedBeatPreparation = false
            waveformPreparationCancelled = false
            waveformPreparationFailed = false
            pendingLongBeatPreparation = null
            return@LaunchedEffect
        }

        val removeBeatOnCancellation = isImportedBeatPreparation
        val cancellation = AtomicBoolean(false)
        waveformCancellation = cancellation
        cancellationRequested = false
        waveformPreparationCancelled = false
        waveformPreparationFailed = false
        isWaveformLoading = true
        isBeatReady = false
        try {
            val cachedWaveform = withContext(Dispatchers.IO) {
                ProjectStorage.loadCachedWaveform(
                    projectDir,
                    WaveformExtractor.DEFAULT_TARGET_SAMPLE_COUNT,
                )
            }
            if (cachedWaveform == null && approvedLongBeatPath != currentBeat.absolutePath) {
                val durationMs = withContext(Dispatchers.IO) {
                    WaveformExtractor.durationMs(currentBeat)
                }
                if (shouldWarnBeforeWaveformExtraction(durationMs)) {
                    pendingLongBeatPreparation = PendingBeatPreparation(
                        file = currentBeat,
                        durationMs = durationMs!!,
                        removeBeatOnCancel = isImportedBeatPreparation,
                    )
                    return@LaunchedEffect
                }
            }

            waveformAmplitudes = cachedWaveform ?: withContext(Dispatchers.IO) {
                ProjectStorage.loadOrExtractWaveform(
                    projectDir = projectDir,
                    beatFile = currentBeat,
                    shouldCancel = cancellation::get,
                )
            }
            if (cancellation.get()) throw java.util.concurrent.CancellationException()
            if (waveformAmplitudes.isEmpty()) {
                waveformPreparationFailed = true
                autoPlayWhenWaveformReady = false
                isImportedBeatPreparation = false
                return@LaunchedEffect
            }

            isBeatReady = beatPlayer.load(currentBeat)
            if (isBeatReady && autoPlayWhenWaveformReady) beatPlayer.play()
            autoPlayWhenWaveformReady = false
            isImportedBeatPreparation = false
            if (!isBeatReady) {
                Toast.makeText(context, "Couldn't play the selected audio file", Toast.LENGTH_SHORT).show()
            }
        } catch (cancellation: java.util.concurrent.CancellationException) {
            if (!cancellationRequested) throw cancellation
            if (beatFile == currentBeat) {
                if (removeBeatOnCancellation) {
                    val removalRevision = projectMutationRevision.incrementAndGet()
                    val updatedMetadata = projectMutationMutex.withLock {
                        withContext(Dispatchers.IO) {
                            ProjectStorage.removeBeatFromProject(projectDir)
                            ProjectStorage.loadMetadata(projectDir, projectTitle)
                        }
                    }
                    if (removalRevision == projectMutationRevision.get()) {
                        metadata = updatedMetadata
                        beatFile = null
                        waveformRevision++
                        isImportedBeatPreparation = false
                    }
                } else {
                    waveformPreparationCancelled = true
                }
                waveformAmplitudes = IntArray(0)
                autoPlayWhenWaveformReady = false
            }
        } finally {
            if (waveformCancellation === cancellation) {
                waveformCancellation = null
                isWaveformLoading = false
            }
        }
    }

    val importBeatLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        isImporting = true
        val importRevision = projectMutationRevision.incrementAndGet()
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                val imported = projectMutationMutex.withLock {
                    withContext(Dispatchers.IO) {
                        runCatching {
                            val originalName = displayNameFor(context, uri) ?: "beat.mp3"
                            val extension = originalName.substringAfterLast('.', "").lowercase()
                            require(extension in ProjectStorage.SUPPORTED_AUDIO_EXTENSIONS) {
                                "Choose an MP3, WAV, OGG, FLAC, M4A, or AAC file"
                            }

                            val assigned = ProjectStorage.assignBeatToProject(
                                projectDir = projectDir,
                                originalName = originalName,
                            ) { destination ->
                                context.contentResolver.openInputStream(uri)?.use { input ->
                                    destination.outputStream().use { output -> input.copyTo(output) }
                                } ?: error("Couldn't read the selected beat")
                            }
                            Triple(
                                assigned,
                                ProjectStorage.loadMetadata(projectDir, projectTitle),
                                WaveformExtractor.durationMs(assigned),
                            )
                        }
                    }
                }
                if (importRevision != projectMutationRevision.get()) return@launch

                imported.onSuccess { (assigned, updatedMetadata, durationMs) ->
                    metadata = updatedMetadata
                    approvedLongBeatPath = null
                    if (durationMs != null && durationMs >= LONG_BEAT_WARNING_MS) {
                        pendingLongBeatPreparation = PendingBeatPreparation(
                            file = assigned,
                            durationMs = durationMs,
                            removeBeatOnCancel = true,
                        )
                    } else {
                        autoPlayWhenWaveformReady = true
                        isImportedBeatPreparation = true
                        beatFile = assigned
                        waveformRevision++
                    }
                }.onFailure {
                    if (it is CancellationException) throw it
                    Toast.makeText(
                        context,
                        it.message ?: "Couldn't import the selected beat",
                        Toast.LENGTH_SHORT,
                    ).show()
                }
            } finally {
                isImporting = false
            }
        }
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

    // Cancel decoding before the final save when leaving the screen. The
    // updated-state holders avoid capturing the values from the first
    // composition in this long-lived effect.
    DisposableEffect(projectTitle) {
        onDispose {
            latestWaveformCancellation.value?.set(true)
            ProjectStorage.rotateAndSave(
                projectDir,
                latestLyrics.value,
                latestKeepCount.value,
            )
        }
    }

    if (showInfoDialog) {
        ProjectInfoDialog(
            metadata = metadata,
            onDismiss = { showInfoDialog = false },
            onSave = { updatedMeta ->
                persistMetadataUpdate(
                    updatedMetadata = updatedMeta,
                    successMessage = "Project info saved",
                    failureMessage = "Couldn't save project info",
                )
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
                    IconButton(
                        onClick = { showInfoDialog = true },
                        enabled = !isImporting && !isReassigningBeat && !isWaveformLoading,
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = "Project Info")
                    }
                    IconButton(onClick = {
                        val contentToSave = lyrics
                        coroutineScope.launch {
                            val saved = withContext(Dispatchers.IO) {
                                ProjectStorage.saveManual(projectDir, projectTitle, contentToSave, keepCount)
                            }
                            Toast.makeText(
                                context,
                                if (saved) "Saved ${ProjectStorage.sanitizeTitle(projectTitle)}.txt"
                                else "Couldn't save lyrics",
                                Toast.LENGTH_SHORT,
                            ).show()
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
                isImporting = isImporting,
                beatDisplayName = metadata.beatOriginalName?.let { originalName ->
                    originalName.substringBeforeLast('.', originalName)
                }
                    ?: beatFile?.nameWithoutExtension.orEmpty(),
                onImportBeat = {
                    importBeatLauncher.launch(
                        arrayOf(
                            "audio/mpeg",
                            "audio/wav",
                            "audio/ogg",
                            "audio/flac",
                            "audio/mp4",
                            "audio/aac",
                        )
                    )
                },
                onReassignBeat = { showReassignConfirmation = true },
                isReassigningBeat = isReassigningBeat,
                waveformAmplitudes = waveformAmplitudes,
                isWaveformLoading = isWaveformLoading,
                markers = metadata.markers,
                onAddMarker = { positionMs -> markerPositionToAdd = positionMs },
                onMarkerClick = { marker -> markerToEdit = marker },
                onMarkerMove = { marker, positionMs ->
                    val markerIndex = metadata.markers.indexOfFirst { it === marker }
                    if (markerIndex >= 0 && marker.positionMs != positionMs) {
                        val updatedMarkers = metadata.markers.toMutableList().apply {
                            this[markerIndex] = marker.copy(positionMs = positionMs)
                        }
                        val updatedMetadata = metadata.copy(markers = updatedMarkers)
                        persistMetadataUpdate(
                            updatedMetadata = updatedMetadata,
                            successMessage = null,
                            failureMessage = "Couldn't move marker",
                        )
                    }
                },
                onCancelWaveformPreparation = {
                    cancellationRequested = true
                    waveformCancellation?.set(true)
                },
                cancelRemovesImportedBeat = isImportedBeatPreparation,
                waveformPreparationCancelled = waveformPreparationCancelled,
                waveformPreparationFailed = waveformPreparationFailed,
                onRetryWaveformPreparation = { waveformRevision++ },
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

    markerPositionToAdd?.let { positionMs ->
        WaveformMarkerDialog(
            title = "Add marker",
            initialLabel = "",
            positionMs = positionMs,
            onSave = { label ->
                val updatedMetadata = metadata.copy(
                    markers = metadata.markers + WaveformMarker(label, positionMs),
                )
                persistMetadataUpdate(
                    updatedMetadata = updatedMetadata,
                    successMessage = "Marker added",
                    failureMessage = "Couldn't save marker",
                )
                markerPositionToAdd = null
            },
            onDelete = null,
            onDismiss = { markerPositionToAdd = null },
        )
    }

    markerToEdit?.let { marker ->
        WaveformMarkerDialog(
            title = "Edit marker",
            initialLabel = marker.label,
            positionMs = marker.positionMs,
            onSave = { label ->
                val markerIndex = metadata.markers.indexOfFirst { it === marker }
                if (markerIndex < 0) {
                    markerToEdit = null
                    return@WaveformMarkerDialog
                }
                val updatedMarkers = metadata.markers.toMutableList().apply {
                    this[markerIndex] = WaveformMarker(label, marker.positionMs)
                }
                val updatedMetadata = metadata.copy(markers = updatedMarkers)
                persistMetadataUpdate(
                    updatedMetadata = updatedMetadata,
                    successMessage = "Marker renamed",
                    failureMessage = "Couldn't save marker",
                )
                markerToEdit = null
            },
            onDelete = {
                val markerIndex = metadata.markers.indexOfFirst { it === marker }
                if (markerIndex < 0) {
                    markerToEdit = null
                    return@WaveformMarkerDialog
                }
                val updatedMarkers = metadata.markers.toMutableList().apply { removeAt(markerIndex) }
                val updatedMetadata = metadata.copy(markers = updatedMarkers)
                persistMetadataUpdate(
                    updatedMetadata = updatedMetadata,
                    successMessage = "Marker deleted",
                    failureMessage = "Couldn't save marker",
                )
                markerToEdit = null
            },
            onDismiss = { markerToEdit = null },
        )
    }

    if (showReassignConfirmation) {
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { showReassignConfirmation = false },
            title = { Text("Reassign beat?") },
            text = {
                Text(
                    "This permanently deletes the current beat file, its waveform cache, " +
                        "and all markers from this project. Your lyrics will not be deleted.",
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        showReassignConfirmation = false
                        isReassigningBeat = true
                        beatPlayer.release()
                        isBeatReady = false
                        val removalRevision = projectMutationRevision.incrementAndGet()
                        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
                            try {
                                val updatedMetadata = projectMutationMutex.withLock {
                                    withContext(Dispatchers.IO) {
                                        ProjectStorage.removeBeatFromProject(projectDir)
                                        ProjectStorage.loadMetadata(projectDir, projectTitle)
                                    }
                                }
                                if (removalRevision == projectMutationRevision.get()) {
                                    metadata = updatedMetadata
                                    beatFile = null
                                    approvedLongBeatPath = null
                                    waveformRevision++
                                }
                            } finally {
                                isReassigningBeat = false
                            }
                        }
                    },
                ) {
                    Text("Reassign")
                }
            },
            dismissButton = {
                TextButton(onClick = { showReassignConfirmation = false }) {
                    Text("Cancel")
                }
            },
        )
    }

    pendingLongBeatPreparation?.let { pendingBeat ->
        androidx.compose.material3.AlertDialog(
            onDismissRequest = { /* Choose Process anyway or Cancel import. */ },
            title = { Text("Long audio file") },
            text = {
                Text(
                    "This beat is ${formatPlaybackTime(pendingBeat.durationMs)} long. " +
                        "Processing its waveform can take a long time."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        approvedLongBeatPath = pendingBeat.file.absolutePath
                        autoPlayWhenWaveformReady = true
                        isImportedBeatPreparation = pendingBeat.removeBeatOnCancel
                        beatFile = pendingBeat.file
                        waveformRevision++
                        pendingLongBeatPreparation = null
                    },
                ) {
                    Text("Process anyway")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingLongBeatPreparation = null
                        if (!pendingBeat.removeBeatOnCancel) {
                            waveformPreparationCancelled = true
                            return@TextButton
                        }
                        isReassigningBeat = true
                        val removalRevision = projectMutationRevision.incrementAndGet()
                        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
                            try {
                                val updatedMetadata = projectMutationMutex.withLock {
                                    withContext(Dispatchers.IO) {
                                        ProjectStorage.removeBeatFromProject(projectDir)
                                        ProjectStorage.loadMetadata(projectDir, projectTitle)
                                    }
                                }
                                if (removalRevision == projectMutationRevision.get()) {
                                    metadata = updatedMetadata
                                    beatFile = null
                                    approvedLongBeatPath = null
                                    Toast.makeText(context, "Beat import cancelled", Toast.LENGTH_SHORT).show()
                                }
                            } finally {
                                isReassigningBeat = false
                            }
                        }
                    },
                ) {
                    Text(if (pendingBeat.removeBeatOnCancel) "Cancel import" else "Cancel preparation")
                }
            },
        )
    }
}

@Composable
private fun BeatPlayerPanel(
    beatPlayer: BeatPlayer,
    isBeatReady: Boolean,
    isImporting: Boolean,
    beatDisplayName: String,
    onImportBeat: () -> Unit,
    onReassignBeat: () -> Unit,
    isReassigningBeat: Boolean,
    waveformAmplitudes: IntArray,
    isWaveformLoading: Boolean,
    markers: List<WaveformMarker>,
    onAddMarker: (Long) -> Unit,
    onMarkerClick: (WaveformMarker) -> Unit,
    onMarkerMove: (WaveformMarker, Long) -> Unit,
    onCancelWaveformPreparation: () -> Unit,
    cancelRemovesImportedBeat: Boolean,
    waveformPreparationCancelled: Boolean,
    waveformPreparationFailed: Boolean,
    onRetryWaveformPreparation: () -> Unit,
    modifier: Modifier = Modifier,
) {
    var isPlaying by remember(beatPlayer) { mutableStateOf(false) }
    var currentPositionMs by remember(beatPlayer) { mutableIntStateOf(0) }
    var durationMs by remember(beatPlayer) { mutableIntStateOf(0) }
    var volume by remember(beatPlayer) { mutableFloatStateOf(beatPlayer.volume) }
    var volumeBeforeMute by remember(beatPlayer) { mutableFloatStateOf(beatPlayer.volume) }
    var isMuted by remember(beatPlayer) { mutableStateOf(beatPlayer.volume == 0f) }
    var isLooping by remember(beatPlayer) { mutableStateOf(beatPlayer.isLooping) }
    var waveformViewport by remember(durationMs) { mutableStateOf(WaveformViewport()) }
    var waveformWidthPx by remember { mutableFloatStateOf(0f) }

    // MediaPlayer has no Compose-observable position state. Poll only while
    // this screen owns a successfully loaded player so the waveform and clock
    // stay in sync with playback.
    LaunchedEffect(beatPlayer, isBeatReady) {
        if (!isBeatReady) return@LaunchedEffect

        while (true) {
            currentPositionMs = beatPlayer.currentPositionMs
            durationMs = beatPlayer.durationMs
            isPlaying = beatPlayer.isPlaying
            delay(250.milliseconds)
        }
    }

    Card(modifier = modifier.height(176.dp)) {
        if (isWaveformLoading) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Preparing waveform…",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Text(
                    text = "The player will be available when preparation finishes.",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 4.dp),
                )
                Button(
                    onClick = onCancelWaveformPreparation,
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text(if (cancelRemovesImportedBeat) "Cancel import" else "Cancel preparation")
                }
            }
            return@Card
        }

        if (waveformPreparationCancelled) {
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)) {
                Text(
                    text = "Waveform preparation canceled",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = onRetryWaveformPreparation,
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text("Retry")
                }
            }
            return@Card
        }

        if (waveformPreparationFailed) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = "Couldn't create waveform",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Row(
                    modifier = Modifier.padding(top = 8.dp),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Button(onClick = onRetryWaveformPreparation) {
                        Text("Retry")
                    }
                    TextButton(onClick = onReassignBeat) {
                        Text("Remove beat")
                    }
                }
            }
            return@Card
        }

        if (!isBeatReady) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
            ) {
                Text(
                    text = if (isReassigningBeat) "Removing beat…" else "No beat selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    onClick = onImportBeat,
                    enabled = !isImporting && !isReassigningBeat,
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Text(
                        when {
                            isReassigningBeat -> "Removing…"
                            isImporting -> "Importing…"
                            else -> "Import beat"
                        }
                    )
                }
            }
            return@Card
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 12.dp, vertical = 4.dp),
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = beatDisplayName,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f),
                )
                IconButton(
                    onClick = {
                        waveformViewport = waveformViewport.zoomBy(
                            scaleFactor = 0.5f,
                            focalXpx = waveformWidthPx / 2f,
                            viewportWidthPx = waveformWidthPx,
                        )
                    },
                    enabled = waveformWidthPx > 0f &&
                        waveformViewport.zoom > WaveformViewport.MIN_ZOOM,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.ZoomOut,
                        contentDescription = "Zoom out waveform",
                        modifier = Modifier.size(20.dp),
                    )
                }
                IconButton(
                    onClick = {
                        waveformViewport = waveformViewport.zoomBy(
                            scaleFactor = 2f,
                            focalXpx = waveformWidthPx / 2f,
                            viewportWidthPx = waveformWidthPx,
                        )
                    },
                    enabled = waveformWidthPx > 0f &&
                        waveformViewport.zoom < WaveformViewport.MAX_ZOOM,
                    modifier = Modifier.size(32.dp),
                ) {
                    Icon(
                        imageVector = Icons.Filled.ZoomIn,
                        contentDescription = "Zoom in waveform",
                        modifier = Modifier.size(20.dp),
                    )
                }
            }
            WaveformView(
                amplitudes = waveformAmplitudes,
                durationMs = durationMs.toLong(),
                currentPositionMs = currentPositionMs.toLong(),
                markers = markers,
                onSeekFinished = { positionMs ->
                    beatPlayer.seekTo(positionMs.toInt())
                    currentPositionMs = positionMs.toInt()
                },
                onAddMarker = onAddMarker,
                onMarkerClick = { marker ->
                    val markerPositionMs = marker.positionMs
                        .coerceIn(0L, durationMs.toLong())
                        .toInt()
                    beatPlayer.seekTo(markerPositionMs)
                    currentPositionMs = markerPositionMs
                    onMarkerClick(marker)
                },
                onMarkerMoveFinished = onMarkerMove,
                viewport = waveformViewport,
                onViewportChange = { waveformViewport = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .onSizeChanged { waveformWidthPx = it.width.toFloat() }
                    .weight(1f),
            )
            Text(
                text = "${formatPlaybackTime(currentPositionMs.toLong())}/" +
                    formatPlaybackTime(durationMs.toLong()),
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Box(modifier = Modifier.fillMaxWidth()) {
                TextButton(
                    onClick = onReassignBeat,
                    enabled = !isReassigningBeat,
                    modifier = Modifier
                        .align(Alignment.CenterStart)
                        .height(32.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp),
                ) {
                    Text("Reassign", style = MaterialTheme.typography.labelSmall)
                }
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
                Row(
                    modifier = Modifier.align(Alignment.CenterEnd),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = {
                            if (isMuted) {
                                beatPlayer.setVolume(volumeBeforeMute.takeIf { it > 0f } ?: 1f)
                                volume = beatPlayer.volume
                                isMuted = false
                            } else {
                                if (volume > 0f) volumeBeforeMute = volume
                                beatPlayer.setVolume(0f)
                                volume = 0f
                                isMuted = true
                            }
                        },
                        modifier = Modifier.size(32.dp),
                    ) {
                        Icon(
                            imageVector = if (isMuted) {
                                Icons.AutoMirrored.Filled.VolumeOff
                            } else {
                                Icons.AutoMirrored.Filled.VolumeUp
                            },
                            contentDescription = if (isMuted) "Unmute" else "Mute",
                        )
                    }
                    Slider(
                        value = volume,
                        onValueChange = { newVolume ->
                            beatPlayer.setVolume(newVolume)
                            volume = beatPlayer.volume
                            if (volume > 0f) volumeBeforeMute = volume
                            isMuted = volume == 0f
                        },
                        valueRange = 0f..1f,
                        modifier = Modifier
                            .height(16.dp)
                            .width(56.dp),
                    )
                }
            }
        }
    }
}

@Composable
private fun WaveformMarkerDialog(
    title: String,
    initialLabel: String,
    positionMs: Long,
    onSave: (String) -> Unit,
    onDelete: (() -> Unit)?,
    onDismiss: () -> Unit,
) {
    var label by remember(title, initialLabel, positionMs) { mutableStateOf(initialLabel) }
    val trimmedLabel = label.trim()

    androidx.compose.material3.AlertDialog(
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

internal fun formatPlaybackTime(milliseconds: Long): String {
    val totalSeconds = (milliseconds.coerceAtLeast(0) / 1_000)
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    return "$minutes:${seconds.toString().padStart(2, '0')}"
}

internal fun shouldWarnBeforeWaveformExtraction(durationMs: Long?): Boolean =
    durationMs != null && durationMs >= LONG_BEAT_WARNING_MS

private fun displayNameFor(context: android.content.Context, uri: Uri): String? {
    return context.contentResolver.query(
        uri,
        arrayOf(OpenableColumns.DISPLAY_NAME),
        null,
        null,
        null,
    )?.use { cursor ->
        val nameColumn = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        if (nameColumn >= 0 && cursor.moveToFirst()) cursor.getString(nameColumn) else null
    }
}
