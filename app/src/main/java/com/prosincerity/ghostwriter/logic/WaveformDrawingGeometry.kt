package com.prosincerity.ghostwriter.logic

import kotlin.math.ceil
import kotlin.math.floor

/** Immutable drawing coordinates; construct when the viewport or sample count changes. */
internal data class WaveformDrawingGeometry(
    val viewport: WaveformViewport,
    val widthPx: Float,
    val sampleCount: Int,
) {
    private val drawingViewport = viewport.clamped(widthPx)
    private val contentWidthPx = drawingViewport.contentWidthPx(widthPx)
    private val scrollOffsetPx = drawingViewport.scrollOffsetPx
    private val sampleDenominator = (sampleCount - 1).coerceAtLeast(1)
    private val hasSamples = sampleCount > 0 && contentWidthPx > 0f

    // Retain the edge samples used by the original Canvas culling calculation.
    val firstSample: Int = if (hasSamples) {
        floor((scrollOffsetPx / contentWidthPx) * sampleCount)
            .toInt().coerceIn(0, sampleCount - 1)
    } else 0

    val lastSample: Int = if (hasSamples) {
        ceil(((scrollOffsetPx + widthPx) / contentWidthPx) * sampleCount)
            .toInt().coerceIn(firstSample, sampleCount - 1)
    } else -1

    fun sampleX(sampleIndex: Int): Float =
        (sampleIndex.toFloat() / sampleDenominator) * contentWidthPx - scrollOffsetPx

    /** Unlike the viewport adapter, this uses already bounded coordinates without copies. */
    fun positionToX(positionMs: Long, durationMs: Long): Float =
        if (durationMs <= 0L || widthPx <= 0f) 0f
        else (positionMs.coerceIn(0L, durationMs).toFloat() / durationMs) * contentWidthPx - scrollOffsetPx
}

internal fun waveformPeakHalfHeight(amplitude: Int, waveformHeightPx: Float): Float =
    (amplitude / 32_768f).coerceIn(0f, 1f) * waveformHeightPx / 2f
