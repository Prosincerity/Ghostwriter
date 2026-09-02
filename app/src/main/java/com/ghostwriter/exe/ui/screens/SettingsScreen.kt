package com.ghostwriter.exe.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.ghostwriter.exe.data.Settings as AppSettings

/**
 * Two settings for now: autosave interval and how many rolling backups
 * to keep. Uses a plain Box + DropdownMenu rather than Material3's
 * ExposedDropdownMenuBox — that API has changed shape across library
 * versions, while basic DropdownMenu has stayed stable, so this is the
 * safer bet against version drift.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current

    var intervalSeconds by remember { mutableIntStateOf(AppSettings.getAutosaveIntervalSeconds(context)) }
    var autosaveCount by remember { mutableIntStateOf(AppSettings.getAutosaveCount(context)) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
            )
        },
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(24.dp),
        ) {
            Text("Autosave interval", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            LabeledDropdown(
                options = AppSettings.INTERVAL_OPTIONS_SECONDS,
                selected = intervalSeconds,
                label = ::formatInterval,
                onSelect = {
                    intervalSeconds = it
                    AppSettings.setAutosaveIntervalSeconds(context, it)
                },
            )

            Spacer(Modifier.height(32.dp))

            Text("Number of autosave backups", style = MaterialTheme.typography.titleLarge)
            Spacer(Modifier.height(8.dp))
            LabeledDropdown(
                options = AppSettings.COUNT_OPTIONS,
                selected = autosaveCount,
                label = { it.toString() },
                onSelect = {
                    autosaveCount = it
                    AppSettings.setAutosaveCount(context, it)
                },
            )
        }
    }
}

@Composable
private fun LabeledDropdown(
    options: List<Int>,
    selected: Int,
    label: (Int) -> String,
    onSelect: (Int) -> Unit,
) {
    var expanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.fillMaxWidth()) {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(label(selected))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(label(option)) },
                    onClick = {
                        onSelect(option)
                        expanded = false
                    },
                )
            }
        }
    }
}

internal fun formatInterval(seconds: Int): String = when {
    seconds < 60 -> "$seconds seconds"
    seconds % 60 == 0 -> "${seconds / 60} minute" + if (seconds / 60 > 1) "s" else ""
    else -> "$seconds seconds"
}
