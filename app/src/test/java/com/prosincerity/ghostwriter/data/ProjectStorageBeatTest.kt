package com.prosincerity.ghostwriter.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.util.concurrent.CountDownLatch
import java.util.concurrent.CancellationException
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit

class ProjectStorageBeatTest {
    @Test
    fun waveformRejectsInvalidResolutionMissingFilesAndBeatsOutsideProject() {
        val project = tempFolder.newFolder("invalid_waveform")
        val beat = File(project, "beat.wav").apply { writeText("audio") }
        val external = tempFolder.newFile("external.wav")
        for ((file, count) in listOf(beat to 0, beat to -1, beat to 100_001,
            File(project, "missing.wav") to 3, external to 3)) {
            val result = ProjectStorage.loadOrExtractWaveform(project, file, count) { _, _ ->
                throw AssertionError("Invalid request must not decode")
            }
            assertTrue(result.isEmpty())
        }
        assertFalse(ProjectStorage.waveformCacheFile(project).exists())
    }

    @Test
    fun fileBasedImportUsesSourceNameAndReportsMetadataFailure() {
        val project = tempFolder.newFolder("file_import")
        val source = tempFolder.newFile("original.wav").apply { writeText("audio") }
        val copied = ProjectStorage.assignBeatToProject(project, source)
        assertEquals("audio", copied.readText())
        assertEquals(copied, ProjectStorage.getProjectBeatFile(project))
        assertEquals("original.wav", ProjectStorage.loadMetadata(project, project.name).beatOriginalName)
        assertTrue(File(project, "project.json").delete())
        File(project, "project.json/keep").apply { parentFile!!.mkdir(); writeText("metadata") }
        val result = runCatching { ProjectStorage.assignBeatToProject(project, source) }
        assertTrue(result.exceptionOrNull() is java.io.IOException)
        assertEquals("audio", copied.readText())
        assertEquals("metadata", File(project, "project.json/keep").readText())
    }


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
        val cacheBefore = ProjectStorage.waveformCacheFile(project).readBytes().toList()
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
        assertEquals(cacheBefore, ProjectStorage.waveformCacheFile(project).readBytes().toList())
        assertEquals("beat", beat.readText())
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
        assertNull(ProjectStorage.loadCachedWaveform(project, 4))
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
    fun loadOrExtractWaveform_doesNotCacheFailedEmptyExtraction() {
        val project = tempFolder.newFolder("failed_waveform")
        val beat = File(project, "beat.mp3").apply { writeText("beat") }
        var extractionCount = 0

        repeat(2) {
            assertTrue(
                ProjectStorage.loadOrExtractWaveform(project, beat, 3) { _, _ ->
                    extractionCount++
                    IntArray(0)
                }.isEmpty(),
            )
        }

        assertEquals(2, extractionCount)
        assertFalse(ProjectStorage.waveformCacheFile(project).exists())
    }

    @Test
    fun loadOrExtractWaveform_doesNotBlockLyricsSaveWhileDecoding() {
        val project = tempFolder.newFolder("non_blocking_waveform")
        val beat = File(project, "beat.mp3").apply { writeText("beat") }
        val extractionStarted = CountDownLatch(1)
        val releaseExtraction = CountDownLatch(1)
        val executor = Executors.newFixedThreadPool(2)

        val extraction = executor.submit<IntArray> {
            ProjectStorage.loadOrExtractWaveform(project, beat, 3) { _, targetCount ->
                extractionStarted.countDown()
                check(releaseExtraction.await(5, TimeUnit.SECONDS))
                IntArray(targetCount) { it + 1 }
            }
        }

        try {
            assertTrue(extractionStarted.await(5, TimeUnit.SECONDS))

            val save = executor.submit<Boolean> {
                ProjectStorage.saveManual(project, "non_blocking_waveform", "lyrics", 3)
            }

            assertTrue(save.get(1, TimeUnit.SECONDS))
            assertEquals("lyrics", File(project, "lyrics.txt").readText())
        } finally {
            releaseExtraction.countDown()
            extraction.get(5, TimeUnit.SECONDS)
            executor.shutdownNow()
        }
    }

    @Test
    fun loadOrExtractWaveform_replacedBeatDoesNotCacheOldExtraction() {
        val project = tempFolder.newFolder("replaced_during_extraction")
        val beat = ProjectStorage.assignBeatToProject(project, "old.mp3") { it.writeText("old") }
        val extractionStarted = CountDownLatch(1)
        val releaseExtraction = CountDownLatch(1)
        val executor = Executors.newSingleThreadExecutor()

        val oldExtraction = executor.submit<IntArray> {
            ProjectStorage.loadOrExtractWaveform(project, beat, 2) { _, _ ->
                extractionStarted.countDown()
                check(releaseExtraction.await(5, TimeUnit.SECONDS))
                intArrayOf(1, 2)
            }
        }

        try {
            assertTrue(extractionStarted.await(5, TimeUnit.SECONDS))
            ProjectStorage.assignBeatToProject(project, "new.mp3") { it.writeText("new beat") }
        } finally {
            releaseExtraction.countDown()
        }

        try {
            assertEquals(listOf(1, 2), oldExtraction.get(5, TimeUnit.SECONDS).toList())
            assertNull(ProjectStorage.loadCachedWaveform(project, 2))
            val newExtraction = ProjectStorage.loadOrExtractWaveform(project, beat, 2) { _, _ ->
                intArrayOf(3, 4)
            }
            assertEquals(listOf(3, 4), newExtraction.toList())
            assertEquals(listOf(3, 4), ProjectStorage.loadCachedWaveform(project, 2)?.toList())
        } finally {
            executor.shutdownNow()
        }
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
            assertTrue(
                "A failed import must not leave a staged file behind",
                project.listFiles().orEmpty().none { it.name.startsWith("beat-import-") },
            )
        }
    }

    @Test
    fun emptyImport_preservesExistingBeatAndMetadata() {
        val project = tempFolder.newFolder("empty_import")
        ProjectStorage.assignBeatToProject(project, "original.mp3") { it.writeText("original") }
        val metadataBefore = File(project, "project.json").readText()

        val result = runCatching {
            ProjectStorage.assignBeatToProject(project, "empty.wav") { it.writeBytes(byteArrayOf()) }
        }

        assertTrue(result.isFailure)
        assertEquals("original", File(project, "beat.mp3").readText())
        assertFalse(File(project, "beat.wav").exists())
        assertEquals(metadataBefore, File(project, "project.json").readText())
        assertTrue(project.listFiles().orEmpty().none { it.name.startsWith("beat-import-") })
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
    fun getProjectBeatFile_ignoresAnEmptyAssignedBeat() {
        val project = tempFolder.newFolder("empty_assigned_beat")
        File(project, "beat.mp3").createNewFile()

        assertNull(
            ProjectStorage.getProjectBeatFile(
                project,
                ProjectMetadata("empty_assigned_beat", beatFile = "beat.mp3"),
            ),
        )
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
    fun removeBeatFromProject_onlyDeletesSupportedProjectBeatFiles() {
        val project = tempFolder.newFolder("selective_beat_removal")
        File(project, "beat.mp3").writeText("assigned")
        File(project, "beat.wav").writeText("stale")
        File(project, "beat.txt").writeText("keep")
        File(project, "beat-remix.mp3").writeText("keep")
        File(project, "beat.flac").mkdir()

        ProjectStorage.removeBeatFromProject(project)

        assertFalse(File(project, "beat.mp3").exists())
        assertFalse(File(project, "beat.wav").exists())
        assertEquals("keep", File(project, "beat.txt").readText())
        assertEquals("keep", File(project, "beat-remix.mp3").readText())
        assertTrue(File(project, "beat.flac").isDirectory)
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

        assertEquals(assigned, ProjectStorage.getProjectBeatFile(projectDir, meta))
    }

    @Test
    fun assignBeatToProject_replacesOldBeatAndInvalidatesItsTimeline() {
        for (oldExtension in listOf("mp3", "wav")) {
            val projectDir = tempFolder.newFolder("replace_$oldExtension")
            File(projectDir, "beat.$oldExtension").writeText("old beat content")
            assertTrue(ProjectStorage.saveMetadata(projectDir, ProjectMetadata(
                title = "ReplacedBeatSong", beatFile = "beat.$oldExtension",
                markers = listOf(WaveformMarker("Hook", 12_000L)),
            )))
            assertTrue(ProjectStorage.saveCachedWaveform(projectDir, 2, intArrayOf(1, 2)))

            val source = tempFolder.newFile().apply { writeText("new mp3 content") }
            val assigned = ProjectStorage.assignBeatToProject(projectDir, source, "new_beat.mp3")

            assertEquals("beat.mp3", assigned.name)
            assertEquals("new mp3 content", assigned.readText())
            assertFalse(ProjectStorage.waveformCacheFile(projectDir).exists())
            assertEquals(setOf("beat.mp3", "project.json"), projectDir.list()!!.toSet())
            val meta = ProjectStorage.loadMetadata(projectDir, "ReplacedBeatSong")
            assertEquals("beat.mp3", meta.beatFile)
            assertTrue(meta.markers.isEmpty())
        }
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
    fun removeBeatFromProject_deletesBeatAndClearsItsTimeline() {
        val projectDir = tempFolder.newFolder("AcapellaTrack")
        val sourceBeat = tempFolder.newFile("temp_beat.mp3")
        sourceBeat.writeText("audio")
        ProjectStorage.assignBeatToProject(projectDir, sourceBeat, "temp_beat.mp3")
        assertTrue(ProjectStorage.saveMetadata(projectDir,
            ProjectStorage.loadMetadata(projectDir, "AcapellaTrack").copy(
                markers = listOf(WaveformMarker("Hook", 12_000L)),
            ),
        ))
        assertTrue(ProjectStorage.saveCachedWaveform(projectDir, 2, intArrayOf(3, 4)))

        val beatFile = File(projectDir, "beat.mp3")
        assertTrue(beatFile.exists())

        ProjectStorage.removeBeatFromProject(projectDir)

        assertFalse("Beat file should be deleted", beatFile.exists())
        assertFalse(ProjectStorage.waveformCacheFile(projectDir).exists())
        val meta = ProjectStorage.loadMetadata(projectDir, "AcapellaTrack")
        assertNull(meta.beatFile)
        assertNull(meta.beatOriginalName)
        assertTrue(meta.markers.isEmpty())
        assertNull(ProjectStorage.getProjectBeatFile(projectDir, meta))
    }

}
