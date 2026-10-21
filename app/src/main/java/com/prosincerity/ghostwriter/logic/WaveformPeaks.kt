package com.prosincerity.ghostwriter.logic

import java.nio.ByteBuffer
import java.util.concurrent.CancellationException
import kotlin.math.abs

/** PCM peak accumulation without Android media types, decoding, or I/O. */
internal object WaveformPeaks {
    fun bucketIndexForTimestamp(
        timestampUs: Long,
        durationUs: Long,
        bucketCount: Int,
    ): Int? {
        if (durationUs <= 0L || bucketCount <= 0) return null

        val clampedTimestampUs = timestampUs.coerceIn(0L, durationUs)
        val bucketIndex = try {
            Math.multiplyExact(clampedTimestampUs, bucketCount.toLong()) / durationUs
        } catch (_: ArithmeticException) {
            ((clampedTimestampUs.toDouble() / durationUs) * bucketCount).toLong()
        }
        return bucketIndex.coerceIn(0L, bucketCount.toLong() - 1L).toInt()
    }

    /** Consume complete interleaved frames from a bounded, little-endian buffer. */
    fun appendFrames(
        buffer: ByteBuffer,
        format: PcmFormat,
        timestampUs: Long,
        durationUs: Long,
        buckets: IntArray,
        shouldCancel: () -> Boolean,
    ): Long {
        val channelCount = format.channelCount.coerceAtLeast(1)
        val sampleRate = format.sampleRate.coerceAtLeast(1)
        val bytesPerFrame = format.encoding.bytesPerSample * channelCount
        if (bytesPerFrame <= 0) return 0L

        var frameIndex = 0L
        while (buffer.remaining() >= bytesPerFrame) {
            if (frameIndex % 256L == 0L) checkWaveformCancelled(shouldCancel)
            var framePeak = 0
            repeat(channelCount) {
                framePeak = maxOf(framePeak, pcmAmplitude(buffer, format.encoding))
            }
            val frameTimeUs = timestampUs + (frameIndex * 1_000_000L / sampleRate)
            val bucketIndex = bucketIndexForTimestamp(frameTimeUs, durationUs, buckets.size)
                ?: return frameIndex
            buckets[bucketIndex] = maxOf(buckets[bucketIndex], framePeak)
            frameIndex++
        }
        return frameIndex
    }

    fun pcmAmplitude(buffer: ByteBuffer, encoding: PcmEncoding): Int = when (encoding) {
        PcmEncoding.PCM_8BIT -> abs((buffer.get().toInt() and 0xFF) - 128) * 256
        PcmEncoding.PCM_FLOAT -> (abs(buffer.float).coerceAtMost(1f) * 32_767f).toInt()
        PcmEncoding.PCM_16BIT -> abs(buffer.short.toInt())
    }
}

internal fun checkWaveformCancelled(shouldCancel: () -> Boolean) {
    if (shouldCancel()) throw CancellationException("Waveform extraction cancelled")
}
