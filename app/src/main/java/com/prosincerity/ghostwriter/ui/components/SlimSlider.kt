package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.height
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp

/** The same quiet track and round thumb for typography and playback volume. */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun SlimSlider(
    value: Float,
    onValueChange: (Float) -> Unit,
    modifier: Modifier = Modifier,
    valueRange: ClosedFloatingPointRange<Float> = 0f..1f,
    enabled: Boolean = true,
    accentColor: Color = MaterialTheme.colorScheme.primary,
) {
    val interactionSource = remember { MutableInteractionSource() }
    val colors = SliderDefaults.colors(
        thumbColor = accentColor,
        activeTrackColor = accentColor,
        inactiveTrackColor = MaterialTheme.colorScheme.surfaceContainerHighest,
    )
    Slider(
        value = value, onValueChange = onValueChange, valueRange = valueRange,
        enabled = enabled, interactionSource = interactionSource, colors = colors,
        thumb = {
            SliderDefaults.Thumb(
                interactionSource = interactionSource, thumbSize = DpSize(12.dp, 12.dp),
                enabled = enabled, colors = colors,
            )
        },
        track = { state ->
            SliderDefaults.Track(
                sliderState = state, enabled = enabled, colors = colors,
                modifier = Modifier.height(4.dp), drawStopIndicator = null,
                thumbTrackGapSize = 2.dp, trackInsideCornerSize = 2.dp,
            )
        },
        modifier = modifier,
    )
}
