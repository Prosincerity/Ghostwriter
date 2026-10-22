package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class Mp3FrameLayoutTest {
    @Test fun mpeg1PacketsHave1152FramesIncludingPadding() {
        assertEquals(1152, Mp3FrameLayout.samplesPerPacket(header(3, 11), 626, 44_100))
        assertEquals(1152, Mp3FrameLayout.samplesPerPacket(header(3, 11, padding = 1), 627, 44_100))
        assertEquals(1152, Mp3FrameLayout.samplesPerPacket(header(3, 11, rate = 1), 576, 48_000))
    }

    @Test fun lowerMpegVersionsHave576Frames() {
        assertEquals(576, Mp3FrameLayout.samplesPerPacket(header(2, 8), 208, 22_050))
        assertEquals(576, Mp3FrameLayout.samplesPerPacket(header(0, 8), 417, 11_025))
        assertEquals(576, Mp3FrameLayout.samplesPerPacket(header(2, 8, padding = 1), 209, 22_050))
    }

    @Test fun truncatedAndCombinedPacketsAreRejected() {
        for (size in listOf(0, 625, 627, 1252)) {
            assertNull(Mp3FrameLayout.samplesPerPacket(header(3, 11), size, 44_100))
        }
    }

    @Test fun mismatchedSampleRateIsRejectedForFallback() {
        assertNull(Mp3FrameLayout.samplesPerPacket(header(3, 11), 626, 48_000))
        assertNull(Mp3FrameLayout.samplesPerPacket(header(3, 11), 626, 0))
    }

    @Test fun unsupportedOrInvalidHeadersAreRejectedForFallback() {
        for (invalid in listOf(
            header(1, 11), header(3, 0), header(3, 15), header(3, 11, rate = 3),
            header(3, 11) and (1 shl 21).inv(),
            header(3, 11) and (3 shl 17).inv(),
            header(3, 11) or (3 shl 17),
        )) {
            assertNull(Mp3FrameLayout.samplesPerPacket(invalid, 626, 44_100))
        }
    }

    private fun header(version: Int, bitrate: Int, rate: Int = 0, padding: Int = 0): Int =
        0xffe00000.toInt() or (version shl 19) or (1 shl 17) or (1 shl 16) or
            (bitrate shl 12) or (rate shl 10) or (padding shl 9)
}
