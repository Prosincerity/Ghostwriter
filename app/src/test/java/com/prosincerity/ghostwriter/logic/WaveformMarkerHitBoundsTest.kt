package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class WaveformMarkerHitBoundsTest {
    @Test
    fun isolatedMarker_keepsItsHandleAndFullLabelTarget() {
        assertEquals(76f..180f, bounds(100f, listOf(100f)))
        assertEquals(76f..124f, waveformMarkerHitBounds(100f, 24f, 10f, 4f, emptyList()))
    }

    @Test
    fun nearbyMarkers_shareMidpointWithoutCoveringEachOthersLines() {
        for (positions in listOf(listOf(100f, 110f), listOf(110f, 100f))) {
            val start = bounds(100f, positions)
            val end = bounds(110f, positions)
            assertEquals(76f..105f, start)
            assertEquals(105f..190f, end)
            assertTrue(100f in start)
            assertTrue(110f in end)
        }
    }

    @Test
    fun middleMarker_remainsReachableWithNeighborsOnBothSides() {
        assertEquals(95f..105f, bounds(100f, listOf(110f, 90f, 100f, 500f)))
    }

    private fun bounds(x: Float, positions: List<Float>) =
        waveformMarkerHitBounds(x, 24f, 76f, 4f, positions)
}
