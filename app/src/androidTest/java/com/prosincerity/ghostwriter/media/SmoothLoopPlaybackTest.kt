package com.prosincerity.ghostwriter.media

import android.os.SystemClock
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import com.prosincerity.ghostwriter.logic.MarkerLoopFrames
import java.io.File
import java.io.IOException
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.*
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class SmoothLoopPlaybackTest {
    private val instrumentation get() = InstrumentationRegistry.getInstrumentation()

    @Test fun pausedAndClosedOutput_rejectsPreparedStart() {
        val source = ObservedSource(MemoryPcmSource(ShortArray(8000), 1))
        val primed = CountDownLatch(1)
        lateinit var playback: SmoothLoopPlayback
        var version = -1L
        instrumentation.runOnMainSync {
            playback = SmoothLoopPlayback(PcmBeat(File("unused.pcm"), 8000, 1, 8000), source,
                0, null, false, 0f, {}, { throw AssertionError(it) }, onPrimed = { primed.countDown() })
            assertFalse(playback.startPrepared(0))
            version = playback.prepareContinuation(100)
        }
        try {
            assertTrue(primed.await(5, TimeUnit.SECONDS))
            instrumentation.runOnMainSync {
                playback.pause()
                assertFalse(playback.startPrepared(version))
                playback.close()
                assertFalse(playback.startPrepared(version))
                assertFalse(playback.isPlaying)
            }
        } finally {
            instrumentation.runOnMainSync { playback.close() }
            assertTrue(source.closed.await(5, TimeUnit.SECONDS))
        }
    }

    @Test fun stereoPlayback_changesVolumeCompletesAndRestartsAtBeginning() {
        val source = ObservedSource(MemoryPcmSource(ShortArray(16000) { if (it % 2 == 0) 1000 else -1000 }, 2))
        val failure = AtomicReference<Exception?>()
        lateinit var playback: SmoothLoopPlayback
        instrumentation.runOnMainSync {
            playback = playback(source, range = null, looping = false, onFailure = { failure.set(it) })
            playback.play()
        }
        try {
            awaitCondition { playback.currentPositionMs > 50 || failure.get() != null }
            assertNull(failure.get())
            instrumentation.runOnMainSync { playback.setVolume(0.25f) }
            awaitCondition { !playback.isPlaying || failure.get() != null }
            assertNull(failure.get())
            assertTrue(playback.currentPositionMs >= 990)
            instrumentation.runOnMainSync { playback.pause(); playback.play() }
            awaitCondition { playback.isPlaying && playback.currentPositionMs in 1..500 || failure.get() != null }
            assertNull(failure.get())
        } finally {
            instrumentation.runOnMainSync { playback.close(); assertFalse(playback.isPlaying) }
            assertTrue("Playback did not close its PCM source", source.closed.await(5, TimeUnit.SECONDS))
        }
    }

    @Test fun invalidAndOversizedRanges_fallBackToFullBeatAndLiveChangesReachSource() {
        val source = ObservedSource(MemoryPcmSource(ShortArray(8000), 1))
        val failure = AtomicReference<Exception?>()
        lateinit var playback: SmoothLoopPlayback
        instrumentation.runOnMainSync {
            playback = playback(source, MarkerLoopFrames(2000, 1000), true) { failure.set(it) }
        }
        try {
            assertEquals(Triple(0L, 8000L, true), source.range.get())
            instrumentation.runOnMainSync { playback.configure(MarkerLoopFrames(9000, 10000), true) }
            assertEquals(Triple(0L, 8000L, true), source.range.get())
            instrumentation.runOnMainSync { playback.configure(MarkerLoopFrames(1000, 10000), false) }
            assertEquals(Triple(1000L, 8000L, false), source.range.get())
            instrumentation.runOnMainSync { playback.configure(null, true) }
            assertEquals(Triple(0L, 8000L, true), source.range.get())
        } finally {
            instrumentation.runOnMainSync { playback.close() }
            assertTrue(source.closed.await(5, TimeUnit.SECONDS))
        }
        assertNull(failure.get())
    }

    @Test fun eightChannelPlayback_advancesInFramesAndPausesCleanly() {
        val source = ObservedSource(MemoryPcmSource(ShortArray(64000) { 1000 }, 8))
        val failure = AtomicReference<Exception?>()
        lateinit var playback: SmoothLoopPlayback
        instrumentation.runOnMainSync {
            playback = playback(source, null, true) { failure.set(it) }
            playback.play()
        }
        try {
            awaitCondition { playback.currentPositionMs > 50 || failure.get() != null }
            assertNull(failure.get())
            instrumentation.runOnMainSync { playback.pause() }
            assertFalse(playback.isPlaying)
        } finally {
            instrumentation.runOnMainSync { playback.close() }
            assertTrue(source.closed.await(5, TimeUnit.SECONDS))
        }
    }

    @Test fun sourceFailure_isDeliveredOnMainThreadAndClosesSource() {
        val expected = IOException("PCM read failed")
        val source = ObservedSource(object : PcmSource {
            override val channels = 1
            override val frames = 8000L
            override fun copyFrames(frame: Long, destination: ShortArray, destinationFrame: Int, count: Int): Int = throw expected
        })
        val observed = AtomicReference<Exception?>()
        val callbackThread = AtomicReference<Thread?>()
        val notified = CountDownLatch(1)
        lateinit var playback: SmoothLoopPlayback
        instrumentation.runOnMainSync {
            playback = playback(source, null, true) {
                callbackThread.set(Thread.currentThread())
                observed.set(it)
                notified.countDown()
            }
            playback.play()
        }
        try {
            assertTrue("Failure callback was not delivered", notified.await(5, TimeUnit.SECONDS))
            assertSame(expected, observed.get())
            assertSame(android.os.Looper.getMainLooper().thread, callbackThread.get())
            assertFalse(playback.isPlaying)
            assertTrue(source.closed.await(5, TimeUnit.SECONDS))
        } finally {
            instrumentation.runOnMainSync { playback.close() }
        }
    }

    @Test fun prefetchWorkerFailure_stopsStreamAndPublishesOriginalException() {
        val missing = File(instrumentation.targetContext.cacheDir, "missing-${System.nanoTime()}.pcm")
        val source = PcmRingBuffer(missing, 1, 8000)
        val observed = AtomicReference<Exception?>()
        val notified = CountDownLatch(1)
        var playback: SmoothLoopPlayback? = null
        try {
            awaitCondition { source.failure != null }
            instrumentation.runOnMainSync {
                playback = playback(source, null, true) { observed.set(it); notified.countDown() }.also { it.play() }
            }
            assertTrue(notified.await(5, TimeUnit.SECONDS))
            assertSame(source.failure, observed.get())
            assertFalse(playback!!.isPlaying)
        } finally {
            playback?.let { current -> instrumentation.runOnMainSync { current.close() } }
            source.close()
        }
    }

    @Test fun continuation_primesSilentlyAndStartsOnlyWhenAuthorized() {
        val source = ObservedSource(MemoryPcmSource(ShortArray(16000) { 1000 }, 1))
        val primed = CountDownLatch(1)
        val started = CountDownLatch(1)
        val failure = AtomicReference<Exception?>()
        val callbackThread = AtomicReference<Thread?>()
        lateinit var playback: SmoothLoopPlayback
        var version = -1L
        instrumentation.runOnMainSync {
            playback = SmoothLoopPlayback(
                pcm = PcmBeat(File(instrumentation.targetContext.cacheDir, "priming-only.pcm"), 8000, 1, 16000),
                source = source,
                initialPositionMs = 0,
                range = null,
                looping = false,
                initialVolume = 0f,
                onStateChanged = {},
                onFailure = { failure.set(it) },
                onPrimed = { callbackThread.set(Thread.currentThread()); primed.countDown() },
                onOutputStarted = { started.countDown() },
            )
            version = playback.prepareContinuation(700)
        }
        try {
            assertTrue("Output never primed", primed.await(5, TimeUnit.SECONDS))
            assertNull(failure.get())
            assertFalse(playback.isPlaying)
            assertEquals(1L, started.count)
            assertSame(android.os.Looper.getMainLooper().thread, callbackThread.get())
            instrumentation.runOnMainSync {
                assertFalse(playback.startPrepared(version - 1))
                assertTrue(playback.startPrepared(version))
            }
            assertTrue("Output never started", started.await(5, TimeUnit.SECONDS))
            assertTrue(playback.isPlaying)
            assertTrue(playback.currentPositionMs >= 700)
            assertNull(failure.get())
        } finally {
            instrumentation.runOnMainSync { playback.close() }
            assertTrue(source.closed.await(5, TimeUnit.SECONDS))
        }
    }

    private fun playback(
        source: PcmSource,
        range: MarkerLoopFrames?,
        looping: Boolean,
        onFailure: (Exception) -> Unit,
    ) = SmoothLoopPlayback(
        pcm = PcmBeat(File(instrumentation.targetContext.cacheDir, "memory-only.pcm"), 8000, source.channels, source.frames),
        source = source,
        initialPositionMs = 0,
        range = range,
        looping = looping,
        initialVolume = 1f,
        onStateChanged = {},
        onFailure = onFailure,
    )

    private fun awaitCondition(condition: () -> Boolean) {
        val deadline = SystemClock.uptimeMillis() + 5000
        while (!condition() && SystemClock.uptimeMillis() < deadline) SystemClock.sleep(10)
        assertTrue("Playback did not reach expected state", condition())
    }

    private class ObservedSource(private val delegate: PcmSource) : PcmSource by delegate {
        val closed = CountDownLatch(1)
        val range = AtomicReference<Triple<Long, Long, Boolean>>()
        override fun prepareLoop(start: Long, end: Long, enabled: Boolean) { range.set(Triple(start, end, enabled)) }
        override fun close() { delegate.close(); closed.countDown() }
    }
}
