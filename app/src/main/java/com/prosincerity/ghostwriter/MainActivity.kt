package com.prosincerity.ghostwriter

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import android.provider.DocumentsContract
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.hapticfeedback.HapticFeedbackType
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.core.net.toUri
import com.prosincerity.ghostwriter.data.PersistentLyricsStorage
import com.prosincerity.ghostwriter.data.Settings
import com.prosincerity.ghostwriter.data.ProjectStorage
import com.prosincerity.ghostwriter.data.ProjectSummary
import com.prosincerity.ghostwriter.media.BeatPlaybackService
import com.prosincerity.ghostwriter.ui.screens.AboutScreen
import com.prosincerity.ghostwriter.ui.screens.DictionaryDownloadsScreen
import com.prosincerity.ghostwriter.ui.screens.DictionaryScreen
import com.prosincerity.ghostwriter.ui.screens.EditorScreen
import com.prosincerity.ghostwriter.ui.screens.HomeScreen
import com.prosincerity.ghostwriter.ui.screens.SettingsScreen
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import java.io.File
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Navigation between the app's screens is a tiny hand-rolled sealed
 * class rather than the Navigation-Compose library — the current screen count
 * doesn't justify that dependency yet. Swap it in later if the screen
 * count grows.
 *
 * Known trade-off: this navigation state is NOT saved across a
 * configuration change (e.g. rotation). To avoid needing a custom
 * Saver for it, MainActivity is instead set to handle orientation
 * changes itself in the manifest, so the Activity — and this state —
 * isn't recreated on rotation in the first place.
 */
private sealed class Screen {
    data object Home : Screen()
    data class Editor(val projectId: String) : Screen()
    data class Settings(val returnTo: Screen) : Screen()
    data class About(val returnTo: Settings) : Screen()
    data class DictionaryDownloads(val returnTo: Screen) : Screen()
    data class Dictionary(val projectId: String) : Screen()
}

class MainActivity : ComponentActivity() {
    internal var externalIntentLauncher: (Intent) -> Unit = ::startActivity

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GhostwriterTheme {
                GhostwriterApp(
                    onOpenExternalLink = { context, url ->
                        openExternalLink(context, url, externalIntentLauncher)
                    },
                )
            }
        }
    }
}

@Composable
private fun GhostwriterApp(
    onOpenExternalLink: (Context, String) -> Boolean,
) {
    val context = LocalContext.current
    val haptics = LocalHapticFeedback.current
    val coroutineScope = rememberCoroutineScope()
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }
    var projects by remember { mutableStateOf(emptyList<String>()) }
    var projectSummaries by remember { mutableStateOf(emptyMap<String, ProjectSummary>()) }
    var storageReady by remember { mutableStateOf(false) }
    var storageLoading by remember { mutableStateOf(true) }
    var storageError by remember { mutableStateOf<String?>(null) }
    val versionName = remember(context) {
        runCatching {
            context.packageManager.getPackageInfo(context.packageName, 0).versionName
        }.getOrNull().orEmpty().ifBlank { "Unknown" }
    }

    suspend fun refreshProjects() {
        val (ids, summaries) = withContext(Dispatchers.IO) {
            val ids = ProjectStorage.listProjects(context)
            val root = ProjectStorage.rootDir(context)
            ids to ids.associateWith { ProjectSummary.fromDirectory(File(root, it)) }
        }
        projectSummaries = summaries
        projects = ids
    }

    LaunchedEffect(context) {
        try {
            val folder = Settings.getPersistentLyricsFolder(context)
            if (folder != null) {
                try {
                    withContext(Dispatchers.IO) { PersistentLyricsStorage.reconcile(context, Uri.parse(folder)) }
                    storageReady = true
                } catch (failure: Exception) {
                    if (failure is kotlinx.coroutines.CancellationException) throw failure
                    storageError = "Couldn't access your lyric folder. Select it again to reconnect. Local drafts have been kept."
                }
            }
            refreshProjects()
        } catch (failure: Exception) {
            if (failure is kotlinx.coroutines.CancellationException) throw failure
            storageError = "Couldn't access your lyric folder. Select it again to reconnect. Local drafts have been kept."
        } finally {
            storageLoading = false
        }
    }

    // This effect runs after the editor's disposal save, so row dates include
    // the last edit made before returning home.
    LaunchedEffect(screen) {
        if (screen == Screen.Home && storageReady) refreshProjects()
    }

    val folderPicker = rememberLauncherForActivityResult(ActivityResultContracts.OpenDocumentTree()) { uri ->
        if (uri != null) coroutineScope.launch {
            storageLoading = true
            storageError = null
            screen = Screen.Home
            try {
                withContext(Dispatchers.IO) { PersistentLyricsStorage.connect(context, uri) }
                refreshProjects()
                storageReady = true
            } catch (failure: Exception) {
                if (failure is kotlinx.coroutines.CancellationException) throw failure
                storageReady = false
                storageError = "Couldn't save to this folder. Choose a writable folder on your device. Local drafts have been kept."
            } finally {
                storageLoading = false
            }
        }
    }
    val chooseFolder = {
        val initial = Settings.getPersistentLyricsFolder(context)?.let(Uri::parse)
            ?: DocumentsContract.buildDocumentUri("com.android.externalstorage.documents", "primary:Documents")
        folderPicker.launch(initial)
    }
    val persistLyrics = remember(context) {
        { project: File, count: Int -> PersistentLyricsStorage.save(context, project, count) }
    }
    val storageFailure = {
        storageReady = false
        storageError = "The lyric folder couldn't be updated. Reconnect it before uninstalling. Local drafts have been kept."
    }

    when (val current = screen) {
        is Screen.Home -> HomeScreen(
            existingProjects = projects,
            projectSummaries = projectSummaries,
            projectTitles = projectSummaries.mapValues { it.value.title },
            onChooseLyricsFolder = chooseFolder,
            lyricsFolderReady = storageReady,
            storageLoading = storageLoading,
            storageError = storageError,
            onCreateProject = { title ->
                coroutineScope.launch {
                    runCatching {
                        val directory = withContext(Dispatchers.IO) {
                            val existing = projects.firstOrNull {
                                ProjectStorage.loadMetadata(File(ProjectStorage.rootDir(context), it), it)
                                    .title.equals(title, ignoreCase = true)
                            }
                            if (existing != null) File(ProjectStorage.rootDir(context), existing)
                            else ProjectStorage.createProject(context, title)
                        }
                        val saved = withContext(Dispatchers.IO) {
                            PersistentLyricsStorage.save(context, directory, Settings.getAutosaveCount(context))
                        }
                        refreshProjects()
                        if (saved) screen = Screen.Editor(directory.name) else storageFailure()
                    }.onFailure { failure ->
                        if (failure is kotlinx.coroutines.CancellationException) throw failure
                        withContext(Dispatchers.Main.immediate) {
                            Toast.makeText(context, "Couldn't create project", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            onOpenProject = { title -> screen = Screen.Editor(title) },
            onDeleteProject = { title ->
                BeatPlaybackService.forgetProject(context, title)
                coroutineScope.launch {
                    val deleted = withContext(Dispatchers.IO) {
                        PersistentLyricsStorage.delete(context, title) && ProjectStorage.deleteProject(context, title)
                    }
                    if (deleted) {
                        haptics.performHapticFeedback(HapticFeedbackType.LongPress)
                        refreshProjects()
                    } else {
                        withContext(Dispatchers.Main.immediate) {
                            Toast.makeText(context, "Couldn't delete ${projectSummaries[title]?.title ?: "project"}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            onRenameProject = { currentTitle, renamedTitle ->
                BeatPlaybackService.forgetProject(context, currentTitle)
                coroutineScope.launch {
                    val renamed = withContext(Dispatchers.IO) {
                        ProjectStorage.renameProject(context, currentTitle, renamedTitle)
                    }
                    if (renamed != null) {
                        val saved = withContext(Dispatchers.IO) {
                            PersistentLyricsStorage.save(context, File(ProjectStorage.rootDir(context), renamed),
                                Settings.getAutosaveCount(context))
                        }
                        refreshProjects()
                        if (!saved) storageFailure()
                    } else {
                        withContext(Dispatchers.Main.immediate) {
                            Toast.makeText(context, "Couldn't rename ${projectSummaries[currentTitle]?.title ?: "project"}", Toast.LENGTH_SHORT).show()
                        }
                    }
                }
            },
            onOpenSettings = { screen = Screen.Settings(returnTo = Screen.Home) },
        )

        is Screen.Editor -> EditorScreen(
            projectTitle = ProjectStorage.loadMetadata(
                File(ProjectStorage.rootDir(context), current.projectId), current.projectId,
            ).title,
            projectId = current.projectId,
            persistLyrics = persistLyrics,
            onStorageFailure = storageFailure,
            onBack = {
                screen = Screen.Home
            },
            onOpenSettings = { screen = Screen.Settings(returnTo = Screen.Editor(current.projectId)) },
            onOpenDictionary = { screen = Screen.Dictionary(current.projectId) },
        )

        is Screen.Dictionary -> DictionaryScreen(
            onBack = { screen = Screen.Editor(current.projectId) },
            onOpenDownloads = { screen = Screen.DictionaryDownloads(returnTo = current) },
        )

        is Screen.Settings -> SettingsScreen(
            onBack = { screen = current.returnTo },
            onOpenAbout = { screen = Screen.About(returnTo = current) },
            onOpenDictionaryDownloads = { screen = Screen.DictionaryDownloads(returnTo = current) },
            onChooseLyricsFolder = chooseFolder,
        )

        is Screen.DictionaryDownloads -> DictionaryDownloadsScreen(
            onBack = { screen = current.returnTo },
        )

        is Screen.About -> AboutScreen(
            versionName = versionName,
            onBack = { screen = current.returnTo },
            onOpenLink = { url -> onOpenExternalLink(context, url) },
        )
    }
}

internal fun openExternalLink(
    context: Context,
    url: String,
    launchIntent: (Intent) -> Unit = context::startActivity,
): Boolean = runCatching {
    launchIntent(Intent(Intent.ACTION_VIEW, url.toUri()))
}.fold(
    onSuccess = { true },
    onFailure = {
        Toast.makeText(context, "No app can open this link", Toast.LENGTH_SHORT).show()
        false
    },
)
