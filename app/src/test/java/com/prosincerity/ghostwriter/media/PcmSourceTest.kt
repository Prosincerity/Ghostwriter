package com.prosincerity.ghostwriter.media

import java.io.EOFException
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CancellationException
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PcmSourceTest {
    @get:Rule val folder = TemporaryFolder()

    @Test fun memorySource_copiesInterleavedChannelsAtDestinationFrameOffset() {
        val source = MemoryPcmSource(shortArrayOf(1, -1, 2, -2, 3, -3), 2)
        val output = ShortArray(10) { 99 }
        assertEquals(3L, source.frames)
        assertEquals(2, source.copyFrames(1, output, 2, 3))
        assertArrayEquals(shortArrayOf(99, 99, 99, 99, 2, -2, 3, -3, 99, 99), output)
    }

    @Test fun memorySource_outOfBoundsAndEmptyRequestsLeaveDestinationUntouched() {
        val source = MemoryPcmSource(shortArrayOf(1, 2), 1)
        val output = shortArrayOf(99, 99)
        for (frame in listOf(-1L, 2L, Long.MAX_VALUE)) {
            assertEquals(0, source.copyFrames(frame, output, 0, 2))
        }
        assertEquals(0, source.copyFrames(0, output, 0, 0))
        assertArrayEquals(shortArrayOf(99, 99), output)
    }

    @Test fun preparation_readsMultipleBuffersInLittleEndianOrderAndRemovesFile() {
        val samples = ShortArray(40000) { (it - 20000).toShort() }
        val file = audio(samples)
        val source = PcmSources.prepare(PcmBeat(file, 48000, 2, 20000)) { false }
        val output = ShortArray(samples.size)
        assertEquals(20000, source.copyFrames(0, output, 0, 20000))
        assertArrayEquals(samples, output)
        assertFalse(file.exists())
    }

    @Test fun cancellationAfterFirstBuffer_deletesPartiallyLoadedPcm() {
        val file = audio(ShortArray(40000))
        var checks = 0
        assertThrows(CancellationException::class.java) {
            PcmSources.prepare(PcmBeat(file, 48000, 1, 40000)) { ++checks == 2 }
        }
        assertEquals(2, checks)
        assertFalse(file.exists())
    }

    @Test fun truncatedPcm_failsPreparationAndDeletesTemporaryFile() {
        val file = audio(shortArrayOf(1, 2))
        assertThrows(EOFException::class.java) {
            PcmSources.prepare(PcmBeat(file, 48000, 2, 10)) { false }
        }
        assertFalse(file.exists())
    }

    private fun audio(samples: ShortArray) = folder.newFile().apply {
        val bytes = ByteBuffer.allocate(samples.size * 2).order(ByteOrder.LITTLE_ENDIAN)
        samples.forEach { bytes.putShort(it) }
        writeBytes(bytes.array())
    }
}
