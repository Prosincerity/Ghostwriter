package com.prosincerity.ghostwriter.media

import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.io.File
import java.util.concurrent.Semaphore
import java.util.concurrent.ThreadFactory
import java.util.concurrent.TimeUnit
import org.junit.After
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class PcmRingBufferTest {
    @get:Rule val folder = TemporaryFolder()
    private val sources = mutableListOf<ControlledSource>()

    @After fun closeSources() { sources.forEach { it.close() } }

    private fun audio(frames: Int = 2048, channels: Int = 2) = folder.newFile().apply {
        val buffer = ByteBuffer.allocate(frames * channels * 2).order(ByteOrder.LITTLE_ENDIAN)
        repeat(frames) { frame -> repeat(channels) { buffer.putShort((frame * channels + it).toShort()) } }
        writeBytes(buffer.array())
    }
    private fun copiedSamples(source: PcmSource, frame: Long, count: Int): ShortArray {
        val samples = ShortArray(count * source.channels)
        assertEquals(count, source.copyFrames(frame, samples, 0, count))
        return samples
    }

    @Test fun worker_prefetchesSequentialPagesAndLoopHeadBeforeWrap() {
        val source = buffered(audio(), regionLimitBytes = 0)
        try {
            source.requestFrame(1000)
            source.prepareLoop(120, 1800, true)
            source.prefetch()
            assertEquals((2000 until 2080).toList(), copiedSamples(source, 1000, 40).map { it.toInt() })
            assertEquals((240 until 264).toList(), copiedSamples(source, 120, 12).map { it.toInt() })
            assertEquals((3580 until 3600).toList(), copiedSamples(source, 1790, 10).map { it.toInt() })
            source.requestFrame(124)
            source.prefetch()
            assertEquals((248 until 328).toList(), copiedSamples(source, 124, 40).map { it.toInt() })
            assertNull(source.failure)
        } finally { source.close() }
    }

    @Test fun liveLoopEdit_prefetchesNewHeadAndReportsMissingPagesWithoutWaiting() {
        val source = buffered(audio(), regionLimitBytes = 0)
        try {
            source.prepareLoop(100, 400, true)
            source.prefetch()
            assertArrayEquals(shortArrayOf(200, 201, 202, 203), copiedSamples(source, 100, 2))
            source.prepareLoop(1500, 1800, true)
            // A page miss returns immediately until the controlled worker loads the new head.
            val untouched = ShortArray(16) { -1 }
            assertEquals(0, source.copyFrames(1500, untouched, 0, 8))
            assertArrayEquals(ShortArray(16) { -1 }, untouched)
            source.prefetch()
            assertEquals((3000 until 3016).toList(), copiedSamples(source, 1500, 8).map { it.toInt() })
            assertEquals(0, source.copyFrames(2048, ShortArray(16), 0, 8))
            assertNull(source.failure)
        } finally { source.close() }
    }

    @Test fun cachedLoopRegions_copyAcrossPagesAndFollowLiveEdits() {
        val source = buffered(audio())
        try {
            source.prepareLoop(100, 1800, true)
            source.prefetch()
            assertEquals((200 until 3600).toList(), copiedSamples(source, 100, 1700).map { it.toInt() })
            source.prepareLoop(800, 1600, true)
            source.prefetch()
            assertEquals((1600 until 3200).toList(), copiedSamples(source, 800, 800).map { it.toInt() })
        } finally { source.close() }
    }

    @Test fun close_diskBackedSourceDeletesOnlyItsTemporaryFile() {
        val file = audio()
        val unrelated = audio(frames = 1)
        val source = buffered(file, deleteOnClose = true)
        assertArrayEquals(shortArrayOf(0, 1, 2, 3), copiedSamples(source, 0, 2))
        source.close()
        assertFalse(file.exists())
        assertArrayEquals(byteArrayOf(0, 0, 1, 0), unrelated.readBytes())
    }

    @Test fun playingAndPausedWorker_copyPartialFinalPageAtDestinationOffset() {
        val source = buffered(audio(frames = 35), frames = 35, regionLimitBytes = 0)
        try {
            source.setPlaying(true)
            source.requestFrame(Long.MAX_VALUE)
            source.prefetch()
            assertArrayEquals(shortArrayOf(64, 65, 66, 67, 68, 69), copiedSamples(source, 32, 3))
            val output = ShortArray(12) { -1 }
            assertEquals(3, source.copyFrames(32, output, 1, 5))
            assertArrayEquals(shortArrayOf(-1, -1, 64, 65, 66, 67, 68, 69, -1, -1, -1, -1), output)
            source.setPlaying(false)
            source.requestFrame(-100)
            source.prefetch()
            assertArrayEquals(shortArrayOf(0, 1, 2, 3), copiedSamples(source, 0, 2))
            assertNull(source.failure)
        } finally { source.close() }
    }

    @Test fun disabledAndEmptyLoops_fallBackToSequentialPrefetch() {
        val source = buffered(audio())
        try {
            source.prepareLoop(100, 1800, true)
            source.prefetch()
            assertEquals(1700, source.copyFrames(100, ShortArray(3400), 0, 1700))
            source.requestFrame(1900)
            source.prepareLoop(100, 1800, false)
            source.prefetch()
            assertEquals((3800 until 3816).toList(), copiedSamples(source, 1900, 8).map { it.toInt() })
            source.requestFrame(500)
            source.prepareLoop(100, 100, true)
            source.prefetch()
            assertEquals((1000 until 1016).toList(), copiedSamples(source, 500, 8).map { it.toInt() })
            assertNull(source.failure)
        } finally { source.close() }
    }

    @Test fun truncatedFile_reportsWorkerFailureAndDeletesTemporaryFile() {
        val file = audio(frames = 1)
        val source = buffered(file, deleteOnClose = true, awaitInitialPrefetch = false)
        try {
            source.awaitStopped()
            assertTrue(source.failure is java.io.EOFException)
            assertFalse(file.exists())
            assertEquals(0, source.copyFrames(0, ShortArray(16), 0, 8))
        } finally { source.close() }
    }

    @Test fun missingFile_reportsFailureWithoutBlockingCopyRequests() {
        val missing = java.io.File(folder.root, "missing.pcm")
        val source = buffered(missing, channels = 1, frames = 32, awaitInitialPrefetch = false)
        try {
            source.awaitStopped()
            assertTrue(source.failure is java.io.FileNotFoundException)
            assertEquals(0, source.copyFrames(0, ShortArray(8), 0, 8))
        } finally { source.close() }
    }

    @Test fun invalidDimensions_areRejectedBeforeWorkerStarts() {
        val file = audio()
        for ((channels, frames, pageFrames, pageCount) in listOf(
            listOf(0, 32, 16, 12), listOf(1, 0, 16, 12),
            listOf(1, 32, 0, 12), listOf(1, 32, 16, 11),
        )) {
            assertThrows(IllegalArgumentException::class.java) {
                PcmRingBuffer(file, channels, frames.toLong(), pageFrames, pageCount)
            }
        }
    }

    private fun buffered(
        file: File,
        channels: Int = 2,
        frames: Long = 2048,
        regionLimitBytes: Long = PcmSources.MEMORY_LIMIT_BYTES,
        deleteOnClose: Boolean = false,
        awaitInitialPrefetch: Boolean = true,
    ): ControlledSource {
        val worker = ControlledWorker()
        val source = ControlledSource(PcmRingBuffer(
            file, channels, frames, pageFrames = 16, regionLimitBytes = regionLimitBytes,
            deleteOnClose = deleteOnClose, workerFactory = worker.factory, waitForWork = worker::pause,
        ), worker)
        sources += source
        if (awaitInitialPrefetch) worker.awaitIdle()
        return source
    }

    private class ControlledSource(
        private val source: PcmRingBuffer,
        private val worker: ControlledWorker,
    ) : PcmSource by source {
        val failure get() = source.failure
        fun prefetch() { worker.resume.release(); worker.awaitIdle() }
        fun awaitStopped() = worker.awaitStopped()
        override fun close() {
            source.close()
            worker.resume.release()
            worker.awaitStopped()
        }
    }

    /** Controls worker scheduling; assertions above only inspect PCM data and file outcomes. */
    private class ControlledWorker {
        private val idle = Semaphore(0)
        val resume = Semaphore(0)
        private lateinit var thread: Thread
        val factory = ThreadFactory { task ->
            Thread(task, "Test PCM prefetch").apply { isDaemon = true }.also { thread = it }
        }
        fun pause(@Suppress("UNUSED_PARAMETER") playing: Boolean) {
            idle.release()
            resume.acquire()
        }
        fun awaitIdle() {
            assertTrue("PCM prefetch did not finish", idle.tryAcquire(5, TimeUnit.SECONDS))
        }
        fun awaitStopped() {
            thread.join(5000)
            assertFalse("PCM worker did not stop", thread.isAlive)
        }
    }
}
