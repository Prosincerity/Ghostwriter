package com.prosincerity.ghostwriter.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNull
import org.junit.Assert.assertThrows
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.File
import java.io.IOException
import java.util.concurrent.CancellationException

class EditorBeatImportTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @Test
    fun supportedFormatsPreserveOriginalNamesAndSaveTheAssignedMetadataToTheFolder() {
        for (extension in listOf("MP3", "WAV", "OGG", "FLAC", "M4A", "AAC")) {
            val project = project()
            val originalName = "Selected track.$extension"
            val result = importBeat(project, originalName, persist = { directory ->
                assertEquals(project, directory)
                assertEquals(originalName, ProjectStorage.loadMetadata(directory, "Track").beatOriginalName)
                assertEquals("audio", File(directory, "beat.${extension.lowercase()}").readText())
                true
            })

            assertEquals(File(project, "beat.${extension.lowercase()}"), result.file)
            assertEquals(result.file.name, result.metadata.beatFile)
            assertEquals(originalName, result.metadata.beatOriginalName)
            assertEquals("Track", result.metadata.title)
            assertEquals(92, result.metadata.bpm)
        }
    }

    @Test
    fun missingDisplayNameUsesTheExistingMp3Fallback() {
        val project = project()

        val result = importBeat(project, null)

        assertEquals("beat.mp3", result.file.name)
        assertEquals("beat.mp3", result.metadata.beatOriginalName)
        assertEquals("audio", result.file.readText())
    }

    @Test
    fun importInvalidatesTheSuppliedWaveformCacheDirectory() {
        val project = project()
        val waveformDirectory = temporaryFolder.newFolder()
        assertTrue(ProjectStorage.saveCachedWaveform(waveformDirectory, 2, intArrayOf(7, 8)))

        val result = importBeat(project, "selected.wav", waveformDirectory = waveformDirectory)

        assertNull(ProjectStorage.loadCachedWaveform(waveformDirectory, 2))
        assertEquals("audio", result.file.readText())
    }

    @Test
    fun invalidNamesRejectTheImportBeforeCopyingOrSavingTheFolder() {
        val project = projectWithBeat()
        val previousMetadata = File(project, "project.json").readText()

        for (name in listOf("", "beat", "beat.", "beat.txt", "beat.mp3 ")) {
            val failure = assertThrows(IllegalArgumentException::class.java) {
                importBeat(project, name,
                    persist = { error("Rejected imports must not save the folder") },
                    copy = { error("Rejected imports must not copy audio") })
            }

            assertEquals("Choose an MP3, WAV, OGG, FLAC, M4A, or AAC file", failure.message)
            assertEquals("original", File(project, "beat.wav").readText())
            assertEquals(previousMetadata, File(project, "project.json").readText())
        }
    }

    @Test
    fun failedCopyPreservesThePreviousBeatWithoutSavingTheFolder() {
        val project = projectWithBeat()
        val previousMetadata = File(project, "project.json").readText()
        val failure = IOException("Interrupted copy")

        val thrown = assertThrows(IOException::class.java) {
            importBeat(project, "replacement.wav",
                persist = { error("Failed copies must not save the folder") },
                copy = { it.writeText("partial"); throw failure })
        }

        assertEquals(failure, thrown)
        assertEquals("original", File(project, "beat.wav").readText())
        assertEquals(previousMetadata, File(project, "project.json").readText())
    }

    @Test
    fun emptyAudioIsRejectedWithoutSavingTheFolder() {
        val project = projectWithBeat()

        val failure = assertThrows(IOException::class.java) {
            importBeat(project, "replacement.wav",
                persist = { error("Empty audio must not save the folder") },
                copy = { it.writeBytes(byteArrayOf()) })
        }

        assertEquals("The selected beat is empty", failure.message)
        assertEquals("original", File(project, "beat.wav").readText())
    }

    @Test
    fun metadataFailureKeepsCopiedAudioWithoutSavingTheFolder() {
        val project = project()
        assertTrue(File(project, "project.json").delete())
        assertTrue(File(project, "project.json").mkdir())
        File(project, "project.json/keep").writeText("metadata")

        val failure = assertThrows(IOException::class.java) {
            importBeat(project, "selected.wav",
                persist = { error("Failed metadata writes must not save the folder") })
        }

        assertEquals("Couldn't save the beat's project information", failure.message)
        assertEquals("audio", File(project, "beat.wav").readText())
        assertEquals("metadata", File(project, "project.json/keep").readText())
    }

    @Test
    fun folderSaveFailureReportsTheFailureAndKeepsTheLocalBeatAndMetadata() {
        val project = project()

        val failure = assertThrows(IOException::class.java) {
            importBeat(project, "selected.wav", persist = { false })
        }

        assertEquals("Couldn't update the lyric folder; local draft kept", failure.message)
        assertEquals("audio", File(project, "beat.wav").readText())
        assertEquals("selected.wav", ProjectStorage.loadMetadata(project, "Track").beatOriginalName)
    }

    @Test
    fun folderSaveExceptionsPropagateWithoutDiscardingTheLocalBeat() {
        val project = project()
        val failure = IOException("Folder unavailable")

        val thrown = assertThrows(IOException::class.java) {
            importBeat(project, "selected.wav", persist = { throw failure })
        }

        assertEquals(failure, thrown)
        assertEquals("audio", File(project, "beat.wav").readText())
    }

    @Test
    fun cancelledCopyPropagatesWithoutReplacingTheBeatOrSavingTheFolder() {
        val project = projectWithBeat()
        val cancellation = CancellationException("Import cancelled")

        val thrown = assertThrows(CancellationException::class.java) {
            importBeat(project, "replacement.wav",
                persist = { error("Cancelled copies must not save the folder") },
                copy = { throw cancellation })
        }

        assertEquals(cancellation, thrown)
        assertEquals("original", File(project, "beat.wav").readText())
    }

    @Test
    fun resultReflectsMetadataAfterTheFolderSave() {
        val project = project()

        val result = importBeat(project, "selected.wav", persist = { directory ->
            ProjectStorage.saveMetadata(directory,
                ProjectStorage.loadMetadata(directory, "Track").copy(notes = "Updated during folder save"),
                now = { 2000L })
        })

        assertEquals("Updated during folder save", result.metadata.notes)
        assertEquals("selected.wav", result.metadata.beatOriginalName)
    }

    private fun project(): File = temporaryFolder.newFolder().also { directory ->
        assertTrue(ProjectStorage.saveMetadata(directory,
            ProjectMetadata("Track", bpm = 92, createdAt = 1000L, updatedAt = 1000L), now = { 1000L }))
    }

    private fun projectWithBeat(): File = project().also { directory ->
        ProjectStorage.assignBeatToProject(directory, "original.wav") { it.writeText("original") }
    }

    private fun importBeat(
        project: File,
        name: String?,
        waveformDirectory: File = project,
        persist: (File) -> Boolean = { true },
        copy: (File) -> Unit = { it.writeText("audio") },
    ): ImportedProjectBeat = importProjectBeat(
        projectDir = project,
        projectTitle = "Track",
        originalName = name,
        waveformDirectory = waveformDirectory,
        persistLyrics = persist,
        copyBeat = copy,
    )
}
