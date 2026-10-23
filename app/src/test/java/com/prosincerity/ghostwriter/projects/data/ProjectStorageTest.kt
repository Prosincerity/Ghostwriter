package com.prosincerity.ghostwriter.projects.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
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
        for (title in listOf("autosave1", "autosave1.manual", "autosave1", "autosave2", "renamed")) {
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
    fun failedNewestSave_preservesExistingBackupContentsAndAges() {
        val project = tempFolder.newFolder("failed_save_history")
        val blockedNewest = File(project, "autosave1.txt")
        assertTrue(blockedNewest.mkdir())
        File(blockedNewest, "keep").writeText("blocked newest slot")
        for (slot in 2..5) {
            File(project, "autosave$slot.txt").apply {
                writeText("draft $slot")
                assertTrue(setLastModified((6 - slot) * 1_000L))
            }
        }

        assertFalse(ProjectStorage.rotateAndSave(project, "unsaved lyrics", 3))

        for (slot in 2..5) {
            val backup = File(project, "autosave$slot.txt")
            assertEquals("draft $slot", backup.readText())
            assertEquals((6 - slot) * 1_000L, backup.lastModified())
        }
        assertEquals("draft 2", ProjectStorage.loadLatest(project))
        assertEquals("blocked newest slot", File(blockedNewest, "keep").readText())
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
        assertEquals("manual", File(project, "lyrics.txt").readText())
    }

    @Test
    fun resolveProjectTitle_reusesExistingCasingAndSanitizedName() {
        assertEquals("My/Track", ProjectStorage.resolveProjectTitle("my/track", listOf("My/Track")))
        assertEquals("New/Track", ProjectStorage.resolveProjectTitle("New/Track", emptyList()))
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
    fun loadLatest_comparesMixedCaseLegacySnapshotWithAutosaveAges() {
        for (manualTime in listOf(1_000L, 2_000L, 3_000L)) {
            val project = tempFolder.newFolder("mixed_case_$manualTime")
            File(project, "AUTOSAVE2.txt").apply {
                writeText("manual lyrics")
                assertTrue(setLastModified(manualTime))
            }
            File(project, "autosave1.txt").apply {
                writeText("autosaved lyrics")
                assertTrue(setLastModified(2_000L))
            }

            assertEquals(if (manualTime > 2_000L) "manual lyrics" else "autosaved lyrics",
                ProjectStorage.loadLatest(project))
        }
    }

    @Test
    fun loadLatest_tiedCaseVariantsPreferCanonicalAutosave() {
        val directory = tempFolder.newFolder("case_variants")
        val legacy = File(directory, "AUTOSAVE1.txt").apply {
            writeText("legacy lyrics")
            assertTrue(setLastModified(2_000L))
        }
        val canonical = File(directory, "autosave1.txt").apply {
            writeText("current lyrics")
            assertTrue(setLastModified(2_000L))
        }
        val project = object : File(directory.path) {
            override fun listFiles(): Array<File> = arrayOf(legacy, canonical)
        }

        assertEquals("current lyrics", ProjectStorage.loadLatest(project))
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
        for (hasManualSave in listOf(false, true)) {
            val project = tempFolder.newFolder("missing_newest_$hasManualSave")
            if (hasManualSave) {
                File(project, "${project.name}.txt").apply {
                    writeText("old manual save")
                    assertTrue(setLastModified(1_000L))
                }
            }
            File(project, "autosave2.txt").apply {
                writeText("newer autosave")
                assertTrue(setLastModified(2_000L))
            }

            assertEquals("newer autosave", ProjectStorage.loadLatest(project))
        }
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
    fun autosave_afterClockRollbackLoadsTheNewEditInsteadOfTheOlderManualSave() {
        val project = tempFolder.newFolder()
        File(project, "lyrics.txt").apply {
            writeText("old manual lyrics")
            assertTrue(setLastModified(2_000L))
        }

        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "new lyrics", 3, now = { 1_000L }))

        assertEquals("new lyrics", ProjectStorage.loadLatest(project))
        assertEquals("old manual lyrics", File(project, "lyrics.txt").readText())
    }

    @Test
    fun autosave_atTheSameClockTimeLoadsTheNewEditInsteadOfTheOlderManualSave() {
        val project = tempFolder.newFolder()
        File(project, "lyrics.txt").apply {
            writeText("old manual lyrics")
            assertTrue(setLastModified(2_000L))
        }

        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "new lyrics", 3, now = { 2_000L }))

        assertEquals("new lyrics", ProjectStorage.loadLatest(project))
    }

    @Test
    fun manualSave_afterClockRollbackOutranksLegacyLyricsAndPreservesHistory() {
        val project = tempFolder.newFolder()
        val legacy = File(project, "old-title.txt").apply {
            writeText("legacy lyrics")
            assertTrue(setLastModified(3_000L))
        }
        File(project, "autosave1.txt").apply {
            writeText("previous autosave")
            assertTrue(setLastModified(2_000L))
        }

        assertTrue(ProjectLyricsStorage.saveManual(project, "lyrics.txt", "new lyrics", 3, now = { 1_000L }))

        assertEquals("new lyrics", ProjectStorage.loadLatest(project))
        assertEquals("new lyrics", File(project, "lyrics.txt").readText())
        assertEquals("previous autosave", File(project, "autosave2.txt").readText())
        assertEquals(2_000L, File(project, "autosave2.txt").lastModified())
        assertEquals("legacy lyrics", legacy.readText())
        assertEquals(3_000L, legacy.lastModified())
    }

    @Test
    fun autosave_afterClockRollbackOutranksMixedCaseBackupsWithEmptyLyrics() {
        val project = tempFolder.newFolder()
        val legacy = File(project, "AUTOSAVE2.TXT").apply {
            writeText("legacy lyrics")
            assertTrue(setLastModified(3_000L))
        }

        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "", 3, now = { 1_000L }))

        assertEquals("", ProjectStorage.loadLatest(project))
        assertEquals("", File(project, "autosave1.txt").readText())
        assertEquals("legacy lyrics", legacy.readText())
        assertEquals(3_000L, legacy.lastModified())
    }

    @Test
    fun autosave_afterClockRollbackRefreshesAStaleMatchingFirstSlot() {
        val project = tempFolder.newFolder()
        File(project, "autosave1.txt").apply {
            writeText("earlier verse")
            assertTrue(setLastModified(1_000L))
        }
        File(project, "lyrics.txt").apply {
            writeText("recovered verse")
            assertTrue(setLastModified(3_000L))
        }

        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "earlier verse", 3, now = { 2_000L }))

        assertEquals("earlier verse", ProjectStorage.loadLatest(project))
        assertEquals("recovered verse", File(project, "lyrics.txt").readText())
        assertFalse(File(project, "autosave2.txt").exists())
    }

    @Test
    fun autosave_matchingAnOlderFirstSlotPersistsTheEditOverNewerSnapshots() {
        for (newerSnapshot in listOf("autosave2.txt", "lyrics.txt")) {
            val project = tempFolder.newFolder()
            val firstSlot = File(project, "autosave1.txt").apply {
                writeText("earlier verse")
                assertTrue(setLastModified(1_000L))
            }
            val recovered = File(project, newerSnapshot).apply {
                writeText("recovered verse")
                assertTrue(setLastModified(2_000L))
            }
            assertEquals("recovered verse", ProjectStorage.loadLatest(project))

            assertTrue(ProjectStorage.rotateAndSave(project, "earlier verse", 3))

            assertEquals("earlier verse", ProjectStorage.loadLatest(project))
            assertEquals("earlier verse", firstSlot.readText())
            assertEquals("recovered verse", recovered.readText())
            assertEquals(2_000L, recovered.lastModified())
            assertFalse(File(project, "autosave3.txt").exists())
        }
    }

    @Test
    fun rotateAndSave_identicalContent_noOpsWithoutRotating() {
        val projectDir = tempFolder.newFolder("test_track")
        ProjectStorage.rotateAndSave(projectDir, "Verse 1", keepCount = 3)
        assertEquals("Verse 1", File(projectDir, "autosave1.txt").readText())
        assertFalse(File(projectDir, "autosave2.txt").exists())
        val previousTime = File(projectDir, "autosave1.txt").lastModified()

        ProjectStorage.rotateAndSave(projectDir, "Verse 1", keepCount = 3)

        val autosave1 = File(projectDir, "autosave1.txt")
        assertTrue(autosave1.exists())
        assertEquals("Verse 1", autosave1.readText())
        assertEquals(previousTime, autosave1.lastModified())
        assertFalse(File(projectDir, "autosave2.txt").exists())
    }

    @Test
    fun autosave_rotatingOutOfOrderBackupsRetainsTheNewestPriorDrafts() {
        val project = tempFolder.newFolder()
        for (slot in 1..3) {
            File(project, "autosave$slot.txt").apply {
                writeText("draft $slot")
                assertTrue(setLastModified(slot * 1_000L))
            }
        }
        assertEquals("draft 3", ProjectStorage.loadLatest(project))

        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "new draft", 3, now = { 4_000L }))

        assertEquals("new draft", ProjectStorage.loadLatest(project))
        assertEquals("draft 3", File(project, "autosave2.txt").readText())
        assertEquals(3_000L, File(project, "autosave2.txt").lastModified())
        assertEquals("draft 2", File(project, "autosave3.txt").readText())
        assertEquals(2_000L, File(project, "autosave3.txt").lastModified())
    }

    @Test
    fun autosave_missingFirstSlotPreservesNewerLegacyBackupsBeforePruning() {
        val project = tempFolder.newFolder()
        File(project, "autosave2.txt").apply {
            writeText("")
            assertTrue(setLastModified(1_000L))
        }
        File(project, "autosave10.txt").apply {
            writeText("recoverable lyrics 🎵")
            assertTrue(setLastModified(3_000L))
        }

        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "new draft", 3, now = { 4_000L }))

        assertEquals("new draft", ProjectStorage.loadLatest(project))
        assertEquals("recoverable lyrics 🎵", File(project, "autosave2.txt").readText())
        assertEquals(3_000L, File(project, "autosave2.txt").lastModified())
        assertEquals("", File(project, "autosave3.txt").readText())
        assertEquals(1_000L, File(project, "autosave3.txt").lastModified())
        assertFalse(File(project, "autosave10.txt").exists())
    }

    @Test
    fun autosave_duplicateCopiesDoNotDisplaceDistinctHistory() {
        val project = tempFolder.newFolder()
        // Interrupted rotations can leave two copies of the same snapshot.
        for (slot in 1..2) {
            File(project, "autosave$slot.txt").apply {
                writeText("recent draft")
                assertTrue(setLastModified(3_000L))
            }
        }
        File(project, "autosave3.txt").apply {
            writeText("earlier draft")
            assertTrue(setLastModified(2_000L))
        }

        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "new draft", 3, now = { 4_000L }))

        assertEquals("new draft", ProjectStorage.loadLatest(project))
        assertEquals("recent draft", File(project, "autosave2.txt").readText())
        assertEquals(3_000L, File(project, "autosave2.txt").lastModified())
        assertEquals("earlier draft", File(project, "autosave3.txt").readText())
        assertEquals(2_000L, File(project, "autosave3.txt").lastModified())
    }

    @Test
    fun autosave_duplicateNewerBackupsDoNotDiscardTheOverwrittenFirstSlot() {
        val project = tempFolder.newFolder()
        for ((slot, text, time) in listOf(
            Triple(1, "earlier draft", 1_000L),
            Triple(2, "recent draft", 2_000L),
            Triple(3, "recent draft", 2_000L),
        )) {
            File(project, "autosave$slot.txt").apply {
                writeText(text)
                assertTrue(setLastModified(time))
            }
        }

        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "new draft", 3, now = { 3_000L }))

        assertEquals("new draft", ProjectStorage.loadLatest(project))
        val history = (2..3).map { slot ->
            val backup = File(project, "autosave$slot.txt")
            backup.readText() to backup.lastModified()
        }
        assertEquals(setOf("recent draft" to 2_000L, "earlier draft" to 1_000L), history.toSet())
    }

    @Test
    fun autosave_outOfOrderHistoryDoesNotCopyADraftTwice() {
        val project = tempFolder.newFolder()
        for ((slot, time) in listOf(2 to 2_000L, 3 to 4_000L, 4 to 1_000L, 5 to 3_000L)) {
            File(project, "autosave$slot.txt").apply {
                writeText("draft $time")
                assertTrue(setLastModified(time))
            }
        }

        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "new draft", 5, now = { 5_000L }))

        assertEquals("new draft", ProjectStorage.loadLatest(project))
        val history = (2..5).map { slot ->
            val backup = File(project, "autosave$slot.txt")
            backup.readText() to backup.lastModified()
        }
        assertEquals((1..4).map { "draft ${it * 1_000L}" to it * 1_000L }.toSet(), history.toSet())
    }

    @Test
    fun autosave_equalBackupAgesRetainEarlierSlotsFirst() {
        val project = tempFolder.newFolder()
        for (slot in 1..3) {
            File(project, "autosave$slot.txt").apply {
                writeText("draft $slot")
                assertTrue(setLastModified(2_000L))
            }
        }

        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "new draft", 3, now = { 3_000L }))

        assertEquals("draft 1", File(project, "autosave2.txt").readText())
        assertEquals("draft 2", File(project, "autosave3.txt").readText())
        assertEquals("new draft", ProjectStorage.loadLatest(project))
    }

    @Test
    fun autosave_blockedSecondSlotRetriesLostNewestHistoryInAnAvailableSlot() {
        for (hasOlderDraft in listOf(false, true)) {
            val project = tempFolder.newFolder()
            File(project, "autosave1.txt").apply {
                writeText("previous draft")
                assertTrue(setLastModified(3_000L))
            }
            File(project, "autosave2.txt/keep").apply {
                assertTrue(parentFile!!.mkdir())
                writeText("blocked slot")
            }
            if (hasOlderDraft) File(project, "autosave3.txt").apply {
                writeText("older draft")
                assertTrue(setLastModified(1_000L))
            }

            assertTrue(ProjectLyricsStorage.rotateAndSave(project, "new draft", 3, now = { 4_000L }))

            assertEquals("new draft", ProjectStorage.loadLatest(project))
            val backup = File(project, "autosave3.txt")
            assertTrue("The previous draft must survive when a backup slot is writable", backup.isFile)
            assertEquals("previous draft", backup.readText())
            assertEquals(3_000L, backup.lastModified())
            assertEquals("blocked slot", File(project, "autosave2.txt/keep").readText())
            assertFalse(project.list()!!.any { it.endsWith(".tmp") })
        }
    }

    @Test
    fun autosave_multipleBlockedSlotsPrioritizeTheLostNewestDraftOverOlderBackups() {
        val project = tempFolder.newFolder()
        for ((slot, time) in listOf(1 to 3_000L, 4 to 2_000L, 5 to 1_000L)) {
            File(project, "autosave$slot.txt").apply {
                writeText("draft $slot")
                assertTrue(setLastModified(time))
            }
        }
        for (slot in 2..3) File(project, "autosave$slot.txt/keep").apply {
            assertTrue(parentFile!!.mkdir())
            writeText("blocked slot $slot")
        }

        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "new draft", 5, now = { 4_000L }))

        assertEquals("new draft", ProjectStorage.loadLatest(project))
        assertEquals("draft 1", File(project, "autosave4.txt").readText())
        assertEquals(3_000L, File(project, "autosave4.txt").lastModified())
        assertEquals("draft 4", File(project, "autosave5.txt").readText())
        assertEquals(2_000L, File(project, "autosave5.txt").lastModified())
        for (slot in 2..3) assertEquals("blocked slot $slot", File(project, "autosave$slot.txt/keep").readText())
    }

    @Test
    fun autosave_blockedSecondSlotPreservesTheNewestHistoryInTheThirdSlot() {
        val project = tempFolder.newFolder()
        File(project, "autosave1.txt").apply {
            writeText("oldest draft")
            assertTrue(setLastModified(1_000L))
        }
        File(project, "autosave2.txt/keep").apply { parentFile!!.mkdir(); writeText("blocked slot") }
        val recovered = File(project, "autosave3.txt").apply {
            writeText("recovered draft")
            assertTrue(setLastModified(3_000L))
        }

        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "new draft", 3, now = { 4_000L }))

        assertEquals("new draft", ProjectStorage.loadLatest(project))
        assertEquals("recovered draft", recovered.readText())
        assertEquals(3_000L, recovered.lastModified())
        assertEquals("blocked slot", File(project, "autosave2.txt/keep").readText())
    }

    @Test
    fun autosave_blockedRotationKeepsLegacyHistoryAcrossUnchangedSaveTicks() {
        val project = tempFolder.newFolder()
        File(project, "autosave1.txt").apply {
            writeText("oldest draft")
            assertTrue(setLastModified(1_000L))
        }
        val blocked = File(project, "autosave2.txt/keep").apply {
            parentFile!!.mkdir()
            writeText("blocked slot")
        }
        val recovered = File(project, "autosave10.txt").apply {
            writeText("recovered draft")
            assertTrue(setLastModified(3_000L))
        }

        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "new draft", 3, now = { 4_000L }))
        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "new draft", 3, now = { 5_000L }))

        assertEquals("new draft", ProjectStorage.loadLatest(project))
        assertEquals("recovered draft", recovered.readText())
        assertEquals(3_000L, recovered.lastModified())
        assertEquals("blocked slot", blocked.readText())

        assertTrue(blocked.delete())
        assertTrue(blocked.parentFile!!.delete())
        assertTrue(ProjectLyricsStorage.rotateAndSave(project, "next draft", 3, now = { 6_000L }))
        assertEquals("next draft", ProjectStorage.loadLatest(project))
        assertEquals("new draft", File(project, "autosave2.txt").readText())
        assertEquals("recovered draft", File(project, "autosave3.txt").readText())
        assertFalse(recovered.exists())
    }

    @Test
    fun rotateAndSave_retainsNewestSnapshotsWithinBackupLimit() {
        val projectDir = tempFolder.newFolder("test_track")
        ProjectStorage.rotateAndSave(projectDir, "Take 1", keepCount = 3)
        assertTrue(File(projectDir, "autosave1.txt").setLastModified(1_000L))
        assertEquals("Take 1", ProjectStorage.loadLatest(projectDir))
        ProjectStorage.rotateAndSave(projectDir, "Take 2", keepCount = 3)

        assertEquals("Take 2", File(projectDir, "autosave1.txt").readText())
        assertEquals("Take 2", ProjectStorage.loadLatest(projectDir))
        assertEquals("Take 1", File(projectDir, "autosave2.txt").readText())
        assertEquals(1_000L, File(projectDir, "autosave2.txt").lastModified())
        assertFalse(File(projectDir, "autosave3.txt").exists())

        ProjectStorage.rotateAndSave(projectDir, "Take 3", keepCount = 3)
        assertEquals("Take 3", File(projectDir, "autosave1.txt").readText())
        assertEquals("Take 2", File(projectDir, "autosave2.txt").readText())
        assertEquals("Take 1", File(projectDir, "autosave3.txt").readText())
        assertEquals(1_000L, File(projectDir, "autosave3.txt").lastModified())

        ProjectStorage.rotateAndSave(projectDir, "Take 4", keepCount = 3)
        assertEquals("Take 4", File(projectDir, "autosave1.txt").readText())
        assertEquals("Take 3", File(projectDir, "autosave2.txt").readText())
        assertEquals("Take 2", File(projectDir, "autosave3.txt").readText())
        assertFalse("Oldest backup beyond keepCount should not exist", File(projectDir, "autosave4.txt").exists())
    }

    @Test
    fun rotateAndSave_retainsEmptySnapshotWithOriginalAge() {
        val project = tempFolder.newFolder("empty_snapshot")
        assertTrue(ProjectStorage.rotateAndSave(project, "", 3))
        assertTrue(File(project, "autosave1.txt").setLastModified(1_000L))

        assertTrue(ProjectStorage.rotateAndSave(project, "new verse 🎵\n字", 3))

        val backup = File(project, "autosave2.txt")
        assertTrue(backup.isFile)
        assertEquals("", backup.readText())
        assertEquals(1_000L, backup.lastModified())
        assertEquals("new verse 🎵\n字", ProjectStorage.loadLatest(project))
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
    fun loadLatest_failedDirectoryListingDoesNotBecomeABlankDraft() {
        val directory = tempFolder.newFolder()
        val snapshot = File(directory, "autosave1.txt").apply { writeText("retained verse") }
        val unavailable = object : File(directory.path) {
            override fun listFiles(): Array<File>? = null
        }

        assertThrows(java.io.IOException::class.java) { ProjectStorage.loadLatest(unavailable) }

        assertEquals("retained verse", snapshot.readText())
    }

    @Test
    fun loadLatest_unreadableSnapshotsDoNotBecomeABlankDraft() {
        for (name in listOf("lyrics.txt", "autosave1.txt", "AUTOSAVE10.TXT", "old-title.txt")) {
            val project = tempFolder.newFolder()
            val blocked = File(project, name).apply { assertTrue(mkdir()) }
            val retained = File(blocked, "keep").apply { writeText("retained bytes") }

            assertThrows(java.io.IOException::class.java) { ProjectStorage.loadLatest(project) }

            assertEquals("retained bytes", retained.readText())
        }
    }

    @Test
    fun loadLatest_emptyDirectory_returnsEmptyString() {
        val projectDir = tempFolder.newFolder("empty_track")
        assertEquals("", ProjectStorage.loadLatest(projectDir))
    }

    // --- saveManual tests ---

    @Test
    fun saveManual_reportsAutosaveFailureAndRetainsTheManualCopy() {
        val project = tempFolder.newFolder("blocked_manual_autosave")
        val blockedAutosave = File(project, "autosave1.txt")
        assertTrue(blockedAutosave.mkdir())
        File(blockedAutosave, "keep").writeText("blocked autosave")

        val saved = ProjectStorage.saveManual(project, "song", "new lyrics", 3)

        assertFalse("An incomplete manual save must report failure", saved)
        assertEquals("new lyrics", File(project, "lyrics.txt").readText())
        assertEquals("new lyrics", ProjectStorage.loadLatest(project))
        assertEquals("blocked autosave", File(blockedAutosave, "keep").readText())
        assertFalse(project.list()!!.any { it.endsWith(".tmp") })
    }

    @Test
    fun saveManual_replacesLyricsAndSynchronizesAutosaveWithoutTemporaryFiles() {
        val projectDir = tempFolder.newFolder("Summer_Bars")
        assertTrue(ProjectStorage.saveManual(projectDir, "Summer Bars", "first draft", keepCount = 3))
        assertEquals("first draft", File(projectDir, "lyrics.txt").readText())
        assertEquals("first draft", File(projectDir, "autosave1.txt").readText())
        assertTrue(ProjectStorage.saveManual(projectDir, "Summer Bars", "Spitting heat", keepCount = 3))

        val manualFile = File(projectDir, "lyrics.txt")
        assertTrue("Manual file should exist", manualFile.exists())
        assertEquals("Spitting heat", manualFile.readText())

        val autosaveFile = File(projectDir, "autosave1.txt")
        assertTrue("Autosave1 should also be synchronized", autosaveFile.exists())
        assertEquals("Spitting heat", autosaveFile.readText())

        assertEquals("loadLatest should load manual save", "Spitting heat", ProjectStorage.loadLatest(projectDir))
        assertFalse(projectDir.list()!!.any { it.endsWith(".tmp") })
    }
}
