package com.prosincerity.ghostwriter.player.audio

import android.media.AudioFormat
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PcmBeatDecoderTest {
    @Test fun unsupportedPcmEncoding_preservesExistingOutput() {
        val file = folder.newFile().apply { writeBytes(byteArrayOf(1, 2)) }
        RandomAccessFile(file, "rw").use { pcm ->
            assertThrows(IllegalArgumentException::class.java) {
                PcmBeatDecoder.appendDecodedBuffer(pcm, ByteBuffer.allocate(4), 0, 4, 0,
                    44100, 1, AudioFormat.ENCODING_INVALID)
            }
        }
        assertArrayEquals(byteArrayOf(1, 2), file.readBytes())
    }

    @get:Rule val folder = TemporaryFolder()

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
