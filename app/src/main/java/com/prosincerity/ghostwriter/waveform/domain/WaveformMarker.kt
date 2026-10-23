package com.prosincerity.ghostwriter.waveform.domain

enum class MarkerLoopRole(val jsonValue: String, val displayName: String) {
    NONE("none", "None"), START("start", "Start"), END("end", "End");

    companion object {
        fun fromJson(value: String): MarkerLoopRole =
            entries.firstOrNull { it.jsonValue == value } ?: NONE
    }
}

/** Frame position is authoritative once the beat's sample rate is known; milliseconds are a UI/legacy adapter. */
data class WaveformMarker(
    val label: String,
    val positionMs: Long,
    val loopRole: MarkerLoopRole = MarkerLoopRole.NONE,
    val frameIndex: Long? = null,
    val sampleRate: Int? = null,
) {
    fun frameAt(rate: Int): Long {
        require(rate > 0)
        val frame = frameIndex
        val savedRate = sampleRate
        return if (frame != null && savedRate != null && savedRate > 0)
            if (savedRate == rate) frame else (frame.toDouble() * rate / savedRate).toLong()
        else (positionMs.toDouble() * rate / 1000).toLong()
    }

    fun withSampleRate(rate: Int): WaveformMarker {
        if (rate <= 0) return this
        val frame = frameAt(rate)
        val position = frameToMs(frame, rate)
        // Unchanged pointerInput keys retain their marker reference. Preserve it
        // when normalizing a list after another marker is added, edited or moved.
        if (sampleRate == rate && frameIndex == frame && positionMs == position) return this
        return copy(positionMs = position, frameIndex = frame, sampleRate = rate)
    }

    fun atPositionMs(position: Long, rate: Int): WaveformMarker =
        copy(positionMs = position, frameIndex = null, sampleRate = null).withSampleRate(rate)

    companion object {
        fun frameToMs(frame: Long, rate: Int): Long = Math.round(frame.toDouble() * 1000 / rate)
    }
}
