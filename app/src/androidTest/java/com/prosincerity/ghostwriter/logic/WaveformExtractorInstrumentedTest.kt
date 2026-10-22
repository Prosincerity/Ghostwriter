package com.prosincerity.ghostwriter.logic

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.junit.runner.RunWith
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CancellationException

@RunWith(AndroidJUnit4::class)
class WaveformExtractorInstrumentedTest {
    @get:Rule
    val folder = TemporaryFolder(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir)

    @Test
    fun audioWithoutDuration_returnsNoDurationOrWaveform() {
        // An MPEG-TS stream with AAC packets at the same timestamp has an
        // audio track but no elapsed time from which Android can infer duration.
        fun bytes(vararg values: Int) = ByteArray(values.size) { values[it].toByte() }
        fun packet(pid: Int, payload: ByteArray, sequence: Int = 0): ByteArray =
            ByteArray(188) { 0xff.toByte() }.apply {
                this[0] = 0x47
                this[1] = (0x40 or (pid shr 8)).toByte()
                this[2] = pid.toByte()
                this[3] = (0x10 or sequence).toByte()
                payload.copyInto(this, 4)
            }
        // Program association and map sections, including their MPEG-2 CRCs.
        val pat = bytes(0, 0x00, 0xb0, 0x0d, 0, 1, 0xc1, 0, 0, 0, 1, 0xe1, 0,
            0xe8, 0xf9, 0x5e, 0x7d)
        val pmt = bytes(0, 0x02, 0xb0, 0x12, 0, 1, 0xc1, 0, 0, 0xe1, 1, 0xf0, 0,
            0x0f, 0xe1, 1, 0xf0, 0, 0xec, 0xe2, 0xb0, 0x94)
        // One AAC-LC stereo silence frame in ADTS, wrapped in PES with PTS = 0.
        val pes = bytes(0, 0, 1, 0xc0, 0, 21, 0x80, 0x80, 5, 0x21, 0, 1, 0, 1,
            0xff, 0xf1, 0x50, 0x80, 1, 0xbf, 0xfc, 0x21, 0x10, 4, 0x60, 0x8c, 0x1c)
        val source = generatedFile("unknown-duration.ts").apply {
            outputStream().use { output ->
                output.write(packet(0, pat))
                output.write(packet(0x100, pmt))
                repeat(4) { output.write(packet(0x101, pes, it)) }
            }
        }
        val extractor = android.media.MediaExtractor()
        try {
            extractor.setDataSource(source.absolutePath)
            assertEquals(1, extractor.trackCount)
            val format = extractor.getTrackFormat(0)
            assertTrue(format.getString(android.media.MediaFormat.KEY_MIME)!!.startsWith("audio/"))
            assertTrue(!format.containsKey(android.media.MediaFormat.KEY_DURATION))
        } finally {
            extractor.release()
        }
        assertNull(WaveformExtractor.durationMs(source))
        assertTrue(WaveformExtractor.extractAmplitudes(source, 20).isEmpty())
    }

    @Test
    fun failureDuringDecoding_returnsEmptyAndAllowsRetry() {
        val wav = createPcm16Wav("failed-decode.wav", 500)
        var checks = 0
        val amplitudes = WaveformExtractor.extractAmplitudes(wav, 20) {
            checks++
            check(checks < 3) { "Interrupted source" }
            false
        }
        assertTrue(amplitudes.isEmpty())
        val retried = WaveformExtractor.extractAmplitudes(wav, 20)
        assertEquals(20, retried.size)
        assertTrue(retried.any { it > 0 })
    }

    @Test
    fun pcmWav_reportsDurationAndProducesBoundedPeakBuckets() {
        val wavFile = createPcm16Wav("valid-waveform.wav", durationMs = 500)

        val durationMs = WaveformExtractor.durationMs(wavFile)
        val amplitudes = WaveformExtractor.extractAmplitudes(wavFile, targetSampleCount = 20)

        assertTrue("Expected a duration near 500 ms, got $durationMs", durationMs in 450L..550L)
        assertEquals(20, amplitudes.size)
        assertEquals(1_000, WaveformExtractor.extractAmplitudes(wavFile).size)
        assertArrayEquals(IntArray(20) { 12_000 }, amplitudes)
    }

    @Test
    fun pcmWav_spanningDecoderBuffersIncludesTheFinalPeak() {
        val wavFile = createPcm16Wav("multiple-buffers.wav", durationMs = 8_000)
        java.io.RandomAccessFile(wavFile, "rw").use { output ->
            output.seek(output.length() - 2)
            output.write(0xff)
            output.write(0x7f)
        }

        val expected = IntArray(32) { 12_000 }.apply { this[lastIndex] = 32_767 }

        assertArrayEquals(expected, WaveformExtractor.extractAmplitudes(wavFile, 32))
    }

    @Test
    fun corruptAudio_returnsNoDurationOrWaveform() {
        val corruptFile = generatedFile("corrupt-waveform.wav").apply {
            writeText("not a WAV file")
        }

        assertNull(WaveformExtractor.durationMs(corruptFile))
        assertTrue(WaveformExtractor.extractAmplitudes(corruptFile, 20).isEmpty())
    }

    @Test
    fun cancellation_isPropagatedAndAllowsRetry() {
        val wavFile = createPcm16Wav("cancelled-waveform.wav", durationMs = 500)
        var cancellationChecks = 0

        assertThrows(CancellationException::class.java) {
            WaveformExtractor.extractAmplitudes(wavFile, targetSampleCount = 20) {
                cancellationChecks++
                cancellationChecks >= 2
            }
        }
        val retried = WaveformExtractor.extractAmplitudes(wavFile, targetSampleCount = 20)
        assertEquals(20, retried.size)
        assertTrue(retried.any { it > 0 })
    }

    @Test
    fun silentMp3VariantsMatchTheSinglePacketDecoder() {
        for ((version, channels) in listOf(3 to 2, 2 to 1, 0 to 2)) {
            val source = createSilentMp3("silent-$version-$channels.mp3", version, channels)
            val expected = WaveformExtractor.extractAmplitudes(source, 1003, { false }, allowBatching = false)
            assertEquals(1003, expected.size)
            assertArrayEquals(IntArray(1003), expected)
            assertArrayEquals(expected, WaveformExtractor.extractAmplitudes(source, 1003))
        }
    }

    @Test
    fun shortMp3DrainsTheFinalDecoderPadding() {
        val source = createSilentMp3("short.mp3", frameCount = 4)
        val expected = WaveformExtractor.extractAmplitudes(source, 20, { false }, allowBatching = false)
        assertEquals(20, expected.size)
        assertArrayEquals(expected, WaveformExtractor.extractAmplitudes(source, 20))
    }

    @Test
    fun mp3SampleRateChangeRetainsSinglePacketDecoding() {
        val source = createSilentMp3("changing-rate.mp3", changeRate = true)
        val expected = WaveformExtractor.extractAmplitudes(source, 20, { false }, allowBatching = false)
        assertEquals(20, expected.size)
        assertArrayEquals(expected, WaveformExtractor.extractAmplitudes(source, 20))
    }

    @Test
    fun mp3CancellationDuringOutputIsPropagatedAndAllowsRetry() {
        val source = createSilentMp3("cancelled.mp3")
        var checks = 0
        assertThrows(CancellationException::class.java) {
            WaveformExtractor.extractAmplitudes(source, 20) { ++checks >= 220 }
        }
        assertEquals(220, checks)
        assertArrayEquals(IntArray(20), WaveformExtractor.extractAmplitudes(source, 20))
    }

    @Test
    fun mp3CancellationPredicateStaysOnTheCallingThread() {
        val source = createSilentMp3("thread-affinity.mp3")
        val callingThread = Thread.currentThread()
        val peaks = WaveformExtractor.extractAmplitudes(source, 20) {
            org.junit.Assert.assertSame(callingThread, Thread.currentThread())
            false
        }
        assertArrayEquals(IntArray(20), peaks)
    }

    @Test
    fun mp3FailureDuringOutputReturnsEmptyAndAllowsRetry() {
        val source = createSilentMp3("failed.mp3")
        var checks = 0
        val failed = WaveformExtractor.extractAmplitudes(source, 20) {
            check(++checks < 220) { "Interrupted MP3 source" }
            false
        }
        assertTrue(failed.isEmpty())
        assertArrayEquals(IntArray(20), WaveformExtractor.extractAmplitudes(source, 20))
    }

    /** Zero side information encodes no spectral coefficients: valid silent MP3. */
    private fun createSilentMp3(
        name: String,
        version: Int = 3,
        channels: Int = 2,
        frameCount: Int = 120,
        changeRate: Boolean = false,
    ): File = generatedFile(name).apply {
        outputStream().buffered().use { output ->
            repeat(frameCount) { index ->
                val currentVersion = if (changeRate && index >= frameCount / 2) 2 else version
                val bitrateIndex = if (currentVersion == 3) {
                    if (index % 2 == 0) 9 else 11
                } else {
                    if (index % 2 == 0) 8 else 10
                }
                val bitrate = if (currentVersion == 3) {
                    if (index % 2 == 0) 128 else 192
                } else {
                    if (index % 2 == 0) 64 else 96
                }
                val rate = 44_100 / when (currentVersion) { 3 -> 1; 2 -> 2; else -> 4 }
                val padding = index and 1
                val size = (if (currentVersion == 3) 144_000 else 72_000) * bitrate / rate + padding
                val header = 0xffe00000.toInt() or (currentVersion shl 19) or (1 shl 17) or
                    (1 shl 16) or (bitrateIndex shl 12) or (padding shl 9) or
                    (if (channels == 1) 3 shl 6 else 0)
                val frame = ByteArray(size)
                ByteBuffer.wrap(frame).putInt(header)
                output.write(frame)
            }
        }
    }

    private fun createPcm16Wav(fileName: String, durationMs: Int): File {
        val sampleRate = 8_000
        val channelCount = 1
        val bytesPerSample = 2
        val sampleCount = sampleRate * durationMs / 1_000
        val dataSize = sampleCount * channelCount * bytesPerSample
        val wav = ByteBuffer.allocate(44 + dataSize).order(ByteOrder.LITTLE_ENDIAN)

        wav.put("RIFF".toByteArray(Charsets.US_ASCII))
        wav.putInt(36 + dataSize)
        wav.put("WAVE".toByteArray(Charsets.US_ASCII))
        wav.put("fmt ".toByteArray(Charsets.US_ASCII))
        wav.putInt(16)
        wav.putShort(1.toShort()) // PCM
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

    private fun generatedFile(fileName: String): File = File(folder.root, fileName)
}
