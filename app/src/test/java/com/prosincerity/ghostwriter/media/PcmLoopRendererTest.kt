package com.prosincerity.ghostwriter.media

import java.lang.management.ManagementFactory
import java.util.concurrent.atomic.AtomicReference
import org.junit.Assert.*
import org.junit.Assume.assumeTrue
import org.junit.Test

class PcmLoopRendererTest {
    private val noEvents = FramePositionEvents { _, _ -> }
    private fun source(channels: Int = 1, frames: Int = 20): MemoryPcmSource =
        MemoryPcmSource(ShortArray(frames * channels) { it.toShort() }, channels)

    @Test fun render_wrapsAcrossChunksAndPreservesBothChannels() {
        val bounds = AtomicReference(PcmLoopBounds.create(3, 7, true, 100))
        val renderer = PcmLoopRenderer(source(2), bounds, 9, noEvents)
        renderer.seek(1)
        val samples = ShortArray(18)
        assertEquals(9, renderer.render(samples))
        assertEquals(listOf(1, 2, 3, 4, 5, 6, 3, 4, 5).flatMap { listOf(it * 2, it * 2 + 1) }, samples.map { it.toInt() })
        renderer.render(samples)
        assertEquals(listOf(6, 3, 4, 5, 6, 3, 4, 5, 6).flatMap { listOf(it * 2, it * 2 + 1) }, samples.map { it.toInt() })
    }

    @Test fun crossfade_overlapsTailWithHeadAndAdvancesPastMixedHead() {
        val bounds = AtomicReference(PcmLoopBounds.create(3, 13, true, 1000))
        val renderer = PcmLoopRenderer(source(), bounds, 8, noEvents)
        renderer.seek(8)
        val samples = ShortArray(8)
        assertEquals(8, renderer.render(samples))
        // Tail 10,11,12 mixes with head 3,4,5 at weights 0,0.5,1.
        assertEquals(listOf(8, 9, 10, 7, 5, 6, 7, 8), samples.map { it.toInt() })
        assertEquals(9L, renderer.position)
        assertEquals(1L, renderer.loops)
    }

    @Test fun continuation_pastMarkerEndFinishesPhysicalTailThenUsesMarkerLoop() {
        val renderer = PcmLoopRenderer(source(), AtomicReference(PcmLoopBounds.create(3, 7, true, 100)), 10, noEvents)
        renderer.seek(17, continueFromCurrentPosition = true)
        val output = ShortArray(10)
        assertEquals(10, renderer.render(output))
        assertArrayEquals(shortArrayOf(17, 18, 19, 3, 4, 5, 6, 3, 4, 5), output)
        assertEquals(2L, renderer.loops)
    }

    @Test fun continuation_insideMarkerRangeKeepsPositionAndWrapsAtMarkerEnd() {
        val renderer = PcmLoopRenderer(source(), AtomicReference(PcmLoopBounds.create(3, 7, true, 100)), 4, noEvents)
        renderer.seek(5, continueFromCurrentPosition = true)
        val output = ShortArray(4)
        renderer.render(output)
        assertArrayEquals(shortArrayOf(5, 6, 3, 4), output)
    }

    @Test fun seek_pastMarkerEndPlaysTailAcrossChunksThenStartsAtStartMarker() {
        val renderer = PcmLoopRenderer(source(), AtomicReference(PcmLoopBounds.create(3, 7, true, 100)), 4, noEvents)
        renderer.seek(15)
        assertEquals(15L, renderer.position)
        val output = ShortArray(4)
        renderer.render(output)
        assertArrayEquals(shortArrayOf(15, 16, 17, 18), output)
        assertEquals(0L, renderer.loops)
        renderer.render(output)
        assertArrayEquals(shortArrayOf(19, 3, 4, 5), output)
        renderer.render(output)
        assertArrayEquals(shortArrayOf(6, 3, 4, 5), output)
        renderer.render(output)
        assertArrayEquals(shortArrayOf(6, 3, 4, 5), output)
    }

    @Test fun seek_atEndOnlyMarkerPlaysTailThenLoopsFromZero() {
        val renderer = PcmLoopRenderer(source(), AtomicReference(PcmLoopBounds.create(0, 7, true, 100)), 4, noEvents)
        renderer.seek(7)
        val output = ShortArray(4)
        renderer.render(output)
        assertArrayEquals(shortArrayOf(7, 8, 9, 10), output)
        renderer.seek(18)
        renderer.render(output)
        assertArrayEquals(shortArrayOf(18, 19, 0, 1), output)
        renderer.render(output)
        assertArrayEquals(shortArrayOf(2, 3, 4, 5), output)
        renderer.render(output)
        assertArrayEquals(shortArrayOf(6, 0, 1, 2), output)
    }

    @Test fun seek_backInsideLoopCancelsTailPlaybackAndRestoresMarkerCrossfade() {
        val renderer = PcmLoopRenderer(source(), AtomicReference(PcmLoopBounds.create(3, 13, true, 1000)), 8, noEvents)
        renderer.seek(15)
        val output = ShortArray(8)
        renderer.render(output)
        assertArrayEquals(shortArrayOf(15, 16, 17, 18, 19, 3, 4, 5), output)
        renderer.seek(8)
        renderer.render(output)
        assertArrayEquals(shortArrayOf(8, 9, 10, 7, 5, 6, 7, 8), output)
    }

    @Test fun seek_beforeStartPlaysIntroThenLoopsAndDisablingLoopPlaysToPhysicalEnd() {
        val bounds = AtomicReference(PcmLoopBounds.create(3, 7, true, 100))
        val renderer = PcmLoopRenderer(source(), bounds, 10, noEvents)
        renderer.seek(1)
        val output = ShortArray(10)
        renderer.render(output)
        assertArrayEquals(shortArrayOf(1, 2, 3, 4, 5, 6, 3, 4, 5, 6), output)
        renderer.seek(17)
        bounds.set(PcmLoopBounds.create(3, 7, false, 100))
        assertEquals(3, renderer.render(output))
        assertArrayEquals(shortArrayOf(17, 18, 19), output.copyOf(3))
        assertEquals(0, renderer.render(output))
    }

    @Test fun seek_outsideLoopReportsConsumedTailAndMarkerLoopPositions() {
        val ledger = PlaybackFrameLedger(16, 20)
        val renderer = PcmLoopRenderer(source(), AtomicReference(PcmLoopBounds.create(3, 7, true, 100)), 14, ledger)
        renderer.seek(17)
        ledger.reset(renderer.position)
        renderer.render(ShortArray(14))
        for ((consumed, beat) in listOf(0L to 17L, 2L to 19L, 3L to 3L, 5L to 5L,
            6L to 6L, 7L to 3L, 10L to 6L, 11L to 3L, 13L to 5L)) {
            assertEquals(beat, ledger.positionAt(consumed))
        }
    }

    @Test fun continuation_tailCrossfadeJoinsHeadAndLiveEditStillApplies() {
        val bounds = AtomicReference(PcmLoopBounds.create(3, 13, true, 1000))
        val renderer = PcmLoopRenderer(source(), bounds, 8, noEvents)
        renderer.seek(15, continueFromCurrentPosition = true)
        val output = ShortArray(8)
        renderer.render(output)
        assertArrayEquals(shortArrayOf(15, 16, 17, 11, 5, 6, 7, 8), output)
        renderer.seek(15, continueFromCurrentPosition = true)
        bounds.set(PcmLoopBounds.create(2, 7, true, 100))
        renderer.render(output)
        assertArrayEquals(shortArrayOf(2, 3, 4, 5, 6, 2, 3, 4), output)
    }

    @Test fun crossfade_constantSignalHasNoDipOrGainSpike() {
        val source = MemoryPcmSource(ShortArray(100) { 12000 }, 2)
        val renderer = PcmLoopRenderer(source, AtomicReference(PcmLoopBounds.create(5, 25, true, 1000)), 40, noEvents)
        renderer.seek(22)
        val output = ShortArray(80)
        assertEquals(40, renderer.render(output))
        assertTrue(output.all { it == 12000.toShort() })
    }

    @Test fun liveBounds_applyNextRenderAndJumpIfPastNewEndWithoutResettingOutputClock() {
        val bounds = AtomicReference(PcmLoopBounds.create(3, 15, true, 100))
        val renderer = PcmLoopRenderer(source(), bounds, 4, noEvents)
        renderer.seek(10)
        val output = ShortArray(4)
        renderer.render(output)
        bounds.set(PcmLoopBounds.create(2, 7, true, 100))
        renderer.render(output)
        assertEquals(listOf(2, 3, 4, 5), output.map { it.toInt() })
        assertEquals(8L, renderer.outputFrames)
        bounds.set(PcmLoopBounds.create(2, 7, false, 100))
        renderer.render(output)
        assertEquals(listOf(6, 7, 8, 9), output.map { it.toInt() })
    }

    @Test fun missingBoundaries_useZeroAndPhysicalEnd() {
        val endOnly = PcmLoopRenderer(source(), AtomicReference(PcmLoopBounds.create(0, 7, true, 100)), 4, noEvents)
        endOnly.seek(5)
        val output = ShortArray(4)
        endOnly.render(output)
        assertEquals(listOf(5, 6, 0, 1), output.map { it.toInt() })
        val startOnly = PcmLoopRenderer(source(), AtomicReference(PcmLoopBounds.create(3, 20, true, 100)), 4, noEvents)
        startOnly.seek(18)
        startOnly.render(output)
        assertEquals(listOf(18, 19, 3, 4), output.map { it.toInt() })
    }

    @Test fun oneFrameLoop_fillsEveryRequestedFrame() {
        val output = ShortArray(10)
        val tiny = PcmLoopRenderer(source(), AtomicReference(PcmLoopBounds.create(4, 5, true, 1000)), 10, noEvents)
        tiny.seek(4)
        assertEquals(10, tiny.render(output))
        assertTrue(output.all { it == 4.toShort() })
    }

    @Test fun seek_clampsPhysicalPositionsPreservesMarkerEndAndResetsOutputClock() {
        val bounds = AtomicReference(PcmLoopBounds.create(3, 7, true, 100))
        val renderer = PcmLoopRenderer(source(), bounds, 10, noEvents)
        renderer.seek(6)
        renderer.render(ShortArray(10))
        assertTrue(renderer.loops > 0)
        renderer.seek(7)
        assertEquals(7L, renderer.position)
        assertEquals(0L, renderer.outputFrames)
        assertEquals(0L, renderer.loops)
        renderer.seek(-1)
        assertEquals(0L, renderer.position)
        bounds.set(PcmLoopBounds.create(3, 7, false, 100))
        renderer.seek(100)
        assertEquals(20L, renderer.position)
        assertEquals(0, renderer.render(ShortArray(10)))
    }

    @Test fun oneFrameCrossfade_usesHeadSampleAndSkipsItOnWrap() {
        val renderer = PcmLoopRenderer(source(), AtomicReference(PcmLoopBounds.create(2, 5, true, 1000)), 4, noEvents)
        renderer.seek(3)
        val output = ShortArray(4)
        assertEquals(4, renderer.render(output))
        assertArrayEquals(shortArrayOf(3, 2, 3, 2), output)
        assertEquals(1L, renderer.loops)
    }

    @Test fun missingSequentialPage_doesNotAdvancePositionOrOutputClockAndCanRetry() {
        val source = UnavailableSource(source())
        source.missingFrame = 0
        val renderer = PcmLoopRenderer(source, AtomicReference(PcmLoopBounds.create(0, 20, false, 100)), 4, noEvents)
        val output = ShortArray(4)
        assertEquals(0, renderer.render(output))
        assertEquals(0L, renderer.position)
        assertEquals(0L, renderer.outputFrames)
        source.missingFrame = null
        assertEquals(4, renderer.render(output))
        assertArrayEquals(shortArrayOf(0, 1, 2, 3), output)
        source.missingFrame = 4
        assertEquals(0, renderer.render(output))
        assertEquals(4L, renderer.position)
        assertEquals(4L, renderer.outputFrames)
        source.missingFrame = null
        assertEquals(4, renderer.render(output))
        assertArrayEquals(shortArrayOf(4, 5, 6, 7), output)
    }

    @Test fun missingCrossfadeHead_retriesEntireJoinWithoutLosingTailFrames() {
        val source = UnavailableSource(source())
        source.missingFrame = 2
        val renderer = PcmLoopRenderer(source, AtomicReference(PcmLoopBounds.create(2, 12, true, 1000)), 4, noEvents)
        renderer.seek(9)
        val output = ShortArray(4)
        assertEquals(0, renderer.render(output))
        assertEquals(9L, renderer.position)
        assertEquals(0L, renderer.outputFrames)
        source.missingFrame = null
        assertEquals(4, renderer.render(output))
        assertArrayEquals(shortArrayOf(9, 6, 4, 5), output)
        assertEquals(6L, renderer.position)
        assertEquals(1L, renderer.loops)
    }

    @Test fun shortSourceCopies_preserveCrossfadeAcrossRepeatedReads() {
        val source = UnavailableSource(source()).apply { maxCopy = 1 }
        val renderer = PcmLoopRenderer(source, AtomicReference(PcmLoopBounds.create(3, 13, true, 1000)), 8, noEvents)
        renderer.seek(8)
        val output = ShortArray(8)
        assertEquals(8, renderer.render(output))
        assertArrayEquals(shortArrayOf(8, 9, 10, 7, 5, 6, 7, 8), output)
    }

    @Test fun bounds_rejectEmptyReversedAndNegativeRanges() {
        for ((start, end) in listOf(-1L to 10L, 5L to 5L, 10L to 5L)) {
            assertThrows(IllegalArgumentException::class.java) { PcmLoopBounds.create(start, end, true, 48000) }
        }
    }

    private class UnavailableSource(private val delegate: PcmSource) : PcmSource by delegate {
        var missingFrame: Long? = null
        var maxCopy = Int.MAX_VALUE
        override fun copyFrames(frame: Long, destination: ShortArray, destinationFrame: Int, count: Int): Int =
            if (frame == missingFrame) 0 else delegate.copyFrames(frame, destination, destinationFrame, minOf(count, maxCopy))
    }

    @Test fun render_allocatesNoMemoryAfterPreparation() {
        val bean = ManagementFactory.getThreadMXBean() as? com.sun.management.ThreadMXBean
        assumeTrue(bean != null && bean.isThreadAllocatedMemorySupported)
        bean!!.isThreadAllocatedMemoryEnabled = true
        val bounds = AtomicReference(PcmLoopBounds.create(10, 3000, true, 48000))
        val renderer = PcmLoopRenderer(source(2, 5000), bounds, 128, noEvents)
        val samples = ShortArray(256)
        repeat(20000) { renderer.render(samples) }
        @Suppress("DEPRECATION") // getId works on every supported build JDK.
        val thread = Thread.currentThread().id
        val before = bean.getThreadAllocatedBytes(thread)
        repeat(1000) { renderer.render(samples) }
        val allocated = bean.getThreadAllocatedBytes(thread) - before
        assertEquals("Audio rendering must not allocate", 0L, allocated)
    }
}
