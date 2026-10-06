package com.prosincerity.ghostwriter.data

import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.File
import java.io.IOException
import java.util.UUID
import org.junit.Assert.*
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class DocumentProjectArchiveTest {
    private val context = InstrumentationRegistry.getInstrumentation().targetContext
    private val scope = UUID.randomUUID().toString()
    private val tree = DocumentsContract.buildTreeDocumentUri(LyricArchiveTestProvider.AUTHORITY, scope)
    private val localRoot = File(context.cacheDir, "archive-working-$scope")
    private val archive get() = DocumentProjectArchive(context, tree)
    private val resolver get() = context.contentResolver
    private val provider = Uri.parse("content://${LyricArchiveTestProvider.AUTHORITY}")

    @Before fun setup() { assertTrue(localRoot.mkdirs()) }
    @After fun cleanup() {
        localRoot.deleteRecursively()
        resolver.call(provider, "clear", scope, null)
    }

    private fun children(parent: Uri): List<Pair<String, Uri>> {
        val query = DocumentsContract.buildChildDocumentsUriUsingTree(tree, DocumentsContract.getDocumentId(parent))
        return resolver.query(query, arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_DOCUMENT_ID), null, null, null)!!.use { cursor ->
            buildList { while (cursor.moveToNext()) add(cursor.getString(0) to
                DocumentsContract.buildDocumentUriUsingTree(tree, cursor.getString(1))) }
        }
    }
    private fun savedFiles(projectId: String): List<Uri> {
        val root = DocumentsContract.buildDocumentUriUsingTree(tree, scope)
        val ghostwriter = children(root).single { it.first == "Ghostwriter" }.second
        val project = children(ghostwriter).single { it.first == projectId }.second
        return children(project).map { it.second }
    }
    private fun failWrites(enabled: Boolean) {
        resolver.call(provider, "failWrites", scope, Bundle().apply { putBoolean("enabled", enabled) })
    }
    private fun fault(kind: String?) {
        resolver.call(provider, "fault", scope, kind?.let { Bundle().apply { putString("kind", it) } })
    }
    private val selected get() = DocumentsContract.buildDocumentUriUsingTree(tree, scope)
    private fun create(parent: Uri, name: String, directory: Boolean = false): Uri =
        DocumentsContract.createDocument(resolver, parent,
            if (directory) DocumentsContract.Document.MIME_TYPE_DIR else "application/json", name)!!
    private fun draft(project: File, text: String) {
        assertTrue(ProjectStorage.saveManual(project, "unused title", text, 3))
    }

    @Test fun unavailableFolderQueriesAndCreationReportUsefulFailures() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        for (kind in listOf("nullRoot", "emptyRoot", "nullChildren", "createFolder")) {
            fault(kind)
            assertThrows(IOException::class.java) { archive.save(project, 3) }
            assertTrue(project.isDirectory)
        }
        fault(null)
        create(selected, "Ghostwriter")
        assertThrows(IOException::class.java) { archive.save(project, 3) }
    }

    @Test fun failedCreationOpeningAndReadBackLeaveEarlierVerifiedSaveIntact() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        draft(project, "previous"); archive.save(project, 3)
        val previous = savedFiles(project.name).single()
        draft(project, "new draft")
        for (kind in listOf("createSnapshot", "nullWrite", "read")) {
            fault(kind)
            assertThrows(IOException::class.java) { archive.save(project, 3) }
            fault(null)
            assertEquals(listOf(previous), savedFiles(project.name))
            assertEquals("new draft", ProjectStorage.loadLatest(project))
        }
        // A corrupt read-back must also remove the just-created snapshot.
        val fresh = ProjectStorage.createProjectDirectory(localRoot, "fresh")
        draft(fresh, "fresh draft")
        fault("corruptRead")
        assertThrows(IOException::class.java) { archive.save(fresh, 3) }
        fault(null)
        assertTrue(savedFiles(fresh.name).isEmpty())
    }

    @Test fun validationRejectsInvalidIdsAndBackupCountsBeforeWriting() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        assertThrows(IllegalArgumentException::class.java) { archive.save(localRoot, 3) }
        assertThrows(IllegalArgumentException::class.java) { archive.save(project, 0) }
        assertThrows(IllegalArgumentException::class.java) { archive.delete("../song") }
        archive.delete(project.name)
        assertTrue(project.isDirectory)
    }

    @Test fun restoreIgnoresUnrelatedEntriesAndEmptyProjectFolders() {
        val folder = create(selected, "Ghostwriter", true)
        create(folder, "readme")
        create(folder, "unrelated", true)
        val emptyId = UUID.randomUUID().toString()
        val empty = create(folder, emptyId, true)
        create(empty, "readme")
        create(empty, "snapshot-incomplete.txt")
        assertEquals(0, archive.restore(localRoot))
        assertFalse(File(localRoot, emptyId).exists())
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        draft(project, "lyrics"); archive.save(project, 3)
        val directory = children(folder).single { it.first == project.name }.second
        create(directory, "snapshot-directory.json", true)
        create(directory, "other.json")
        assertTrue(project.deleteRecursively())
        assertEquals(1, archive.restore(localRoot))
        assertEquals("lyrics", ProjectStorage.loadLatest(project))
        fault("delete")
        assertThrows(IllegalArgumentException::class.java) { archive.delete(project.name) }
        fault(null)
        archive.delete(project.name)
    }

    @Test fun missingOrForeignCheckpointsPreserveExistingLocalLyrics() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        draft(project, "lyrics"); archive.save(project, 3)
        val checkpoint = File(project, ".lyric-archive.json")
        val original = checkpoint.readText()
        assertTrue(checkpoint.delete())
        assertEquals(0, archive.restore(localRoot))
        checkpoint.writeText(org.json.JSONObject(original).put("archive", "another/folder").toString())
        assertEquals(0, archive.restore(localRoot))
        checkpoint.writeText("invalid")
        assertEquals(0, archive.restore(localRoot))
        assertEquals("lyrics", ProjectStorage.loadLatest(project))
    }

    @Test fun missingMetadataCanBeArchivedAndBlockedRestoreDoesNotDestroyLocalFiles() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        draft(project, "lyrics")
        assertTrue(ProjectStorage.metadataFile(project).delete())
        archive.save(project, 3)
        val blockedRoot = File(localRoot, ".blocked").apply { mkdir() }
        val blocked = File(blockedRoot, project.name).apply { writeText("keep") }
        assertThrows(IllegalStateException::class.java) { archive.restore(blockedRoot) }
        assertEquals("keep", blocked.readText())
    }

    @Test fun reinstallRecoveryKeepsStableIdUnicodeLyricsAndMetadata() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "Verse / chorus 🎵")
        ProjectStorage.saveMetadata(project, ProjectMetadata("Verse / chorus 🎵", bpm = 92, notes = "keep notes"))
        File(project, "beat.wav").writeText("not part of lyric archive")
        File(project, "waveform.dat").writeText("regenerable")
        draft(project, "first 字")
        archive.save(project, 3)
        draft(project, "latest 🎵")
        archive.save(project, 3)
        assertTrue(project.deleteRecursively()) // Simulate uninstall removing only app-specific working data.
        assertEquals(1, archive.restore(localRoot))
        val restored = File(localRoot, project.name)
        assertEquals("latest 🎵", ProjectStorage.loadLatest(restored))
        assertEquals("first 字", File(restored, "autosave2.txt").readText())
        val metadata = ProjectStorage.loadMetadata(restored, "fallback")
        assertEquals("Verse / chorus 🎵", metadata.title)
        assertEquals(92, metadata.bpm)
        assertEquals("keep notes", metadata.notes)
        assertFalse(File(restored, "beat.wav").exists())
        assertFalse(File(restored, "waveform.dat").exists())
    }

    @Test fun failedNewWritePreservesThePreviousSaveForRecovery() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        draft(project, "previous")
        archive.save(project, 3)
        draft(project, "new local draft")
        failWrites(true)
        assertThrows(IOException::class.java) { archive.save(project, 3) }
        assertEquals("new local draft", ProjectStorage.loadLatest(project))
        assertEquals(1, savedFiles(project.name).size)
        failWrites(false)
        assertTrue(project.deleteRecursively())
        archive.restore(localRoot)
        assertEquals("previous", ProjectStorage.loadLatest(File(localRoot, project.name)))
    }

    @Test fun corruptedNewestSaveFallsBackToOlderVerifiedLyrics() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        draft(project, "previous"); archive.save(project, 3)
        draft(project, "latest"); archive.save(project, 3)
        val newest = savedFiles(project.name).maxBy { uri ->
            resolver.openInputStream(uri)!!.bufferedReader().use {
                PersistentProjectSnapshot.decode(it.readText(), project.name)!!.savedAt
            }
        }
        resolver.openOutputStream(newest, "wt")!!.use { it.write("incomplete".toByteArray()) }
        assertTrue(project.deleteRecursively())
        archive.restore(localRoot)
        assertEquals("previous", ProjectStorage.loadLatest(File(localRoot, project.name)))
    }

    @Test fun newerLocalDraftIsKeptWhenReconnectingAndThenArchived() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        draft(project, "archived"); archive.save(project, 3)
        draft(project, "newer local")
        File(project, "lyrics.txt").setLastModified(System.currentTimeMillis() + 60_000)
        assertEquals(0, archive.restore(localRoot))
        assertEquals("newer local", ProjectStorage.loadLatest(project))
        archive.save(project, 3)
        assertTrue(project.deleteRecursively())
        archive.restore(localRoot)
        assertEquals("newer local", ProjectStorage.loadLatest(File(localRoot, project.name)))
    }

    @Test fun restoringOldAppDataUsesTheSharedSaveEvenIfFileTimesWereReset() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        draft(project, "older app backup"); archive.save(project, 3)
        val oldWorkingCopy = File(localRoot, ".app-backup")
        project.copyRecursively(oldWorkingCopy)
        draft(project, "latest shared lyrics"); archive.save(project, 3)
        assertTrue(project.deleteRecursively())
        assertTrue(oldWorkingCopy.renameTo(project))
        project.listFiles { it.extension == "txt" }!!.forEach {
            it.setLastModified(System.currentTimeMillis() + 60_000)
        }
        assertEquals(1, archive.restore(localRoot))
        assertEquals("latest shared lyrics", ProjectStorage.loadLatest(project))
    }

    @Test fun clockChangesCannotOverwriteAnUnarchivedLocalDraft() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        draft(project, "archived"); archive.save(project, 3)
        draft(project, "unarchived draft")
        project.listFiles { it.extension == "txt" }!!.forEach {
            it.setLastModified(if (it.name == "autosave2.txt") 0L else 1L)
        }
        assertEquals(0, archive.restore(localRoot))
        assertEquals("unarchived draft", ProjectStorage.loadLatest(project))
    }

    @Test fun allCorruptSavesReportFailureUntilAnExistingLocalCopyRepairsThem() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        draft(project, "good local copy"); archive.save(project, 3)
        val saved = savedFiles(project.name).single()
        resolver.openOutputStream(saved, "wt")!!.use { it.write("incomplete".toByteArray()) }
        val missingRoot = File(localRoot, ".reinstall").apply { mkdir() }
        assertThrows(IOException::class.java) { archive.restore(missingRoot) }
        assertEquals(0, archive.restore(localRoot))
        archive.save(project, 3)
        assertTrue(project.deleteRecursively())
        archive.restore(localRoot)
        assertEquals("good local copy", ProjectStorage.loadLatest(File(localRoot, project.name)))
    }

    @Test fun selectingAnExistingGhostwriterFolderDoesNotNestAnotherFolder() {
        val selected = DocumentsContract.buildDocumentUriUsingTree(tree, scope)
        val folder = DocumentsContract.createDocument(resolver, selected,
            DocumentsContract.Document.MIME_TYPE_DIR, "Ghostwriter")!!
        val nestedTree = DocumentsContract.buildTreeDocumentUri(LyricArchiveTestProvider.AUTHORITY,
            DocumentsContract.getDocumentId(folder))
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        draft(project, "lyrics")
        DocumentProjectArchive(context, nestedTree).save(project, 3)
        assertEquals(listOf(project.name), children(folder).map { it.first })
    }

    @Test fun unchangedLegacyMetadataDoesNotConsumeAnotherBackupSlot() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        draft(project, "lyrics")
        val metadata = org.json.JSONObject(ProjectStorage.metadataFile(project).readText())
        metadata.remove("createdAt"); metadata.remove("updatedAt")
        ProjectStorage.metadataFile(project).writeText(metadata.toString())
        archive.save(project, 3)
        val before = savedFiles(project.name).toSet()
        archive.save(project, 3)
        assertEquals(before, savedFiles(project.name).toSet())
    }

    @Test fun unchangedSavesDoNotRotateAndRetentionPrunesOnlyAfterSuccess() {
        val project = ProjectStorage.createProjectDirectory(localRoot, "song")
        for (index in 1..6) { draft(project, "take $index"); archive.save(project, 3) }
        val before = savedFiles(project.name).toSet()
        assertEquals(3, before.size)
        archive.save(project, 3)
        assertEquals(before, savedFiles(project.name).toSet())
        assertTrue(ProjectStorage.renameProjectDirectory(project, "New / title") != null)
        archive.save(project, 3)
        assertTrue(project.deleteRecursively())
        archive.restore(localRoot)
        assertEquals("New / title", ProjectStorage.loadMetadata(File(localRoot, project.name), "fallback").title)
        archive.delete(project.name)
        val root = DocumentsContract.buildDocumentUriUsingTree(tree, scope)
        val ghostwriter = children(root).single().second
        assertTrue(children(ghostwriter).isEmpty())
    }
}
