package com.prosincerity.ghostwriter.dictionary.domain

import com.prosincerity.ghostwriter.dictionary.data.DictionarySearchResult

internal sealed interface DictionarySearchStatus {
    data object Idle : DictionarySearchStatus
    data object Loading : DictionarySearchStatus
    data class Ready(val result: DictionarySearchResult, val page: Int) : DictionarySearchStatus
    data class Failed(val message: String) : DictionarySearchStatus
}

/** A request may publish only until the next search or input change. */
internal data class DictionarySearchState(
    val revision: Int = 0,
    val status: DictionarySearchStatus = DictionarySearchStatus.Idle,
) {
    val loading: Boolean get() = status == DictionarySearchStatus.Loading
    val result: DictionarySearchResult? get() = (status as? DictionarySearchStatus.Ready)?.result
    val page: Int get() = (status as? DictionarySearchStatus.Ready)?.page ?: 0
    val error: String? get() = (status as? DictionarySearchStatus.Failed)?.message

    fun reset(): DictionarySearchState = DictionarySearchState(revision + 1)

    fun start(): DictionarySearchState = DictionarySearchState(revision + 1, DictionarySearchStatus.Loading)

    fun complete(requestRevision: Int, result: DictionarySearchResult, page: Int): DictionarySearchState =
        if (requestRevision == revision) copy(status = DictionarySearchStatus.Ready(result, page)) else this

    fun fail(requestRevision: Int, message: String?): DictionarySearchState =
        if (requestRevision == revision) copy(status = DictionarySearchStatus.Failed(message ?: "Dictionary search failed"))
        else this

    fun finish(requestRevision: Int): DictionarySearchState =
        if (requestRevision == revision && loading) copy(status = DictionarySearchStatus.Idle) else this
}
