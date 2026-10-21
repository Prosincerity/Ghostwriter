package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectTransformGestures
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.semantics.ProgressBarRangeInfo
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.progressBarRangeInfo
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.setProgress
import androidx.compose.ui.semantics.stateDescription
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.drawText
import androidx.compose.ui.text.rememberTextMeasurer
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.prosincerity.ghostwriter.data.MarkerLoopRole
import com.prosincerity.ghostwriter.data.WaveformMarker
import com.prosincerity.ghostwriter.logic.MarkerLoopRange
import com.prosincerity.ghostwriter.logic.WaveformViewport
import com.prosincerity.ghostwriter.logic.WaveformDrawingGeometry
import com.prosincerity.ghostwriter.logic.waveformPeakHalfHeight
import com.prosincerity.ghostwriter.logic.crossesWaveformMarker
import com.prosincerity.ghostwriter.logic.waveformMarkerHitBounds
import com.prosincerity.ghostwriter.ui.theme.GhostBorder
import com.prosincerity.ghostwriter.ui.theme.GhostPrimary
import com.prosincerity.ghostwriter.ui.theme.GhostSecondary
import com.prosincerity.ghostwriter.ui.theme.GhostText
import kotlin.math.ceil
import kotlin.math.roundToInt

/**
 * Canvas waveform with parent-owned viewport state. The parent owns persistence
 * and playback: [onSeekFinished] commits a seek after a tap or slider-style
 * drag, while marker callbacks describe user intent without touching project
 * data.
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
    onMarkerMoveFinished: (WaveformMarker, Long) -> Unit,
    viewport: WaveformViewport,
    onViewportChange: (WaveformViewport) -> Unit,
    modifier: Modifier = Modifier,
    loopEnabled: Boolean = false,
) {
    var viewportWidthPx by remember { mutableFloatStateOf(0f) }
    val textMeasurer = rememberTextMeasurer()
    val density = LocalDensity.current
    val haptics = LocalHapticFeedback.current
    val latestHaptics = rememberUpdatedState(haptics)
    val latestMarkers = rememberUpdatedState(markers)
    val markerHitRadiusPx = with(density) { 24.dp.toPx() }
    val markerLabelPaddingPx = with(density) { 4.dp.toPx() }
    val markerLabelStyle = TextStyle(
        color = GhostSecondary,
        fontFamily = MaterialTheme.typography.labelSmall.fontFamily,
        fontSize = 11.sp,
    )
    val markerLabels = remember(markers, markerLabelStyle, textMeasurer) {
        markers.map { marker ->
            val color = when (marker.loopRole) {
                MarkerLoopRole.NONE -> lerp(GhostSecondary, Color.White, 0.24f)
                MarkerLoopRole.START -> lerp(GhostSecondary, Color.White, 0.55f)
                MarkerLoopRole.END -> lerp(GhostSecondary, Color.White, 0.18f)
            }
            WaveformMarkerLabel(
                color,
                textMeasurer.measure(marker.displayLabel(), markerLabelStyle.copy(color = color)),
            )
        }
    }
    val latestViewport = rememberUpdatedState(viewport)
    // Playback invalidates drawing without rebuilding cached geometry and text.
    val latestPositionMs = rememberUpdatedState(currentPositionMs)
    var draggedMarker by remember { mutableStateOf<WaveformMarker?>(null) }
    var pendingMarkerPositionMs by remember { mutableLongStateOf(0L) }

    fun seekAt(xPx: Float): Long =
        latestViewport.value.xToPositionMs(xPx, durationMs, viewportWidthPx)

    Box(
        modifier = modifier
            .testTag("Waveform")
            .clipToBounds()
            .onSizeChanged { size ->
                val newWidthPx = size.width.toFloat()
                onViewportChange(
                    latestViewport.value.resized(viewportWidthPx, newWidthPx),
                )
                viewportWidthPx = newWidthPx
            },
    ) {
        Spacer(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(durationMs, markers, viewportWidthPx) {
                    detectTapGestures(
                        onTap = { offset -> onSeekFinished(seekAt(offset.x)) },
                        onLongPress = { offset ->
                            latestHaptics.value.performHapticFeedback(HapticFeedbackType.SegmentTick)
                            onAddMarker(seekAt(offset.x))
                        },
                    )
                }
                .pointerInput(viewportWidthPx) {
                    detectTransformGestures { centroid, pan, zoom, _ ->
                        val currentViewport = latestViewport.value
                        if (
                            currentViewport.zoom == WaveformViewport.MIN_ZOOM &&
                            kotlin.math.abs(zoom - 1f) < 0.01f
                        ) {
                            // At the full-beat view, a one-finger drag keeps
                            // the old seek-slider feel instead of panning.
                            // The detector reports the previous centroid; pan
                            // takes us to the current finger/playhead position.
                            val fromMs = seekAt(centroid.x)
                            val toMs = seekAt(centroid.x + pan.x)
                            if (crossesWaveformMarker(fromMs, toMs, latestMarkers.value)) {
                                latestHaptics.value.performHapticFeedback(HapticFeedbackType.SegmentFrequentTick)
                            }
                            onSeekFinished(toMs)
                        } else {
                            onViewportChange(
                                currentViewport
                                    .zoomBy(zoom, centroid.x, viewportWidthPx)
                                    .panBy(pan.x, viewportWidthPx),
                            )
                        }
                    }
                }
                .drawWithCache {
                    val geometry = WaveformDrawingGeometry(viewport, size.width, amplitudes.size)
                    val markerAreaHeight = 24.dp.toPx()
                    val waveformHeight = (size.height - markerAreaHeight).coerceAtLeast(0f)
                    val waveformCenterY = markerAreaHeight + waveformHeight / 2f
                    val strokeWidth = 1.dp.toPx()
                    val markerTickWidth = 3.dp.toPx()
                    val markerTickLength = 8.dp.toPx()
                    val markerTickY = markerAreaHeight - 2.dp.toPx()
                    val playheadStrokeWidth = 2.dp.toPx()
                    val markerMinX = -48.dp.toPx()
                    val labelPadding = 4.dp.toPx()
                    val displayedMarkers = if (draggedMarker == null) markers else markers.map {
                        if (it === draggedMarker) it.copy(positionMs = pendingMarkerPositionMs) else it
                    }
                    val loopRange = MarkerLoopRange.fromMarkers(displayedMarkers, durationMs)
                    val loopLeft = loopRange?.let {
                        geometry.positionToX(it.startMs, durationMs).coerceIn(0f, size.width)
                    } ?: 0f
                    val loopRight = loopRange?.let {
                        geometry.positionToX(it.endMs, durationMs).coerceIn(0f, size.width)
                    } ?: 0f
                    val loopColor = GhostSecondary.copy(alpha = if (loopEnabled) 0.16f else 0.08f)
                    val drawingMarkerXs = FloatArray(markerLabels.size) { index ->
                        val marker = markers[index]
                        val positionMs = if (marker === draggedMarker) pendingMarkerPositionMs else marker.positionMs
                        geometry.positionToX(positionMs, durationMs)
                    }
                    // Match drawText's original available-space wrapping, including
                    // labels moving near the right edge during a drag preview.
                    val drawingLabels = markerLabels.mapIndexed { index, label ->
                        textMeasurer.measure(
                            text = label.layout.layoutInput.text,
                            style = label.layout.layoutInput.style,
                            constraints = Constraints(
                                maxWidth = ceil(size.width - drawingMarkerXs[index] - labelPadding)
                                    .toInt().coerceAtLeast(0),
                                maxHeight = ceil(size.height).toInt().coerceAtLeast(0),
                            ),
                            layoutDirection = layoutDirection,
                            density = this,
                        )
                    }

                    onDrawBehind {
                        if (loopRight > loopLeft) drawRect(
                            color = loopColor,
                            topLeft = Offset(loopLeft, markerAreaHeight),
                            size = Size(loopRight - loopLeft, waveformHeight),
                        )
                        drawLine(
                            color = GhostBorder,
                            start = Offset(0f, waveformCenterY),
                            end = Offset(size.width, waveformCenterY),
                            strokeWidth = strokeWidth,
                        )
                        for (sampleIndex in geometry.firstSample..geometry.lastSample) {
                            val x = geometry.sampleX(sampleIndex)
                            val halfHeight = waveformPeakHalfHeight(amplitudes[sampleIndex], waveformHeight)
                            drawLine(
                                color = GhostPrimary,
                                start = Offset(x, waveformCenterY - halfHeight),
                                end = Offset(x, waveformCenterY + halfHeight),
                                strokeWidth = strokeWidth,
                                cap = StrokeCap.Round,
                            )
                        }
                        for (index in markerLabels.indices) {
                            val label = markerLabels[index]
                            val markerX = drawingMarkerXs[index]
                            if (markerX >= markerMinX && markerX <= size.width) {
                                drawLine(
                                    color = label.color,
                                    start = Offset(markerX, 0f),
                                    end = Offset(markerX, size.height),
                                    strokeWidth = strokeWidth,
                                )
                                drawLine(
                                    color = label.color,
                                    start = Offset(markerX, markerTickY),
                                    end = Offset(markerX + markerTickLength, markerTickY),
                                    strokeWidth = markerTickWidth,
                                    cap = StrokeCap.Square,
                                )
                                drawText(
                                    textLayoutResult = drawingLabels[index],
                                    topLeft = Offset(markerX + labelPadding, 0f),
                                )
                            }
                        }
                        val playheadX = geometry.positionToX(latestPositionMs.value, durationMs)
                        drawLine(
                            color = GhostText,
                            start = Offset(playheadX, markerAreaHeight),
                            end = Offset(playheadX, size.height),
                            strokeWidth = playheadStrokeWidth,
                        )
                    }
                },
        )

        // Each marker gets a transparent hit target. This keeps its gestures
        // separate from the waveform's seek and pan gestures underneath.
        val markerXs = markers.map {
            viewport.positionToX(it.positionMs, durationMs, viewportWidthPx)
        }
        markers.forEachIndexed { index, marker ->
            val label = markerLabels[index]
            // Keep this target at the marker's pre-drag position. Moving the
            // target under an active pointer changes its local coordinates and
            // makes the marker lag or jump.
            val markerX = viewport.positionToX(marker.positionMs, durationMs, viewportWidthPx)
            val labelSize = label.layout.size
            val hitBounds = waveformMarkerHitBounds(
                markerX, markerHitRadiusPx, labelSize.width.toFloat(), markerLabelPaddingPx, markerXs,
            )
            val overlayLeftPx = hitBounds.start.roundToInt().toFloat()
            val overlayWidthPx = (hitBounds.endInclusive.roundToInt() - overlayLeftPx).coerceAtLeast(1f)
            val labelLeftPx = markerX - overlayLeftPx + markerLabelPaddingPx
            val overlayWidth = with(density) { overlayWidthPx.toDp() }

            if (markerX in -overlayWidthPx..viewportWidthPx) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .width(overlayWidth)
                        .offset { IntOffset(overlayLeftPx.roundToInt(), 0) }
                        // Report semantics at the same position as the pointer
                        // handlers, after applying the marker's layout offset.
                        .testTag("Waveform marker ${marker.label}")
                        .semantics {
                            stateDescription = "Looping: ${marker.loopRole.displayName}"
                            onClick(label = "Edit marker") {
                                onMarkerClick(marker)
                                true
                            }
                            val displayPositionMs = if (marker === draggedMarker) pendingMarkerPositionMs
                                else marker.positionMs
                            progressBarRangeInfo = ProgressBarRangeInfo(
                                current = displayPositionMs.toFloat(),
                                range = 0f..durationMs.coerceAtLeast(0L).toFloat(),
                            )
                            setProgress { requestedPosition ->
                                onMarkerMoveFinished(
                                    marker,
                                    requestedPosition.toLong().coerceIn(0L, durationMs),
                                )
                                true
                            }
                        }
                        .pointerInput(marker, durationMs, viewportWidthPx, overlayLeftPx, labelLeftPx, labelSize) {
                            detectTapGestures(
                                onTap = { offset ->
                                    val isLabelTap = offset.x >= labelLeftPx &&
                                        offset.x <= labelLeftPx + labelSize.width &&
                                        offset.y <= labelSize.height
                                    if (isLabelTap) onMarkerClick(marker)
                                    else onSeekFinished(marker.positionMs)
                                },
                                onLongPress = { onMarkerClick(marker) },
                            )
                        }
                        .pointerInput(marker, durationMs, viewportWidthPx, overlayLeftPx) {
                            var dragStartPointerX = 0f
                            var dragStartMarkerX = 0f
                            detectDragGestures(
                                orientationLock = null,
                                onDragStart = { down, _, _ ->
                                    draggedMarker = marker
                                    pendingMarkerPositionMs = marker.positionMs
                                    // Anchor to the press, not the event that crossed
                                    // touch slop, or that initial movement is lost.
                                    dragStartPointerX = overlayLeftPx + down.position.x
                                    dragStartMarkerX = latestViewport.value.positionToX(
                                        marker.positionMs,
                                        durationMs,
                                        viewportWidthPx,
                                    )
                                },
                                onDragCancel = { draggedMarker = null },
                                onDragEnd = { change ->
                                    val horizontalDelta = (overlayLeftPx + change.position.x) - dragStartPointerX
                                    pendingMarkerPositionMs = seekAt(dragStartMarkerX + horizontalDelta)
                                    onMarkerMoveFinished(marker, pendingMarkerPositionMs)
                                    draggedMarker = null
                                },
                                onDrag = { change, _ ->
                                    val horizontalDelta = (overlayLeftPx + change.position.x) - dragStartPointerX
                                    pendingMarkerPositionMs = seekAt(dragStartMarkerX + horizontalDelta)
                                    change.consume()
                                },
                            )
                        },
                )
            }
        }

    }
}

private fun WaveformMarker.displayLabel(): String =
    if (loopRole == MarkerLoopRole.NONE) label else "${loopRole.displayName} · $label"

private data class WaveformMarkerLabel(
    val color: Color,
    val layout: TextLayoutResult,
)
