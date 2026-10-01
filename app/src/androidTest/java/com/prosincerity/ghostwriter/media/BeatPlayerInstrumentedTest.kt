package com.prosincerity.ghostwriter.media

import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prosincerity.ghostwriter.data.MarkerLoopRole
import com.prosincerity.ghostwriter.data.WaveformMarker
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
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit

@RunWith(AndroidJUnit4::class)
class BeatPlayerInstrumentedTest {

    @Test
    fun pcmDecoder_preservesWavFramesAndCleansUpCancelledPreparation() {
        val source = createPcm16Wav("beat-loop-decode.wav", 1000)
        val directory = source.parentFile!!
        val decoded = PcmBeatDecoder.decode(source, directory) { false }
        try {
            assertEquals(8000, decoded.sampleRate)
            assertEquals(1, decoded.channels)
            assertEquals(8000L, decoded.frames)
            org.junit.Assert.assertArrayEquals(source.readBytes().copyOfRange(44, source.length().toInt()), decoded.file.readBytes())
        } finally {
            decoded.file.delete()
        }
        val filesBefore = directory.listFiles()?.map { it.name }?.toSet()
        try {
            PcmBeatDecoder.decode(source, directory) { true }
            fail("Cancelled decoding should throw")
        } catch (_: java.util.concurrent.CancellationException) {
            assertEquals(filesBefore, directory.listFiles()?.map { it.name }?.toSet())
        }
    }

    @Test
    fun markerLoops_repeatRegionAndHonorToggleAndMissingBoundaries() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val player = createPlayer()
        val source = createPcm16Wav("beat-player-marker-loop.wav", 2000)
        instrumentation.runOnMainSync {
            assertTrue(player.load(source))
            player.setMarkers(listOf(
                WaveformMarker("Start", 200, MarkerLoopRole.START),
                WaveformMarker("End", 600, MarkerLoopRole.END),
            ))
        }
        waitUntil("PCM preparation did not finish", timeoutMs = 10000) { !player.isPreparingLoop }
        instrumentation.runOnMainSync { player.seekTo(500); player.play() }
        waitUntil("End marker did not wrap to start") { player.isPlaying && player.currentPositionMs in 200..350 }
        instrumentation.runOnMainSync {
            player.setMarkers(listOf(
                WaveformMarker("Start", 100, MarkerLoopRole.START).withSampleRate(8000),
                WaveformMarker("End", 250, MarkerLoopRole.END).withSampleRate(8000),
            ))
        }
        waitUntil("Live end-marker edit did not select the new loop") { player.isPlaying && player.currentPositionMs in 100..180 }
        instrumentation.runOnMainSync { player.toggleLoop() }
        waitUntil("Disabled loop did not play beyond end marker") { player.currentPositionMs > 700 }

        instrumentation.runOnMainSync {
            player.setMarkers(listOf(WaveformMarker("Start", 200, MarkerLoopRole.START)))
            player.toggleLoop()
            player.seekTo(1900)
        }
        waitUntil("Missing end did not wrap at beat end") { player.isPlaying && player.currentPositionMs in 200..350 }
        instrumentation.runOnMainSync {
            player.setMarkers(listOf(WaveformMarker("End", 600, MarkerLoopRole.END)))
            player.seekTo(500)
        }
        waitUntil("Missing start did not wrap to zero") { player.isPlaying && player.currentPositionMs in 0..150 }
        instrumentation.runOnMainSync { player.pause() }
        assertFalse(player.isPlaying)
        instrumentation.runOnMainSync { player.toggleLoop(); player.seekTo(1990); player.play() }
        waitUntil("A short final tail did not complete") { !player.isPlaying }
        assertTrue(player.currentPositionMs >= 1990)
    }

    @Test
    fun fractionalDuration_acceptsTailStartBeforeAndAfterPcmPreparation() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val player = createPlayer()
        val source = createPcm16Wav("beat-fractional-duration.wav", 1000, extraFrames = 4)
        assertEquals(Pair(8000, 1_000_500L), PcmBeatDecoder.audioTiming(source))
        val markers = listOf(WaveformMarker("Start", 1000, MarkerLoopRole.START, 8002, 8000))
        instrumentation.runOnMainSync {
            assertTrue(player.load(source))
            assertTrue(player.hasValidMarkerLoop(markers))
            player.setMarkers(markers)
            assertTrue(player.isPreparingLoop)
        }
        waitUntil("Tail marker did not prepare PCM", 10000) { !player.isPreparingLoop }
        instrumentation.runOnMainSync {
            assertTrue(player.hasValidMarkerLoop(markers))
            assertFalse(player.hasValidMarkerLoop(listOf(markers[0].copy(frameIndex = 8004))))
        }
    }

    @Test
    fun releaseDuringLoopPreparation_cannotAttachOrRestartTheOldBeat() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val player = createPlayer()
        val source = createPcm16Wav("beat-player-cancel-loop.wav", 2000)
        val replacement = createPcm16Wav("beat-player-replacement.wav", 1000)
        instrumentation.runOnMainSync {
            assertTrue(player.load(source))
            player.setMarkers(listOf(WaveformMarker("End", 600, MarkerLoopRole.END)))
            player.play()
            player.release()
            assertTrue(player.load(replacement))
        }
        instrumentation.waitForIdleSync()
        assertFalse(player.isPreparingLoop)
        assertFalse(player.isPlaying)
        assertTrue(player.durationMs in 900..1100)
    }

    @Test
    fun failedLoopPreparation_keepsPlayerReadyAndExplicitPlayRetriesAfterRepair() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val context = instrumentation.targetContext
        val player = BeatPlayer(context).also { beatPlayer = it }
        val file = createPcm16Wav("beat-loop-retry.wav", 2000)
        val original = file.readBytes()
        val cacheBefore = context.cacheDir.listFiles()?.filter { it.name.startsWith("beat-loop-") }?.map { it.name }?.toSet()
        instrumentation.runOnMainSync {
            assertTrue(player.load(file))
            assertTrue(file.delete())
            player.setMarkers(listOf(WaveformMarker("End", 600, MarkerLoopRole.END)))
            assertTrue(player.isPreparingLoop)
        }
        waitUntil("Failed preparation remained pending", 10000) { !player.isPreparingLoop }
        assertTrue(player.isReady)
        assertFalse(player.isPlaying)
        assertEquals(cacheBefore?.minus(file.name), context.cacheDir.listFiles()
            ?.filter { it.name.startsWith("beat-loop-") }?.map { it.name }?.toSet())
        file.writeBytes(original)
        instrumentation.runOnMainSync { player.play() }
        waitUntil("Retry did not prepare loop audio", 10000) { !player.isPreparingLoop }
        waitUntil("Retry did not resume playback", 5000) { player.isPlaying && player.currentPositionMs > 50 }
        instrumentation.runOnMainSync { player.setVolume(0.25f); player.pause() }
        assertEquals(0.25f, player.volume)
        assertFalse(player.isPlaying)
    }

    @Test
    fun playingDuringPreparation_continuesAtLivePositionWithoutRestartingAtLoopStart() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val player = createPlayer()
        val finishDecode = holdLoopDecode(player)
        val file = createPcm16Wav("beat-player-continuous-preparation.wav", 10000)
        instrumentation.runOnMainSync {
            assertTrue(player.load(file))
            player.play()
            player.setMarkers(listOf(WaveformMarker("Start", 4000, MarkerLoopRole.START)))
            assertTrue(player.isPlaying)
            assertTrue(player.isPreparingLoop)
        }
        waitUntil("MediaPlayer stopped advancing during preparation") { player.currentPositionMs > 100 }
        instrumentation.runOnMainSync {
            player.setMarkers(listOf(WaveformMarker("Start", 3000, MarkerLoopRole.START)))
            finishDecode.countDown()
        }
        waitUntil("PCM preparation did not finish", 10000) { !player.isPreparingLoop }
        assertTrue(player.isPlaying)
        assertTrue("PCM handoff restarted at the loop start", player.currentPositionMs in 100..2500)
        val position = player.currentPositionMs
        waitUntil("PCM playback did not continue advancing") { player.currentPositionMs > position + 100 }
    }

    @Test
    fun handoffPastLoopEnd_continuesCurrentPassWithoutJumpingBack() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val player = createPlayer()
        val finishDecode = holdLoopDecode(player)
        val file = createPcm16Wav("beat-player-continuation-tail.wav", 10000)
        instrumentation.runOnMainSync {
            assertTrue(player.load(file))
            player.setMarkers(listOf(
                WaveformMarker("Start", 1000, MarkerLoopRole.START),
                WaveformMarker("End", 3000, MarkerLoopRole.END),
            ))
            player.seekTo(5000)
        }
        waitUntil("Seek did not finish") { player.currentPositionMs in 4900..5100 }
        instrumentation.runOnMainSync { player.play() }
        waitUntil("Playback did not advance") { player.currentPositionMs > 5100 }
        finishDecode.countDown()
        waitUntil("Handoff did not finish", 10000) { !player.isPreparingLoop }
        assertTrue(player.isPlaying)
        assertTrue("Handoff jumped to a loop boundary", player.currentPositionMs in 5100..8000)
    }

    @Test
    fun pauseWhilePcmIsPriming_cancelsScheduledStart() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val player = createPlayer()
        val finishDecode = holdLoopDecode(player)
        val file = createPcm16Wav("beat-player-handoff-pause.wav", 10000)
        instrumentation.runOnMainSync {
            assertTrue(player.load(file))
            player.setMarkers(listOf(WaveformMarker("Start", 3000, MarkerLoopRole.START)))
            player.play()
        }
        finishDecode.countDown()
        waitUntil("PCM did not start priming", 10000) { player.isHandingOff }
        instrumentation.runOnMainSync { player.pause() }
        assertFalse(player.isPlaying)
        assertFalse(player.isPreparingLoop)
        val position = player.currentPositionMs
        // Run beyond the scheduled start to catch a stale timer or output callback.
        val barrier = CountDownLatch(1)
        android.os.Handler(android.os.Looper.getMainLooper()).postDelayed({ barrier.countDown() }, 300)
        assertTrue(barrier.await(2, TimeUnit.SECONDS))
        assertFalse(player.isPlaying)
        assertTrue(kotlin.math.abs(player.currentPositionMs - position) < 50)
    }

    @Test
    fun seekAndPauseDuringPreparation_preserveLatestTransportIntent() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val player = createPlayer()
        val finishDecode = holdLoopDecode(player)
        val file = createPcm16Wav("beat-player-preparation-intent.wav", 2000)
        instrumentation.runOnMainSync {
            assertTrue(player.load(file))
            player.setMarkers(listOf(WaveformMarker("End", 1500, MarkerLoopRole.END)))
            player.play()
            player.pause()
            player.seekTo(700)
            assertTrue(player.isPreparingLoop)
            assertFalse(player.isPlaying)
        }
        waitUntil("MediaPlayer lost seek during preparation") { player.currentPositionMs in 650..750 }
        finishDecode.countDown()
        waitUntil("PCM preparation did not finish", 10000) { !player.isPreparingLoop }
        assertFalse(player.isPlaying)
        waitUntil("Prepared player lost latest seek") { player.currentPositionMs in 650..750 }
    }

    @Test
    fun disablingLoopDuringPreparation_keepsCurrentPositionAtHandoff() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val player = createPlayer()
        val finishDecode = holdLoopDecode(player)
        val file = createPcm16Wav("beat-player-preparation-disable-loop.wav", 10000)
        instrumentation.runOnMainSync {
            assertTrue(player.load(file))
            player.setMarkers(listOf(WaveformMarker("Start", 4000, MarkerLoopRole.START)))
            player.toggleLoop()
            player.play()
        }
        waitUntil("Playback did not advance during decoding") { player.currentPositionMs > 100 }
        finishDecode.countDown()
        waitUntil("PCM preparation did not finish", 10000) { !player.isPreparingLoop }
        assertTrue(player.isPlaying)
        assertTrue("Disabled loop restarted at a marker", player.currentPositionMs < 4000)
    }

    @Test
    fun failedPreparation_keepsAudiblePlaybackRunning() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val player = createPlayer()
        val finishDecode = holdLoopDecode(player, fail = true)
        val file = createPcm16Wav("beat-player-preparation-failure.wav", 10000)
        instrumentation.runOnMainSync {
            assertTrue(player.load(file))
            player.play()
            player.setMarkers(listOf(WaveformMarker("Start", 4000, MarkerLoopRole.START)))
        }
        waitUntil("Playback did not advance during decoding") { player.currentPositionMs > 100 }
        finishDecode.countDown()
        waitUntil("Failed decoding stayed pending", 10000) { !player.isPreparingLoop }
        assertTrue(player.isReady)
        assertTrue(player.isPlaying)
        val position = player.currentPositionMs
        waitUntil("Failure stopped playback") { player.currentPositionMs > position + 100 }
    }

    @Test
    fun completionDuringPreparation_doesNotRestartPlayback() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val player = createPlayer()
        val finishDecode = holdLoopDecode(player)
        val file = createPcm16Wav("beat-player-preparation-completion.wav", 500)
        instrumentation.runOnMainSync {
            assertTrue(player.load(file))
            player.toggleLoop()
            player.setMarkers(listOf(WaveformMarker("End", 400, MarkerLoopRole.END)))
            player.play()
        }
        waitUntil("Nonlooping playback did not finish") { !player.isPlaying }
        finishDecode.countDown()
        waitUntil("PCM preparation did not finish", 10000) { !player.isPreparingLoop }
        assertFalse(player.isPlaying)
    }

    /** Hold decoding off the UI thread so transport assertions do not race fast fixtures. */
    private fun holdLoopDecode(player: BeatPlayer, fail: Boolean = false): CountDownLatch {
        val finish = CountDownLatch(1).also { pendingDecodes += it }
        player.decodeLoopAudio = { source, directory, cancelled ->
            check(finish.await(15, TimeUnit.SECONDS)) { "Test never released decoding" }
            if (fail) error("Decoder failure for test")
            PcmBeatDecoder.decode(source, directory, cancelled)
        }
        return finish
    }

    @Test
    fun streamingPcmFailure_releasesPlayerAndDeletesTemporaryAudio() {
        val instrumentation = InstrumentationRegistry.getInstrumentation()
        val directory = instrumentation.targetContext.cacheDir
        val before = directory.listFiles()!!.filter { it.extension == "pcm" }.toSet()
        val player = BeatPlayer(instrumentation.targetContext).also { beatPlayer = it }
        // Exceed the memory limit so playback depends on the prefetch worker.
        val file = createPcm16Wav("beat-player-disk-failure.wav", 550000)
        assertTrue("Fixture must exceed the PCM memory limit", file.length() - WAV_HEADER_SIZE > PcmSources.MEMORY_LIMIT_BYTES)
        instrumentation.runOnMainSync {
            assertTrue(player.load(file))
            player.toggleLoop()
            player.setMarkers(listOf(WaveformMarker("End", 2000, MarkerLoopRole.END)))
        }
        waitUntil("Disk-backed preparation did not finish", 20000) { !player.isPreparingLoop }
        assertTrue(player.isReady)
        val pcm = directory.listFiles()!!.filter { it.extension == "pcm" && it !in before }.single()
        assertTrue(pcm.length() > PcmSources.MEMORY_LIMIT_BYTES)
        java.io.RandomAccessFile(pcm, "rw").use { it.setLength(0) }
        instrumentation.runOnMainSync { player.seekTo(400000); player.play() }
        waitUntil("Streaming failure did not release playback", 10000) { !player.isReady }
        assertFalse(player.isPlaying)
        assertEquals(0, player.currentPositionMs)
        assertEquals(0, player.durationMs)
        waitUntil("Failed stream retained its PCM file") { !pcm.exists() }
    }

    private val pendingDecodes = mutableListOf<CountDownLatch>()
    private val generatedFiles = mutableListOf<File>()
    private var beatPlayer: BeatPlayer? = null

    @After
    fun releasePlayerAndDeleteGeneratedAudio() {
        beatPlayer?.release()
        beatPlayer = null
        pendingDecodes.forEach { it.countDown() }
        pendingDecodes.clear()
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

    @Test
    fun ensureLoaded_sameBeat_keepsPlaybackAndPosition() {
        val player = createPlayer()
        val file = createPcm16Wav("beat-player-reattach.wav", durationMs = 10_000)
        assertTrue(player.ensureLoaded(file))
        player.seekTo(3_000)
        waitUntil("Seek did not finish") { player.currentPositionMs >= 2_750 }
        player.play()
        assertTrue(player.ensureLoaded(file))
        assertTrue(player.isPlaying)
        assertTrue(player.currentPositionMs >= 2_750)
    }

    @Test
    fun play_whenAudioFocusDenied_doesNotStart() {
        val player = BeatPlayer(beforePlay = { false }).also { beatPlayer = it }
        assertTrue(player.load(createPcm16Wav("beat-player-denied.wav", 1_000)))
        player.play()
        assertFalse(player.isPlaying)
    }

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

    private fun createPcm16Wav(fileName: String, durationMs: Int, extraFrames: Int = 0): File {
        val sampleRate = 8_000
        val channelCount = 1
        val bytesPerSample = 2
        val sampleCount = (sampleRate.toLong() * durationMs / 1_000).toInt() + extraFrames
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
