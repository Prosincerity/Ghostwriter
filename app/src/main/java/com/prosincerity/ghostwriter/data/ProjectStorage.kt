package com.prosincerity.ghostwriter.data

import android.content.Context
import com.prosincerity.ghostwriter.logic.WaveformExtractor
import java.io.File
import java.io.IOException
import java.util.concurrent.CancellationException
import java.util.UUID
import org.json.JSONObject

/** App-local project working copies, addressed by UUID rather than display title. */
object ProjectStorage {

    private const val MAX_PROJECT_TITLE_UTF8_BYTES = 251
    // Checked under the storage monitor before committing a decoded waveform.
    private var beatMutationRevision = 0L

    fun rootDir(context: Context): File =
        File(context.getExternalFilesDir(null), "ghostwriter").apply { mkdirs() }

    /** UUIDs are path components; legacy title callers resolve through metadata. */
    @Synchronized
    fun projectDir(context: Context, projectId: String): File {
        val root = rootDir(context)
        migrateLegacyProjects(root)
        if (isProjectId(projectId)) return File(root, projectId).also {
            require(it.isDirectory) { "Project no longer exists" }
        }
        val title = normalizeTitle(projectId)
        return root.listFiles()?.firstOrNull {
            it.isDirectory && loadMetadata(it, it.name).title == title
        } ?: createProjectDirectory(root, title)
    }

    @Synchronized
    fun listProjects(context: Context): List<String> {
        val root = rootDir(context)
        migrateLegacyProjects(root)
        val directories = root.listFiles { f -> f.isDirectory && isProjectId(f.name) }.orEmpty()
        // Old releases kept this regenerable cache alongside backed-up project data.
        directories.forEach { WaveformCache.invalidate(it) }
        return directories.map { it.name }.sorted()
    }

    fun createProject(context: Context, title: String): File =
        createProjectDirectory(rootDir(context), title)

    @Synchronized
    internal fun createProjectDirectory(root: File, title: String): File {
        val directory = File(root, UUID.randomUUID().toString())
        check(directory.mkdir()) { "Couldn't create project" }
        if (!saveMetadata(directory, ProjectMetadata(title = normalizeTitle(title)))) {
            directory.delete()
            throw IOException("Couldn't save project information")
        }
        return directory
    }

    internal fun isProjectId(value: String): Boolean =
        runCatching { UUID.fromString(value).toString() == value }.getOrDefault(false)

    fun normalizeTitle(title: String): String = title.trim().ifBlank { "Untitled" }

    /** Convert old title folders once; keep every lyric/beat file and snapshot age. */
    @Synchronized
    internal fun migrateLegacyProjects(root: File) {
        root.listFiles { file -> file.isDirectory && !file.name.startsWith(".") && !isProjectId(file.name) }
            ?.forEach { source ->
                val metadata = loadMetadata(source, source.name)
                val destination = File(root, UUID.randomUUID().toString())
                // Persist the display title before changing its fallback folder name.
                // A failed migration leaves the original directory available for retry.
                runCatching {
                    val json = runCatching { JSONObject(metadataFile(source).readText()) }
                        .getOrElse { metadata.toJsonObject() }
                    json.put("title", metadata.title)
                    // Freeze old missing timestamps once; otherwise the tolerant
                    // reader invents new times on every shared save/checkpoint.
                    val metadataTime = metadataFile(source).lastModified().takeIf { it > 0L }
                        ?: source.lastModified()
                    if (!json.has("createdAt")) json.put("createdAt", metadataTime)
                    if (!json.has("updatedAt")) json.put("updatedAt", maxOf(metadataTime,
                        source.listFiles { it.isFile && it.extension == "txt" }
                            ?.maxOfOrNull { it.lastModified() } ?: 0L))
                    StagedFileWriter.writeText(metadataFile(source), json.toString(2))
                    if (!source.renameTo(destination)) throw IOException("Couldn't migrate ${source.name}")
                }.getOrThrow()
            }
    }

    fun deleteProject(context: Context, projectId: String): Boolean {
        val root = rootDir(context)
        val directory = if (isProjectId(projectId)) File(root, projectId) else
            root.listFiles()?.firstOrNull { it.isDirectory && loadMetadata(it, it.name).title == projectId }
                ?: return false
        return deleteProjectDirectory(directory)
    }

    /** Renaming changes metadata only; project identity, lyrics and playback paths stay stable. */
    fun renameProject(context: Context, projectId: String, requestedTitle: String): String? {
        val root = rootDir(context)
        val directory = if (isProjectId(projectId)) File(root, projectId) else
            root.listFiles()?.firstOrNull { it.isDirectory && loadMetadata(it, it.name).title == projectId }
                ?: return null
        return renameProjectDirectory(directory, requestedTitle)?.name
    }

    @Synchronized
    internal fun deleteProjectDirectory(projectDir: File): Boolean {
        if (!projectDir.isDirectory) return false
        return runCatching { projectDir.deleteRecursively() }.getOrDefault(false)
    }

    @Synchronized
    internal fun renameProjectDirectory(projectDir: File, requestedTitle: String): File? {
        if (!projectDir.isDirectory) return null
        val metadata = loadMetadata(projectDir, projectDir.name)
        val title = normalizeTitle(requestedTitle)
        if (metadata.title == title) return projectDir
        return projectDir.takeIf { saveMetadata(it, metadata.copy(title = title)) }
    }

    /** Most recent readable manual save or autosave, falling back through the backup ring. */
    fun loadLatest(projectDir: File): String = ProjectLyricsStorage.loadLatest(projectDir)

    /** Saves a fixed-name manual snapshot and updates the autosave ring. */
    @Synchronized
    fun saveManual(projectDir: File, title: String, content: String, keepCount: Int): Boolean =
        ProjectLyricsStorage.saveManual(
            projectDir,
            ProjectLyricsStorage.MANUAL_FILE_NAME,
            content,
            keepCount,
        )

    fun manualSaveFileName(@Suppress("UNUSED_PARAMETER") title: String): String =
        ProjectLyricsStorage.MANUAL_FILE_NAME

    /** Rotates the backup ring only when the lyrics have changed. */
    @Synchronized
    fun rotateAndSave(projectDir: File, content: String, keepCount: Int): Boolean =
        ProjectLyricsStorage.rotateAndSave(projectDir, content, keepCount)

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
        val normalized = normalizeTitle(title)
        return existingProjects.firstOrNull { it.equals(normalized, ignoreCase = true) } ?: normalized
    }

    fun metadataFile(projectDir: File): File =
        File(projectDir, "project.json")

    /** Regenerable peaks stay outside project data and Android backups. */
    fun waveformCacheDirectory(context: Context, projectDir: File): File =
        File(context.cacheDir, "waveforms/${projectDir.name}").apply { mkdirs() }

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
        waveformDirectory: File = projectDir,
    ): IntArray = loadOrExtractWaveform(
        projectDir = projectDir,
        beatFile = beatFile,
        targetSampleCount = targetSampleCount,
        shouldCancel = shouldCancel,
        waveformDirectory = waveformDirectory,
        extract = { file, sampleCount ->
            WaveformExtractor.extractAmplitudes(file, sampleCount, shouldCancel)
        },
    )

    internal fun loadOrExtractWaveform(
        projectDir: File,
        beatFile: File,
        targetSampleCount: Int,
        shouldCancel: () -> Boolean = { false },
        waveformDirectory: File = projectDir,
        extract: (File, Int) -> IntArray,
    ): IntArray {
        if (targetSampleCount !in 1..WaveformCache.MAX_SAMPLES || !beatFile.isFile) {
            return IntArray(0)
        }
        val belongsToProject = runCatching {
            beatFile.canonicalFile.parentFile == projectDir.canonicalFile
        }.getOrDefault(false)
        if (!belongsToProject) return IntArray(0)

        val beatRevisionAtStart = synchronized(this) { beatMutationRevision }
        throwIfWaveformCancelled(shouldCancel)
        loadCachedWaveform(waveformDirectory, targetSampleCount)?.let { return it }

        // Decoding can take tens of seconds on some devices. Do not hold the
        // ProjectStorage monitor while it runs: lyrics and metadata use that
        // monitor for short atomic writes and must remain responsive.
        val amplitudes = extract(beatFile, targetSampleCount)
        throwIfWaveformCancelled(shouldCancel)
        if (amplitudes.size == targetSampleCount) {
            synchronized(this) {
                if (beatMutationRevision == beatRevisionAtStart) {
                    saveCachedWaveform(waveformDirectory, targetSampleCount, amplitudes)
                }
            }
        }
        return amplitudes
    }

    private fun throwIfWaveformCancelled(shouldCancel: () -> Boolean) {
        if (shouldCancel()) throw CancellationException("Waveform extraction cancelled")
    }

    fun loadMetadata(projectDir: File, fallbackTitle: String): ProjectMetadata {
        val title = if (isProjectId(fallbackTitle)) "Untitled" else fallbackTitle
        val contents = runCatching { metadataFile(projectDir).readText() }.getOrNull()
            ?: return ProjectMetadata(title = title)
        return ProjectMetadata.fromJsonString(contents, title)
    }

    @Synchronized
    fun saveMetadata(
        projectDir: File,
        metadata: ProjectMetadata,
        now: () -> Long = System::currentTimeMillis,
    ): Boolean =
        runCatching {
            val file = metadataFile(projectDir)
            val updated = metadata.copy(
                updatedAt = now(),
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
        waveformDirectory: File = projectDir,
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
        if (waveformDirectory != projectDir) invalidateWaveformCache(waveformDirectory)

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
    fun removeBeatFromProject(projectDir: File, waveformDirectory: File = projectDir) {
        projectDir.listFiles { file -> file.isProjectBeatFile() }?.forEach { it.delete() }
        beatMutationRevision++
        invalidateWaveformCache(projectDir)
        if (waveformDirectory != projectDir) invalidateWaveformCache(waveformDirectory)

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
