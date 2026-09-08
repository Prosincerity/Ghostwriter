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
import java.util.concurrent.CancellationException

class ProjectStorageBeatTest {

    @Test
    fun loadOrExtractWaveform_cancelledBeforeExtractionDoesNotCreateCache() {
        val project = tempFolder.newFolder("cancel_before_extraction")
        val beat = File(project, "beat.mp3").apply { writeText("beat") }
        var extractionCalled = false

        val result = runCatching {
            ProjectStorage.loadOrExtractWaveform(project, beat, 3, shouldCancel = { true }) { _, _ ->
                extractionCalled = true
                intArrayOf(1, 2, 3)
            }
        }

        assertTrue(result.exceptionOrNull() is CancellationException)
        assertFalse(extractionCalled)
        assertFalse(ProjectStorage.waveformCacheFile(project).exists())
        assertEquals("beat", beat.readText())
    }

    @Test
    fun loadOrExtractWaveform_cancelledDuringExtractionPreservesPreviousCache() {
        val project = tempFolder.newFolder("cancel_during_extraction")
        val beat = File(project, "beat.mp3").apply { writeText("beat") }
        assertTrue(ProjectStorage.saveCachedWaveform(project, 2, intArrayOf(7, 8)))
        val cacheBefore = ProjectStorage.waveformCacheFile(project).readText()
        var cancelled = false
        var extractionCalled = false

        val result = runCatching {
            ProjectStorage.loadOrExtractWaveform(project, beat, 3, shouldCancel = { cancelled }) { _, _ ->
                extractionCalled = true
                cancelled = true
                intArrayOf(1, 2, 3)
            }
        }

        assertTrue(extractionCalled)
        assertTrue(result.exceptionOrNull() is CancellationException)
        assertEquals(cacheBefore, ProjectStorage.waveformCacheFile(project).readText())
        assertEquals("beat", beat.readText())
    }

    @Test
    fun loadCachedWaveform_rejectsMalformedSamples() {
        val project = tempFolder.newFolder("invalid_cache_samples")
        for (payload in listOf("-1,2", "32769,2", "text,2", "1", "1,2,3", "1,")) {
            ProjectStorage.waveformCacheFile(project).writeText("2\n$payload")

            assertNull("Invalid payload: $payload", ProjectStorage.loadCachedWaveform(project, 2))
        }
    }

    @Test
    fun saveCachedWaveform_invalidReplacementPreservesExistingCache() {
        val project = tempFolder.newFolder("preserve_valid_cache")
        assertTrue(ProjectStorage.saveCachedWaveform(project, 2, intArrayOf(0, 32_768)))
        val cacheBefore = ProjectStorage.waveformCacheFile(project).readText()
        val invalidReplacements = listOf(
            0 to IntArray(0),
            -1 to IntArray(0),
            100_001 to IntArray(0),
            2 to intArrayOf(1),
            2 to intArrayOf(-1, 2),
            2 to intArrayOf(32_769, 2),
        )

        for ((targetCount, samples) in invalidReplacements) {
            assertFalse(ProjectStorage.saveCachedWaveform(project, targetCount, samples))
            assertEquals(cacheBefore, ProjectStorage.waveformCacheFile(project).readText())
        }
    }

    @Test
    fun waveformCache_roundTripsOnlyAtItsOriginalResolution() {
        val project = tempFolder.newFolder("waveform_cache")
        val peaks = intArrayOf(0, 12, 3, 32_768)

        assertTrue(ProjectStorage.saveCachedWaveform(project, 4, peaks))
        assertEquals(peaks.toList(), ProjectStorage.loadCachedWaveform(project, 4)?.toList())
        assertNull(ProjectStorage.loadCachedWaveform(project, 5))
    }

    @Test
    fun loadOrExtractWaveform_reusesACachedWaveformBeforeDecodingAgain() {
        val project = tempFolder.newFolder("cached_waveform")
        val beat = File(project, "beat.mp3").apply { writeText("beat") }
        var extractionCount = 0

        val first = ProjectStorage.loadOrExtractWaveform(project, beat, 3) { _, targetCount ->
            extractionCount++
            IntArray(targetCount) { it + 1 }
        }
        val second = ProjectStorage.loadOrExtractWaveform(project, beat, 3) { _, _ ->
            throw AssertionError("A valid cache should avoid a second extraction")
        }

        assertEquals(listOf(1, 2, 3), first.toList())
        assertEquals(first.toList(), second.toList())
        assertEquals(1, extractionCount)
    }

    @Test
    fun loadOrExtractWaveform_regeneratesCorruptOrDifferentResolutionCaches() {
        val project = tempFolder.newFolder("regenerate_waveform")
        val beat = File(project, "beat.mp3").apply { writeText("beat") }
        ProjectStorage.waveformCacheFile(project).writeText("not a waveform")

        val regenerated = ProjectStorage.loadOrExtractWaveform(project, beat, 2) { _, targetCount ->
            IntArray(targetCount) { 7 }
        }
        val differentResolution = ProjectStorage.loadOrExtractWaveform(project, beat, 3) { _, targetCount ->
            IntArray(targetCount) { 9 }
        }

        assertEquals(listOf(7, 7), regenerated.toList())
        assertEquals(listOf(9, 9, 9), differentResolution.toList())
    }

    @Test
    fun assigningOrRemovingABeat_invalidatesTheWaveformCache() {
        val project = tempFolder.newFolder("invalidate_waveform")
        assertTrue(ProjectStorage.saveCachedWaveform(project, 2, intArrayOf(1, 2)))

        ProjectStorage.assignBeatToProject(project, "new.mp3") { it.writeText("new beat") }
        assertFalse(ProjectStorage.waveformCacheFile(project).exists())

        assertTrue(ProjectStorage.saveCachedWaveform(project, 2, intArrayOf(3, 4)))
        ProjectStorage.removeBeatFromProject(project)
        assertFalse(ProjectStorage.waveformCacheFile(project).exists())
    }

    @Test
    fun assigningABeat_clearsMarkersFromThePreviousTimeline() {
        val project = tempFolder.newFolder("replace_markers")
        assertTrue(
            ProjectStorage.saveMetadata(
                project,
                ProjectMetadata(
                    title = "replace_markers",
                    markers = listOf(WaveformMarker("Hook", 12_000L)),
                ),
            ),
        )

        ProjectStorage.assignBeatToProject(project, "replacement.mp3") { it.writeText("new beat") }

        assertTrue(ProjectStorage.loadMetadata(project, "replace_markers").markers.isEmpty())
    }

    @Test
    fun failedImport_preservesExistingBeatAndMetadata() {
        val project = tempFolder.newFolder("failed_import")
        ProjectStorage.assignBeatToProject(project, "original.mp3") { it.writeText("original") }
        val metadataBefore = File(project, "project.json").readText()
        for (name in listOf("replacement.mp3", "replacement.wav")) {
            val result = runCatching {
                ProjectStorage.assignBeatToProject(project, name) {
                    it.writeText("partial")
                    throw java.io.IOException("Interrupted copy")
                }
            }
            assertTrue(result.isFailure)
            assertEquals("original", File(project, "beat.mp3").readText())
            assertEquals(metadataBefore, File(project, "project.json").readText())
            assertEquals(setOf("beat.mp3", "project.json"), project.list()!!.toSet())
        }
    }

    @Test
    fun getProjectBeatFile_rejectsOutsideFilesAndDirectories() {
        val project = tempFolder.newFolder("bounded_beat")
        File(tempFolder.root, "outside.mp3").writeText("outside")
        File(project, "directory.mp3").mkdir()
        for (name in listOf("../outside.mp3", "directory.mp3")) {
            assertNull(ProjectStorage.getProjectBeatFile(project, ProjectMetadata("song", beatFile = name)))
        }
    }

    @Test
    fun assignBeatToProject_rejectsUnsupportedExtensionBeforeCopy() {
        val project = tempFolder.newFolder("unsupported")
        val result = runCatching {
            ProjectStorage.assignBeatToProject(project, "notes.txt") {
                throw AssertionError("Copy must not run")
            }
        }
        assertTrue(result.exceptionOrNull() is IllegalArgumentException)
        assertTrue(project.list()!!.isEmpty())
    }

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
    fun assignBeatToProject_copyActionStoresSelectedFileAndOriginalName() {
        val projectDir = tempFolder.newFolder("SelectedBeatSong")

        val assigned = ProjectStorage.assignBeatToProject(
            projectDir = projectDir,
            originalName = "Midnight Loop.FLAC",
        ) { destination ->
            destination.writeText("selected beat content")
        }

        assertEquals("beat.flac", assigned.name)
        assertEquals("selected beat content", assigned.readText())
        assertEquals(
            "Midnight Loop.FLAC",
            ProjectStorage.loadMetadata(projectDir, "SelectedBeatSong").beatOriginalName,
        )
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
