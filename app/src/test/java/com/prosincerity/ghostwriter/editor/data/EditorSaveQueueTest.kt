package com.prosincerity.ghostwriter.editor.data

import com.prosincerity.ghostwriter.projects.data.ProjectStorage
import java.io.File
import kotlin.coroutines.CoroutineContext
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class EditorSaveQueueTest {
    @get:Rule val temporaryFolder = TemporaryFolder()
    private val dispatcher = QueuedDispatcher()
    private val owner = SupervisorJob()
    private val queue = EditorSaveQueue(CoroutineScope(owner + dispatcher))

    @After fun closeQueue() {
        owner.cancel()
        dispatcher.runPending()
    }

    @Test fun cancelledScreen_doesNotCancelAnAcceptedExitSave() {
        val project = temporaryFolder.newFolder()
        val session = EditorLyricsSession(project, "song")
        val saving = queue.enqueue(project, "final draft") { session.finish("final draft", 3) }
        val screen = Job()
        CoroutineScope(screen + dispatcher).launch { saving.await() }
        screen.cancel()

        dispatcher.runPending()

        assertEquals("final draft", ProjectStorage.loadLatest(project))
        assertTrue(runBlocking { saving.await() })
        assertNull(queue.pendingLyrics(project))
    }

    @Test fun reopenedEditor_savesAfterThePreviousVisitsPendingExit() {
        val project = temporaryFolder.newFolder()
        val oldSession = EditorLyricsSession(project, "song")
        val newSession = EditorLyricsSession(project, "song")
        val releaseOldSave = CompletableDeferred<Unit>()
        queue.enqueue(project, "old visit") {
            releaseOldSave.await()
            oldSession.finish("old visit", 3)
        }
        val current = queue.enqueue(project, "new visit") { newSession.autosave("new visit", 3) }

        dispatcher.runPending()
        assertFalse(current.isCompleted)
        assertEquals("new visit", queue.pendingLyrics(project))
        releaseOldSave.complete(Unit)
        dispatcher.runPending()

        assertEquals("new visit", ProjectStorage.loadLatest(project))
        assertTrue(runBlocking { current.await() })
        assertNull(queue.pendingLyrics(project))
    }

    @Test fun completedEarlierSave_doesNotDiscardANewerPendingDraft() {
        val project = temporaryFolder.newFolder()
        val releaseFirst = CompletableDeferred<Unit>()
        val releaseSecond = CompletableDeferred<Unit>()
        queue.enqueue(project, "first") { releaseFirst.await(); true }
        queue.enqueue(project, "second") { releaseSecond.await(); true }
        dispatcher.runPending()

        releaseFirst.complete(Unit)
        dispatcher.runPending()

        assertEquals("second", queue.pendingLyrics(project))
        releaseSecond.complete(Unit)
        dispatcher.runPending()
        assertNull(queue.pendingLyrics(project))
    }

    @Test fun blockedProject_doesNotPreventAnotherProjectsLocalSave() {
        val blocked = temporaryFolder.newFolder()
        val other = temporaryFolder.newFolder()
        val release = CompletableDeferred<Unit>()
        queue.enqueue(blocked, "pending") { release.await(); true }
        queue.enqueue(other, "other draft") { ProjectStorage.rotateAndSave(other, "other draft", 3) }

        dispatcher.runPending()

        assertEquals("other draft", ProjectStorage.loadLatest(other))
        assertEquals("pending", queue.pendingLyrics(blocked))
    }

    @Test fun failedSave_doesNotPreventTheNextSaveFromRunning() {
        val project = temporaryFolder.newFolder()
        val failed = queue.enqueue(project, "failed") { throw java.io.IOException("Unavailable folder") }
        val retried = queue.enqueue(project, "retry") { ProjectStorage.rotateAndSave(project, "retry", 3) }

        dispatcher.runPending()

        assertFalse(runBlocking { failed.await() })
        assertTrue(runBlocking { retried.await() })
        assertEquals("retry", ProjectStorage.loadLatest(project))
    }

    @Test fun cancelledQueueOwner_discardsUnstartedWork() {
        val project = temporaryFolder.newFolder()
        var saved = false
        val result = queue.enqueue(project, "draft") { saved = true; true }

        owner.cancel()
        dispatcher.runPending()

        assertFalse(saved)
        assertTrue(result.isCancelled)
        assertNull(queue.pendingLyrics(project))
    }

    @Test fun projectMutations_keepTheirResultsAndOrderingBetweenLyricSaves() {
        val project = temporaryFolder.newFolder()
        val release = CompletableDeferred<Unit>()
        val order = mutableListOf<String>()
        queue.enqueue(project, "first") { release.await(); order += "lyrics"; true }
        val mutation = queue.submit(project, "second") { order += "beat"; "imported.wav" }
        queue.enqueue(project, "second") { order += "metadata"; true }
        dispatcher.runPending()
        assertTrue(order.isEmpty())

        release.complete(Unit)
        dispatcher.runPending()

        assertEquals(listOf("lyrics", "beat", "metadata"), order)
        assertEquals("imported.wav", runBlocking { mutation.await() })
    }

    @Test fun failedProjectMutation_doesNotCancelALaterSave() {
        val project = temporaryFolder.newFolder()
        val failure = queue.submit(project, "draft") { throw java.io.IOException("Import failed") }
        val saved = queue.enqueue(project, "draft") { ProjectStorage.rotateAndSave(project, "draft", 3) }

        dispatcher.runPending()

        org.junit.Assert.assertThrows(java.io.IOException::class.java) { runBlocking { failure.await() } }
        assertTrue(runBlocking { saved.await() })
        assertEquals("draft", ProjectStorage.loadLatest(project))
    }

    private class QueuedDispatcher : CoroutineDispatcher() {
        private val tasks = ArrayDeque<Runnable>()
        override fun dispatch(context: CoroutineContext, block: Runnable) { tasks.addLast(block) }
        fun runPending() { while (tasks.isNotEmpty()) tasks.removeFirst().run() }
    }
}
