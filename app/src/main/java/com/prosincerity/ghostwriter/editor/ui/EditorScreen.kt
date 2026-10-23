package com.prosincerity.ghostwriter.editor.ui

import com.prosincerity.ghostwriter.projects.ui.ProjectInfoDialog

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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
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
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.prosincerity.ghostwriter.R
import com.prosincerity.ghostwriter.editor.data.EditorLyricsSession
import com.prosincerity.ghostwriter.editor.data.EditorSaveQueue
import com.prosincerity.ghostwriter.editor.data.importProjectBeat
import com.prosincerity.ghostwriter.projects.data.ProjectMetadata
import com.prosincerity.ghostwriter.projects.data.ProjectStorage
import com.prosincerity.ghostwriter.waveform.domain.WaveformMarker
import com.prosincerity.ghostwriter.waveform.domain.MarkerLoopRole
import com.prosincerity.ghostwriter.waveform.domain.moveMarker
import com.prosincerity.ghostwriter.settings.Settings as AppSettings
import com.prosincerity.ghostwriter.waveform.data.WaveformExtractor
import com.prosincerity.ghostwriter.waveform.domain.WaveformPreparationState
import com.prosincerity.ghostwriter.waveform.domain.prepareWaveform
import com.prosincerity.ghostwriter.player.ui.rememberBeatPlayback
import com.prosincerity.ghostwriter.player.ui.BeatPlayerPanel
import com.prosincerity.ghostwriter.editor.ui.LongBeatWarningDialog
import com.prosincerity.ghostwriter.editor.ui.LyricsNotepad
import com.prosincerity.ghostwriter.editor.ui.ReassignBeatDialog
import com.prosincerity.ghostwriter.core.ui.theme.GhostButtonShape
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineStart
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.IOException
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicLong

/**
 * Lyric editing, project information, and beat preparation for one project.
 * Lyrics are saved manually, on the autosave tick, on activity stop, and on exit.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditorScreen(
    projectTitle: String,
    onBack: () -> Unit,
    onOpenSettings: () -> Unit,
    onOpenDictionary: () -> Unit = {},
    projectId: String = projectTitle,
    persistLyrics: (File, Int) -> Boolean = { _, _ -> true },
    onStorageFailure: () -> Unit = {},
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val projectDir = remember(projectId) { ProjectStorage.projectDir(context, projectId) }
    var loadedLyrics by rememberSaveable(projectId) {
        mutableStateOf(try {
            EditorSaveQueue.process.pendingLyrics(projectDir) ?: ProjectStorage.loadLatest(projectDir)
        } catch (_: IOException) {
            null
        })
    }
    val lyrics = loadedLyrics
    if (lyrics == null) {
        LaunchedEffect(projectDir) {
            onStorageFailure()
            Toast.makeText(context, "Couldn't read this project's lyrics. Existing files have been kept.", Toast.LENGTH_LONG).show()
            onBack()
        }
        return
    }

    val playback = rememberBeatPlayback(projectId, projectTitle)
    val haptics = LocalHapticFeedback.current
    val lyricTextSettings = remember(context) { AppSettings.getLyricTextSettings(context) }
    val coroutineScope = rememberCoroutineScope()
    val waveformDirectory = remember(projectDir) { ProjectStorage.waveformCacheDirectory(context, projectDir) }
    val lyricsSession = remember(projectDir, projectTitle) { EditorLyricsSession(projectDir, projectTitle, persistLyrics) }

    var intervalSeconds by remember { mutableIntStateOf(AppSettings.getAutosaveIntervalSeconds(context)) }
    var keepCount by remember { mutableIntStateOf(AppSettings.getAutosaveCount(context)) }

    var metadata by remember(projectId) {
        mutableStateOf(ProjectStorage.loadMetadata(projectDir, projectTitle))
    }
    var showInfoDialog by rememberSaveable { mutableStateOf(false) }

    val beatPlayer = playback?.player
    var isBeatReady by remember(projectId) { mutableStateOf(false) }
    var beatFile by remember(projectId) {
        mutableStateOf(ProjectStorage.getProjectBeatFile(projectDir, metadata))
    }
    var isImporting by remember(projectId) { mutableStateOf(false) }
    var isReassigningBeat by remember(projectId) { mutableStateOf(false) }
    var waveformAmplitudes by remember(projectId) { mutableStateOf(IntArray(0)) }
    var waveformPreparation by remember(projectId) {
        mutableStateOf<WaveformPreparationState>(WaveformPreparationState.Idle)
    }
    val isWaveformLoading = beatFile != null && (
        waveformPreparation is WaveformPreparationState.Idle ||
            waveformPreparation is WaveformPreparationState.Loading
        )
    var waveformRevision by remember(projectId) { mutableIntStateOf(0) }
    var waveformCancellation by remember(projectId) { mutableStateOf<AtomicBoolean?>(null) }
    var cancellationRequested by remember(projectId) { mutableStateOf(false) }
    var autoPlayWhenBeatReady by remember(projectId) { mutableStateOf(false) }
    var isImportedBeatPreparation by remember(projectId) { mutableStateOf(false) }
    var approvedLongBeatPath by remember(projectId) { mutableStateOf<String?>(null) }
    var showReassignConfirmation by rememberSaveable(projectId) { mutableStateOf(false) }
    var markerPositionToAdd by remember(projectId) { mutableStateOf<Long?>(null) }
    var markerToEdit by remember(projectId) { mutableStateOf<WaveformMarker?>(null) }
    // Read the text state directly: lifecycle callbacks can precede recomposition.
    fun latestLyrics(): String = loadedLyrics ?: lyrics
    val latestKeepCount = rememberUpdatedState(keepCount)
    val latestWaveformCancellation = rememberUpdatedState(waveformCancellation)
    fun reportStorageFailure() {
        val report = {
            onStorageFailure()
            Toast.makeText(context, "Couldn't update the lyric folder. Local draft kept; reconnect before uninstalling.", Toast.LENGTH_LONG).show()
        }
        if (android.os.Looper.myLooper() == android.os.Looper.getMainLooper()) report()
        else android.os.Handler(android.os.Looper.getMainLooper()).post { report() }
    }

    fun queueSave(content: String, save: () -> Boolean) =
        EditorSaveQueue.process.enqueue(projectDir, content) {
            val saved = runCatching(save).getOrDefault(false)
            if (!saved) reportStorageFailure()
            saved
        }

    val lifecycleOwner = LocalLifecycleOwner.current
    DisposableEffect(lifecycleOwner, lyricsSession) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_STOP) {
                val content = latestLyrics()
                val backups = latestKeepCount.value
                queueSave(content) { lyricsSession.autosave(content, backups) }
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    val projectMutationRevision = remember(projectId) { AtomicLong(0L) }

    fun persistMetadataUpdate(
        updatedMetadata: ProjectMetadata,
        successMessage: String?,
        failureMessage: String,
        successHaptic: HapticFeedbackType? = null,
    ) {
        // Update Compose state immediately so a following marker operation is
        // based on this change rather than an older metadata snapshot.
        metadata = updatedMetadata
        if (isBeatReady) beatPlayer?.setMarkers(updatedMetadata.markers)
        val revision = projectMutationRevision.incrementAndGet()
        val backups = keepCount
        val saving = queueSave(latestLyrics()) {
            ProjectStorage.saveMetadata(projectDir, updatedMetadata) && persistLyrics(projectDir, backups)
        }
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            val saved = saving.await()
            // Compose test scopes can resume off the UI thread after IO.
            withContext(Dispatchers.Main.immediate) {
                if (revision != projectMutationRevision.get()) return@withContext
                if (saved) {
                    successHaptic?.let { haptics.performHapticFeedback(it) }
                    successMessage?.let { message ->
                        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                    }
                } else {
                    metadata = withContext(Dispatchers.IO) {
                        ProjectStorage.loadMetadata(projectDir, projectTitle)
                    }
                    Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    fun persistMarkers(
        markers: List<WaveformMarker>,
        successMessage: String?,
        failureMessage: String,
        successHaptic: HapticFeedbackType? = null,
    ) {
        val beatPlayer = beatPlayer ?: return
        val valid = beatPlayer.hasValidMarkerLoop(markers)
        if (markers.any { it.loopRole != MarkerLoopRole.NONE } && !valid) {
            Toast.makeText(context, "Loop start must be before loop end", Toast.LENGTH_SHORT).show()
            return
        }
        persistMetadataUpdate(
            updatedMetadata = metadata.copy(markers = markers.map { it.withSampleRate(beatPlayer.sampleRate) }),
            successMessage = successMessage,
            failureMessage = failureMessage,
            successHaptic = successHaptic,
        )
    }

    fun removeBeatFromEditor(
        successHaptic: HapticFeedbackType? = null,
        onRemoved: () -> Unit = {},
    ) {
        isReassigningBeat = true
        val removalRevision = projectMutationRevision.incrementAndGet()
        val backups = keepCount
        val removing = EditorSaveQueue.process.submit(projectDir, latestLyrics()) {
            ProjectStorage.removeBeatAndPersist(projectDir, waveformDirectory) {
                persistLyrics(it, backups)
            }
        }
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                val updatedMetadata = removing.await()
                val removed = successHaptic != null && withContext(Dispatchers.IO) {
                    updatedMetadata.beatFile == null &&
                        ProjectStorage.getProjectBeatFile(projectDir, updatedMetadata) == null
                }
                withContext(Dispatchers.Main.immediate) {
                    if (removalRevision == projectMutationRevision.get()) {
                        metadata = updatedMetadata
                        beatFile = null
                        approvedLongBeatPath = null
                        if (removed) haptics.performHapticFeedback(successHaptic)
                        onRemoved()
                    }
                }
            } catch (failure: Exception) {
                if (failure is CancellationException) throw failure
                withContext(Dispatchers.Main.immediate) {
                    reportStorageFailure()
                    if (removalRevision == projectMutationRevision.get()) {
                        // The retained beat was released before removal. Reload
                        // it so transport controls remain usable after a failure.
                        isImportedBeatPreparation = false
                        waveformRevision++
                    }
                }
            } finally {
                isReassigningBeat = false
            }
        }
    }

    // Decoding a full beat can take noticeable time, so it happens once for
    // each assigned/reassigned beat on IO. Playback is loaded independently so
    // waveform preparation, cancellation and retry never interrupt the beat.
    LaunchedEffect(playback, beatFile, waveformRevision) {
        val currentBeat = beatFile
        if (currentBeat == null) {
            waveformAmplitudes = IntArray(0)
            waveformPreparation = WaveformPreparationState.Idle
            isBeatReady = false
            autoPlayWhenBeatReady = false
            isImportedBeatPreparation = false
            return@LaunchedEffect
        }
        if (playback == null || beatPlayer == null) {
            isBeatReady = false
            return@LaunchedEffect
        }

        val removeBeatOnCancellation = isImportedBeatPreparation
        val cancellation = AtomicBoolean(false)
        waveformCancellation = cancellation
        cancellationRequested = false
        waveformPreparation = WaveformPreparationState.Loading
        playback.setBeatTitle(metadata.beatOriginalName ?: currentBeat.nameWithoutExtension)
        isBeatReady = beatPlayer.ensureLoaded(currentBeat)
        if (isBeatReady) beatPlayer.setMarkers(metadata.markers)
        else Toast.makeText(context, "Couldn't play the selected audio file", Toast.LENGTH_SHORT).show()
        // Consume import autoplay before any suspension so a later user pause
        // cannot be overridden by waveform loading or warning approval.
        if (isBeatReady && autoPlayWhenBeatReady) beatPlayer.play()
        autoPlayWhenBeatReady = false
        try {
            val approvedPath = approvedLongBeatPath
            val result = withContext(Dispatchers.IO) {
                prepareWaveform(
                    file = currentBeat,
                    removeBeatOnCancel = removeBeatOnCancellation,
                    approvedLongBeatPath = approvedPath,
                    loadCached = {
                        ProjectStorage.loadCachedWaveform(
                            waveformDirectory,
                            WaveformExtractor.DEFAULT_TARGET_SAMPLE_COUNT,
                        )
                    },
                    readDurationMs = { WaveformExtractor.durationMs(currentBeat) },
                    extract = {
                        ProjectStorage.loadOrExtractWaveform(
                            projectDir = projectDir,
                            beatFile = currentBeat,
                            shouldCancel = cancellation::get,
                            waveformDirectory = waveformDirectory,
                        )
                    },
                    shouldCancel = cancellation::get,
                )
            }
            // A cancellation can arrive while the result waits to resume on Main.
            if (result !is WaveformPreparationState.AwaitingApproval && cancellation.get()) {
                throw CancellationException()
            }
            waveformPreparation = result
            when (result) {
                is WaveformPreparationState.Ready -> waveformAmplitudes = result.amplitudes
                WaveformPreparationState.Failed -> waveformAmplitudes = IntArray(0)
                else -> return@LaunchedEffect
            }
            isImportedBeatPreparation = false
        } catch (cancellation: CancellationException) {
            if (!cancellationRequested) throw cancellation
            if (beatFile == currentBeat) {
                if (removeBeatOnCancellation) {
                    beatPlayer.release()
                    isBeatReady = false
                    removeBeatFromEditor {
                        waveformRevision++
                        isImportedBeatPreparation = false
                    }
                } else {
                    waveformPreparation = WaveformPreparationState.Cancelled
                }
                waveformAmplitudes = IntArray(0)
                autoPlayWhenBeatReady = false
            }
        } finally {
            if (waveformCancellation === cancellation) {
                waveformCancellation = null
                waveformPreparation = waveformPreparation.finish()
            }
        }
    }

    LaunchedEffect(beatPlayer, metadata.markers, isBeatReady, beatPlayer?.sampleRate) {
        if (isBeatReady && beatPlayer != null) {
            val frameMarkers = metadata.markers.map { it.withSampleRate(beatPlayer.sampleRate) }
            if (frameMarkers != metadata.markers) persistMetadataUpdate(
                metadata.copy(markers = frameMarkers), null, "Couldn't save frame marker positions",
            ) else beatPlayer.setMarkers(metadata.markers)
        }
    }

    val importBeatLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.OpenDocument(),
    ) { uri ->
        if (uri == null) return@rememberLauncherForActivityResult

        isImporting = true
        val importRevision = projectMutationRevision.incrementAndGet()
        val backups = keepCount
        val importing = EditorSaveQueue.process.submit(projectDir, latestLyrics()) {
            runCatching {
                importProjectBeat(
                    projectDir = projectDir,
                    projectTitle = projectTitle,
                    originalName = displayNameFor(context, uri),
                    waveformDirectory = waveformDirectory,
                    persistLyrics = { directory ->
                        persistLyrics(directory, backups).also { saved ->
                            if (!saved) reportStorageFailure()
                        }
                    },
                ) { destination ->
                    context.contentResolver.openInputStream(uri)?.use { input ->
                        destination.outputStream().use { output -> input.copyTo(output) }
                    } ?: error("Couldn't read the selected beat")
                }
            }
        }
        coroutineScope.launch(start = CoroutineStart.UNDISPATCHED) {
            try {
                val imported = importing.await()
                if (importRevision != projectMutationRevision.get()) return@launch

                imported.onSuccess { importedBeat ->
                    metadata = importedBeat.metadata
                    approvedLongBeatPath = null
                    autoPlayWhenBeatReady = true
                    isImportedBeatPreparation = true
                    beatFile = importedBeat.file
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

    // Background autosave loop. Settings are re-read every cycle so a
    // change made in the Settings screen takes effect from the next tick
    // onward (the cycle already in progress finishes on its old interval).
    LaunchedEffect(projectId) {
        while (true) {
            delay(intervalSeconds.seconds)
            intervalSeconds = AppSettings.getAutosaveIntervalSeconds(context)
            keepCount = AppSettings.getAutosaveCount(context)
            val contentToSave = latestLyrics()
            val backupsToKeep = keepCount
            queueSave(contentToSave) {
                lyricsSession.autosave(contentToSave, backupsToKeep)
            }.await()
        }
    }

    // Capture the final draft before disposal; the process queue owns its write.
    DisposableEffect(lyricsSession) {
        onDispose {
            latestWaveformCancellation.value?.set(true)
            val content = latestLyrics()
            val backups = latestKeepCount.value
            queueSave(content) { lyricsSession.finish(content, backups) }
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

    val isBeatProcessing = isImporting || isReassigningBeat || isWaveformLoading
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
                    IconButton(shape = GhostButtonShape, onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(
                        shape = GhostButtonShape,
                        onClick = onOpenDictionary,
                        enabled = !isBeatProcessing,
                    ) {
                        Icon(Icons.Filled.Book, contentDescription = "Dictionary")
                    }
                    IconButton(
                        shape = GhostButtonShape,
                        onClick = { showInfoDialog = true },
                        enabled = !isBeatProcessing,
                    ) {
                        Icon(Icons.Filled.Info, contentDescription = "Project Info")
                    }
                    IconButton(shape = GhostButtonShape, onClick = {
                        val contentToSave = latestLyrics()
                        val backupsToKeep = keepCount
                        val saving = queueSave(contentToSave) {
                            lyricsSession.saveManual(contentToSave, backupsToKeep)
                        }
                        coroutineScope.launch {
                            val saved = saving.await()
                            withContext(Dispatchers.Main.immediate) {
                                Toast.makeText(
                                    context,
                                    if (saved) "Saved lyrics"
                                    else "Couldn't save lyrics",
                                    Toast.LENGTH_SHORT,
                                ).show()
                            }
                        }
                    }) {
                        Icon(
                            painter = painterResource(R.drawable.ic_save),
                            contentDescription = "Save",
                        )
                    }
                    IconButton(shape = GhostButtonShape, onClick = onOpenSettings) {
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
                    val updatedMarkers = moveMarker(metadata.markers, marker, positionMs, beatPlayer?.sampleRate ?: 0)
                    if (updatedMarkers != null) {
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
                waveformPreparationCancelled = waveformPreparation is WaveformPreparationState.Cancelled,
                waveformPreparationFailed = waveformPreparation is WaveformPreparationState.Failed,
                onRetryWaveformPreparation = { waveformRevision++ },
                isAwaitingWaveformApproval = waveformPreparation is WaveformPreparationState.AwaitingApproval,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 6.dp),
            )

            LyricsNotepad(
                lyrics = lyrics,
                onLyricsChange = { loadedLyrics = it },
                textSettings = lyricTextSettings,
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(top = 4.dp),
            )
        }
    }

    EditorMarkerDialogs(
        sampleRate = beatPlayer?.sampleRate ?: 0,
        markers = metadata.markers,
        positionToAdd = markerPositionToAdd,
        markerToEdit = markerToEdit,
        onMarkersChange = { markers, successMessage, failureMessage, successHaptic ->
            persistMarkers(markers, successMessage, failureMessage, successHaptic)
        },
        onAddDismiss = { markerPositionToAdd = null },
        onEditDismiss = { markerToEdit = null },
    )

    if (showReassignConfirmation) {
        ReassignBeatDialog(
            onConfirm = {
                showReassignConfirmation = false
                beatPlayer?.release()
                isBeatReady = false
                removeBeatFromEditor(successHaptic = HapticFeedbackType.LongPress) { waveformRevision++ }
            },
            onDismiss = { showReassignConfirmation = false },
        )
    }

    (waveformPreparation as? WaveformPreparationState.AwaitingApproval)?.let { pendingBeat ->
        LongBeatWarningDialog(
            durationMs = pendingBeat.durationMs,
            cancelRemovesImportedBeat = pendingBeat.removeBeatOnCancel,
            onProcess = {
                approvedLongBeatPath = pendingBeat.file.absolutePath
                isImportedBeatPreparation = pendingBeat.removeBeatOnCancel
                beatFile = pendingBeat.file
                waveformRevision++
                waveformPreparation = WaveformPreparationState.Idle
            },
            onCancel = {
                waveformPreparation = WaveformPreparationState.Idle
                if (!pendingBeat.removeBeatOnCancel) {
                    waveformPreparation = WaveformPreparationState.Cancelled
                } else {
                    beatPlayer?.release()
                    isBeatReady = false
                    autoPlayWhenBeatReady = false
                    removeBeatFromEditor {
                        Toast.makeText(context, "Beat import cancelled", Toast.LENGTH_SHORT).show()
                    }
                }
            },
        )
    }
}

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
