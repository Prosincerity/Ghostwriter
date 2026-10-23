package com.prosincerity.ghostwriter.waveform.domain

import org.junit.Assert.assertEquals
import org.junit.Test

class WaveformDrawingGeometryTest {
    @Test
    fun wholeBeatSpansTheViewportAndIncludesEverySample() {
        val geometry = WaveformDrawingGeometry(WaveformViewport(), 200f, 5)

        assertEquals(0, geometry.firstSample)
        assertEquals(4, geometry.lastSample)
        assertEquals(0f, geometry.sampleX(0), DELTA)
        assertEquals(100f, geometry.sampleX(2), DELTA)
        assertEquals(200f, geometry.sampleX(4), DELTA)
    }

    @Test
    fun zoomAndScrollPreserveVisibleEdgeSamples() {
        val geometry = WaveformDrawingGeometry(WaveformViewport(4f, 210f), 200f, 9)

        assertEquals(2, geometry.firstSample)
        assertEquals(5, geometry.lastSample)
        assertEquals(-10f, geometry.sampleX(2), DELTA)
        assertEquals(190f, geometry.sampleX(4), DELTA)
        assertEquals(290f, geometry.sampleX(5), DELTA)
    }

    @Test
    fun scrollingToTheEndIncludesTheLastSample() {
        val geometry = WaveformDrawingGeometry(WaveformViewport(4f, 600f), 200f, 9)

        assertEquals(6, geometry.firstSample)
        assertEquals(8, geometry.lastSample)
        assertEquals(200f, geometry.sampleX(8), DELTA)
    }

    @Test
    fun emptySamplesOrUnmeasuredWidthsHaveNoSampleRange() {
        for (geometry in listOf(
            WaveformDrawingGeometry(WaveformViewport(), 200f, 0),
            WaveformDrawingGeometry(WaveformViewport(), 0f, 5),
            WaveformDrawingGeometry(WaveformViewport(), -100f, 5),
        )) {
            assertEquals(0, geometry.firstSample)
            assertEquals(-1, geometry.lastSample)
        }
    }

    @Test
    fun oneSampleStaysAtTheContentStartWithoutDivisionByZero() {
        val geometry = WaveformDrawingGeometry(WaveformViewport(2f, 100f), 200f, 1)

        assertEquals(0, geometry.firstSample)
        assertEquals(0, geometry.lastSample)
        assertEquals(-100f, geometry.sampleX(0), DELTA)
    }

    @Test
    fun invalidViewportBoundsAreClampedForSamplesAndPositions() {
        val geometry = WaveformDrawingGeometry(WaveformViewport(100f, Float.MAX_VALUE), 200f, 1000)

        assertEquals(984, geometry.firstSample)
        assertEquals(999, geometry.lastSample)
        assertEquals(200f, geometry.sampleX(999), DELTA)
        assertEquals(200f, geometry.positionToX(1000L, 1000L), DELTA)
    }

    @Test
    fun positionsClampToBeatBoundsAndRespectTheViewport() {
        val geometry = WaveformDrawingGeometry(WaveformViewport(2f, 100f), 200f, 5)

        assertEquals(-100f, geometry.positionToX(-1L, 1000L), DELTA)
        assertEquals(100f, geometry.positionToX(500L, 1000L), DELTA)
        assertEquals(300f, geometry.positionToX(1001L, 1000L), DELTA)
        assertEquals(0f, geometry.positionToX(500L, 0L), DELTA)
        assertEquals(0f, WaveformDrawingGeometry(WaveformViewport(), 0f, 5).positionToX(500L, 1000L), DELTA)
    }

    @Test
    fun peakHeightNormalizesAndClampsAmplitude() {
        assertEquals(0f, waveformPeakHalfHeight(-1, 100f), DELTA)
        assertEquals(0f, waveformPeakHalfHeight(0, 100f), DELTA)
        assertEquals(25f, waveformPeakHalfHeight(16384, 100f), DELTA)
        assertEquals(50f, waveformPeakHalfHeight(32768, 100f), DELTA)
        assertEquals(50f, waveformPeakHalfHeight(Int.MAX_VALUE, 100f), DELTA)
        assertEquals(0f, waveformPeakHalfHeight(32768, 0f), DELTA)
    }

    private companion object {
        const val DELTA = 0.001f
    }
}
