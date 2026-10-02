package com.prosincerity.ghostwriter.logic

import com.prosincerity.ghostwriter.data.MarkerLoopRole
import com.prosincerity.ghostwriter.data.WaveformMarker

/** Resolve the marker selected by a pointer handler or an open edit dialog. */
internal fun indexOfSelectedMarker(markers: List<WaveformMarker>, selected: WaveformMarker): Int {
    val sameReference = markers.indexOfFirst { it === selected }
    if (sameReference >= 0) return sameReference
    // Frame migration may replace the object while an edit dialog is open.
    // Recover only an unambiguous normalization of that exact marker; a changed
    // position, label or role must not be overwritten by a stale selection.
    var match = -1
    markers.forEachIndexed { index, marker ->
        if (marker == selected.withSampleRate(marker.sampleRate ?: 0)) {
            if (match >= 0) return -1
            match = index
        }
    }
    return match
}

/** Each beat has at most one start and one end; assigning a role transfers it. */
fun replaceLoopMarker(
    markers: List<WaveformMarker>,
    index: Int,
    replacement: WaveformMarker,
): List<WaveformMarker> = markers.mapIndexed { currentIndex, marker ->
    when {
        currentIndex == index -> replacement
        replacement.loopRole != MarkerLoopRole.NONE && marker.loopRole == replacement.loopRole ->
            marker.copy(loopRole = MarkerLoopRole.NONE)
        else -> marker
    }
}

data class MarkerLoopRange(val startMs: Long, val endMs: Long) {
    companion object {
        fun fromMarkers(markers: List<WaveformMarker>, durationMs: Long): MarkerLoopRange? {
            if (durationMs <= 0L || markers.none { it.loopRole != MarkerLoopRole.NONE }) return null
            val start = markers.firstOrNull { it.loopRole == MarkerLoopRole.START }?.positionMs ?: 0L
            val end = markers.firstOrNull { it.loopRole == MarkerLoopRole.END }?.positionMs ?: durationMs
            return if (start in 0 until end && end <= durationMs) MarkerLoopRange(start, end) else null
        }
    }
}

data class MarkerLoopFrames(val start: Long, val end: Long) {
    companion object {
        fun fromDurationUs(markers: List<WaveformMarker>, sampleRate: Int, durationUs: Long): MarkerLoopFrames? {
            if (sampleRate <= 0 || durationUs <= 0) return null
            // MediaPlayer's millisecond duration is only a display adapter.
            // Recover the nearest frame from the container's microsecond time.
            val frames = durationUs / 1_000_000L * sampleRate +
                (durationUs % 1_000_000L * sampleRate + 500_000L) / 1_000_000L
            return fromMarkers(markers, sampleRate, frames)
        }

        fun fromMarkers(markers: List<WaveformMarker>, sampleRate: Int, totalFrames: Long): MarkerLoopFrames? {
            if (sampleRate <= 0 || totalFrames <= 0 || markers.none { it.loopRole != MarkerLoopRole.NONE }) return null
            val start = markers.firstOrNull { it.loopRole == MarkerLoopRole.START }?.frameAt(sampleRate) ?: 0
            val end = (markers.firstOrNull { it.loopRole == MarkerLoopRole.END }?.frameAt(sampleRate) ?: totalFrames)
                .coerceAtMost(totalFrames)
            return if (start in 0 until end && end <= totalFrames) MarkerLoopFrames(start, end) else null
        }
    }
}
