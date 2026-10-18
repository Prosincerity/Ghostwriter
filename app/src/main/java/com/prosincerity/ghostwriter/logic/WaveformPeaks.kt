package com.prosincerity.ghostwriter.logic

import java.nio.ByteBuffer
import java.util.concurrent.CancellationException
import kotlin.math.abs

internal enum class WaveformPcmEncoding(val bytesPerSample: Int) {
    PCM_8BIT(1), PCM_16BIT(2), PCM_FLOAT(4),
}

internal data class WaveformPcmFormat(
    val channelCount: Int,
    val sampleRate: Int,
    val encoding: WaveformPcmEncoding,
)

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
        format: WaveformPcmFormat,
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

    fun pcmAmplitude(buffer: ByteBuffer, encoding: WaveformPcmEncoding): Int = when (encoding) {
        WaveformPcmEncoding.PCM_8BIT -> abs((buffer.get().toInt() and 0xFF) - 128) * 256
        WaveformPcmEncoding.PCM_FLOAT -> (abs(buffer.float).coerceAtMost(1f) * 32_767f).toInt()
        WaveformPcmEncoding.PCM_16BIT -> abs(buffer.short.toInt())
    }
}

internal fun checkWaveformCancelled(shouldCancel: () -> Boolean) {
    if (shouldCancel()) throw CancellationException("Waveform extraction cancelled")
}
