package com.ghostwriter.exe.data

import android.content.Context
import java.io.File

/**
 * Handles the on-disk layout for lyric projects.
 *
 * Layout, under the app's own external files directory (no storage
 * permission needed, private to this app, removed on uninstall):
 *
 *   <externalFilesDir>/ghostwriter/<project title>/autosave1.txt   (newest)
 *   <externalFilesDir>/ghostwriter/<project title>/autosave2.txt
 *   ...
 *
 * autosave1.txt is always the most recent snapshot. On each autosave
 * tick, older files shift up by one index (oldest beyond the configured
 * count is dropped) and autosave1.txt is overwritten with the current
 * text — a small rolling backup ring buffer.
 *
 * NOTE: this directory isn't easily browsable from a stock file manager
 * (Android's scoped storage rules hide other apps' external-files
 * directories from general browsing). That's fine for autosave/backup
 * purposes, but when real Import/Export is built, that feature should
 * use the Storage Access Framework so the user can point at a folder
 * they actually see and manage themselves.
 */
object ProjectStorage {

    fun rootDir(context: Context): File =
        File(context.getExternalFilesDir(null), "ghostwriter").apply { mkdirs() }

    fun projectDir(context: Context, title: String): File =
        File(rootDir(context), sanitizeTitle(title)).apply { mkdirs() }

    fun listProjects(context: Context): List<String> =
        rootDir(context).listFiles { f -> f.isDirectory && !f.name.startsWith(".") }
            ?.map { it.name }
            ?.sorted()
            ?: emptyList()

    /**
     * Most recent saved content for a project. Checks <title>.txt first if present
     * and up-to-date, then checks autosave1.txt, and falls back to older backups
     * if autosave1 is missing or unreadable.
     */
    fun loadLatest(projectDir: File): String {
        val manualFile = File(projectDir, "${projectDir.name}.txt")
        val autosaveFile = File(projectDir, "autosave1.txt")

        if (manualFile.exists() && (!autosaveFile.exists() || manualFile.lastModified() >= autosaveFile.lastModified())) {
            val text = runCatching { manualFile.readText() }.getOrNull()
            if (text != null) return text
        }

        for (i in 1..5) {
            val file = File(projectDir, "autosave$i.txt")
            if (file.exists()) {
                val text = runCatching { file.readText() }.getOrNull()
                if (text != null) return text
            }
        }
        return ""
    }

    /**
     * Manually saves [content] as "<title>.txt" inside [projectDir].
     * Also synchronizes the autosave backup ring so the backup ring stays up to date.
     */
    fun saveManual(projectDir: File, title: String, content: String, keepCount: Int) {
        runCatching {
            val fileName = "${sanitizeTitle(title)}.txt"
            val target = File(projectDir, fileName)
            val temp = File(projectDir, "$fileName.tmp")
            temp.writeText(content)
            if (target.exists()) {
                target.delete()
            }
            if (!temp.renameTo(target)) {
                temp.copyTo(target, overwrite = true)
                temp.delete()
            }

            rotateAndSave(projectDir, content, keepCount)
        }
    }

    /**
     * Rotates the backup ring and writes [content] as the new autosave1.txt.
     * No-ops if [content] already matches what's saved, so an idle editor
     * doesn't keep burning through backup slots.
     */
    fun rotateAndSave(projectDir: File, content: String, keepCount: Int) {
        runCatching {
            val newest = File(projectDir, "autosave1.txt")
            if (newest.exists() && newest.readText() == content) return

            for (i in keepCount downTo 2) {
                val src = File(projectDir, "autosave${i - 1}.txt")
                val dst = File(projectDir, "autosave$i.txt")
                if (src.exists()) src.copyTo(dst, overwrite = true)
            }

            // Prune any backups beyond keepCount (e.g. if keepCount was reduced in Settings)
            for (i in (keepCount + 1)..10) {
                val oldBackup = File(projectDir, "autosave$i.txt")
                if (oldBackup.exists()) oldBackup.delete()
            }

            // Write to a temporary file first, then replace newest to prevent corruption on crash
            val temp = File(projectDir, "autosave1.tmp")
            temp.writeText(content)
            if (newest.exists()) {
                newest.delete()
            }
            if (!temp.renameTo(newest)) {
                temp.copyTo(newest, overwrite = true)
                temp.delete()
            }
        }
    }

    fun sanitizeTitle(title: String): String {
        val cleaned = title.trim()
            .replace(Regex("""[\\/:*?"<>|\x00-\x1F]"""), "_")
            .trim { it == '.' || it == ' ' }
        return cleaned.ifBlank { "untitled" }
    }

    fun metadataFile(projectDir: File): File =
        File(projectDir, "project.json")

    fun loadMetadata(projectDir: File, fallbackTitle: String): ProjectMetadata {
        val file = metadataFile(projectDir)
        if (!file.exists()) {
            return ProjectMetadata(title = fallbackTitle)
        }
        return runCatching {
            ProjectMetadata.fromJsonString(file.readText(), fallbackTitle)
        }.getOrElse {
            ProjectMetadata(title = fallbackTitle)
        }
    }

    fun saveMetadata(projectDir: File, metadata: ProjectMetadata) {
        runCatching {
            val file = metadataFile(projectDir)
            val temp = File(projectDir, "project.json.tmp")
            val updated = metadata.copy(updatedAt = System.currentTimeMillis())
            temp.writeText(updated.toJsonObject().toString(2))
            if (file.exists()) {
                file.delete()
            }
            if (!temp.renameTo(file)) {
                temp.copyTo(file, overwrite = true)
                temp.delete()
            }
        }
    }
}
