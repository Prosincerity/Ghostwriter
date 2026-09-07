package com.prosincerity.ghostwriter.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File

class ProjectStorageTest {

    @Test
    fun resolveProjectTitle_reusesExistingCasingAndSanitizedName() {
        assertEquals("My_Track", ProjectStorage.resolveProjectTitle("my/track", listOf("My_Track")))
        assertEquals("New_Track", ProjectStorage.resolveProjectTitle("New/Track", emptyList()))
    }

    @Test
    fun manualSave_replacesExistingContentsWithoutLeavingTemporaryFiles() {
        val project = tempFolder.newFolder("replace")
        assertTrue(ProjectStorage.saveManual(project, "replace", "first", 3))
        assertTrue(ProjectStorage.saveManual(project, "replace", "second", 3))
        assertEquals("second", File(project, "replace.txt").readText())
        assertEquals("second", ProjectStorage.loadLatest(project))
        assertFalse(project.list()!!.any { it.endsWith(".tmp") })
    }

    @Test
    fun unchangedLyrics_stillPruneBackupsAfterCountReduction() {
        val project = tempFolder.newFolder("prune")
        for (i in 1..5) ProjectStorage.rotateAndSave(project, "take $i", 5)
        ProjectStorage.rotateAndSave(project, "take 5", 3)
        assertEquals("take 5", File(project, "autosave1.txt").readText())
        assertFalse(File(project, "autosave4.txt").exists())
        assertFalse(File(project, "autosave5.txt").exists())
    }

    @Test
    fun loadLatest_recoversManualSaveWhenAutosavesAreUnreadable() {
        val project = tempFolder.newFolder("recovery")
        File(project, "recovery.txt").apply {
            writeText("recover me")
            setLastModified(1000)
        }
        File(project, "autosave1.txt").mkdir()
        assertEquals("recover me", ProjectStorage.loadLatest(project))
    }

    @Test
    fun saveFailures_areReportedToCaller() {
        val missing = File(tempFolder.root, "missing")
        assertFalse(ProjectStorage.saveManual(missing, "song", "lyrics", 3))
        assertFalse(ProjectStorage.saveMetadata(missing, ProjectMetadata("song")))
    }

    @Rule
    @JvmField
    val tempFolder = TemporaryFolder()

    // --- sanitizeTitle tests ---

    @Test
    fun sanitizeTitle_normalTitle_unchanged() {
        assertEquals("My Track", ProjectStorage.sanitizeTitle("My Track"))
    }

    @Test
    fun sanitizeTitle_trimsWhitespace() {
        assertEquals("My Track", ProjectStorage.sanitizeTitle("   My Track   "))
    }

    @Test
    fun sanitizeTitle_replacesIllegalCharacters() {
        assertEquals("Track_with_slash_and_colon", ProjectStorage.sanitizeTitle("Track/with:slash?and*colon"))
    }

    @Test
    fun sanitizeTitle_disallowsDirectoryTraversal() {
        assertEquals("untitled", ProjectStorage.sanitizeTitle(".."))
        assertEquals("untitled", ProjectStorage.sanitizeTitle("."))
        assertEquals("untitled", ProjectStorage.sanitizeTitle("..."))
        assertEquals("untitled", ProjectStorage.sanitizeTitle("   ....   "))
    }

    @Test
    fun sanitizeTitle_stripsLeadingAndTrailingDots() {
        assertEquals("hidden_track", ProjectStorage.sanitizeTitle(".hidden_track."))
    }

    @Test
    fun sanitizeTitle_emptyOrBlank_returnsUntitled() {
        assertEquals("untitled", ProjectStorage.sanitizeTitle(""))
        assertEquals("untitled", ProjectStorage.sanitizeTitle("   "))
    }

    // --- project deletion tests ---

    @Test
    fun deleteProjectDirectory_removesAllProjectContents() {
        val projectDir = tempFolder.newFolder("delete_me")
        File(projectDir, "delete_me.txt").writeText("lyrics")
        File(projectDir, "autosave1.txt").writeText("latest lyrics")
        File(projectDir, "autosave2.txt").writeText("older lyrics")
        File(projectDir, "project.json").writeText("{\"title\":\"delete_me\"}")
        File(projectDir, "beat.mp3").writeText("beat data")

        assertTrue(ProjectStorage.deleteProjectDirectory(projectDir))
        assertFalse(projectDir.exists())
    }

    @Test
    fun deleteProjectDirectory_returnsFalseForMissingProject() {
        val missingProject = File(tempFolder.root, "missing_project")
        assertFalse(ProjectStorage.deleteProjectDirectory(missingProject))
    }

    // --- rotateAndSave tests ---

    @Test
    fun rotateAndSave_firstWrite_createsAutosave1() {
        val projectDir = tempFolder.newFolder("test_track")
        ProjectStorage.rotateAndSave(projectDir, "Verse 1", keepCount = 3)

        val autosave1 = File(projectDir, "autosave1.txt")
        assertTrue(autosave1.exists())
        assertEquals("Verse 1", autosave1.readText())
        assertFalse(File(projectDir, "autosave2.txt").exists())
    }

    @Test
    fun rotateAndSave_identicalContent_noOpsWithoutRotating() {
        val projectDir = tempFolder.newFolder("test_track")
        ProjectStorage.rotateAndSave(projectDir, "Verse 1", keepCount = 3)
        ProjectStorage.rotateAndSave(projectDir, "Verse 1", keepCount = 3)

        val autosave1 = File(projectDir, "autosave1.txt")
        assertTrue(autosave1.exists())
        assertEquals("Verse 1", autosave1.readText())
        assertFalse(File(projectDir, "autosave2.txt").exists())
    }

    @Test
    fun rotateAndSave_multipleWrites_shiftsFilesUp() {
        val projectDir = tempFolder.newFolder("test_track")
        ProjectStorage.rotateAndSave(projectDir, "Take 1", keepCount = 3)
        ProjectStorage.rotateAndSave(projectDir, "Take 2", keepCount = 3)

        assertEquals("Take 2", File(projectDir, "autosave1.txt").readText())
        assertEquals("Take 1", File(projectDir, "autosave2.txt").readText())
        assertFalse(File(projectDir, "autosave3.txt").exists())

        ProjectStorage.rotateAndSave(projectDir, "Take 3", keepCount = 3)
        assertEquals("Take 3", File(projectDir, "autosave1.txt").readText())
        assertEquals("Take 2", File(projectDir, "autosave2.txt").readText())
        assertEquals("Take 1", File(projectDir, "autosave3.txt").readText())
    }

    @Test
    fun rotateAndSave_exceedsKeepCount_dropsOldestBackup() {
        val projectDir = tempFolder.newFolder("test_track")
        ProjectStorage.rotateAndSave(projectDir, "Line 1", keepCount = 3)
        ProjectStorage.rotateAndSave(projectDir, "Line 2", keepCount = 3)
        ProjectStorage.rotateAndSave(projectDir, "Line 3", keepCount = 3)
        ProjectStorage.rotateAndSave(projectDir, "Line 4", keepCount = 3)

        assertEquals("Line 4", File(projectDir, "autosave1.txt").readText())
        assertEquals("Line 3", File(projectDir, "autosave2.txt").readText())
        assertEquals("Line 2", File(projectDir, "autosave3.txt").readText())
        assertFalse("Oldest backup beyond keepCount should not exist", File(projectDir, "autosave4.txt").exists())
    }

    @Test
    fun rotateAndSave_loweringKeepCount_prunesOldBackups() {
        val projectDir = tempFolder.newFolder("test_track")
        for (i in 1..5) {
            ProjectStorage.rotateAndSave(projectDir, "Snapshot $i", keepCount = 5)
        }
        assertTrue(File(projectDir, "autosave4.txt").exists())
        assertTrue(File(projectDir, "autosave5.txt").exists())

        // Lower keepCount to 3 and save new content
        ProjectStorage.rotateAndSave(projectDir, "Snapshot 6", keepCount = 3)

        assertTrue(File(projectDir, "autosave1.txt").exists())
        assertTrue(File(projectDir, "autosave2.txt").exists())
        assertTrue(File(projectDir, "autosave3.txt").exists())
        assertFalse("autosave4 should be pruned", File(projectDir, "autosave4.txt").exists())
        assertFalse("autosave5 should be pruned", File(projectDir, "autosave5.txt").exists())
    }

    // --- loadLatest tests ---

    @Test
    fun loadLatest_emptyDirectory_returnsEmptyString() {
        val projectDir = tempFolder.newFolder("empty_track")
        assertEquals("", ProjectStorage.loadLatest(projectDir))
    }

    @Test
    fun loadLatest_readsAutosave1WhenPresent() {
        val projectDir = tempFolder.newFolder("test_track")
        ProjectStorage.rotateAndSave(projectDir, "Latest Verse", keepCount = 3)
        assertEquals("Latest Verse", ProjectStorage.loadLatest(projectDir))
    }

    @Test
    fun loadLatest_fallsBackToOlderAutosaveIfNewestMissing() {
        val projectDir = tempFolder.newFolder("test_track")
        File(projectDir, "autosave2.txt").writeText("Recovered Take")
        assertEquals("Recovered Take", ProjectStorage.loadLatest(projectDir))
    }

    // --- saveManual tests ---

    @Test
    fun saveManual_createsNamedTextFileAndSynchronizesAutosave() {
        val projectDir = tempFolder.newFolder("Summer_Bars")
        ProjectStorage.saveManual(projectDir, "Summer Bars", "Spitting heat", keepCount = 3)

        val manualFile = File(projectDir, "${ProjectStorage.sanitizeTitle("Summer Bars")}.txt")
        assertTrue("Manual file should exist", manualFile.exists())
        assertEquals("Spitting heat", manualFile.readText())

        val autosaveFile = File(projectDir, "autosave1.txt")
        assertTrue("Autosave1 should also be synchronized", autosaveFile.exists())
        assertEquals("Spitting heat", autosaveFile.readText())

        assertEquals("loadLatest should load manual save", "Spitting heat", ProjectStorage.loadLatest(projectDir))
    }
}
