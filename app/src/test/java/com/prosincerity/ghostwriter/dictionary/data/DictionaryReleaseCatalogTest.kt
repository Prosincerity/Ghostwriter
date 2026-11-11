package com.prosincerity.ghostwriter.dictionary.data

import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import java.io.ByteArrayInputStream
import java.io.IOException

class DictionaryReleaseCatalogTest {
    @get:Rule val temporary = TemporaryFolder()
    private val hash = "a".repeat(64)

    @Test fun discoversSixArchivesAndReadsLanguagePrefixedChecksums() {
        val release = DictionaryReleaseCatalog.fromGithub(metadata("next-release"), checksums("next-release"))

        assertEquals("next-release", release.tag)
        assertEquals(setOf("en", "de", "tr"), release.languages.keys)
        release.languages.values.forEach { pair ->
            DictionarySource.entries.forEach { assertEquals(hash, pair.asset(it).sha256) }
        }
        assertEquals(release.toJson().toString(), DictionaryRelease.parse(release.toJson()).toJson().toString())
    }

    @Test fun acceptsBasenamesBinaryMarkersUppercaseHashesAndAbsentGithubDigests() {
        val metadata = metadata("v2")
        val assets = metadata.getJSONArray("assets")
        for (index in 0 until assets.length()) assets.getJSONObject(index).remove("digest")
        val sums = checksums("v2").lines().joinToString("\r\n") {
            "${hash.uppercase()} *${it.substringAfterLast('/')}"
        }

        assertEquals(hash, DictionaryReleaseCatalog.fromGithub(metadata, sums).languages.getValue("de").espeak.sha256)
    }

    @Test fun rejectsDraftsPrereleasesAndUnsafeTags() {
        for (field in listOf("draft", "prerelease")) {
            assertThrows(IllegalArgumentException::class.java) {
                DictionaryReleaseCatalog.fromGithub(metadata("v2").put(field, true), checksums("v2"))
            }
        }
        for (tag in listOf("../v2", ".v2", "v2/path", "", "two words")) {
            assertThrows(IllegalArgumentException::class.java) {
                DictionaryReleaseCatalog.fromGithub(metadata(tag), checksums(tag))
            }
        }
    }

    @Test fun rejectsMissingAmbiguousOrUnsafeAssetsAndMissingChecksums() {
        val valid = metadata("v2")
        for (name in listOf("SHA256SUMS", "en_v2.db.gz", "tr_espeak_v2.db.gz")) {
            val json = JSONObject(valid.toString())
            val assets = json.getJSONArray("assets")
            val index = (0 until assets.length()).first { assets.getJSONObject(it).getString("name") == name }
            assets.remove(index)
            assertThrows(IllegalStateException::class.java) {
                DictionaryReleaseCatalog.fromGithub(json, checksums("v2"))
            }
        }
        val duplicate = metadata("v2")
        duplicate.getJSONArray("assets").put(duplicate.getJSONArray("assets").getJSONObject(0))
        assertThrows(IllegalStateException::class.java) {
            DictionaryReleaseCatalog.fromGithub(duplicate, checksums("v2"))
        }
        val unsafe = metadata("v2")
        unsafe.getJSONArray("assets").getJSONObject(0).put("browser_download_url", "https://example.com/dictionary.db.gz")
        assertThrows(IllegalArgumentException::class.java) {
            DictionaryReleaseCatalog.fromGithub(unsafe, checksums("v2"))
        }
        assertThrows(IllegalStateException::class.java) {
            DictionaryReleaseCatalog.fromGithub(metadata("v2"), "")
        }
    }

    @Test fun rejectsMalformedDuplicateUnsafeAndConflictingHashes() {
        for (sums in listOf(
            "not a checksum", checksums("v2") + "\n" + checksums("v2").lines().first(),
            checksums("v2").replace("en/", "../"), checksums("v2").replace(hash, "b".repeat(64)),
        )) {
            assertThrows(Exception::class.java) { DictionaryReleaseCatalog.fromGithub(metadata("v2"), sums) }
        }
    }

    @Test fun refreshPersistsReleaseAndOfflineConstructionDoesNotUseNetwork() = runBlocking {
        val cache = temporary.root.resolve("dictionaries/release.json")
        val requests = mutableListOf<String>()
        val catalog = DictionaryReleaseCatalog(cache, release("v1"), DictionaryArchiveSource { url ->
            requests += url
            ByteArrayInputStream((if (url == DictionaryReleaseCatalog.LATEST_URL) metadata("v2").toString()
                else checksums("v2")).toByteArray())
        })
        assertEquals("v1", catalog.release.tag)

        assertEquals("v2", catalog.refresh().tag)
        assertEquals(listOf(DictionaryReleaseCatalog.LATEST_URL,
            "https://github.com/Prosincerity/Ghostwriter-Dict/releases/download/v2/SHA256SUMS"), requests)
        val offline = DictionaryReleaseCatalog(cache, release("v1"), DictionaryArchiveSource { error("Offline") })
        assertEquals("v2", offline.release.tag)
    }

    @Test fun failedRefreshKeepsCachedReleaseAndCanBeRetried() = runBlocking {
        val cache = temporary.newFile()
        cache.writeText(release("v1").toJson().toString())
        val original = cache.readText()
        var online = false
        val catalog = DictionaryReleaseCatalog(cache, release("bundled"), DictionaryArchiveSource { url ->
            if (!online) throw IOException("offline")
            ByteArrayInputStream((if (url == DictionaryReleaseCatalog.LATEST_URL) metadata("v2").toString()
                else checksums("v2")).toByteArray())
        })

        assertThrows(IOException::class.java) { runBlocking { catalog.refresh() } }
        assertEquals("v1", catalog.release.tag)
        assertEquals(original, cache.readText())
        online = true
        assertEquals("v2", catalog.refresh().tag)
    }

    @Test fun corruptCacheUsesBundledMetadataAndOversizedRemoteMetadataIsRejected() {
        val cache = temporary.newFile().apply { writeText("broken") }
        val catalog = DictionaryReleaseCatalog(cache, release("v1"), DictionaryArchiveSource {
            ByteArrayInputStream(ByteArray(2 * 1024 * 1024 + 1))
        })

        assertEquals("v1", catalog.release.tag)
        assertThrows(IllegalStateException::class.java) { runBlocking { catalog.refresh() } }
        assertEquals("broken", cache.readText())
    }

    private fun release(tag: String) = DictionaryReleaseCatalog.fromGithub(metadata(tag), checksums(tag))

    private fun metadata(tag: String): JSONObject {
        val assets = JSONArray()
        for (language in listOf("en", "de", "tr")) {
            for (source in listOf("", "espeak_")) {
                val name = "${language}_$source$tag.db.gz"
                assets.put(JSONObject().put("name", name).put("size", 123)
                    .put("browser_download_url", "https://github.com/Prosincerity/Ghostwriter-Dict/releases/download/$tag/$name")
                    .put("digest", "sha256:$hash"))
            }
        }
        assets.put(JSONObject().put("name", "SHA256SUMS")
            .put("browser_download_url", "https://github.com/Prosincerity/Ghostwriter-Dict/releases/download/$tag/SHA256SUMS"))
        return JSONObject().put("tag_name", tag).put("draft", false).put("prerelease", false).put("assets", assets)
    }

    private fun checksums(tag: String) = listOf("en", "de", "tr").flatMap { language ->
        listOf("", "espeak_").map { "$hash  $language/${language}_$it$tag.db.gz" }
    }.joinToString("\n")
}
