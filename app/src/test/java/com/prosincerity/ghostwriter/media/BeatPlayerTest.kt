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

/** Transport state and input validation that do not require Android audio playback. */
class BeatPlayerTest {

    @Test
    fun unpreparedTransport_keepsPlaybackStoppedWithoutStartingTheService() {
        val actions = listOf<(BeatPlayer) -> Unit>(
            { it.play() }, { it.pause() }, { it.togglePlayPause() }, { it.seekTo(5000) },
            { it.release(); it.release() },
        )
        for (action in actions) {
            var starts = 0
            val player = BeatPlayer(beforePlay = { starts++; true })
            action(player)
            assertFalse(player.isReady)
            assertFalse(player.isPlaying)
            assertEquals(0, player.currentPositionMs)
            assertEquals(0, player.durationMs)
            assertEquals(0, starts)
        }
    }

    @Test
    fun setVolume_preservesValidVolumeWhenNaNIsRejected() {
        val player = BeatPlayer()
        player.setVolume(0.35f)
        assertEquals(0.35f, player.volume)
        player.setVolume(Float.NaN)
        assertEquals(0.35f, player.volume)
    }

    @Test
    fun load_rejectsMissingFilesAndDirectoriesWithoutPreparingPlayback() {
        for (file in listOf(tempFolder.root, File(tempFolder.root, "nonexistent.mp3"))) {
            val player = BeatPlayer()
            assertFalse(player.load(file))
            assertFalse(player.isReady)
            assertFalse(player.isPlaying)
        }
    }

    @get:Rule
    val tempFolder = TemporaryFolder()

    // --- Default state ---

    @Test
    fun freshPlayer_startsStoppedWithWholeBeatLoopingAndFullVolume() {
        val player = BeatPlayer()
        assertFalse(player.isReady)
        assertFalse(player.isPlaying)
        assertEquals(0, player.currentPositionMs)
        assertEquals(0, player.durationMs)
        assertEquals(BeatLoopMode.WHOLE_BEAT, player.loopMode)
        assertTrue(player.isLooping)
        assertEquals(1f, player.volume)
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
    fun toggleLoop_cyclesModesAndNotifiesTheOwnerWithUpdatedPlaybackState() {
        val observed = mutableListOf<Pair<BeatLoopMode, Boolean>>()
        lateinit var player: BeatPlayer
        player = BeatPlayer(onStateChanged = { observed += player.loopMode to player.isLooping })
        val cycle = listOf(
            BeatLoopMode.MARKERS to true,
            BeatLoopMode.OFF to false,
            BeatLoopMode.WHOLE_BEAT to true,
        )
        for ((mode, looping) in cycle) {
            assertEquals(mode, player.toggleLoop())
            assertEquals(looping, player.isLooping)
        }
        assertEquals(cycle, observed)
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
}
