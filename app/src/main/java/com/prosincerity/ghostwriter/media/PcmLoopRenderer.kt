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
    private var finishInitialTail = false
    private var tailJoinsLoop = false

    fun seek(frame: Long, continueFromCurrentPosition: Boolean = false) {
        active = bounds.get()
        position = frame.coerceIn(0, source.frames)
        // A manual seek outside the loop plays the physical tail, then the intro.
        // Decoder handoff keeps its existing join directly into the marker loop.
        finishInitialTail = active.enabled && position >= active.end && position < source.frames
        tailJoinsLoop = continueFromCurrentPosition
        if (active.enabled && position >= active.end && !finishInitialTail) position = active.start
        outputFrames = 0
        loops = 0
        source.requestFrame(position)
    }

    fun render(destination: ShortArray): Int {
        val updated = bounds.get()
        if (updated !== active) {
            active = updated
            finishInitialTail = false
            if (active.enabled && position >= active.end) position = active.start
        }
        val channels = source.channels
        val capacity = destination.size / channels
        var written = 0
        events.record(outputFrames, position)
        source.requestFrame(position)
        while (written < capacity) {
            val end = if (active.enabled && !finishInitialTail) active.end else source.frames
            if (position >= end) {
                if (!active.enabled) break
                position = if (finishInitialTail && !tailJoinsLoop) 0 else active.start + active.crossfade
                finishInitialTail = false
                loops++
                events.record(outputFrames + written, position)
                source.requestFrame(position)
            }
            val playbackEnd = if (active.enabled && !finishInitialTail) active.end else source.frames
            val crossfade = if (finishInitialTail && !tailJoinsLoop) 0 else active.crossfade
            val fadeStart = playbackEnd - crossfade
            val mixing = active.enabled && crossfade > 0 && position >= fadeStart
            val limit = if (mixing) playbackEnd else if (active.enabled && crossfade > 0) fadeStart else playbackEnd
            val count = minOf((capacity - written).toLong(), limit - position, (head.size / channels).toLong()).toInt()
            if (count <= 0) break
            var copied = source.copyFrames(position, destination, written, count)
            if (mixing && copied > 0) {
                val offset = position - fadeStart
                copied = minOf(copied, source.copyFrames(active.start + offset, head, 0, copied))
                for (frame in 0 until copied) {
                    val weight = if (crossfade <= 1) 1f else (offset + frame).toFloat() / (crossfade - 1)
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
