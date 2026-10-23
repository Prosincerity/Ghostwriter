package com.prosincerity.ghostwriter.waveform.domain

import java.io.File
import java.util.concurrent.CancellationException

private const val LONG_BEAT_WARNING_MS = 5 * 60 * 1_000L

internal sealed interface WaveformPreparationState {
    data object Idle : WaveformPreparationState
    data object Loading : WaveformPreparationState
    data class AwaitingApproval(
        val file: File,
        val durationMs: Long,
        val removeBeatOnCancel: Boolean,
    ) : WaveformPreparationState

    /** Owns the cache/decoder output, which callers treat as read-only. */
    class Ready(val amplitudes: IntArray) : WaveformPreparationState
    data object Cancelled : WaveformPreparationState
    data object Failed : WaveformPreparationState

    /** Effect cleanup must preserve warnings, results, and explicit cancellation. */
    fun finish(): WaveformPreparationState = if (this is Loading) Idle else this
}

/**
 * Select cached peaks, request long-beat approval, or decode once. Dependencies
 * are blocking; the caller owns background dispatch and cancellation lifetime.
 */
internal fun prepareWaveform(
    file: File,
    removeBeatOnCancel: Boolean,
    approvedLongBeatPath: String?,
    loadCached: () -> IntArray?,
    readDurationMs: () -> Long?,
    extract: () -> IntArray,
    shouldCancel: () -> Boolean,
): WaveformPreparationState {
    val cached = loadCached()
    if (cached == null && approvedLongBeatPath != file.absolutePath) {
        val durationMs = readDurationMs()
        if (durationMs != null && shouldWarnBeforeWaveformExtraction(durationMs)) {
            return WaveformPreparationState.AwaitingApproval(file, durationMs, removeBeatOnCancel)
        }
    }

    val amplitudes = cached ?: extract()
    if (shouldCancel()) throw CancellationException()
    return if (amplitudes.isEmpty()) WaveformPreparationState.Failed
    else WaveformPreparationState.Ready(amplitudes)
}

internal fun shouldWarnBeforeWaveformExtraction(durationMs: Long?): Boolean =
    durationMs != null && durationMs >= LONG_BEAT_WARNING_MS
