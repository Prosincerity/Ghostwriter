package com.prosincerity.ghostwriter.media

import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class PlaybackFocusStateTest {
    @Test
    fun transientInterruption_keepsForegroundUntilPlaybackCanResume() {
        val focus = playingFocus()

        focus.onTransientLoss(wasPlaying = true)

        assertFalse(focus.hasFocus)
        assertTrue(focus.resumeOnFocusGain)
        // An unbound background service must remain eligible to resume after the interruption.
        assertTrue(focus.shouldKeepForeground(playing = false))
        assertTrue(focus.onGain())
        assertTrue(focus.hasFocus)
        assertFalse(focus.resumeOnFocusGain)
    }

    @Test
    fun repeatedTransientLoss_stillResumesExactlyOnce() {
        val focus = playingFocus()
        focus.onTransientLoss(wasPlaying = true)
        focus.onTransientLoss(wasPlaying = false)

        assertTrue(focus.onGain())
        assertFalse(focus.onGain())
        assertTrue(focus.hasFocus)
    }

    @Test
    fun userPauseDuringInterruption_cancelsResumeAndAllowsForegroundToStop() {
        val focus = playingFocus()
        focus.onTransientLoss(wasPlaying = true)

        focus.onPauseRequested()
        focus.onAbandoned()

        assertFalse(focus.resumeOnFocusGain)
        assertFalse(focus.shouldKeepForeground(playing = false))
        assertFalse(focus.onGain())
        focus.onPlaybackRequested()
        // A new user request must acquire focus again, even after a queued gain.
        assertFalse(focus.hasFocus)
    }

    @Test
    fun lossWhileAlreadyPaused_doesNotScheduleResume() {
        val focus = PlaybackFocusState()

        focus.onTransientLoss(wasPlaying = false)

        assertFalse(focus.resumeOnFocusGain)
        assertFalse(focus.onGain())
        assertFalse(focus.shouldKeepForeground(playing = false))
    }

    @Test
    fun deniedFocusRequest_doesNotScheduleAutomaticPlayback() {
        val focus = PlaybackFocusState()
        focus.onPlaybackRequested()
        focus.onRequestResult(granted = false)

        assertFalse(focus.hasFocus)
        assertFalse(focus.resumeOnFocusGain)
        assertFalse(focus.shouldKeepForeground(playing = false))
    }

    private fun playingFocus() = PlaybackFocusState().apply {
        onPlaybackRequested()
        onRequestResult(granted = true)
        assertTrue(shouldKeepForeground(playing = true))
    }
}
