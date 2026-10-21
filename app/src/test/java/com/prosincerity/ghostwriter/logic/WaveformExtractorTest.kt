package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.concurrent.CancellationException

class WaveformExtractorTest {
    @Test
    fun decoderProgressGuard_requiresAPositiveStallLimit() {
        for (limit in listOf(0, -1)) {
            assertThrows(IllegalArgumentException::class.java) {
                WaveformExtractor.DecoderProgressGuard(limit)
            }
        }
    }


    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun extractAmplitudes_returnsEmptyForMissingFile() {
        val missing = File(tempFolder.root, "missing.wav")

        assertTrue(WaveformExtractor.extractAmplitudes(missing, 100).isEmpty())
        assertEquals(null, WaveformExtractor.durationMs(missing))
    }

    @Test
    fun extractAmplitudes_rejectsNonPositiveTargetSampleCounts() {
        val source = tempFolder.newFile("source.wav")

        assertTrue(WaveformExtractor.extractAmplitudes(source, 0).isEmpty())
        assertTrue(WaveformExtractor.extractAmplitudes(source, -1).isEmpty())
    }

    @Test
    fun extractAmplitudes_stopsBeforeDecodingWhenCancellationIsRequested() {
        val source = tempFolder.newFile("cancelled.wav").apply { writeText("audio") }

        assertThrows(CancellationException::class.java) {
            WaveformExtractor.extractAmplitudes(source, 100) { true }
        }
    }

    @Test
    fun decoderProgressGuard_resetsAfterProgressAndRejectsConsecutiveStalls() {
        val guard = WaveformExtractor.DecoderProgressGuard(maxConsecutiveStalls = 2)

        guard.record(madeProgress = false)
        guard.record(madeProgress = true)
        guard.record(madeProgress = false)
        assertThrows(IllegalStateException::class.java) {
            guard.record(madeProgress = false)
        }
    }
}
