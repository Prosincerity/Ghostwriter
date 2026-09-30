package com.prosincerity.ghostwriter.media

import android.media.AudioAttributes
import android.media.AudioFormat
import android.media.AudioTrack
import android.os.Handler
import android.os.Looper
import android.os.Process
import com.prosincerity.ghostwriter.logic.MarkerLoopFrames
import java.util.concurrent.atomic.AtomicBoolean
import java.util.concurrent.atomic.AtomicInteger
import java.util.concurrent.atomic.AtomicReference

/** AOSP push output with a lock-free render path and a separate disk-prefetch worker. */
internal class SmoothLoopPlayback(
    private val pcm: PcmBeat,
    private val source: PcmSource,
    initialPositionMs: Int,
    range: MarkerLoopFrames?,
    looping: Boolean,
    initialVolume: Float,
    private val onStateChanged: () -> Unit,
    private val onFailure: (Exception) -> Unit,
) {
    private class Transport(
        val frame: Long, val seekVersion: Long, val playing: Boolean,
        val finished: Boolean, val completion: Transport?,
    ) {
        companion object {
            // Both objects are allocated on the control thread; EOF only publishes one.
            fun create(frame: Long, version: Long, playing: Boolean): Transport {
                val completion = Transport(frame, version, false, true, null)
                return Transport(frame, version, playing, false, completion)
            }
        }
    }
    private val transport = AtomicReference(Transport.create(msToFrame(initialPositionMs), 0, false))
    private val bounds = AtomicReference(makeBounds(range, looping))
    private val closed = AtomicBoolean(false)
    private val notifications = AtomicInteger(0)
    @Volatile private var failure: Exception? = null
    @Volatile private var volume = initialVolume
    @Volatile var currentPositionMs = initialPositionMs
        private set
    val isPlaying: Boolean get() = transport.get().playing && !closed.get() && failure == null
    private val main = Handler(Looper.getMainLooper())
    private val publish = object : Runnable {
        override fun run() {
            if (closed.get()) return
            val events = notifications.getAndSet(0)
            if (events and FAILED != 0) failure?.let(onFailure)
            else if (events and CHANGED != 0) { source.setPlaying(isPlaying); onStateChanged() }
            if (!closed.get()) main.postDelayed(this, if (isPlaying) 33 else 250)
        }
    }

    init {
        val initial = bounds.get()
        source.prepareLoop(initial.start, initial.end, initial.enabled)
        source.requestFrame(msToFrame(initialPositionMs))
        main.post(publish)
        Thread(::stream, "Beat loop audio").apply { isDaemon = true; start() }
    }

    fun play() {
        source.setPlaying(true)
        val old = transport.get()
        val restart = old.finished || msToFrame(currentPositionMs) >= pcm.frames
        val start = if (bounds.get().enabled) bounds.get().start else 0
        transport.set(Transport.create(if (restart) start else old.frame, old.seekVersion + if (restart) 1 else 0, true))
    }

    fun pause() {
        source.setPlaying(false)
        val old = transport.get()
        if (!old.finished) transport.set(Transport.create(old.frame, old.seekVersion, false))
    }

    fun seekTo(positionMs: Int) {
        val old = transport.get()
        currentPositionMs = positionMs
        transport.set(Transport.create(msToFrame(positionMs), old.seekVersion + 1, old.playing))
    }

    fun configure(range: MarkerLoopFrames?, looping: Boolean) {
        val next = makeBounds(range, looping)
        if (next == bounds.get()) return
        source.prepareLoop(next.start, next.end, next.enabled)
        bounds.set(next) // Live marker edits never pause or flush output.
    }

    fun setVolume(value: Float) { volume = value }
    fun close() { closed.set(true); main.removeCallbacks(publish) }
    private fun msToFrame(ms: Int): Long = ms.toLong() * pcm.sampleRate / 1000
    private fun makeBounds(range: MarkerLoopFrames?, looping: Boolean): PcmLoopBounds {
        val start = range?.start ?: 0
        val end = (range?.end ?: pcm.frames).coerceAtMost(pcm.frames)
        val valid = start in 0 until end
        return PcmLoopBounds.create(if (valid) start else 0, if (valid) end else pcm.frames, looping, pcm.sampleRate)
    }
    private fun signal(event: Int) {
        var old = notifications.get()
        while (!notifications.compareAndSet(old, old or event)) old = notifications.get()
    }

    private fun stream() {
        var track: AudioTrack? = null
        try {
            Process.setThreadPriority(Process.THREAD_PRIORITY_AUDIO)
            val format = AudioFormat.Builder().setEncoding(AudioFormat.ENCODING_PCM_16BIT)
                .setSampleRate(pcm.sampleRate).setChannelIndexMask((1 shl pcm.channels) - 1).build()
            val mask = when (pcm.channels) {
                1 -> AudioFormat.CHANNEL_OUT_MONO
                2 -> AudioFormat.CHANNEL_OUT_STEREO
                else -> AudioFormat.CHANNEL_OUT_7POINT1_SURROUND
            }
            val minimum = AudioTrack.getMinBufferSize(pcm.sampleRate, mask, AudioFormat.ENCODING_PCM_16BIT)
            require(minimum > 0)
            val output = AudioTrack.Builder().setAudioAttributes(AudioAttributes.Builder()
                .setUsage(AudioAttributes.USAGE_MEDIA).setContentType(AudioAttributes.CONTENT_TYPE_MUSIC).build())
                .setAudioFormat(format).setTransferMode(AudioTrack.MODE_STREAM)
                .setBufferSizeInBytes(maxOf(minimum, pcm.sampleRate / 10 * pcm.bytesPerFrame)).build()
            track = output
            check(output.state == AudioTrack.STATE_INITIALIZED)
            val chunkFrames = maxOf(1, pcm.sampleRate / 50)
            val samples = ShortArray(chunkFrames * pcm.channels)
            val ledger = PlaybackFrameLedger(output.bufferSizeInFrames + chunkFrames + 2, pcm.frames)
            val renderer = PcmLoopRenderer(source, bounds, chunkFrames, ledger)
            var seekVersion = -1L
            var consumed = 0L
            var previousHead = 0L
            var previousPosition = 0L
            var playing = false
            var primed = false
            var queuedFrames = 0L
            var pendingSamples = 0
            var pendingOffset = 0
            var appliedVolume = volume
            output.setVolume(appliedVolume)
            while (!closed.get()) {
                val command = transport.get()
                if (command.seekVersion != seekVersion) {
                    output.pause()
                    output.flush()
                    renderer.seek(command.frame)
                    ledger.reset(renderer.position)
                    seekVersion = command.seekVersion
                    consumed = 0
                    previousHead = 0
                    previousPosition = renderer.position
                    playing = false
                    primed = false
                    queuedFrames = 0
                    pendingSamples = 0
                    pendingOffset = 0
                }
                if (volume != appliedVolume) { appliedVolume = volume; output.setVolume(appliedVolume) }
                if (!command.playing && playing) { output.pause(); playing = false }
                val head = output.playbackHeadPosition.toLong() and 0xffffffffL
                consumed += (head - previousHead) and 0xffffffffL
                previousHead = head
                val position = ledger.positionAt(consumed)
                if (transport.get().seekVersion == seekVersion) currentPositionMs = (position * 1000 / pcm.sampleRate).toInt()
                if (position < previousPosition) signal(CHANGED)
                previousPosition = position
                if (!command.playing) { Thread.sleep(10); continue }
                if (primed && !playing) { output.play(); playing = true }
                (source as? PcmRingBuffer)?.failure?.let { throw it }
                if (pendingOffset == pendingSamples) {
                    pendingSamples = renderer.render(samples) * pcm.channels
                    pendingOffset = 0
                }
                if (pendingSamples == 0) {
                    val atEnd = !bounds.get().enabled && renderer.position >= pcm.frames
                    if (!atEnd) { Thread.sleep(2); continue }
                    if (consumed >= renderer.outputFrames) {
                        command.completion?.let { if (transport.compareAndSet(command, it)) signal(CHANGED) }
                        output.pause()
                        playing = false
                        Thread.sleep(5)
                        continue
                    }
                    if (primed) { Thread.sleep(5); continue }
                    samples.fill(0)
                    pendingSamples = samples.size
                }
                val written = output.write(samples, pendingOffset, pendingSamples - pendingOffset, AudioTrack.WRITE_BLOCKING)
                check(written >= 0) { "AudioTrack write failed: $written" }
                pendingOffset += written
                queuedFrames += written / pcm.channels
                if (!primed && queuedFrames >= output.bufferSizeInFrames && transport.get().playing &&
                    transport.get().seekVersion == seekVersion) {
                    primed = true
                    output.play()
                    playing = true
                }
                if (written == 0) Thread.sleep(2)
            }
        } catch (problem: Exception) {
            if (!closed.get()) { failure = problem; signal(FAILED) }
        } finally {
            runCatching { track?.pause() }
            runCatching { track?.flush() }
            runCatching { track?.release() }
            source.close()
        }
    }

    private companion object { const val CHANGED = 1; const val FAILED = 2 }
}
