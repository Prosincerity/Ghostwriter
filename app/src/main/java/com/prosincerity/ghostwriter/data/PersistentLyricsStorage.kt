package com.prosincerity.ghostwriter.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import java.io.File
import java.io.IOException

/** Coordinates the shared lyric archive and local working copies under one save monitor. */
internal object PersistentLyricsStorage {
    @Synchronized
    fun connect(context: Context, uri: Uri) {
        context.contentResolver.takePersistableUriPermission(uri,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        reconcile(context, uri)
        if (!Settings.setPersistentLyricsFolder(context, uri.toString())) {
            throw IOException("Couldn't remember the lyric folder")
        }
    }

    @Synchronized
    fun reconcile(context: Context, uri: Uri) {
        // Legacy projects migrate before merging so every archive path has a stable ID.
        ProjectStorage.listProjects(context)
        val archive = DocumentProjectArchive(context, uri)
        archive.restore(ProjectStorage.rootDir(context))
        for (id in ProjectStorage.listProjects(context)) {
            archive.save(ProjectStorage.projectDir(context, id), Settings.getAutosaveCount(context))
        }
    }

    @Synchronized
    fun save(context: Context, project: File, keepCount: Int): Boolean = runCatching {
        val uri = Settings.getPersistentLyricsFolder(context)?.let(Uri::parse)
            ?: throw IOException("Choose a lyric folder first")
        DocumentProjectArchive(context, uri).save(project, keepCount)
    }.isSuccess

    @Synchronized
    fun delete(context: Context, projectId: String): Boolean = runCatching {
        val uri = Settings.getPersistentLyricsFolder(context)?.let(Uri::parse)
            ?: throw IOException("Choose a lyric folder first")
        DocumentProjectArchive(context, uri).delete(projectId)
    }.isSuccess
}
