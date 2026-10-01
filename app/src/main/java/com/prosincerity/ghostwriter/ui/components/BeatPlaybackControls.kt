package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Loop
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.media.BeatPlayer
import com.prosincerity.ghostwriter.ui.theme.GhostButtonShape

@Composable
internal fun BeatPlaybackControls(
    beatPlayer: BeatPlayer,
    isPlaying: Boolean,
    onPlayFromStart: () -> Unit,
    onTogglePlayback: () -> Unit,
    onReassignBeat: () -> Unit,
    isReassigningBeat: Boolean,
) {
    val haptics = LocalHapticFeedback.current
    var volume by remember(beatPlayer) { mutableFloatStateOf(beatPlayer.volume) }
    val isMuted = volume == 0f
    val isLooping = beatPlayer.isLooping

    Row(
        modifier = Modifier.fillMaxWidth().testTag("Beat playback controls"),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        TextButton(
            shape = GhostButtonShape,
            onClick = onReassignBeat,
            enabled = !isReassigningBeat,
            modifier = Modifier.height(32.dp),
            contentPadding = PaddingValues(horizontal = 8.dp),
        ) {
            Text("Reassign", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        IconButton(modifier = Modifier.size(32.dp), shape = GhostButtonShape, onClick = {
            onPlayFromStart()
            haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
        }) {
            Icon(Icons.Filled.SkipPrevious, contentDescription = "Play from start")
        }
        FilledIconButton(
            shape = GhostButtonShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
            ),
            modifier = Modifier.size(32.dp),
            onClick = {
                onTogglePlayback()
                haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
            },
        ) {
            Icon(
                imageVector = if (isPlaying) Icons.Filled.Pause else Icons.Filled.PlayArrow,
                contentDescription = if (isPlaying) "Pause" else "Play",
                modifier = Modifier.size(20.dp),
            )
        }
        FilledIconButton(
            shape = GhostButtonShape,
            colors = IconButtonDefaults.filledIconButtonColors(
                containerColor = if (isLooping) MaterialTheme.colorScheme.secondaryContainer
                    else androidx.compose.ui.graphics.Color.Transparent,
                contentColor = if (isLooping) MaterialTheme.colorScheme.onSecondaryContainer
                    else MaterialTheme.colorScheme.onSurfaceVariant,
            ),
            modifier = Modifier.size(32.dp).semantics { stateDescription = if (isLooping) "On" else "Off" },
            onClick = {
                beatPlayer.toggleLoop()
                haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
            },
        ) {
            Icon(Icons.Filled.Loop,
                contentDescription = if (isLooping) "Disable loop" else "Enable loop")
        }
        IconButton(
            shape = GhostButtonShape,
            modifier = Modifier.size(32.dp),
            onClick = {
                beatPlayer.toggleMute()
                volume = beatPlayer.volume
                haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
            },
        ) {
            Icon(
                imageVector = if (isMuted) Icons.AutoMirrored.Filled.VolumeOff
                    else Icons.AutoMirrored.Filled.VolumeUp,
                contentDescription = if (isMuted) "Unmute" else "Mute",
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        SlimSlider(
            value = volume,
            onValueChange = { newVolume ->
                beatPlayer.setVolume(newVolume)
                volume = beatPlayer.volume
            },
            accentColor = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.height(16.dp).width(56.dp).semantics { contentDescription = "Beat volume" },
        )
    }
}
