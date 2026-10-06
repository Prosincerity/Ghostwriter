package com.prosincerity.ghostwriter.media

import com.prosincerity.ghostwriter.data.MarkerLoopRole
import com.prosincerity.ghostwriter.data.WaveformMarker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

/**
 * Unit tests for [BeatPlayer].
 *
 * [android.media.MediaPlayer] is not available in JVM unit tests (Android
 * framework stubs don't implement its methods), so these tests cover all
 * logic that can be verified without a real audio device:
 *   - Default state values
 *   - Graceful no-ops when not prepared
 *   - seekTo clamping arithmetic
 *   - Loop toggle state
 *   - Volume state and clamping
 *   - load() failure on a non-existent file
 *   - release() being safe to call multiple times
 */
class BeatPlayerTest {

    @Test
    fun unpreparedPlayer_neverRequestsPlaybackServiceStart() {
        var starts = 0
        val player = BeatPlayer(beforePlay = { starts++; true })
        player.play()
        player.togglePlayPause()
        assertEquals(0, starts)
    }

    @Test
    fun setVolume_ignoresNaNAndKeepsPreviousVolume() {
        val player = BeatPlayer()
        player.setVolume(0.5f)
        player.setVolume(Float.NaN)
        assertEquals(0.5f, player.volume)
    }

    @Test
    fun load_rejectsDirectoryWithoutCreatingMediaPlayer() {
        assertFalse(BeatPlayer().load(tempFolder.root))
    }

    @get:Rule
    val tempFolder = TemporaryFolder()

    // --- Default state ---

    @Test
    fun freshPlayer_isNotReady() {
        val player = BeatPlayer()
        assertFalse(player.isReady)
    }

    @Test
    fun freshPlayer_isNotPlaying() {
        val player = BeatPlayer()
        assertFalse(player.isPlaying)
    }

    @Test
    fun freshPlayer_defaultsToWholeBeatLooping() {
        val player = BeatPlayer()
        assertEquals(BeatLoopMode.WHOLE_BEAT, player.loopMode)
        assertTrue(player.isLooping)
    }

    @Test
    fun freshPlayer_currentPositionIsZero() {
        val player = BeatPlayer()
        assertEquals(0, player.currentPositionMs)
    }

    @Test
    fun freshPlayer_durationIsZero() {
        val player = BeatPlayer()
        assertEquals(0, player.durationMs)
    }

    // --- No-ops when not prepared ---

    @Test
    fun play_whenNotPrepared_keepsPlaybackStopped() {
        val player = BeatPlayer()
        player.play()   // must be a no-op, not throw
        assertFalse(player.isPlaying)
    }

    @Test
    fun pause_whenNotPrepared_keepsPlaybackStopped() {
        val player = BeatPlayer()
        player.pause()
        assertFalse(player.isPlaying)
    }

    @Test
    fun togglePlayPause_whenNotPrepared_keepsPlaybackStopped() {
        val player = BeatPlayer()
        player.togglePlayPause()
        assertFalse(player.isPlaying)
    }

    @Test
    fun seekTo_whenNotPrepared_keepsPositionAtZero() {
        val player = BeatPlayer()
        player.seekTo(5000)  // must be a no-op, not throw
        assertEquals(0, player.currentPositionMs)
    }

    @Test
    fun markersWithoutLoadedBeat_doNotPreparePcmInAnyLoopMode() {
        val player = BeatPlayer()
        player.decodeLoopAudio = { _, _, _ -> error("No beat is loaded") }
        val markers = listOf(WaveformMarker("Start", 400, MarkerLoopRole.START))
        for (mode in BeatLoopMode.entries) {
            player.setLoopMode(mode)
            player.setMarkers(markers)
            player.play()
            assertFalse(player.isPreparingLoop)
            assertFalse(player.isReady)
            assertFalse(player.isPlaying)
        }
    }

    // --- Loop toggle ---

    @Test
    fun toggleLoop_notifiesOwnerWithUpdatedState() {
        val observed = mutableListOf<BeatLoopMode>()
        lateinit var player: BeatPlayer
        player = BeatPlayer(onStateChanged = { observed += player.loopMode })

        player.toggleLoop()
        player.toggleLoop()
        player.toggleLoop()

        assertEquals(listOf(BeatLoopMode.MARKERS, BeatLoopMode.OFF, BeatLoopMode.WHOLE_BEAT), observed)
    }

    @Test
    fun toggleLoop_cyclesEveryModeAndReportsWhetherPlaybackRepeats() {
        val player = BeatPlayer()
        assertEquals(BeatLoopMode.MARKERS, player.toggleLoop())
        assertTrue(player.isLooping)
        assertEquals(BeatLoopMode.OFF, player.toggleLoop())
        assertFalse(player.isLooping)
        assertEquals(BeatLoopMode.WHOLE_BEAT, player.toggleLoop())
        assertTrue(player.isLooping)
    }

    @Test
    fun setLoopMode_keepsSelectionAcrossReleaseAndDoesNotNotifyForSameMode() {
        var changes = 0
        val player = BeatPlayer(onStateChanged = { changes++ })
        player.setLoopMode(BeatLoopMode.OFF)
        player.setLoopMode(BeatLoopMode.OFF)
        assertEquals(1, changes)
        player.release()
        assertEquals(BeatLoopMode.OFF, player.loopMode)
        assertFalse(player.isLooping)
    }

    // --- Volume ---

    @Test
    fun freshPlayer_defaultsToFullVolume() {
        val player = BeatPlayer()
        assertEquals(1f, player.volume)
    }

    @Test
    fun setVolume_updatesVolumeWithinValidRange() {
        val player = BeatPlayer()
        player.setVolume(0.35f)
        assertEquals(0.35f, player.volume)
    }

    @Test
    fun setVolume_clampsValuesOutsideValidRange() {
        val player = BeatPlayer()

        player.setVolume(-0.5f)
        assertEquals(0f, player.volume)

        player.setVolume(1.5f)
        assertEquals(1f, player.volume)
    }

    @Test
    fun toggleMute_restoresLastAudibleVolumeAfterPlayerRelease() {
        val player = BeatPlayer()
        player.setVolume(0.35f)
        player.toggleMute()
        assertEquals(0f, player.volume)

        player.release()
        player.toggleMute()
        assertEquals(0.35f, player.volume)
    }

    @Test
    fun toggleMute_restoresLastAudibleVolumeAfterSliderIsSetToZero() {
        val player = BeatPlayer()
        player.setVolume(0.35f)
        player.setVolume(0f)

        player.toggleMute()
        assertEquals(0.35f, player.volume)
    }

    // --- load() with a missing file ---

    @Test
    fun load_returnsFalse_whenFileDoesNotExist() {
        val player = BeatPlayer()
        val missing = File(tempFolder.root, "nonexistent.mp3")
        val result = player.load(missing)
        assertFalse("load() must return false for missing file", result)
        assertFalse(player.isReady)
    }

    // --- release() ---

    @Test
    fun release_repeatedlyKeepsUnpreparedPlayerStopped() {
        val player = BeatPlayer()
        player.release()
        player.release()
        assertFalse(player.isReady)
        assertFalse(player.isPlaying)
        assertEquals(0, player.currentPositionMs)
        assertEquals(0, player.durationMs)
    }
}
