package com.prosincerity.ghostwriter.data

import android.content.Context
import com.prosincerity.ghostwriter.logic.WaveformExtractor
import java.io.File
import java.io.IOException
import java.util.concurrent.CancellationException

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

    private const val MAX_PROJECT_TITLE_UTF8_BYTES = 251
    // Checked under the storage monitor before committing a decoded waveform.
    private var beatMutationRevision = 0L

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

    /**
     * Renames a project without changing its contents. The title-based manual
     * save file and the metadata title are updated to match the new folder.
     *
     * @return the renamed project title, or null if the source is missing, the
     * destination already exists, or the rename cannot be completed safely.
     */
    fun renameProject(context: Context, currentTitle: String, requestedTitle: String): String? =
        renameProjectDirectory(
            projectDir = File(rootDir(context), sanitizeTitle(currentTitle)),
            requestedTitle = requestedTitle,
        )?.name

    @Synchronized
    internal fun deleteProjectDirectory(projectDir: File): Boolean {
        if (!projectDir.isDirectory) return false
        return runCatching { projectDir.deleteRecursively() }.getOrDefault(false)
    }

    @Synchronized
    internal fun renameProjectDirectory(projectDir: File, requestedTitle: String): File? {
        if (!projectDir.isDirectory) return null

        val renamedTitle = sanitizeTitle(requestedTitle)
        if (projectDir.name == renamedTitle) return projectDir

        val renamedProjectDir = File(projectDir.parentFile ?: return null, renamedTitle)
        if (renamedProjectDir.exists()) return null

        val originalManualSave = File(projectDir, "${projectDir.name}.txt")
        val renamedManualSave = File(projectDir, "$renamedTitle.txt")
        val manualSaveWasRenamed = originalManualSave.isFile
        if (manualSaveWasRenamed && renamedManualSave.exists()) return null
        if (manualSaveWasRenamed && !originalManualSave.renameTo(renamedManualSave)) return null

        if (!projectDir.renameTo(renamedProjectDir)) {
            if (manualSaveWasRenamed) renamedManualSave.renameTo(originalManualSave)
            return null
        }

        val renamedMetadata = loadMetadata(renamedProjectDir, renamedTitle).copy(title = renamedTitle)
        if (!saveMetadata(renamedProjectDir, renamedMetadata)) {
            // Keep the old project intact if its metadata cannot be updated.
            if (renamedProjectDir.renameTo(projectDir) && manualSaveWasRenamed) {
                File(projectDir, "$renamedTitle.txt").renameTo(originalManualSave)
            }
            return null
        }

        return renamedProjectDir
    }

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

    /**
     * Manually saves [content] as "<title>.txt" inside [projectDir].
     * Also synchronizes the autosave backup ring so the backup ring stays up to date.
     */
    @Synchronized
    fun saveManual(projectDir: File, title: String, content: String, keepCount: Int): Boolean =
        runCatching {
            val fileName = "${sanitizeTitle(title)}.txt"
            val target = File(projectDir, fileName)
            StagedFileWriter.writeText(target, content)

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
                if (src.exists()) {
                    src.copyTo(dst, overwrite = true)
                    // Recovery compares snapshots by age when autosave1 is lost.
                    dst.setLastModified(src.lastModified())
                }
            }

            StagedFileWriter.writeText(newest, content)
        }
    }

    fun sanitizeTitle(title: String): String {
        val cleaned = title.trim()
            .replace(Regex("""[\\/:*?"<>|\x00-\x1F]"""), "_")
            .trim { it == '.' || it == ' ' }
        val truncated = cleaned.truncateToUtf8Bytes(MAX_PROJECT_TITLE_UTF8_BYTES)
            .trimEnd { it == '.' || it == ' ' }
        return truncated.ifBlank { "untitled" }
    }

    private fun String.truncateToUtf8Bytes(maxBytes: Int): String {
        var byteCount = 0
        var endIndex = 0
        while (endIndex < length) {
            val codePoint = codePointAt(endIndex)
            val codePointBytes = when {
                codePoint <= 0x7F -> 1
                codePoint <= 0x7FF -> 2
                codePoint <= 0xFFFF -> 3
                else -> 4
            }
            if (byteCount + codePointBytes > maxBytes) break
            byteCount += codePointBytes
            endIndex += Character.charCount(codePoint)
        }
        return substring(0, endIndex)
    }

    /** Match the folder name used on disk, including an existing name's casing. */
    fun resolveProjectTitle(title: String, existingProjects: List<String>): String {
        val sanitized = sanitizeTitle(title)
        return existingProjects.firstOrNull { it.equals(sanitized, ignoreCase = true) } ?: sanitized
    }

    fun metadataFile(projectDir: File): File =
        File(projectDir, "project.json")

    /** Project-local waveform cache for the currently assigned beat. */
    fun waveformCacheFile(projectDir: File): File =
        WaveformCache.file(projectDir)

    /**
     * Returns a cached waveform with exactly [targetSampleCount] samples, or
     * null when no compatible, readable cache is present. Empty extraction
     * results are failures and are never treated as a valid cache.
     */
    fun loadCachedWaveform(projectDir: File, targetSampleCount: Int): IntArray? =
        WaveformCache.load(projectDir, targetSampleCount)

    @Synchronized
    internal fun saveCachedWaveform(
        projectDir: File,
        targetSampleCount: Int,
        amplitudes: IntArray,
    ): Boolean = WaveformCache.save(projectDir, targetSampleCount, amplitudes)

    /** Deletes the cache so the next request decodes the currently assigned beat again. */
    @Synchronized
    internal fun invalidateWaveformCache(projectDir: File): Boolean =
        WaveformCache.invalidate(projectDir)

    /**
     * Loads a compatible cache when possible; otherwise decodes [beatFile]
     * and stores the result for the next editor open. Call from Dispatchers.IO.
     */
    fun loadOrExtractWaveform(
        projectDir: File,
        beatFile: File,
        targetSampleCount: Int = WaveformExtractor.DEFAULT_TARGET_SAMPLE_COUNT,
        shouldCancel: () -> Boolean = { false },
    ): IntArray = loadOrExtractWaveform(
        projectDir = projectDir,
        beatFile = beatFile,
        targetSampleCount = targetSampleCount,
        shouldCancel = shouldCancel,
        extract = { file, sampleCount ->
            WaveformExtractor.extractAmplitudes(file, sampleCount, shouldCancel)
        },
    )

    internal fun loadOrExtractWaveform(
        projectDir: File,
        beatFile: File,
        targetSampleCount: Int,
        shouldCancel: () -> Boolean = { false },
        extract: (File, Int) -> IntArray,
    ): IntArray {
        if (
            targetSampleCount !in 1..WaveformCache.MAX_SAMPLES ||
            !beatFile.isFile ||
            runCatching { beatFile.canonicalFile.parentFile == projectDir.canonicalFile }.getOrDefault(false).not()
        ) return IntArray(0)

        val beatRevisionAtStart = synchronized(this) { beatMutationRevision }
        throwIfWaveformCancelled(shouldCancel)
        loadCachedWaveform(projectDir, targetSampleCount)?.let { return it }

        // Decoding can take tens of seconds on some devices. Do not hold the
        // ProjectStorage monitor while it runs: lyrics and metadata use that
        // monitor for short atomic writes and must remain responsive.
        val amplitudes = extract(beatFile, targetSampleCount)
        throwIfWaveformCancelled(shouldCancel)
        if (amplitudes.size == targetSampleCount) {
            synchronized(this) {
                if (beatMutationRevision == beatRevisionAtStart) {
                    saveCachedWaveform(projectDir, targetSampleCount, amplitudes)
                }
            }
        }
        return amplitudes
    }

    private fun throwIfWaveformCancelled(shouldCancel: () -> Boolean) {
        if (shouldCancel()) throw CancellationException("Waveform extraction cancelled")
    }

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
            val updated = metadata.copy(
                updatedAt = System.currentTimeMillis(),
                version = maxOf(metadata.version, ProjectMetadata.CURRENT_VERSION),
            )
            StagedFileWriter.writeText(file, updated.toJsonObject().toString(2))
        }.isSuccess

    // --- Project beat storage helpers ---

    val SUPPORTED_AUDIO_EXTENSIONS: Set<String> = setOf("mp3", "wav", "ogg", "flac", "m4a", "aac")

    /**
     * Resolves the assigned beat file for [projectDir].
     * Looks up metadata first, then falls back to any existing `beat.*` file.
     */
    fun getProjectBeatFile(projectDir: File, metadata: ProjectMetadata? = null): File? {
        val meta = metadata ?: loadMetadata(projectDir, projectDir.name)
        val fileName = meta.beatFile
        if (!fileName.isNullOrBlank()) {
            val file = File(projectDir, fileName)
            if (file.isSupportedAudioFile() && file.canonicalFile.parentFile == projectDir.canonicalFile) {
                return file
            }
        }
        // Fallback: check if a beat file exists on disk
        return projectDir.listFiles { file -> file.isProjectBeatFile() }?.firstOrNull()
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
        StagedFileWriter.replace(
            target = destFile,
            tempFilePrefix = "beat-import-",
            replacementFailureMessage = "Couldn't store the selected beat",
        ) { stagedFile ->
            copyAction(stagedFile)
            if (!stagedFile.isFile || stagedFile.length() == 0L) {
                throw IOException("The selected beat is empty")
            }
        }
        beatMutationRevision++

        // Remove the old format only after the new file has been copied.
        projectDir.listFiles { file -> file.isProjectBeatFile() && file != destFile }
            ?.forEach { it.delete() }

        invalidateWaveformCache(projectDir)

        val currentMeta = loadMetadata(projectDir, projectDir.name)
        val updatedMeta = currentMeta.copy(
            beatFile = destFile.name,
            beatOriginalName = originalName,
            // Marker positions belong to the old timeline. A replacement beat
            // has a different duration/arrangement, so it starts unmarked.
            markers = emptyList(),
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
        projectDir.listFiles { file -> file.isProjectBeatFile() }?.forEach { it.delete() }
        beatMutationRevision++
        invalidateWaveformCache(projectDir)

        val currentMeta = loadMetadata(projectDir, projectDir.name)
        val updatedMeta = currentMeta.copy(
            beatFile = null,
            beatOriginalName = null,
            markers = emptyList(),
        )
        saveMetadata(projectDir, updatedMeta)
    }

    private fun File.isSupportedAudioFile(): Boolean =
        isFile && length() > 0L && extension.lowercase() in SUPPORTED_AUDIO_EXTENSIONS

    private fun File.isProjectBeatFile(): Boolean =
        nameWithoutExtension == "beat" && isSupportedAudioFile()
}
