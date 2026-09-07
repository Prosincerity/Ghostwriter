package com.prosincerity.ghostwriter

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.prosincerity.ghostwriter.data.ProjectStorage
import com.prosincerity.ghostwriter.ui.screens.EditorScreen
import com.prosincerity.ghostwriter.ui.screens.HomeScreen
import com.prosincerity.ghostwriter.ui.screens.SettingsScreen
import com.prosincerity.ghostwriter.ui.theme.GhostwriterTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * Navigation between the three screens is a tiny hand-rolled sealed
 * class rather than the Navigation-Compose library — three screens
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
    data class Editor(val projectTitle: String) : Screen()
    data class Settings(val returnTo: Screen) : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            GhostwriterTheme {
                GhostwriterApp()
            }
        }
    }
}

@Composable
private fun GhostwriterApp() {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }
    var projects by remember { mutableStateOf(ProjectStorage.listProjects(context)) }

    when (val current = screen) {
        is Screen.Home -> HomeScreen(
            existingProjects = projects,
            onCreateProject = { title ->
                val cleanTitle = ProjectStorage.resolveProjectTitle(title, projects)
                ProjectStorage.projectDir(context, cleanTitle) // creates the folder immediately
                projects = ProjectStorage.listProjects(context)
                screen = Screen.Editor(cleanTitle)
            },
            onOpenProject = { title -> screen = Screen.Editor(title) },
            onDeleteProject = { title ->
                coroutineScope.launch {
                    val deleted = withContext(Dispatchers.IO) {
                        ProjectStorage.deleteProject(context, title)
                    }
                    if (deleted) {
                        projects = withContext(Dispatchers.IO) {
                            ProjectStorage.listProjects(context)
                        }
                    } else {
                        Toast.makeText(context, "Couldn't delete $title", Toast.LENGTH_SHORT).show()
                    }
                }
            },
            onOpenSettings = { screen = Screen.Settings(returnTo = Screen.Home) },
        )

        is Screen.Editor -> EditorScreen(
            projectTitle = current.projectTitle,
            onBack = {
                projects = ProjectStorage.listProjects(context)
                screen = Screen.Home
            },
            onOpenSettings = { screen = Screen.Settings(returnTo = Screen.Editor(current.projectTitle)) },
        )

        is Screen.Settings -> SettingsScreen(
            onBack = { screen = current.returnTo },
        )
    }
}
