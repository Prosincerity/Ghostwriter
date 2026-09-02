package com.ghostwriter.exe

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.ghostwriter.exe.data.ProjectStorage
import com.ghostwriter.exe.ui.screens.EditorScreen
import com.ghostwriter.exe.ui.screens.HomeScreen
import com.ghostwriter.exe.ui.screens.SettingsScreen
import com.ghostwriter.exe.ui.theme.GhostwriterTheme

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
    data class SettingsFrom(val projectTitle: String) : Screen()
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
    var screen by remember { mutableStateOf<Screen>(Screen.Home) }
    var projects by remember { mutableStateOf(ProjectStorage.listProjects(context)) }

    when (val current = screen) {
        is Screen.Home -> HomeScreen(
            existingProjects = projects,
            onCreateProject = { title ->
                ProjectStorage.projectDir(context, title) // creates the folder immediately
                projects = ProjectStorage.listProjects(context)
                screen = Screen.Editor(title)
            },
            onOpenProject = { title -> screen = Screen.Editor(title) },
        )

        is Screen.Editor -> EditorScreen(
            projectTitle = current.projectTitle,
            onBack = { screen = Screen.Home },
            onOpenSettings = { screen = Screen.SettingsFrom(current.projectTitle) },
        )

        is Screen.SettingsFrom -> SettingsScreen(
            onBack = { screen = Screen.Editor(current.projectTitle) },
        )
    }
}
