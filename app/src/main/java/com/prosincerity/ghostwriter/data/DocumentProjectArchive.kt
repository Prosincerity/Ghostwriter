package com.prosincerity.ghostwriter.data

import android.content.Context
import android.net.Uri
import android.provider.DocumentsContract
import java.io.File
import android.os.ParcelFileDescriptor
import java.io.IOException
import java.util.UUID
import org.json.JSONObject

/** AOSP SAF storage: immutable saves preserve the previous good copy on failed writes. */
internal class DocumentProjectArchive(context: Context, private val treeUri: Uri) {
    private val resolver = context.applicationContext.contentResolver
    private data class Entry(val uri: Uri, val name: String, val directory: Boolean)
    private data class Saved(val entry: Entry, val snapshot: PersistentProjectSnapshot)
    private val selected = DocumentsContract.buildDocumentUriUsingTree(
        treeUri, DocumentsContract.getTreeDocumentId(treeUri),
    )
    private val root: Uri by lazy {
        val name = resolver.query(selected, arrayOf(DocumentsContract.Document.COLUMN_DISPLAY_NAME), null, null, null)
            ?.use { if (it.moveToFirst()) it.getString(0) else null }
            ?: throw IOException("The selected folder is unavailable")
        if (name.equals("Ghostwriter", ignoreCase = true)) selected else directory(selected, "Ghostwriter")
    }

    private fun children(parent: Uri): List<Entry> {
        val uri = DocumentsContract.buildChildDocumentsUriUsingTree(treeUri, DocumentsContract.getDocumentId(parent))
        return resolver.query(uri, arrayOf(
            DocumentsContract.Document.COLUMN_DOCUMENT_ID,
            DocumentsContract.Document.COLUMN_DISPLAY_NAME,
            DocumentsContract.Document.COLUMN_MIME_TYPE,
        ), null, null, null)?.use { cursor ->
            buildList {
                while (cursor.moveToNext()) add(Entry(
                    DocumentsContract.buildDocumentUriUsingTree(treeUri, cursor.getString(0)),
                    cursor.getString(1), cursor.getString(2) == DocumentsContract.Document.MIME_TYPE_DIR,
                ))
            }
        } ?: throw IOException("Couldn't read the lyric folder")
    }

    private fun directory(parent: Uri, name: String): Uri {
        children(parent).firstOrNull { it.name == name }?.let {
            if (!it.directory) throw IOException("$name is a file, not a folder")
            return it.uri
        }
        return DocumentsContract.createDocument(resolver, parent, DocumentsContract.Document.MIME_TYPE_DIR, name)
            ?: throw IOException("Couldn't create the lyric folder")
    }

    private fun snapshots(directory: Uri, projectId: String): List<Saved> = children(directory)
        .filter { !it.directory && it.name.startsWith("snapshot-") && it.name.endsWith(".json") }
        .mapNotNull { entry ->
            // Unreadable/incomplete saves are skipped so older intact lyrics can recover.
            val snapshot = runCatching {
                resolver.openInputStream(entry.uri)?.bufferedReader(Charsets.UTF_8)?.use { reader ->
                    PersistentProjectSnapshot.decode(reader.readText(), projectId)
                }
            }.getOrNull()
            snapshot?.let { Saved(entry, it) }
        }.sortedByDescending { it.snapshot.savedAt }

    private val archiveKey: String get() = "${root.authority}/${DocumentsContract.getDocumentId(root)}"
    private data class Checkpoint(val fingerprint: String, val savedAt: Long)

    private fun workingCopy(project: File): PersistentProjectSnapshot {
        val file = ProjectStorage.metadataFile(project)
        val json = runCatching { JSONObject(file.readText()) }.getOrNull()
        val metadata = ProjectStorage.loadMetadata(project, project.name)
        val fileTime = file.lastModified()
        val stable = metadata.copy(
            createdAt = if (json?.has("createdAt") == true) metadata.createdAt else fileTime,
            updatedAt = if (json?.has("updatedAt") == true) metadata.updatedAt else fileTime,
        )
        return PersistentProjectSnapshot(project.name, 1L, stable.toJsonObject().toString(), ProjectStorage.loadLatest(project))
    }

    private fun checkpoint(project: File): Checkpoint? = runCatching {
        val json = JSONObject(File(project, ".lyric-archive.json").readText())
        if (json.getString("archive") != archiveKey) return null
        Checkpoint(json.getString("fingerprint"), json.getLong("savedAt"))
    }.getOrNull()

    private fun rememberSave(project: File, snapshot: PersistentProjectSnapshot) = synchronized(ProjectStorage) {
        // A second editor may have written a newer draft while provider I/O ran.
        if (workingCopy(project).contentFingerprint != snapshot.contentFingerprint) return@synchronized
        StagedFileWriter.writeText(File(project, ".lyric-archive.json"), JSONObject().apply {
            put("archive", archiveKey)
            put("fingerprint", snapshot.contentFingerprint)
            put("savedAt", snapshot.savedAt)
        }.toString())
    }

    fun save(project: File, keepCount: Int) {
        require(ProjectStorage.isProjectId(project.name))
        require(keepCount in Settings.COUNT_OPTIONS)
        val current = synchronized(ProjectStorage) { workingCopy(project) }
        val directory = directory(root, project.name)
        val previous = snapshots(directory, project.name)
        val latest = previous.firstOrNull()?.snapshot
        if (latest?.metadata == current.metadata && latest.lyrics == current.lyrics) {
            rememberSave(project, latest)
            prune(previous, keepCount)
            return
        }
        val snapshot = current.copy(savedAt = maxOf(System.currentTimeMillis(), (latest?.savedAt ?: 0L) + 1L))
        val uri = DocumentsContract.createDocument(resolver, directory, "application/json", "snapshot-${UUID.randomUUID()}.json")
            ?: throw IOException("Couldn't create the lyric save")
        try {
            val bytes = snapshot.encode().toByteArray(Charsets.UTF_8)
            val descriptor = resolver.openFileDescriptor(uri, "w") ?: throw IOException("Couldn't open the lyric save")
            ParcelFileDescriptor.AutoCloseOutputStream(descriptor).use { output ->
                output.write(bytes)
                output.flush()
                output.fd.sync()
            }
            val verified = resolver.openInputStream(uri)?.bufferedReader(Charsets.UTF_8)?.use {
                PersistentProjectSnapshot.decode(it.readText(), project.name)
            }
            if (verified != snapshot) throw IOException("Couldn't verify the lyric save")
        } catch (failure: Exception) {
            runCatching { DocumentsContract.deleteDocument(resolver, uri) }
            throw failure
        }
        // KeepCount includes this new durable snapshot. Never prune before verification.
        rememberSave(project, snapshot)
        prune(previous, keepCount - 1)
    }

    private fun prune(saved: List<Saved>, keep: Int) {
        saved.drop(keep).forEach { runCatching { DocumentsContract.deleteDocument(resolver, it.entry.uri) } }
    }

    /** Restore missing/older working copies, preserving newer local drafts and beat files. */
    fun restore(localRoot: File): Int {
        var restored = 0
        for (entry in children(root).filter { it.directory && ProjectStorage.isProjectId(it.name) }) {
            val saved = snapshots(entry.uri, entry.name)
            val latest = saved.firstOrNull()?.snapshot
            val project = File(localRoot, entry.name)
            synchronized(ProjectStorage) {
                if (project.isDirectory) {
                    val checkpoint = checkpoint(project)
                    // Timestamps alone cannot distinguish restored app data from
                    // a new edit. Only overwrite a working copy that still matches
                    // its last verified shared save and is behind the archive.
                    if (checkpoint == null || checkpoint.fingerprint != workingCopy(project).contentFingerprint ||
                        checkpoint.savedAt >= (latest?.savedAt ?: 0L)) return@synchronized
                }
                if (latest == null) {
                    if (children(entry.uri).any { it.name.startsWith("snapshot-") && it.name.endsWith(".json") }) {
                        throw IOException("This folder contains a project with no readable lyric saves")
                    }
                    return@synchronized
                }
                check(project.isDirectory || project.mkdirs()) { "Couldn't restore project" }
                for ((index, record) in saved.take(10).withIndex()) {
                    val file = File(project, "autosave${index + 1}.txt")
                    StagedFileWriter.writeText(file, record.snapshot.lyrics)
                    file.setLastModified(record.snapshot.savedAt)
                }
                val manual = File(project, ProjectLyricsStorage.MANUAL_FILE_NAME)
                StagedFileWriter.writeText(manual, latest.lyrics)
                manual.setLastModified(latest.savedAt)
                StagedFileWriter.writeText(ProjectStorage.metadataFile(project), latest.metadata)
                rememberSave(project, latest)
                restored++
            }
        }
        return restored
    }

    fun delete(projectId: String) {
        require(ProjectStorage.isProjectId(projectId))
        val project = children(root).firstOrNull { it.directory && it.name == projectId } ?: return
        if (!DocumentsContract.deleteDocument(resolver, project.uri)) throw IOException("Couldn't delete the saved lyrics")
    }
}
