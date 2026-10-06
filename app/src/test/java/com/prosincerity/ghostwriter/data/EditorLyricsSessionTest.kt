package com.prosincerity.ghostwriter.data

import java.io.File
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class EditorLyricsSessionTest {
    @get:Rule
    val temporaryFolder = TemporaryFolder()

    @Test
    fun persistentSaveFailureReportsFailureButKeepsLocalDraftAndAllowsRetry() {
        val project = temporaryFolder.newFolder("song")
        var available = false
        val archived = mutableListOf<String>()
        val session = EditorLyricsSession(project, "song") { directory, _ ->
            if (available) archived.add(ProjectStorage.loadLatest(directory))
            available
        }
        assertFalse(session.saveManual("first", 3))
        assertEquals("first", ProjectStorage.loadLatest(project))
        assertFalse(session.autosave("second", 3))
        assertEquals("second", ProjectStorage.loadLatest(project))
        available = true
        assertTrue(session.autosave("second", 3))
        assertEquals(listOf("second"), archived)
        assertTrue(session.finish("final", 3))
        assertFalse(session.saveManual("stale", 3))
        assertEquals(listOf("second", "final"), archived)
    }

    @Test
    fun failedLocalSaveDoesNotArchiveOldLyricsAsTheNewSave() {
        val project = temporaryFolder.newFolder("blocked")
        File(project, "autosave1.txt").mkdir()
        File(project, "autosave1.txt/keep").writeText("unrelated")
        var persisted = false
        val session = EditorLyricsSession(project, "blocked") { _, _ -> persisted = true; true }
        assertFalse(session.autosave("new", 3))
        assertFalse(persisted)
        assertFalse(session.finish("final", 3))
    }

    @Test
    fun manualAndPeriodicSaves_persistLyricsBeforeExit() {
        val project = temporaryFolder.newFolder("song")
        val session = EditorLyricsSession(project, "song")

        assertTrue(session.saveManual("first verse", 3))
        assertEquals("first verse", File(project, "lyrics.txt").readText())
        assertTrue(File(project, "lyrics.txt").setLastModified(1_000L))
        session.autosave("second verse", 3)
        assertEquals("second verse", File(project, "autosave1.txt").readText())
        session.finish("final verse", 3)
        assertEquals("final verse", ProjectStorage.loadLatest(project))
    }

    @Test
    fun queuedManualSave_afterExitCannotOverwriteFinalLyrics() {
        val project = temporaryFolder.newFolder("song")
        val session = EditorLyricsSession(project, "song")
        assertTrue(session.saveManual("saved verse", 3))
        assertTrue(File(project, "lyrics.txt").setLastModified(1_000L))

        session.finish("new lyrics typed before leaving", 3)
        // An IO callback already queued by the Save button can run after disposal.
        assertFalse(session.saveManual("older button snapshot", 3))

        assertEquals("new lyrics typed before leaving", ProjectStorage.loadLatest(project))
        assertEquals("saved verse", File(project, "lyrics.txt").readText())
    }

    @Test
    fun queuedAutosave_andRepeatedExitCannotOverwriteFinalLyrics() {
        val project = temporaryFolder.newFolder("song")
        val session = EditorLyricsSession(project, "song")
        session.finish("latest lyrics", 3)
        session.autosave("stale periodic snapshot", 3)
        session.finish("stale disposal", 3)

        assertEquals("latest lyrics", ProjectStorage.loadLatest(project))
        assertFalse(File(project, "autosave2.txt").exists())
    }

    @Test
    fun reopeningProject_allowsNewSavesWithoutRevivingOldSession() {
        val project = temporaryFolder.newFolder("song")
        val oldSession = EditorLyricsSession(project, "song")
        oldSession.finish("before settings", 3)

        val newSession = EditorLyricsSession(project, "song")
        assertTrue(newSession.saveManual("after settings", 3))
        oldSession.autosave("before settings", 3)

        assertEquals("after settings", ProjectStorage.loadLatest(project))
        assertEquals("after settings", File(project, "autosave1.txt").readText())
    }
}
