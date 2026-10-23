package com.prosincerity.ghostwriter.waveform.domain

import org.junit.Assert.assertArrayEquals
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.io.File
import java.io.IOException
import java.util.concurrent.CancellationException

class WaveformPreparationTest {
    private val beat = File("fixture.wav")

    @Test
    fun cachedPeaksBypassDurationLookupAndDecoding() {
        val result = prepare(
            cached = intArrayOf(0, 8000, 32768),
            duration = { error("Cached beats must not read duration") },
            extract = { error("Cached beats must not decode") },
        ) as WaveformPreparationState.Ready

        assertArrayEquals(intArrayOf(0, 8000, 32768), result.amplitudes)
    }

    @Test
    fun longUncachedBeatRequiresApprovalBeforeDecoding() {
        for (imported in listOf(false, true)) {
            val result = prepare(
                imported = imported,
                duration = { 300_000L },
                extract = { error("Unapproved long beats must not decode") },
            )

            assertEquals(WaveformPreparationState.AwaitingApproval(beat, 300_000L, imported), result)
        }
    }

    @Test
    fun shortOrUnknownDurationAllowsDecoding() {
        for (durationMs in listOf(null, 0L, 299_999L)) {
            val result = prepare(duration = { durationMs }) as WaveformPreparationState.Ready

            assertArrayEquals(intArrayOf(123, 456), result.amplitudes)
        }
    }

    @Test
    fun approvalForThisBeatBypassesDurationLookup() {
        val result = prepare(
            approvedPath = beat.absolutePath,
            duration = { error("An approved beat needs no duration lookup") },
        ) as WaveformPreparationState.Ready

        assertArrayEquals(intArrayOf(123, 456), result.amplitudes)
    }

    @Test
    fun approvalForAnotherBeatStillRequiresApproval() {
        assertEquals(
            WaveformPreparationState.AwaitingApproval(beat, 600_000L, false),
            prepare(approvedPath = File("other.wav").absolutePath, duration = { 600_000L }),
        )
    }

    @Test
    fun emptyDecodedPeaksReportFailure() {
        assertEquals(WaveformPreparationState.Failed, prepare(extract = { IntArray(0) }))
    }

    @Test
    fun emptyCachedPeaksReportFailureWithoutDecoding() {
        assertEquals(
            WaveformPreparationState.Failed,
            prepare(cached = IntArray(0), extract = { error("A cache hit must not decode") }),
        )
    }

    @Test
    fun cancellationRejectsCachedAndDecodedResults() {
        for (cached in listOf(null, intArrayOf(123))) {
            assertThrows(CancellationException::class.java) {
                prepare(cached = cached, shouldCancel = { true })
            }
        }
    }

    @Test
    fun cancellationDuringDecodingRejectsTheCompletedPeaks() {
        var cancelled = false

        assertThrows(CancellationException::class.java) {
            prepare(
                extract = {
                    cancelled = true
                    intArrayOf(123)
                },
                shouldCancel = { cancelled },
            )
        }
    }

    @Test
    fun decoderCancellationPropagates() {
        val cancellation = CancellationException("Cancelled by decoder")

        val thrown = assertThrows(CancellationException::class.java) {
            prepare(extract = { throw cancellation })
        }

        assertEquals(cancellation, thrown)
    }

    @Test
    fun storageFailurePropagates() {
        val failure = IOException("Cache unavailable")

        val thrown = assertThrows(IOException::class.java) {
            prepareWaveform(
                beat, false, null,
                loadCached = { throw failure },
                readDurationMs = { null },
                extract = { error("A failed cache read must not decode") },
                shouldCancel = { false },
            )
        }

        assertEquals(failure, thrown)
    }

    @Test
    fun effectCleanupClearsOnlyAnUnfinishedLoadingState() {
        assertEquals(WaveformPreparationState.Idle, WaveformPreparationState.Loading.finish())
        val completedStates = listOf(
            WaveformPreparationState.Idle,
            WaveformPreparationState.AwaitingApproval(beat, 300_000L, true),
            WaveformPreparationState.Ready(intArrayOf(123)),
            WaveformPreparationState.Cancelled,
            WaveformPreparationState.Failed,
        )
        for (state in completedStates) assertEquals(state, state.finish())
    }

    private fun prepare(
        cached: IntArray? = null,
        imported: Boolean = false,
        approvedPath: String? = null,
        duration: () -> Long? = { null },
        extract: () -> IntArray = { intArrayOf(123, 456) },
        shouldCancel: () -> Boolean = { false },
    ): WaveformPreparationState = prepareWaveform(
        file = beat,
        removeBeatOnCancel = imported,
        approvedLongBeatPath = approvedPath,
        loadCached = { cached },
        readDurationMs = duration,
        extract = extract,
        shouldCancel = shouldCancel,
    )
}
