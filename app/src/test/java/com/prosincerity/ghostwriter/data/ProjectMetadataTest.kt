package com.prosincerity.ghostwriter.data

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Assert.assertTrue
import org.junit.Assert.assertThrows
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TemporaryFolder
import org.json.JSONArray
import org.json.JSONObject
import java.io.File

class ProjectMetadataTest {

    @Test
    fun frameMarkers_persistExactSamplesWithoutMillisecondRounding() {
        val marker = WaveformMarker("Start", 0, MarkerLoopRole.START, frameIndex = 44144, sampleRate = 44100)
            .withSampleRate(44100)
        val json = ProjectMetadata("Frames", markers = listOf(marker)).toJsonObject()
        val stored = json.getJSONArray("markers").getJSONObject(0)
        assertTrue(!stored.has("positionMs"))
        assertEquals(44144L, stored.getLong("frameIndex"))
        assertEquals(44100, stored.getInt("sampleRate"))
        val loaded = ProjectMetadata.fromJsonObject(json, "Fallback").markers.single()
        assertEquals(marker, loaded)
        assertEquals(44144L, loaded.frameAt(44100))
        assertEquals(1001L, loaded.positionMs)
    }

    @Test
    fun legacyMarkers_migrateAndDraggingUpdatesTheAuthoritativeFrame() {
        val legacy = WaveformMarker("End", 1001, MarkerLoopRole.END)
        val migrated = legacy.withSampleRate(44100)
        assertEquals(44144L, migrated.frameIndex)
        assertEquals(1001L, migrated.positionMs)
        assertEquals(48047L, migrated.withSampleRate(48000).frameIndex)
        val moved = migrated.atPositionMs(2000, 44100)
        assertEquals(88200L, moved.frameIndex)
        assertEquals(MarkerLoopRole.END, moved.loopRole)
    }

    @Test
    fun markerLoopRoles_roundTripAndOldOrUnknownRolesRemainOrdinary() {
        val markers = listOf(
            WaveformMarker("Start", 1000, MarkerLoopRole.START),
            WaveformMarker("End", 2000, MarkerLoopRole.END),
            WaveformMarker("Verse", 1500),
        )
        val json = ProjectMetadata("Loop", markers = markers).toJsonObject()
        assertEquals(markers, ProjectMetadata.fromJsonObject(json, "Fallback").markers)
        val entries = json.getJSONArray("markers")
        assertEquals("start", entries.getJSONObject(0).getString("loopRole"))
        entries.getJSONObject(0).remove("loopRole")
        entries.getJSONObject(1).put("loopRole", "future-role")
        assertTrue(ProjectMetadata.fromJsonObject(json, "Fallback").markers.all { it.loopRole == MarkerLoopRole.NONE })
    }

    @Test
    fun framePositions_rejectInvalidTargetRatesAndFallBackForIncompleteSavedFrames() {
        val marker = WaveformMarker("Start", 1500, MarkerLoopRole.START)
        val precise = marker.withSampleRate(48000)
        for (rate in listOf(0, -1)) {
            assertEquals(precise, precise.withSampleRate(rate))
            assertThrows(IllegalArgumentException::class.java) { marker.frameAt(rate) }
        }
        for (incomplete in listOf(
            marker.copy(frameIndex = 99),
            marker.copy(sampleRate = 44100),
            marker.copy(frameIndex = 99, sampleRate = 0),
            marker.copy(frameIndex = 99, sampleRate = -1),
        )) {
            assertEquals(72000L, incomplete.frameAt(48000))
            val normalized = incomplete.withSampleRate(48000)
            assertEquals(72000L, normalized.frameIndex)
            assertEquals(1500L, normalized.positionMs)
            assertEquals(MarkerLoopRole.START, normalized.loopRole)
        }
    }

    @Test
    fun frameNormalization_repairsStaleMillisecondsWithoutChangingExactFrames() {
        val marker = WaveformMarker("End", 99, MarkerLoopRole.END, 48001, 48000)
        val normalized = marker.withSampleRate(48000)
        assertEquals(1000L, normalized.positionMs)
        assertEquals(48001L, normalized.frameIndex)
        assertEquals(normalized, normalized.withSampleRate(48000))
        val movedWithoutRate = normalized.atPositionMs(2000, 0)
        assertEquals(2000L, movedWithoutRate.positionMs)
        assertNull(movedWithoutRate.frameIndex)
        assertNull(movedWithoutRate.sampleRate)
    }

    @Test
    fun incompleteFrameMetadata_roundTripsAsLegacyPositions() {
        val marker = WaveformMarker("End", 1500, MarkerLoopRole.END)
        val incomplete = listOf(
            marker.copy(frameIndex = 42), marker.copy(sampleRate = 48000),
            marker.copy(frameIndex = 42, sampleRate = 0),
        )
        val json = ProjectMetadata("Legacy", markers = incomplete).toJsonObject()
        val stored = json.getJSONArray("markers")
        repeat(stored.length()) { index ->
            assertEquals(1500L, stored.getJSONObject(index).getLong("positionMs"))
            assertTrue(!stored.getJSONObject(index).has("frameIndex"))
        }
        assertEquals(List(3) { marker }, ProjectMetadata.fromJsonObject(json, "Fallback").markers)
    }

    @Test
    fun incompleteSavedFrames_useLegacyPositionAndSkipEntriesWithNoUsablePosition() {
        val entries = JSONArray()
        for (fields in listOf(
            JSONObject().put("frameIndex", 42),
            JSONObject().put("sampleRate", 48000),
            JSONObject().put("frameIndex", 42).put("sampleRate", 0),
            JSONObject().put("frameIndex", -1).put("sampleRate", 48000),
        )) {
            entries.put(JSONObject(fields.toString()).put("label", "Legacy").put("positionMs", 1500))
            entries.put(JSONObject(fields.toString()).put("label", "Missing position"))
        }
        val metadata = ProjectMetadata.fromJsonObject(JSONObject().put("markers", entries), "Fallback")
        assertEquals(List(4) { WaveformMarker("Legacy", 1500) }, metadata.markers)
    }

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
    fun toJsonObject_writesJsonNullForEveryOptionalField() {
        val json = ProjectMetadata(title = "Acapella").toJsonObject()

        for (field in listOf("bpm", "key", "timeSignature", "notes", "beatFile", "beatOriginalName")) {
            assertTrue("Expected $field to be present", json.has(field))
            assertTrue("Expected $field to contain JSON null", json.isNull(field))
        }
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
