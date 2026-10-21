package com.prosincerity.ghostwriter.data

import org.json.JSONObject
import org.junit.Assert.*
import org.junit.Test

class PersistentProjectSnapshotTest {
    private val id = "00000000-0000-4000-8000-000000000001"
    private val snapshot = PersistentProjectSnapshot(id, 1234L,
        ProjectMetadata("Verse / chorus", notes = "Notes 🎵", createdAt = 1000L, updatedAt = 1234L).toJsonObject().toString(), "字\nVerse 🎵\u0000")

    @Test fun roundTripPreservesEmptyAndUnicodeLyricsAndCompleteMetadata() {
        assertEquals(snapshot, PersistentProjectSnapshot.decode(snapshot.encode(), id))
        val empty = snapshot.copy(lyrics = "")
        assertEquals(empty, PersistentProjectSnapshot.decode(empty.encode(), id))
    }

    @Test fun contentFingerprintIgnoresSaveTimeButDetectsDraftOrMetadataChanges() {
        assertEquals(snapshot.contentFingerprint, snapshot.copy(savedAt = 5000).contentFingerprint)
        assertNotEquals(snapshot.contentFingerprint, snapshot.copy(lyrics = "changed").contentFingerprint)
        assertNotEquals(snapshot.contentFingerprint,
            snapshot.copy(metadata = ProjectMetadata("Renamed", createdAt = 1000L, updatedAt = 1234L).toJsonObject().toString()).contentFingerprint)
    }

    @Test fun corruptedAndMisplacedSavesCannotReplaceAnOlderGoodSnapshot() {
        for (key in listOf("lyrics", "metadata", "savedAt", "projectId", "checksum", "format")) {
            val encoded = JSONObject(snapshot.encode()).put(key, "corrupted").toString()
            assertNull(PersistentProjectSnapshot.decode(encoded, id))
        }
        assertNull(PersistentProjectSnapshot.decode(snapshot.encode().take(20), id))
        assertNull(PersistentProjectSnapshot.decode(snapshot.encode(), "00000000-0000-4000-8000-000000000002"))
        assertNull(PersistentProjectSnapshot.decode(snapshot.copy(projectId = "../outside").encode(), "../outside"))
        assertNull(PersistentProjectSnapshot.decode(snapshot.copy(savedAt = 0L).encode(), id))
        assertNull(PersistentProjectSnapshot.decode(snapshot.copy(metadata = "{}").encode(), id))
    }
}
