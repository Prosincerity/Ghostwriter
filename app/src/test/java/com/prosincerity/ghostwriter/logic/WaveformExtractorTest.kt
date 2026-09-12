package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.concurrent.CancellationException

class WaveformExtractorTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun bucketIndexForTimestamp_mapsTimestampsToEvenBuckets() {
        assertEquals(0, WaveformExtractor.bucketIndexForTimestamp(0L, 1_000L, 4))
        assertEquals(0, WaveformExtractor.bucketIndexForTimestamp(249L, 1_000L, 4))
        assertEquals(1, WaveformExtractor.bucketIndexForTimestamp(250L, 1_000L, 4))
        assertEquals(3, WaveformExtractor.bucketIndexForTimestamp(999L, 1_000L, 4))
    }

    @Test
    fun bucketIndexForTimestamp_clampsTimestampsToTheWaveformBounds() {
        assertEquals(0, WaveformExtractor.bucketIndexForTimestamp(-1L, 1_000L, 4))
        assertEquals(3, WaveformExtractor.bucketIndexForTimestamp(1_000L, 1_000L, 4))
        assertEquals(3, WaveformExtractor.bucketIndexForTimestamp(2_000L, 1_000L, 4))
    }

    @Test
    fun bucketIndexForTimestamp_rejectsInvalidDimensions() {
        assertEquals(null, WaveformExtractor.bucketIndexForTimestamp(0L, 0L, 4))
        assertEquals(null, WaveformExtractor.bucketIndexForTimestamp(0L, 1_000L, 0))
    }

    @Test
    fun extractAmplitudes_returnsEmptyForMissingFile() {
        val missing = File(tempFolder.root, "missing.wav")

        assertTrue(WaveformExtractor.extractAmplitudes(missing, 100).isEmpty())
    }

    @Test
    fun extractAmplitudes_rejectsNonPositiveTargetSampleCounts() {
        val source = tempFolder.newFile("source.wav")

        assertTrue(WaveformExtractor.extractAmplitudes(source, 0).isEmpty())
        assertTrue(WaveformExtractor.extractAmplitudes(source, -1).isEmpty())
    }

    @Test
    fun extractAmplitudes_returnsEmptyForCorruptFileWithoutThrowing() {
        val corrupt = tempFolder.newFile("corrupt.wav").apply { writeText("not audio") }

        assertTrue(WaveformExtractor.extractAmplitudes(corrupt, 100).isEmpty())
    }

    @Test(expected = CancellationException::class)
    fun extractAmplitudes_stopsBeforeDecodingWhenCancellationIsRequested() {
        val source = tempFolder.newFile("cancelled.wav").apply { writeText("audio") }

        WaveformExtractor.extractAmplitudes(source, 100) { true }
    }

    @Test
    fun decoderProgressGuard_resetsItsStallCountAfterProgress() {
        val guard = WaveformExtractor.DecoderProgressGuard(maxConsecutiveStalls = 2)

        guard.record(madeProgress = false)
        guard.record(madeProgress = true)
        guard.record(madeProgress = false)
    }

    @Test
    fun decoderProgressGuard_rejectsAStalledDecoder() {
        val guard = WaveformExtractor.DecoderProgressGuard(maxConsecutiveStalls = 2)

        guard.record(madeProgress = false)
        try {
            guard.record(madeProgress = false)
            fail("Expected a stalled decoder to be rejected")
        } catch (expected: IllegalStateException) {
            assertEquals("Audio decoder stopped making progress", expected.message)
        }
    }
}
