package com.prosincerity.ghostwriter.logic

import com.prosincerity.ghostwriter.data.DictionaryDownloadProgress
import com.prosincerity.ghostwriter.data.DictionarySource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.joinAll
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertSame
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.IOException

class DictionaryDownloadSessionTest {
    private val key = "en" to DictionarySource.WIKTIONARY
    private val progress = DictionaryDownloadProgress(25, 100)

    @Test
    fun workerProgressIsPublishedAndSuccessfulInstallationReturnsToTheOwner() = withSession { session, owner ->
        val reportedProgress = CompletableDeferred<Unit>()
        val finishInstallation = CompletableDeferred<Unit>()
        val ownerThread = Thread.currentThread()
        var completionThread: Thread? = null
        session.start(key, onInstalled = { completionThread = Thread.currentThread() }) { report ->
            withContext(Dispatchers.Default) {
                report(progress)
                reportedProgress.complete(Unit)
                finishInstallation.await()
            }
        }
        assertEquals(DictionaryDownloadState.Downloading(key), session.state.value)

        reportedProgress.await()
        assertEquals(DictionaryDownloadState.Downloading(key, progress), session.state.value)
        assertEquals(null, completionThread)

        finishInstallation.complete(Unit)
        owner.children.toList().joinAll()

        assertSame(ownerThread, completionThread)
        assertEquals(DictionaryDownloadState.Idle, session.state.value)
    }

    @Test
    fun failedInstallationCanBeRetriedForAnotherSourceWithTheCurrentCallback() = withSession { session, owner ->
        val installed = mutableListOf<Pair<String, DictionarySource>>()
        session.start(key, onInstalled = { installed += key }) { throw IOException("Invalid dictionary") }
        owner.children.toList().joinAll()
        assertEquals(DictionaryDownloadState.Failed(key, "Invalid dictionary"), session.state.value)
        assertTrue(installed.isEmpty())

        val retryKey = "de" to DictionarySource.ESPEAK
        session.start(retryKey, onInstalled = { installed += retryKey }) { }
        assertEquals(DictionaryDownloadState.Downloading(retryKey), session.state.value)
        owner.children.toList().joinAll()

        assertEquals(listOf(retryKey), installed)
        assertEquals(DictionaryDownloadState.Idle, session.state.value)
    }

    @Test
    fun cancellationBeforeDispatchClearsThePendingDownloadAndAllowsRetry() = withSession { session, owner ->
        var installations = 0
        var completions = 0
        session.start(key, onInstalled = { completions++ }) { installations++ }

        session.cancel()
        owner.children.toList().joinAll()

        assertEquals(0, installations)
        assertEquals(0, completions)
        assertEquals(DictionaryDownloadState.Idle, session.state.value)

        session.start(key, onInstalled = { completions++ }) { installations++ }
        owner.children.toList().joinAll()

        assertEquals(1, installations)
        assertEquals(1, completions)
        assertEquals(DictionaryDownloadState.Idle, session.state.value)
    }

    @Test
    fun ownerCancellationBeforeDispatchClearsThePendingDownload() = withSession { session, owner ->
        var installed = false
        var started = false
        session.start(key, onInstalled = { installed = true }) { started = true }

        owner.cancelAndJoin()

        assertFalse(started)
        assertFalse(installed)
        assertEquals(DictionaryDownloadState.Idle, session.state.value)
    }

    @Test
    fun cancellationBeforeOrAfterProgressClearsTheDownloadWithoutAnnouncingInstallation() = withSession { session, owner ->
        for (reported in listOf(null, progress)) {
            val started = CompletableDeferred<Unit>()
            var installed = false
            var released = false
            session.start(key, onInstalled = { installed = true }) { report ->
                try {
                    if (reported != null) report(reported)
                    started.complete(Unit)
                    awaitCancellation()
                } finally {
                    released = true
                }
            }
            started.await()
            assertEquals(DictionaryDownloadState.Downloading(key, reported), session.state.value)

            session.cancel()
            owner.children.toList().joinAll()

            assertTrue(released)
            assertFalse(installed)
            assertEquals(DictionaryDownloadState.Idle, session.state.value)
        }
    }

    @Test
    fun cancellingTheOwnerReleasesPendingInstallation() = withSession { session, owner ->
        val started = CompletableDeferred<Unit>()
        var installed = false
        var released = false
        session.start(key, onInstalled = { installed = true }) {
            try {
                started.complete(Unit)
                awaitCancellation()
            } finally {
                released = true
            }
        }
        started.await()

        owner.cancelAndJoin()

        assertTrue(released)
        assertFalse(installed)
        assertEquals(DictionaryDownloadState.Idle, session.state.value)
    }

    @Test
    fun cancelledInstallerFinishesWithoutReportingFailureOrCancellingTheOwner() = withSession { session, owner ->
        var installed = false
        session.start(key, onInstalled = { installed = true }) { throw CancellationException("Cancelled") }
        val download = owner.children.single()
        download.join()

        assertTrue(download.isCancelled)
        assertTrue(owner.isActive)
        assertFalse(installed)
        assertEquals(DictionaryDownloadState.Idle, session.state.value)
    }

    @Test
    fun cancellationWithoutAnActiveDownloadPreservesTheDisplayedState() = withSession { session, owner ->
        session.cancel()
        assertEquals(DictionaryDownloadState.Idle, session.state.value)

        session.start(key, onInstalled = {}) { throw IOException("Invalid dictionary") }
        owner.children.toList().joinAll()
        session.cancel()

        assertEquals(DictionaryDownloadState.Failed(key, "Invalid dictionary"), session.state.value)
    }

    @Test
    fun progressReportedAfterCancellationIsRejected() = withSession { session, owner ->
        val started = CompletableDeferred<Unit>()
        var rejected = false
        var installed = false
        session.start(key, onInstalled = { installed = true }) { report ->
            try {
                started.complete(Unit)
                awaitCancellation()
            } finally {
                try {
                    report(progress)
                } catch (cancelled: CancellationException) {
                    rejected = true
                    throw cancelled
                }
            }
        }
        started.await()

        session.cancel()
        owner.children.toList().joinAll()

        assertTrue(rejected)
        assertFalse(installed)
        assertEquals(DictionaryDownloadState.Idle, session.state.value)
    }

    private fun withSession(test: suspend (DictionaryDownloadSession, Job) -> Unit) = runBlocking {
        withTimeout(5_000) {
            val owner = Job(coroutineContext[Job])
            val session = DictionaryDownloadSession(CoroutineScope(coroutineContext + owner))
            try {
                test(session, owner)
            } finally {
                owner.cancelAndJoin()
            }
        }
    }
}
