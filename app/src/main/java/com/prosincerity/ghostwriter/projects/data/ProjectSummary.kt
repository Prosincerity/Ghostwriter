package com.prosincerity.ghostwriter.projects.data

import java.io.File
import org.json.JSONObject

/** Read-only information for project rows; waveform caches and beat files are not edits. */
internal data class ProjectSummary(
    val title: String,
    val lastEditedAt: Long,
    val bpm: Int? = null,
    val key: String? = null,
) {
    companion object {
        fun fromDirectory(directory: File): ProjectSummary {
            val metadata = runCatching {
                JSONObject(File(directory, "project.json").readText())
            }.getOrNull()
            val snapshotTime = directory.listFiles { file ->
                file.isFile && !file.name.startsWith(".") &&
                    (file.extension == "txt" || ProjectLyricsStorage.autosaveFileName.matches(file.name))
            }?.maxOfOrNull { it.lastModified() } ?: 0L
            val editTime = maxOf(snapshotTime, metadata?.optLong("updatedAt", 0L) ?: 0L)
            return ProjectSummary(
                title = (metadata?.opt("title") as? String)?.takeIf { it.isNotBlank() } ?: if (ProjectStorage.isProjectId(directory.name)) "Untitled" else directory.name,
                lastEditedAt = editTime.takeIf { it > 0L } ?: directory.lastModified(),
                bpm = metadata?.optInt("bpm", 0)?.takeIf { it > 0 },
                key = metadata?.takeUnless { it.isNull("key") }?.optString("key")
                    ?.trim()?.takeIf { it.isNotEmpty() },
            )
        }
    }
}
