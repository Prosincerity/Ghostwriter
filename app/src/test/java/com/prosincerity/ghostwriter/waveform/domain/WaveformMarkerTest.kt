package com.prosincerity.ghostwriter.waveform.domain

import org.junit.Assert.assertNull
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class WaveformMarkerTest {

    @Test
    fun legacyMarkers_migrateAndDraggingUpdatesTheAuthoritativeFrame() {
        val legacy = WaveformMarker("End", 1001, MarkerLoopRole.END)
        val migrated = legacy.withSampleRate(44100)
        assertEquals(44144L, migrated.frameIndex)
        assertEquals(1001L, migrated.positionMs)
        assertEquals(48047L, migrated.withSampleRate(48000).frameIndex)
        val moved = migrated.atPositionMs(2000, 44100)
        assertEquals(88200L, moved.frameIndex)
        assertEquals(MarkerLoopRole.END, moved.loopRole)
    }

    @Test
    fun framePositions_rejectInvalidTargetRatesAndFallBackForIncompleteSavedFrames() {
        val marker = WaveformMarker("Start", 1500, MarkerLoopRole.START)
        val precise = marker.withSampleRate(48000)
        for (rate in listOf(0, -1)) {
            assertEquals(precise, precise.withSampleRate(rate))
            assertThrows(IllegalArgumentException::class.java) { marker.frameAt(rate) }
        }
        for (incomplete in listOf(
            marker.copy(frameIndex = 99),
            marker.copy(sampleRate = 44100),
            marker.copy(frameIndex = 99, sampleRate = 0),
            marker.copy(frameIndex = 99, sampleRate = -1),
        )) {
            assertEquals(72000L, incomplete.frameAt(48000))
            val normalized = incomplete.withSampleRate(48000)
            assertEquals(72000L, normalized.frameIndex)
            assertEquals(1500L, normalized.positionMs)
            assertEquals(MarkerLoopRole.START, normalized.loopRole)
        }
    }

    @Test
    fun frameNormalization_repairsStaleMillisecondsWithoutChangingExactFrames() {
        val marker = WaveformMarker("End", 99, MarkerLoopRole.END, 48001, 48000)
        val normalized = marker.withSampleRate(48000)
        assertEquals(1000L, normalized.positionMs)
        assertEquals(48001L, normalized.frameIndex)
        assertEquals(normalized, normalized.withSampleRate(48000))
        val movedWithoutRate = normalized.atPositionMs(2000, 0)
        assertEquals(2000L, movedWithoutRate.positionMs)
        assertNull(movedWithoutRate.frameIndex)
        assertNull(movedWithoutRate.sampleRate)
    }

}
