package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.concurrent.CancellationException

class WaveformExtractorTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun downsampleFramePeaks_returnsOneMaximumPerEvenBucket() {
        val waveform = WaveformExtractor.downsampleFramePeaks(
            framePeaks = intArrayOf(0, 5, 20, 2, 1, 7, 3, 9),
            targetSampleCount = 4,
        )

        assertArrayEquals(intArrayOf(5, 20, 7, 9), waveform)
    }

    @Test
    fun downsampleFramePeaks_preservesTheRequestedOutputLength() {
        val waveform = WaveformExtractor.downsampleFramePeaks(
            framePeaks = intArrayOf(11, 22),
            targetSampleCount = 4,
        )

        assertEquals(4, waveform.size)
        assertArrayEquals(intArrayOf(11, 0, 22, 0), waveform)
    }

    @Test
    fun downsampleFramePeaks_returnsEmptyForEmptyInputOrNonPositiveTarget() {
        assertTrue(WaveformExtractor.downsampleFramePeaks(IntArray(0), 10).isEmpty())
        assertTrue(WaveformExtractor.downsampleFramePeaks(intArrayOf(1), 0).isEmpty())
    }

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
    fun extractAmplitudes_returnsEmptyForCorruptFileWithoutThrowing() {
        val corrupt = tempFolder.newFile("corrupt.wav").apply { writeText("not audio") }

        assertTrue(WaveformExtractor.extractAmplitudes(corrupt, 100).isEmpty())
    }

    @Test(expected = CancellationException::class)
    fun extractAmplitudes_stopsBeforeDecodingWhenCancellationIsRequested() {
        val source = tempFolder.newFile("cancelled.wav").apply { writeText("audio") }

        WaveformExtractor.extractAmplitudes(source, 100) { true }
    }
}
