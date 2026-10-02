package com.prosincerity.ghostwriter.media

import org.junit.Assert.*
import org.junit.Test

class PlaybackHandoffTest {
    @Test fun plan_advancesFromLivePositionAndAllowsTimeToBuffer() {
        val plan = PlaybackHandoff.plan(4200, 10000, true, 1000)
        assertEquals(4350, plan.positionMs)
        assertEquals(1150L, plan.startAtMs)
        assertFalse(plan.isAligned(4350, 10000, true, 1149))
        assertTrue(plan.isAligned(4350, 10000, true, 1150))
        assertTrue(plan.isAligned(4360, 10000, true, 1160))
    }

    @Test fun lateOrStaleAudio_requiresRepriming() {
        val plan = PlaybackHandoff.plan(4200, 10000, true, 1000)
        assertFalse(plan.isAligned(4350, 10000, true, 1171))
        assertFalse(plan.isAligned(4200, 10000, true, 1150))
        assertFalse(plan.isAligned(9000, 10000, true, 1150))
        val retry = PlaybackHandoff.plan(9000, 10000, true, 1200)
        assertTrue(retry.isAligned(9150, 10000, true, 1350))
    }

    @Test fun fullBeatWrap_keepsPositionAndAlignmentAcrossZero() {
        val plan = PlaybackHandoff.plan(9900, 10000, true, 1000)
        assertEquals(50, plan.positionMs)
        assertTrue(plan.isAligned(55, 10000, true, 1155))
        val nearEnd = PlaybackHandoff.plan(9840, 10000, true, 1000)
        assertTrue(nearEnd.isAligned(5, 10000, true, 1150))
        assertFalse(nearEnd.isAligned(100, 10000, true, 1150))
    }

    @Test fun nonloopingTail_clampsAtEndWithoutWrapping() {
        val plan = PlaybackHandoff.plan(9900, 10000, false, 1000)
        assertEquals(10000, plan.positionMs)
        assertTrue(plan.isAligned(10000, 10000, false, 1150))
        assertFalse(plan.isAligned(0, 10000, false, 1150))
        assertEquals(0, PlaybackHandoff.plan(0, 0, false, 1000).positionMs)
    }
}
