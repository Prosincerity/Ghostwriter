package com.prosincerity.ghostwriter.media

import android.media.MediaPlayer
import android.util.Log
import java.io.File

/**
 * Lightweight wrapper around Android's built-in [MediaPlayer].
 *
 * Design decisions:
 * - Zero external dependencies: uses only AOSP [MediaPlayer].
 * - Lifecycle is caller-managed: call [release] when the editor screen
 *   is disposed so the OS can reclaim audio resources.
 * - Looping defaults to ON: rap songwriters almost always want their
 *   instrumental to loop continuously while writing verses.
 * - State is exposed through properties; callers observe it via Compose state
 *   (see EditorScreen) rather than callbacks or flows.
 */
class BeatPlayer {

    private var player: MediaPlayer? = null
    private var prepared = false

    /** True if a beat file is loaded and [MediaPlayer] is prepared. */
    val isReady: Boolean get() = prepared

    /** True if the player is currently playing audio. */
    val isPlaying: Boolean get() = player?.isPlaying == true

    /** Whether the beat will loop automatically when it reaches the end. */
    var isLooping: Boolean = true
        private set

    /** Playback volume, from silent (0f) to full volume (1f). */
    var volume: Float = 1f
        private set

    /**
     * Current playback position in milliseconds.
     * Returns 0 when not prepared.
     */
    val currentPositionMs: Int
        get() = if (prepared) player?.currentPosition ?: 0 else 0

    /**
     * Total beat duration in milliseconds.
     * Returns 0 when not prepared.
     */
    val durationMs: Int
        get() = if (prepared) player?.duration ?: 0 else 0

    /**
     * Loads [beatFile] and prepares the player for playback.
     * Releases any previously loaded beat before loading the new one.
     * Playback does NOT start automatically after loading.
     *
     * @return true if the file was loaded successfully, false on error.
     */
    fun load(beatFile: File): Boolean {
        release()
        if (!beatFile.isFile) return false
        return runCatching {
            val mp = MediaPlayer().also { player = it }
            mp.setDataSource(beatFile.absolutePath)
            mp.isLooping = isLooping
            mp.setVolume(volume, volume)
            mp.prepare()
            prepared = true
            true
        }.onFailure { e ->
            Log.e(TAG, "BeatPlayer: failed to load ${beatFile.name}", e)
            release()
        }.getOrDefault(false)
    }

    /** Starts or resumes playback. No-op if not prepared. */
    fun play() {
        if (!prepared) return
        player?.start()
    }

    /** Pauses playback. No-op if not playing. */
    fun pause() {
        player?.takeIf { it.isPlaying }?.pause()
    }

    /** Toggles between play and pause. */
    fun togglePlayPause() {
        if (isPlaying) pause() else play()
    }

    /**
     * Seeks to [positionMs] milliseconds.
     * Clamps the value to the valid range [0, durationMs].
     */
    fun seekTo(positionMs: Int) {
        if (!prepared) return
        val clamped = positionMs.coerceIn(0, durationMs)
        player?.seekTo(clamped)
    }

    /**
     * Toggles looping on/off and applies the change to the active player.
     * @return the new looping state.
     */
    fun toggleLoop(): Boolean {
        isLooping = !isLooping
        player?.isLooping = isLooping
        return isLooping
    }

    /**
     * Sets looping to [loop] and applies the change to the active player.
     */
    fun setLooping(loop: Boolean) {
        isLooping = loop
        player?.isLooping = loop
    }

    /**
     * Sets playback volume. Values outside the valid [0f, 1f] range are
     * clamped before being applied to both channels of the active player.
     */
    fun setVolume(volume: Float) {
        if (volume.isNaN()) return
        this.volume = volume.coerceIn(0f, 1f)
        player?.setVolume(this.volume, this.volume)
    }

    /**
     * Stops playback and releases all underlying [MediaPlayer] resources.
     * Must be called when the editor screen is disposed. Safe to call
     * multiple times.
     */
    fun release() {
        // release() already stops playback; querying/stopping first can throw
        // in an error state and prevent native resources from being released.
        val previousPlayer = player
        player = null
        prepared = false
        runCatching { previousPlayer?.release() }
    }

    private companion object {
        private const val TAG = "BeatPlayer"
    }
}
