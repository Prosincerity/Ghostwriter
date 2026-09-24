package com.prosincerity.ghostwriter.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.data.DictionaryInstaller
import com.prosincerity.ghostwriter.data.DictionarySearch
import com.prosincerity.ghostwriter.data.DictionarySearchMode
import com.prosincerity.ghostwriter.data.DictionarySearchResult
import com.prosincerity.ghostwriter.data.PronunciationSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DictionaryScreen(
    onBack: () -> Unit,
    onOpenDownloads: () -> Unit,
    search: (suspend (String, String, DictionarySearchMode) -> DictionarySearchResult)? = null,
    installedLanguages: List<String>? = null,
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val installer = remember(context) { DictionaryInstaller(context) }
    val repository = remember(installer) { DictionarySearch(installer) }
    val availableLanguages = installedLanguages ?: remember(installer) { installer.availableLanguages() }
    val searchAction = search ?: repository::search
    val scope = rememberCoroutineScope()
    var word by rememberSaveable { mutableStateOf("") }
    var selectedLanguage by rememberSaveable { mutableStateOf("en") }
    val language = selectedLanguage.takeIf { it in availableLanguages } ?: availableLanguages.firstOrNull().orEmpty()
    var modeName by rememberSaveable { mutableStateOf(DictionarySearchMode.RHYME.name) }
    val mode = DictionarySearchMode.valueOf(modeName)
    var languageMenu by remember { mutableStateOf(false) }
    var modeMenu by remember { mutableStateOf(false) }
    var result by remember { mutableStateOf<DictionarySearchResult?>(null) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var job by remember { mutableStateOf<Job?>(null) }
    var revision by remember { mutableIntStateOf(0) }

    fun submit() {
        val input = word.trim()
        if (input.isEmpty() || language !in availableLanguages) return
        job?.cancel()
        val requested = ++revision
        loading = true
        error = null
        result = null
        job = scope.launch {
            try {
                val found = searchAction(input, language, mode)
                if (requested == revision) result = found
            } catch (cancelled: CancellationException) {
                throw cancelled
            } catch (failure: Exception) {
                if (requested == revision) error = failure.message ?: "Dictionary search failed"
            } finally {
                if (requested == revision) loading = false
            }
        }
    }

    Scaffold(topBar = {
        TopAppBar(
            title = { Text("Dictionary") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            if (availableLanguages.isEmpty()) {
                Text("No rhyme dictionaries are installed")
                Spacer(Modifier.height(8.dp))
                Text("Download at least one language to search for matching words.")
                Spacer(Modifier.height(12.dp))
                Button(onClick = onOpenDownloads) { Text("Download dictionaries") }
            } else {
                OutlinedTextField(
                    value = word,
                    onValueChange = { word = it; job?.cancel(); revision++; loading = false; result = null },
                    label = { Text("Word") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box {
                        TextButton(onClick = { languageMenu = true }) { Text("Language: ${languageLabel(language)}") }
                        DropdownMenu(expanded = languageMenu, onDismissRequest = { languageMenu = false }) {
                            availableLanguages.forEach { code ->
                                DropdownMenuItem(
                                    text = { Text(languageLabel(code)) },
                                    onClick = { selectedLanguage = code; languageMenu = false; job?.cancel(); revision++; loading = false; result = null },
                                )
                            }
                        }
                    }
                    Box {
                        TextButton(onClick = { modeMenu = true }) { Text("Mode: ${modeLabel(mode)}") }
                        DropdownMenu(expanded = modeMenu, onDismissRequest = { modeMenu = false }) {
                            DictionarySearchMode.entries.forEach { option ->
                                DropdownMenuItem(
                                    text = { Text(modeLabel(option)) },
                                    onClick = { modeName = option.name; modeMenu = false; job?.cancel(); revision++; loading = false; result = null },
                                )
                            }
                        }
                    }
                }
                Button(onClick = ::submit, enabled = word.isNotBlank() && !loading) { Text("Search") }
                Spacer(Modifier.height(12.dp))
                if (loading) Text("Searching…")
                error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                result?.let { found ->
                    val pronunciation = found.pronunciation
                    if (pronunciation == null) {
                        Text("No IPA found for this word")
                    } else {
                        Text("IPA: ${pronunciation.ipa.joinToString(", ")}")
                        Text("Source: ${sourceLabel(pronunciation.source)}", style = MaterialTheme.typography.bodySmall)
                    }
                    Spacer(Modifier.height(8.dp))
                    Text("Matches (${found.matches.size})", style = MaterialTheme.typography.titleMedium)
                    if (found.matches.isEmpty()) {
                        Text("No matches in installed dictionaries")
                    } else {
                        LazyColumn {
                            items(found.matches) { match ->
                                Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                    Text(match.word, style = MaterialTheme.typography.bodyLarge)
                                    Text("${match.ipa} · ${sourceLabel(match.source)}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun languageLabel(language: String): String = when (language) {
    "en" -> "English"
    "de" -> "German"
    else -> "Turkish"
}

private fun modeLabel(mode: DictionarySearchMode): String = when (mode) {
    DictionarySearchMode.RHYME -> "Rhyme"
    DictionarySearchMode.WORD_PREFIX -> "Word prefix"
    DictionarySearchMode.WORD_SUFFIX -> "Word suffix"
    DictionarySearchMode.ASSONANCE -> "Assonance"
}

private fun sourceLabel(source: PronunciationSource): String = when (source) {
    PronunciationSource.WIKTIONARY -> "Wiktionary"
    PronunciationSource.ESPEAK_DATABASE -> "eSpeak dictionary"
    PronunciationSource.ESPEAK_GENERATED -> "Generated offline by eSpeak"
}
