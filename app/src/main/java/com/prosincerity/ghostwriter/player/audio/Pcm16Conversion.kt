package com.prosincerity.ghostwriter.player.audio

import com.prosincerity.ghostwriter.waveform.domain.PcmEncoding
import com.prosincerity.ghostwriter.waveform.domain.PcmFormat
import java.nio.ByteBuffer
import java.nio.ByteOrder

/** PCM conversion and frame arithmetic without codec calls or file I/O. */
internal object Pcm16Conversion {
    fun convertBuffer(
        decoded: ByteBuffer,
        offset: Int,
        size: Int,
        presentationTimeUs: Long,
        format: PcmFormat,
    ): ByteArray {
        require(size >= 0) { "PCM buffer size must not be negative" }
        val bytesPerSample = format.encoding.bytesPerSample
        val channels = format.channelCount
        val rate = format.sampleRate
        val buffer = decoded.duplicate().order(ByteOrder.LITTLE_ENDIAN)
        buffer.position(offset)
        buffer.limit(offset + size)
        val timestampFrame = nearestFrame(presentationTimeUs, rate)
        val skipFrames = (-timestampFrame).coerceAtLeast(0)
            .coerceAtMost((buffer.remaining() / (bytesPerSample * channels)).toLong())
        buffer.position(buffer.position() + (skipFrames * bytesPerSample * channels).toInt())
        // Keep decoder sample order; timestamps only remove negative preroll.
        val data = ByteArray(buffer.remaining() / bytesPerSample * 2)
        // PCM16 is already in the signed little-endian output format. Copy it
        // in bulk while keeping the same offset, preroll and whole-sample bounds.
        if (format.encoding == PcmEncoding.PCM_16BIT) {
            buffer.get(data)
            return data
        }
        var written = 0
        while (buffer.remaining() >= bytesPerSample) {
            val sample = if (format.encoding == PcmEncoding.PCM_FLOAT) {
                (buffer.float.coerceIn(-1f, 1f) * 32767).toInt()
            } else ((buffer.get().toInt() and 255) - 128) shl 8
            data[written++] = sample.toByte()
            data[written++] = (sample shr 8).toByte()
        }
        return data
    }

    fun trimmedFrames(byteCount: Long, sampleRate: Int, channels: Int, durationUs: Long): Long {
        val frames = byteCount / (channels * 2)
        val durationFrames = nearestFrame(durationUs, sampleRate)
        return if (durationFrames > 0) minOf(frames, durationFrames) else frames
    }

    private fun nearestFrame(timeUs: Long, sampleRate: Int): Long {
        // Container/codec times lose sub-microsecond precision. Truncating again
        // loses one frame at both the duration and the negative preroll boundary.
        val remainder = timeUs % 1_000_000L * sampleRate
        val rounding = if (remainder < 0) -500_000L else 500_000L
        return timeUs / 1_000_000L * sampleRate + (remainder + rounding) / 1_000_000L
    }
}
