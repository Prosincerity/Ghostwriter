package com.prosincerity.ghostwriter.media

import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.ThreadFactory
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicLong
import java.util.concurrent.atomic.AtomicReference
import java.util.concurrent.locks.LockSupport

/**
 * Bounded circular PCM page buffer. Only the worker seeks/reads the file. The
 * consumer claims ready slots with one CAS; it never waits for the producer.
 * Loop head/tail pages stay prefetched alongside the sequential read-ahead.
 */
internal class PcmRingBuffer(
    private val file: File,
    override val channels: Int,
    override val frames: Long,
    private val pageFrames: Int = 2048,
    private val pageCount: Int = 16,
    private val deleteOnClose: Boolean = false,
    private val regionLimitBytes: Long = PcmSources.MEMORY_LIMIT_BYTES,
    workerFactory: ThreadFactory = ThreadFactory { task ->
        Thread(task, "Beat PCM prefetch").apply { isDaemon = true }
    },
    private val waitForWork: (Boolean) -> Unit = { playing ->
        LockSupport.parkNanos(if (playing) 5_000_000 else 250_000_000)
    },
) : PcmSource {
    private class Page(samples: Int) {
        val data = ShortArray(samples)
        val state = AtomicInteger(EMPTY)
        @Volatile var first = -1L
        @Volatile var count = 0
    }
    private data class Loop(val start: Long, val end: Long, val enabled: Boolean)
    private data class Region(val start: Long, val end: Long, val samples: ShortArray)
    private val pages = Array(pageCount) { Page(pageFrames * channels) }
    private val requested = AtomicLong(0)
    private val loop = AtomicReference(Loop(0, frames, false))
    private val region = AtomicReference<Region?>(null)
    private val closed = AtomicBoolean(false)
    private val playing = AtomicBoolean(false)
    @Volatile var failure: Exception? = null
        private set
    private val worker: Thread

    init {
        require(channels > 0 && frames > 0 && pageFrames > 0 && pageCount >= 12)
        worker = workerFactory.newThread(::prefetch).apply { start() }
    }

    override fun requestFrame(frame: Long) { requested.set(frame.coerceIn(0, frames - 1)) }

    override fun prepareLoop(start: Long, end: Long, enabled: Boolean) {
        loop.set(Loop(start, end, enabled)) // Allocated by the UI/control thread, never render().
        LockSupport.unpark(worker)
    }

    override fun setPlaying(playing: Boolean) {
        this.playing.set(playing)
        LockSupport.unpark(worker)
    }

    override fun copyFrames(frame: Long, destination: ShortArray, destinationFrame: Int, count: Int): Int {
        var copied = 0
        while (copied < count && frame + copied < frames) {
            val position = frame + copied
            val cached = region.get()
            if (cached != null && position >= cached.start && position < cached.end) {
                val amount = minOf((count - copied).toLong(), cached.end - position).toInt()
                System.arraycopy(cached.samples, ((position - cached.start) * channels).toInt(), destination,
                    (destinationFrame + copied) * channels, amount * channels)
                copied += amount
                continue
            }
            var found = false
            for (page in pages) {
                if (position < page.first || position >= page.first + page.count) continue
                if (!page.state.compareAndSet(READY, READING)) continue
                try {
                    if (position >= page.first && position < page.first + page.count) {
                        val offset = (position - page.first).toInt()
                        val amount = minOf(count - copied, page.count - offset)
                        System.arraycopy(page.data, offset * channels, destination,
                            (destinationFrame + copied) * channels, amount * channels)
                        copied += amount
                        found = true
                    }
                } finally {
                    page.state.set(READY)
                }
                if (found) break
            }
            if (!found) break
        }
        return copied
    }

    override fun close() { closed.set(true); LockSupport.unpark(worker) }

    private fun prefetch() {
        try {
            val bytes = ByteArray(pageFrames * channels * 2)
            val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
            val wanted = LongArray(12)
            var nextSlot = 0
            var cachedLoop: Loop? = null
            RandomAccessFile(file, "r").use { input ->
                while (!closed.get()) {
                    val current = requested.get() / pageFrames
                    val bounds = loop.get()
                    if (bounds !== cachedLoop) {
                        cachedLoop = bounds
                        val length = bounds.end - bounds.start
                        if (bounds.enabled && length > 0 && length <= regionLimitBytes / (channels * 2)) {
                            val samples = ShortArray((length * channels).toInt())
                            input.seek(bounds.start * channels * 2)
                            var offset = 0
                            while (offset < samples.size && !closed.get()) {
                                val count = minOf(bytes.size / 2, samples.size - offset)
                                input.readFully(bytes, 0, count * 2)
                                buffer.clear()
                                repeat(count) { samples[offset++] = buffer.short }
                            }
                            if (!closed.get()) region.set(Region(bounds.start, bounds.end, samples))
                        } else region.set(null)
                    }
                    var wantedCount = 0
                    fun want(page: Long) {
                        if (page < 0 || page * pageFrames >= frames) return
                        for (i in 0 until wantedCount) if (wanted[i] == page) return
                        wanted[wantedCount++] = page
                    }
                    want(current)
                    // The join is resident before reaching the end, including a page-straddling head.
                    if (bounds.enabled) {
                        want(bounds.start / pageFrames)
                        want(bounds.start / pageFrames + 1)
                        want((bounds.end - 1) / pageFrames)
                        want((bounds.end - 1) / pageFrames - 1)
                    }
                    for (i in 1..7) want(current + i)
                    for (i in 0 until wantedCount) {
                        if (closed.get()) break
                        val first = wanted[i] * pageFrames
                        if (pages.any { it.first == first && (it.state.get() == READY || it.state.get() == READING) }) continue
                        var selected: Page? = null
                        repeat(pageCount) {
                            if (selected != null) return@repeat
                            val page = pages[nextSlot]
                            nextSlot = (nextSlot + 1) % pageCount
                            var protected = false
                            for (j in 0 until wantedCount) if (page.first == wanted[j] * pageFrames) protected = true
                            if (!protected) {
                                val state = page.state.get()
                                if ((state == READY || state == EMPTY) && page.state.compareAndSet(state, WRITING)) selected = page
                            }
                        }
                        val page = selected ?: continue
                        val count = minOf(pageFrames.toLong(), frames - first).toInt()
                        input.seek(first * channels * 2)
                        input.readFully(bytes, 0, count * channels * 2)
                        buffer.clear()
                        repeat(count * channels) { page.data[it] = buffer.short }
                        page.first = first
                        page.count = count
                        page.state.set(READY)
                    }
                    waitForWork(playing.get())
                }
            }
        } catch (problem: Exception) {
            if (!closed.get()) failure = problem
        } finally {
            if (deleteOnClose) file.delete()
        }
    }

    private companion object {
        const val EMPTY = 0
        const val READY = 1
        const val READING = 2
        const val WRITING = 3
    }
}
