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
