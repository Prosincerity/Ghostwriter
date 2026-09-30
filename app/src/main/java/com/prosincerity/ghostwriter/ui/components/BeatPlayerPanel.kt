package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ZoomIn
import androidx.compose.material.icons.filled.ZoomOut
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.data.WaveformMarker
import com.prosincerity.ghostwriter.logic.WaveformViewport
import com.prosincerity.ghostwriter.media.BeatPlayer
import com.prosincerity.ghostwriter.ui.theme.GhostButtonShape
import kotlinx.coroutines.delay
import kotlin.time.Duration.Companion.milliseconds

@Composable
internal fun BeatPlayerPanel(
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
            delay(if (isPlaying) 33.milliseconds else 250.milliseconds)
        }
    }

    Card(modifier = modifier.height(176.dp)) {
        if (isWaveformLoading) {
            CenteredPlayerContent {
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
                    shape = GhostButtonShape,
                    onClick = onCancelWaveformPreparation,
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text(if (cancelRemovesImportedBeat) "Cancel import" else "Cancel preparation")
                }
            }
            return@Card
        }

        if (waveformPreparationCancelled) {
            CenteredPlayerContent {
                Text(
                    text = "Waveform preparation canceled",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    shape = GhostButtonShape,
                    onClick = onRetryWaveformPreparation,
                    modifier = Modifier.padding(top = 8.dp),
                ) {
                    Text("Retry")
                }
            }
            return@Card
        }

        if (waveformPreparationFailed) {
            CenteredPlayerContent {
                Text(
                    text = "Couldn't create waveform",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
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
            return@Card
        }

        if (!isBeatReady) {
            CenteredPlayerContent {
                Text(
                    text = if (isReassigningBeat) "Removing beat…" else "No beat selected",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Button(
                    shape = GhostButtonShape,
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
                text = if (beatPlayer.isPreparingLoop) "Preparing loop audio…" else
                    "${formatPlaybackTime(currentPositionMs.toLong())}/" + formatPlaybackTime(durationMs.toLong()),
                maxLines = 1,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

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

@Composable
private fun CenteredPlayerContent(content: @Composable ColumnScope.() -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        content = content,
    )
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
