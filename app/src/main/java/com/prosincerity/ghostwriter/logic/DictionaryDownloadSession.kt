package com.prosincerity.ghostwriter.logic

import com.prosincerity.ghostwriter.data.DictionaryDownloadProgress
import com.prosincerity.ghostwriter.data.DictionarySource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.currentCoroutineContext
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Coordinates one UI-selected installation on the owner's scope. The installer
 * dispatches blocking work and may report progress from a worker dispatcher.
 */
internal class DictionaryDownloadSession(private val scope: CoroutineScope) {
    private val mutableState = MutableStateFlow<DictionaryDownloadState>(DictionaryDownloadState.Idle)
    val state = mutableState.asStateFlow()
    private var downloadJob: Job? = null

    fun start(
        key: Pair<String, DictionarySource>,
        onInstalled: () -> Unit,
        install: suspend (suspend (DictionaryDownloadProgress) -> Unit) -> Unit,
    ) {
        mutableState.value = DictionaryDownloadState.Downloading(key)
        val job = scope.launch {
            try {
                install { progress ->
                    currentCoroutineContext().ensureActive()
                    mutableState.update { it.withProgress(progress) }
                }
                onInstalled()
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                mutableState.update { it.fail(failure.message) }
            }
        }
        downloadJob = job
        // Completion also runs when cancellation prevents the coroutine body from starting.
        job.invokeOnCompletion {
            mutableState.update { it.finish() }
            downloadJob = null
        }
    }

    fun cancel() {
        downloadJob?.cancel()
    }
}
