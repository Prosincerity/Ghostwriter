package com.prosincerity.ghostwriter.player.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class PlaybackVolumeTest {
    @Test
    fun muteBeforeSelectingAnAudibleLevel_restoresFullVolume() {
        val muted = PlaybackVolume().withLevel(0f)

        assertEquals(1f, muted.toggleMute().level)
    }

    @Test
    fun selectingANewAudibleLevel_replacesTheUnmuteLevel() {
        val original = PlaybackVolume().withLevel(0.35f)
        val changed = original.withLevel(0.6f)

        assertEquals(0.35f, original.level)
        assertEquals(0f, changed.toggleMute().level)
        assertEquals(0.6f, changed.toggleMute().toggleMute().level)
    }

    @Test
    fun silentAndRejectedLevels_preserveTheLastAudibleSelection() {
        val audible = PlaybackVolume().withLevel(0.6f)
        for (silentLevel in listOf(0f, -1f, Float.NEGATIVE_INFINITY)) {
            val muted = audible.withLevel(silentLevel).withLevel(Float.NaN)

            assertEquals(0f, muted.level)
            assertEquals(0.6f, muted.toggleMute().level)
        }
    }

    @Test
    fun rejectedLevel_preservesTheCurrentAudibleSelection() {
        val volume = PlaybackVolume().withLevel(0.35f).withLevel(Float.NaN)

        assertEquals(0.35f, volume.level)
        assertEquals(0.35f, volume.toggleMute().toggleMute().level)
    }

    @Test
    fun aboveMaximumLevels_restoreTheClampedAudibleSelection() {
        for (requestedLevel in listOf(1f, 1.5f, Float.POSITIVE_INFINITY)) {
            val volume = PlaybackVolume().withLevel(requestedLevel)

            assertEquals(1f, volume.level)
            assertEquals(1f, volume.toggleMute().toggleMute().level)
        }
    }
}
