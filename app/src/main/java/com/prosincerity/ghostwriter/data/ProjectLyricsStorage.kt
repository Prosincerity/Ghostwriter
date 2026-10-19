package com.prosincerity.ghostwriter.data

import java.io.File
import java.io.IOException

/** Manual lyric snapshots and the rolling autosave backup ring. */
internal object ProjectLyricsStorage {
    const val MANUAL_FILE_NAME = "lyrics.txt"
    internal val autosaveFileName = Regex("autosave(?:[1-9]|10)\\.txt", RegexOption.IGNORE_CASE)
    /** Most recent readable manual save or autosave, falling back through the backup ring. */
    fun loadLatest(projectDir: File): String {
        // Legacy title snapshots remain readable after UUID migration and rename.
        val projectFiles = projectDir.listFiles().orEmpty()
        val manualFiles = projectFiles.filter { file ->
            file.isFile && file.extension == "txt" &&
                !autosaveFileName.matches(file.name)
        }
        val manualFile = manualFiles.filter { it.readTextIfFile() != null }
            .maxWithOrNull(compareBy<File> { it.lastModified() }.thenBy { it.name == MANUAL_FILE_NAME })
            ?: File(projectDir, MANUAL_FILE_NAME)
        // Older versions could keep ten slots. Best-effort rotations can also
        // leave slot ages out of order, so compare every readable snapshot.
        // Scanning in slot order keeps the earlier slot when timestamps tie.
        val newestReadableAutosave = (1..10).asSequence()
            .flatMap { i ->
                // Match the exclusion above so mixed-case legacy title files
                // remain readable after their project directory becomes a UUID.
                projectFiles.asSequence().filter { it.name.equals("autosave$i.txt", ignoreCase = true) }
                    .sortedBy { it.name != "autosave$i.txt" }
            }
            .mapNotNull { file ->
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

    fun saveManual(
        projectDir: File,
        fileName: String,
        content: String,
        keepCount: Int,
        now: () -> Long = System::currentTimeMillis,
    ): Boolean =
        runCatching {
            writeSnapshot(File(projectDir, fileName), content, now)
            rotateAndSave(projectDir, content, keepCount, now)
        }.getOrDefault(false)

    /** An unchanged autosave does not consume another backup slot. */
    fun rotateAndSave(
        projectDir: File,
        content: String,
        keepCount: Int,
        now: () -> Long = System::currentTimeMillis,
    ): Boolean =
        runCatching {
            require(keepCount in Settings.COUNT_OPTIONS)
            val newest = File(projectDir, "autosave1.txt")
            val previousNewest = newest.readTextIfFile()
            if (previousNewest != content) {
                // Recovery can select any slot. Capture the newest readable
                // history before writing over files that may contain it. Duplicate
                // copies of one snapshot must not displace another prior draft.
                val history = autosaveHistory(projectDir).distinct().take(keepCount - 1)
                val pendingHistory = history.toMutableSet()
                // A failed current save must not overwrite any existing history.
                writeSnapshot(newest, content, now)
                for (index in history.indices) {
                    val dst = File(projectDir, "autosave${index + 2}.txt")
                    val (previousText, sourceTime) = history[index]
                    val displaced = dst.readTextIfFile()?.let { it to dst.lastModified() }
                    // Do not overwrite a newer draft whose earlier copy failed.
                    if (displaced != null && history.indexOf(displaced) in 0 until index &&
                        displaced in pendingHistory) continue
                    // An unreadable/blocked older slot must not stop the current
                    // lyrics from reaching disk. Stage copies to preserve the
                    // destination if a backup copy fails partway through.
                    runCatching {
                        StagedFileWriter.replace(dst, "save-", "Couldn't rotate ${dst.name}") { staged ->
                            staged.writeText(previousText)
                            // Recovery compares snapshots by age when autosave1 is lost.
                            staged.setLastModified(sourceTime)
                        }
                    }.onSuccess { pendingHistory.remove(history[index]) }
                }
            } else if (loadLatest(projectDir) != content) {
                // Recovery may prefer a newer manual save or out-of-order backup.
                // Reassert this edit without rotating away that recovery history.
                writeSnapshot(newest, content, now)
            }
            // Prune only after the latest snapshot is safe. Settings changes
            // still apply when the lyrics haven't changed.
            val retainedHistory = autosaveHistory(projectDir).distinct().take(keepCount).toSet()
            val retainedSlots = (1..keepCount).mapNotNull { slot ->
                val file = File(projectDir, "autosave$slot.txt")
                file.readTextIfFile()?.let { it to file.lastModified() }
            }.toSet()
            for (i in (keepCount + 1)..10) {
                val oldBackup = File(projectDir, "autosave$i.txt")
                if (oldBackup.isFile) {
                    // A failed rotation must not prune the only surviving copy.
                    val needed = oldBackup.readTextIfFile()?.let {
                        val snapshot = it to oldBackup.lastModified()
                        snapshot in retainedHistory && snapshot !in retainedSlots
                    } == true
                    if (!needed) oldBackup.delete()
                }
            }
        }.isSuccess

    private fun autosaveHistory(projectDir: File): List<Pair<String, Long>> =
        (1..10).asSequence()
            .map { File(projectDir, "autosave$it.txt") }
            .sortedByDescending { it.lastModified() }
            .mapNotNull { file -> file.readTextIfFile()?.let { it to file.lastModified() } }
            .toList()

    private fun writeSnapshot(target: File, content: String, now: () -> Long) {
        // Recovery ranks snapshots by age. Keep new edits ahead of every
        // existing snapshot even after a clock rollback or a same-tick save.
        val previousTime = target.parentFile.listFiles().orEmpty().asSequence()
            .filter { it.isFile && (it.extension == "txt" || autosaveFileName.matches(it.name)) }
            .maxOfOrNull { it.lastModified() } ?: 0L
        val savedAt = maxOf(now(), Math.addExact(previousTime, 1L))
        StagedFileWriter.replace(target, "save-", "Couldn't replace ${target.name}") { staged ->
            staged.writeText(content)
            if (!staged.setLastModified(savedAt) || staged.lastModified() <= previousTime) {
                throw IOException("Couldn't preserve the lyric save's order")
            }
        }
    }
}
