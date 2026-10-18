package com.prosincerity.ghostwriter.logic

import com.prosincerity.ghostwriter.data.ProjectSummary
import org.junit.Assert.assertEquals
import org.junit.Test

class HomeProjectRowsTest {
    @Test
    fun noProjectsProducesNoRowsEvenWithUnrelatedMetadata() {
        val rows = homeProjectRows(
            emptyList(),
            mapOf("other" to ProjectSummary("Other", 1000)),
            mapOf("other" to "Other"),
        ) { error("Unused summaries must not be formatted") }

        assertEquals(emptyList<HomeProjectRow>(), rows)
    }

    @Test
    fun displayTitlesKeepProjectIdentityAndFallBackToTheOriginalIdentifier() {
        val rows = homeProjectRows(
            listOf("uuid", "Legacy"),
            mapOf("uuid" to ProjectSummary("Stale title", 0)),
            mapOf("uuid" to "Verse / chorus"),
        ) { error("Undated rows must not be formatted") }

        assertEquals(
            listOf(HomeProjectRow("Legacy", "Legacy", null), HomeProjectRow("uuid", "Verse / chorus", null)),
            rows,
        )
    }

    @Test
    fun summaryTitleDoesNotReplaceAMissingDisplayTitle() {
        val rows = homeProjectRows(
            listOf("project-id"), mapOf("project-id" to ProjectSummary("Metadata title", 0)), emptyMap(),
        ) { error("Undated rows must not be formatted") }

        assertEquals("project-id", rows.single().title)
    }

    @Test
    fun detailsKeepEditedTimeTempoAndKeyInTheirOriginalOrder() {
        val rows = homeProjectRows(
            listOf("project"),
            mapOf("project" to ProjectSummary("Title", 1234, 92, "C minor")),
            emptyMap(),
        ) { timestamp ->
            assertEquals(1234L, timestamp)
            "formatted edit time"
        }

        assertEquals("Edited formatted edit time · 92 BPM · C minor", rows.single().details)
    }

    @Test
    fun absentMusicalDetailsLeaveOnlyTheEditedTime() {
        val rows = homeProjectRows(
            listOf("project"), mapOf("project" to ProjectSummary("Title", 1)), emptyMap(),
        ) { "date" }

        assertEquals("Edited date", rows.single().details)
    }

    @Test
    fun unknownEditTimesDoNotHideAvailableMusicalDetails() {
        for (timestamp in listOf(0L, -1L)) {
            val rows = homeProjectRows(
                listOf("project"), mapOf("project" to ProjectSummary("Title", timestamp, 120, "D major")), emptyMap(),
            ) { error("Nonpositive edit times must not be formatted") }

            assertEquals("120 BPM · D major", rows.single().details)
        }
    }

    @Test
    fun eachOptionalMusicalDetailCanAppearOnItsOwnWithoutSeparators() {
        val rows = homeProjectRows(
            listOf("key", "tempo"),
            mapOf("key" to ProjectSummary("Key", 0, key = "F minor"),
                "tempo" to ProjectSummary("Tempo", 0, bpm = 80)),
            emptyMap(),
        ) { error("Undated rows must not be formatted") }

        assertEquals(listOf("F minor", "80 BPM"), rows.map { it.details })
    }

    @Test
    fun newestEditsSortFirstAndMissingSummariesUseZeroWithoutOverflow() {
        val rows = homeProjectRows(
            listOf("oldest", "missing", "newest", "recent"),
            mapOf("oldest" to ProjectSummary("Oldest", Long.MIN_VALUE),
                "newest" to ProjectSummary("Newest", Long.MAX_VALUE),
                "recent" to ProjectSummary("Recent", 1000)),
            emptyMap(),
        ) { it.toString() }

        assertEquals(listOf("newest", "recent", "missing", "oldest"), rows.map { it.projectId })
        assertEquals(null, rows.first { it.projectId == "missing" }.details)
    }

    @Test
    fun anEmptyKeyRetainsTheOriginalSubtitlePresence() {
        val rows = homeProjectRows(
            listOf("project"), mapOf("project" to ProjectSummary("Title", 0, key = "")), emptyMap(),
        ) { error("Undated rows must not be formatted") }

        assertEquals("", rows.single().details)
    }

    @Test
    fun tiedEditsSortByDisplayTitleIgnoringCase() {
        val rows = homeProjectRows(
            listOf("a-id", "Legacy", "z-id"), emptyMap(),
            mapOf("a-id" to "Beta", "z-id" to "alpha"),
        ) { error("Missing summaries must not be formatted") }

        assertEquals(listOf("z-id", "a-id", "Legacy"), rows.map { it.projectId })
    }

    @Test
    fun equivalentTitlesRetainInputOrderAndDoNotMutateTheProjectList() {
        val projects = mutableListOf("second", "first")
        val rows = homeProjectRows(projects, emptyMap(), mapOf("second" to "ALPHA", "first" to "alpha")) {
            error("Missing summaries must not be formatted")
        }

        assertEquals(listOf("second", "first"), rows.map { it.projectId })
        assertEquals(listOf("second", "first"), projects)
    }
}
