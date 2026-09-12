package com.prosincerity.ghostwriter.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

internal const val GHOSTWRITER_REPOSITORY_URL = "https://github.com/Prosincerity/Ghostwriter"
internal const val GHOSTWRITER_LICENSE_URL =
    "https://github.com/Prosincerity/Ghostwriter/blob/main/LICENSE"
internal const val DICTIONARY_REPOSITORY_URL =
    "https://github.com/Prosincerity/Ghostwriter-Dict"
internal const val KAIKKI_URL = "https://kaikki.org/dictionary/"
internal const val WIKTIONARY_COPYRIGHT_URL =
    "https://en.wiktionary.org/wiki/Wiktionary:Copyrights"
internal const val CC_BY_SA_URL = "https://creativecommons.org/licenses/by-sa/4.0/"
internal const val GFDL_URL = "https://www.gnu.org/licenses/fdl-1.3.html"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    versionName: String,
    onBack: () -> Unit,
    onOpenLink: (String) -> Unit,
) {
    BackHandler(onBack = onBack)

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("About") },
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
                .padding(horizontal = 16.dp, vertical = 12.dp),
        ) {
            Text("Ghostwriter", style = MaterialTheme.typography.headlineMedium)
            Text(
                "Songwriting Environment",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "A free, open-source, no-bloat, no-AI Android app for writing rap lyrics.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                "Version $versionName",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )

            AboutSectionTitle("Open source")
            Text(
                "Copyright © 2026 Ghostwriter contributors. Ghostwriter is released under " +
                    "the MIT License.",
                style = MaterialTheme.typography.bodyMedium,
            )
            AboutLink("Source code", GHOSTWRITER_REPOSITORY_URL, onOpenLink)
            AboutLink("MIT License", GHOSTWRITER_LICENSE_URL, onOpenLink)

            AboutSectionTitle("Dictionary attribution")
            Text(
                "Dictionary datasets prepared for Ghostwriter are derived from Kaikki.org's " +
                    "machine-readable dictionaries, extracted from Wiktionary with Wiktextract, " +
                    "and modified for this project.",
                style = MaterialTheme.typography.bodyMedium,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "The source data is available under the same licenses as Wiktionary: Creative " +
                    "Commons Attribution-ShareAlike 4.0 International and the GNU Free " +
                    "Documentation License 1.1 or later.",
                style = MaterialTheme.typography.bodyMedium,
            )
            AboutLink("Dictionary source files", DICTIONARY_REPOSITORY_URL, onOpenLink)
            AboutLink("Kaikki.org data source", KAIKKI_URL, onOpenLink)
            AboutLink("Wiktionary copyright and licensing", WIKTIONARY_COPYRIGHT_URL, onOpenLink)
            AboutLink("CC BY-SA 4.0", CC_BY_SA_URL, onOpenLink)
            AboutLink("GNU Free Documentation License", GFDL_URL, onOpenLink)

            Text(
                "Ghostwriter is not affiliated with or endorsed by Kaikki.org, Wiktionary, " +
                    "Wikimedia Foundation, or their contributors.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 8.dp),
            )
        }
    }
}

@Composable
private fun AboutSectionTitle(title: String) {
    Spacer(Modifier.height(24.dp))
    HorizontalDivider()
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(top = 16.dp, bottom = 8.dp),
    )
}

@Composable
private fun AboutLink(
    label: String,
    url: String,
    onOpenLink: (String) -> Unit,
) {
    TextButton(
        onClick = { onOpenLink(url) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Text(label, modifier = Modifier.fillMaxWidth())
    }
}
