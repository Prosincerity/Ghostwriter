package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prosincerity.ghostwriter.data.WaveformMarker
import com.prosincerity.ghostwriter.logic.WaveformViewport
import com.prosincerity.ghostwriter.ui.theme.GhostBorder
import com.prosincerity.ghostwriter.ui.theme.GhostPrimary
import com.prosincerity.ghostwriter.ui.theme.GhostSecondary
import com.prosincerity.ghostwriter.ui.theme.GhostText
import kotlin.math.ceil
import kotlin.math.floor
import kotlin.math.roundToInt

/**
 * Canvas waveform with local zoom/pan state. The parent owns persistence and
 * playback: [onSeekFinished] commits a seek after a tap or playhead drag,
 * while marker callbacks describe user intent without touching project data.
 */
@Composable
fun WaveformView(
    amplitudes: IntArray,
    durationMs: Long,
    currentPositionMs: Long,
    markers: List<WaveformMarker>,
    onSeekFinished: (Long) -> Unit,
    onAddMarker: (Long) -> Unit,
    onMarkerClick: (WaveformMarker) -> Unit,
    modifier: Modifier = Modifier,
) {
    var viewport by remember(durationMs) { mutableStateOf(WaveformViewport()) }
    var viewportWidthPx by remember { mutableFloatStateOf(0f) }
    var isScrubbing by remember { mutableStateOf(false) }
    var pendingSeekMs by remember { mutableLongStateOf(0L) }
    var playheadHandleWidthPx by remember { mutableFloatStateOf(0f) }
    val textMeasurer = rememberTextMeasurer()
    val markerHitRadiusPx = with(LocalDensity.current) { 24.dp.toPx() }

    val visiblePositionMs = if (isScrubbing) pendingSeekMs else currentPositionMs
    val playheadX = viewport.positionToX(visiblePositionMs, durationMs, viewportWidthPx)
    val handleLeftPx = playheadX - playheadHandleWidthPx / 2f

    fun seekAt(xPx: Float): Long =
        viewport.xToPositionMs(xPx, durationMs, viewportWidthPx)

    fun markerAt(xPx: Float): WaveformMarker? {
        return markers.minByOrNull { marker ->
            kotlin.math.abs(viewport.positionToX(marker.positionMs, durationMs, viewportWidthPx) - xPx)
        }?.takeIf { marker ->
            kotlin.math.abs(viewport.positionToX(marker.positionMs, durationMs, viewportWidthPx) - xPx) <= markerHitRadiusPx
        }
    }

    Box(
        modifier = modifier
            .clipToBounds()
            .onSizeChanged { size ->
                viewportWidthPx = size.width.toFloat()
                viewport = viewport.clamped(viewportWidthPx)
            },
    ) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(durationMs, markers, viewport, viewportWidthPx) {
                    detectTapGestures(
                        onTap = { offset ->
                            markerAt(offset.x)?.let(onMarkerClick)
                                ?: onSeekFinished(seekAt(offset.x))
                        },
                        onLongPress = { offset -> onAddMarker(seekAt(offset.x)) },
                    )
                }
                .pointerInput(viewport, viewportWidthPx) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        viewport = viewport
                            .zoomBy(zoom, centroid.x, viewportWidthPx)
                            .panBy(pan.x, viewportWidthPx)
                    }
                },
        ) {
            val drawingViewport = viewport.clamped(size.width)
            val markerAreaHeight = 20.dp.toPx()
            val waveformTop = markerAreaHeight
            val waveformHeight = (size.height - waveformTop).coerceAtLeast(0f)
            val waveformCenterY = waveformTop + waveformHeight / 2f

            drawLine(
                color = GhostBorder,
                start = Offset(0f, waveformCenterY),
                end = Offset(size.width, waveformCenterY),
                strokeWidth = 1.dp.toPx(),
            )

            if (amplitudes.isNotEmpty() && drawingViewport.contentWidthPx(size.width) > 0f) {
                val contentWidth = drawingViewport.contentWidthPx(size.width)
                val firstSample = floor(
                    (drawingViewport.scrollOffsetPx / contentWidth) * amplitudes.size,
                ).toInt().coerceIn(0, amplitudes.lastIndex)
                val lastSample = ceil(
                    ((drawingViewport.scrollOffsetPx + size.width) / contentWidth) * amplitudes.size,
                ).toInt().coerceIn(firstSample, amplitudes.lastIndex)

                for (sampleIndex in firstSample..lastSample) {
                    val x = ((sampleIndex.toFloat() / amplitudes.lastIndex.coerceAtLeast(1)) * contentWidth) -
                        drawingViewport.scrollOffsetPx
                    val normalizedAmplitude = (amplitudes[sampleIndex] / 32_768f).coerceIn(0f, 1f)
                    val halfHeight = normalizedAmplitude * waveformHeight / 2f
                    drawLine(
                        color = GhostSecondary,
                        start = Offset(x, waveformCenterY - halfHeight),
                        end = Offset(x, waveformCenterY + halfHeight),
                        strokeWidth = 1.dp.toPx(),
                        cap = StrokeCap.Round,
                    )
                }
            }

            markers.forEach { marker ->
                val markerX = drawingViewport.positionToX(marker.positionMs, durationMs, size.width)
                if (markerX in -48.dp.toPx()..size.width) {
                    drawLine(
                        color = GhostBorder,
                        start = Offset(markerX, 0f),
                        end = Offset(markerX, size.height),
                        strokeWidth = 1.dp.toPx(),
                    )
                    drawLine(
                        color = GhostBorder,
                        start = Offset(markerX, markerAreaHeight - 2.dp.toPx()),
                        end = Offset(markerX + 8.dp.toPx(), markerAreaHeight - 2.dp.toPx()),
                        strokeWidth = 3.dp.toPx(),
                        cap = StrokeCap.Square,
                    )
                    drawText(
                        textMeasurer = textMeasurer,
                        text = marker.label,
                        topLeft = Offset(markerX + 4.dp.toPx(), 0f),
                        style = TextStyle(
                            color = GhostText,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 10.sp,
                        ),
                    )
                }
            }

            val currentPlayheadX = drawingViewport.positionToX(visiblePositionMs, durationMs, size.width)
            drawLine(
                color = GhostPrimary,
                start = Offset(currentPlayheadX, 0f),
                end = Offset(currentPlayheadX, size.height),
                strokeWidth = 2.dp.toPx(),
            )
        }

        Box(
            modifier = Modifier
                .fillMaxHeight()
                .width(32.dp)
                .offset { IntOffset(handleLeftPx.roundToInt(), 0) }
                .onSizeChanged { playheadHandleWidthPx = it.width.toFloat() }
                .pointerInput(durationMs, viewport, viewportWidthPx, handleLeftPx) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            isScrubbing = true
                            pendingSeekMs = seekAt(handleLeftPx + offset.x)
                        },
                        onDragCancel = { isScrubbing = false },
                        onDragEnd = {
                            onSeekFinished(pendingSeekMs)
                            isScrubbing = false
                        },
                        onDrag = { change, _ ->
                            pendingSeekMs = seekAt(handleLeftPx + change.position.x)
                            change.consume()
                        },
                    )
                },
        )
    }
}
