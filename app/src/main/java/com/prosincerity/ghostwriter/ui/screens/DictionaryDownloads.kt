package com.prosincerity.ghostwriter.ui.screens

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.data.DictionaryDownloadProgress
import com.prosincerity.ghostwriter.data.DictionaryInstaller
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
internal fun DictionaryDownloads() {
    val context = LocalContext.current
    val installer = remember(context) { DictionaryInstaller(context) }
    val scope = rememberCoroutineScope()
    val installed = remember(installer) {
        mutableStateMapOf<String, Boolean>().apply {
            for (language in installer.release.languages.keys) {
                this[language] = installer.installedDatabases(language) != null
            }
        }
    }
    var activeLanguage by remember { mutableStateOf<String?>(null) }
    var downloadJob by remember { mutableStateOf<Job?>(null) }
    var progress by remember { mutableStateOf<DictionaryDownloadProgress?>(null) }
    var error by remember { mutableStateOf<String?>(null) }
    val labels = mapOf("en" to "English", "de" to "German", "tr" to "Turkish")

    Column {
        Text("Rhyme dictionaries", style = MaterialTheme.typography.titleMedium)
        Text(
            "Download a language once to use its dictionary offline.",
            style = MaterialTheme.typography.bodySmall,
        )
        Spacer(Modifier.height(8.dp))
        for ((language, pair) in installer.release.languages) {
            val label = labels.getValue(language)
            val sizeMb = (pair.downloadBytes + 999_999) / 1_000_000
            val wikiMb = (pair.wiktionary.sizeBytes + 999_999) / 1_000_000
            val espeakMb = (pair.espeak.sizeBytes + 999_999) / 1_000_000
            OutlinedButton(
                onClick = {
                    activeLanguage = language
                    progress = null
                    error = null
                    downloadJob = scope.launch {
                        try {
                            installer.install(language) { value ->
                                scope.launch { progress = value }
                            }
                            installed[language] = true
                        } catch (cancelled: CancellationException) {
                            throw cancelled
                        } catch (failure: Exception) {
                            error = "Could not download $label: ${failure.message ?: "unknown error"}"
                        } finally {
                            activeLanguage = null
                            progress = null
                            downloadJob = null
                        }
                    }
                },
                enabled = activeLanguage == null && installed[language] != true,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(if (installed[language] == true) "$label installed" else "Download $label ($sizeMb MB)")
            }
            if (installed[language] != true) {
                Text("Wiktionary $wikiMb MB + eSpeak $espeakMb MB", style = MaterialTheme.typography.bodySmall)
            }
            if (activeLanguage == language) {
                val percent = progress?.let { (100 * it.downloadedBytes / it.totalBytes).toInt() }
                Text("Downloading $label${percent?.let { " $it%" } ?: "..."}")
                OutlinedButton(onClick = { downloadJob?.cancel() }) { Text("Cancel download") }
            }
        }
        if (error != null) {
            Text(error!!, color = MaterialTheme.colorScheme.error)
        }
    }
}
