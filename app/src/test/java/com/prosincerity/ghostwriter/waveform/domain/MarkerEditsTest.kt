package com.prosincerity.ghostwriter.waveform.domain

import com.prosincerity.ghostwriter.waveform.domain.MarkerLoopRole
import com.prosincerity.ghostwriter.waveform.domain.WaveformMarker
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class MarkerEditsTest {
    @Test fun addingToEmptyList_keepsLegacyPositionWhenSampleRateIsUnknown() {
        assertEquals(listOf(WaveformMarker("Intro", 0)),
            editMarkers(emptyList(), MarkerEdit.Add(0, "Intro", MarkerLoopRole.NONE), 0))
    }

    @Test fun addingLoopMarker_transfersOnlyItsRoleAndStoresFrames() {
        val start = WaveformMarker("Start", 500, MarkerLoopRole.START).withSampleRate(44100)
        val end = WaveformMarker("End", 2000, MarkerLoopRole.END).withSampleRate(44100)
        val original = listOf(start, end)

        assertEquals(listOf(start.copy(loopRole = MarkerLoopRole.NONE), end,
            WaveformMarker("New start", 1000, MarkerLoopRole.START, 44100, 44100)),
            editMarkers(original, MarkerEdit.Add(1000, "New start", MarkerLoopRole.START), 44100))
        assertEquals(listOf(start, end), original)
    }

    @Test fun addingOrdinaryMarker_preservesExistingLoopRoles() {
        val end = WaveformMarker("End", 2000, MarkerLoopRole.END)
        assertEquals(listOf(end, WaveformMarker("Verse", 1000)),
            editMarkers(listOf(end), MarkerEdit.Add(1000, "Verse", MarkerLoopRole.NONE), -1))
    }

    @Test fun updatingMigratedMarker_preservesCurrentFramesAndTransfersRole() {
        val selected = WaveformMarker("Verse", 1001)
        val current = selected.withSampleRate(44100)
        val oldStart = WaveformMarker("Start", 500, MarkerLoopRole.START).withSampleRate(44100)
        val end = WaveformMarker("End", 2000, MarkerLoopRole.END).withSampleRate(44100)

        assertEquals(listOf(oldStart.copy(loopRole = MarkerLoopRole.NONE),
            current.copy(label = "New start", loopRole = MarkerLoopRole.START), end),
            editMarkers(listOf(oldStart, current, end),
                MarkerEdit.Update(selected, "New start", MarkerLoopRole.START), 48000))
    }

    @Test fun clearingLoopRole_keepsOtherRolesAndExactPosition() {
        val selected = WaveformMarker("Start", 1000, MarkerLoopRole.START, 44101, 44100)
        val end = WaveformMarker("End", 2000, MarkerLoopRole.END)
        assertEquals(listOf(selected.copy(label = "Verse", loopRole = MarkerLoopRole.NONE), end),
            editMarkers(listOf(selected, end), MarkerEdit.Update(selected, "Verse", MarkerLoopRole.NONE), 44100))
    }

    @Test fun updatingEqualMarkers_changesOnlySelectedInstance() {
        val selected = WaveformMarker("Verse", 1000)
        val equal = selected.copy()
        assertEquals(listOf(equal, selected.copy(label = "Chorus")),
            editMarkers(listOf(equal, selected), MarkerEdit.Update(selected, "Chorus", MarkerLoopRole.NONE), 0))
    }

    @Test fun deletingEqualMarkers_removesOnlySelectedInstance() {
        val selected = WaveformMarker("Verse", 1000)
        val equal = selected.copy()
        val end = WaveformMarker("End", 2000, MarkerLoopRole.END)
        assertEquals(listOf(equal, end),
            editMarkers(listOf(equal, selected, end), MarkerEdit.Delete(selected), 0))
    }

    @Test fun deletingMigratedMarker_preservesOtherMarkers() {
        val selected = WaveformMarker("Start", 1000, MarkerLoopRole.START)
        val end = WaveformMarker("End", 2000, MarkerLoopRole.END).withSampleRate(44100)
        assertEquals(listOf(end), editMarkers(listOf(selected.withSampleRate(44100), end),
            MarkerEdit.Delete(selected), 44100))
    }

    @Test fun deletingOnlyMarker_returnsEmptyList() {
        val selected = WaveformMarker("Verse", 1000)
        assertEquals(emptyList<WaveformMarker>(),
            editMarkers(listOf(selected), MarkerEdit.Delete(selected), 0))
    }

    @Test fun staleOrAmbiguousSelection_doesNotUpdateDeleteOrMoveMarkers() {
        val selected = WaveformMarker("Verse", 1000)
        val migrated = selected.withSampleRate(44100)
        val currentLists = listOf(emptyList(), listOf(migrated.copy(label = "Newer edit")),
            listOf(migrated.atPositionMs(1500, 44100)),
            listOf(migrated.copy(loopRole = MarkerLoopRole.START)), listOf(migrated, migrated.copy()))

        for (current in currentLists) {
            assertNull(editMarkers(current, MarkerEdit.Update(selected, "Stale edit", MarkerLoopRole.END), 44100))
            assertNull(editMarkers(current, MarkerEdit.Delete(selected), 44100))
            assertNull(moveMarker(current, selected, 2000, 44100))
        }
    }

    @Test fun movingMarker_replacesFramePositionAndPreservesOtherMarkers() {
        val selected = WaveformMarker("Start", 1000, MarkerLoopRole.START, 44101, 44100)
        val end = WaveformMarker("End", 2000, MarkerLoopRole.END)
        val original = listOf(selected, end)

        assertEquals(listOf(WaveformMarker("Start", 1500, MarkerLoopRole.START, 72000, 48000), end),
            moveMarker(original, selected, 1500, 48000))
        assertEquals(listOf(selected, end), original)
    }

    @Test fun movingMarkerToSameMillisecond_keepsExactFramePosition() {
        val selected = WaveformMarker("Start", 1000, MarkerLoopRole.START, 44101, 44100)
        assertNull(moveMarker(listOf(selected), selected, 1000, 48000))
    }

    @Test fun movingMigratedSelectionWithoutSampleRate_keepsLegacyPosition() {
        val selected = WaveformMarker("Verse", 1000)
        val migrated = selected.withSampleRate(44100)
        assertEquals(listOf(WaveformMarker("Verse", 0)),
            moveMarker(listOf(migrated), selected, 0, 0))
    }
}
