package com.prosincerity.ghostwriter.projects.data

import android.app.backup.BackupAgentHelper
import android.app.backup.FullBackupDataOutput
import com.prosincerity.ghostwriter.settings.Settings
import java.io.File
import java.io.IOException

/** Select files individually because backup XML cannot match files inside UUID directories. */
class ProjectBackupAgent : BackupAgentHelper() {
    override fun onFullBackup(data: FullBackupDataOutput) {
        val external = getExternalFilesDir(null) ?: throw IOException("Project storage is unavailable for backup")
        val preferences = File(applicationInfo.dataDir, "shared_prefs/${Settings.PREFS_NAME}.xml")
        val projects = File(external, ProjectStorage.PROJECT_DIRECTORY)
        for (file in projectBackupFiles(projects, preferences)) fullBackupFile(file, data)
    }
}
