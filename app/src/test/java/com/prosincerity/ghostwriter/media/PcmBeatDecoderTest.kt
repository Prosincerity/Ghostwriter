package com.prosincerity.ghostwriter.media

import android.media.AudioFormat
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PcmBeatDecoderTest {
    @get:Rule val folder = TemporaryFolder()

    @Test fun roundedContainerDuration_preservesEveryDecodedFrame() {
        for (rate in listOf(44100, 48000, 96000)) {
            for (frames in listOf(2, 800, 1024)) {
                val file = folder.newFile()
                RandomAccessFile(file, "rw").use { pcm ->
                    pcm.setLength(frames * 4L)
                    val durationUs = frames * 1_000_000L / rate
                    assertEquals("$frames stereo frames at $rate Hz", frames.toLong(),
                        PcmBeatDecoder.trimToDuration(pcm, rate, 2, durationUs))
                    assertEquals(frames * 4L, pcm.length())
                }
            }
        }
    }

    @Test fun roundedNegativeTimestamp_skipsAllPrerollFramesAndKeepsChannelOrder() {
        for (preroll in listOf(1, 1024)) {
            val samples = ShortArray((preroll + 2) * 2) { it.toShort() }
            val file = folder.newFile()
            RandomAccessFile(file, "rw").use { pcm ->
                append(pcm, samples, -preroll * 1_000_000L / 44100)
            }
            assertArrayEquals(samples.takeLast(4).toShortArray(), readSamples(file.readBytes()))
        }
    }

    @Test fun entirelyNegativeBuffer_doesNotLeakPrerollIntoNextBuffer() {
        val file = folder.newFile()
        RandomAccessFile(file, "rw").use { pcm ->
            append(pcm, shortArrayOf(10, -10), -1_000_000L / 44100)
            append(pcm, shortArrayOf(20, -20, 30, -30), 0)
        }
        assertArrayEquals(shortArrayOf(20, -20, 30, -30), readSamples(file.readBytes()))
    }

    @Test fun durationTrimming_removesPaddingWithoutExtendingShortOrUnknownDurationAudio() {
        val file = folder.newFile()
        RandomAccessFile(file, "rw").use { pcm ->
            pcm.setLength(1100 * 4L)
            assertEquals(1024L, PcmBeatDecoder.trimToDuration(pcm, 44100, 2, 1024 * 1_000_000L / 44100))
            assertEquals(4096L, pcm.length())
            assertEquals(1024L, PcmBeatDecoder.trimToDuration(pcm, 44100, 2, 1_000_000))
            assertEquals(1024L, PcmBeatDecoder.trimToDuration(pcm, 44100, 2, 0))
            assertEquals(1024L, PcmBeatDecoder.trimToDuration(pcm, 44100, 2, -1))
        }
    }

    @Test fun outputBuffers_appendDespiteTimestampRoundingAndRespectOffsetAndSize() {
        val file = folder.newFile()
        RandomAccessFile(file, "rw").use { pcm ->
            append(pcm, shortArrayOf(1, -1, 2, -2), 0)
            append(pcm, shortArrayOf(3, -3), 2 * 1_000_000L / 44100)
        }
        assertArrayEquals(shortArrayOf(1, -1, 2, -2, 3, -3), readSamples(file.readBytes()))
    }

    @Test fun pcm8AndFloatBuffers_convertToLittleEndianSignedSamples() {
        val file = folder.newFile()
        RandomAccessFile(file, "rw").use { pcm ->
            val unsigned = byteArrayOf(0, 64, 128.toByte(), 192.toByte(), 255.toByte())
            PcmBeatDecoder.appendDecodedBuffer(pcm, ByteBuffer.wrap(unsigned), 0, unsigned.size, 0,
                44100, 1, AudioFormat.ENCODING_PCM_8BIT)
            val floating = ByteBuffer.allocate(7 * 4).order(ByteOrder.LITTLE_ENDIAN)
            floatArrayOf(-2f, -1f, -0.5f, 0f, 0.5f, 1f, 2f).forEach { floating.putFloat(it) }
            PcmBeatDecoder.appendDecodedBuffer(pcm, floating, 0, floating.capacity(), 0,
                44100, 1, AudioFormat.ENCODING_PCM_FLOAT)
        }
        assertArrayEquals(shortArrayOf(-32768, -16384, 0, 16384, 32512,
            -32767, -32767, -16383, 0, 16383, 32767, 32767), readSamples(file.readBytes()))
    }

    @Test fun pcm16DirectReadOnlyBuffer_preservesBytesAndIgnoresIncompleteTrailingSample() {
        val samples = shortArrayOf(Short.MIN_VALUE, -12345, -1, 0, 1, 12345, Short.MAX_VALUE)
        val buffer = ByteBuffer.allocateDirect(samples.size * 2 + 4).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putShort(999)
        samples.forEach { buffer.putShort(it) }
        buffer.putShort(999)
        val input = buffer.asReadOnlyBuffer()
        input.position(1)
        val originalLimit = input.limit()
        val file = folder.newFile()
        RandomAccessFile(file, "rw").use { pcm ->
            PcmBeatDecoder.appendDecodedBuffer(pcm, input, 2, samples.size * 2 + 1, 0,
                44100, 1, AudioFormat.ENCODING_PCM_16BIT)
        }
        assertArrayEquals(samples, readSamples(file.readBytes()))
        assertEquals(1, input.position())
        assertEquals(originalLimit, input.limit())
    }

    private fun append(pcm: RandomAccessFile, samples: ShortArray, timestamp: Long) {
        val buffer = ByteBuffer.allocate(samples.size * 2 + 4).order(ByteOrder.LITTLE_ENDIAN)
        buffer.putShort(999)
        samples.forEach { buffer.putShort(it) }
        buffer.putShort(999)
        PcmBeatDecoder.appendDecodedBuffer(pcm, buffer, 2, samples.size * 2, timestamp,
            44100, 2, AudioFormat.ENCODING_PCM_16BIT)
    }

    private fun readSamples(bytes: ByteArray): ShortArray {
        val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
        return ShortArray(bytes.size / 2) { buffer.short }
    }
}
