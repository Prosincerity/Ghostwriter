package com.ghostwriter.exe.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ProjectMetadataTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun loadMetadata_returnsDefaultWhenFileDoesNotExist() {
        val projectDir = tempFolder.newFolder("TestSong")
        val meta = ProjectStorage.loadMetadata(projectDir, "TestSong")

        assertEquals("TestSong", meta.title)
        assertNull(meta.bpm)
        assertNull(meta.key)
        assertNull(meta.timeSignature)
        assertNull(meta.notes)
        assertNull(meta.beatFile)
        assertNull(meta.beatOriginalName)
    }

    @Test
    fun saveAndLoadMetadata_persistsAllFields() {
        val projectDir = tempFolder.newFolder("HitTrack")
        val initial = ProjectMetadata(
            title = "HitTrack",
            bpm = 95,
            key = "F# Minor",
            timeSignature = "4/4",
            notes = "First single for the EP",
            beatFile = "beat.mp3",
            beatOriginalName = "hard_trap_95bpm.mp3",
            createdAt = 1000L,
            updatedAt = 2000L,
            version = 1,
        )

        ProjectStorage.saveMetadata(projectDir, initial)

        val loaded = ProjectStorage.loadMetadata(projectDir, "HitTrack")
        assertEquals("HitTrack", loaded.title)
        assertEquals(95, loaded.bpm)
        assertEquals("F# Minor", loaded.key)
        assertEquals("4/4", loaded.timeSignature)
        assertEquals("First single for the EP", loaded.notes)
        assertEquals("beat.mp3", loaded.beatFile)
        assertEquals("hard_trap_95bpm.mp3", loaded.beatOriginalName)
        assertEquals(1000L, loaded.createdAt)
        // updatedAt should be refreshed on save
        assertTrue("updatedAt should be updated on save", loaded.updatedAt >= 2000L)
        assertEquals(1, loaded.version)
    }

    @Test
    fun saveAndLoadMetadata_handlesOptionalFieldsAsNull() {
        val projectDir = tempFolder.newFolder("Acapella")
        val initial = ProjectMetadata(
            title = "Acapella",
            bpm = null,
            key = null,
            timeSignature = null,
            notes = null,
        )

        ProjectStorage.saveMetadata(projectDir, initial)

        val loaded = ProjectStorage.loadMetadata(projectDir, "Acapella")
        assertEquals("Acapella", loaded.title)
        assertNull(loaded.bpm)
        assertNull(loaded.key)
        assertNull(loaded.timeSignature)
        assertNull(loaded.notes)
    }

    @Test
    fun loadMetadata_handlesCorruptedJsonGracefully() {
        val projectDir = tempFolder.newFolder("CorruptedTrack")
        val file = File(projectDir, "project.json")
        file.writeText("{ invalid json content ...")

        val loaded = ProjectStorage.loadMetadata(projectDir, "CorruptedTrack")
        assertEquals("CorruptedTrack", loaded.title)
        assertNull(loaded.bpm)
    }
}
