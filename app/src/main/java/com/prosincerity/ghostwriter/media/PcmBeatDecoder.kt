package com.prosincerity.ghostwriter.media

import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import com.prosincerity.ghostwriter.logic.WaveformExtractor
import java.io.File
import java.io.RandomAccessFile
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CancellationException

internal data class PcmBeat(val file: File, val sampleRate: Int, val channels: Int, val frames: Long) {
    val bytesPerFrame: Int get() = channels * 2
}

/** Temporary, disk-backed PCM keeps long beats out of the Java heap. Run off the UI thread. */
internal object PcmBeatDecoder {
    fun sampleRate(source: File): Int {
        val extractor = MediaExtractor()
        return try {
            extractor.setDataSource(source.absolutePath)
            val track = (0 until extractor.trackCount).firstOrNull {
                extractor.getTrackFormat(it).getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true
            } ?: return 0
            extractor.getTrackFormat(track).getInteger(MediaFormat.KEY_SAMPLE_RATE)
        } catch (_: Exception) { 0 } finally { extractor.release() }
    }

    fun decode(source: File, cacheDirectory: File, cancelled: () -> Boolean): PcmBeat {
        val output = File.createTempFile("beat-loop-", ".pcm", cacheDirectory)
        val extractor = MediaExtractor()
        var codec: MediaCodec? = null
        var started = false
        var succeeded = false
        try {
            extractor.setDataSource(source.absolutePath)
            val index = (0 until extractor.trackCount).firstOrNull {
                extractor.getTrackFormat(it).getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true
            } ?: error("No audio track")
            val input = extractor.getTrackFormat(index)
            val durationUs = if (input.containsKey(MediaFormat.KEY_DURATION)) input.getLong(MediaFormat.KEY_DURATION) else 0L
            // For raw audio this key describes the input samples, not an output request.
            if (input.getString(MediaFormat.KEY_MIME) != "audio/raw") {
                input.setInteger(MediaFormat.KEY_PCM_ENCODING, AudioFormat.ENCODING_PCM_16BIT)
            }
            extractor.selectTrack(index)
            val decoder = MediaCodec.createDecoderByType(requireNotNull(input.getString(MediaFormat.KEY_MIME)))
            codec = decoder
            decoder.configure(input, null, null, 0)
            decoder.start()
            started = true
            var format = input
            var sampleRate = 0
            var channels = 0
            var inputEnded = false
            var outputEnded = false
            val info = MediaCodec.BufferInfo()
            val progress = WaveformExtractor.DecoderProgressGuard()
            RandomAccessFile(output, "rw").use { pcm ->
                while (!outputEnded) {
                    if (cancelled()) throw CancellationException("Loop preparation cancelled")
                    var madeProgress = false
                    if (!inputEnded) {
                        val inputIndex = decoder.dequeueInputBuffer(10_000)
                        if (inputIndex >= 0) {
                            madeProgress = true
                            val buffer = requireNotNull(decoder.getInputBuffer(inputIndex)).apply { clear() }
                            val size = extractor.readSampleData(buffer, 0)
                            if (size < 0) {
                                decoder.queueInputBuffer(inputIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                                inputEnded = true
                            } else {
                                decoder.queueInputBuffer(inputIndex, 0, size, extractor.sampleTime, 0)
                                extractor.advance()
                            }
                        }
                    }
                    when (val outputIndex = decoder.dequeueOutputBuffer(info, 10_000)) {
                        MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> { format = decoder.outputFormat; madeProgress = true }
                        MediaCodec.INFO_TRY_AGAIN_LATER -> Unit
                        else -> if (outputIndex >= 0) {
                            madeProgress = true
                            try {
                                if (info.size > 0 && info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG == 0) {
                                    val rate = format.getInteger(MediaFormat.KEY_SAMPLE_RATE)
                                    val channelCount = format.getInteger(MediaFormat.KEY_CHANNEL_COUNT)
                                    require(rate > 0 && channelCount in 1..8)
                                    require(sampleRate == 0 || (rate == sampleRate && channelCount == channels)) {
                                        "Audio format changed during decoding"
                                    }
                                    sampleRate = rate
                                    channels = channelCount
                                    val encoding = if (format.containsKey(MediaFormat.KEY_PCM_ENCODING))
                                        format.getInteger(MediaFormat.KEY_PCM_ENCODING) else AudioFormat.ENCODING_PCM_16BIT
                                    appendDecodedBuffer(pcm, requireNotNull(decoder.getOutputBuffer(outputIndex)),
                                        info.offset, info.size, info.presentationTimeUs, rate, channels, encoding)
                                }
                                outputEnded = info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                            } finally {
                                decoder.releaseOutputBuffer(outputIndex, false)
                            }
                        }
                    }
                    progress.record(madeProgress)
                }
                require(sampleRate > 0 && channels > 0 && pcm.length() > 0) { "No decoded audio" }
                val trimmedFrames = trimToDuration(pcm, sampleRate, channels, durationUs)
                succeeded = true
                return PcmBeat(output, sampleRate, channels, trimmedFrames)
            }
        } finally {
            if (started) runCatching { codec?.stop() }
            runCatching { codec?.release() }
            extractor.release()
            if (!succeeded) output.delete()
        }
    }

    /** PCM conversion and trimming are separate from Android codec calls for JVM regression tests. */
    internal fun appendDecodedBuffer(
        pcm: RandomAccessFile,
        decoded: ByteBuffer,
        offset: Int,
        size: Int,
        presentationTimeUs: Long,
        rate: Int,
        channels: Int,
        encoding: Int,
    ) {
        require(encoding == AudioFormat.ENCODING_PCM_16BIT || encoding == AudioFormat.ENCODING_PCM_FLOAT ||
            encoding == AudioFormat.ENCODING_PCM_8BIT) { "Unsupported decoder PCM format" }
        val bytesPerSample = when (encoding) {
            AudioFormat.ENCODING_PCM_FLOAT -> 4
            AudioFormat.ENCODING_PCM_8BIT -> 1
            else -> 2
        }
        val buffer = decoded.duplicate().order(ByteOrder.LITTLE_ENDIAN)
        buffer.position(offset)
        buffer.limit(offset + size)
        val timestampFrame = nearestFrame(presentationTimeUs, rate)
        val skipFrames = (-timestampFrame).coerceAtLeast(0)
            .coerceAtMost((buffer.remaining() / (bytesPerSample * channels)).toLong())
        buffer.position(buffer.position() + (skipFrames * bytesPerSample * channels).toInt())
        // Append decoder output in order. Timestamp rounding must not overwrite samples.
        val data = ByteArray(buffer.remaining() / bytesPerSample * 2)
        var written = 0
        while (buffer.remaining() >= bytesPerSample) {
            val sample = when (encoding) {
                AudioFormat.ENCODING_PCM_FLOAT -> (buffer.float.coerceIn(-1f, 1f) * 32767).toInt()
                AudioFormat.ENCODING_PCM_8BIT -> ((buffer.get().toInt() and 255) - 128) shl 8
                else -> buffer.short.toInt()
            }
            data[written++] = sample.toByte()
            data[written++] = (sample shr 8).toByte()
        }
        pcm.write(data)
    }

    internal fun trimToDuration(pcm: RandomAccessFile, sampleRate: Int, channels: Int, durationUs: Long): Long {
        val frames = pcm.length() / (channels * 2)
        val durationFrames = nearestFrame(durationUs, sampleRate)
        val trimmedFrames = if (durationFrames > 0) minOf(frames, durationFrames) else frames
        pcm.setLength(trimmedFrames * channels * 2)
        return trimmedFrames
    }

    private fun nearestFrame(timeUs: Long, sampleRate: Int): Long {
        // Container/codec times lose sub-microsecond precision. Truncating again
        // loses one frame at both the duration and the negative preroll boundary.
        val remainder = timeUs % 1_000_000L * sampleRate
        val rounding = if (remainder < 0) -500_000L else 500_000L
        return timeUs / 1_000_000L * sampleRate + (remainder + rounding) / 1_000_000L
    }
}
