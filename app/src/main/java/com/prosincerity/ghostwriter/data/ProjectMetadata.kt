package com.prosincerity.ghostwriter.data

import org.json.JSONArray
import org.json.JSONObject

/** A named point in a beat, stored as a non-negative playback position in milliseconds. */
data class WaveformMarker(
    val label: String,
    val positionMs: Long,
)

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
    val markers: List<WaveformMarker> = emptyList(),
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val version: Int = CURRENT_VERSION,
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
            put("markers", JSONArray().apply {
                markers.forEach { marker ->
                    put(JSONObject().apply {
                        put("label", marker.label)
                        put("positionMs", marker.positionMs)
                    })
                }
            })
            put("createdAt", createdAt)
            put("updatedAt", updatedAt)
        }
    }

    companion object {
        const val CURRENT_VERSION = 2

        private fun JSONObject.optionalString(name: String): String? =
            if (isNull(name)) null else optString(name).ifBlank { null }

        fun fromJsonObject(json: JSONObject, fallbackTitle: String): ProjectMetadata {
            val title = json.optString("title", fallbackTitle).ifBlank { fallbackTitle }
            val bpm = if (json.has("bpm") && !json.isNull("bpm")) {
                val parsed = json.optInt("bpm", -1)
                if (parsed > 0) parsed else null
            } else null
            val key = json.optionalString("key")
            val timeSignature = json.optionalString("timeSignature")
            val notes = json.optionalString("notes")
            val beatFile = json.optionalString("beatFile")
            val beatOriginalName = json.optionalString("beatOriginalName")
            val markers = json.optJSONArray("markers")?.let { markerArray ->
                buildList {
                    for (index in 0 until markerArray.length()) {
                        val markerJson = markerArray.optJSONObject(index) ?: continue
                        val label = markerJson.optionalString("label") ?: continue
                        val positionMs = markerJson.optLong("positionMs", -1L)
                        if (positionMs >= 0L) add(WaveformMarker(label, positionMs))
                    }
                }
            } ?: emptyList()
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
                markers = markers,
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
