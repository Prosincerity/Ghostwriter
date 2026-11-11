package com.prosincerity.ghostwriter.dictionary.data

import com.prosincerity.ghostwriter.core.io.StagedFileWriter
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.File
import kotlin.coroutines.coroutineContext

/** Checks only on the downloads screen; editing and search use local metadata. */
internal class DictionaryReleaseCatalog(
    private val cache: File,
    bundledRelease: DictionaryRelease,
    private val source: DictionaryArchiveSource,
) {
    @Volatile
    var release: DictionaryRelease = try {
        DictionaryRelease.parse(JSONObject(cache.readText()))
    } catch (_: Exception) {
        bundledRelease
    }
        private set

    suspend fun refresh(): DictionaryRelease = withContext(Dispatchers.IO) {
        val metadata = JSONObject(readText(LATEST_URL, 2 * 1024 * 1024))
        val checksums = readText(checksumUrl(metadata), 256 * 1024)
        val latest = fromGithub(metadata, checksums)
        coroutineContext.ensureActive()
        check(cache.parentFile!!.mkdirs() || cache.parentFile!!.isDirectory) { "Cannot save dictionary release" }
        StagedFileWriter.writeText(cache, latest.toJson().toString())
        release = latest
        latest
    }

    private suspend fun readText(url: String, limit: Int): String = source.open(url).use { input ->
        val output = java.io.ByteArrayOutputStream()
        val buffer = ByteArray(8192)
        while (true) {
            coroutineContext.ensureActive()
            val count = input.read(buffer)
            if (count < 0) break
            check(output.size() + count <= limit) { "Dictionary release metadata is too large" }
            output.write(buffer, 0, count)
        }
        output.toString(Charsets.UTF_8.name())
    }

    companion object {
        const val LATEST_URL = "https://api.github.com/repos/Prosincerity/Ghostwriter-Dict/releases/latest"
        private const val DOWNLOAD_ROOT = "https://github.com/Prosincerity/Ghostwriter-Dict/releases/download/"

        private fun tag(metadata: JSONObject): String {
            require(!metadata.getBoolean("draft") && !metadata.getBoolean("prerelease")) {
                "Dictionary release must be a published stable release"
            }
            return metadata.getString("tag_name").also {
                require(it.matches(Regex("[A-Za-z0-9_-][A-Za-z0-9._-]*"))) { "Invalid dictionary release tag" }
            }
        }

        private fun assets(metadata: JSONObject): List<JSONObject> {
            val array = metadata.getJSONArray("assets")
            return (0 until array.length()).map(array::getJSONObject)
        }

        private fun assetUrl(asset: JSONObject, tag: String): String {
            val name = asset.getString("name")
            require(name.matches(Regex("[A-Za-z0-9._-]+"))) { "Invalid release asset name" }
            return asset.getString("browser_download_url").also {
                require(it == "$DOWNLOAD_ROOT$tag/$name") { "Invalid release asset URL" }
            }
        }

        fun checksumUrl(metadata: JSONObject): String {
            val tag = tag(metadata)
            val asset = assets(metadata).singleOrNull { it.getString("name") == "SHA256SUMS" }
                ?: error("Dictionary release is missing SHA256SUMS")
            return assetUrl(asset, tag)
        }

        fun fromGithub(metadata: JSONObject, checksums: String): DictionaryRelease {
            val tag = tag(metadata)
            checksumUrl(metadata)
            val hashes = parseChecksums(checksums)
            val assets = assets(metadata)
            val languages = JSONObject()
            for (language in listOf("en", "de", "tr")) {
                val pair = JSONObject()
                for (source in DictionarySource.entries) {
                    val pattern = if (source == DictionarySource.ESPEAK) {
                        Regex("${language}_espeak_.+\\.db\\.gz")
                    } else Regex("${language}_(?!espeak_).+\\.db\\.gz")
                    val asset = assets.singleOrNull { it.getString("name").matches(pattern) }
                        ?: error("Dictionary release is missing a unique $language ${source.name.lowercase()} archive")
                    val name = asset.getString("name")
                    val hash = hashes[name] ?: error("Missing SHA-256 for $name")
                    if (!asset.isNull("digest")) {
                        require(asset.getString("digest").lowercase() == "sha256:$hash") {
                            "Conflicting SHA-256 for $name"
                        }
                    }
                    pair.put(source.name.lowercase(), JSONObject()
                        .put("file", name).put("url", assetUrl(asset, tag))
                        .put("sizeBytes", asset.getLong("size")).put("sha256", hash))
                }
                languages.put(language, pair)
            }
            return DictionaryRelease.parse(JSONObject().put("tag", tag).put("languages", languages))
        }

        private fun parseChecksums(text: String): Map<String, String> = buildMap {
            for (line in text.lineSequence().filter { it.isNotBlank() }) {
                val match = Regex("([a-fA-F0-9]{64}) [ *](.+)").matchEntire(line.trimEnd('\r'))
                    ?: error("Invalid SHA256SUMS entry")
                val path = match.groupValues[2]
                require(path.split('/').all { it.matches(Regex("[A-Za-z0-9_-][A-Za-z0-9._-]*")) }) {
                    "Invalid SHA256SUMS path"
                }
                val name = path.substringAfterLast('/')
                require(put(name, match.groupValues[1].lowercase()) == null) { "Duplicate SHA-256 for $name" }
            }
        }
    }
}
