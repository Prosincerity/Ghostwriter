package com.prosincerity.ghostwriter.data

import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/** Writes a complete temporary file before replacing the current copy. */
internal object StagedFileWriter {
    fun writeText(target: File, content: String) {
        replace(target, "save-", "Couldn't replace ${target.name}") { staged ->
            staged.writeText(content)
        }
    }

    fun replace(
        target: File,
        tempFilePrefix: String,
        replacementFailureMessage: String,
        writeStagedFile: (File) -> Unit,
    ) {
        val staged = File.createTempFile(tempFilePrefix, ".tmp", target.parentFile)
        try {
            writeStagedFile(staged)
            // All writers (including streamed beat copies and backup rotation)
            // must close/flush their output before returning. Sync the complete
            // staged file before replacing the last usable copy.
            FileOutputStream(staged, true).use { it.fd.sync() }
            if (!staged.renameTo(target)) throw IOException(replacementFailureMessage)
        } finally {
            staged.delete()
        }
    }
}
