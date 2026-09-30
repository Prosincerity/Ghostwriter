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
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.data.DictionaryInstaller
import com.prosincerity.ghostwriter.data.DictionarySearch
import com.prosincerity.ghostwriter.data.DictionarySearchMode
import com.prosincerity.ghostwriter.data.DictionarySearchResult
import com.prosincerity.ghostwriter.data.PronunciationSource
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun DictionaryScreen(
    onBack: () -> Unit,
    onOpenDownloads: () -> Unit,
    search: (suspend (String, String, DictionarySearchMode, Int) -> DictionarySearchResult)? = null,
    installedLanguages: List<String>? = null,
) {
    BackHandler(onBack = onBack)
    val context = LocalContext.current
    val installer = remember(context) { DictionaryInstaller(context) }
    val repository = remember(installer) { DictionarySearch(installer) }
    val discoveredLanguages by produceState<List<String>?>(null, installer, installedLanguages) {
        value = installedLanguages ?: withContext(Dispatchers.IO) { installer.availableLanguages() }
    }
    val checkingDictionaries = installedLanguages == null && discoveredLanguages == null
    val availableLanguages = installedLanguages ?: discoveredLanguages.orEmpty()
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
    var page by remember { mutableIntStateOf(0) }
    var loading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }
    var job by remember { mutableStateOf<Job?>(null) }
    var revision by remember { mutableIntStateOf(0) }

    fun resetSearch() {
        revision++
        job?.cancel()
        job = null
        loading = false
        result = null
        error = null
        page = 0
    }

    fun submit(requestedPage: Int = 0) {
        val input = word.trim()
        if (input.isEmpty() || language !in availableLanguages) return
        resetSearch()
        val requested = revision
        loading = true
        job = scope.launch {
            try {
                val found = searchAction(input, language, mode, requestedPage)
                if (requested == revision) {
                    result = found
                    page = requestedPage
                }
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
            title = { Text("Rhyme Search") },
            navigationIcon = {
                IconButton(onClick = onBack) {
                    Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                }
            },
        )
    }) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            if (checkingDictionaries) {
                Text("Checking installed dictionaries…")
            } else if (availableLanguages.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    Text("No rhyme dictionaries are installed", textAlign = TextAlign.Center)
                    Spacer(Modifier.height(8.dp))
                    Text(
                        "Download at least one language to search for matching words.",
                        textAlign = TextAlign.Center,
                    )
                    Spacer(Modifier.height(12.dp))
                    Button(onClick = onOpenDownloads) { Text("Download dictionaries") }
                }
            } else {
                OutlinedTextField(
                    value = word,
                    onValueChange = {
                        word = it
                        resetSearch()
                    },
                    label = { Text("Word") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                )
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    Box {
                        TextButton(onClick = { languageMenu = true }) { Text("Language: ${dictionaryLanguageLabel(language)}") }
                        DropdownMenu(expanded = languageMenu, onDismissRequest = { languageMenu = false }) {
                            availableLanguages.forEach { code ->
                                DropdownMenuItem(
                                    text = { Text(dictionaryLanguageLabel(code)) },
                                    onClick = {
                                        selectedLanguage = code
                                        languageMenu = false
                                        resetSearch()
                                    },
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
                                    onClick = {
                                        modeName = option.name
                                        modeMenu = false
                                        resetSearch()
                                    },
                                )
                            }
                        }
                    }
                }
                Button(onClick = { submit() }, enabled = word.isNotBlank() && !loading) { Text("Search") }
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
                        LazyColumn(modifier = Modifier.weight(1f)) {
                            items(found.matches) { match ->
                                Column(Modifier.fillMaxWidth().padding(vertical = 6.dp)) {
                                    Text(match.word, style = MaterialTheme.typography.bodyLarge)
                                    Text("${match.ipa} · ${sourceLabel(match.source)}", style = MaterialTheme.typography.bodySmall)
                                }
                            }
                        }
                    }
                    if (page > 0 || found.hasNext) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            TextButton(onClick = { submit(page - 1) }, enabled = page > 0) { Text("Previous") }
                            Text("Page ${page + 1}")
                            TextButton(onClick = { submit(page + 1) }, enabled = found.hasNext) { Text("Next") }
                        }
                    }
                }
            }
        }
    }
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
