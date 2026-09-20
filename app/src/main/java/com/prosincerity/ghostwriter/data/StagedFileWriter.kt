package com.prosincerity.ghostwriter.data

import java.io.File
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
            if (!staged.renameTo(target)) throw IOException(replacementFailureMessage)
        } finally {
            staged.delete()
        }
    }
}
