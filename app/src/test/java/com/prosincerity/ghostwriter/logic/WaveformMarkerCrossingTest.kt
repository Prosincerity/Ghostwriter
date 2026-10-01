package com.prosincerity.ghostwriter.logic

import com.prosincerity.ghostwriter.data.WaveformMarker
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WaveformMarkerCrossingTest {
    private val markers = listOf(WaveformMarker("Verse", 3000), WaveformMarker("Hook", 7000))

    @Test fun crossingsWorkInBothDirectionsAndAcrossSkippedMarkers() {
        assertTrue(crossesWaveformMarker(1000, 4000, markers))
        assertTrue(crossesWaveformMarker(4000, 1000, markers))
        assertTrue(crossesWaveformMarker(0, 10000, markers))
        assertTrue(crossesWaveformMarker(10000, 0, markers))
    }

    @Test fun landingCountsButDepartureAndStationaryUpdatesAreSilent() {
        assertTrue(crossesWaveformMarker(2000, 3000, markers))
        assertTrue(crossesWaveformMarker(4000, 3000, markers))
        assertFalse(crossesWaveformMarker(3000, 4000, markers))
        assertFalse(crossesWaveformMarker(3000, 2000, markers))
        assertFalse(crossesWaveformMarker(3000, 3000, markers))
    }

    @Test fun movementsBetweenMarkersAndEmptyTimelinesAreSilent() {
        assertFalse(crossesWaveformMarker(4000, 6000, markers))
        assertFalse(crossesWaveformMarker(6000, 4000, markers))
        assertFalse(crossesWaveformMarker(0, 10000, emptyList()))
    }

    @Test fun beatStartAndEndMarkersCanBeReached() {
        val boundaries = listOf(WaveformMarker("Start", 0), WaveformMarker("End", 10000))
        assertTrue(crossesWaveformMarker(1000, 0, boundaries))
        assertTrue(crossesWaveformMarker(9000, 10000, boundaries))
    }
}
