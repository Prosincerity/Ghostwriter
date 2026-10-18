package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CancellationException

class WaveformPeaksTest {
    @Test
    fun bucketIndexForTimestamp_mapsTimestampsToEvenBuckets() {
        assertEquals(0, WaveformPeaks.bucketIndexForTimestamp(0L, 1_000L, 4))
        assertEquals(0, WaveformPeaks.bucketIndexForTimestamp(249L, 1_000L, 4))
        assertEquals(1, WaveformPeaks.bucketIndexForTimestamp(250L, 1_000L, 4))
        assertEquals(3, WaveformPeaks.bucketIndexForTimestamp(999L, 1_000L, 4))
    }

    @Test
    fun bucketIndexForTimestamp_clampsTimestampsToTheWaveformBounds() {
        assertEquals(0, WaveformPeaks.bucketIndexForTimestamp(-1L, 1_000L, 4))
        assertEquals(3, WaveformPeaks.bucketIndexForTimestamp(1_000L, 1_000L, 4))
        assertEquals(3, WaveformPeaks.bucketIndexForTimestamp(2_000L, 1_000L, 4))
    }

    @Test
    fun bucketIndexForTimestamp_doesNotOverflowForExtremeContainerMetadata() {
        assertEquals(
            999,
            WaveformPeaks.bucketIndexForTimestamp(
                timestampUs = Long.MAX_VALUE - 1,
                durationUs = Long.MAX_VALUE,
                bucketCount = 1_000,
            ),
        )
    }

    @Test
    fun bucketIndexForTimestamp_rejectsInvalidDimensions() {
        assertEquals(null, WaveformPeaks.bucketIndexForTimestamp(0L, 0L, 4))
        assertEquals(null, WaveformPeaks.bucketIndexForTimestamp(0L, 1_000L, 0))
    }

    @Test
    fun pcmAmplitude_decodesSupportedSampleFormats() {
        for ((sample, amplitude) in listOf(0x00 to 32_768, 0x80 to 0, 0xFF to 32_512)) {
            assertEquals(
                amplitude,
                WaveformPeaks.pcmAmplitude(
                    ByteBuffer.wrap(byteArrayOf(sample.toByte())),
                    PcmEncoding.PCM_8BIT,
                ),
            )
        }
        assertEquals(
            32_768,
            WaveformPeaks.pcmAmplitude(
                ByteBuffer.wrap(byteArrayOf(0x00, 0x80.toByte())).order(ByteOrder.LITTLE_ENDIAN),
                PcmEncoding.PCM_16BIT,
            ),
        )
        val floatSample = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
            .putFloat(-0.5f).apply { flip() }
        assertEquals(
            16_383,
            WaveformPeaks.pcmAmplitude(floatSample, PcmEncoding.PCM_FLOAT),
        )
    }

    @Test
    fun pcmAmplitude_clampsFloatSamplesOutsideTheSupportedRange() {
        for (value in listOf(-2f, 2f)) {
            val sample = ByteBuffer.allocate(4).order(ByteOrder.LITTLE_ENDIAN)
                .putFloat(value).apply { flip() }
            assertEquals(32_767, WaveformPeaks.pcmAmplitude(sample, PcmEncoding.PCM_FLOAT))
        }
    }

    @Test
    fun appendFrames_decodesEachSupportedFormatIntoTimestampedBuckets() {
        val inputs = listOf(
            Triple(PcmEncoding.PCM_8BIT, ByteBuffer.wrap(byteArrayOf(128.toByte(), 0, 255.toByte())),
                intArrayOf(0, 32768, 32512)),
            Triple(PcmEncoding.PCM_16BIT, pcm16(0, Short.MIN_VALUE, Short.MAX_VALUE),
                intArrayOf(0, 32768, 32767)),
            Triple(PcmEncoding.PCM_FLOAT,
                ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN)
                    .putFloat(0f).putFloat(-1f).putFloat(0.5f).apply { flip() },
                intArrayOf(0, 32767, 16383)),
        )
        for ((encoding, buffer, expected) in inputs) {
            val buckets = IntArray(3)

            val frames = WaveformPeaks.appendFrames(
                buffer, PcmFormat(1, 1000, encoding), 0L, 3000L, buckets, { false },
            )

            assertEquals(3L, frames)
            assertArrayEquals(expected, buckets)
        }
    }

    @Test
    fun appendFrames_stereoPeaksUseTheLoudestChannelAndCountFrames() {
        val buckets = IntArray(2)

        val frames = WaveformPeaks.appendFrames(
            pcm16(10, -400, Short.MIN_VALUE, 123),
            PcmFormat(2, 1000, PcmEncoding.PCM_16BIT), 0L, 2000L, buckets, { false },
        )

        assertEquals(2L, frames)
        assertArrayEquals(intArrayOf(400, 32768), buckets)
    }

    @Test
    fun appendFrames_mergesMultipleBuffersWithoutReplacingLargerPeaks() {
        val buckets = intArrayOf(900, 0)
        val format = PcmFormat(1, 1000, PcmEncoding.PCM_16BIT)

        assertEquals(2L, WaveformPeaks.appendFrames(pcm16(100, -200), format, 0L, 4000L, buckets, { false }))
        assertEquals(2L, WaveformPeaks.appendFrames(pcm16(-500, 300), format, 2000L, 4000L, buckets, { false }))

        assertArrayEquals(intArrayOf(900, 500), buckets)
    }

    @Test
    fun appendFrames_positionsSamplesUsingTheBufferTimestampAndSampleRate() {
        val buckets = IntArray(4)

        val frames = WaveformPeaks.appendFrames(
            pcm16(4, 8), PcmFormat(1, 4, PcmEncoding.PCM_16BIT),
            500_000L, 1_000_000L, buckets, { false },
        )

        assertEquals(2L, frames)
        assertArrayEquals(intArrayOf(0, 0, 4, 8), buckets)
    }

    @Test
    fun appendFrames_clampsBufferTimestampsBeforeAndAfterTheBeat() {
        for ((timestamp, expected) in listOf(-1_000_000L to intArrayOf(8, 0),
            2_000_000L to intArrayOf(0, 8))) {
            val buckets = IntArray(2)

            val frames = WaveformPeaks.appendFrames(
                pcm16(4, 8), PcmFormat(1, 4, PcmEncoding.PCM_16BIT),
                timestamp, 1_000_000L, buckets, { false },
            )

            assertEquals(2L, frames)
            assertArrayEquals(expected, buckets)
        }
    }

    @Test
    fun appendFrames_ignoresIncompleteTrailingFramesInReadOnlyDirectBuffers() {
        val buffer = ByteBuffer.allocateDirect(7).order(ByteOrder.LITTLE_ENDIAN)
            .putShort(100).putShort(-200).putShort(32000).put(0).apply { flip() }
            .asReadOnlyBuffer().order(ByteOrder.LITTLE_ENDIAN)
        val buckets = IntArray(1)

        val frames = WaveformPeaks.appendFrames(
            buffer, PcmFormat(2, 1000, PcmEncoding.PCM_16BIT),
            0L, 1000L, buckets, { false },
        )

        assertEquals(1L, frames)
        assertEquals(3, buffer.remaining())
        assertArrayEquals(intArrayOf(200), buckets)
    }

    @Test
    fun appendFrames_emptyInputLeavesExistingPeaksUntouched() {
        val buckets = intArrayOf(123)

        val frames = WaveformPeaks.appendFrames(
            pcm16(), PcmFormat(1, 1000, PcmEncoding.PCM_16BIT),
            0L, 1000L, buckets, { error("Empty input must not enter the frame loop") },
        )

        assertEquals(0L, frames)
        assertArrayEquals(intArrayOf(123), buckets)
    }

    @Test
    fun appendFrames_nonpositiveChannelsAndSampleRatesRetainTheSingleChannelFallback() {
        val buckets = IntArray(2)

        val frames = WaveformPeaks.appendFrames(
            pcm16(10, 20), PcmFormat(0, 0, PcmEncoding.PCM_16BIT),
            0L, 2_000_000L, buckets, { false },
        )

        assertEquals(2L, frames)
        assertArrayEquals(intArrayOf(10, 20), buckets)
    }

    @Test
    fun appendFrames_overflowingFrameSizeLeavesPeaksAndInputUntouched() {
        val buffer = pcm16(123)
        val buckets = intArrayOf(456)

        val frames = WaveformPeaks.appendFrames(
            buffer, PcmFormat(Int.MAX_VALUE, 1000, PcmEncoding.PCM_16BIT),
            0L, 1000L, buckets, { false },
        )

        assertEquals(0L, frames)
        assertEquals(0, buffer.position())
        assertArrayEquals(intArrayOf(456), buckets)
    }

    @Test
    fun appendFrames_invalidBucketDimensionsProduceNoPeaks() {
        for ((duration, buckets) in listOf(0L to IntArray(2), 1000L to IntArray(0))) {
            val frames = WaveformPeaks.appendFrames(
                pcm16(123), PcmFormat(1, 1000, PcmEncoding.PCM_16BIT),
                0L, duration, buckets, { false },
            )

            assertEquals(0L, frames)
            assertArrayEquals(IntArray(buckets.size), buckets)
        }
    }

    @Test
    fun appendFrames_cancellationBeforeTheFirstFramePreservesExistingPeaks() {
        val buckets = intArrayOf(456)

        assertThrows(CancellationException::class.java) {
            WaveformPeaks.appendFrames(
                pcm16(123), PcmFormat(1, 1000, PcmEncoding.PCM_16BIT),
                0L, 1000L, buckets, { true },
            )
        }

        assertArrayEquals(intArrayOf(456), buckets)
    }

    @Test
    fun appendFrames_cancellationDuringALongBufferStopsBeforeLaterBuckets() {
        val buffer = pcm16(*ShortArray(512) { 7 })
        val buckets = IntArray(2)

        assertThrows(CancellationException::class.java) {
            WaveformPeaks.appendFrames(
                buffer, PcmFormat(1, 1000, PcmEncoding.PCM_16BIT),
                0L, 512_000L, buckets, { buffer.position() >= 512 },
            )
        }

        assertArrayEquals(intArrayOf(7, 0), buckets)
    }

    private fun pcm16(vararg samples: Short): ByteBuffer =
        ByteBuffer.allocate(samples.size * 2).order(ByteOrder.LITTLE_ENDIAN).apply {
            samples.forEach { putShort(it) }
            flip()
        }
}
