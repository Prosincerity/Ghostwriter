package com.prosincerity.ghostwriter.player.domain

/** Session volume and the audible level restored by unmuting. */
internal data class PlaybackVolume(
    val level: Float = 1f,
    private val lastAudibleLevel: Float = 1f,
) {
    fun withLevel(requestedLevel: Float): PlaybackVolume {
        if (requestedLevel.isNaN()) return this
        val level = requestedLevel.coerceIn(0f, 1f)
        return PlaybackVolume(level, if (level > 0f) level else lastAudibleLevel)
    }

    fun toggleMute(): PlaybackVolume = withLevel(if (level == 0f) lastAudibleLevel else 0f)
}
