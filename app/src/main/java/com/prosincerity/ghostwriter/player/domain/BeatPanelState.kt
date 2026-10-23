package com.prosincerity.ghostwriter.player.domain

/** The panel's main content; playback controls are shown only with the waveform. */
internal sealed interface BeatPanelState {
    data object Connecting : BeatPanelState
    data object AwaitingWaveformApproval : BeatPanelState
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
    isAwaitingWaveformApproval: Boolean = false,
): BeatPanelState = when {
    !hasPlayer -> BeatPanelState.Connecting
    isAwaitingWaveformApproval -> BeatPanelState.AwaitingWaveformApproval
    isWaveformLoading -> BeatPanelState.PreparingWaveform(cancelRemovesImportedBeat)
    waveformPreparationCancelled -> BeatPanelState.WaveformCancelled
    waveformPreparationFailed -> BeatPanelState.WaveformFailed
    isBeatReady -> BeatPanelState.Waveform
    isReassigningBeat -> BeatPanelState.RemovingBeat
    isImporting -> BeatPanelState.ImportingBeat
    else -> BeatPanelState.NoBeat
}
