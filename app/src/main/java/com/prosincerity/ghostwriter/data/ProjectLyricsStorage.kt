package com.prosincerity.ghostwriter.data

import java.io.File

/** Manual lyric snapshots and the rolling autosave backup ring. */
internal object ProjectLyricsStorage {
    /** Most recent readable manual save or autosave, falling back through the backup ring. */
    fun loadLatest(projectDir: File): String {
        val manualFile = File(projectDir, "${projectDir.name}.txt")
        var newestReadableAutosave: Pair<File, String>? = null
        for (i in 1..5) {
            val file = File(projectDir, "autosave$i.txt")
            if (file.isFile) {
                val text = runCatching { file.readText() }.getOrNull()
                if (text != null) {
                    newestReadableAutosave = file to text
                    break
                }
            }
        }
        val manualText = manualFile.takeIf { it.isFile }
            ?.let { runCatching { it.readText() }.getOrNull() }
        return when {
            manualText != null &&
                (newestReadableAutosave == null ||
                    manualFile.lastModified() >= newestReadableAutosave.first.lastModified()) -> manualText
            newestReadableAutosave != null -> newestReadableAutosave.second
            else -> manualText ?: ""
        }
    }

    fun saveManual(projectDir: File, fileName: String, content: String, keepCount: Int): Boolean =
        runCatching {
            StagedFileWriter.writeText(File(projectDir, fileName), content)
            rotateAndSave(projectDir, content, keepCount)
        }.isSuccess

    /** An unchanged autosave does not consume another backup slot. */
    fun rotateAndSave(projectDir: File, content: String, keepCount: Int) {
        runCatching {
            require(keepCount in Settings.COUNT_OPTIONS)
            // Settings changes apply even when the lyrics haven't changed.
            for (i in (keepCount + 1)..10) {
                val oldBackup = File(projectDir, "autosave$i.txt")
                if (oldBackup.exists()) oldBackup.delete()
            }
            val newest = File(projectDir, "autosave1.txt")
            if (newest.exists() && newest.readText() == content) return

            for (i in keepCount downTo 2) {
                val src = File(projectDir, "autosave${i - 1}.txt")
                val dst = File(projectDir, "autosave$i.txt")
                if (src.exists()) {
                    src.copyTo(dst, overwrite = true)
                    // Recovery compares snapshots by age when autosave1 is lost.
                    dst.setLastModified(src.lastModified())
                }
            }

            StagedFileWriter.writeText(newest, content)
        }
    }
}
