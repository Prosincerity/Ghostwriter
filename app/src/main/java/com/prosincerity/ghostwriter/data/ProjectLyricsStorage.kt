package com.prosincerity.ghostwriter.data

import java.io.File

/** Manual lyric snapshots and the rolling autosave backup ring. */
internal object ProjectLyricsStorage {
    const val MANUAL_FILE_NAME = "lyrics.txt"
    /** Most recent readable manual save or autosave, falling back through the backup ring. */
    fun loadLatest(projectDir: File): String {
        // Legacy title snapshots remain readable after UUID migration and rename.
        val manualFiles = projectDir.listFiles { file ->
            file.isFile && file.extension == "txt" &&
                !Regex("autosave(?:[1-9]|10)\\.txt", RegexOption.IGNORE_CASE).matches(file.name)
        }.orEmpty()
        val manualFile = manualFiles.filter { it.readTextIfFile() != null }
            .maxWithOrNull(compareBy<File> { it.lastModified() }.thenBy { it.name == MANUAL_FILE_NAME })
            ?: File(projectDir, MANUAL_FILE_NAME)
        // Older versions could keep ten slots. Best-effort rotations can also
        // leave slot ages out of order, so compare every readable snapshot.
        // Scanning in slot order keeps the earlier slot when timestamps tie.
        val newestReadableAutosave = (1..10).asSequence()
            .mapNotNull { i ->
                val file = File(projectDir, "autosave$i.txt")
                file.readTextIfFile()?.let { file to it }
            }
            .maxByOrNull { it.first.lastModified() }
        val manualText = manualFile.readTextIfFile()
        return when {
            manualText != null &&
                (newestReadableAutosave == null ||
                    manualFile.lastModified() >= newestReadableAutosave.first.lastModified()) -> manualText
            newestReadableAutosave != null -> newestReadableAutosave.second
            // Older versions used a backup filename for these titles. Do not
            // rank that alias as a manual save ahead of the newest ring entry.
            else -> manualText ?: File(projectDir, "${projectDir.name}.txt").readTextIfFile() ?: ""
        }
    }

    private fun File.readTextIfFile(): String? =
        if (isFile) runCatching { readText() }.getOrNull() else null

    fun saveManual(projectDir: File, fileName: String, content: String, keepCount: Int): Boolean =
        runCatching {
            StagedFileWriter.writeText(File(projectDir, fileName), content)
            rotateAndSave(projectDir, content, keepCount)
        }.isSuccess

    /** An unchanged autosave does not consume another backup slot. */
    fun rotateAndSave(projectDir: File, content: String, keepCount: Int): Boolean =
        runCatching {
            require(keepCount in Settings.COUNT_OPTIONS)
            val newest = File(projectDir, "autosave1.txt")
            if (newest.readTextIfFile() != content) {
                for (i in keepCount downTo 2) {
                    val src = File(projectDir, "autosave${i - 1}.txt")
                    val dst = File(projectDir, "autosave$i.txt")
                    // An unreadable/blocked older slot must not stop the current
                    // lyrics from reaching disk. Stage copies to preserve the
                    // destination if a backup copy fails partway through.
                    if (src.isFile) runCatching {
                        StagedFileWriter.replace(dst, "save-", "Couldn't rotate ${dst.name}") { staged ->
                            src.copyTo(staged, overwrite = true)
                            // Recovery compares snapshots by age when autosave1 is lost.
                            staged.setLastModified(src.lastModified())
                        }
                    }
                }

                StagedFileWriter.writeText(newest, content)
            }
            // Prune only after the latest snapshot is safe. Settings changes
            // still apply when the lyrics haven't changed.
            for (i in (keepCount + 1)..10) {
                val oldBackup = File(projectDir, "autosave$i.txt")
                if (oldBackup.isFile) oldBackup.delete()
            }
        }.isSuccess
}
