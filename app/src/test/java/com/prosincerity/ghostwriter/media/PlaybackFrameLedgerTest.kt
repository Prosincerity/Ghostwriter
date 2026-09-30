package com.prosincerity.ghostwriter.media

import org.junit.Assert.assertEquals
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

    @Test fun consumedEvents_areReusedAndSeekResetsTheLedger() {
        val ledger = PlaybackFrameLedger(4, 10000)
        repeat(10000) { index ->
            ledger.record(index.toLong(), (index % 4).toLong())
            assertEquals((index % 4).toLong(), ledger.positionAt(index.toLong()))
        }
        ledger.reset(9000)
        assertEquals(9000L, ledger.positionAt(0))
        assertEquals(10000L, ledger.positionAt(2000))
    }
}
