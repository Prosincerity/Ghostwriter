package com.prosincerity.ghostwriter.logic

import com.prosincerity.ghostwriter.data.DictionarySearchResult
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Coordinates requests on the owner's scope and dispatcher. The owner controls
 * its lifetime; each lookup remains responsible for dispatching blocking work.
 */
internal class DictionarySearchSession(private val scope: CoroutineScope) {
    private val mutableState = MutableStateFlow(DictionarySearchState())
    val state = mutableState.asStateFlow()
    private var searchJob: Job? = null

    fun reset() {
        mutableState.value = mutableState.value.reset()
        searchJob?.cancel()
        searchJob = null
    }

    fun submit(requestedPage: Int = 0, search: suspend () -> DictionarySearchResult) {
        mutableState.value = mutableState.value.start()
        val requestedRevision = mutableState.value.revision
        searchJob?.cancel()
        val job = scope.launch {
            try {
                val found = search()
                mutableState.value = mutableState.value.complete(requestedRevision, found, requestedPage)
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                mutableState.value = mutableState.value.fail(requestedRevision, failure.message)
            }
        }
        searchJob = job
        // Cancellation can prevent the coroutine body, including finally, from running.
        job.invokeOnCompletion {
            mutableState.update { it.finish(requestedRevision) }
        }
    }
}
