package com.prosincerity.ghostwriter.waveform.data

import com.prosincerity.ghostwriter.waveform.domain.Mp3FrameLayout
import com.prosincerity.ghostwriter.waveform.domain.PcmEncoding
import com.prosincerity.ghostwriter.waveform.domain.PcmFormat
import com.prosincerity.ghostwriter.waveform.domain.WaveformPeaks
import com.prosincerity.ghostwriter.waveform.domain.checkWaveformCancelled

import android.media.AudioFormat
import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaExtractor
import android.media.MediaFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.HandlerThread
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.util.ArrayDeque
import java.util.concurrent.CancellationException
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.TimeUnit

/**
 * Decodes a beat with Android's built-in media stack and reduces its PCM data
 * to fixed-size peak samples suitable for drawing a waveform.
 *
 * This intentionally has no Compose dependency. Calls block until decoding
 * finishes, so invoke [extractAmplitudes] from a background dispatcher. Codec
 * callbacks only enqueue events; PCM processing and cancellation checks stay
 * on the calling thread.
 */
object WaveformExtractor {

    /** Default waveform resolution: 1,000 evenly distributed amplitude peaks per beat. */
    const val DEFAULT_TARGET_SAMPLE_COUNT = 1_000

    private const val DEQUEUE_TIMEOUT_US = 10_000L
    private const val MAX_CONSECUTIVE_DEQUEUE_STALLS = 500
    private const val BATCH_INPUT_BYTES = 64 * 1024
    private const val BATCH_OUTPUT_BYTES = 256 * 1024

    private class BatchDecodeException(cause: Exception) : Exception(cause)

    internal class DecoderProgressGuard(
        private val maxConsecutiveStalls: Int = MAX_CONSECUTIVE_DEQUEUE_STALLS,
    ) {
        private var consecutiveStalls = 0

        init {
            require(maxConsecutiveStalls > 0)
        }

        fun record(madeProgress: Boolean) {
            if (madeProgress) {
                consecutiveStalls = 0
                return
            }

            consecutiveStalls++
            check(consecutiveStalls < maxConsecutiveStalls) {
                "Audio decoder stopped making progress"
            }
        }
    }

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
    ): IntArray = extractAmplitudes(file, targetSampleCount, shouldCancel, allowBatching = true)

    internal fun extractAmplitudes(
        file: File,
        targetSampleCount: Int,
        shouldCancel: () -> Boolean,
        allowBatching: Boolean,
    ): IntArray {
        if (targetSampleCount <= 0 || !file.isFile) return IntArray(0)

        return try {
            checkWaveformCancelled(shouldCancel)
            try {
                decodePeakBuckets(file, targetSampleCount, shouldCancel, allowBatching)
            } catch (_: BatchDecodeException) {
                checkWaveformCancelled(shouldCancel)
                decodePeakBuckets(file, targetSampleCount, shouldCancel, allowBatching = false)
            }
        } catch (cancellation: CancellationException) {
            throw cancellation
        } catch (_: Exception) {
            IntArray(0)
        }
    }

    private fun decodePeakBuckets(
        file: File,
        targetSampleCount: Int,
        shouldCancel: () -> Boolean,
        allowBatching: Boolean,
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
            // C2SoftMP3 timestamps each packet from its cumulative PCM frame count.
            // Keep other decoders on the original path; batch timing is checked
            // against two individually decoded packets before combining input.
            if (allowBatching && Build.VERSION.SDK_INT >= 35 && mimeType == "audio/mpeg" &&
                activeCodec.canonicalName == "c2.android.mp3.decoder" && activeCodec.codecInfo
                    .getCapabilitiesForType(mimeType)
                    .isFeatureSupported(MediaCodecInfo.CodecCapabilities.FEATURE_MultipleFrames)) {
                try {
                    return decodeBatched(extractor, activeCodec, inputFormat, durationUs,
                        targetSampleCount, shouldCancel)
                } catch (cancellation: CancellationException) {
                    throw cancellation
                } catch (failure: Exception) {
                    // Retry with fresh extractor/codec instances after this scope
                    // releases them. Unusual streams retain the original decoder.
                    throw BatchDecodeException(failure)
                }
            }
            activeCodec.configure(inputFormat, null, null, 0)
            activeCodec.start()
            codecStarted = true

            val buckets = IntArray(targetSampleCount)
            val bufferInfo = MediaCodec.BufferInfo()
            var outputFormat = inputFormat.pcmFormat()
            var inputEnded = false
            var outputEnded = false
            var decodedFrameCount = 0L
            val progressGuard = DecoderProgressGuard()
            val peakScratch = WaveformPeaks.Scratch()

            while (!outputEnded) {
                checkWaveformCancelled(shouldCancel)
                var madeProgress = false
                if (!inputEnded) {
                    val inputIndex = activeCodec.dequeueInputBuffer(DEQUEUE_TIMEOUT_US)
                    if (inputIndex >= 0) {
                        madeProgress = true
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
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        outputFormat = activeCodec.outputFormat.pcmFormat()
                        madeProgress = true
                    }
                    MediaCodec.INFO_TRY_AGAIN_LATER -> Unit
                    else -> if (outputIndex >= 0) {
                        madeProgress = true
                        try {
                            activeCodec.getOutputBuffer(outputIndex)?.let { outputBuffer ->
                                decodedFrameCount += appendFramePeaksToBuckets(
                                    buffer = outputBuffer,
                                    bufferInfo = bufferInfo,
                                    format = outputFormat,
                                    scratch = peakScratch,
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
                progressGuard.record(madeProgress)
            }

            return if (decodedFrameCount > 0L) buckets else IntArray(0)
        } finally {
            if (codecStarted) runCatching { codec?.stop() }
            runCatching { codec?.release() }
            extractor.release()
        }
    }

    private sealed interface DecoderEvent {
        data class Input(val index: Int) : DecoderEvent
        data class Output(val index: Int, val infos: List<MediaCodec.BufferInfo>) : DecoderEvent
        data class Format(val format: MediaFormat) : DecoderEvent
        data class Error(val exception: MediaCodec.CodecException) : DecoderEvent
    }

    @androidx.annotation.RequiresApi(35)
    private fun decodeBatched(
        extractor: MediaExtractor,
        codec: MediaCodec,
        inputFormat: MediaFormat,
        durationUs: Long,
        targetSampleCount: Int,
        shouldCancel: () -> Boolean,
    ): IntArray {
        val events = LinkedBlockingQueue<DecoderEvent>()
        val callbacks = HandlerThread("WaveformDecoderEvents").apply { start() }
        var started = false
        try {
            codec.setCallback(object : MediaCodec.Callback() {
                override fun onInputBufferAvailable(codec: MediaCodec, index: Int) {
                    events.add(DecoderEvent.Input(index))
                }
                override fun onOutputBufferAvailable(codec: MediaCodec, index: Int, info: MediaCodec.BufferInfo) {
                    events.add(DecoderEvent.Output(index, listOf(info.copyInfo())))
                }
                override fun onOutputBuffersAvailable(codec: MediaCodec, index: Int, infos: ArrayDeque<MediaCodec.BufferInfo>) {
                    events.add(DecoderEvent.Output(index, infos.map { it.copyInfo() }))
                }
                override fun onOutputFormatChanged(codec: MediaCodec, format: MediaFormat) {
                    events.add(DecoderEvent.Format(format))
                }
                override fun onError(codec: MediaCodec, exception: MediaCodec.CodecException) {
                    events.add(DecoderEvent.Error(exception))
                }
            }, Handler(callbacks.looper))
            inputFormat.setInteger(MediaFormat.KEY_MAX_INPUT_SIZE, BATCH_INPUT_BYTES)
            inputFormat.setInteger(MediaFormat.KEY_BUFFER_BATCH_MAX_OUTPUT_SIZE, BATCH_OUTPUT_BYTES)
            inputFormat.setInteger(MediaFormat.KEY_BUFFER_BATCH_THRESHOLD_OUTPUT_SIZE, 1)
            codec.configure(inputFormat, null, null, 0)
            codec.start()
            started = true
            val buckets = IntArray(targetSampleCount)
            val scratch = WaveformPeaks.Scratch()
            var outputFormat = inputFormat.pcmFormat()
            var inputEnded = false
            var outputEnded = false
            var frames = 0L
            val progress = DecoderProgressGuard()
            val inputInfos = ArrayDeque<MediaCodec.BufferInfo>()
            val pendingInputs = ArrayDeque<Int>()
            var primed = false
            var firstInputQueued = false
            var anchorUs = 0L
            var streamFrames = 0L
            var initialFrames = 0
            var samplesPerPacket = 0
            while (!outputEnded) {
                checkWaveformCancelled(shouldCancel)
                val event = if ((primed || !firstInputQueued) && pendingInputs.isNotEmpty()) {
                    DecoderEvent.Input(pendingInputs.removeFirst())
                } else {
                    events.poll(10, TimeUnit.MILLISECONDS)
                }
                progress.record(event != null)
                when (event) {
                    null -> Unit
                    is DecoderEvent.Error -> throw event.exception
                    is DecoderEvent.Format -> {
                        val nextFormat = event.format.pcmFormat()
                        check(!primed || nextFormat == outputFormat) { "MP3 format changed" }
                        check(nextFormat.channelCount in 1..2 && nextFormat.sampleRate > 0) { "Invalid MP3 PCM format" }
                        check(nextFormat.encoding == PcmEncoding.PCM_16BIT) { "MP3 batching requires PCM16" }
                        outputFormat = nextFormat
                    }
                    is DecoderEvent.Input -> if (!inputEnded && firstInputQueued && !primed) {
                        pendingInputs.add(event.index)
                    } else if (!inputEnded) {
                        val buffer = requireNotNull(codec.getInputBuffer(event.index)).order(ByteOrder.BIG_ENDIAN)
                        buffer.clear()
                        inputInfos.clear()
                        var offset = 0
                        while (offset < buffer.capacity()) {
                            checkWaveformCancelled(shouldCancel)
                            val nextSize = extractor.sampleSize
                            if (nextSize < 0L) {
                                inputInfos.add(MediaCodec.BufferInfo().apply {
                                    set(offset, 0, 0L, MediaCodec.BUFFER_FLAG_END_OF_STREAM)
                                })
                                inputEnded = true
                                break
                            }
                            if (nextSize > buffer.capacity() - offset) {
                                check(inputInfos.isNotEmpty()) { "Audio packet exceeds decoder input capacity" }
                                break
                            }
                            val size = extractor.readSampleData(buffer, offset)
                            check(size >= 4) { "Invalid MP3 packet" }
                            val samples = requireNotNull(Mp3FrameLayout.samplesPerPacket(
                                buffer.getInt(offset), size, outputFormat.sampleRate,
                            )) { "Unsupported MP3 packet layout" }
                            check(samplesPerPacket == 0 || samples == samplesPerPacket) { "MP3 packet size changed" }
                            samplesPerPacket = samples
                            inputInfos.add(MediaCodec.BufferInfo().apply {
                                set(offset, size, extractor.sampleTime, 0)
                            })
                            offset += size
                            extractor.advance()
                            if (!primed || inputInfos.size >= 128) break
                        }
                        firstInputQueued = true
                        codec.queueInputBuffers(event.index, inputInfos)
                    }
                    is DecoderEvent.Output -> {
                        try {
                            val buffer = codec.getOutputBuffer(event.index)
                            for (info in event.infos) {
                                if (buffer != null) {
                                    if (!primed && info.size > 0) {
                                        val added = appendFramePeaksToBuckets(buffer, info, outputFormat,
                                            scratch, durationUs, buckets, shouldCancel)
                                        check(added > 0L) { "Incomplete MP3 PCM frame" }
                                        if (initialFrames == 0) {
                                            initialFrames = added.toInt()
                                            anchorUs = info.presentationTimeUs
                                            firstInputQueued = false
                                        } else {
                                            check(info.presentationTimeUs == anchorUs +
                                                streamFrames * 1_000_000L / outputFormat.sampleRate) {
                                                "MP3 packet timestamps do not follow the PCM clock"
                                            }
                                            val eos = info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                                            check(eos || added == samplesPerPacket.toLong()) {
                                                "MP3 decoder packet length changed"
                                            }
                                            if (!eos) {
                                                primed = true
                                                // Delay trimming and packet timing are now known.
                                                // Let subsequent transfers contain large PCM buffers.
                                                codec.setParameters(Bundle().apply {
                                                    putInt(MediaFormat.KEY_BUFFER_BATCH_MAX_OUTPUT_SIZE, BATCH_OUTPUT_BYTES)
                                                    putInt(MediaFormat.KEY_BUFFER_BATCH_THRESHOLD_OUTPUT_SIZE, BATCH_OUTPUT_BYTES)
                                                })
                                            }
                                        }
                                        frames += added
                                        streamFrames += added
                                    } else if (primed && info.size > 0) {
                                        val added = appendFramePeaksToBuckets(buffer, info, outputFormat, scratch,
                                            durationUs, buckets, shouldCancel,
                                            WaveformPeaks.PacketTiming(anchorUs, initialFrames,
                                                samplesPerPacket, streamFrames))
                                        frames += added
                                        streamFrames += added
                                    }
                                }
                                outputEnded = outputEnded || info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                            }
                        } finally {
                            codec.releaseOutputBuffer(event.index, false)
                        }
                    }
                }
            }
            check(frames > 0L) { "Batched decoder produced no PCM" }
            return buckets
        } finally {
            if (started) runCatching { codec.stop() }
            callbacks.quitSafely()
            callbacks.join()
        }
    }

    private fun MediaCodec.BufferInfo.copyInfo() = MediaCodec.BufferInfo().also {
        it.set(offset, size, presentationTimeUs, flags)
    }

    private fun appendFramePeaksToBuckets(
        buffer: ByteBuffer,
        bufferInfo: MediaCodec.BufferInfo,
        format: PcmFormat,
        scratch: WaveformPeaks.Scratch,
        durationUs: Long,
        buckets: IntArray,
        shouldCancel: () -> Boolean,
        packetTiming: WaveformPeaks.PacketTiming? = null,
    ): Long {
        if (bufferInfo.size <= 0) return 0L

        val start = bufferInfo.offset.coerceIn(0, buffer.capacity())
        val end = (bufferInfo.offset.toLong() + bufferInfo.size)
            .coerceIn(start.toLong(), buffer.capacity().toLong())
            .toInt()
        val readable = buffer.duplicate().order(ByteOrder.LITTLE_ENDIAN).apply {
            clear()
            position(start)
            limit(end)
        }

        return WaveformPeaks.appendFrames(
            buffer = readable,
            format = format,
            scratch = scratch,
            packetTiming = packetTiming,
            timestampUs = bufferInfo.presentationTimeUs,
            durationUs = durationUs,
            buckets = buckets,
            shouldCancel = shouldCancel,
        )
    }

    private fun MediaFormat.pcmFormat(): PcmFormat {
        val channelCount = integerOrDefault(MediaFormat.KEY_CHANNEL_COUNT, 1)
        val sampleRate = integerOrDefault(MediaFormat.KEY_SAMPLE_RATE, 44_100)
        val pcmEncoding = integerOrDefault(
            MediaFormat.KEY_PCM_ENCODING,
            AudioFormat.ENCODING_PCM_16BIT,
        )
        val encoding = when (pcmEncoding) {
            AudioFormat.ENCODING_PCM_8BIT -> PcmEncoding.PCM_8BIT
            AudioFormat.ENCODING_PCM_FLOAT -> PcmEncoding.PCM_FLOAT
            else -> PcmEncoding.PCM_16BIT
        }

        return PcmFormat(channelCount, sampleRate, encoding)
    }

    private fun MediaExtractor.findAudioTrack(): Int? =
        (0 until trackCount).firstOrNull { trackIndex ->
            getTrackFormat(trackIndex).getString(MediaFormat.KEY_MIME)?.startsWith("audio/") == true
        }

    private fun MediaFormat.integerOrDefault(key: String, defaultValue: Int): Int =
        if (containsKey(key)) getInteger(key) else defaultValue

    private fun MediaFormat.longOrNull(key: String): Long? =
        if (containsKey(key)) getLong(key) else null
}
