package com.prosincerity.ghostwriter.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import kotlin.text.Charsets.UTF_8

class ProjectStorageTest {
    @Test
    fun renameFailure_rollsBackDirectoryAndManualSaveWhenMetadataCannotBeWritten() {
        for (hasManualSave in listOf(false, true)) {
            val project = tempFolder.newFolder("rollback_$hasManualSave")
            if (hasManualSave) File(project, "${project.name}.txt").writeText("keep lyrics")
            File(project, "project.json/keep").apply { parentFile!!.mkdir(); writeText("keep metadata") }

            assertNull(ProjectStorage.renameProjectDirectory(project, "renamed_$hasManualSave"))
            assertTrue(project.isDirectory)
            assertFalse(File(project.parentFile, "renamed_$hasManualSave").exists())
            assertEquals("keep metadata", File(project, "project.json/keep").readText())
            if (hasManualSave) assertEquals("keep lyrics", File(project, "${project.name}.txt").readText())
        }
    }

    @Test
    fun renameSameTitle_preservesDirectoryAndMetadata() {
        val project = tempFolder.newFolder("same")
        File(project, "project.json").writeText("unchanged")
        assertEquals(project, ProjectStorage.renameProjectDirectory(project, "same"))
        assertEquals("unchanged", File(project, "project.json").readText())
    }

    @Test
    fun sanitizeTitle_truncatesTwoAndThreeByteCharactersAndTrailingSpaces() {
        for (character in listOf("é", "字")) {
            val sanitized = ProjectStorage.sanitizeTitle(character.repeat(300))
            assertTrue(sanitized.toByteArray(UTF_8).size <= 251)
            assertEquals(character.repeat(251 / character.toByteArray(UTF_8).size), sanitized)
        }
        assertEquals("a".repeat(250), ProjectStorage.sanitizeTitle("a".repeat(250) + " .suffix"))
    }


    @Test
    fun autosaveNamedProject_keepsManualSnapshotAndBackupHistorySeparate() {
        for (title in listOf("autosave1", "autosave2", "autosave5", "autosave10", "AUTOSAVE1")) {
            val project = tempFolder.newFolder(title)
            ProjectStorage.rotateAndSave(project, "first", 3)
            assertTrue(ProjectStorage.saveManual(project, title, "manual", 3))
            assertEquals("first", File(project, "autosave2.txt").readText())
            assertTrue(File(project, "lyrics.txt").setLastModified(1_000L))
            ProjectStorage.rotateAndSave(project, "latest", 3)
            assertEquals("manual", File(project, "lyrics.txt").readText())
            assertEquals("manual", File(project, "autosave2.txt").readText())
            assertEquals("first", File(project, "autosave3.txt").readText())
            assertEquals("latest", ProjectStorage.loadLatest(project))
        }
    }

    @Test
    fun renameToAndFromAutosaveName_preservesManualSaveAndBackupRing() {
        val project = tempFolder.newFolder("track")
        assertTrue(ProjectStorage.saveManual(project, "track", "manual", 3))
        File(project, "lyrics.txt").setLastModified(1000L)
        ProjectStorage.rotateAndSave(project, "latest", 3)
        for (title in listOf("autosave2", "renamed")) {
            assertEquals(project, ProjectStorage.renameProjectDirectory(project, title))
            assertEquals(title, ProjectStorage.loadMetadata(project, "fallback").title)
            assertEquals("manual", File(project, "lyrics.txt").readText())
            assertEquals("latest", ProjectStorage.loadLatest(project))
            assertEquals("manual", File(project, "autosave2.txt").readText())
        }
    }

    @Test
    fun legacyAutosaveNamedProject_remainsReadableAndRenameKeepsBackups() {
        val project = tempFolder.newFolder("autosave2")
        File(project, "autosave1.txt").apply { writeText("latest"); assertTrue(setLastModified(2_000L)) }
        File(project, "autosave2.txt").apply { writeText("older"); assertTrue(setLastModified(1_000L)) }
        assertEquals("latest", ProjectStorage.loadLatest(project))

        val renamed = ProjectStorage.renameProjectDirectory(project, "legacy")!!
        assertEquals("older", File(renamed, "autosave2.txt").readText())
        assertEquals("latest", ProjectStorage.loadLatest(renamed))
    }

    @Test
    fun blockedOlderBackup_doesNotPreventSavingTheLatestLyrics() {
        val project = tempFolder.newFolder("blocked_backup")
        ProjectStorage.rotateAndSave(project, "first", 3)
        ProjectStorage.rotateAndSave(project, "second", 3)
        assertTrue(File(project, "autosave3.txt").mkdir())
        File(project, "autosave3.txt/keep").writeText("unrelated file")

        ProjectStorage.rotateAndSave(project, "latest", 3)

        assertEquals("latest", File(project, "autosave1.txt").readText())
        assertEquals("second", File(project, "autosave2.txt").readText())
        assertEquals("latest", ProjectStorage.loadLatest(project))
        assertFalse(project.list()!!.any { it.endsWith(".tmp") })
    }

    @Test
    fun failedNewestSave_preservesBackupsOutsideTheReducedCount() {
        val project = tempFolder.newFolder("failed_newest")
        File(project, "autosave1.txt").mkdir()
        File(project, "autosave1.txt/keep").writeText("unrelated file")
        File(project, "autosave4.txt").apply {
            writeText("recoverable lyrics")
            assertTrue(setLastModified(2_000L))
        }
        File(project, "autosave5.txt").apply {
            writeText("older lyrics")
            assertTrue(setLastModified(1_000L))
        }

        ProjectStorage.rotateAndSave(project, "unsaved lyrics", 3)

        assertEquals("recoverable lyrics", File(project, "autosave4.txt").readText())
        assertEquals("older lyrics", File(project, "autosave5.txt").readText())
        assertEquals("recoverable lyrics", ProjectStorage.loadLatest(project))
        assertFalse(project.list()!!.any { it.endsWith(".tmp") })
    }

    @Test
    fun reservedTitle_recoversDedicatedManualSaveWhenAutosavesAreUnreadable() {
        val project = tempFolder.newFolder("autosave1")
        assertTrue(ProjectStorage.saveManual(project, "autosave1", "manual", 3))
        val newest = File(project, "autosave1.txt")
        assertTrue(newest.delete())
        assertTrue(newest.mkdir())

        assertEquals("manual", ProjectStorage.loadLatest(project))
        assertEquals("lyrics.txt", ProjectStorage.manualSaveFileName("autosave1"))
        assertEquals("lyrics.txt", ProjectStorage.manualSaveFileName("song"))
    }

    @Test
    fun renameDoesNotDependOnManualFilename() {
        val project = tempFolder.newFolder("autosave1")
        assertTrue(ProjectStorage.saveManual(project, "autosave1", "manual", 3))
        assertEquals(project, ProjectStorage.renameProjectDirectory(project, "autosave1.manual"))
        assertEquals(project, ProjectStorage.renameProjectDirectory(project, "autosave1"))
        assertEquals("manual", File(project, "lyrics.txt").readText())
    }

    @Test
    fun resolveProjectTitle_reusesExistingCasingAndSanitizedName() {
        assertEquals("My/Track", ProjectStorage.resolveProjectTitle("my/track", listOf("My/Track")))
        assertEquals("New/Track", ProjectStorage.resolveProjectTitle("New/Track", emptyList()))
    }

    @Test
    fun manualSave_replacesExistingContentsWithoutLeavingTemporaryFiles() {
        val project = tempFolder.newFolder("replace")
        assertTrue(ProjectStorage.saveManual(project, "replace", "first", 3))
        assertTrue(ProjectStorage.saveManual(project, "replace", "second", 3))
        assertEquals("second", File(project, "lyrics.txt").readText())
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
    fun loadLatest_missingNewestAutosaveUsesNewerBackupInsteadOfOldManualSave() {
        val project = tempFolder.newFolder("missing_newest")
        File(project, "missing_newest.txt").apply {
            writeText("old manual save")
            assertTrue(setLastModified(1_000L))
        }
        File(project, "autosave2.txt").apply {
            writeText("newer autosave")
            assertTrue(setLastModified(2_000L))
        }

        assertEquals("newer autosave", ProjectStorage.loadLatest(project))
    }

    @Test
    fun loadLatest_unreadableNewestAutosaveUsesNewerManualSaveInsteadOfOldBackup() {
        val project = tempFolder.newFolder("unreadable_newest")
        File(project, "autosave2.txt").apply {
            writeText("old autosave")
            assertTrue(setLastModified(1_000L))
        }
        File(project, "unreadable_newest.txt").apply {
            writeText("newer manual save")
            assertTrue(setLastModified(2_000L))
        }
        File(project, "autosave1.txt").apply {
            assertTrue(mkdir())
            assertTrue(setLastModified(3_000L))
        }

        assertEquals("newer manual save", ProjectStorage.loadLatest(project))
    }

    @Test
    fun loadLatest_recoversTheNewestReadableBackupWhenSlotAgesAreOutOfOrder() {
        val project = tempFolder.newFolder("out_of_order")
        assertTrue(File(project, "autosave1.txt").mkdir())
        File(project, "autosave2.txt").apply {
            writeText("stale backup")
            assertTrue(setLastModified(1_000L))
        }
        File(project, "out_of_order.txt").apply {
            writeText("manual save")
            assertTrue(setLastModified(2_000L))
        }
        File(project, "autosave3.txt").apply {
            writeText("latest recoverable lyrics")
            assertTrue(setLastModified(3_000L))
        }

        assertEquals("latest recoverable lyrics", ProjectStorage.loadLatest(project))
    }

    @Test
    fun loadLatest_recoversLegacyBackupsBeyondTheCurrentCountOptions() {
        for (slot in 6..10) {
            val project = tempFolder.newFolder("legacy_backup_$slot")
            File(project, "${project.name}.txt").apply {
                writeText("old manual save")
                assertTrue(setLastModified(1_000L))
            }
            File(project, "autosave$slot.txt").apply {
                writeText("recoverable backup $slot")
                assertTrue(setLastModified(2_000L))
            }

            assertEquals("recoverable backup $slot", ProjectStorage.loadLatest(project))
        }
    }

    @Test
    fun loadLatest_equalBackupTimestampsPreferTheEarlierSlotAndManualSaveWinsTies() {
        val project = tempFolder.newFolder("equal_ages")
        for (slot in 1..3) {
            File(project, "autosave$slot.txt").apply {
                writeText("backup $slot")
                assertTrue(setLastModified(2_000L))
            }
        }
        assertEquals("backup 1", ProjectStorage.loadLatest(project))
        File(project, "equal_ages.txt").apply {
            writeText("manual save")
            assertTrue(setLastModified(2_000L))
        }
        assertEquals("manual save", ProjectStorage.loadLatest(project))
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

    @Test
    fun sanitizeTitle_limitsUtf8LengthSoTheManualSaveNameFitsTheFilesystem() {
        for (title in listOf("a".repeat(300), "🎵".repeat(100))) {
            val sanitized = ProjectStorage.sanitizeTitle(title)

            assertTrue(
                "Sanitized title must leave room for the .txt extension: $sanitized",
                sanitized.toByteArray(UTF_8).size <= 251,
            )
            assertFalse(sanitized.last().isHighSurrogate())

            val project = File(tempFolder.root, sanitized)
            assertTrue(project.mkdir())
            assertTrue(ProjectStorage.saveManual(project, title, "lyrics", keepCount = 3))
        }
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

    // --- project renaming tests ---

    @Test
    fun renameProjectDirectory_changesOnlyMetadataAndKeepsLegacyLyricsReadable() {
        val project = tempFolder.newFolder("Old Track")
        File(project, "Old Track.txt").writeText("saved lyrics")
        File(project, "beat.mp3").writeText("beat data")
        assertTrue(ProjectStorage.saveMetadata(project,
            ProjectMetadata(title = "Old Track", bpm = 90, beatFile = "beat.mp3")))
        val renamed = ProjectStorage.renameProjectDirectory(project, "New/Track")
        assertEquals(project, renamed)
        assertTrue(File(project, "Old Track.txt").isFile)
        assertEquals("saved lyrics", ProjectStorage.loadLatest(project))
        assertEquals("beat data", File(project, "beat.mp3").readText())
        val metadata = ProjectStorage.loadMetadata(project, project.name)
        assertEquals("New/Track", metadata.title)
        assertEquals(90, metadata.bpm)
        assertEquals("beat.mp3", metadata.beatFile)
    }

    @Test
    fun renameDoesNotCollideWithAnotherFolder() {
        val source = tempFolder.newFolder("source")
        File(source, "source.txt").writeText("source lyrics")
        val existing = tempFolder.newFolder("existing")
        File(existing, "existing.txt").writeText("existing lyrics")
        assertEquals(source, ProjectStorage.renameProjectDirectory(source, "existing"))
        assertEquals("source lyrics", ProjectStorage.loadLatest(source))
        assertEquals("existing lyrics", ProjectStorage.loadLatest(existing))
    }

    @Test
    fun renamePreservesEveryExistingSnapshotFilename() {
        val source = tempFolder.newFolder("old_title")
        File(source, "old_title.txt").writeText("current lyrics")
        File(source, "new_title.txt").writeText("other saved lyrics")
        assertEquals(source, ProjectStorage.renameProjectDirectory(source, "new_title"))
        assertEquals("current lyrics", File(source, "old_title.txt").readText())
        assertEquals("other saved lyrics", File(source, "new_title.txt").readText())
    }

    @Test
    fun rotateAndSave_identicalContent_noOpsWithoutRotating() {
        val projectDir = tempFolder.newFolder("test_track")
        ProjectStorage.rotateAndSave(projectDir, "Verse 1", keepCount = 3)
        assertEquals("Verse 1", File(projectDir, "autosave1.txt").readText())
        assertFalse(File(projectDir, "autosave2.txt").exists())

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
    fun rotateAndSave_preservesTheTimestampOfOlderSnapshots() {
        val project = tempFolder.newFolder("backup_timestamps")
        val first = File(project, "autosave1.txt")
        first.writeText("older lyrics")
        assertTrue(first.setLastModified(1_000L))

        ProjectStorage.rotateAndSave(project, "newer lyrics", keepCount = 3)

        assertEquals(1_000L, File(project, "autosave2.txt").lastModified())
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
    fun saveManual_createsFixedTextFileAndSynchronizesAutosave() {
        val projectDir = tempFolder.newFolder("Summer_Bars")
        ProjectStorage.saveManual(projectDir, "Summer Bars", "Spitting heat", keepCount = 3)

        val manualFile = File(projectDir, "lyrics.txt")
        assertTrue("Manual file should exist", manualFile.exists())
        assertEquals("Spitting heat", manualFile.readText())

        val autosaveFile = File(projectDir, "autosave1.txt")
        assertTrue("Autosave1 should also be synchronized", autosaveFile.exists())
        assertEquals("Spitting heat", autosaveFile.readText())

        assertEquals("loadLatest should load manual save", "Spitting heat", ProjectStorage.loadLatest(projectDir))
    }
}
