package com.prosincerity.ghostwriter.logic

import org.junit.Assert.assertEquals
import org.junit.Test

class BeatPanelStateTest {
    @Test
    fun awaitingApproval_hidesReadyControlsAndTakesPriorityOverLoading() {
        for (ready in listOf(false, true)) {
            assertEquals(
                BeatPanelState.AwaitingWaveformApproval,
                state(ready = ready, loading = true, awaitingApproval = true),
            )
        }
        assertEquals(BeatPanelState.Connecting, state(hasPlayer = false, awaitingApproval = true))
    }

    @Test
    fun missingPlayer_showsConnectionBeforeAnyBeatOrWaveformStatus() {
        assertEquals(BeatPanelState.Connecting, state(hasPlayer = false))
        assertEquals(
            BeatPanelState.Connecting,
            state(hasPlayer = false, ready = true, importing = true, removing = true,
                loading = true, cancelled = true, failed = true),
        )
    }

    @Test
    fun loading_showsPreparationBeforeCancellationFailureOrAssignedBeat() {
        for (ready in listOf(false, true)) {
            for (removeOnCancel in listOf(false, true)) {
                assertEquals(
                    BeatPanelState.PreparingWaveform(removeOnCancel),
                    state(ready = ready, loading = true, cancelled = true, failed = true,
                        removeOnCancel = removeOnCancel),
                )
            }
        }
    }

    @Test
    fun cancellation_showsRetryBeforeFailureOrAssignedBeat() {
        for (ready in listOf(false, true)) {
            assertEquals(BeatPanelState.WaveformCancelled, state(ready = ready, cancelled = true, failed = true))
        }
    }

    @Test
    fun failure_showsRecoveryActionsBeforeAssignedOrMissingBeat() {
        for (ready in listOf(false, true)) {
            assertEquals(BeatPanelState.WaveformFailed, state(ready = ready, failed = true))
        }
    }

    @Test
    fun readyBeat_showsWaveformDespiteImportOrRemovalFlags() {
        assertEquals(BeatPanelState.Waveform, state(ready = true))
        assertEquals(BeatPanelState.Waveform, state(ready = true, importing = true, removing = true))
    }

    @Test
    fun removal_takesPriorityOverImportForAnUnreadyBeat() {
        assertEquals(BeatPanelState.RemovingBeat, state(removing = true))
        assertEquals(BeatPanelState.RemovingBeat, state(removing = true, importing = true))
    }

    @Test
    fun importWithoutReadyBeat_showsImportInProgress() {
        assertEquals(BeatPanelState.ImportingBeat, state(importing = true))
    }

    @Test
    fun noBeatOrPendingWork_offersImport() {
        assertEquals(BeatPanelState.NoBeat, state())
    }

    private fun state(
        hasPlayer: Boolean = true,
        ready: Boolean = false,
        importing: Boolean = false,
        removing: Boolean = false,
        loading: Boolean = false,
        removeOnCancel: Boolean = false,
        cancelled: Boolean = false,
        failed: Boolean = false,
        awaitingApproval: Boolean = false,
    ): BeatPanelState = resolveBeatPanelState(
        hasPlayer = hasPlayer,
        isBeatReady = ready,
        isImporting = importing,
        isReassigningBeat = removing,
        isWaveformLoading = loading,
        cancelRemovesImportedBeat = removeOnCancel,
        waveformPreparationCancelled = cancelled,
        waveformPreparationFailed = failed,
        isAwaitingWaveformApproval = awaitingApproval,
    )
}
