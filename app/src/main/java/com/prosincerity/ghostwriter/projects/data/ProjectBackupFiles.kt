package com.prosincerity.ghostwriter.projects.data

import java.io.File
import java.io.IOException

private val backupMetadataFiles = setOf("project.json", ".lyric-archive.json", ".lyric-restore.json")

/** Portable user data only; beats, dictionaries, engine data, caches, and staging stay local. */
internal fun projectBackupFiles(projectRoot: File, preferences: File): List<File> = buildList {
    if (preferences.isFile) add(preferences)
    val root = projectRoot.canonicalFile
    for (project in projectRoot.backupChildren()) {
        if (!project.isDirectory || project.name.startsWith(".") || project.canonicalFile.parentFile != root) continue
        val directory = project.canonicalFile
        for (file in project.backupChildren()) {
            if (file.isFile && file.canonicalFile.parentFile == directory &&
                (file.name in backupMetadataFiles || file.extension.equals("txt", ignoreCase = true))) {
                add(file)
            }
        }
    }
}

private fun File.backupChildren(): List<File> {
    if (!exists()) return emptyList()
    return listFiles()?.toList() ?: throw IOException("Couldn't list project files for backup")
}
