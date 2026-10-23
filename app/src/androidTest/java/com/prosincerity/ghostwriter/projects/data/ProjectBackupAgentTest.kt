package com.prosincerity.ghostwriter.projects.data

import android.app.backup.BackupAgent
import android.content.pm.ApplicationInfo
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class ProjectBackupAgentTest {
    @Test fun applicationUsesSelectiveFullBackupInsteadOfIncludingAllProjectAudio() {
        val context = InstrumentationRegistry.getInstrumentation().targetContext
        val info = context.packageManager.getApplicationInfo(context.packageName, 0)

        assertEquals("com.prosincerity.ghostwriter.projects.data.ProjectBackupAgent", info.backupAgentName)
        assertTrue(info.flags and ApplicationInfo.FLAG_FULL_BACKUP_ONLY != 0)
        assertTrue(Class.forName(info.backupAgentName).getDeclaredConstructor().newInstance() is BackupAgent)
    }
}
