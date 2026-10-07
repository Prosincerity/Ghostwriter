package com.prosincerity.ghostwriter

import android.app.Activity
import android.app.Instrumentation
import android.content.IntentFilter
import android.net.Uri
import android.provider.DocumentsContract
import android.os.Bundle
import java.io.File
import com.prosincerity.ghostwriter.data.LyricArchiveTestProvider
import org.junit.Before
import org.junit.After
import android.content.Intent
import android.graphics.drawable.ColorDrawable
import android.util.TypedValue
import androidx.compose.ui.semantics.SemanticsActions
import androidx.compose.ui.test.hasContentDescription
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasSetTextAction
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onNodeWithContentDescription
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import androidx.compose.ui.test.performSemanticsAction
import androidx.compose.ui.test.performTextInput
import androidx.compose.ui.test.performTextReplacement
import androidx.compose.ui.text.TextLayoutResult
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.toArgb
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.prosincerity.ghostwriter.data.LyricTextSettings
import com.prosincerity.ghostwriter.data.ProjectStorage
import com.prosincerity.ghostwriter.data.PersistentLyricsStorage
import com.prosincerity.ghostwriter.data.Settings
import com.prosincerity.ghostwriter.ui.screens.GHOSTWRITER_REPOSITORY_URL
import com.prosincerity.ghostwriter.ui.theme.GhostColorScheme
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestName
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class MainActivityTest {
    @get:Rule val testName = TestName()

    @get:Rule
    val composeRule = createAndroidComposeRule<MainActivity>()

    private var originalFolder: String? = null
    private val archiveScope get() = "main-activity-${testName.methodName}"

    @Before fun useIsolatedLyricFolder() {
        originalFolder = Settings.getPersistentLyricsFolder(composeRule.activity)
        composeRule.activity.contentResolver.call(Uri.parse("content://${LyricArchiveTestProvider.AUTHORITY}"),
            "clear", archiveScope, null)
        val tree = DocumentsContract.buildTreeDocumentUri(LyricArchiveTestProvider.AUTHORITY, archiveScope)
        Settings.setPersistentLyricsFolder(composeRule.activity, tree.toString())
        composeRule.activityRule.scenario.recreate()
        waitUntilTextDoesNotExist("Preparing your projects…")
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithText("Choose folder").fetchSemanticsNodes().isEmpty()
        }
        composeRule.onNodeWithText("New project").assertIsEnabled()
    }

    @After fun restoreLyricFolder() {
        val context = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation().targetContext
        composeRule.activityRule.scenario.close()
        Settings.setPersistentLyricsFolder(context, originalFolder)
        context.contentResolver.call(Uri.parse("content://${LyricArchiveTestProvider.AUTHORITY}"), "clear", archiveScope, null)
    }

    @Test
    fun missingFolder_opensHomeAndPickerCancellationKeepsHome() {
        Settings.setPersistentLyricsFolder(composeRule.activity, null)
        composeRule.activityRule.scenario.recreate()
        waitUntilTextExists("Choose a lyric folder")
        waitUntilTextDoesNotExist("Preparing your projects…")
        composeRule.onNodeWithText("New project").assertIsNotEnabled()
        composeRule.onNodeWithText("Keep your lyrics").assertDoesNotExist()
        composeRule.onNodeWithText("Choose folder").assertIsEnabled()

        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        val monitor = instrumentation.addMonitor(
            IntentFilter(Intent.ACTION_OPEN_DOCUMENT_TREE),
            Instrumentation.ActivityResult(Activity.RESULT_CANCELED, null), true,
        )
        try {
            composeRule.onNodeWithText("Choose folder").performClick()
            composeRule.runOnIdle { assertEquals(1, monitor.hits) }
            composeRule.onNodeWithText("Choose folder").assertIsEnabled()
            composeRule.onNodeWithText("New project").assertIsNotEnabled()
        } finally {
            instrumentation.removeMonitor(monitor)
        }
        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.onNodeWithText("Choose lyric folder").assertExists()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Choose folder").assertExists()
    }

    @Test
    fun folderPicker_rejectsInaccessibleFolderThenConnectsGrantedFolder() {
        Settings.setPersistentLyricsFolder(composeRule.activity, null)
        composeRule.activityRule.scenario.recreate()
        waitUntilTextExists("Choose folder")
        returnFolderFromPicker(Uri.parse("content://missing.ghostwriter.provider/tree/lyrics"))
        waitUntilTextExists("Couldn't save to this folder. Choose a writable folder on your device. Local drafts have been kept.")
        composeRule.onNodeWithText("New project").assertIsNotEnabled()

        val tree = DocumentsContract.buildTreeDocumentUri(LyricArchiveTestProvider.AUTHORITY, archiveScope)
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        instrumentation.context.grantUriPermission(composeRule.activity.packageName, tree,
            Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION or
                Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION or Intent.FLAG_GRANT_PREFIX_URI_PERMISSION)
        try {
            returnFolderFromPicker(tree)
            waitUntilTextDoesNotExist("Choose folder")
            composeRule.onNodeWithText("New project").assertIsEnabled()
            assertEquals(tree.toString(), Settings.getPersistentLyricsFolder(composeRule.activity))
            composeRule.onNodeWithContentDescription("Settings").performClick()
            returnFolderFromPicker(tree, "Choose lyric folder")
            waitUntilTextExists("New project")
            composeRule.onNodeWithText("New project").assertIsEnabled()
        } finally {
            composeRule.activity.contentResolver.releasePersistableUriPermission(tree,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            instrumentation.context.revokeUriPermission(tree,
                Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
        }
    }

    @Test
    fun duplicateProjectName_opensExistingLyricsWithoutCreatingAnotherProject() {
        val title = uniqueProjectTitle("Existing")
        val project = ProjectStorage.createProject(composeRule.activity, title)
        assertTrue(ProjectStorage.saveManual(project, title, "original lyrics", 3))
        try {
            composeRule.activityRule.scenario.recreate()
            waitUntilTextExists(title)
            composeRule.onNodeWithText("New project").performClick()
            composeRule.onNode(hasSetTextAction()).performTextInput(title.lowercase())
            composeRule.onNodeWithText("Open").performClick()
            waitUntilTextExists("original lyrics")
            assertEquals(1, ProjectStorage.listProjects(composeRule.activity).count {
                ProjectStorage.loadMetadata(ProjectStorage.projectDir(composeRule.activity, it), "").title == title
            })
            composeRule.onNodeWithContentDescription("Back").performClick()
        } finally {
            ProjectStorage.deleteProject(composeRule.activity, project.name)
        }
    }

    @Test
    fun sharedSaveFailures_keepCreatedAndRenamedLocalProjectsOnHome() {
        val title = uniqueProjectTitle("Unarchived")
        val renamed = "$title renamed"
        val context = composeRule.activity
        try {
            setArchiveFault("createSnapshot")
            composeRule.onNodeWithText("New project").performClick()
            composeRule.onNode(hasSetTextAction()).performTextInput(title)
            composeRule.onNodeWithText("Create").performClick()
            waitUntilTextExists("The lyric folder couldn't be updated. Reconnect it before uninstalling. Local drafts have been kept.")
            composeRule.onNodeWithText(title).assertExists()
            composeRule.onNodeWithText("New project").assertIsNotEnabled()
            setArchiveFault(null)
            composeRule.activityRule.scenario.recreate()
            waitUntilTextDoesNotExist("Choose folder")
            setArchiveFault("createSnapshot")
            composeRule.onNodeWithContentDescription("Project options for $title").performClick()
            composeRule.onNodeWithText("Rename").performClick()
            composeRule.onNode(hasSetTextAction()).performTextReplacement(renamed)
            composeRule.onNodeWithText("Rename").performClick()
            waitUntilTextExists(renamed)
            waitUntilTextExists("The lyric folder couldn't be updated. Reconnect it before uninstalling. Local drafts have been kept.")
            setArchiveFault(null)
            composeRule.activityRule.scenario.recreate()
            waitUntilTextDoesNotExist("Choose folder")
            setArchiveFault("delete")
            composeRule.onNodeWithContentDescription("Project options for $renamed").performClick()
            composeRule.onNodeWithText("Delete").performClick()
            composeRule.onNodeWithText("Delete").performClick()
            waitUntilTextDoesNotExist("Delete project?")
            composeRule.onNodeWithText(renamed).assertExists()
            assertTrue(ProjectStorage.projectDir(context, renamed).isDirectory)
        } finally {
            setArchiveFault(null)
            ProjectStorage.deleteProject(context, title)
            ProjectStorage.deleteProject(context, renamed)
        }
    }

    private fun setArchiveFault(kind: String?) {
        composeRule.activity.contentResolver.call(Uri.parse("content://${LyricArchiveTestProvider.AUTHORITY}"),
            "fault", archiveScope, kind?.let { Bundle().apply { putString("kind", it) } })
    }

    private fun returnFolderFromPicker(uri: Uri, button: String = "Choose folder") {
        val instrumentation = androidx.test.platform.app.InstrumentationRegistry.getInstrumentation()
        val monitor = instrumentation.addMonitor(IntentFilter(Intent.ACTION_OPEN_DOCUMENT_TREE),
            Instrumentation.ActivityResult(Activity.RESULT_OK, Intent().setData(uri)), true)
        try {
            composeRule.onNodeWithText(button).performClick()
            composeRule.runOnIdle { assertEquals(1, monitor.hits) }
        } finally {
            instrumentation.removeMonitor(monitor)
        }
    }

    @Test
    fun inaccessibleFolder_keepsLocalProjectsVisibleOnHome() {
        val context = composeRule.activity
        val project = ProjectStorage.createProject(context, uniqueProjectTitle("Local draft"))
        val title = ProjectStorage.loadMetadata(project, project.name).title
        try {
            Settings.setPersistentLyricsFolder(context, "content://missing.ghostwriter.provider/tree/lyrics")
            composeRule.activityRule.scenario.recreate()
            waitUntilTextExists("Couldn't access your lyric folder. Select it again to reconnect. Local drafts have been kept.")
            waitUntilTextDoesNotExist("Preparing your projects…")
            composeRule.onNodeWithText(title).assertExists()
            composeRule.onNodeWithText("New project").assertIsNotEnabled()
            composeRule.onNodeWithText("Choose folder").assertIsEnabled()
            assertTrue(project.exists())
        } finally {
            ProjectStorage.deleteProject(context, project.name)
        }
    }

    @Test
    fun unreadableLegacyProject_keepsHomeAvailableAndReportsStorageError() {
        val context = composeRule.activity
        val legacy = File(ProjectStorage.rootDir(context), uniqueProjectTitle("Unreadable legacy"))
        assertTrue(File(legacy, "project.json").mkdirs())
        File(legacy, "project.json/keep").writeText("preserve")
        try {
            composeRule.activityRule.scenario.recreate()
            waitUntilTextExists("Couldn't access your lyric folder. Select it again to reconnect. Local drafts have been kept.")
            waitUntilTextDoesNotExist("Preparing your projects…")
            composeRule.onNodeWithText("New project").assertIsNotEnabled()
            composeRule.onNodeWithText("Choose folder").assertIsEnabled()
            assertEquals("preserve", File(legacy, "project.json/keep").readText())
        } finally {
            legacy.deleteRecursively()
        }
    }

    @Test
    fun activityWindow_usesStudioBackground() {
        composeRule.runOnIdle {
            val theme = composeRule.activity.theme
            val background = TypedValue()
            assertTrue(theme.resolveAttribute(android.R.attr.windowBackground, background, true))
            val drawable = composeRule.activity.getDrawable(background.resourceId) as ColorDrawable
            assertEquals(GhostColorScheme.background.toArgb(), drawable.color)
        }
    }

    @Test
    fun returningFromSettingsAndDictionary_keepsLatestLyrics() {
        val title = uniqueProjectTitle("Editor round trips")
        try {
            createProject(title)
            for ((visit, destination) in listOf("Settings", "Dictionary", "Settings", "Dictionary").withIndex()) {
                val lyrics = "Visit $visit: latest lyrics before $destination"
                composeRule.onNode(hasSetTextAction()).performTextReplacement(lyrics)
                composeRule.onNodeWithContentDescription(destination).performClick()
                composeRule.onNodeWithContentDescription("Back").performClick()
                waitUntilTextExists(title)
                composeRule.onNodeWithText(lyrics).assertExists()
            }
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitUntilTextExists(title)
        } finally {
            ProjectStorage.deleteProject(composeRule.activity, title)
        }
    }

    @Test
    fun settingsDestinations_returnThroughSettingsToHome() {
        composeRule.onNodeWithText("New project").assertExists()
        composeRule.onNodeWithText("Choose folder").assertDoesNotExist()
        composeRule.onNodeWithText("Change folder").assertDoesNotExist()

        composeRule.onNodeWithContentDescription("Settings").performClick()
        composeRule.onNodeWithText("Choose lyric folder").assertExists()
        composeRule.onNodeWithText("Settings").assertExists()

        composeRule.onNodeWithText("Download Dictionaries").performScrollTo().performClick()
        composeRule.onNodeWithText("Data sources, attribution, and licensing are listed in Settings → About Ghostwriter.").assertExists()
        composeRule.onNode(
            hasContentDescription("Download Wiktionary Kaikki for English") or
                hasContentDescription("Wiktionary Kaikki installed for English"),
        ).assertExists()
        composeRule.onNode(
            hasContentDescription("Download eSpeak NG generated for English") or
                hasContentDescription("eSpeak NG generated installed for English"),
        ).assertExists()
        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("About Ghostwriter").assertExists()

        composeRule.onNodeWithText("About Ghostwriter").performScrollTo().performClick()
        composeRule.onNodeWithText("About").assertExists()

        var launchedIntent: Intent? = null
        composeRule.runOnIdle {
            composeRule.activity.externalIntentLauncher = { intent -> launchedIntent = intent }
        }
        composeRule.onNodeWithText("Source code").performClick()
        composeRule.runOnIdle {
            assertEquals(Intent.ACTION_VIEW, launchedIntent?.action)
            assertEquals(GHOSTWRITER_REPOSITORY_URL, launchedIntent?.dataString)
        }

        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("Settings").assertExists()

        composeRule.onNodeWithContentDescription("Back").performClick()
        composeRule.onNodeWithText("New project").assertExists()
    }

    @Test
    fun changingTypographyInSettings_appliesWhenReturningToExistingLyrics() {
        val projectTitle = uniqueProjectTitle("Typography")
        val originalSettings = Settings.getLyricTextSettings(composeRule.activity)
        try {
            Settings.setLyricTextSettings(composeRule.activity, LyricTextSettings())
            createProject(projectTitle)
            composeRule.onNode(hasSetTextAction()).performTextInput("Keep these lyrics")
            composeRule.onNodeWithContentDescription("Settings").performClick()
            composeRule.onNodeWithContentDescription("Font family").performScrollTo().performClick()
            composeRule.onNodeWithText("System default").performClick()
            composeRule.onNodeWithContentDescription("Font size").performScrollTo().performClick()
            composeRule.onNodeWithText("24 sp").performClick()
            composeRule.onNodeWithContentDescription("Text alignment").performScrollTo().performClick()
            composeRule.onNodeWithText("Center").performClick()
            composeRule.onNodeWithContentDescription("Back").performClick()

            composeRule.onNodeWithText("Keep these lyrics").assertExists()
            val results = mutableListOf<TextLayoutResult>()
            composeRule.onNode(hasSetTextAction()).performSemanticsAction(SemanticsActions.GetTextLayoutResult) {
                it(results)
            }
            val style = results.single().layoutInput.style
            assertEquals(FontFamily.Default, style.fontFamily)
            assertEquals(24.sp, style.fontSize)
            assertEquals(TextAlign.Center, style.textAlign)
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitUntilTextExists(projectTitle)
        } finally {
            Settings.setLyricTextSettings(composeRule.activity, originalSettings)
            ProjectStorage.deleteProject(composeRule.activity, projectTitle)
        }
    }

    @Test
    fun projectLifecycle_createReopenRenameAndDelete() {
        val projectTitle = uniqueProjectTitle("Main activity project")
        val renamedTitle = "$projectTitle renamed"

        try {
            createProject(projectTitle)
            composeRule.onNodeWithText(projectTitle).assertExists()

            composeRule.onNodeWithContentDescription("Back").performClick()
            waitUntilTextExists(projectTitle)
            composeRule.onNodeWithText("Recent").assertExists()
            composeRule.onNodeWithText(projectTitle).performClick()
            composeRule.onNodeWithText(projectTitle).assertExists()
            composeRule.onNodeWithContentDescription("Back").performClick()

            composeRule.onNodeWithContentDescription("Project options for $projectTitle").performClick()
            composeRule.onNodeWithText("Rename").performClick()
            composeRule.onNodeWithText("Rename track").assertExists()
            composeRule.onNode(hasSetTextAction()).performTextReplacement(renamedTitle)
            composeRule.onNodeWithText("Rename").performClick()
            waitUntilTextExists(renamedTitle)

            composeRule.onNodeWithContentDescription("Project options for $renamedTitle").performClick()
            composeRule.onNodeWithText("Delete").performClick()
            composeRule.onNodeWithText("Delete project?").assertExists()
            composeRule.onNodeWithText("Delete").performClick()
            waitUntilTextDoesNotExist(renamedTitle)

            assertFalse(
                ProjectStorage.listProjects(composeRule.activity).any { id ->
                    ProjectStorage.loadMetadata(ProjectStorage.projectDir(composeRule.activity, id), "").title == renamedTitle
                },
            )
        } finally {
            ProjectStorage.deleteProject(composeRule.activity, projectTitle)
            ProjectStorage.deleteProject(composeRule.activity, renamedTitle)
        }
    }

    @Test
    fun renameFailure_keepsOriginalProjectVisible() {
        val projectTitle = uniqueProjectTitle("Main activity rename failure")
        val renamedTitle = "$projectTitle renamed"

        try {
            createProject(projectTitle)
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitUntilTextExists(projectTitle)

            composeRule.onNodeWithContentDescription("Project options for $projectTitle").performClick()
            composeRule.onNodeWithText("Rename").performClick()
            composeRule.onNode(hasSetTextAction()).performTextReplacement(renamedTitle)
            assertTrue(ProjectStorage.deleteProject(composeRule.activity, projectTitle))
            composeRule.onNodeWithText("Rename").performClick()

            waitUntilTextDoesNotExist("Rename track")
            composeRule.onNodeWithText(projectTitle).assertExists()
        } finally {
            ProjectStorage.deleteProject(composeRule.activity, projectTitle)
            ProjectStorage.deleteProject(composeRule.activity, renamedTitle)
        }
    }

    @Test
    fun deleteFailure_keepsProjectVisible() {
        val projectTitle = uniqueProjectTitle("Main activity delete failure")

        try {
            createProject(projectTitle)
            composeRule.onNodeWithContentDescription("Back").performClick()
            waitUntilTextExists(projectTitle)

            composeRule.onNodeWithContentDescription("Project options for $projectTitle").performClick()
            composeRule.onNodeWithText("Delete").performClick()
            assertTrue(ProjectStorage.deleteProject(composeRule.activity, projectTitle))
            composeRule.onNodeWithText("Delete").performClick()

            waitUntilTextDoesNotExist("Delete project?")
            composeRule.onNodeWithText(projectTitle).assertExists()
        } finally {
            ProjectStorage.deleteProject(composeRule.activity, projectTitle)
        }
    }

    private fun createProject(projectTitle: String) {
        composeRule.onNodeWithText("New project").performClick()
        composeRule.onNodeWithText("Name this track").assertExists()
        composeRule.onNode(hasSetTextAction()).performTextInput(projectTitle)
        composeRule.onNodeWithText("Create").performClick()
        waitUntilTextExists(projectTitle)
    }

    private fun waitUntilTextExists(text: String) {
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isNotEmpty()
        }
    }

    private fun waitUntilTextDoesNotExist(text: String) {
        composeRule.waitUntil(timeoutMillis = 15_000) {
            composeRule.onAllNodesWithText(text).fetchSemanticsNodes().isEmpty()
        }
    }

    private fun uniqueProjectTitle(prefix: String): String =
        "Test $prefix ${testName.methodName}"
}
