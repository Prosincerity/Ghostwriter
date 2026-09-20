package com.prosincerity.ghostwriter.media

import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder

@RunWith(AndroidJUnit4::class)
class BeatPlayerInstrumentedTest {

    private val generatedFiles = mutableListOf<File>()
    private var beatPlayer: BeatPlayer? = null

    @After
    fun releasePlayerAndDeleteGeneratedAudio() {
        beatPlayer?.release()
        beatPlayer = null
        generatedFiles.forEach(File::delete)
        generatedFiles.clear()
    }

    @Test
    fun load_validPcmWav_preparesPlayerWithConfiguredState() {
        val player = createPlayer()
        val wavFile = createPcm16Wav("beat-player-valid.wav", durationMs = 1_000)
        player.toggleLoop()
        player.setVolume(0.25f)

        assertTrue(player.load(wavFile))

        assertTrue(player.isReady)
        assertFalse(player.isPlaying)
        assertFalse(player.isLooping)
        assertEquals(0.25f, player.volume)
        assertTrue("Expected a duration near 1 second, got ${player.durationMs}", player.durationMs in 900..1_100)
        assertEquals(0, player.currentPositionMs)
    }

    @Test
    fun playbackControls_loadedPlayer_startPauseToggleAndClampSeeks() {
        val player = createPlayer()
        val wavFile = createPcm16Wav("beat-player-controls.wav", durationMs = 5_000)
        assertTrue(player.load(wavFile))

        player.play()
        waitUntil("Player did not start") { player.isPlaying }
        player.pause()
        waitUntil("Player did not pause") { !player.isPlaying }

        player.togglePlayPause()
        waitUntil("Toggle did not start playback") { player.isPlaying }
        player.togglePlayPause()
        waitUntil("Toggle did not pause playback") { !player.isPlaying }

        player.seekTo(-1_000)
        waitUntil("Negative seek was not clamped to the start") {
            player.currentPositionMs in 0..SEEK_TOLERANCE_MS
        }

        player.seekTo(player.durationMs + 1_000)
        waitUntil("Seek past duration was not clamped to the end") {
            player.currentPositionMs >= player.durationMs - SEEK_TOLERANCE_MS
        }

        player.toggleLoop()
        player.setVolume(0.4f)
        assertFalse(player.isLooping)
        assertEquals(0.4f, player.volume)
    }

    @Test
    fun load_existingCorruptAudio_returnsFalseAndResetsState() {
        val player = createPlayer()
        val corruptFile = generatedFile("beat-player-corrupt.wav").apply {
            writeText("not a WAV file")
        }

        assertFalse(player.load(corruptFile))
        assertFalse(player.isReady)
        assertFalse(player.isPlaying)
        assertEquals(0, player.currentPositionMs)
        assertEquals(0, player.durationMs)
    }

    @Test
    fun release_loadedPlayer_resetsStateAndRemainsIdempotent() {
        val player = createPlayer()
        val wavFile = createPcm16Wav("beat-player-release.wav", durationMs = 1_000)
        assertTrue(player.load(wavFile))

        player.release()
        player.release()

        assertFalse(player.isReady)
        assertFalse(player.isPlaying)
        assertEquals(0, player.currentPositionMs)
        assertEquals(0, player.durationMs)
    }

    private fun createPlayer(): BeatPlayer = BeatPlayer().also { beatPlayer = it }

    private fun waitUntil(
        message: String,
        timeoutMs: Long = 2_000,
        condition: () -> Boolean,
    ) {
        val deadline = SystemClock.uptimeMillis() + timeoutMs
        while (!condition() && SystemClock.uptimeMillis() < deadline) {
            SystemClock.sleep(10)
        }
        if (!condition()) fail(message)
    }

    private fun createPcm16Wav(fileName: String, durationMs: Int): File {
        val sampleRate = 8_000
        val channelCount = 1
        val bytesPerSample = 2
        val sampleCount = sampleRate * durationMs / 1_000
        val dataSize = sampleCount * channelCount * bytesPerSample
        val wav = ByteBuffer.allocate(WAV_HEADER_SIZE + dataSize).order(ByteOrder.LITTLE_ENDIAN)

        wav.put("RIFF".toByteArray(Charsets.US_ASCII))
        wav.putInt(36 + dataSize)
        wav.put("WAVE".toByteArray(Charsets.US_ASCII))
        wav.put("fmt ".toByteArray(Charsets.US_ASCII))
        wav.putInt(16)
        wav.putShort(1.toShort())
        wav.putShort(channelCount.toShort())
        wav.putInt(sampleRate)
        wav.putInt(sampleRate * channelCount * bytesPerSample)
        wav.putShort((channelCount * bytesPerSample).toShort())
        wav.putShort(16.toShort())
        wav.put("data".toByteArray(Charsets.US_ASCII))
        wav.putInt(dataSize)

        repeat(sampleCount) { sampleIndex ->
            val sample = if ((sampleIndex / 20) % 2 == 0) 12_000 else -12_000
            wav.putShort(sample.toShort())
        }

        return generatedFile(fileName).apply { writeBytes(wav.array()) }
    }

    private fun generatedFile(fileName: String): File {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return File(context.cacheDir, fileName).also {
            it.delete()
            generatedFiles += it
        }
    }

    private companion object {
        private const val WAV_HEADER_SIZE = 44
        private const val SEEK_TOLERANCE_MS = 250
    }
}
