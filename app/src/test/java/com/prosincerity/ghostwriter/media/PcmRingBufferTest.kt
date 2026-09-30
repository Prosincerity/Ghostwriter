package com.prosincerity.ghostwriter.media

import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PcmRingBufferTest {
    @get:Rule val folder = TemporaryFolder()
    private fun audio(frames: Int = 2048, channels: Int = 2) = folder.newFile().apply {
        val buffer = ByteBuffer.allocate(frames * channels * 2).order(ByteOrder.LITTLE_ENDIAN)
        repeat(frames) { frame -> repeat(channels) { buffer.putShort((frame * channels + it).toShort()) } }
        writeBytes(buffer.array())
    }
    private fun awaitCopy(source: PcmSource, frame: Long, count: Int): ShortArray {
        val samples = ShortArray(count * source.channels)
        val deadline = System.nanoTime() + 2_000_000_000
        while (source.copyFrames(frame, samples, 0, count) != count && System.nanoTime() < deadline) Thread.yield()
        assertEquals(count, source.copyFrames(frame, samples, 0, count))
        return samples
    }

    @Test fun worker_prefetchesSequentialPagesAndLoopHeadBeforeWrap() {
        val source = PcmRingBuffer(audio(), 2, 2048, pageFrames = 16, regionLimitBytes = 0)
        try {
            source.requestFrame(1000)
            source.prepareLoop(120, 1800, true)
            assertEquals((2000 until 2080).toList(), awaitCopy(source, 1000, 40).map { it.toInt() })
            assertEquals((240 until 264).toList(), awaitCopy(source, 120, 12).map { it.toInt() })
            assertEquals((3580 until 3600).toList(), awaitCopy(source, 1790, 10).map { it.toInt() })
            source.requestFrame(124)
            assertEquals((248 until 328).toList(), awaitCopy(source, 124, 40).map { it.toInt() })
            assertNull(source.failure)
        } finally { source.close() }
    }

    @Test fun liveLoopEdit_prefetchesNewHeadAndReportsMissingPagesWithoutWaiting() {
        val source = PcmRingBuffer(audio(), 2, 2048, pageFrames = 16, regionLimitBytes = 0)
        try {
            source.prepareLoop(100, 400, true)
            awaitCopy(source, 100, 8)
            source.prepareLoop(1500, 1800, true)
            assertEquals((3000 until 3016).toList(), awaitCopy(source, 1500, 8).map { it.toInt() })
            assertEquals(0, source.copyFrames(2048, ShortArray(16), 0, 8))
            assertNull(source.failure)
        } finally { source.close() }
    }

    @Test fun smallRegionInLongBeat_isRetainedAsAnImmutableSampleArray() {
        val source = PcmRingBuffer(audio(), 2, 2048, pageFrames = 16)
        try {
            source.prepareLoop(100, 1800, true)
            assertEquals((200 until 3600).toList(), awaitCopy(source, 100, 1700).map { it.toInt() })
            source.prepareLoop(800, 1600, true)
            assertEquals((1600 until 3200).toList(), awaitCopy(source, 800, 800).map { it.toInt() })
        } finally { source.close() }
    }

    @Test fun prepare_shortBeatUsesMemoryAndRemovesTemporaryPcmFile() {
        val file = audio(frames = 32)
        val source = PcmSources.prepare(PcmBeat(file, 48000, 2, 32)) { false }
        assertTrue(source is MemoryPcmSource)
        assertFalse(file.exists())
        assertEquals((0 until 64).toList(), awaitCopy(source, 0, 32).map { it.toInt() })
    }

    @Test fun close_diskBackedSourceDeletesOnlyItsTemporaryFile() {
        val file = audio()
        val source = PcmRingBuffer(file, 2, 2048, deleteOnClose = true)
        awaitCopy(source, 0, 8)
        source.close()
        val deadline = System.nanoTime() + 2_000_000_000
        while (file.exists() && System.nanoTime() < deadline) Thread.yield()
        assertFalse(file.exists())
    }

    @Test fun prepare_longBeatUsesBoundedRingInsteadOfLoadingWholeFile() {
        val file = folder.newFile()
        val length = PcmSources.MEMORY_LIMIT_BYTES + 4
        java.io.RandomAccessFile(file, "rw").use { it.setLength(length) }
        val source = PcmSources.prepare(PcmBeat(file, 48000, 2, length / 4)) { false }
        assertTrue(source is PcmRingBuffer)
        try {
            assertTrue(awaitCopy(source, 0, 16).all { it == 0.toShort() })
        } finally { source.close() }
    }

    @Test fun cancelledMemoryPreparation_removesPartialCache() {
        val file = audio()
        try {
            PcmSources.prepare(PcmBeat(file, 48000, 2, 2048)) { true }
            fail("Expected cancellation")
        } catch (_: java.util.concurrent.CancellationException) {
            assertFalse(file.exists())
        }
    }
}
