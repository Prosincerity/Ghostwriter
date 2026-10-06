package com.prosincerity.ghostwriter.media

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.junit.After
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PcmBeatDecoderTest {
    @Test fun changingAacSampleRate_neverReturnsPcmWithMixedFrameFormats() {
        val source = File(directory, "rate-change.aac")
        source.outputStream().use { output ->
            for ((rate, frequencyIndex) in listOf(44100 to 4, 48000 to 3)) {
                val extractor = android.media.MediaExtractor()
                try {
                    extractor.setDataSource(encodeAac(rate).absolutePath)
                    extractor.selectTrack(0)
                    val packet = ByteBuffer.allocate(8192)
                    while (true) {
                        packet.clear()
                        val size = extractor.readSampleData(packet, 0)
                        if (size < 0) break
                        val length = size + 7
                        output.write(byteArrayOf(0xff.toByte(), 0xf1.toByte(),
                            ((1 shl 6) or (frequencyIndex shl 2)).toByte(),
                            ((1 shl 6) or (length shr 11)).toByte(), (length shr 3).toByte(),
                            (((length and 7) shl 5) or 0x1f).toByte(), 0xfc.toByte()))
                        output.write(packet.array(), 0, size)
                        extractor.advance()
                    }
                } finally { extractor.release() }
            }
        }
        val result = runCatching { PcmBeatDecoder.decode(source, directory) { false } }
        if (result.isSuccess) {
            // Some Android codecs normalize rate changes to their configured
            // output rate. In that case all returned frames use that rate.
            val pcm = result.getOrThrow()
            assertEquals(44100, pcm.sampleRate)
            assertEquals(1, pcm.channels)
            assertEquals(pcm.frames * 2, pcm.file.length())
            assertTrue(pcm.frames > 0)
        } else {
            assertTrue(result.exceptionOrNull() is IllegalArgumentException)
            assertEquals("Audio format changed during decoding", result.exceptionOrNull()!!.message)
            assertTrue(directory.listFiles()!!.none { it.extension == "pcm" })
        }
    }

    @Test fun unsupportedChannelCount_releasesDecoderAndDeletesFailedOutput() {
        val source = wav("nine-channel.wav", 1, 16, 9, ByteArray(8000 * 9 * 2))
        assertThrows(IllegalArgumentException::class.java) {
            PcmBeatDecoder.decode(source, directory) { false }
        }
        assertTrue(directory.listFiles()!!.none { it.extension == "pcm" })
    }
    @Test fun cancellationAfterSetup_deletesPartialPcmAndAllowsRetry() {
        val file = wav("cancel.wav", 1, 16, 1, ByteArray(16000))
        val before = directory.listFiles()!!.map { it.name }.toSet()
        assertThrows(java.util.concurrent.CancellationException::class.java) {
            PcmBeatDecoder.decode(file, directory) { true }
        }
        assertEquals(before, directory.listFiles()!!.map { it.name }.toSet())
        val pcm = PcmBeatDecoder.decode(file, directory) { false }
        assertEquals(8000L, pcm.frames)
        assertEquals(16000L, pcm.file.length())
    }
    private val directory = File(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir,
        "decoder-tests-${System.nanoTime()}").apply { check(mkdirs()) }

    @After fun deleteFixtures() { directory.deleteRecursively() }

    @Test fun pcm8Bit_convertsUnsignedSamplesToSigned16Bit() {
        val input = ByteArray(1280) { intArrayOf(0, 64, 128, 192, 255)[it % 5].toByte() }
        val file = wav("8bit.wav", format = 1, bits = 8, channels = 1, data = input)
        val pcm = PcmBeatDecoder.decode(file, directory) { false }
        assertEquals(1280L, pcm.frames)
        assertEquals(1, pcm.channels)
        val expected = ShortArray(1280) { shortArrayOf(-32768, -16384, 0, 16384, 32512)[it % 5] }
        assertArrayEquals(expected, readSamples(pcm.file))
    }

    @Test fun pcmFloat_clampsOutOfRangeSamplesBeforeConverting() {
        val values = floatArrayOf(-2f, -1f, -0.5f, 0f, 0.5f, 1f, 2f)
        val bytes = ByteBuffer.allocate(1400 * 4).order(ByteOrder.LITTLE_ENDIAN)
        repeat(1400) { bytes.putFloat(values[it % values.size]) }
        val file = wav("float.wav", format = 3, bits = 32, channels = 1, data = bytes.array())
        val pcm = PcmBeatDecoder.decode(file, directory) { false }
        assertEquals(1400L, pcm.frames)
        val expected = shortArrayOf(-32768, -32768, -16384, 0, 16384, 32767, 32767)
        val actual = readSamples(pcm.file)
        assertEquals(1400, actual.size)
        // Android may convert the float WAV to PCM16 before exposing it to the
        // decoder. Its quantization and our float conversion differ by one unit.
        actual.forEachIndexed { index, sample ->
            assertEquals("Sample $index", expected[index % expected.size].toDouble(), sample.toDouble(), 1.0)
        }
    }

    @Test fun stereoPcm_keepsChannelOrderAndCountsFramesRatherThanSamples() {
        val expected = ShortArray(1600) { if (it % 2 == 0) 12000 else -6000 }
        val bytes = ByteBuffer.allocate(expected.size * 2).order(ByteOrder.LITTLE_ENDIAN)
        expected.forEach { bytes.putShort(it) }
        val file = wav("stereo.wav", format = 1, bits = 16, channels = 2, data = bytes.array())
        assertEquals(8000, PcmBeatDecoder.sampleRate(file))
        val pcm = PcmBeatDecoder.decode(file, directory) { false }
        assertEquals(8000, pcm.sampleRate)
        assertEquals(2, pcm.channels)
        assertEquals(800L, pcm.frames)
        assertEquals(3200L, pcm.file.length())
        assertArrayEquals(expected, readSamples(pcm.file))
    }

    @Test fun corruptInput_returnsUnknownSampleRateAndDeletesFailedDecodeOutput() {
        val file = File(directory, "corrupt.wav").apply { writeText("not audio") }
        assertEquals(0, PcmBeatDecoder.sampleRate(file))
        val before = directory.listFiles()!!.map { it.name }.toSet()
        assertThrows(Exception::class.java) { PcmBeatDecoder.decode(file, directory) { false } }
        assertEquals(before, directory.listFiles()!!.map { it.name }.toSet())
    }

    @Test fun pcm44100Hz_preservesFinalFrameDespiteRoundedWavDuration() {
        val expected = ShortArray(1600) { (it - 800).toShort() }
        val bytes = ByteBuffer.allocate(expected.size * 2).order(ByteOrder.LITTLE_ENDIAN)
        expected.forEach { bytes.putShort(it) }
        val file = wav("rounded-duration.wav", format = 1, bits = 16, channels = 2,
            data = bytes.array(), sampleRate = 44100)
        val pcm = PcmBeatDecoder.decode(file, directory) { false }
        assertEquals(800L, pcm.frames)
        assertArrayEquals(expected, readSamples(pcm.file))
    }

    @Test fun aacInput_decodesCompressedAudioToLittleEndianPcm() {
        val source = encodeAac()
        assertEquals(44100, PcmBeatDecoder.sampleRate(source))
        val pcm = PcmBeatDecoder.decode(source, directory) { false }
        assertEquals(44100, pcm.sampleRate)
        assertEquals(1, pcm.channels)
        assertTrue("AAC decoded no audio", pcm.frames > 0)
        assertTrue("AAC output unexpectedly exceeded padding allowance", pcm.frames <= 8192)
        assertEquals(pcm.frames * 2, pcm.file.length())
        assertTrue(readSamples(pcm.file).any { it.toInt() != 0 })
    }

    private fun readSamples(file: File): ShortArray {
        val bytes = ByteBuffer.wrap(file.readBytes()).order(ByteOrder.LITTLE_ENDIAN)
        return ShortArray(bytes.remaining() / 2) { bytes.short }
    }

    private fun wav(name: String, format: Int, bits: Int, channels: Int, data: ByteArray, sampleRate: Int = 8000): File {
        val bytesPerFrame = channels * bits / 8
        val wav = ByteBuffer.allocate(44 + data.size).order(ByteOrder.LITTLE_ENDIAN)
        wav.put("RIFF".toByteArray(Charsets.US_ASCII)).putInt(36 + data.size)
        wav.put("WAVEfmt ".toByteArray(Charsets.US_ASCII)).putInt(16)
        wav.putShort(format.toShort()).putShort(channels.toShort()).putInt(sampleRate)
        wav.putInt(sampleRate * bytesPerFrame).putShort(bytesPerFrame.toShort()).putShort(bits.toShort())
        wav.put("data".toByteArray(Charsets.US_ASCII)).putInt(data.size).put(data)
        return File(directory, name).apply { writeBytes(wav.array()) }
    }

    /** Generate the compressed fixture with AOSP APIs so this test stays offline. */
    private fun encodeAac(sampleRate: Int = 44100): File {
        val file = File(directory, "encoded-$sampleRate.m4a")
        val encoder = MediaCodec.createEncoderByType("audio/mp4a-latm")
        val muxer = MediaMuxer(file.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        var encoderStarted = false
        var muxerStarted = false
        try {
            val format = MediaFormat.createAudioFormat("audio/mp4a-latm", sampleRate, 1).apply {
                setInteger(MediaFormat.KEY_AAC_PROFILE, MediaCodecInfo.CodecProfileLevel.AACObjectLC)
                setInteger(MediaFormat.KEY_BIT_RATE, 64000)
            }
            encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            encoder.start()
            encoderStarted = true
            var inputFrame = 0
            var inputEnded = false
            var outputEnded = false
            var track = -1
            val info = MediaCodec.BufferInfo()
            val deadline = SystemClock.uptimeMillis() + 10000
            while (!outputEnded) {
                check(SystemClock.uptimeMillis() < deadline) { "AAC fixture encoder stalled" }
                if (!inputEnded) {
                    val index = encoder.dequeueInputBuffer(10000)
                    if (index >= 0) {
                        val input = requireNotNull(encoder.getInputBuffer(index)).apply { clear(); order(ByteOrder.LITTLE_ENDIAN) }
                        val count = minOf(input.remaining() / 2, 4096 - inputFrame)
                        repeat(count) { input.putShort(if ((inputFrame + it) % 32 < 16) 10000 else -10000) }
                        val pts = inputFrame * 1000000L / sampleRate
                        encoder.queueInputBuffer(index, 0, count * 2, pts,
                            if (count == 0) MediaCodec.BUFFER_FLAG_END_OF_STREAM else 0)
                        inputEnded = count == 0
                        inputFrame += count
                    }
                }
                val index = encoder.dequeueOutputBuffer(info, 10000)
                if (index == MediaCodec.INFO_OUTPUT_FORMAT_CHANGED) {
                    track = muxer.addTrack(encoder.outputFormat)
                    muxer.start()
                    muxerStarted = true
                } else if (index >= 0) {
                    try {
                        if (info.size > 0 && info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG == 0) {
                            val output = requireNotNull(encoder.getOutputBuffer(index))
                            output.position(info.offset)
                            output.limit(info.offset + info.size)
                            muxer.writeSampleData(track, output, info)
                        }
                        outputEnded = info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                    } finally { encoder.releaseOutputBuffer(index, false) }
                }
            }
            return file
        } finally {
            try { if (encoderStarted) encoder.stop() } finally {
                encoder.release()
                try { if (muxerStarted) muxer.stop() } finally { muxer.release() }
            }
        }
    }
}
