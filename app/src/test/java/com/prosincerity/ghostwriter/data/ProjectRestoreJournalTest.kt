package com.prosincerity.ghostwriter.data

import java.io.File
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ProjectRestoreJournalTest {
    @get:Rule val folder = TemporaryFolder()

    private fun write(project: File, name: String, text: String, time: Long): File =
        File(project, name).apply {
            writeText(text)
            assertTrue(setLastModified(time))
        }

    @Test fun originalPartialAndCompletedFileStatesCanResume() {
        val project = folder.newFolder()
        write(project, "lyrics.txt", "old lyrics", 1_000L)
        write(project, "project.json", "old metadata", 1_000L)
        val legacy = write(project, "old title.txt", "legacy 字", 9_000L)
        val journal = ProjectRestoreJournal(project, "folder")
        journal.prepare(mapOf(
            "lyrics.txt" to ("" to 3_000L),
            "autosave1.txt" to ("" to 3_000L),
            "project.json" to ("new metadata" to null),
        ), olderThanRestored = 2_999L)

        assertTrue(journal.exists)
        assertTrue(journal.matches())
        write(project, "autosave1.txt", "", 3_000L)
        assertTrue(ProjectRestoreJournal(project, "folder").matches())
        write(project, "lyrics.txt", "", 3_000L)
        write(project, "project.json", "new metadata", 4_000L)
        assertTrue(legacy.setLastModified(2_999L))
        assertTrue(journal.matches())
        journal.clear()
        assertFalse(journal.exists)
        assertEquals("", File(project, "lyrics.txt").readText())
    }

    @Test fun agingAMixedCaseBackupIsARecognizedRestoreState() {
        val project = folder.newFolder()
        val backup = write(project, "AUTOSAVE10.TXT", "older lyrics", 9_000L)
        val journal = ProjectRestoreJournal(project, "folder")
        journal.prepare(mapOf("lyrics.txt" to ("shared lyrics" to 3_000L)), 2_999L)
        assertTrue(journal.matches())

        assertTrue(backup.setLastModified(2_999L))

        assertTrue(journal.matches())
        assertEquals("older lyrics", backup.readText())
    }

    @Test fun subsequentLyricEditsIncludingRevertsInvalidateRecovery() {
        for ((text, time) in listOf("new local draft" to 4_000L, "old lyrics" to 4_000L)) {
            val project = folder.newFolder()
            write(project, "lyrics.txt", "old lyrics", 1_000L)
            val journal = ProjectRestoreJournal(project, "folder")
            journal.prepare(mapOf("lyrics.txt" to ("shared lyrics" to 3_000L)), 2_999L)

            write(project, "lyrics.txt", text, time)

            assertFalse(journal.matches())
            assertEquals(text, File(project, "lyrics.txt").readText())
        }
    }

    @Test fun newSnapshotOrChangedMetadataInvalidatesRecovery() {
        for (name in listOf("new title.txt", "AUTOSAVE10.TXT", "project.json")) {
            val project = folder.newFolder()
            write(project, "project.json", "old metadata", 1_000L)
            val journal = ProjectRestoreJournal(project, "folder")
            journal.prepare(mapOf("project.json" to ("shared metadata" to null)), 2_999L)

            write(project, name, "local edit", 4_000L)

            assertFalse(journal.matches())
        }
    }

    @Test fun removingABlockedDestinationAllowsRecoveryToResume() {
        val project = folder.newFolder()
        val blocked = File(project, "autosave1.txt").apply { assertTrue(mkdir()) }
        val journal = ProjectRestoreJournal(project, "folder")
        journal.prepare(mapOf("autosave1.txt" to ("lyrics" to 3_000L)), 2_999L)

        assertTrue(journal.matches())
        assertTrue(blocked.delete())
        assertTrue(journal.matches())
        write(project, "autosave1.txt", "lyrics", 3_000L)
        assertTrue(journal.matches())
    }

    @Test fun recoveryCannotResumeAgainstAnotherArchive() {
        val project = folder.newFolder()
        write(project, "lyrics.txt", "local lyrics", 1_000L)
        ProjectRestoreJournal(project, "original folder").prepare(
            mapOf("lyrics.txt" to ("shared lyrics" to 3_000L)), 2_999L,
        )

        assertFalse(ProjectRestoreJournal(project, "different folder").matches())
        assertEquals("local lyrics", File(project, "lyrics.txt").readText())
    }

    @Test fun corruptJournalReportsFailureWithoutAuthorizingRecovery() {
        val project = folder.newFolder()
        write(project, "lyrics.txt", "local lyrics", 1_000L)
        File(project, ".lyric-restore.json").writeText("incomplete")

        assertThrows(org.json.JSONException::class.java) {
            ProjectRestoreJournal(project, "folder").matches()
        }
        assertEquals("local lyrics", File(project, "lyrics.txt").readText())
    }
}
