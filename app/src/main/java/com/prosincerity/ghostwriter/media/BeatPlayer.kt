package com.prosincerity.ghostwriter.media

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.PowerManager
import android.util.Log
import java.io.File

/**
 * Lightweight wrapper around Android's built-in [MediaPlayer].
 *
 * Design decisions:
 * - Zero external dependencies: uses only AOSP [MediaPlayer].
 * - Production playback is owned by BeatPlaybackService, independently of screens.
 * - Looping defaults to ON: rap songwriters almost always want their
 *   instrumental to loop continuously while writing verses.
 * - State is exposed through properties; callers observe it via Compose state
 *   (see EditorScreen) rather than callbacks or flows.
 */
class BeatPlayer(
    private val context: Context? = null,
    private val beforePlay: () -> Boolean = { true },
    private val onStateChanged: () -> Unit = {},
    private val onPauseRequested: () -> Unit = {},
) {

    private var player: MediaPlayer? = null
    private var prepared = false
    private var loadedFile: File? = null

    /** Reattaching an editor must not restart the current beat. */
    fun ensureLoaded(beatFile: File): Boolean =
        (prepared && loadedFile == beatFile) || load(beatFile)

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

    private var lastAudibleVolume = 1f

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
            mp.setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA)
                .setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
            context?.let { mp.setWakeMode(it, PowerManager.PARTIAL_WAKE_LOCK) }
            mp.setOnCompletionListener { onStateChanged() }
            mp.setOnSeekCompleteListener { onStateChanged() }
            mp.setOnErrorListener { _, _, _ -> release(); true }
            mp.setDataSource(beatFile.absolutePath)
            mp.isLooping = isLooping
            mp.setVolume(volume, volume)
            mp.prepare()
            prepared = true
            loadedFile = beatFile
            onStateChanged()
            true
        }.onFailure { e ->
            Log.e(TAG, "BeatPlayer: failed to load ${beatFile.name}", e)
            release()
        }.getOrDefault(false)
    }

    /** Starts or resumes playback. No-op if not prepared. */
    fun play() {
        if (!prepared || isPlaying || !beforePlay()) return
        player?.start()
        onStateChanged()
    }

    /** Pauses playback. No-op if not playing. */
    fun pause() {
        onPauseRequested()
        player?.takeIf { it.isPlaying }?.pause()
        onStateChanged()
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
     * Sets playback volume. Values outside the valid [0f, 1f] range are
     * clamped before being applied to both channels of the active player.
     */
    fun setVolume(volume: Float) {
        if (volume.isNaN()) return
        this.volume = volume.coerceIn(0f, 1f)
        if (this.volume > 0f) lastAudibleVolume = this.volume
        player?.setVolume(this.volume, this.volume)
    }

    /** Mutes or restores the last audible level, even if the controls are recreated. */
    fun toggleMute() {
        setVolume(if (volume == 0f) lastAudibleVolume else 0f)
    }

    /**
     * Stops playback and releases all underlying [MediaPlayer] resources.
     * Called on beat/project replacement or service destruction, never screen exit.
     */
    fun release() {
        // release() already stops playback; querying/stopping first can throw
        // in an error state and prevent native resources from being released.
        val previousPlayer = player
        player = null
        prepared = false
        loadedFile = null
        runCatching { previousPlayer?.release() }
        onStateChanged()
    }

    private companion object {
        private const val TAG = "BeatPlayer"
    }
}
