package com.ghostwriter.exe.media

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
    fun freshPlayer_defaultsLoopingToTrue() {
        val player = BeatPlayer()
        assertTrue("Loop should default to ON", player.isLooping)
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
    fun play_whenNotPrepared_doesNotCrash() {
        val player = BeatPlayer()
        player.play()   // must be a no-op, not throw
        assertFalse(player.isPlaying)
    }

    @Test
    fun pause_whenNotPrepared_doesNotCrash() {
        val player = BeatPlayer()
        player.pause()  // must be a no-op, not throw
    }

    @Test
    fun togglePlayPause_whenNotPrepared_doesNotCrash() {
        val player = BeatPlayer()
        player.togglePlayPause()
        assertFalse(player.isPlaying)
    }

    @Test
    fun seekTo_whenNotPrepared_doesNotCrash() {
        val player = BeatPlayer()
        player.seekTo(5000)  // must be a no-op, not throw
        assertEquals(0, player.currentPositionMs)
    }

    // --- Loop toggle ---

    @Test
    fun toggleLoop_flipsFromTrueToFalse() {
        val player = BeatPlayer()
        assertTrue(player.isLooping)         // default: true
        val after = player.toggleLoop()
        assertFalse("Loop should have been toggled off", after)
        assertFalse(player.isLooping)
    }

    @Test
    fun toggleLoop_flipsBackToTrue() {
        val player = BeatPlayer()
        player.toggleLoop()  // true → false
        val back = player.toggleLoop()  // false → true
        assertTrue("Loop should have been toggled back on", back)
        assertTrue(player.isLooping)
    }

    @Test
    fun setLooping_setsExplicitState() {
        val player = BeatPlayer()
        player.setLooping(false)
        assertFalse(player.isLooping)
        player.setLooping(true)
        assertTrue(player.isLooping)
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
    fun release_whenNeverLoaded_doesNotCrash() {
        val player = BeatPlayer()
        player.release()  // must be safe
        assertFalse(player.isReady)
        assertFalse(player.isPlaying)
    }

    @Test
    fun release_calledTwice_doesNotCrash() {
        val player = BeatPlayer()
        player.release()
        player.release()  // second call must also be safe
    }
}
