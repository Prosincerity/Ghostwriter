package com.prosincerity.ghostwriter.logic

import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.junit.runner.RunWith
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CancellationException

@RunWith(AndroidJUnit4::class)
class WaveformExtractorInstrumentedTest {
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
        assertTrue(checks >= 3)
        assertTrue(amplitudes.isEmpty())
        val retried = WaveformExtractor.extractAmplitudes(wav, 20)
        assertEquals(20, retried.size)
        assertTrue(retried.any { it > 0 })
    }

    @Test fun defaultResolution_producesOneThousandPeaks() {
        val wav = createPcm16Wav("default-resolution.wav", 100)
        assertEquals(1000, WaveformExtractor.extractAmplitudes(wav).size)
    }

    private val generatedFiles = mutableListOf<File>()

    @After
    fun deleteGeneratedAudio() {
        generatedFiles.forEach(File::delete)
        generatedFiles.clear()
    }

    @Test
    fun pcmWav_reportsDurationAndProducesBoundedPeakBuckets() {
        val wavFile = createPcm16Wav("valid-waveform.wav", durationMs = 500)

        val durationMs = WaveformExtractor.durationMs(wavFile)
        val amplitudes = WaveformExtractor.extractAmplitudes(wavFile, targetSampleCount = 20)

        assertTrue("Expected a duration near 500 ms, got $durationMs", durationMs in 450L..550L)
        assertEquals(20, amplitudes.size)
        assertTrue("Expected decoded audio to contain a non-zero peak", amplitudes.any { it > 0 })
        assertTrue(amplitudes.all { it in 0..32_768 })
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
    fun cancellationAfterDecoderSetup_isPropagatedAndReleasesResources() {
        val wavFile = createPcm16Wav("cancelled-waveform.wav", durationMs = 500)
        var cancellationChecks = 0

        try {
            WaveformExtractor.extractAmplitudes(wavFile, targetSampleCount = 20) {
                cancellationChecks++
                cancellationChecks >= 2
            }
            fail("Expected waveform extraction to be cancelled")
        } catch (_: CancellationException) {
            assertTrue(cancellationChecks >= 2)
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

    private fun generatedFile(fileName: String): File {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        return File(context.cacheDir, fileName).also {
            it.delete()
            generatedFiles += it
        }
    }
}
