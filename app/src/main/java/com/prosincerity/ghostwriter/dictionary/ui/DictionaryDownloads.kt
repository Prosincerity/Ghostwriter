package com.prosincerity.ghostwriter.dictionary.ui

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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Download
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.dictionary.data.DictionaryInstaller
import com.prosincerity.ghostwriter.dictionary.data.DictionarySource
import com.prosincerity.ghostwriter.dictionary.domain.DictionaryDownloadState
import com.prosincerity.ghostwriter.dictionary.domain.DictionaryDownloadSession
import com.prosincerity.ghostwriter.core.ui.SettingsSection
import com.prosincerity.ghostwriter.core.ui.theme.GhostButtonShape
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DictionaryDownloadsScreen(onBack: () -> Unit) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val installer = remember(context) { DictionaryInstaller(context) }
    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Rhyme dictionary downloads") },
            navigationIcon = {
                IconButton(shape = GhostButtonShape, onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )
    }) { padding ->
        DictionaryDownloads(
            installer = installer,
            modifier = Modifier
                .fillMaxWidth()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp, vertical = 12.dp),
        )
    }
}

@Composable
internal fun DictionaryDownloads(installer: DictionaryInstaller, modifier: Modifier = Modifier) {
    val scope = rememberCoroutineScope()
    val installed = remember(installer) {
        mutableStateMapOf<Pair<String, DictionarySource>, Boolean>()
    }
    var checkingDictionaries by remember(installer) { mutableStateOf(true) }
    LaunchedEffect(installer) {
        val status = withContext(Dispatchers.IO) {
            buildMap {
                for (language in installer.release.languages.keys) {
                    for (source in DictionarySource.entries) {
                        put(language to source, installer.availableDatabase(language, source) != null)
                    }
                }
            }
        }
        installed.putAll(status)
        checkingDictionaries = false
    }
    val downloadSession = remember(scope) { DictionaryDownloadSession(scope) }
    val downloadState by downloadSession.state.collectAsState()
    val active = downloadState as? DictionaryDownloadState.Downloading

    fun startDownload(language: String, source: DictionarySource) {
        val key = language to source
        downloadSession.start(key, onInstalled = { installed[key] = true }) { onProgress ->
            installer.install(language, source, onProgress)
        }
    }

    Column(modifier.fillMaxWidth()) {
        Text(
            "These pronunciation dictionaries help find rhymes. Download only the languages and sources you want; installed dictionaries work offline.",
            style = MaterialTheme.typography.bodyMedium,
        )
        Spacer(Modifier.height(24.dp))
        if (checkingDictionaries) {
            Text(
                "Checking installed dictionaries…",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.padding(bottom = 12.dp),
            )
        }
        for ((language, assets) in installer.release.languages) {
            val languageLabel = dictionaryLanguageLabel(language)
            SettingsSection(languageLabel) {
                for (source in DictionarySource.entries) {
                    val asset = assets.asset(source)
                    val key = language to source
                    val sourceLabel = downloadSourceLabel(source)
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                    ) {
                        Column(Modifier.weight(1f)) {
                            Text(sourceLabel, style = MaterialTheme.typography.bodyMedium)
                            Text(
                                if (installed[key] == true) "Installed · Available offline"
                                else "${(asset.sizeBytes + 999_999) / 1_000_000} MB download",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                        if (installed[key] == true) {
                            Icon(
                                Icons.Default.Check,
                                contentDescription = "$sourceLabel installed for $languageLabel",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(12.dp),
                            )
                        } else {
                            IconButton(
                                shape = GhostButtonShape,
                                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                                onClick = { startDownload(language, source) },
                                enabled = !checkingDictionaries && active == null,
                            ) {
                                Icon(Icons.Default.Download, contentDescription = "Download $sourceLabel for $languageLabel")
                            }
                        }
                    }
                    if (active?.key == key) {
                        val fraction = active.fraction
                        val percent = active.percent
                        if (fraction == null) {
                            LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
                        } else {
                            LinearProgressIndicator(progress = { fraction }, modifier = Modifier.fillMaxWidth())
                        }
                        Text(
                            "Downloading $sourceLabel${percent?.let { " $it%" } ?: "..."}",
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier.padding(top = 8.dp),
                        )
                        TextButton(shape = GhostButtonShape, onClick = downloadSession::cancel) {
                            Text("Cancel download")
                        }
                    }
                }
            }
            Spacer(Modifier.height(24.dp))
        }
        (downloadState as? DictionaryDownloadState.Failed)?.let { failure ->
            Text(
                "Could not download ${downloadSourceLabel(failure.key.second)} for " +
                    "${dictionaryLanguageLabel(failure.key.first)}: ${failure.message}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(bottom = 24.dp),
            )
        }
        SettingsSection("Dictionary sources") {
            Text(
                "Wiktionary Kaikki contains pronunciations from Wiktionary, extracted by Wiktextract and prepared for Ghostwriter. " +
                    "The separate eSpeak NG database contains pronunciations generated by the eSpeak NG speech engine.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.height(8.dp))
            Text(
                "Data sources, attribution, and licensing are listed in Settings → About Ghostwriter.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

private fun downloadSourceLabel(source: DictionarySource): String = when (source) {
    DictionarySource.WIKTIONARY -> "Wiktionary Kaikki"
    DictionarySource.ESPEAK -> "eSpeak NG generated"
}
