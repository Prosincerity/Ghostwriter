package com.prosincerity.ghostwriter.data

import org.json.JSONException
import org.json.JSONObject
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test

class DictionaryReleaseTest {
    @Test
    fun parsesBothSourcesForEachSupportedLanguage() {
        val release = DictionaryRelease.parse(manifest())

        assertEquals("test-v1.2_3", release.tag)
        assertEquals(setOf("en", "de", "tr"), release.languages.keys)
        release.languages.forEach { (language, assets) ->
            DictionarySource.entries.forEach { source ->
                val file = "$language-${source.name.lowercase()}.db.gz"
                assertEquals(DictionaryAsset(file, 123L, "$releaseUrl/$file"), assets.asset(source))
            }
        }
    }

    @Test
    fun rejectsEmptyPathLikeAndHiddenReleaseTags() {
        listOf("", "../outside", "release/name", "tag with spaces", ".", "..", ".release-v1").forEach { tag ->
            assertThrows("tag: $tag", IllegalArgumentException::class.java) {
                DictionaryRelease.parse(manifest().put("tag", tag))
            }
        }
    }

    @Test
    fun rejectsNonPositiveArchiveSizesForEveryLanguageAndSource() {
        listOf("en", "de", "tr").forEach { language ->
            listOf("wiktionary", "espeak").forEach { source ->
                listOf(0L, -1L, Long.MIN_VALUE).forEach { size ->
                    val json = manifest()
                    json.getJSONObject("languages").getJSONObject(language)
                        .getJSONObject(source).put("sizeBytes", size)

                    assertThrows("$language $source size: $size", IllegalArgumentException::class.java) {
                        DictionaryRelease.parse(json)
                    }
                }
            }
        }
    }

    @Test
    fun rejectsInvalidArchiveNamesEvenWhenUrlMatches() {
        listOf("../dictionary.db.gz", "dictionary.gz", "dictionary.db", "dictionary name.db.gz")
            .forEach { file ->
                val json = manifest()
                asset(json).put("file", file).put("url", "$releaseUrl/$file")
                assertThrows("file: $file", IllegalArgumentException::class.java) {
                    DictionaryRelease.parse(json)
                }
            }
    }

    @Test
    fun rejectsOtherHostsInsecureUrlsAndLookalikeRepositories() {
        listOf(
            "http://github.com/Prosincerity/Ghostwriter-Dict/releases/download/test/en-wiktionary.db.gz",
            "https://example.com/en-wiktionary.db.gz",
            "https://github.com/Prosincerity/Other/releases/download/test/en-wiktionary.db.gz",
            "https://github.com/Prosincerity/Ghostwriter-Dict/releases/download-evil/en-wiktionary.db.gz",
        ).forEach { url ->
            val json = manifest()
            asset(json).put("url", url)
            assertThrows("url: $url", IllegalArgumentException::class.java) {
                DictionaryRelease.parse(json)
            }
        }
    }

    @Test
    fun rejectsUrlForADifferentArchive() {
        val json = manifest()
        asset(json).put("url", "$releaseUrl/another.db.gz")

        assertThrows(IllegalArgumentException::class.java) { DictionaryRelease.parse(json) }
    }

    @Test
    fun requiresAllThreeLanguagePairs() {
        val json = manifest()
        json.getJSONObject("languages").remove("tr")

        assertThrows(JSONException::class.java) { DictionaryRelease.parse(json) }
    }

    @Test
    fun requiresBothDictionarySources() {
        val json = manifest()
        json.getJSONObject("languages").getJSONObject("de").remove("espeak")

        assertThrows(JSONException::class.java) { DictionaryRelease.parse(json) }
    }

    @Test
    fun requiresAnArchiveSize() {
        val json = manifest()
        asset(json).remove("sizeBytes")

        assertThrows(JSONException::class.java) { DictionaryRelease.parse(json) }
    }

    private fun asset(json: JSONObject): JSONObject =
        json.getJSONObject("languages").getJSONObject("en").getJSONObject("wiktionary")

    private fun manifest(): JSONObject {
        val languages = JSONObject()
        listOf("en", "de", "tr").forEach { language ->
            val sources = JSONObject()
            listOf("wiktionary", "espeak").forEach { source ->
                val file = "$language-$source.db.gz"
                sources.put(source, JSONObject().put("file", file).put("sizeBytes", 123L)
                    .put("url", "$releaseUrl/$file"))
            }
            languages.put(language, sources)
        }
        return JSONObject().put("tag", "test-v1.2_3").put("languages", languages)
    }

    private val releaseUrl = "https://github.com/Prosincerity/Ghostwriter-Dict/releases/download/test-v1.2_3"
}
