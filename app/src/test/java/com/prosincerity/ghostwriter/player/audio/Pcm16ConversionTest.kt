package com.prosincerity.ghostwriter.player.audio

import com.prosincerity.ghostwriter.waveform.domain.PcmEncoding
import com.prosincerity.ghostwriter.waveform.domain.PcmFormat
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class Pcm16ConversionTest {
    @Test
    fun roundedContainerDuration_preservesEveryDecodedFrame() {
        for (rate in listOf(44100, 48000, 96000)) {
            for (frames in listOf(2, 800, 1024)) {
                val durationUs = frames * 1_000_000L / rate

                assertEquals("$frames stereo frames at $rate Hz", frames.toLong(),
                    Pcm16Conversion.trimmedFrames(frames * 4L, rate, 2, durationUs))
            }
        }
    }

    @Test
    fun roundedNegativeTimestamp_skipsAllPrerollFramesAndKeepsChannelOrder() {
        for (preroll in listOf(1, 1024)) {
            val samples = ShortArray((preroll + 2) * 2) { it.toShort() }

            val converted = convertStereo(samples, -preroll * 1_000_000L / 44100)

            assertArrayEquals(samples.takeLast(4).toShortArray(), readSamples(converted))
        }
    }

    @Test
    fun entirelyNegativeBuffer_doesNotLeakPrerollIntoNextBuffer() {
        val negative = convertStereo(shortArrayOf(10, -10), -1_000_000L / 44100)
        val next = convertStereo(shortArrayOf(20, -20, 30, -30), 0)

        assertArrayEquals(byteArrayOf(), negative)
        assertArrayEquals(shortArrayOf(20, -20, 30, -30), readSamples(next))
    }

    @Test
    fun pcm8AndFloatBuffers_convertToLittleEndianSignedSamples() {
        val unsigned = byteArrayOf(0, 64, 128.toByte(), 192.toByte(), 255.toByte())
        val converted8 = Pcm16Conversion.convertBuffer(ByteBuffer.wrap(unsigned), 0, unsigned.size, 0,
            PcmFormat(1, 44100, PcmEncoding.PCM_8BIT))
        val floating = ByteBuffer.allocate(7 * 4).order(ByteOrder.LITTLE_ENDIAN)
        floatArrayOf(-2f, -1f, -0.5f, 0f, 0.5f, 1f, 2f).forEach { floating.putFloat(it) }
        val convertedFloat = Pcm16Conversion.convertBuffer(floating, 0, floating.capacity(), 0,
            PcmFormat(1, 44100, PcmEncoding.PCM_FLOAT))

        assertArrayEquals(shortArrayOf(-32768, -16384, 0, 16384, 32512), readSamples(converted8))
        assertArrayEquals(shortArrayOf(-32767, -32767, -16383, 0, 16383, 32767, 32767),
            readSamples(convertedFloat))
    }

    @Test
    fun pcm16DirectReadOnlyBuffer_preservesBytesAndIgnoresIncompleteTrailingSample() {
        val samples = shortArrayOf(Short.MIN_VALUE, -12345, -1, 0, 1, 12345, Short.MAX_VALUE)
        val buffer = ByteBuffer.allocateDirect(samples.size * 2 + 4).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putShort(999)
        samples.forEach { buffer.putShort(it) }
        buffer.putShort(999)
        val input = buffer.asReadOnlyBuffer()
        input.position(1)
        val originalLimit = input.limit()
        val originalOrder = input.order()

        val converted = Pcm16Conversion.convertBuffer(input, 2, samples.size * 2 + 1, 0,
            PcmFormat(1, 44100, PcmEncoding.PCM_16BIT))

        assertArrayEquals(samples, readSamples(converted))
        assertEquals(1, input.position())
        assertEquals(originalLimit, input.limit())
        assertEquals(originalOrder, input.order())
    }

    @Test
    fun emptyBuffersProduceNoSamplesForAnySupportedEncoding() {
        for (encoding in PcmEncoding.entries) {
            val converted = Pcm16Conversion.convertBuffer(ByteBuffer.allocate(0), 0, 0, 0,
                PcmFormat(1, 44100, encoding))

            assertArrayEquals(byteArrayOf(), converted)
        }
    }

    @Test
    fun incompleteFloatSampleIsExcludedFromTheOutput() {
        val buffer = ByteBuffer.allocate(7).order(ByteOrder.LITTLE_ENDIAN)
            .putFloat(0.5f).put(byteArrayOf(1, 2, 3))

        val converted = Pcm16Conversion.convertBuffer(buffer, 0, buffer.capacity(), 0,
            PcmFormat(1, 44100, PcmEncoding.PCM_FLOAT))

        assertArrayEquals(shortArrayOf(16383), readSamples(converted))
    }

    @Test
    fun nonfiniteFloatSamplesRetainSaturationAndSilentNanBehavior() {
        val buffer = ByteBuffer.allocate(12).order(ByteOrder.LITTLE_ENDIAN)
            .putFloat(Float.NEGATIVE_INFINITY).putFloat(Float.NaN).putFloat(Float.POSITIVE_INFINITY)

        val converted = Pcm16Conversion.convertBuffer(buffer, 0, buffer.capacity(), 0,
            PcmFormat(1, 44100, PcmEncoding.PCM_FLOAT))

        assertArrayEquals(shortArrayOf(-32767, 0, 32767), readSamples(converted))
    }

    @Test
    fun invalidBufferRangesDoNotChangeTheSourceBuffer() {
        val buffer = ByteBuffer.allocate(8).apply { position(1) }
        for ((offset, size) in listOf(-1 to 2, 9 to 0, 0 to 9, 2 to -3, 4 to -2)) {
            assertThrows(IllegalArgumentException::class.java) {
                Pcm16Conversion.convertBuffer(buffer, offset, size, 0,
                    PcmFormat(1, 44100, PcmEncoding.PCM_16BIT))
            }
            assertEquals(1, buffer.position())
            assertEquals(8, buffer.limit())
        }
    }

    @Test
    fun durationTrimmingCountsOnlyCompleteFramesWithoutExtendingShortAudio() {
        assertEquals(0L, Pcm16Conversion.trimmedFrames(0, 44100, 2, 1_000_000))
        assertEquals(0L, Pcm16Conversion.trimmedFrames(3, 44100, 2, 1_000_000))
        assertEquals(2L, Pcm16Conversion.trimmedFrames(11, 44100, 2, 1_000_000))
        assertEquals(2L, Pcm16Conversion.trimmedFrames(11, 44100, 2, 0))
        assertEquals(2L, Pcm16Conversion.trimmedFrames(11, 44100, 2, -1))
    }

    private fun convertStereo(samples: ShortArray, timestamp: Long): ByteArray {
        val buffer = ByteBuffer.allocate(samples.size * 2 + 4).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putShort(999)
        samples.forEach { buffer.putShort(it) }
        buffer.putShort(999)
        return Pcm16Conversion.convertBuffer(buffer, 2, samples.size * 2, timestamp,
            PcmFormat(2, 44100, PcmEncoding.PCM_16BIT))
    }

    private fun readSamples(bytes: ByteArray): ShortArray {
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        return ShortArray(bytes.size / 2) { buffer.short }
    }
}
