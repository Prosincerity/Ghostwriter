package com.prosincerity.ghostwriter.logic

/** Divide overlapping touch areas at the midpoint so every distinct marker line stays reachable. */
internal fun waveformMarkerHitBounds(
    markerX: Float,
    radiusPx: Float,
    labelWidthPx: Float,
    labelPaddingPx: Float,
    otherMarkerXs: List<Float>,
): ClosedFloatingPointRange<Float> {
    var left = markerX - radiusPx
    var right = markerX + maxOf(radiusPx, labelPaddingPx + labelWidthPx)
    for (otherX in otherMarkerXs) {
        val midpoint = (markerX + otherX) / 2f
        if (otherX < markerX) left = maxOf(left, midpoint)
        if (otherX > markerX) right = minOf(right, midpoint)
    }
    return left..right
}
