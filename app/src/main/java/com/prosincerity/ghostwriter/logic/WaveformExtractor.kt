package com.prosincerity.ghostwriter.logic

import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaExtractor
import android.media.MediaFormat
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
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
     * Returns [targetSampleCount] peak amplitudes for a decodable audio file.
     * Amplitudes range from 0 to 32,768. Invalid, unsupported, or corrupt
     * files return an empty array so an unavailable waveform never blocks
     * beat playback.
     */
    fun extractAmplitudes(file: File, targetSampleCount: Int = DEFAULT_TARGET_SAMPLE_COUNT): IntArray {
        if (targetSampleCount <= 0 || !file.isFile) return IntArray(0)

        return runCatching {
            decodeFramePeaks(file).let { framePeaks ->
                if (framePeaks.isEmpty()) IntArray(0)
                else downsampleFramePeaks(framePeaks, targetSampleCount)
            }
        }.getOrElse { IntArray(0) }
    }

    /**
     * Reduces one peak per decoded PCM frame into [targetSampleCount] evenly
     * spaced buckets. Empty input and non-positive targets have no waveform.
     */
    internal fun downsampleFramePeaks(framePeaks: IntArray, targetSampleCount: Int): IntArray {
        if (framePeaks.isEmpty() || targetSampleCount <= 0) return IntArray(0)

        return IntArray(targetSampleCount).also { peaks ->
            framePeaks.forEachIndexed { frameIndex, amplitude ->
                val bucket = ((frameIndex.toLong() * targetSampleCount) / framePeaks.size)
                    .toInt()
                    .coerceAtMost(targetSampleCount - 1)
                peaks[bucket] = maxOf(peaks[bucket], amplitude.coerceAtLeast(0))
            }
        }
    }

    private fun decodeFramePeaks(file: File): IntArray {
        val extractor = MediaExtractor()
        var codec: MediaCodec? = null
        var codecStarted = false

        try {
            extractor.setDataSource(file.absolutePath)
            val audioTrackIndex = (0 until extractor.trackCount).firstOrNull { trackIndex ->
                extractor.getTrackFormat(trackIndex)
                    .getString(MediaFormat.KEY_MIME)
                    ?.startsWith("audio/") == true
            } ?: return IntArray(0)

            val inputFormat = extractor.getTrackFormat(audioTrackIndex)
            val mimeType = inputFormat.getString(MediaFormat.KEY_MIME) ?: return IntArray(0)
            extractor.selectTrack(audioTrackIndex)

            val activeCodec = MediaCodec.createDecoderByType(mimeType)
            codec = activeCodec
            activeCodec.configure(inputFormat, null, null, 0)
            activeCodec.start()
            codecStarted = true

            val peakBuilder = IntArrayBuilder()
            val bufferInfo = MediaCodec.BufferInfo()
            var outputFormat = inputFormat
            var inputEnded = false
            var outputEnded = false

            while (!outputEnded) {
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
                                appendFramePeaks(outputBuffer, bufferInfo, outputFormat, peakBuilder)
                            }
                            outputEnded = bufferInfo.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                        } finally {
                            activeCodec.releaseOutputBuffer(outputIndex, false)
                        }
                    }
                }
            }

            return peakBuilder.toIntArray()
        } finally {
            if (codecStarted) runCatching { codec?.stop() }
            runCatching { codec?.release() }
            extractor.release()
        }
    }

    private fun appendFramePeaks(
        buffer: ByteBuffer,
        bufferInfo: MediaCodec.BufferInfo,
        format: MediaFormat,
        peakBuilder: IntArrayBuilder,
    ) {
        if (bufferInfo.size <= 0) return

        val channelCount = format.integerOrDefault(MediaFormat.KEY_CHANNEL_COUNT, 1).coerceAtLeast(1)
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
        if (bytesPerFrame <= 0) return

        val readable = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN).apply {
            clear()
            val start = bufferInfo.offset.coerceIn(0, capacity())
            val end = (bufferInfo.offset.toLong() + bufferInfo.size)
                .coerceIn(start.toLong(), capacity().toLong())
                .toInt()
            position(start)
            limit(end)
        }

        while (readable.remaining() >= bytesPerFrame) {
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
            peakBuilder.add(framePeak)
        }
    }

    private class IntArrayBuilder {
        private var values = IntArray(1_024)
        private var size = 0

        fun add(value: Int) {
            if (size == values.size) values = values.copyOf(values.size * 2)
            values[size++] = value
        }

        fun isEmpty(): Boolean = size == 0

        fun toIntArray(): IntArray = values.copyOf(size)
    }

    private fun MediaFormat.integerOrDefault(key: String, defaultValue: Int): Int =
        if (containsKey(key)) getInteger(key) else defaultValue
}
