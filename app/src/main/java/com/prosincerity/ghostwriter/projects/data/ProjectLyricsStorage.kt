package com.prosincerity.ghostwriter.projects.data

import com.prosincerity.ghostwriter.core.io.StagedFileWriter
import com.prosincerity.ghostwriter.settings.Settings

import java.io.File
import java.io.IOException

/** Manual lyric snapshots and the rolling autosave backup ring. */
internal object ProjectLyricsStorage {
    const val MANUAL_FILE_NAME = "lyrics.txt"
    internal val autosaveFileName = Regex("autosave(?:[1-9]|10)\\.txt", RegexOption.IGNORE_CASE)
    /** Most recent readable manual save or autosave, falling back through the backup ring. */
    fun loadLatest(projectDir: File): String {
        // Legacy title snapshots remain readable after UUID migration and rename.
        val projectFiles = projectDir.listFiles()
            ?: throw IOException("Couldn't read the project's lyric files")
        var readFailure: IOException? = null
        fun File.readSnapshot(): String? = try {
            readText()
        } catch (failure: IOException) {
            readFailure = failure
            null // Keep looking for a readable older snapshot.
        }
        val manualFiles = projectFiles.filter { file ->
            file.extension == "txt" &&
                !autosaveFileName.matches(file.name)
        }
        val manual = manualFiles.mapNotNull { file -> file.readSnapshot()?.let { file to it } }
            .maxWithOrNull(compareBy<Pair<File, String>> { it.first.lastModified() }
                .thenBy { it.first.name == MANUAL_FILE_NAME })
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
                file.readSnapshot()?.let { file to it }
            }
            .maxByOrNull { it.first.lastModified() }
        return when {
            manual != null &&
                (newestReadableAutosave == null ||
                    manual.first.lastModified() >= newestReadableAutosave.first.lastModified()) -> manual.second
            newestReadableAutosave != null -> newestReadableAutosave.second
            else -> readFailure?.let { throw it } ?: ""
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
                val pendingHistory = history.toMutableList()
                // A failed current save must not overwrite any existing history.
                writeSnapshot(newest, content, now)
                for (index in 0 until keepCount - 1) {
                    val dst = File(projectDir, "autosave${index + 2}.txt")
                    // A failed copy may leave a draft only in memory after its
                    // source was replaced. Retry it before copying older history.
                    val savedHistory = autosaveHistory(projectDir)
                    val snapshot = pendingHistory.firstOrNull { it !in savedHistory }
                        ?: pendingHistory.firstOrNull() ?: break
                    val (previousText, sourceTime) = snapshot
                    val displaced = dst.readTextIfFile()?.let { it to dst.lastModified() }
                    // Keep the only copy of a newer pending draft. This slot is
                    // now safe from later writes, so that draft is no longer pending.
                    if (displaced != null && history.indexOf(displaced) in 0 until history.indexOf(snapshot) &&
                        displaced in pendingHistory && savedHistory.count { it == displaced } == 1) {
                        pendingHistory.remove(displaced)
                        continue
                    }
                    // An unreadable/blocked older slot must not stop the current
                    // lyrics from reaching disk. Stage copies to preserve the
                    // destination if a backup copy fails partway through.
                    runCatching {
                        StagedFileWriter.replace(dst, "save-", "Couldn't rotate ${dst.name}") { staged ->
                            staged.writeText(previousText)
                            // Recovery compares snapshots by age when autosave1 is lost.
                            staged.setLastModified(sourceTime)
                        }
                    }.onSuccess { pendingHistory.remove(snapshot) }
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
        val projectDir = requireNotNull(target.parentFile) { "Lyric snapshots must have a project directory" }
        val previousTime = projectDir.listFiles().orEmpty().asSequence()
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
