package com.prosincerity.ghostwriter.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.ui.components.SettingsSection
import com.prosincerity.ghostwriter.ui.theme.GhostButtonShape

internal const val GHOSTWRITER_REPOSITORY_URL = "https://github.com/Prosincerity/Ghostwriter"
internal const val GHOSTWRITER_LICENSE_URL =
    "https://github.com/Prosincerity/Ghostwriter/blob/main/LICENSE"
internal const val DICTIONARY_REPOSITORY_URL =
    "https://github.com/Prosincerity/Ghostwriter-Dict"
internal const val DICTIONARY_DATA_LICENSE_URL =
    "https://github.com/Prosincerity/Ghostwriter-Dict/blob/main/LICENSE-DATA.md"
internal const val THIRD_PARTY_LICENSES_URL =
    "https://github.com/Prosincerity/Ghostwriter/blob/main/THIRD_PARTY_LICENSES.md"
internal const val KAIKKI_URL = "https://kaikki.org/dictionary/"
internal const val WIKTIONARY_COPYRIGHT_URL =
    "https://en.wiktionary.org/wiki/Wiktionary:Copyrights"
internal const val CC_BY_SA_URL = "https://creativecommons.org/licenses/by-sa/4.0/"
internal const val GFDL_URL = "https://www.gnu.org/licenses/fdl-1.3.html"
internal const val ESPEAK_SOURCE_URL = "https://github.com/espeak-ng/espeak-ng/tree/1.52.0"
internal const val ESPEAK_LICENSE_URL = "https://github.com/espeak-ng/espeak-ng/blob/1.52.0/COPYING"

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
                    IconButton(shape = GhostButtonShape, onClick = onBack) {
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
            SettingsSection("Ghostwriter") {
                Text(
                    "Songwriting Environment",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "A libre, open-source, no-bloat, no-AI Android app for writing rap lyrics.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "Version $versionName",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(top = 8.dp),
                )
            }

            Spacer(Modifier.height(24.dp))
            SettingsSection("Open source") {
                Text(
                    "Copyright © 2026 Ghostwriter contributors. Ghostwriter is released under " +
                        "the GNU General Public License v3.0 or later.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "Ghostwriter comes with no warranty, to the extent permitted by law. " +
                        "You may redistribute and modify it under the GNU General Public License " +
                        "v3.0 or later. See the license below for the full terms.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                AboutLink("Source code", GHOSTWRITER_REPOSITORY_URL, onOpenLink)
                AboutLink("GNU GPL v3.0 or later", GHOSTWRITER_LICENSE_URL, onOpenLink)
                AboutLink("Third-party licenses", THIRD_PARTY_LICENSES_URL, onOpenLink)
            }

            Spacer(Modifier.height(24.dp))
            SettingsSection("Dictionary attribution") {
                Text(
                    "The Wiktionary dictionary data was prepared from Kaikki.org's machine-readable " +
                        "Wiktionary extraction by Wiktextract. The separate eSpeak NG dictionary " +
                        "contains generated pronunciations. Both were prepared for Ghostwriter.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    "The dictionary databases are distributed under Creative Commons " +
                        "Attribution-ShareAlike 4.0 International. Wiktionary source material " +
                        "also has GNU Free Documentation License terms; see the links below.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                AboutLink("Dictionary source files", DICTIONARY_REPOSITORY_URL, onOpenLink)
                AboutLink("Dictionary data license and attribution", DICTIONARY_DATA_LICENSE_URL, onOpenLink)
                AboutLink("Kaikki.org data source", KAIKKI_URL, onOpenLink)
                AboutLink("Wiktionary copyright and licensing", WIKTIONARY_COPYRIGHT_URL, onOpenLink)
                AboutLink("CC BY-SA 4.0", CC_BY_SA_URL, onOpenLink)
                AboutLink("GNU Free Documentation License", GFDL_URL, onOpenLink)
            }

            Spacer(Modifier.height(24.dp))
            SettingsSection("Offline IPA generation") {
                Text(
                    "For words missing from both downloaded dictionaries, Ghostwriter uses " +
                        "eSpeak NG 1.52.0 locally to generate IPA. Its source is available under " +
                        "the GNU General Public License version 3 or later.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                AboutLink("eSpeak NG 1.52.0 source", ESPEAK_SOURCE_URL, onOpenLink)
                AboutLink("eSpeak NG license", ESPEAK_LICENSE_URL, onOpenLink)
            }

            Text(
                "Ghostwriter is not affiliated with or endorsed by Kaikki.org, Wiktionary, " +
                    "Wikimedia Foundation, or their contributors.",
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(top = 16.dp),
            )
        }
    }
}

@Composable
private fun AboutLink(
    label: String,
    url: String,
    onOpenLink: (String) -> Unit,
) {
    Surface(
        shape = MaterialTheme.shapes.medium,
        color = MaterialTheme.colorScheme.surfaceContainer,
        onClick = { onOpenLink(url) },
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Text(
                label,
                style = MaterialTheme.typography.bodyMedium,
                modifier = Modifier.weight(1f),
            )
            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
        }
    }
}
