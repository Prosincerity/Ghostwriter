package com.ghostwriter.exe.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ProjectStorageBeatTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun listInstrumentals_filtersAudioExtensionsAndSortsAlphabetically() {
        val beatsDir = tempFolder.newFolder("instrumentals")
        File(beatsDir, "trap_b.mp3").writeText("audio1")
        File(beatsDir, "boom_a.wav").writeText("audio2")
        File(beatsDir, "drill_c.ogg").writeText("audio3")
        File(beatsDir, "notes.txt").writeText("not audio")
        File(beatsDir, "cover.png").writeText("not audio")

        val result = ProjectStorage.listInstrumentals(beatsDir)
        assertEquals(3, result.size)
        assertEquals("boom_a.wav", result[0].name)
        assertEquals("drill_c.ogg", result[1].name)
        assertEquals("trap_b.mp3", result[2].name)
    }

    @Test
    fun assignBeatToProject_copiesBeatAndUpdatesMetadata() {
        val projectDir = tempFolder.newFolder("MySong")
        val sourceBeat = tempFolder.newFile("sample_source_90bpm.mp3")
        sourceBeat.writeText("fake mp3 audio content")

        val assigned = ProjectStorage.assignBeatToProject(
            projectDir = projectDir,
            sourceFile = sourceBeat,
            originalName = "Cool Sample (90 BPM).mp3"
        )

        assertEquals("beat.mp3", assigned.name)
        assertTrue(assigned.exists())
        assertEquals("fake mp3 audio content", assigned.readText())

        val meta = ProjectStorage.loadMetadata(projectDir, "MySong")
        assertEquals("beat.mp3", meta.beatFile)
        assertEquals("Cool Sample (90 BPM).mp3", meta.beatOriginalName)

        val resolved = ProjectStorage.getProjectBeatFile(projectDir, meta)
        assertNotNull(resolved)
        assertEquals(assigned.absolutePath, resolved?.absolutePath)
    }

    @Test
    fun assignBeatToProject_cleansUpOldBeatWithDifferentExtension() {
        val projectDir = tempFolder.newFolder("ReplacedBeatSong")
        val oldWav = File(projectDir, "beat.wav")
        oldWav.writeText("old wav content")

        val newMp3Source = tempFolder.newFile("new_beat.mp3")
        newMp3Source.writeText("new mp3 content")

        val assigned = ProjectStorage.assignBeatToProject(projectDir, newMp3Source, "new_beat.mp3")

        assertEquals("beat.mp3", assigned.name)
        assertTrue(assigned.exists())
        assertFalse("Old wav beat should have been deleted", oldWav.exists())

        val meta = ProjectStorage.loadMetadata(projectDir, "ReplacedBeatSong")
        assertEquals("beat.mp3", meta.beatFile)
    }

    @Test
    fun removeBeatFromProject_deletesFileAndClearsMetadata() {
        val projectDir = tempFolder.newFolder("AcapellaTrack")
        val sourceBeat = tempFolder.newFile("temp_beat.mp3")
        sourceBeat.writeText("audio")
        ProjectStorage.assignBeatToProject(projectDir, sourceBeat, "temp_beat.mp3")

        val beatFile = File(projectDir, "beat.mp3")
        assertTrue(beatFile.exists())

        ProjectStorage.removeBeatFromProject(projectDir)

        assertFalse("Beat file should be deleted", beatFile.exists())
        val meta = ProjectStorage.loadMetadata(projectDir, "AcapellaTrack")
        assertNull(meta.beatFile)
        assertNull(meta.beatOriginalName)
        assertNull(ProjectStorage.getProjectBeatFile(projectDir, meta))
    }

    @Test
    fun importInstrumental_createsSanitizedFileInDirectory() {
        val beatsDir = tempFolder.newFolder("instrumentals")
        val imported = ProjectStorage.importInstrumental(beatsDir, "My / Wild : Beat 140.wav") { dest ->
            dest.writeText("imported content")
        }

        assertTrue(imported.exists())
        assertEquals("My _ Wild _ Beat 140.wav", imported.name)
        assertEquals("imported content", imported.readText())
    }
}
