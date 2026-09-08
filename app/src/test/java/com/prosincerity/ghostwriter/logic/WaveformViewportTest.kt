package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Test

class WaveformViewportTest {

    @Test
    fun positionToX_atOneXMapsTheWholeBeatAcrossTheViewport() {
        val viewport = WaveformViewport()

        assertEquals(0f, viewport.positionToX(0, 1_000, 200f), DELTA)
        assertEquals(100f, viewport.positionToX(500, 1_000, 200f), DELTA)
        assertEquals(200f, viewport.positionToX(1_000, 1_000, 200f), DELTA)
    }

    @Test
    fun positionAndSeekConversion_accountForZoomAndScroll() {
        val viewport = WaveformViewport(zoom = 2f, scrollOffsetPx = 100f)

        assertEquals(100f, viewport.positionToX(500, 1_000, 200f), DELTA)
        assertEquals(500L, viewport.xToPositionMs(100f, 1_000, 200f))
    }

    @Test
    fun xToPositionMs_clampsTouchesBeforeAndAfterTheVisibleArea() {
        val viewport = WaveformViewport(zoom = 2f, scrollOffsetPx = 100f)

        assertEquals(0L, viewport.xToPositionMs(-200f, 1_000, 200f))
        assertEquals(1_000L, viewport.xToPositionMs(1_000f, 1_000, 200f))
    }

    @Test
    fun zoomBy_preservesTheWaveformPointAtThePinchFocalPoint() {
        val zoomed = WaveformViewport().zoomBy(
            scaleFactor = 2f,
            focalXpx = 250f,
            viewportWidthPx = 1_000f,
        )

        assertEquals(2f, zoomed.zoom, DELTA)
        assertEquals(250f, zoomed.scrollOffsetPx, DELTA)
        assertEquals(250f, zoomed.positionToX(250, 1_000, 1_000f), DELTA)
    }

    @Test
    fun zoomBy_clampsZoomToTheDocumentedBounds() {
        val zoomedIn = WaveformViewport(zoom = 32f).zoomBy(4f, 50f, 100f)
        val zoomedOut = WaveformViewport(zoom = 2f).zoomBy(0.1f, 50f, 100f)

        assertEquals(WaveformViewport.MAX_ZOOM, zoomedIn.zoom, DELTA)
        assertEquals(WaveformViewport.MIN_ZOOM, zoomedOut.zoom, DELTA)
    }

    @Test
    fun panBy_clampsAtTheStartAndEndOfTheTimeline() {
        val viewport = WaveformViewport(zoom = 2f, scrollOffsetPx = 50f)

        assertEquals(0f, viewport.panBy(60f, 100f).scrollOffsetPx, DELTA)
        assertEquals(100f, viewport.panBy(-100f, 100f).scrollOffsetPx, DELTA)
    }

    @Test
    fun zeroDurationOrViewport_hasAStableZeroPosition() {
        val viewport = WaveformViewport(zoom = 4f, scrollOffsetPx = 25f)

        assertEquals(0f, viewport.positionToX(100, 0, 200f), DELTA)
        assertEquals(0L, viewport.xToPositionMs(10f, 1_000, 0f))
    }

    private companion object {
        const val DELTA = 0.001f
    }
}
