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
        File(rootDir(context), sanitize(title)).apply { mkdirs() }

    fun listProjects(context: Context): List<String> =
        rootDir(context).listFiles { f -> f.isDirectory }
            ?.map { it.name }
            ?.sorted()
            ?: emptyList()

    /** Most recent saved content for a project, or an empty string for a brand-new one. */
    fun loadLatest(projectDir: File): String {
        val latest = File(projectDir, "autosave1.txt")
        return if (latest.exists()) latest.readText() else ""
    }

    /**
     * Rotates the backup ring and writes [content] as the new autosave1.txt.
     * No-ops if [content] already matches what's saved, so an idle editor
     * doesn't keep burning through backup slots.
     */
    fun rotateAndSave(projectDir: File, content: String, keepCount: Int) {
        val newest = File(projectDir, "autosave1.txt")
        if (newest.exists() && newest.readText() == content) return

        for (i in keepCount downTo 2) {
            val src = File(projectDir, "autosave${i - 1}.txt")
            val dst = File(projectDir, "autosave$i.txt")
            if (src.exists()) src.copyTo(dst, overwrite = true)
        }
        newest.writeText(content)
    }

    private fun sanitize(title: String): String {
        val cleaned = title.trim().replace(Regex("""[\\/:*?"<>|]"""), "_")
        return cleaned.ifBlank { "untitled" }
    }
}
