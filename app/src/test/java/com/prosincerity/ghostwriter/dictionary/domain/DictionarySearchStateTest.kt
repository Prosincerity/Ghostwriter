package com.prosincerity.ghostwriter.dictionary.domain

import com.prosincerity.ghostwriter.dictionary.data.DictionaryMatch
import com.prosincerity.ghostwriter.dictionary.data.DictionarySearchResult
import com.prosincerity.ghostwriter.dictionary.data.PronunciationSource
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Test

class DictionarySearchStateTest {
    private val found = DictionarySearchResult(
        null, listOf(DictionaryMatch("cat", "/æt/", PronunciationSource.WIKTIONARY)), hasNext = true,
    )

    @Test fun initialState_hasNoResultsErrorOrLoading() {
        val state = DictionarySearchState()
        assertFalse(state.loading)
        assertNull(state.result)
        assertNull(state.error)
        assertEquals(0, state.page)
    }

    @Test fun startingSearch_clearsPreviousResultsAndPage() {
        val started = DictionarySearchState().start()
        val ready = started.complete(started.revision, found, 2)
        val next = ready.start()
        assertTrue(next.loading)
        assertNull(next.result)
        assertEquals(0, next.page)
        assertEquals(found, ready.result)
        assertEquals(2, ready.page)
    }

    @Test fun startingSearch_clearsPreviousError() {
        val started = DictionarySearchState().start()
        val next = started.fail(started.revision, "Lookup failed").start()
        assertTrue(next.loading)
        assertNull(next.error)
    }

    @Test fun resettingSearch_clearsPreviousResultsAndPage() {
        val started = DictionarySearchState().start()
        val reset = started.complete(started.revision, found, 2).reset()
        assertFalse(reset.loading)
        assertNull(reset.result)
        assertEquals(0, reset.page)
    }

    @Test fun resettingSearch_clearsPreviousError() {
        val started = DictionarySearchState().start()
        val reset = started.fail(started.revision, "Lookup failed").reset()
        assertFalse(reset.loading)
        assertNull(reset.error)
    }

    @Test fun resettingSearch_rejectsLateResultsFromCancelledRequest() {
        val started = DictionarySearchState().start()
        val reset = started.reset()
        assertEquals(reset, reset.complete(started.revision, found, 2))
        assertFalse(reset.loading)
        assertNull(reset.result)
    }

    @Test fun resettingSearch_rejectsLateErrorsFromCancelledRequest() {
        val started = DictionarySearchState().start()
        val reset = started.reset()
        assertEquals(reset, reset.fail(started.revision, "Stale failure"))
        assertNull(reset.error)
    }

    @Test fun completingCurrentSearch_publishesResultsAndRequestedPage() {
        val started = DictionarySearchState().start()
        val completed = started.complete(started.revision, found, 2)
        assertEquals(found, completed.result)
        assertEquals(2, completed.page)
        assertFalse(completed.loading)
        assertNull(completed.error)
    }

    @Test fun completingEmptySearch_stillPublishesAnEmptyResult() {
        val started = DictionarySearchState().start()
        val empty = DictionarySearchResult(null, emptyList())
        val completed = started.complete(started.revision, empty, 0)
        assertEquals(empty, completed.result)
        assertEquals(0, completed.page)
        assertFalse(completed.loading)
    }

    @Test fun supersededResults_cannotReplaceNewSearchLoadingState() {
        val first = DictionarySearchState().start()
        val second = first.start()
        assertEquals(second, second.complete(first.revision, found, 2))
        assertTrue(second.loading)
        assertNull(second.result)
    }

    @Test fun supersededResults_cannotOverwriteNewerResultsOrPage() {
        val first = DictionarySearchState().start()
        val second = first.start()
        val latest = second.complete(second.revision, found, 1)
        assertEquals(latest, latest.complete(first.revision, DictionarySearchResult(null, emptyList()), 2))
    }

    @Test fun supersededFailure_cannotReplaceNewSearchLoadingState() {
        val first = DictionarySearchState().start()
        val second = first.start()
        assertEquals(second, second.fail(first.revision, "Stale failure"))
        assertTrue(second.loading)
        assertNull(second.error)
    }

    @Test fun failingCurrentSearch_publishesErrorWithoutResultsOrPage() {
        val started = DictionarySearchState().start()
        val failed = started.fail(started.revision, "Lookup failed")
        assertEquals("Lookup failed", failed.error)
        assertFalse(failed.loading)
        assertNull(failed.result)
        assertEquals(0, failed.page)
    }

    @Test fun failureWithoutMessage_usesFallbackText() {
        val started = DictionarySearchState().start()
        assertEquals("Dictionary search failed", started.fail(started.revision, null).error)
    }

    @Test fun failureWithEmptyMessage_preservesTheMessage() {
        val started = DictionarySearchState().start()
        assertEquals("", started.fail(started.revision, "").error)
    }

    @Test fun finishingCancelledCurrentSearch_clearsLoading() {
        val started = DictionarySearchState().start()
        val finished = started.finish(started.revision)
        assertFalse(finished.loading)
        assertNull(finished.result)
        assertNull(finished.error)
    }

    @Test fun finishingSupersededSearch_cannotClearNewSearchLoadingState() {
        val first = DictionarySearchState().start()
        val second = first.start()
        assertEquals(second, second.finish(first.revision))
        assertTrue(second.loading)
    }

    @Test fun finishingSuccessfulSearch_preservesResultsAndPage() {
        val started = DictionarySearchState().start()
        val completed = started.complete(started.revision, found, 2)
        assertEquals(completed, completed.finish(started.revision))
    }

    @Test fun finishingFailedSearch_preservesError() {
        val started = DictionarySearchState().start()
        val failed = started.fail(started.revision, "Lookup failed")
        assertEquals(failed, failed.finish(started.revision))
    }
}
