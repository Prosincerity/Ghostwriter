package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.ui.theme.GhostButtonShape

/** The shared folder grant is required before editing so saves can survive uninstall. */
@Composable
internal fun LyricsFolderScreen(
    loading: Boolean,
    error: String?,
    onChooseFolder: () -> Unit,
    onOpenSettings: () -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxSize().windowInsetsPadding(WindowInsets.safeDrawing).verticalScroll(rememberScrollState()).padding(horizontal = 24.dp, vertical = 48.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically),
    ) {
        Text("Keep your lyrics", style = MaterialTheme.typography.headlineMedium)
        Text("Choose Documents or a Ghostwriter folder on your device. Lyrics will be saved there automatically and remain after uninstall. After reinstall, select the same folder to restore your projects.")
        Text("Existing lyrics will be copied there. Beat files stay in app storage.")
        error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
        if (loading) {
            CircularProgressIndicator()
            Text("Preparing your projects…")
        } else {
            Button(shape = GhostButtonShape, onClick = onChooseFolder) { Text("Choose lyric folder") }
            TextButton(shape = GhostButtonShape, onClick = onOpenSettings) { Text("Settings") }
        }
    }
}
