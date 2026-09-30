package com.prosincerity.ghostwriter.logic

import com.prosincerity.ghostwriter.data.MarkerLoopRole
import com.prosincerity.ghostwriter.data.WaveformMarker

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
        fun fromMarkers(markers: List<WaveformMarker>, sampleRate: Int, totalFrames: Long): MarkerLoopFrames? {
            if (sampleRate <= 0 || totalFrames <= 0 || markers.none { it.loopRole != MarkerLoopRole.NONE }) return null
            val start = markers.firstOrNull { it.loopRole == MarkerLoopRole.START }?.frameAt(sampleRate) ?: 0
            val end = (markers.firstOrNull { it.loopRole == MarkerLoopRole.END }?.frameAt(sampleRate) ?: totalFrames)
                .coerceAtMost(totalFrames)
            return if (start in 0 until end && end <= totalFrames) MarkerLoopFrames(start, end) else null
        }
    }
}
