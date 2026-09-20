package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.media.BeatPlayer

@Composable
internal fun BeatPlaybackControls(
    beatPlayer: BeatPlayer,
    isPlaying: Boolean,
    onPlayFromStart: () -> Unit,
    onTogglePlayback: () -> Unit,
    onReassignBeat: () -> Unit,
    isReassigningBeat: Boolean,
) {
    var volume by remember(beatPlayer) { mutableFloatStateOf(beatPlayer.volume) }
    var volumeBeforeMute by remember(beatPlayer) { mutableFloatStateOf(beatPlayer.volume) }
    val isMuted = volume == 0f
    var isLooping by remember(beatPlayer) { mutableStateOf(beatPlayer.isLooping) }

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
            IconButton(onClick = onPlayFromStart) {
                Icon(
                    imageVector = Icons.Filled.SkipPrevious,
                    contentDescription = "Play from start",
                )
            }
            IconButton(onClick = onTogglePlayback) {
                Icon(
                    imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                )
            }
            IconButton(onClick = { isLooping = beatPlayer.toggleLoop() }) {
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
                    } else {
                        if (volume > 0f) volumeBeforeMute = volume
                        beatPlayer.setVolume(0f)
                        volume = 0f
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
                },
                valueRange = 0f..1f,
                modifier = Modifier
                    .height(16.dp)
                    .width(56.dp),
            )
        }
    }
}
