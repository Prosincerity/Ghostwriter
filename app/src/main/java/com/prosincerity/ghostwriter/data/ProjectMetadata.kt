package com.prosincerity.ghostwriter.data

import org.json.JSONArray
import org.json.JSONObject

enum class MarkerLoopRole(val jsonValue: String, val displayName: String) {
    NONE("none", "None"), START("start", "Start"), END("end", "End");

    companion object {
        fun fromJson(value: String): MarkerLoopRole =
            entries.firstOrNull { it.jsonValue == value } ?: NONE
    }
}

/** Frame position is authoritative once the beat's sample rate is known; milliseconds are a UI/legacy adapter. */
data class WaveformMarker(
    val label: String,
    val positionMs: Long,
    val loopRole: MarkerLoopRole = MarkerLoopRole.NONE,
    val frameIndex: Long? = null,
    val sampleRate: Int? = null,
) {
    fun frameAt(rate: Int): Long {
        require(rate > 0)
        val frame = frameIndex
        val savedRate = sampleRate
        return if (frame != null && savedRate != null && savedRate > 0)
            if (savedRate == rate) frame else (frame.toDouble() * rate / savedRate).toLong()
        else (positionMs.toDouble() * rate / 1000).toLong()
    }

    fun withSampleRate(rate: Int): WaveformMarker {
        if (rate <= 0) return this
        val frame = frameAt(rate)
        val position = frameToMs(frame, rate)
        // Unchanged pointerInput keys retain their marker reference. Preserve it
        // when normalizing a list after another marker is added, edited or moved.
        if (sampleRate == rate && frameIndex == frame && positionMs == position) return this
        return copy(positionMs = position, frameIndex = frame, sampleRate = rate)
    }

    fun atPositionMs(position: Long, rate: Int): WaveformMarker =
        copy(positionMs = position, frameIndex = null, sampleRate = null).withSampleRate(rate)

    companion object {
        fun frameToMs(frame: Long, rate: Int): Long = Math.round(frame.toDouble() * 1000 / rate)
    }
}

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
            put("bpm", bpm ?: JSONObject.NULL)
            put("key", key ?: JSONObject.NULL)
            put("timeSignature", timeSignature ?: JSONObject.NULL)
            put("notes", notes ?: JSONObject.NULL)
            put("beatFile", beatFile ?: JSONObject.NULL)
            put("beatOriginalName", beatOriginalName ?: JSONObject.NULL)
            put("markers", JSONArray().apply {
                markers.forEach { marker ->
                    put(JSONObject().apply {
                        put("label", marker.label)
                        if (marker.frameIndex != null && marker.sampleRate != null && marker.sampleRate > 0) {
                            put("frameIndex", marker.frameIndex)
                            put("sampleRate", marker.sampleRate)
                        } else put("positionMs", marker.positionMs) // Legacy metadata until a beat is opened.
                        put("loopRole", marker.loopRole.jsonValue)
                    })
                }
            })
            put("createdAt", createdAt)
            put("updatedAt", updatedAt)
        }
    }

    companion object {
        const val CURRENT_VERSION = 4

        private fun JSONObject.optionalString(name: String): String? =
            if (isNull(name)) null else optString(name).ifBlank { null }

        fun fromJsonObject(json: JSONObject, fallbackTitle: String): ProjectMetadata {
            val title = json.optString("title", fallbackTitle).ifBlank { fallbackTitle }
            val bpm = json.optInt("bpm", -1).takeIf { it > 0 }
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
                        val frame = markerJson.optLong("frameIndex", -1L).takeIf { it >= 0 }
                        val rate = markerJson.optInt("sampleRate", 0).takeIf { it > 0 }
                        val hasFrame = frame != null && rate != null
                        val positionMs = if (hasFrame) WaveformMarker.frameToMs(frame, rate)
                            else markerJson.optLong("positionMs", -1L)
                        if (positionMs >= 0L) add(WaveformMarker(
                            label, positionMs, MarkerLoopRole.fromJson(markerJson.optString("loopRole")),
                            if (hasFrame) frame else null, if (hasFrame) rate else null,
                        ))
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
