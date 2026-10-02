package com.prosincerity.ghostwriter.data

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
                file.isFile && file.extension == "txt" && !file.name.startsWith(".")
            }?.maxOfOrNull { it.lastModified() } ?: 0L
            val editTime = maxOf(snapshotTime, metadata?.optLong("updatedAt", 0L) ?: 0L)
            return ProjectSummary(
                title = directory.name,
                lastEditedAt = editTime.takeIf { it > 0L } ?: directory.lastModified(),
                bpm = metadata?.optInt("bpm", 0)?.takeIf { it > 0 },
                key = metadata?.takeUnless { it.isNull("key") }?.optString("key")
                    ?.trim()?.takeIf { it.isNotEmpty() },
            )
        }
    }
}
