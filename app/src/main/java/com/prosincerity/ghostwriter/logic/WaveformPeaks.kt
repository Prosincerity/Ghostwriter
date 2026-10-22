package com.prosincerity.ghostwriter.logic

import java.nio.ByteBuffer
import java.util.concurrent.CancellationException
import kotlin.math.abs

/** PCM peak accumulation without Android media types, decoding, or I/O. */
internal object WaveformPeaks {
    /** Reuse one PCM scratch array across all decoder output buffers. */
    class Scratch {
        private var pcm16 = ShortArray(0)

        fun readPcm16(buffer: ByteBuffer, sampleCount: Int): ShortArray {
            if (pcm16.size < sampleCount) pcm16 = ShortArray(sampleCount)
            buffer.asShortBuffer().get(pcm16, 0, sampleCount)
            return pcm16
        }
    }

    /** Original packet clock when the codec combines PCM into larger buffers. */
    data class PacketTiming(
        val anchorUs: Long,
        val initialFrames: Int,
        val packetFrames: Int,
        val decodedFrames: Long,
    ) {
        init {
            require(initialFrames > 0 && packetFrames > 0 && decodedFrames >= initialFrames)
        }
    }

    fun bucketIndexForTimestamp(
        timestampUs: Long,
        durationUs: Long,
        bucketCount: Int,
    ): Int? {
        if (durationUs <= 0L || bucketCount <= 0) return null
        return bucketIndex(timestampUs, durationUs, bucketCount)
    }

    private fun bucketIndex(timestampUs: Long, durationUs: Long, bucketCount: Int): Int {
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
        scratch: Scratch = Scratch(),
        packetTiming: PacketTiming? = null,
    ): Long {
        val channelCount = format.channelCount.coerceAtLeast(1)
        val sampleRate = format.sampleRate.coerceAtLeast(1)
        val frameSize = format.encoding.bytesPerSample.toLong() * channelCount
        if (frameSize > buffer.remaining()) return 0L
        val bytesPerFrame = frameSize.toInt()
        val frameCount = buffer.remaining() / bytesPerFrame
        checkWaveformCancelled(shouldCancel)
        if (durationUs <= 0L || buckets.isEmpty()) return 0L
        val startPosition = buffer.position()
        val pcm16 = if (format.encoding == PcmEncoding.PCM_16BIT) {
            scratch.readPcm16(buffer, frameCount * channelCount)
        } else {
            null
        }
        var frameIndex = 0
        while (frameIndex < frameCount) {
            val packetOffset: Int
            val packetTimestampUs: Long
            val packetRemaining: Int
            if (packetTiming == null) {
                packetOffset = frameIndex
                packetTimestampUs = timestampUs
                packetRemaining = frameCount - frameIndex
            } else {
                val streamFrame = packetTiming.decodedFrames + frameIndex
                packetOffset = ((streamFrame - packetTiming.initialFrames) % packetTiming.packetFrames).toInt()
                packetTimestampUs = packetTiming.anchorUs +
                    (streamFrame - packetOffset) * 1_000_000L / sampleRate
                packetRemaining = packetTiming.packetFrames - packetOffset
            }
            if (frameIndex != 0 && packetOffset and 255 == 0) checkWaveformCancelled(shouldCancel)
            val frameTimeUs = packetTimestampUs + packetOffset * 1_000_000L / sampleRate
            val bucketIndex = bucketIndex(frameTimeUs, durationUs, buckets.size)

            // Bound each run by its packet and cancellation checkpoint as well as
            // its bucket. This retains integer rounding at original packet edges.
            val chunkEnd = frameIndex + minOf(frameCount - frameIndex, packetRemaining,
                256 - (packetOffset and 255))
            val lastTimeUs = packetTimestampUs +
                (packetOffset.toLong() + chunkEnd - frameIndex - 1) * 1_000_000L / sampleRate
            var end = chunkEnd
            if (lastTimeUs < frameTimeUs) {
                // Preserve timestamp wrapping for extreme metadata; do not binary
                // search across a discontinuity in the otherwise ordered timestamps.
                end = frameIndex + 1
            } else if (bucketIndex(lastTimeUs, durationUs, buckets.size) != bucketIndex) {
                var low = frameIndex + 1
                var high = chunkEnd - 1
                while (low < high) {
                    val middle = low + (high - low) / 2
                    val timeUs = packetTimestampUs + (packetOffset.toLong() + middle - frameIndex) * 1_000_000L / sampleRate
                    if (bucketIndex(timeUs, durationUs, buckets.size) == bucketIndex) {
                        low = middle + 1
                    } else {
                        high = middle
                    }
                }
                end = low
            }

            // Every channel of every frame in this run contributes to the same
            // bucket. Select the PCM reader once and update the bucket once.
            buckets[bucketIndex] = peakOfSamples(
                buffer, frameIndex * channelCount, (end - frameIndex) * channelCount,
                format.encoding, buckets[bucketIndex], pcm16,
            )
            buffer.position(startPosition + end * bytesPerFrame)
            frameIndex = end
        }
        return frameIndex.toLong()
    }

    fun pcmAmplitude(buffer: ByteBuffer, encoding: PcmEncoding): Int = when (encoding) {
        PcmEncoding.PCM_8BIT -> abs((buffer.get().toInt() and 0xFF) - 128) * 256
        PcmEncoding.PCM_FLOAT -> (abs(buffer.float).coerceAtMost(1f) * 32_767f).toInt()
        PcmEncoding.PCM_16BIT -> abs(buffer.short.toInt())
    }

    private fun peakOfSamples(
        buffer: ByteBuffer,
        sampleStart: Int,
        sampleCount: Int,
        encoding: PcmEncoding,
        initialPeak: Int,
        pcm16: ShortArray?,
    ): Int = when (encoding) {
        PcmEncoding.PCM_8BIT -> peakOfSamples(sampleCount, initialPeak) {
            abs((buffer.get().toInt() and 0xFF) - 128) * 256
        }
        PcmEncoding.PCM_FLOAT -> peakOfSamples(sampleCount, initialPeak) {
            (abs(buffer.float).coerceAtMost(1f) * 32_767f).toInt()
        }
        PcmEncoding.PCM_16BIT -> {
            val samples = requireNotNull(pcm16)
            var maximum = initialPeak.coerceAtLeast(0)
            var minimum = -maximum
            if (maximum < 32_768) {
                for (index in sampleStart until sampleStart + sampleCount) {
                    val sample = samples[index].toInt()
                    if (sample > maximum) maximum = sample
                    if (sample < minimum) {
                        minimum = sample
                        // No remaining PCM16 sample can exceed this amplitude.
                        if (minimum == -32_768) break
                    }
                }
            }
            maxOf(maximum, -minimum)
        }
    }

    private inline fun peakOfSamples(sampleCount: Int, initialPeak: Int, amplitude: () -> Int): Int {
        var peak = initialPeak
        repeat(sampleCount) { peak = maxOf(peak, amplitude()) }
        return peak
    }
}

internal fun checkWaveformCancelled(shouldCancel: () -> Boolean) {
    if (shouldCancel()) throw CancellationException("Waveform extraction cancelled")
}
