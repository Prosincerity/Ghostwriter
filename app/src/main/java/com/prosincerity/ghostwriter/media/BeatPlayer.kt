package com.prosincerity.ghostwriter.media

import android.content.Context
import android.media.AudioAttributes
import android.media.MediaPlayer
import android.os.Handler
import android.os.Looper
import android.os.PowerManager
import android.util.Log
import android.widget.Toast
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.setValue
import com.prosincerity.ghostwriter.data.WaveformMarker
import com.prosincerity.ghostwriter.logic.MarkerLoopRange
import com.prosincerity.ghostwriter.logic.MarkerLoopFrames
import java.io.File
import java.util.concurrent.atomic.AtomicBoolean

/**
 * Service-owned beat playback using MediaPlayer and smooth PCM marker loops.
 *
 * Design decisions:
 * - Zero external dependencies: uses only AOSP media APIs.
 * - Production playback is owned by BeatPlaybackService, independently of screens.
 * - Looping defaults to ON: rap songwriters almost always want their
 *   instrumental to loop continuously while writing verses.
 * - State is exposed through properties; loop state is observable by Compose
 *   so external media controls and the editor share the same setting.
 *   Playback changes also notify the owning service to update its media session.
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
    private var markers: List<WaveformMarker> = emptyList()
    private var smoothPlayback: SmoothLoopPlayback? = null
    private var loopPcm: PcmBeat? = null
    private var sourceDurationUs = 0L
    private var loopPreparation: AtomicBoolean? = null
    private var preparationPositionMs = 0
    private var resumeAfterPreparation = false
    private var loopWakeLock: PowerManager.WakeLock? = null

    var isPreparingLoop: Boolean by mutableStateOf(false)
        private set
    var sampleRate: Int by mutableIntStateOf(0)
        private set

    /** Reattaching an editor must not restart the current beat. */
    fun ensureLoaded(beatFile: File): Boolean =
        (prepared && loadedFile == beatFile) || load(beatFile)

    /** True if a beat file is loaded and [MediaPlayer] is prepared. */
    val isReady: Boolean get() = prepared

    /** True if the player is currently playing audio. */
    val isPlaying: Boolean get() = if (isPreparingLoop) resumeAfterPreparation
        else smoothPlayback?.isPlaying ?: (player?.isPlaying == true)

    /** Whether playback repeats between marker boundaries, or the whole beat without markers. */
    var isLooping: Boolean by mutableStateOf(true)
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
        get() = if (!prepared) 0 else if (isPreparingLoop) preparationPositionMs
            else smoothPlayback?.currentPositionMs ?: (player?.currentPosition ?: 0)

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
            val timing = PcmBeatDecoder.audioTiming(beatFile)
            sampleRate = timing?.first ?: 0
            sourceDurationUs = timing?.second ?: 0
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
        // An explicit play retries preparation after a transient decoder/storage failure.
        if (smoothPlayback == null && !isPreparingLoop && hasMarkerLoop()) setMarkers(markers)
        when {
            isPreparingLoop -> resumeAfterPreparation = true
            smoothPlayback != null -> smoothPlayback?.play()
            else -> player?.start()
        }
        updateLoopWakeLock()
        onStateChanged()
    }

    /** Pauses playback. No-op if not playing. */
    fun pause() {
        onPauseRequested()
        resumeAfterPreparation = false
        smoothPlayback?.pause()
        player?.takeIf { it.isPlaying }?.pause()
        updateLoopWakeLock()
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
        when {
            isPreparingLoop -> preparationPositionMs = clamped
            smoothPlayback != null -> smoothPlayback?.seekTo(clamped)
            else -> player?.seekTo(clamped)
        }
        onStateChanged()
    }

    /**
     * Toggles looping on/off and applies the change to the active player.
     * @return the new looping state.
     */
    fun toggleLoop(): Boolean {
        isLooping = !isLooping
        player?.isLooping = isLooping
        smoothPlayback?.configure(markerFrames(), isLooping)
        onStateChanged()
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
        smoothPlayback?.setVolume(this.volume)
    }

    private fun hasMarkerLoop(): Boolean = hasValidMarkerLoop(markers)

    /** Editor validation and preparation must use the same audio boundaries. */
    internal fun hasValidMarkerLoop(value: List<WaveformMarker>): Boolean {
        loopPcm?.let { return MarkerLoopFrames.fromMarkers(value, it.sampleRate, it.frames) != null }
        return if (sampleRate > 0) MarkerLoopFrames.fromDurationUs(value, sampleRate,
            sourceDurationUs.takeIf { it > 0 } ?: durationMs.toLong() * 1000) != null
        else MarkerLoopRange.fromMarkers(value, durationMs.toLong()) != null
    }

    private fun markerFrames(): MarkerLoopFrames? = loopPcm?.let {
        MarkerLoopFrames.fromMarkers(markers, it.sampleRate, it.frames)
    }

    /** The service-owned player keeps marker loops alive after the editor unbinds. */
    fun setMarkers(value: List<WaveformMarker>) {
        if (!prepared) return
        markers = value.toList()
        smoothPlayback?.let { it.configure(markerFrames(), isLooping); return }
        if (!hasMarkerLoop() || isPreparingLoop) return
        val source = loadedFile ?: return
        val directory = context?.cacheDir ?: source.parentFile ?: return
        val cancelled = AtomicBoolean(false)
        loopPreparation = cancelled
        preparationPositionMs = currentPositionMs
        resumeAfterPreparation = isPlaying
        player?.takeIf { it.isPlaying }?.pause()
        isPreparingLoop = true
        updateLoopWakeLock()
        onStateChanged()
        val main = Handler(Looper.getMainLooper())
        Thread({
            val decoded = runCatching {
                val pcm = PcmBeatDecoder.decode(source, directory, cancelled::get)
                try { Pair(pcm, PcmSources.prepare(pcm, cancelled::get)) }
                catch (problem: Exception) { pcm.file.delete(); throw problem }
            }
            main.post {
                if (cancelled.get() || loopPreparation !== cancelled) {
                    decoded.getOrNull()?.second?.close()
                    return@post
                }
                isPreparingLoop = false
                decoded.onSuccess { (pcm, audio) ->
                    loopPcm = pcm
                    sampleRate = pcm.sampleRate
                    smoothPlayback = SmoothLoopPlayback(
                        pcm, audio, preparationPositionMs, markerFrames(), isLooping, volume,
                        onStateChanged = {
                            if (!cancelled.get() && loopPreparation === cancelled) { updateLoopWakeLock(); onStateChanged() }
                        },
                        onFailure = { failure ->
                            if (!cancelled.get() && loopPreparation === cancelled) {
                                Log.e(TAG, "Smooth loop playback failed", failure)
                                release()
                                context?.let { Toast.makeText(it, "Couldn't play loop audio", Toast.LENGTH_LONG).show() }
                            }
                        },
                    )
                    if (resumeAfterPreparation) smoothPlayback?.play()
                }.onFailure { failure ->
                    loopPreparation = null
                    resumeAfterPreparation = false
                    Log.e(TAG, "Couldn't prepare loop audio", failure)
                    context?.let { Toast.makeText(it, "Couldn't prepare smooth loop audio", Toast.LENGTH_LONG).show() }
                    player?.seekTo(preparationPositionMs)
                }
                updateLoopWakeLock()
                onStateChanged()
            }
        }, "Beat loop decoder").apply { isDaemon = true; start() }
    }

    private fun updateLoopWakeLock() {
        if (isPlaying && (isPreparingLoop || smoothPlayback != null)) {
            if (loopWakeLock == null) loopWakeLock = context?.getSystemService(PowerManager::class.java)
                ?.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, "Ghostwriter:marker-loop")
                ?.apply { setReferenceCounted(false) }
            @Suppress("WakelockTimeout") // Released on pause, completion, errors and service destruction.
            if (loopWakeLock?.isHeld == false) loopWakeLock?.acquire()
        } else if (loopWakeLock?.isHeld == true) loopWakeLock?.release()
    }

    /** Mutes or restores the last audible level, even if the controls are recreated. */
    fun toggleMute() {
        setVolume(if (volume == 0f) lastAudibleVolume else 0f)
    }

    /**
     * Stops playback, cancels loop preparation and releases media resources.
     * Called on beat/project replacement or service destruction, never screen exit.
     */
    fun release() {
        // release() already stops playback; querying/stopping first can throw
        // in an error state and prevent native resources from being released.
        val previousPlayer = player
        loopPreparation?.set(true)
        loopPreparation = null
        isPreparingLoop = false
        resumeAfterPreparation = false
        markers = emptyList()
        smoothPlayback?.close()
        smoothPlayback = null
        loopPcm = null
        sourceDurationUs = 0
        sampleRate = 0
        if (loopWakeLock?.isHeld == true) loopWakeLock?.release()
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
