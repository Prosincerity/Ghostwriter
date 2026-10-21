package com.prosincerity.ghostwriter.media

import android.media.MediaCodec
import android.media.MediaCodecInfo
import android.media.MediaFormat
import android.media.MediaMuxer
import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prosincerity.ghostwriter.logic.WaveformExtractor
import com.prosincerity.ghostwriter.data.MarkerLoopRole
import com.prosincerity.ghostwriter.data.WaveformMarker
import java.io.File
import java.nio.ByteBuffer
import java.nio.ByteOrder
import org.junit.Rule
import org.junit.rules.TemporaryFolder
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MediaDecoderBoundaryTest {
    @get:Rule val folder = TemporaryFolder(InstrumentationRegistry.getInstrumentation().targetContext.cacheDir)
    private val directory get() = folder.root

    @Test fun videoOnlyContainer_hasNoAudioTimingOrWaveformAndLeavesNoPcm() {
        val source = videoOnly()
        assertNull(WaveformExtractor.durationMs(source))
        assertTrue(WaveformExtractor.extractAmplitudes(source).isEmpty())
        assertNull(PcmBeatDecoder.audioTiming(source))
        val failure = assertThrows(IllegalStateException::class.java) {
            PcmBeatDecoder.decode(source, directory) { false }
        }
        assertEquals("No audio track", failure.message)
        assertEquals(listOf(source), directory.listFiles()!!.toList())

        // MediaPlayer can prepare a video container even when audio timing
        // cannot be read. Marker validation must still use its duration.
        val player = BeatPlayer()
        try {
            assertTrue(player.load(source))
            assertEquals(0, player.sampleRate)
            assertTrue(player.durationMs > 0)
            assertFalse(player.hasValidMarkerLoop(emptyList()))
            assertTrue(player.hasValidMarkerLoop(listOf(
                WaveformMarker("Start", 0, MarkerLoopRole.START),
                WaveformMarker("End", player.durationMs.toLong(), MarkerLoopRole.END),
            )))
            player.setLoopMode(BeatLoopMode.MARKERS)
            assertTrue(player.isReady)
        } finally { player.release() }
    }

    @Test fun emptyAudio_hasNoDurationOrPeaksAndDoesNotLeaveFailedDecodeOutput() {
        val bytes = ByteBuffer.allocate(44).order(ByteOrder.LITTLE_ENDIAN)
        bytes.put("RIFF".toByteArray()).putInt(36).put("WAVEfmt ".toByteArray())
        bytes.putInt(16).putShort(1).putShort(1).putInt(8000).putInt(16000)
        bytes.putShort(2).putShort(16).put("data".toByteArray()).putInt(0)
        val source = File(directory, "empty.wav").apply { writeBytes(bytes.array()) }
        assertNull(WaveformExtractor.durationMs(source))
        assertTrue(WaveformExtractor.extractAmplitudes(source).isEmpty())
        assertThrows(Exception::class.java) { PcmBeatDecoder.decode(source, directory) { false } }
        assertEquals(listOf(source), directory.listFiles()!!.toList())
    }

    /** A real container made offline with AOSP APIs, with no bundled binary fixture. */
    private fun videoOnly(): File {
        val source = File(directory, "video-only.mp4")
        val encoder = MediaCodec.createEncoderByType("video/avc")
        val muxer = MediaMuxer(source.absolutePath, MediaMuxer.OutputFormat.MUXER_OUTPUT_MPEG_4)
        var started = false
        var muxing = false
        try {
            val format = MediaFormat.createVideoFormat("video/avc", 320, 240).apply {
                setInteger(MediaFormat.KEY_COLOR_FORMAT, MediaCodecInfo.CodecCapabilities.COLOR_FormatYUV420Flexible)
                setInteger(MediaFormat.KEY_BIT_RATE, 64000)
                setInteger(MediaFormat.KEY_FRAME_RATE, 10)
                setInteger(MediaFormat.KEY_I_FRAME_INTERVAL, 1)
            }
            encoder.configure(format, null, null, MediaCodec.CONFIGURE_FLAG_ENCODE)
            encoder.start()
            started = true
            var frames = 0
            var ended = false
            var track = -1
            val info = MediaCodec.BufferInfo()
            val deadline = SystemClock.uptimeMillis() + 10000
            while (!ended) {
                check(SystemClock.uptimeMillis() < deadline) { "Video fixture encoder stalled" }
                if (frames <= 2) {
                    val input = encoder.dequeueInputBuffer(10000)
                    if (input >= 0) {
                        val size = if (frames < 2) 320 * 240 * 3 / 2 else 0
                        encoder.getInputBuffer(input)!!.apply { clear(); put(ByteArray(size)) }
                        encoder.queueInputBuffer(input, 0, size, frames * 100000L,
                            if (frames == 2) MediaCodec.BUFFER_FLAG_END_OF_STREAM else 0)
                        frames++
                    }
                }
                when (val output = encoder.dequeueOutputBuffer(info, 10000)) {
                    MediaCodec.INFO_OUTPUT_FORMAT_CHANGED -> {
                        track = muxer.addTrack(encoder.outputFormat)
                        muxer.start()
                        muxing = true
                    }
                    else -> if (output >= 0) {
                        try {
                            if (info.size > 0 && info.flags and MediaCodec.BUFFER_FLAG_CODEC_CONFIG == 0) {
                                muxer.writeSampleData(track, encoder.getOutputBuffer(output)!!, info)
                            }
                            ended = info.flags and MediaCodec.BUFFER_FLAG_END_OF_STREAM != 0
                        } finally { encoder.releaseOutputBuffer(output, false) }
                    }
                }
            }
        } finally {
            try { if (started) encoder.stop() } finally {
                encoder.release()
                try { if (muxing) muxer.stop() } finally { muxer.release() }
            }
        }
        return source
    }
}
