package com.prosincerity.ghostwriter.media

import java.util.concurrent.atomic.AtomicReference

/** One atomic snapshot keeps both boundaries coherent during live edits. */
internal data class PcmLoopBounds(val start: Long, val end: Long, val enabled: Boolean, val crossfade: Int) {
    companion object {
        fun create(start: Long, end: Long, enabled: Boolean, sampleRate: Int): PcmLoopBounds {
            require(start >= 0 && end > start)
            return PcmLoopBounds(start, end, enabled, minOf(sampleRate * 3L / 1000, (end - start - 1) / 2).toInt())
        }
    }
}

internal fun interface FramePositionEvents {
    fun record(outputFrame: Long, beatFrame: Long)
}

/** Primitive arithmetic, atomic loads/CAS and array copies only: no allocation, lock, I/O or decode. */
internal class PcmLoopRenderer(
    private val source: PcmSource,
    private val bounds: AtomicReference<PcmLoopBounds>,
    chunkFrames: Int,
    private val events: FramePositionEvents,
) {
    private val head = ShortArray(chunkFrames * source.channels)
    var position = 0L
        private set
    var outputFrames = 0L
        private set
    var loops = 0L
        private set
    private var active = bounds.get()

    fun seek(frame: Long) {
        active = bounds.get()
        position = frame.coerceIn(0, source.frames)
        if (active.enabled && position >= active.end) position = active.start
        outputFrames = 0
        loops = 0
        source.requestFrame(position)
    }

    fun render(destination: ShortArray): Int {
        val updated = bounds.get()
        if (updated !== active) {
            active = updated
            if (active.enabled && position >= active.end) position = active.start
        }
        val channels = source.channels
        val capacity = destination.size / channels
        var written = 0
        events.record(outputFrames, position)
        source.requestFrame(position)
        while (written < capacity) {
            val end = if (active.enabled) active.end else source.frames
            if (position >= end) {
                if (!active.enabled) break
                position = active.start + active.crossfade
                loops++
                events.record(outputFrames + written, position)
                source.requestFrame(position)
            }
            val fadeStart = active.end - active.crossfade
            val mixing = active.enabled && active.crossfade > 0 && position >= fadeStart
            val limit = if (mixing) active.end else if (active.enabled && active.crossfade > 0) fadeStart else end
            val count = minOf((capacity - written).toLong(), limit - position, (head.size / channels).toLong()).toInt()
            if (count <= 0) break
            var copied = source.copyFrames(position, destination, written, count)
            if (mixing && copied > 0) {
                val offset = position - fadeStart
                copied = minOf(copied, source.copyFrames(active.start + offset, head, 0, copied))
                for (frame in 0 until copied) {
                    val weight = if (active.crossfade <= 1) 1f else (offset + frame).toFloat() / (active.crossfade - 1)
                    for (channel in 0 until channels) {
                        val target = (written + frame) * channels + channel
                        destination[target] = (destination[target] * (1f - weight) + head[frame * channels + channel] * weight)
                            .toInt().coerceIn(-32768, 32767).toShort()
                    }
                }
            }
            if (copied == 0) break // Prefetch miss: the feeder retries; render never waits.
            position += copied
            written += copied
        }
        outputFrames += written
        return written
    }
}
