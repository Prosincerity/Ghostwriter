package com.prosincerity.ghostwriter.projects.data

import java.io.File
import java.io.IOException
import java.io.RandomAccessFile
import java.nio.file.Files
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class ProjectBackupFilesTest {
    @get:Rule val folder = TemporaryFolder()
    private val root get() = File(folder.root, "external/ghostwriter")
    private val preferences get() = File(folder.root, "shared_prefs/ghostwriter_settings.xml")

    private fun write(file: File): File = file.apply { parentFile!!.mkdirs(); writeText("Retained data") }

    @Test fun includesSettingsLyricsMetadataAndInterruptedRecoveryState() {
        val expected = setOf(
            write(preferences),
            write(File(root, "project/lyrics.txt")),
            write(File(root, "project/autosave1.txt")),
            write(File(root, "project/AUTOSAVE10.TXT")),
            write(File(root, "project/project.json")),
            write(File(root, "project/.lyric-archive.json")),
            write(File(root, "project/.lyric-restore.json")),
            write(File(root, "Legacy title/Legacy title.txt")),
        )

        assertEquals(expected, projectBackupFiles(root, preferences).toSet())
    }

    @Test fun largeAudioAndRegenerableAssetsDoNotDisplaceWritingData() {
        val lyrics = write(File(root, "project/lyrics.txt"))
        val audio = File(root, "project/beat.wav")
        RandomAccessFile(audio, "rw").use { it.setLength(30L * 1024 * 1024) }
        write(File(root, "project/beat.mp3"))
        write(File(root, "project/waveform.dat"))
        write(File(root, "project/save-interrupted.tmp"))
        write(File(root, ".staging/lyrics.txt"))
        write(File(folder.root, "files/dictionaries/en/wiktionary.db"))
        write(File(folder.root, "files/espeak-ng/phondata"))

        assertEquals(listOf(lyrics), projectBackupFiles(root, preferences))
    }

    @Test fun missingProjectsStillAllowSettingsToBeBackedUp() {
        write(preferences)

        assertEquals(listOf(preferences), projectBackupFiles(root, preferences))
    }

    @Test fun missingSettingsDoNotDropLocalDrafts() {
        val lyrics = write(File(root, "project/lyrics.txt"))

        assertEquals(listOf(lyrics), projectBackupFiles(root, preferences))
    }

    @Test fun listingFailureAbortsInsteadOfPublishingAnIncompleteBackup() {
        write(File(root, "project/lyrics.txt"))
        val unavailable = object : File(root.path) {
            override fun listFiles(): Array<File>? = null
        }

        assertThrows(IOException::class.java) { projectBackupFiles(unavailable, preferences) }
    }

    @Test fun symlinksCannotIncludeFilesOutsideTheProjectStorage() {
        val unrelated = write(File(folder.root, "unrelated/private.txt"))
        val project = File(root, "project").apply { mkdirs() }
        Files.createSymbolicLink(File(project, "lyrics.txt").toPath(), unrelated.toPath())
        Files.createSymbolicLink(File(root, "linked-project").toPath(), unrelated.parentFile!!.toPath())

        assertEquals(emptyList<File>(), projectBackupFiles(root, preferences))
    }
}
