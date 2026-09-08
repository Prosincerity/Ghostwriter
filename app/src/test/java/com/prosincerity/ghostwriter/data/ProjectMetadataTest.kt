package com.prosincerity.ghostwriter.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ProjectMetadataTest {

    @get:Rule
    val tempFolder = TemporaryFolder()

    @Test
    fun loadMetadata_returnsDefaultWhenFileDoesNotExist() {
        val projectDir = tempFolder.newFolder("TestSong")
        val meta = ProjectStorage.loadMetadata(projectDir, "TestSong")

        assertEquals("TestSong", meta.title)
        assertNull(meta.bpm)
        assertNull(meta.key)
        assertNull(meta.timeSignature)
        assertNull(meta.notes)
        assertNull(meta.beatFile)
        assertNull(meta.beatOriginalName)
        assertTrue(meta.markers.isEmpty())
    }

    @Test
    fun saveAndLoadMetadata_persistsAllFields() {
        val projectDir = tempFolder.newFolder("HitTrack")
        val initial = ProjectMetadata(
            title = "HitTrack",
            bpm = 95,
            key = "F# Minor",
            timeSignature = "4/4",
            notes = "First single for the EP",
            beatFile = "beat.mp3",
            beatOriginalName = "hard_trap_95bpm.mp3",
            markers = listOf(
                WaveformMarker("Verse 1", 12_000L),
                WaveformMarker("Hook", 32_000L),
            ),
            createdAt = 1000L,
            updatedAt = 2000L,
            version = 1,
        )

        ProjectStorage.saveMetadata(projectDir, initial)

        val loaded = ProjectStorage.loadMetadata(projectDir, "HitTrack")
        assertEquals("HitTrack", loaded.title)
        assertEquals(95, loaded.bpm)
        assertEquals("F# Minor", loaded.key)
        assertEquals("4/4", loaded.timeSignature)
        assertEquals("First single for the EP", loaded.notes)
        assertEquals("beat.mp3", loaded.beatFile)
        assertEquals("hard_trap_95bpm.mp3", loaded.beatOriginalName)
        assertEquals(
            listOf(WaveformMarker("Verse 1", 12_000L), WaveformMarker("Hook", 32_000L)),
            loaded.markers,
        )
        assertEquals(1000L, loaded.createdAt)
        // updatedAt should be refreshed on save
        assertTrue("updatedAt should be updated on save", loaded.updatedAt >= 2000L)
        assertEquals(ProjectMetadata.CURRENT_VERSION, loaded.version)
    }

    @Test
    fun saveAndLoadMetadata_handlesOptionalFieldsAsNull() {
        val projectDir = tempFolder.newFolder("Acapella")
        val initial = ProjectMetadata(
            title = "Acapella",
            bpm = null,
            key = null,
            timeSignature = null,
            notes = null,
        )

        ProjectStorage.saveMetadata(projectDir, initial)

        val loaded = ProjectStorage.loadMetadata(projectDir, "Acapella")
        assertEquals("Acapella", loaded.title)
        assertNull(loaded.bpm)
        assertNull(loaded.key)
        assertNull(loaded.timeSignature)
        assertNull(loaded.notes)
    }

    @Test
    fun loadMetadata_handlesCorruptedJsonGracefully() {
        val projectDir = tempFolder.newFolder("CorruptedTrack")
        val file = File(projectDir, "project.json")
        file.writeText("{ invalid json content ...")

        val loaded = ProjectStorage.loadMetadata(projectDir, "CorruptedTrack")
        assertEquals("CorruptedTrack", loaded.title)
        assertNull(loaded.bpm)
    }

    @Test
    fun fromJsonObject_readsVersionOneProjectsWithoutMarkers() {
        val legacy = JSONObject().apply {
            put("version", 1)
            put("title", "Legacy Track")
        }

        val metadata = ProjectMetadata.fromJsonObject(legacy, "Fallback")

        assertEquals("Legacy Track", metadata.title)
        assertEquals(1, metadata.version)
        assertTrue(metadata.markers.isEmpty())
    }

    @Test
    fun fromJsonObject_invalidMarkersFieldPreservesOtherMetadata() {
        for (markersValue in listOf(JSONObject.NULL, "invalid", 42, JSONObject())) {
            val json = JSONObject()
                .put("title", "Track")
                .put("bpm", 92)
                .put("markers", markersValue)

            val metadata = ProjectMetadata.fromJsonObject(json, "Fallback")

            assertTrue("Markers value: $markersValue", metadata.markers.isEmpty())
            assertEquals("Track", metadata.title)
            assertEquals(92, metadata.bpm)
        }
    }

    @Test
    fun fromJsonObject_skipsIncompleteMarkersAndKeepsValidEntriesInOrder() {
        val json = JSONObject().put("markers", JSONArray().apply {
            put(JSONObject().put("label", "Start").put("positionMs", 0L))
            put(JSONObject().put("label", "Missing position"))
            put(JSONObject().put("label", "Null position").put("positionMs", JSONObject.NULL))
            put(JSONObject().put("label", "Invalid position").put("positionMs", "invalid"))
            put(JSONObject().put("positionMs", 100L))
            put(JSONObject().put("label", JSONObject.NULL).put("positionMs", 100L))
            put(JSONObject().put("label", "   ").put("positionMs", 100L))
            put(JSONObject.NULL)
            put(JSONObject().put("label", "Hook").put("positionMs", 32_000L))
        })

        val metadata = ProjectMetadata.fromJsonObject(json, "Track")

        assertEquals(
            listOf(WaveformMarker("Start", 0L), WaveformMarker("Hook", 32_000L)),
            metadata.markers,
        )
    }

    @Test
    fun metadata_serializesMarkersAndIgnoresMalformedMarkerEntries() {
        val metadata = ProjectMetadata(
            title = "Marked Track",
            markers = listOf(WaveformMarker("Bridge", 45_000L)),
        )

        val serializedMarkers = metadata.toJsonObject().getJSONArray("markers")
        assertEquals(1, serializedMarkers.length())
        assertEquals("Bridge", serializedMarkers.getJSONObject(0).getString("label"))
        assertEquals(45_000L, serializedMarkers.getJSONObject(0).getLong("positionMs"))

        val malformed = JSONObject().apply {
            put("title", "Marked Track")
            put("markers", JSONArray().apply {
                put(JSONObject().put("label", "Valid").put("positionMs", 1_000L))
                put(JSONObject().put("label", "").put("positionMs", 2_000L))
                put(JSONObject().put("label", "Negative").put("positionMs", -1L))
                put("not an object")
            })
        }

        assertEquals(
            listOf(WaveformMarker("Valid", 1_000L)),
            ProjectMetadata.fromJsonObject(malformed, "Fallback").markers,
        )
    }
}
