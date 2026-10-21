package com.prosincerity.ghostwriter.data

import java.io.File
import java.io.IOException
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder

class StagedFileWriterTest {
    @get:Rule val temporaryFolder = TemporaryFolder()

    @Test fun failedWritePreservesLastUsableCopy() {
        val target = temporaryFolder.newFile("lyrics.txt").apply { writeText("previous verse") }
        assertThrows(IOException::class.java) {
            StagedFileWriter.replace(target, "save-", "replace failed") { staged ->
                staged.writeText("incomplete verse")
                throw IOException("storage disconnected")
            }
        }
        assertEquals("previous verse", target.readText())
        assertFalse(temporaryFolder.root.list()!!.any { it.endsWith(".tmp") })
    }

    @Test fun syncedReplacementPreservesUtf8AndEmptyLyrics() {
        val target = File(temporaryFolder.root, "lyrics.txt")
        StagedFileWriter.writeText(target, "Verse 🎵\n字")
        assertEquals("Verse 🎵\n字", target.readText())
        StagedFileWriter.writeText(target, "")
        assertEquals("", target.readText())
        assertFalse(temporaryFolder.root.list()!!.any { it.endsWith(".tmp") })
    }
}
