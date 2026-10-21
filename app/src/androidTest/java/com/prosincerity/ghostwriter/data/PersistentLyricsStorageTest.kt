package com.prosincerity.ghostwriter.data

import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.provider.DocumentsContract
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.platform.app.InstrumentationRegistry
import java.io.IOException
import org.junit.After
import org.junit.Assert.*
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class PersistentLyricsStorageTest {
    @get:Rule val testName = TestName()
    private val instrumentation = InstrumentationRegistry.getInstrumentation()
    private val context = instrumentation.targetContext
    private val scope get() = "persistent-lyrics-${testName.methodName}"
    private val tree get() = DocumentsContract.buildTreeDocumentUri(LyricArchiveTestProvider.AUTHORITY, scope)
    private val provider = Uri.parse("content://${LyricArchiveTestProvider.AUTHORITY}")
    private var originalFolder: String? = null
    private val projects = mutableListOf<String>()

    @Before fun setup() {
        originalFolder = Settings.getPersistentLyricsFolder(context)
        context.contentResolver.call(provider, "clear", scope, null)
        instrumentation.context.grantUriPermission(context.packageName, tree,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
    }

    @After fun cleanup() {
        projects.forEach { ProjectStorage.deleteProject(context, it) }
        Settings.setPersistentLyricsFolder(context, originalFolder)
        runCatching { context.contentResolver.releasePersistableUriPermission(tree,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION) }
        instrumentation.context.revokeUriPermission(tree,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        context.contentResolver.call(provider, "clear", scope, null)
    }

    @Test fun connectRemembersFolderAndReconciliationRestoresDeletedWorkingCopy() {
        val project = ProjectStorage.createProject(context, "Test Persistent ${testName.methodName}")
        projects += project.name
        assertTrue(ProjectStorage.saveManual(project, "unused", "retained lyrics", 3))
        PersistentLyricsStorage.connect(context, tree)
        assertEquals(tree.toString(), Settings.getPersistentLyricsFolder(context))
        assertTrue(context.contentResolver.persistedUriPermissions.any { it.uri == tree && it.isReadPermission && it.isWritePermission })
        assertTrue(ProjectStorage.deleteProject(context, project.name))
        PersistentLyricsStorage.reconcile(context, tree)
        assertEquals("retained lyrics", ProjectStorage.loadLatest(ProjectStorage.projectDir(context, project.name)))
        assertTrue(PersistentLyricsStorage.delete(context, project.name))
        assertTrue(PersistentLyricsStorage.delete(context, project.name))
        assertTrue(project.exists())
        assertEquals(project.name, ProjectStorage.renameProject(context, ProjectStorage.loadMetadata(project, "").title, "Renamed"))
        assertNull(ProjectStorage.renameProject(context, "Missing ${testName.methodName}", "other"))
        assertThrows(IllegalArgumentException::class.java) { ProjectStorage.projectDir(context, "00000000-0000-4000-8000-000000000002") }
    }

    @Test fun missingAndDisconnectedFoldersReportSaveAndDeleteFailure() {
        val project = ProjectStorage.createProject(context, "Test Draft ${testName.methodName}")
        projects += project.name
        Settings.setPersistentLyricsFolder(context, null)
        assertFalse(PersistentLyricsStorage.save(context, project, 3))
        assertFalse(PersistentLyricsStorage.delete(context, project.name))
        Settings.setPersistentLyricsFolder(context, tree.toString())
        assertTrue(PersistentLyricsStorage.save(context, project, 3))
        context.contentResolver.call(provider, "failWrites", scope, Bundle().apply { putBoolean("enabled", true) })
        assertTrue(ProjectStorage.saveManual(project, "unused", "local draft", 3))
        assertFalse(PersistentLyricsStorage.save(context, project, 3))
        context.contentResolver.call(provider, "fault", scope, Bundle().apply { putString("kind", "delete") })
        assertFalse(PersistentLyricsStorage.delete(context, project.name))
        assertEquals("local draft", ProjectStorage.loadLatest(project))
    }

    @Test fun failedPreferenceCommitDoesNotReportAConnectedFolder() {
        val failingContext = object : ContextWrapper(context) {
            override fun getApplicationContext(): Context = this
            override fun getSharedPreferences(name: String, mode: Int): SharedPreferences {
                val preferences = context.getSharedPreferences(name, mode)
                return object : SharedPreferences by preferences {
                    override fun edit(): SharedPreferences.Editor {
                        val editor = preferences.edit()
                        return object : SharedPreferences.Editor by editor {
                            override fun putString(key: String?, value: String?): SharedPreferences.Editor = this
                            override fun commit(): Boolean = false
                        }
                    }
                }
            }
        }
        assertThrows(IOException::class.java) { PersistentLyricsStorage.connect(failingContext, tree) }
        assertEquals(originalFolder, Settings.getPersistentLyricsFolder(context))
    }
}
