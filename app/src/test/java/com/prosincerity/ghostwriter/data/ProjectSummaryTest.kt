package com.prosincerity.ghostwriter.data

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ProjectSummaryTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @Test
    fun latestLyricSnapshotWinsOverMetadataAndIgnoresBeatAndCacheWrites() {
        val project = temporaryFolder.newFolder("Track")
        File(project, "project.json").writeText("""{"updatedAt":1000,"bpm":92,"key":"C minor"}""")
        timedFile(project, "Track.txt", 2000)
        timedFile(project, "autosave3.txt", 4000)
        timedFile(project, "autosave1.txt", 3000)
        timedFile(project, "beat.wav", 9000)
        timedFile(project, "waveform.cache", 10000)
        timedFile(project, ".pending.txt", 11000)

        val summary = ProjectSummary.fromDirectory(project)
        assertEquals("Track", summary.title)
        assertEquals(4000L, summary.lastEditedAt)
        assertEquals(92, summary.bpm)
        assertEquals("C minor", summary.key)
    }

    @Test
    fun newerProjectInformationIsAlsoAnEdit() {
        val project = temporaryFolder.newFolder("Info")
        File(project, "project.json").writeText("""{"updatedAt":5000,"bpm":120,"key":" A minor "}""")
        timedFile(project, "Info.txt", 2000)
        val summary = ProjectSummary.fromDirectory(project)
        assertEquals(5000L, summary.lastEditedAt)
        assertEquals("A minor", summary.key)
    }

    @Test
    fun legacyAndMalformedMetadataDoNotInventAnEditAtTheCurrentTime() {
        for (contents in listOf("not json", "{}", """{"bpm":0,"key":null}""")) {
            val project = temporaryFolder.newFolder()
            File(project, "project.json").writeText(contents)
            timedFile(project, "autosave1.txt", 2000)
            val summary = ProjectSummary.fromDirectory(project)
            assertEquals(2000L, summary.lastEditedAt)
            assertNull(summary.bpm)
            assertNull(summary.key)
        }
    }

    @Test
    fun newProjectUsesDirectoryTimeWithoutWritingMetadata() {
        val project = temporaryFolder.newFolder("New")
        assertTrue(project.setLastModified(1000))
        assertEquals(1000L, ProjectSummary.fromDirectory(project).lastEditedAt)
        assertTrue(project.listFiles()!!.isEmpty())
    }

    private fun timedFile(directory: File, name: String, time: Long) {
        File(directory, name).apply {
            writeText("fixture")
            assertTrue(setLastModified(time))
        }
    }
}
