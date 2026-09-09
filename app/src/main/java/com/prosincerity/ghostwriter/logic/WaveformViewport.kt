package com.prosincerity.ghostwriter.logic

/**
 * Pure zoom, pan, and seek math for a waveform drawing surface.
 *
 * [zoom] is a multiplier of the viewport width: 1x shows the complete beat,
 * while the documented 64x maximum gives fine placement (about 2.8 seconds
 * across a typical three-minute beat). [scrollOffsetPx] is measured in the
 * zoomed content's pixels from its left edge.
 */
data class WaveformViewport(
    val zoom: Float = MIN_ZOOM,
    val scrollOffsetPx: Float = 0f,
) {

    /** Returns a viewport with its zoom and scroll offset constrained to valid bounds. */
    fun clamped(viewportWidthPx: Float): WaveformViewport = copy(
        zoom = zoom.coerceIn(MIN_ZOOM, MAX_ZOOM),
    ).let { viewport ->
        viewport.copy(scrollOffsetPx = viewport.scrollOffsetPx.coerceIn(0f, viewport.maxScrollPx(viewportWidthPx)))
    }

    /** Pixel width of the full waveform after horizontal zoom. */
    fun contentWidthPx(viewportWidthPx: Float): Float =
        viewportWidthPx.coerceAtLeast(0f) * zoom.coerceIn(MIN_ZOOM, MAX_ZOOM)

    /** Largest valid [scrollOffsetPx] for the supplied drawing width. */
    fun maxScrollPx(viewportWidthPx: Float): Float =
        (contentWidthPx(viewportWidthPx) - viewportWidthPx.coerceAtLeast(0f)).coerceAtLeast(0f)

    /** Preserves the visible timeline center when the drawing surface changes width. */
    fun resized(previousWidthPx: Float, newWidthPx: Float): WaveformViewport {
        if (previousWidthPx <= 0f || newWidthPx <= 0f) return clamped(newWidthPx)

        val oldViewport = clamped(previousWidthPx)
        val centerFraction = (oldViewport.scrollOffsetPx + previousWidthPx / 2f) /
            oldViewport.contentWidthPx(previousWidthPx)
        val newScrollOffset = centerFraction * oldViewport.contentWidthPx(newWidthPx) - newWidthPx / 2f
        return oldViewport.copy(scrollOffsetPx = newScrollOffset).clamped(newWidthPx)
    }

    /** Maps a playback position to its x coordinate in the visible viewport. */
    fun positionToX(positionMs: Long, durationMs: Long, viewportWidthPx: Float): Float {
        if (durationMs <= 0L || viewportWidthPx <= 0f) return 0f
        val fullPosition = positionMs.coerceIn(0L, durationMs).toFloat() / durationMs
        return fullPosition * contentWidthPx(viewportWidthPx) - clamped(viewportWidthPx).scrollOffsetPx
    }

    /** Maps an x coordinate in the visible viewport to a clamped playback position. */
    fun xToPositionMs(xPx: Float, durationMs: Long, viewportWidthPx: Float): Long {
        if (durationMs <= 0L || viewportWidthPx <= 0f) return 0L
        val viewport = clamped(viewportWidthPx)
        val contentPosition = (xPx + viewport.scrollOffsetPx)
            .coerceIn(0f, viewport.contentWidthPx(viewportWidthPx))
        return ((contentPosition / viewport.contentWidthPx(viewportWidthPx)) * durationMs)
            .toLong()
            .coerceIn(0L, durationMs)
    }

    /**
     * Applies a pinch scale around [focalXpx], preserving the waveform point
     * below the fingers. The result is clamped between [MIN_ZOOM] and
     * [MAX_ZOOM].
     */
    fun zoomBy(scaleFactor: Float, focalXpx: Float, viewportWidthPx: Float): WaveformViewport {
        if (viewportWidthPx <= 0f || !scaleFactor.isFinite() || scaleFactor <= 0f) {
            return clamped(viewportWidthPx)
        }

        val oldViewport = clamped(viewportWidthPx)
        val newZoom = (oldViewport.zoom * scaleFactor).coerceIn(MIN_ZOOM, MAX_ZOOM)
        val focalContentFraction = (oldViewport.scrollOffsetPx + focalXpx)
            .coerceIn(0f, oldViewport.contentWidthPx(viewportWidthPx)) /
            oldViewport.contentWidthPx(viewportWidthPx)
        val newContentWidth = viewportWidthPx * newZoom
        val newScroll = focalContentFraction * newContentWidth - focalXpx

        return WaveformViewport(newZoom, newScroll).clamped(viewportWidthPx)
    }

    /**
     * Applies a horizontal finger-pan delta. A drag to the left moves the
     * timeline forward; a drag to the right moves it back toward the start.
     */
    fun panBy(dragDeltaXPx: Float, viewportWidthPx: Float): WaveformViewport {
        if (!dragDeltaXPx.isFinite()) return clamped(viewportWidthPx)
        val viewport = clamped(viewportWidthPx)
        return viewport.copy(scrollOffsetPx = viewport.scrollOffsetPx - dragDeltaXPx)
            .clamped(viewportWidthPx)
    }

    companion object {
        const val MIN_ZOOM = 1f
        const val MAX_ZOOM = 64f
    }
}
