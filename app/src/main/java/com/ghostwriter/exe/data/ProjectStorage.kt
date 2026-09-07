package com.ghostwriter.exe.data

import android.content.Context
import java.io.File
import java.io.IOException

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
     * Permanently deletes the project directory for [title], including all
     * lyrics, autosaves, metadata, and any copied beat file.
     */
    fun deleteProject(context: Context, title: String): Boolean =
        deleteProjectDirectory(File(rootDir(context), sanitizeTitle(title)))

    @Synchronized
    internal fun deleteProjectDirectory(projectDir: File): Boolean {
        if (!projectDir.isDirectory) return false
        return runCatching { projectDir.deleteRecursively() }.getOrDefault(false)
    }

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
        // An older manual save is still preferable to losing all text when
        // every autosave is unreadable.
        return runCatching { manualFile.readText() }.getOrDefault("")
    }

    /**
     * Manually saves [content] as "<title>.txt" inside [projectDir].
     * Also synchronizes the autosave backup ring so the backup ring stays up to date.
     */
    @Synchronized
    fun saveManual(projectDir: File, title: String, content: String, keepCount: Int): Boolean =
        runCatching {
            val fileName = "${sanitizeTitle(title)}.txt"
            val target = File(projectDir, fileName)
            writeTextSafely(target, content)

            rotateAndSave(projectDir, content, keepCount)
        }.isSuccess

    /**
     * Rotates the backup ring and writes [content] as the new autosave1.txt.
     * No-ops if [content] already matches what's saved, so an idle editor
     * doesn't keep burning through backup slots.
     */
    @Synchronized
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
                if (src.exists()) src.copyTo(dst, overwrite = true)
            }

            writeTextSafely(newest, content)
        }
    }

    /** Replace only after writing succeeds; never delete the last good copy first. */
    private fun writeTextSafely(target: File, content: String) {
        val staged = File.createTempFile("save-", ".tmp", target.parentFile)
        try {
            staged.writeText(content)
            if (!staged.renameTo(target)) throw IOException("Couldn't replace ${target.name}")
        } finally {
            staged.delete()
        }
    }

    fun sanitizeTitle(title: String): String {
        val cleaned = title.trim()
            .replace(Regex("""[\\/:*?"<>|\x00-\x1F]"""), "_")
            .trim { it == '.' || it == ' ' }
        return cleaned.ifBlank { "untitled" }
    }

    /** Match the folder name used on disk, including an existing name's casing. */
    fun resolveProjectTitle(title: String, existingProjects: List<String>): String {
        val sanitized = sanitizeTitle(title)
        return existingProjects.firstOrNull { it.equals(sanitized, ignoreCase = true) } ?: sanitized
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

    @Synchronized
    fun saveMetadata(projectDir: File, metadata: ProjectMetadata): Boolean =
        runCatching {
            val file = metadataFile(projectDir)
            val updated = metadata.copy(updatedAt = System.currentTimeMillis())
            writeTextSafely(file, updated.toJsonObject().toString(2))
        }.isSuccess

    // --- Beat & Instrumental storage helpers ---

    val SUPPORTED_AUDIO_EXTENSIONS: Set<String> = setOf("mp3", "wav", "ogg", "flac", "m4a", "aac")

    fun instrumentalsDir(context: Context): File =
        File(context.getExternalFilesDir(null), "instrumentals").apply { mkdirs() }

    /**
     * Lists all supported audio beat files in [dir], sorted alphabetically.
     */
    fun listInstrumentals(dir: File): List<File> =
        dir.listFiles { file ->
            file.isFile && file.extension.lowercase() in SUPPORTED_AUDIO_EXTENSIONS
        }?.sortedBy { it.name.lowercase() } ?: emptyList()

    /**
     * Resolves the assigned beat file for [projectDir].
     * Looks up metadata first, then falls back to any existing `beat.*` file.
     */
    fun getProjectBeatFile(projectDir: File, metadata: ProjectMetadata? = null): File? {
        val meta = metadata ?: loadMetadata(projectDir, projectDir.name)
        val fileName = meta.beatFile
        if (!fileName.isNullOrBlank()) {
            val file = File(projectDir, fileName)
            if (file.isFile && file.canonicalFile.parentFile == projectDir.canonicalFile &&
                file.extension.lowercase() in SUPPORTED_AUDIO_EXTENSIONS
            ) return file
        }
        // Fallback: check if a beat file exists on disk
        return projectDir.listFiles { f ->
            f.isFile && f.nameWithoutExtension == "beat" && f.extension.lowercase() in SUPPORTED_AUDIO_EXTENSIONS
        }?.firstOrNull()
    }

    /**
     * Assigns [sourceFile] as the beat for [projectDir].
     * Copies the file to `projectDir/beat.<ext>`, removes any previous beat file
     * with a different extension, and updates `project.json`.
     */
    @Synchronized
    fun assignBeatToProject(
        projectDir: File,
        sourceFile: File,
        originalName: String = sourceFile.name,
    ): File = assignBeatToProject(projectDir, originalName) { destination ->
        sourceFile.copyTo(destination, overwrite = true)
    }

    /**
     * Copies a selected beat into [projectDir] via [copyAction], preserving the
     * original name in metadata while storing the project copy as `beat.<ext>`.
     */
    @Synchronized
    fun assignBeatToProject(
        projectDir: File,
        originalName: String,
        copyAction: (destination: File) -> Unit,
    ): File {
        val ext = originalName.substringAfterLast('.', "").lowercase()
        require(ext in SUPPORTED_AUDIO_EXTENSIONS) { "Unsupported audio file extension" }
        val destFile = File(projectDir, "beat.$ext")

        // A failed stream copy must not truncate or delete the previous beat.
        val stagedFile = File.createTempFile("beat-import-", ".tmp", projectDir)
        try {
            copyAction(stagedFile)
            if (!stagedFile.renameTo(destFile)) {
                throw IOException("Couldn't store the selected beat")
            }
        } finally {
            stagedFile.delete()
        }

        // Remove the old format only after the new file has been copied.
        projectDir.listFiles { f ->
            f.isFile && f.nameWithoutExtension == "beat" && f.extension.lowercase() in SUPPORTED_AUDIO_EXTENSIONS && f != destFile
        }?.forEach { it.delete() }

        val currentMeta = loadMetadata(projectDir, projectDir.name)
        val updatedMeta = currentMeta.copy(
            beatFile = destFile.name,
            beatOriginalName = originalName,
        )
        if (!saveMetadata(projectDir, updatedMeta)) {
            throw IOException("Couldn't save the beat's project information")
        }

        return destFile
    }

    /**
     * Unassigns and removes the beat from [projectDir], updating `project.json`.
     */
    @Synchronized
    fun removeBeatFromProject(projectDir: File) {
        projectDir.listFiles { f ->
            f.isFile && f.nameWithoutExtension == "beat" && f.extension.lowercase() in SUPPORTED_AUDIO_EXTENSIONS
        }?.forEach { it.delete() }

        val currentMeta = loadMetadata(projectDir, projectDir.name)
        val updatedMeta = currentMeta.copy(
            beatFile = null,
            beatOriginalName = null,
        )
        saveMetadata(projectDir, updatedMeta)
    }

    /**
     * Imports an audio file into [instrumentalsDir] via [copyAction].
     */
    fun importInstrumental(
        instrumentalsDir: File,
        fileName: String,
        copyAction: (destination: File) -> Unit,
    ): File {
        val baseName = sanitizeTitle(fileName.substringBeforeLast('.'))
        val ext = if (fileName.contains('.')) fileName.substringAfterLast('.').lowercase() else "mp3"
        val dest = File(instrumentalsDir, "$baseName.$ext")
        copyAction(dest)
        return dest
    }
}
