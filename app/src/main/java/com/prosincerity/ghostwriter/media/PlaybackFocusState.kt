package com.prosincerity.ghostwriter.media

/** Main-thread audio-focus decisions, shared by the service's player and focus callbacks. */
internal class PlaybackFocusState {
    var hasFocus = false
        private set
    var resumeOnFocusGain = false
        private set

    fun onPlaybackRequested() {
        resumeOnFocusGain = false
    }

    fun onRequestResult(granted: Boolean) {
        hasFocus = granted
    }

    fun onTransientLoss(wasPlaying: Boolean) {
        hasFocus = false
        resumeOnFocusGain = resumeOnFocusGain || wasPlaying
    }

    /** Returns whether the interrupted beat should resume. */
    fun onGain(): Boolean {
        // A gain is useful only while waiting for an interrupted beat. In particular,
        // it must not let a later play skip requesting focus after pause or release.
        if (!resumeOnFocusGain) return false
        hasFocus = true
        resumeOnFocusGain = false
        return true
    }

    fun onPauseRequested() {
        resumeOnFocusGain = false
    }

    fun onAbandoned() {
        hasFocus = false
    }

    // Dropping foreground status during an interruption can prevent a background
    // service from restarting when focus returns (or let Android stop it first).
    fun shouldKeepForeground(playing: Boolean): Boolean = playing || resumeOnFocusGain
}
