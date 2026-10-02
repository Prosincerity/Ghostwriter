package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeOff
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.SkipPrevious
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import com.prosincerity.ghostwriter.media.BeatPlayer
import com.prosincerity.ghostwriter.media.BeatLoopMode
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
    val loopMode = beatPlayer.loopMode
    val loopLabel = stringResource(loopMode.labelRes)

    BoxWithConstraints(Modifier.fillMaxWidth()) {
        // Equal compact side areas anchor Play at the center of the entire row.
        // Keep the 32 dp mute button and 56 dp slider, giving the rest to playback.
        val sideWidth = (maxWidth * 0.3f).coerceIn(88.dp, 96.dp)
        val baseReassignWidth = (maxWidth * 0.2f).coerceIn(48.dp, 64.dp)
        // Enlarged text can use the spare space in the left area without moving Play.
        val reassignWidth = (baseReassignWidth * LocalDensity.current.fontScale.coerceAtLeast(1f))
            .coerceAtMost(sideWidth - 4.dp)
        Row(
            modifier = Modifier.fillMaxWidth().testTag("Beat playback controls"),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Box(
                modifier = Modifier.width(sideWidth).testTag("Beat reassign group"),
                contentAlignment = Alignment.CenterStart,
            ) {
                TextButton(
                    shape = GhostButtonShape,
                    onClick = onReassignBeat,
                    enabled = !isReassigningBeat,
                    modifier = Modifier.width(reassignWidth).height(32.dp).testTag("Beat reassign button"),
                    contentPadding = PaddingValues(horizontal = 4.dp),
                ) {
                    BasicText(
                        text = "Reassign",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            lineHeight = 1.2.em,
                        ),
                        maxLines = 1,
                        softWrap = false,
                        autoSize = TextAutoSize.StepBased(minFontSize = 6.sp, maxFontSize = 11.sp),
                    )
                }
            }
            Row(
                modifier = Modifier.weight(1f).testTag("Beat transport group"),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                // Equal slots keep Play centered even when the loop label is wider.
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    IconButton(modifier = Modifier.size(32.dp), shape = GhostButtonShape, onClick = {
                        onPlayFromStart()
                        haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                    }) {
                        Icon(Icons.Filled.SkipPrevious, contentDescription = "Play from start")
                    }
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
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
                }
                Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                    TextButton(
                        shape = GhostButtonShape,
                        colors = ButtonDefaults.textButtonColors(
                            containerColor = if (loopMode != BeatLoopMode.OFF) MaterialTheme.colorScheme.secondaryContainer
                                else Color.Transparent,
                            contentColor = if (loopMode != BeatLoopMode.OFF) MaterialTheme.colorScheme.onSecondaryContainer
                                else MaterialTheme.colorScheme.onSurfaceVariant,
                        ),
                        contentPadding = PaddingValues(horizontal = 2.dp),
                        modifier = Modifier.width(80.dp).height(48.dp).semantics {
                            contentDescription = loopLabel
                            stateDescription = loopLabel
                        },
                        onClick = {
                            beatPlayer.toggleLoop()
                            haptics.performHapticFeedback(HapticFeedbackType.ContextClick)
                        },
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(1.dp)) {
                            Icon(painterResource(loopMode.iconRes), contentDescription = null, modifier = Modifier.size(16.dp))
                            BasicText(
                                text = stringResource(loopMode.shortLabelRes),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = if (loopMode != BeatLoopMode.OFF) MaterialTheme.colorScheme.onSecondaryContainer
                                        else MaterialTheme.colorScheme.onSurfaceVariant,
                                    lineHeight = 1.2.em,
                                    // Fixed sp letter spacing otherwise stays large as the label shrinks.
                                    letterSpacing = 0.em,
                                ),
                                maxLines = 1,
                                softWrap = false,
                                autoSize = TextAutoSize.StepBased(minFontSize = 6.sp, maxFontSize = 11.sp),
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(3.dp)) {
                                BeatLoopMode.entries.forEach { mode ->
                                    Box(Modifier.size(3.dp).background(
                                        MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = if (mode == loopMode) 1f else 0.25f),
                                        CircleShape,
                                    ))
                                }
                            }
                        }
                    }
                }
            }
            Row(
                modifier = Modifier.width(sideWidth).testTag("Beat volume group"),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
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
                Box(Modifier.width(56.dp).testTag("Beat volume slider slot"), contentAlignment = Alignment.Center) {
                    SlimSlider(
                        value = volume,
                        onValueChange = { newVolume ->
                            beatPlayer.setVolume(newVolume)
                            volume = beatPlayer.volume
                        },
                        accentColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.height(16.dp).fillMaxWidth().semantics { contentDescription = "Beat volume" },
                    )
                }
            }
        }
    }
}
