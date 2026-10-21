package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.GenericShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.prosincerity.ghostwriter.data.WaveformMarker
import com.prosincerity.ghostwriter.logic.BeatPanelState
import com.prosincerity.ghostwriter.logic.resolveBeatPanelState
import com.prosincerity.ghostwriter.logic.WaveformViewport
import com.prosincerity.ghostwriter.media.BeatPlayer
import com.prosincerity.ghostwriter.media.BeatLoopMode
import com.prosincerity.ghostwriter.ui.theme.GhostButtonShape
import kotlin.time.Duration.Companion.milliseconds
import kotlinx.coroutines.delay

@Composable
internal fun BeatPlayerPanel(
    beatPlayer: BeatPlayer?,
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
    isAwaitingWaveformApproval: Boolean = false,
    modifier: Modifier = Modifier,
) {
    var isMinimized by rememberSaveable { mutableStateOf(false) }
    var isPlaying by remember(beatPlayer) { mutableStateOf(false) }
    var currentPositionMs by remember(beatPlayer) { mutableIntStateOf(0) }
    var durationMs by remember(beatPlayer) { mutableIntStateOf(0) }
    var waveformViewport by remember(durationMs) { mutableStateOf(WaveformViewport()) }
    var waveformWidthPx by remember { mutableFloatStateOf(0f) }
    val panelState = resolveBeatPanelState(
        hasPlayer = beatPlayer != null,
        isBeatReady = isBeatReady,
        isImporting = isImporting,
        isReassigningBeat = isReassigningBeat,
        isWaveformLoading = isWaveformLoading,
        cancelRemovesImportedBeat = cancelRemovesImportedBeat,
        waveformPreparationCancelled = waveformPreparationCancelled,
        waveformPreparationFailed = waveformPreparationFailed,
        isAwaitingWaveformApproval = isAwaitingWaveformApproval,
    )

    // MediaPlayer has no Compose-observable position state. Poll only while
    // this screen owns a successfully loaded player so the waveform and clock
    // stay in sync with playback.
    LaunchedEffect(beatPlayer, isBeatReady, isMinimized) {
        if (!isBeatReady || beatPlayer == null || isMinimized) return@LaunchedEffect

        while (true) {
            currentPositionMs = beatPlayer.currentPositionMs
            durationMs = beatPlayer.durationMs
            isPlaying = beatPlayer.isPlaying
            delay(if (isPlaying) 33.milliseconds else 250.milliseconds)
        }
    }

    if (isMinimized) {
        Box(modifier = modifier, contentAlignment = Alignment.TopCenter) {
            BeatPlayerTab(
                isMinimized = true,
                onClick = { isMinimized = false },
            )
        }
        return
    }

    Card(
        modifier = modifier.height(172.dp),
        shape = MaterialTheme.shapes.large,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceContainer),
    ) {
        Column(modifier = Modifier.fillMaxSize().padding(horizontal = 10.dp, vertical = 2.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = if (panelState == BeatPanelState.Waveform) beatDisplayName else "",
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.weight(1f).padding(end = 4.dp),
                )
                BeatPlayerTab(isMinimized = false, onClick = { isMinimized = true })
                Row(
                    modifier = Modifier.weight(1f),
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    if (panelState == BeatPanelState.Waveform) {
                        WaveformZoomButton(
                            imageVector = Icons.Filled.ZoomOut,
                            contentDescription = "Zoom out waveform",
                            onClick = {
                                waveformViewport = waveformViewport.zoomBy(
                                    scaleFactor = 0.5f,
                                    focalXpx = waveformWidthPx / 2f,
                                    viewportWidthPx = waveformWidthPx,
                                )
                            },
                            enabled = waveformWidthPx > 0f &&
                                waveformViewport.zoom > WaveformViewport.MIN_ZOOM,
                        )
                        WaveformZoomButton(
                            imageVector = Icons.Filled.ZoomIn,
                            contentDescription = "Zoom in waveform",
                            onClick = {
                                waveformViewport = waveformViewport.zoomBy(
                                    scaleFactor = 2f,
                                    focalXpx = waveformWidthPx / 2f,
                                    viewportWidthPx = waveformWidthPx,
                                )
                            },
                            enabled = waveformWidthPx > 0f &&
                                waveformViewport.zoom < WaveformViewport.MAX_ZOOM,
                        )
                    }
                }
            }
            when (panelState) {
                BeatPanelState.AwaitingWaveformApproval -> PlayerStatusMessage(
                    message = "Waiting for waveform approval…",
                    modifier = Modifier.weight(1f),
                )
                BeatPanelState.Connecting -> PlayerStatusMessage(
                    message = "Connecting beat player…",
                    modifier = Modifier.weight(1f),
                )
                is BeatPanelState.PreparingWaveform -> PlayerStatusMessage(
                    message = "Preparing waveform…",
                    modifier = Modifier.weight(1f),
                ) {
                    Button(
                        shape = GhostButtonShape,
                        onClick = onCancelWaveformPreparation,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Text(if (panelState.cancelRemovesImportedBeat) "Cancel import" else "Cancel preparation")
                    }
                }
                BeatPanelState.WaveformCancelled -> PlayerStatusMessage(
                    message = "Waveform preparation canceled",
                    modifier = Modifier.weight(1f),
                ) {
                    Button(
                        shape = GhostButtonShape,
                        onClick = onRetryWaveformPreparation,
                        modifier = Modifier.padding(top = 8.dp),
                    ) {
                        Text("Retry")
                    }
                }
                BeatPanelState.WaveformFailed -> PlayerStatusMessage(
                    message = "Couldn't create waveform",
                    modifier = Modifier.weight(1f),
                ) {
                    Row(
                        modifier = Modifier.padding(top = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(shape = GhostButtonShape, onClick = onRetryWaveformPreparation) {
                            Text("Retry")
                        }
                        TextButton(shape = GhostButtonShape, onClick = onReassignBeat) {
                            Text("Remove beat")
                        }
                    }
                }
                BeatPanelState.NoBeat, BeatPanelState.ImportingBeat, BeatPanelState.RemovingBeat -> PlayerStatusMessage(
                    message = if (panelState == BeatPanelState.RemovingBeat) "Removing beat…" else "No beat selected",
                    modifier = Modifier.weight(1f),
                ) {
                    Button(
                        shape = GhostButtonShape,
                        onClick = onImportBeat,
                        enabled = panelState == BeatPanelState.NoBeat,
                        modifier = Modifier.padding(top = 4.dp),
                    ) {
                        Text(
                            when (panelState) {
                                BeatPanelState.RemovingBeat -> "Removing…"
                                BeatPanelState.ImportingBeat -> "Importing…"
                                else -> "Import beat"
                            }
                        )
                    }
                }
                BeatPanelState.Waveform -> beatPlayer?.let { player ->
                    Column(
                        modifier = Modifier.weight(1f),
                    ) {
                        WaveformView(
                            amplitudes = waveformAmplitudes,
                            durationMs = durationMs.toLong(),
                            currentPositionMs = currentPositionMs.toLong(),
                            markers = markers,
                            onSeekFinished = { positionMs ->
                                player.seekTo(positionMs.toInt())
                                currentPositionMs = positionMs.toInt()
                            },
                            onAddMarker = onAddMarker,
                            onMarkerClick = { marker ->
                                val markerPositionMs = marker.positionMs
                                    .coerceIn(0L, durationMs.toLong())
                                    .toInt()
                                player.seekTo(markerPositionMs)
                                currentPositionMs = markerPositionMs
                                onMarkerClick(marker)
                            },
                            onMarkerMoveFinished = onMarkerMove,
                            viewport = waveformViewport,
                            loopEnabled = player.loopMode == BeatLoopMode.MARKERS,
                            onViewportChange = { waveformViewport = it },
                            modifier = Modifier
                                .fillMaxWidth()
                                .onSizeChanged { waveformWidthPx = it.width.toFloat() }
                                .weight(1f),
                        )
                        Text(
                            text = if (player.isPreparingLoop) "Preparing loop audio…" else
                                "${formatPlaybackTime(currentPositionMs.toLong())} / " + formatPlaybackTime(durationMs.toLong()),
                            maxLines = 1,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    }
                }
            }
            if (panelState == BeatPanelState.Waveform && beatPlayer != null) {
                BeatPlaybackControls(
                    beatPlayer = beatPlayer,
                    isPlaying = isPlaying,
                    onPlayFromStart = {
                        beatPlayer.seekTo(0)
                        beatPlayer.play()
                        currentPositionMs = 0
                        isPlaying = beatPlayer.isPlaying
                    },
                    onTogglePlayback = {
                        beatPlayer.togglePlayPause()
                        isPlaying = beatPlayer.isPlaying
                    },
                    onReassignBeat = onReassignBeat,
                    isReassigningBeat = isReassigningBeat,
                )
            }
        }
    }
}

@Composable
private fun BeatPlayerTab(isMinimized: Boolean, onClick: () -> Unit) {
    Surface(
        onClick = onClick,
        shape = BeatPlayerTabShape,
        color = MaterialTheme.colorScheme.primary,
        contentColor = MaterialTheme.colorScheme.onPrimary,
        modifier = Modifier.width(132.dp).height(32.dp).semantics {
            contentDescription = if (isMinimized) "Open beat player" else "Minimize beat player"
        },
    ) {
        Box(contentAlignment = Alignment.Center) {
            BasicText(
                text = "Beat Player",
                style = MaterialTheme.typography.labelLarge.copy(
                    color = MaterialTheme.colorScheme.onPrimary,
                    lineHeight = 1.2.em,
                ),
                maxLines = 1,
                softWrap = false,
                autoSize = TextAutoSize.StepBased(minFontSize = 8.sp, maxFontSize = 14.sp),
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 6.dp),
            )
        }
    }
}

private val BeatPlayerTabShape = GenericShape { size, _ ->
    val slant = size.height * 0.35f
    moveTo(0f, 0f)
    lineTo(size.width, 0f)
    lineTo(size.width - slant, size.height)
    lineTo(slant, size.height)
    close()
}

@Composable
private fun PlayerStatusMessage(
    message: String,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit = {},
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        content()
    }
}

@Composable
private fun WaveformZoomButton(
    imageVector: ImageVector,
    contentDescription: String,
    enabled: Boolean,
    onClick: () -> Unit,
) {
    IconButton(
        shape = GhostButtonShape,
        onClick = onClick,
        enabled = enabled,
        modifier = Modifier.size(32.dp),
    ) {
        Icon(
            imageVector = imageVector,
            contentDescription = contentDescription,
            modifier = Modifier.size(20.dp),
        )
    }
}
