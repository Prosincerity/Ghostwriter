package com.prosincerity.ghostwriter.logic

import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.concurrent.CancellationException
import kotlin.math.abs

/**
 * Decodes a beat with Android's built-in media stack and reduces its PCM data
 * to fixed-size peak samples suitable for drawing a waveform.
 *
 * This intentionally has no Compose dependency. Decoding is synchronous, so
 * callers must invoke [extractAmplitudes] from a background dispatcher.
 */
object WaveformExtractor {

    /** Default waveform resolution: 1,000 evenly distributed amplitude peaks per beat. */
    const val DEFAULT_TARGET_SAMPLE_COUNT = 1_000

    private const val DEQUEUE_TIMEOUT_US = 10_000L

    /**
     * Returns the duration of the first audio track, or null if it cannot be
     * read. This only reads container metadata; it does not decode the beat.
     */
    fun durationMs(file: File): Long? {
        if (!file.isFile) return null
        val extractor = MediaExtractor()
        return try {
                extractor.setDataSource(file.absolutePath)
                val trackIndex = extractor.findAudioTrack() ?: return null
                val durationUs = extractor.getTrackFormat(trackIndex)
                    .longOrNull(MediaFormat.KEY_DURATION)
                    ?: return null
                (durationUs / 1_000L).takeIf { it > 0L }
        } catch (_: Exception) {
            null
        } finally {
            extractor.release()
        }
    }

    /**
     * Returns [targetSampleCount] peak amplitudes for a decodable audio file.
     * Amplitudes range from 0 to 32,768. Invalid, unsupported, or corrupt
     * files return an empty array. [shouldCancel] is checked throughout
     * decoding and throws [CancellationException] without writing a cache.
     */
    fun extractAmplitudes(
        file: File,
        targetSampleCount: Int = DEFAULT_TARGET_SAMPLE_COUNT,
        shouldCancel: () -> Boolean = { false },
    ): IntArray {
        if (targetSampleCount <= 0 || !file.isFile) return IntArray(0)

        return try {
            checkCancelled(shouldCancel)
            decodePeakBuckets(file, targetSampleCount, shouldCancel)
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            IntArray(0)
        }
    }

    internal fun bucketIndexForTimestamp(
        timestampUs: Long,
        durationUs: Long,
        bucketCount: Int,
    ): Int? {
        if (durationUs <= 0L || bucketCount <= 0) return null

        return ((timestampUs.coerceIn(0L, durationUs) * bucketCount) / durationUs)
            .toInt()
            .coerceAtMost(bucketCount - 1)
    }

    private fun decodePeakBuckets(
        file: File,
        targetSampleCount: Int,
        shouldCancel: () -> Boolean,
    ): IntArray {
        val extractor = MediaExtractor()
        var codec: MediaCodec? = null
        var codecStarted = false

        try {
            extractor.setDataSource(file.absolutePath)
            val audioTrackIndex = extractor.findAudioTrack() ?: return IntArray(0)
            val inputFormat = extractor.getTrackFormat(audioTrackIndex)
            val durationUs = inputFormat.longOrNull(MediaFormat.KEY_DURATION) ?: return IntArray(0)
            if (durationUs <= 0L) return IntArray(0)
            val mimeType = inputFormat.getString(MediaFormat.KEY_MIME) ?: return IntArray(0)
            extractor.selectTrack(audioTrackIndex)

            val activeCodec = MediaCodec.createDecoderByType(mimeType)
            codec = activeCodec
            activeCodec.configure(inputFormat, null, null, 0)
            activeCodec.start()
            codecStarted = true

            val buckets = IntArray(targetSampleCount)
            val bufferInfo = MediaCodec.BufferInfo()
            var outputFormat = inputFormat
            var inputEnded = false
            var outputEnded = false
            var decodedFrameCount = 0L

            while (!outputEnded) {
                checkCancelled(shouldCancel)
                if (!inputEnded) {
                    val inputIndex = activeCodec.dequeueInputBuffer(DEQUEUE_TIMEOUT_US)
                    if (inputIndex >= 0) {
                        val inputBuffer = activeCodec.getInputBuffer(inputIndex)
                            ?: throw IllegalStateException("Decoder provided no input buffer")
                        inputBuffer.clear()
                        val sampleSize = extractor.readSampleData(inputBuffer, 0)
                        if (sampleSize < 0) {
                            activeCodec.queueInputBuffer(
                                inputIndex,
                                0,
                                0,
                                0,
                                MediaCodec.BUFFER_FLAG_END_OF_STREAM,
                            )
                            inputEnded = true
                        } else {
                            activeCodec.queueInputBuffer(
                                inputIndex,
                                0,
                                sampleSize,
                                extractor.sampleTime,
                                0,
                            )
                            extractor.advance()
                        }
                    }
                }

                when (val outputIndex = activeCodec.dequeueOutputBuffer(bufferInfo, DEQUEUE_TIMEOUT_US)) {
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> outputFormat = activeCodec.outputFormat
                    MediaCodec.INFO_TRY_AGAIN_LATER -> Unit
                    else -> if (outputIndex >= 0) {
                        try {
                            activeCodec.getOutputBuffer(outputIndex)?.let { outputBuffer ->
                                decodedFrameCount += appendFramePeaksToBuckets(
                                    buffer = outputBuffer,
                                    bufferInfo = bufferInfo,
                                    format = outputFormat,
                                    durationUs = durationUs,
                                    buckets = buckets,
                                    shouldCancel = shouldCancel,
                                )
                            }
                            outputEnded = bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                        } finally {
                            activeCodec.releaseOutputBuffer(outputIndex, false)
                        }
                    }
                }
            }

            return if (decodedFrameCount > 0L) buckets else IntArray(0)
        } finally {
            if (codecStarted) runCatching { codec?.stop() }
            runCatching { codec?.release() }
            extractor.release()
        }
    }

    private fun appendFramePeaksToBuckets(
        buffer: ByteBuffer,
        bufferInfo: MediaCodec.BufferInfo,
        format: MediaFormat,
        durationUs: Long,
        buckets: IntArray,
        shouldCancel: () -> Boolean,
    ): Long {
        if (bufferInfo.size <= 0) return 0L

        val channelCount = format.integerOrDefault(MediaFormat.KEY_CHANNEL_COUNT, 1).coerceAtLeast(1)
        val sampleRate = format.integerOrDefault(MediaFormat.KEY_SAMPLE_RATE, 44_100).coerceAtLeast(1)
        val pcmEncoding = format.integerOrDefault(
            MediaFormat.KEY_PCM_ENCODING,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        val bytesPerSample = when (pcmEncoding) {
            AudioFormat.ENCODING_PCM_8BIT -> 1
            AudioFormat.ENCODING_PCM_FLOAT -> 4
            else -> 2
        }
        val bytesPerFrame = bytesPerSample * channelCount
        if (bytesPerFrame <= 0) return 0L

        val readable = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN).apply {
            clear()
            val start = bufferInfo.offset.coerceIn(0, capacity())
            val end = (bufferInfo.offset.toLong() + bufferInfo.size)
                .coerceIn(start.toLong(), capacity().toLong())
                .toInt()
            position(start)
            limit(end)
        }

        var frameIndex = 0L
        while (readable.remaining() >= bytesPerFrame) {
            if (frameIndex % 256L == 0L) checkCancelled(shouldCancel)
            var framePeak = 0
            repeat(channelCount) {
                val amplitude = when (pcmEncoding) {
                    AudioFormat.ENCODING_PCM_8BIT -> abs((readable.get().toInt() and 0xFF) - 128) * 257
                    AudioFormat.ENCODING_PCM_FLOAT -> {
                        (abs(readable.float).coerceAtMost(1f) * 32_767f).toInt()
                    }
                    else -> abs(readable.short.toInt())
                }
                framePeak = maxOf(framePeak, amplitude)
            }
            val frameTimeUs = bufferInfo.presentationTimeUs + (frameIndex * 1_000_000L / sampleRate)
            val bucketIndex = bucketIndexForTimestamp(
                timestampUs = frameTimeUs,
                durationUs = durationUs,
                bucketCount = buckets.size,
            ) ?: return frameIndex
            buckets[bucketIndex] = maxOf(buckets[bucketIndex], framePeak)
            frameIndex++
        }
        return frameIndex
    }

    private fun MediaExtractor.findAudioTrack(): Int? =
        (0 until trackCount).firstOrNull { trackIndex ->
            getTrackFormat(trackIndex).getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true
        }

    private fun MediaFormat.integerOrDefault(key: String, defaultValue: Int): Int =
        if (containsKey(key)) getInteger(key) else defaultValue

    private fun MediaFormat.longOrNull(key: String): Long? =
        if (containsKey(key)) getLong(key) else null

    private fun checkCancelled(shouldCancel: () -> Boolean) {
        if (shouldCancel()) throw CancellationException("Waveform extraction cancelled")
    }
}
