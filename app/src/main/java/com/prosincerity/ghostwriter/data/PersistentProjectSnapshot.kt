package com.prosincerity.ghostwriter.data

import java.security.MessageDigest
import org.json.JSONObject

/** One immutable, checksummed lyric save with enough metadata for reinstall recovery. */
internal data class PersistentProjectSnapshot(
    val projectId: String,
    val savedAt: Long,
    val metadata: String,
    val lyrics: String,
) {
    fun encode(): String = JSONObject().apply {
        put("format", 1)
        put("projectId", projectId)
        put("savedAt", savedAt)
        put("metadata", metadata)
        put("lyrics", lyrics)
        put("checksum", checksum())
    }.toString()

    val contentFingerprint: String get() = digest(listOf(projectId, metadata, lyrics))

    private fun checksum(): String = digest(listOf(projectId, savedAt.toString(), metadata, lyrics))

    private fun digest(values: List<String>): String {
        // Length prefixes avoid ambiguity when lyrics or notes contain delimiters.
        val digest = MessageDigest.getInstance("SHA-256")
        for (value in values) {
            val bytes = value.toByteArray(Charsets.UTF_8)
            digest.update(java.nio.ByteBuffer.allocate(4).putInt(bytes.size).array())
            digest.update(bytes)
        }
        return digest.digest().joinToString("") { "%02x".format(it) }
    }

    companion object {
        fun decode(encoded: String, expectedProjectId: String): PersistentProjectSnapshot? = runCatching {
            val json = JSONObject(encoded)
            require(json.getInt("format") == 1)
            val snapshot = PersistentProjectSnapshot(
                json.getString("projectId"), json.getLong("savedAt"),
                json.getString("metadata"), json.getString("lyrics"),
            )
            require(ProjectStorage.isProjectId(snapshot.projectId) && snapshot.projectId == expectedProjectId)
            require(snapshot.savedAt > 0L)
            // Validate metadata without using the tolerant fallback reader.
            require(JSONObject(snapshot.metadata).getString("title").isNotBlank())
            require(json.getString("checksum") == snapshot.checksum())
            snapshot
        }.getOrNull()
    }
}
