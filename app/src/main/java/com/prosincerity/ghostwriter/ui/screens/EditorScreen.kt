package com.prosincerity.ghostwriter.ui.screens

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import com.prosincerity.ghostwriter.media.BeatPlayer
import com.prosincerity.ghostwriter.ui.components.BeatPlayerPanel
import com.prosincerity.ghostwriter.ui.components.LongBeatWarningDialog
import com.prosincerity.ghostwriter.ui.components.ReassignBeatDialog
import com.prosincerity.ghostwriter.ui.components.WaveformMarkerDialog
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
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

    suspend fun removeBeatAndLoadMetadata(): ProjectMetadata =
        projectMutationMutex.withLock {
            withContext(Dispatchers.IO) {
                ProjectStorage.removeBeatFromProject(projectDir)
                ProjectStorage.loadMetadata(projectDir, projectTitle)
            }
        }

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

    fun persistMarkers(
        markers: List<WaveformMarker>,
        successMessage: String?,
        failureMessage: String,
    ) {
        persistMetadataUpdate(
            updatedMetadata = metadata.copy(markers = markers),
            successMessage = successMessage,
            failureMessage = failureMessage,
        )
    }

    fun removeBeatFromEditor(onRemoved: () -> Unit = {}) {
        isReassigningBeat = true
        val removalRevision = projectMutationRevision.incrementAndGet()
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                val updatedMetadata = removeBeatAndLoadMetadata()
                if (removalRevision == projectMutationRevision.get()) {
                    metadata = updatedMetadata
                    beatFile = null
                    approvedLongBeatPath = null
                    onRemoved()
                }
            } finally {
                isReassigningBeat = false
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
            autoPlayWhenWaveformReady = false
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
                    val updatedMetadata = removeBeatAndLoadMetadata()
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
                            Pair(
                                assigned,
                                ProjectStorage.loadMetadata(projectDir, projectTitle),
                            )
                        }
                    }
                }
                if (importRevision != projectMutationRevision.get()) return@launch

                imported.onSuccess { (assigned, updatedMetadata) ->
                    metadata = updatedMetadata
                    approvedLongBeatPath = null
                    autoPlayWhenWaveformReady = true
                    isImportedBeatPreparation = true
                    beatFile = assigned
                    waveformRevision++
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
                        persistMarkers(
                            markers = updatedMarkers,
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
                persistMarkers(
                    markers = metadata.markers + WaveformMarker(label, positionMs),
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
                persistMarkers(
                    markers = updatedMarkers,
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
                persistMarkers(
                    markers = updatedMarkers,
                    successMessage = "Marker deleted",
                    failureMessage = "Couldn't save marker",
                )
                markerToEdit = null
            },
            onDismiss = { markerToEdit = null },
        )
    }

    if (showReassignConfirmation) {
        ReassignBeatDialog(
            onConfirm = {
                showReassignConfirmation = false
                beatPlayer.release()
                isBeatReady = false
                removeBeatFromEditor { waveformRevision++ }
            },
            onDismiss = { showReassignConfirmation = false },
        )
    }

    pendingLongBeatPreparation?.let { pendingBeat ->
        LongBeatWarningDialog(
            durationMs = pendingBeat.durationMs,
            cancelRemovesImportedBeat = pendingBeat.removeBeatOnCancel,
            onProcess = {
                approvedLongBeatPath = pendingBeat.file.absolutePath
                autoPlayWhenWaveformReady = true
                isImportedBeatPreparation = pendingBeat.removeBeatOnCancel
                beatFile = pendingBeat.file
                waveformRevision++
                pendingLongBeatPreparation = null
            },
            onCancel = {
                pendingLongBeatPreparation = null
                if (!pendingBeat.removeBeatOnCancel) {
                    waveformPreparationCancelled = true
                } else {
                    removeBeatFromEditor {
                        Toast.makeText(context, "Beat import cancelled", Toast.LENGTH_SHORT).show()
                    }
                }
            },
        )
    }
}


internal fun shouldWarnBeforeWaveformExtraction(durationMs: Long?): Boolean =
    durationMs != null && durationMs >= LONG_BEAT_WARNING_MS

private fun displayNameFor(context: Context, uri: Uri): String? {
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
