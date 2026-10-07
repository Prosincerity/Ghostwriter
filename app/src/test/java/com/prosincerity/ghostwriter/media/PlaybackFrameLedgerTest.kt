package com.prosincerity.ghostwriter.media

import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class PlaybackFrameLedgerTest {
    @Test fun queuedOldAudio_keepsItsPositionUntilNewLoopAudioIsConsumed() {
        val ledger = PlaybackFrameLedger(8, 10000)
        ledger.reset(500)
        ledger.record(0, 500)
        ledger.record(100, 200)
        assertEquals(599L, ledger.positionAt(99))
        assertEquals(200L, ledger.positionAt(100))
        ledger.record(200, 1000)
        assertEquals(299L, ledger.positionAt(199))
        assertEquals(1050L, ledger.positionAt(250))
    }

    @Test fun consumedPositions_freeCapacityAcrossWrapsAndSeekResetsTheLedger() {
        val ledger = PlaybackFrameLedger(4, 10000)
        repeat(10) { index ->
            ledger.record(index.toLong(), (index % 4).toLong())
            assertEquals((index % 4).toLong(), ledger.positionAt(index.toLong()))
        }
        ledger.reset(9000)
        assertEquals(9000L, ledger.positionAt(0))
        assertEquals(10000L, ledger.positionAt(2000))
    }

    @Test fun sameOutputFrame_replacesPendingPositionEvenWhenLedgerIsFull() {
        val ledger = PlaybackFrameLedger(2, 10000)
        ledger.record(0, 500)
        ledger.record(100, 800)
        ledger.record(100, 200)
        assertEquals(599L, ledger.positionAt(99))
        assertEquals(200L, ledger.positionAt(100))
    }

    @Test fun overflow_rejectsNewEventsWithoutCorruptingQueuedPositions() {
        val ledger = PlaybackFrameLedger(2, 10000)
        ledger.record(0, 500)
        ledger.record(100, 200)
        val failure = assertThrows(IllegalStateException::class.java) { ledger.record(200, 900) }
        assertEquals("Playback position ledger overflow", failure.message)
        assertEquals(500L, ledger.positionAt(0))
        ledger.record(200, 900)
        assertEquals(200L, ledger.positionAt(100))
        assertEquals(950L, ledger.positionAt(250))
    }

    @Test fun reset_discardsQueuedEventsAndClampsPositionsToPhysicalBeat() {
        val ledger = PlaybackFrameLedger(2, 1000)
        ledger.record(10, 800)
        ledger.reset(50)
        assertEquals(0L, ledger.positionAt(-100))
        assertEquals(70L, ledger.positionAt(20))
        assertEquals(1000L, ledger.positionAt(2000))
    }
}
