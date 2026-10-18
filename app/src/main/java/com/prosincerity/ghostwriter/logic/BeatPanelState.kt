package com.prosincerity.ghostwriter.logic

/** The panel's main content; playback controls remain available independently. */
internal sealed interface BeatPanelState {
    data object Connecting : BeatPanelState
    data class PreparingWaveform(val cancelRemovesImportedBeat: Boolean) : BeatPanelState
    data object WaveformCancelled : BeatPanelState
    data object WaveformFailed : BeatPanelState
    data object NoBeat : BeatPanelState
    data object ImportingBeat : BeatPanelState
    data object RemovingBeat : BeatPanelState
    data object Waveform : BeatPanelState
}

/** Resolve transient flags at the UI boundary before rendering a single state. */
internal fun resolveBeatPanelState(
    hasPlayer: Boolean,
    isBeatReady: Boolean,
    isImporting: Boolean,
    isReassigningBeat: Boolean,
    isWaveformLoading: Boolean,
    cancelRemovesImportedBeat: Boolean,
    waveformPreparationCancelled: Boolean,
    waveformPreparationFailed: Boolean,
): BeatPanelState = when {
    !hasPlayer -> BeatPanelState.Connecting
    isWaveformLoading -> BeatPanelState.PreparingWaveform(cancelRemovesImportedBeat)
    waveformPreparationCancelled -> BeatPanelState.WaveformCancelled
    waveformPreparationFailed -> BeatPanelState.WaveformFailed
    isBeatReady -> BeatPanelState.Waveform
    isReassigningBeat -> BeatPanelState.RemovingBeat
    isImporting -> BeatPanelState.ImportingBeat
    else -> BeatPanelState.NoBeat
}
