package com.prosincerity.ghostwriter.dictionary.domain

import com.prosincerity.ghostwriter.dictionary.data.DictionaryDownloadProgress
import com.prosincerity.ghostwriter.dictionary.data.DictionarySource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Test

class DictionaryDownloadStateTest {
    private val key = "en" to DictionarySource.WIKTIONARY

    @Test fun newDownload_hasNoProgressMeasurement() {
        val state = DictionaryDownloadState.Downloading(key)
        assertEquals(key, state.key)
        assertNull(state.fraction)
        assertNull(state.percent)
    }

    @Test fun progressUpdate_keepsTheActiveSourceAndLeavesPreviousStateUnchanged() {
        val started = DictionaryDownloadState.Downloading(key)
        val progress = DictionaryDownloadProgress(25, 100)
        assertEquals(DictionaryDownloadState.Downloading(key, progress), started.withProgress(progress))
        assertNull(started.progress)
    }

    @Test fun knownTotal_exposesFractionAndTruncatedPercentage() {
        val state = DictionaryDownloadState.Downloading(key, DictionaryDownloadProgress(1, 3))
        assertEquals(1f / 3f, checkNotNull(state.fraction), 0.0001f)
        assertEquals(33, state.percent)
    }

    @Test fun unknownOrInvalidTotal_keepsProgressIndeterminate() {
        for (total in listOf(0L, -1L)) {
            val state = DictionaryDownloadState.Downloading(key, DictionaryDownloadProgress(10, total))
            assertNull(state.fraction)
            assertNull(state.percent)
        }
    }

    @Test fun noBytesDownloaded_reportsZeroProgress() {
        val state = DictionaryDownloadState.Downloading(key, DictionaryDownloadProgress(0, 100))
        assertEquals(0f, checkNotNull(state.fraction), 0f)
        assertEquals(0, state.percent)
    }

    @Test fun completedDownload_reportsFullProgress() {
        val state = DictionaryDownloadState.Downloading(key, DictionaryDownloadProgress(100, 100))
        assertEquals(1f, checkNotNull(state.fraction), 0f)
        assertEquals(100, state.percent)
    }

    @Test fun excessDownloadedBytes_clampProgressToFull() {
        val state = DictionaryDownloadState.Downloading(key, DictionaryDownloadProgress(150, 100))
        assertEquals(1f, checkNotNull(state.fraction), 0f)
        assertEquals(100, state.percent)
    }

    @Test fun negativeDownloadedBytes_clampProgressToZero() {
        val state = DictionaryDownloadState.Downloading(key, DictionaryDownloadProgress(-10, 100))
        assertEquals(0f, checkNotNull(state.fraction), 0f)
        assertEquals(0, state.percent)
    }

    @Test fun failure_keepsSourceAndReason() {
        val started = DictionaryDownloadState.Downloading(key, DictionaryDownloadProgress(25, 100))
        assertEquals(DictionaryDownloadState.Failed(key, "Invalid dictionary"), started.fail("Invalid dictionary"))
    }

    @Test fun failureWithoutMessage_usesUnknownError() {
        assertEquals(DictionaryDownloadState.Failed(key, "unknown error"),
            DictionaryDownloadState.Downloading(key).fail(null))
    }

    @Test fun failureWithEmptyMessage_preservesTheMessage() {
        assertEquals(DictionaryDownloadState.Failed(key, ""), DictionaryDownloadState.Downloading(key).fail(""))
    }

    @Test fun finishingDownload_clearsActiveSourceAndProgress() {
        val state = DictionaryDownloadState.Downloading(key, DictionaryDownloadProgress(25, 100))
        assertEquals(DictionaryDownloadState.Idle, state.finish())
    }

    @Test fun finishingFailedDownload_keepsErrorForRetry() {
        val failed = DictionaryDownloadState.Failed(key, "Invalid dictionary")
        assertEquals(failed, failed.finish())
    }

    @Test fun idleState_ignoresProgressAndFailureFromAnInactiveDownload() {
        val idle = DictionaryDownloadState.Idle
        assertEquals(idle, idle.withProgress(DictionaryDownloadProgress(25, 100)))
        assertEquals(idle, idle.fail("Late failure"))
        assertEquals(idle, idle.finish())
    }

    @Test fun failedState_keepsItsOriginalErrorWhenInactiveCallbacksArrive() {
        val failed = DictionaryDownloadState.Failed(key, "Invalid dictionary")
        assertEquals(failed, failed.withProgress(DictionaryDownloadProgress(25, 100)))
        assertEquals(failed, failed.fail("Late failure"))
    }
}
