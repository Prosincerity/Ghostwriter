package com.prosincerity.ghostwriter.media

import kotlin.math.abs

/** A future source position lets AudioTrack buffer while MediaPlayer keeps playing. */
internal data class PlaybackHandoff(val positionMs: Int, val startAtMs: Long) {
    fun isAligned(currentPositionMs: Int, durationMs: Int, looping: Boolean, nowMs: Long): Boolean {
        val lateness = nowMs - startAtMs
        if (lateness !in 0..20) return false
        val expected = position(positionMs.toLong() + lateness, durationMs, looping)
        val difference = abs(currentPositionMs.toLong() - expected)
        val distance = if (looping && durationMs > 0)
            minOf(difference, abs(durationMs - difference)) else difference
        return distance <= 20
    }

    companion object {
        fun plan(positionMs: Int, durationMs: Int, looping: Boolean, nowMs: Long): PlaybackHandoff =
            PlaybackHandoff(position(positionMs.toLong() + 150, durationMs, looping), nowMs + 150)

        private fun position(value: Long, durationMs: Int, looping: Boolean): Int =
            if (looping && durationMs > 0) (value % durationMs).toInt()
            else value.coerceIn(0, durationMs.toLong()).toInt()
    }
}
