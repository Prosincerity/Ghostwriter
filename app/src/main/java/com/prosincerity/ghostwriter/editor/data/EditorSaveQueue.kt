package com.prosincerity.ghostwriter.editor.data

import java.io.File
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.CoroutineStart
import kotlinx.coroutines.Deferred
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.async
import kotlinx.coroutines.joinAll

/** Accepted saves survive screen disposal and run in order for each project. */
internal class EditorSaveQueue(
    private val scope: CoroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
) {
    private class Pending(val lyrics: String, val result: Job)
    private val pending = mutableMapOf<File, Pending>()

    @Synchronized
    fun pendingLyrics(project: File): String? = pending[project]?.lyrics

    fun enqueue(project: File, lyrics: String, save: suspend () -> Boolean): Deferred<Boolean> =
        submit(project, lyrics) {
            try {
                save()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (_: Exception) {
                false
            }
        }

    /** Beat imports/removals must share the same ordering as lyric and metadata saves. */
    @Synchronized
    fun <T> submit(project: File, lyrics: String, save: suspend () -> T): Deferred<T> {
        val previous = pending[project]?.result
        val result = scope.async(start = CoroutineStart.LAZY) {
            previous?.join()
            save()
        }
        pending[project] = Pending(lyrics, result)
        result.invokeOnCompletion {
            synchronized(this) {
                if (pending[project]?.result === result) pending.remove(project)
            }
        }
        result.start()
        return result
    }

    suspend fun awaitPending(project: File) {
        while (true) {
            val result = synchronized(this) { pending[project]?.result } ?: return
            result.join()
        }
    }

    suspend fun awaitIdle() {
        while (true) {
            val results = synchronized(this) { pending.values.map { it.result } }
            if (results.isEmpty()) return
            results.joinAll()
        }
    }

    companion object {
        val process = EditorSaveQueue()
    }
}
