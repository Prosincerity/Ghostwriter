package com.prosincerity.ghostwriter.logic

import com.prosincerity.ghostwriter.data.MarkerLoopRole
import com.prosincerity.ghostwriter.data.WaveformMarker
import org.junit.Assert.*
import org.junit.Test

class MarkerLoopTest {
    @Test fun durationPrecision_acceptsStartInFinalFractionalMillisecond() {
        val marker = WaveformMarker("Start", 1000, MarkerLoopRole.START, 48010, 48000)
        assertEquals(MarkerLoopFrames(48010, 48024),
            MarkerLoopFrames.fromDurationUs(listOf(marker), 48000, 1_000_500))
    }

    @Test fun durationPrecision_missingEndRetainsFinalFramesAt44100Hz() {
        val marker = WaveformMarker("Start", 10, MarkerLoopRole.START, 441, 44100)
        val durationUs = 800 * 1_000_000L / 44100
        assertEquals(MarkerLoopFrames(441, 800),
            MarkerLoopFrames.fromDurationUs(listOf(marker), 44100, durationUs))
    }

    @Test fun selectedMarker_canStillBeEditedOrDeletedAfterFrameMigration() {
        val selected = start(1000)
        val migrated = listOf(selected.withSampleRate(44100), end(2000).withSampleRate(44100))
        assertEquals(0, indexOfSelectedMarker(migrated, selected))
    }

    @Test fun selectedMarker_canStillBeResolvedAfterSampleRateNormalization() {
        val selected = WaveformMarker("Start", 1001, MarkerLoopRole.START, 44144, 44100)
        assertEquals(0, indexOfSelectedMarker(listOf(selected.withSampleRate(48000)), selected))
    }

    @Test fun selectedMarker_identityTakesPriorityAndAmbiguousCopiesAreNotEdited() {
        val selected = start(1000)
        val normalized = selected.withSampleRate(44100)
        assertEquals(1, indexOfSelectedMarker(listOf(normalized, selected), selected))
        assertEquals(-1, indexOfSelectedMarker(listOf(normalized, normalized.copy()), selected))
    }

    @Test fun selectedMarker_recoveryDoesNotOverwriteNewerEditsOrResurrectDeletedMarkers() {
        val selected = start(1000)
        val normalized = selected.withSampleRate(44100)
        for (changed in listOf(normalized.copy(label = "Renamed"),
            normalized.copy(loopRole = MarkerLoopRole.NONE), normalized.atPositionMs(1100, 44100))) {
            assertEquals(-1, indexOfSelectedMarker(listOf(changed), selected))
        }
        assertEquals(-1, indexOfSelectedMarker(emptyList(), selected))
    }

    @Test fun durationPrecision_rejectsEmptyReversedAndPhysicalEndStarts() {
        val duration = 1_000_500L
        for (markers in listOf(listOf(start(-1)), listOf(start(500), end(400)),
            listOf(WaveformMarker("Start", 1001, MarkerLoopRole.START, 48024, 48000)))) {
            assertNull(MarkerLoopFrames.fromDurationUs(markers, 48000, duration))
        }
        assertNull(MarkerLoopFrames.fromDurationUs(listOf(start(0)), 0, duration))
        assertNull(MarkerLoopFrames.fromDurationUs(listOf(start(0)), 48000, 0))
        assertNull(MarkerLoopFrames.fromDurationUs(listOf(start(0)), 48000, -1))
    }

    @Test fun editingMigratedStart_transfersRoleWithoutLosingFramesOrOtherMarkerUpdates() {
        val selected = WaveformMarker("Verse", 1000)
        val current = listOf(start(500).withSampleRate(44100), selected.withSampleRate(44100),
            end(2500).withSampleRate(44100))
        val index = indexOfSelectedMarker(current, selected)
        val edited = replaceLoopMarker(current, index,
            current[index].copy(label = "Loop start", loopRole = MarkerLoopRole.START))
        assertEquals(MarkerLoopRole.NONE, edited[0].loopRole)
        assertEquals(44100L, edited[1].frameIndex)
        assertEquals(current[2], edited[2])
        assertEquals(MarkerLoopFrames(44100, 110250), MarkerLoopFrames.fromMarkers(edited, 44100, 132300))
    }

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

    @Test fun frameRange_rejectsInvalidAudioDimensionsAndMissingRoles() {
        for (rate in listOf(0, -1)) {
            assertNull(MarkerLoopFrames.fromMarkers(listOf(start(0)), rate, 48000))
        }
        for (frames in listOf(0L, -1L)) {
            assertNull(MarkerLoopFrames.fromMarkers(listOf(start(0)), 48000, frames))
        }
        assertNull(MarkerLoopFrames.fromMarkers(emptyList(), 48000, 48000))
        assertNull(MarkerLoopFrames.fromMarkers(listOf(WaveformMarker("Verse", 10)), 48000, 48000))
    }

    @Test fun frameRange_rejectsReversedNegativeAndPhysicalEndStarts() {
        for (markers in listOf(
            listOf(start(-1), end(100)), listOf(start(100), end(50)),
            listOf(start(1000)), listOf(end(0)), listOf(end(-1)),
        )) {
            assertNull(MarkerLoopFrames.fromMarkers(markers, 48000, 48000))
        }
    }

    @Test fun frameRange_preservesSubMillisecondLoopsAndRescalesSavedFrames() {
        val start = WaveformMarker("Start", 1000, MarkerLoopRole.START, 48001, 48000)
        val end = WaveformMarker("End", 1000, MarkerLoopRole.END, 48002, 48000)
        assertEquals(MarkerLoopFrames(48001, 48002), MarkerLoopFrames.fromMarkers(listOf(start, end), 48000, 96000))
        assertEquals(MarkerLoopFrames(44100, 44101), MarkerLoopFrames.fromMarkers(listOf(start, end), 44100, 88200))
    }

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
