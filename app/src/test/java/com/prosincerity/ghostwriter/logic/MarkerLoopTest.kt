package com.prosincerity.ghostwriter.logic

import com.prosincerity.ghostwriter.data.MarkerLoopRole
import com.prosincerity.ghostwriter.data.WaveformMarker
import org.junit.Assert.*
import org.junit.Test

class MarkerLoopTest {
    @Test fun frameRange_prefersExactIndicesAndSuppliesMissingBoundaries() {
        val first = WaveformMarker("Start", 1000, MarkerLoopRole.START, 44101, 44100)
        val last = WaveformMarker("End", 2000, MarkerLoopRole.END, 88201, 44100)
        assertEquals(MarkerLoopFrames(44101, 88201), MarkerLoopFrames.fromMarkers(listOf(first, last), 44100, 132300))
        assertEquals(MarkerLoopFrames(0, 88201), MarkerLoopFrames.fromMarkers(listOf(last), 44100, 132300))
        assertEquals(MarkerLoopFrames(44101, 132300), MarkerLoopFrames.fromMarkers(listOf(first), 44100, 132300))
        assertEquals(MarkerLoopFrames(44101, 132300), MarkerLoopFrames.fromMarkers(
            listOf(first, last.copy(frameIndex = 140000)), 44100, 132300,
        ))
        assertNull(MarkerLoopFrames.fromMarkers(listOf(first, last.copy(frameIndex = first.frameIndex)), 44100, 132300))
    }
    private fun start(position: Long) = WaveformMarker("Start", position, MarkerLoopRole.START)
    private fun end(position: Long) = WaveformMarker("End", position, MarkerLoopRole.END)

    @Test fun range_usesExplicitBoundariesAndIgnoresOrdinaryMarkers() {
        assertEquals(MarkerLoopRange(2000, 6000), MarkerLoopRange.fromMarkers(
            listOf(end(6000), WaveformMarker("Verse", 1000), start(2000)), 10000,
        ))
    }

    @Test fun range_missingStartUsesZeroAndMissingEndUsesBeatDuration() {
        assertEquals(MarkerLoopRange(0, 6000), MarkerLoopRange.fromMarkers(listOf(end(6000)), 10000))
        assertEquals(MarkerLoopRange(2000, 10000), MarkerLoopRange.fromMarkers(listOf(start(2000)), 10000))
        assertNull(MarkerLoopRange.fromMarkers(listOf(WaveformMarker("Verse", 1000)), 10000))
    }

    @Test fun range_rejectsInvalidAndOutOfBoundsPositions() {
        for (markers in listOf(listOf(start(6000), end(2000)), listOf(start(2000), end(2000)),
            listOf(end(0)), listOf(start(10000)), listOf(start(-1)), listOf(end(10001)))) {
            assertNull(MarkerLoopRange.fromMarkers(markers, 10000))
        }
        assertNull(MarkerLoopRange.fromMarkers(listOf(start(0)), 0))
    }

    @Test fun assigningRole_transfersOnlyThatRoleAndRetainsOtherMarkerData() {
        val markers = listOf(start(2000), end(6000), WaveformMarker("Verse", 1000))
        val updated = replaceLoopMarker(markers, 2, markers[2].copy(loopRole = MarkerLoopRole.START))
        assertEquals(listOf(markers[0].copy(loopRole = MarkerLoopRole.NONE), markers[1],
            markers[2].copy(loopRole = MarkerLoopRole.START)), updated)
        assertEquals(MarkerLoopRole.START, markers[0].loopRole)
        val cleared = replaceLoopMarker(updated, 2, updated[2].copy(loopRole = MarkerLoopRole.NONE))
        assertEquals(MarkerLoopRole.END, cleared[1].loopRole)
    }
}
