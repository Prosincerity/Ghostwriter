package com.prosincerity.ghostwriter.media

import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CancellationException

/** All audio-thread calls are nonblocking and allocation-free. A miss returns a short copy. */
internal interface PcmSource {
    val channels: Int
    val frames: Long
    fun copyFrames(frame: Long, destination: ShortArray, destinationFrame: Int, count: Int): Int
    fun requestFrame(frame: Long) {}
    fun prepareLoop(start: Long, end: Long, enabled: Boolean) {}
    fun setPlaying(playing: Boolean) {}
    fun close() {}
}

internal class MemoryPcmSource(
    private val samples: ShortArray,
    override val channels: Int,
) : PcmSource {
    override val frames: Long = samples.size.toLong() / channels

    override fun copyFrames(frame: Long, destination: ShortArray, destinationFrame: Int, count: Int): Int {
        if (frame < 0 || frame >= frames) return 0
        val copied = minOf(count.toLong(), frames - frame).toInt()
        System.arraycopy(samples, (frame * channels).toInt(), destination, destinationFrame * channels, copied * channels)
        return copied
    }
}

internal object PcmSources {
    const val MEMORY_LIMIT_BYTES = 8L * 1024 * 1024

    /** Run during background preparation, before the audio render thread exists. */
    fun prepare(pcm: PcmBeat, cancelled: () -> Boolean): PcmSource {
        if (pcm.frames * pcm.bytesPerFrame > MEMORY_LIMIT_BYTES) {
            try {
                if (cancelled()) throw CancellationException()
                return PcmRingBuffer(pcm.file, pcm.channels, pcm.frames, deleteOnClose = true)
            } catch (problem: Exception) {
                pcm.file.delete()
                throw problem
            }
        }
        try {
            val samples = ShortArray((pcm.frames * pcm.channels).toInt())
            val bytes = ByteArray(64 * 1024)
            val buffer = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN)
            var offset = 0
            RandomAccessFile(pcm.file, "r").use { file ->
                while (offset < samples.size) {
                    if (cancelled()) throw CancellationException()
                    val count = minOf(bytes.size / 2, samples.size - offset)
                    file.readFully(bytes, 0, count * 2)
                    buffer.clear()
                    repeat(count) { samples[offset++] = buffer.short }
                }
            }
            return MemoryPcmSource(samples, pcm.channels)
        } finally {
            pcm.file.delete()
        }
    }
}
