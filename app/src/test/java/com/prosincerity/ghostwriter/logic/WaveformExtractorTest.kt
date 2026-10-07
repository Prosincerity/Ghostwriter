package com.prosincerity.ghostwriter.logic

import android.media.AudioFormat
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CancellationException

class WaveformExtractorTest {
    @Test
    fun decoderProgressGuard_requiresAPositiveStallLimit() {
        for (limit in listOf(0, -1)) {
            assertThrows(IllegalArgumentException::class.java) {
                WaveformExtractor.DecoderProgressGuard(limit)
            }
        }
    }


    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun bucketIndexForTimestamp_mapsTimestampsToEvenBuckets() {
        assertEquals(0, WaveformExtractor.bucketIndexForTimestamp(0L, 1_000L, 4))
        assertEquals(0, WaveformExtractor.bucketIndexForTimestamp(249L, 1_000L, 4))
        assertEquals(1, WaveformExtractor.bucketIndexForTimestamp(250L, 1_000L, 4))
        assertEquals(3, WaveformExtractor.bucketIndexForTimestamp(999L, 1_000L, 4))
    }

    @Test
    fun bucketIndexForTimestamp_clampsTimestampsToTheWaveformBounds() {
        assertEquals(0, WaveformExtractor.bucketIndexForTimestamp(-1L, 1_000L, 4))
        assertEquals(3, WaveformExtractor.bucketIndexForTimestamp(1_000L, 1_000L, 4))
        assertEquals(3, WaveformExtractor.bucketIndexForTimestamp(2_000L, 1_000L, 4))
    }

    @Test
    fun bucketIndexForTimestamp_doesNotOverflowForExtremeContainerMetadata() {
        assertEquals(
            999,
            WaveformExtractor.bucketIndexForTimestamp(
                timestampUs = Long.MAX_VALUE - 1,
                durationUs = Long.MAX_VALUE,
                bucketCount = 1_000,
            ),
        )
    }

    @Test
    fun bucketIndexForTimestamp_rejectsInvalidDimensions() {
        assertEquals(null, WaveformExtractor.bucketIndexForTimestamp(0L, 0L, 4))
        assertEquals(null, WaveformExtractor.bucketIndexForTimestamp(0L, 1_000L, 0))
    }

    @Test
    fun pcmAmplitude_decodesSupportedSampleFormats() {
        for ((sample, amplitude) in listOf(0x00 to 32_768, 0x80 to 0, 0xFF to 32_512)) {
            assertEquals(
                amplitude,
                WaveformExtractor.pcmAmplitude(
                    ByteBuffer.wrap(byteArrayOf(sample.toByte())),
                    AudioFormat.ENCODING_PCM_8BIT,
                ),
            )
        }
        assertEquals(
            32_768,
            WaveformExtractor.pcmAmplitude(
                ByteBuffer.wrap(byteArrayOf(0x00, 0x80.toByte())).order(ByteOrder.LITTLE_ENDIAN),
                AudioFormat.ENCODING_PCM_16BIT,
            ),
        )
        val floatSample = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
            .putFloat(-0.5f).apply { flip() }
        assertEquals(
            16_383,
            WaveformExtractor.pcmAmplitude(floatSample, AudioFormat.ENCODING_PCM_FLOAT),
        )
    }

    @Test
    fun pcmAmplitude_clampsFloatSamplesOutsideTheSupportedRange() {
        for (value in listOf(-2f, 2f)) {
            val sample = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putFloat(value).apply { flip() }
            assertEquals(32_767, WaveformExtractor.pcmAmplitude(sample, AudioFormat.ENCODING_PCM_FLOAT))
        }
    }

    @Test
    fun extractAmplitudes_returnsEmptyForMissingFile() {
        val missing = File(tempFolder.root, "missing.wav")

        assertTrue(WaveformExtractor.extractAmplitudes(missing, 100).isEmpty())
        assertEquals(null, WaveformExtractor.durationMs(missing))
    }

    @Test
    fun extractAmplitudes_rejectsNonPositiveTargetSampleCounts() {
        val source = tempFolder.newFile("source.wav")

        assertTrue(WaveformExtractor.extractAmplitudes(source, 0).isEmpty())
        assertTrue(WaveformExtractor.extractAmplitudes(source, -1).isEmpty())
    }

    @Test
    fun extractAmplitudes_stopsBeforeDecodingWhenCancellationIsRequested() {
        val source = tempFolder.newFile("cancelled.wav").apply { writeText("audio") }

        assertThrows(CancellationException::class.java) {
            WaveformExtractor.extractAmplitudes(source, 100) { true }
        }
    }

    @Test
    fun decoderProgressGuard_resetsAfterProgressAndRejectsConsecutiveStalls() {
        val guard = WaveformExtractor.DecoderProgressGuard(maxConsecutiveStalls = 2)

        guard.record(madeProgress = false)
        guard.record(madeProgress = true)
        guard.record(madeProgress = false)
        assertThrows(IllegalStateException::class.java) {
            guard.record(madeProgress = false)
        }
    }
}
