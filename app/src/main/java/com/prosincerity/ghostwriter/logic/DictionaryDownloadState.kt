package com.prosincerity.ghostwriter.logic

import com.prosincerity.ghostwriter.data.DictionaryDownloadProgress
import com.prosincerity.ghostwriter.data.DictionarySource

internal sealed interface DictionaryDownloadState {
    data object Idle : DictionaryDownloadState

    data class Downloading(
        val key: Pair<String, DictionarySource>,
        val progress: DictionaryDownloadProgress? = null,
    ) : DictionaryDownloadState {
        private val measuredProgress: DictionaryDownloadProgress?
            get() = progress?.takeIf { it.totalBytes > 0 }

        val fraction: Float?
            get() = measuredProgress?.let { (it.downloadedBytes.toFloat() / it.totalBytes).coerceIn(0f, 1f) }

        val percent: Int?
            get() = measuredProgress?.let { (100 * it.downloadedBytes / it.totalBytes).toInt().coerceIn(0, 100) }
    }

    data class Failed(val key: Pair<String, DictionarySource>, val message: String) : DictionaryDownloadState

    fun withProgress(progress: DictionaryDownloadProgress): DictionaryDownloadState =
        if (this is Downloading) copy(progress = progress) else this

    fun fail(message: String?): DictionaryDownloadState =
        if (this is Downloading) Failed(key, message ?: "unknown error") else this

    /** Success and cancellation clear progress; failure remains visible until retry. */
    fun finish(): DictionaryDownloadState = if (this is Downloading) Idle else this
}
