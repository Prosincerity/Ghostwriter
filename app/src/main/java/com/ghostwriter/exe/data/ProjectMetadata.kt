package com.ghostwriter.exe.data

import org.json.JSONObject
import java.io.File

/**
 * Metadata for a lyric project, stored in project.json.
 * Uses Android's built-in org.json library (zero external dependencies).
 */
data class ProjectMetadata(
    val title: String,
    val bpm: Int? = null,
    val key: String? = null,
    val timeSignature: String? = null,
    val notes: String? = null,
    val beatFile: String? = null,
    val beatOriginalName: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val version: Int = 1,
) {
    fun toJsonObject(): JSONObject {
        return JSONObject().apply {
            put("version", version)
            put("title", title)
            if (bpm != null) put("bpm", bpm) else put("bpm", JSONObject.NULL)
            if (key != null) put("key", key) else put("key", JSONObject.NULL)
            if (timeSignature != null) put("timeSignature", timeSignature) else put("timeSignature", JSONObject.NULL)
            if (notes != null) put("notes", notes) else put("notes", JSONObject.NULL)
            if (beatFile != null) put("beatFile", beatFile) else put("beatFile", JSONObject.NULL)
            if (beatOriginalName != null) put("beatOriginalName", beatOriginalName) else put("beatOriginalName", JSONObject.NULL)
            put("createdAt", createdAt)
            put("updatedAt", updatedAt)
        }
    }

    companion object {
        fun fromJsonObject(json: JSONObject, fallbackTitle: String): ProjectMetadata {
            val title = json.optString("title", fallbackTitle).ifBlank { fallbackTitle }
            val bpm = if (json.has("bpm") && !json.isNull("bpm")) {
                val parsed = json.optInt("bpm", -1)
                if (parsed > 0) parsed else null
            } else null
            val key = if (json.has("key") && !json.isNull("key")) json.optString("key").ifBlank { null } else null
            val timeSignature = if (json.has("timeSignature") && !json.isNull("timeSignature")) json.optString("timeSignature").ifBlank { null } else null
            val notes = if (json.has("notes") && !json.isNull("notes")) json.optString("notes").ifBlank { null } else null
            val beatFile = if (json.has("beatFile") && !json.isNull("beatFile")) json.optString("beatFile").ifBlank { null } else null
            val beatOriginalName = if (json.has("beatOriginalName") && !json.isNull("beatOriginalName")) json.optString("beatOriginalName").ifBlank { null } else null
            val createdAt = json.optLong("createdAt", System.currentTimeMillis())
            val updatedAt = json.optLong("updatedAt", System.currentTimeMillis())
            val version = json.optInt("version", 1)

            return ProjectMetadata(
                title = title,
                bpm = bpm,
                key = key,
                timeSignature = timeSignature,
                notes = notes,
                beatFile = beatFile,
                beatOriginalName = beatOriginalName,
                createdAt = createdAt,
                updatedAt = updatedAt,
                version = version,
            )
        }

        fun fromJsonString(jsonStr: String, fallbackTitle: String): ProjectMetadata {
            return runCatching {
                fromJsonObject(JSONObject(jsonStr), fallbackTitle)
            }.getOrElse {
                ProjectMetadata(title = fallbackTitle)
            }
        }
    }
}
