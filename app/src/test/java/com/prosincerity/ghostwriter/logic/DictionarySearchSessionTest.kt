package com.prosincerity.ghostwriter.logic

import com.prosincerity.ghostwriter.data.DictionaryMatch
import com.prosincerity.ghostwriter.data.DictionarySearchResult
import com.prosincerity.ghostwriter.data.PronunciationSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test
import kotlin.coroutines.Continuation
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class DictionarySearchSessionTest {
    private val dispatcher = QueuedDispatcher()
    private val owner = Job()
    private val session = DictionarySearchSession(CoroutineScope(owner + dispatcher))
    private val pending = ArrayDeque<Continuation<DictionarySearchResult>>()
    private val found = result("current result")

    @After
    fun finishRequests() {
        owner.cancel()
        while (pending.isNotEmpty()) pending.removeFirst().resume(DictionarySearchResult(null, emptyList()))
        dispatcher.runPending()
    }

    @Test
    fun pendingSearch_staysLoadingUntilItPublishesTheRequestedPage() {
        session.submit(2) { awaitResult() }
        assertEquals(DictionarySearchStatus.Loading, session.state.value.status)
        dispatcher.runPending()
        assertEquals(DictionarySearchStatus.Loading, session.state.value.status)

        pending.removeFirst().resume(found)
        dispatcher.runPending()

        assertEquals(DictionarySearchStatus.Ready(found, 2), session.state.value.status)
    }

    @Test
    fun failedSearch_canBeReplacedByASuccessfulRetry() {
        session.submit { error("Lookup failed") }
        dispatcher.runPending()
        assertEquals(DictionarySearchStatus.Failed("Lookup failed"), session.state.value.status)

        session.submit { found }
        assertEquals(DictionarySearchStatus.Loading, session.state.value.status)
        dispatcher.runPending()

        assertEquals(DictionarySearchStatus.Ready(found, 0), session.state.value.status)
    }

    @Test
    fun cancelledSearch_finishesWithoutReportingAnErrorOrCancellingTheOwner() {
        session.submit { throw CancellationException("Lookup cancelled") }
        val request = owner.children.single()

        dispatcher.runPending()

        assertTrue(request.isCancelled)
        assertTrue(owner.isActive)
        assertEquals(DictionarySearchStatus.Idle, session.state.value.status)
    }

    @Test
    fun resetBeforeSearchStarts_preventsTheLookupFromRunning() {
        var searched = false
        session.submit { searched = true; found }

        session.reset()
        dispatcher.runPending()

        assertFalse(searched)
        assertEquals(DictionarySearchStatus.Idle, session.state.value.status)
    }

    @Test
    fun resetDuringSearch_cancelsPendingWorkAndClearsLoading() {
        var cancelled = false
        session.submit {
            try {
                awaitCancellation()
            } finally {
                cancelled = true
            }
        }
        dispatcher.runPending()

        session.reset()
        dispatcher.runPending()

        assertTrue(cancelled)
        assertEquals(DictionarySearchStatus.Idle, session.state.value.status)
    }

    @Test
    fun reset_rejectsALateResultFromAnUncooperativeLookup() {
        session.submit { awaitResult() }
        dispatcher.runPending()

        session.reset()
        pending.removeFirst().resume(found)
        dispatcher.runPending()

        assertEquals(DictionarySearchStatus.Idle, session.state.value.status)
    }

    @Test
    fun replacementSearch_cancelsTheOldRequestAndRejectsItsLateResult() {
        session.submit(2) { awaitResult() }
        dispatcher.runPending()
        val oldRequest = owner.children.single()

        session.submit(1) { awaitResult() }
        dispatcher.runPending()
        assertTrue(oldRequest.isCancelled)

        pending.removeFirst().resume(result("stale result"))
        dispatcher.runPending()
        assertEquals(DictionarySearchStatus.Loading, session.state.value.status)

        pending.removeFirst().resume(found)
        dispatcher.runPending()
        assertEquals(DictionarySearchStatus.Ready(found, 1), session.state.value.status)
    }

    @Test
    fun supersededFailure_cannotReplaceTheNewerResultOrPage() {
        session.submit(2) { awaitResult() }
        dispatcher.runPending()
        session.submit(1) { found }
        dispatcher.runPending()

        pending.removeFirst().resumeWithException(IllegalStateException("Stale failure"))
        dispatcher.runPending()

        assertEquals(DictionarySearchStatus.Ready(found, 1), session.state.value.status)
    }

    @Test
    fun cancellingTheOwner_releasesPendingSearchWork() {
        var cancelled = false
        session.submit {
            try {
                awaitCancellation()
            } finally {
                cancelled = true
            }
        }
        dispatcher.runPending()

        owner.cancel()
        dispatcher.runPending()

        assertTrue(cancelled)
        assertTrue(owner.isCompleted)
        assertEquals(DictionarySearchStatus.Idle, session.state.value.status)
    }

    // Deliberately ignores cancellation, as a blocking lookup can do.
    private suspend fun awaitResult(): DictionarySearchResult = suspendCoroutine { pending.addLast(it) }

    private fun result(word: String) = DictionarySearchResult(
        null, listOf(DictionaryMatch(word, "/æt/", PronunciationSource.WIKTIONARY)),
    )

    private class QueuedDispatcher : CoroutineDispatcher() {
        private val tasks = ArrayDeque<Runnable>()

        override fun dispatch(context: CoroutineContext, block: Runnable) {
            tasks.addLast(block)
        }

        fun runPending() {
            while (tasks.isNotEmpty()) tasks.removeFirst().run()
        }
    }
}
