package com.prosincerity.ghostwriter.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Book
import androidx.compose.material.icons.filled.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.data.LyricFont
import com.prosincerity.ghostwriter.data.LyricFontFamily
import com.prosincerity.ghostwriter.data.LyricTextAlignment
import com.prosincerity.ghostwriter.data.LyricTextSettings
import com.prosincerity.ghostwriter.data.Settings as AppSettings
import com.prosincerity.ghostwriter.data.SystemFontCatalog
import com.prosincerity.ghostwriter.ui.components.SlimSlider
import com.prosincerity.ghostwriter.ui.components.toTextStyle
import com.prosincerity.ghostwriter.ui.theme.GhostButtonShape
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

/**
 * Autosave and lyric typography settings. Uses a plain
 * Box + DropdownMenu rather than Material3's
 * ExposedDropdownMenuBox — that API has changed shape across library
 * versions, while basic DropdownMenu has stayed stable, so this is the
 * safer bet against version drift.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onBack: () -> Unit,
    onOpenAbout: () -> Unit,
    onOpenDictionaryDownloads: () -> Unit,
) {
    BackHandler(onBack = onBack)

    val context = LocalContext.current

    var intervalSeconds by remember { mutableIntStateOf(AppSettings.getAutosaveIntervalSeconds(context)) }
    var autosaveCount by remember { mutableIntStateOf(AppSettings.getAutosaveCount(context)) }
    var lyricTextSettings by remember(context) { mutableStateOf(AppSettings.getLyricTextSettings(context)) }
    val fontOptions by produceState<List<LyricFont>>(initialValue = LyricFontFamily.entries) {
        value = withContext(Dispatchers.IO) { SystemFontCatalog.availableFonts() }
    }

    fun updateLyricTextSettings(settings: LyricTextSettings) {
        lyricTextSettings = settings.normalized()
        AppSettings.setLyricTextSettings(context, lyricTextSettings)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
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
            SettingsSection("Saving") {
                SettingsDropdownRow(
                    description = "Autosave interval",
                    options = AppSettings.INTERVAL_OPTIONS_SECONDS,
                    selected = intervalSeconds,
                    label = ::formatInterval,
                    onSelect = {
                        intervalSeconds = it
                        AppSettings.setAutosaveIntervalSeconds(context, it)
                    },
                )

                Spacer(Modifier.height(8.dp))

                SettingsDropdownRow(
                    description = "Autosave backups",
                    options = AppSettings.COUNT_OPTIONS,
                    selected = autosaveCount,
                    label = { it.toString() },
                    onSelect = {
                        autosaveCount = it
                        AppSettings.setAutosaveCount(context, it)
                    },
                )
            }

            Spacer(Modifier.height(24.dp))

            SettingsSection("Lyric appearance") {
                Text(
                    "Applies to all projects. Uses fonts built into your device. Text size follows Android's font size setting.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(bottom = 8.dp),
                )
                SettingsDropdownRow(
                    description = "Font family",
                    options = fontOptions,
                    selected = lyricTextSettings.fontFamily,
                    label = { if (it in fontOptions) it.label else "${it.label} (unavailable)" },
                    onSelect = { updateLyricTextSettings(lyricTextSettings.copy(fontFamily = it)) },
                    useDialog = true,
                )
                SettingsDropdownRow(
                    description = "Font size",
                    options = LyricTextSettings.FONT_SIZE_OPTIONS,
                    selected = lyricTextSettings.fontSizeSp,
                    label = { "$it sp" },
                    onSelect = { updateLyricTextSettings(lyricTextSettings.copy(fontSizeSp = it)) },
                )
                SettingsSliderRow(
                    description = "Line height",
                    value = lyricTextSettings.lineHeightMultiplier,
                    valueRange = LyricTextSettings.LINE_HEIGHT_RANGE,
                    valueLabel = "${formatTypographyNumber(lyricTextSettings.lineHeightMultiplier)}×",
                    onValueChange = { updateLyricTextSettings(lyricTextSettings.copy(lineHeightMultiplier = it)) },
                )
                SettingsSliderRow(
                    description = "Letter spacing",
                    value = if (lyricTextSettings.alignment == LyricTextAlignment.JUSTIFY) 0f
                        else lyricTextSettings.letterSpacingSp,
                    valueRange = LyricTextSettings.LETTER_SPACING_RANGE,
                    valueLabel = if (lyricTextSettings.alignment == LyricTextAlignment.JUSTIFY) "0 sp"
                        else "${formatTypographyNumber(lyricTextSettings.letterSpacingSp)} sp",
                    onValueChange = { updateLyricTextSettings(lyricTextSettings.copy(letterSpacingSp = it)) },
                    enabled = lyricTextSettings.alignment != LyricTextAlignment.JUSTIFY,
                )
                SettingsDropdownRow(
                    description = "Text alignment",
                    options = LyricTextAlignment.entries,
                    selected = lyricTextSettings.alignment,
                    label = { it.label },
                    onSelect = { updateLyricTextSettings(lyricTextSettings.copy(alignment = it)) },
                )
                if (lyricTextSettings.alignment == LyricTextAlignment.JUSTIFY) {
                    Text(
                        "Justified text uses normal letter spacing. Your spacing is kept for other alignments.",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(top = 4.dp),
                    )
                }
                Text("Preview", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
                Surface(
                    shape = GhostButtonShape,
                    color = MaterialTheme.colorScheme.surfaceContainerLow,
                    modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
                ) {
                    Text(
                        "Find the rhythm in the words\nLet the next line carry the beat",
                        style = lyricTextSettings.toTextStyle(MaterialTheme.typography.bodyLarge),
                        modifier = Modifier.fillMaxWidth().padding(12.dp),
                    )
                }
            }

            Spacer(Modifier.height(24.dp))
            SettingsNavigationRow("Download Dictionaries", Icons.Filled.Book, onOpenDictionaryDownloads)
            SettingsNavigationRow("About Ghostwriter", Icons.Filled.Info, onOpenAbout)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SettingsSliderRow(
    description: String,
    value: Float,
    valueRange: ClosedFloatingPointRange<Float>,
    valueLabel: String,
    onValueChange: (Float) -> Unit,
    enabled: Boolean = true,
) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(modifier = Modifier.weight(0.4f).padding(end = 12.dp)) {
            Text(
                description,
                style = MaterialTheme.typography.bodyMedium,
            )
            Text(
                valueLabel,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
        SlimSlider(
            value = value,
            enabled = enabled,
            onValueChange = { onValueChange((it * 100).roundToInt() / 100f) },
            valueRange = valueRange,
            modifier = Modifier.weight(0.6f).semantics { contentDescription = description },
        )
    }
}

@Composable
private fun SettingsSection(title: String, content: @Composable ColumnScope.() -> Unit) {
    Text(
        title, style = MaterialTheme.typography.titleMedium,
        modifier = Modifier.padding(bottom = 12.dp),
    )
    Surface(shape = MaterialTheme.shapes.large, color = MaterialTheme.colorScheme.surfaceContainer) {
        Column(Modifier.fillMaxWidth().padding(16.dp), content = content)
    }
}

@Composable
private fun SettingsNavigationRow(label: String, icon: ImageVector, onClick: () -> Unit) {
    Surface(onClick = onClick, shape = MaterialTheme.shapes.medium, color = MaterialTheme.colorScheme.background) {
        Row(
            Modifier.fillMaxWidth().padding(vertical = 16.dp, horizontal = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(label, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
            Icon(Icons.AutoMirrored.Filled.ArrowForward, contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}

@Composable
private fun <T> SettingsDropdownRow(
    description: String,
    options: List<T>,
    selected: T,
    label: (T) -> String,
    onSelect: (T) -> Unit,
    useDialog: Boolean = false,
) {
    var expanded by remember { mutableStateOf(false) }

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(
            text = description,
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.bodyMedium,
        )
        Box(modifier = if (useDialog) Modifier.weight(1.5f) else Modifier) {
            OutlinedButton(
                shape = GhostButtonShape,
                onClick = { expanded = true },
                modifier = Modifier
                    .heightIn(min = 36.dp)
                    .semantics { contentDescription = description },
                contentPadding = PaddingValues(horizontal = 12.dp),
            ) {
                Text(
                    label(selected),
                    style = MaterialTheme.typography.labelMedium,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                )
            }
            if (useDialog && expanded) {
                AlertDialog(
                    onDismissRequest = { expanded = false },
                    title = { Text(description) },
                    text = {
                        LazyColumn(Modifier.fillMaxWidth().heightIn(max = 400.dp)) {
                            items(options) { option ->
                                TextButton(
                                    shape = GhostButtonShape,
                                    onClick = {
                                        onSelect(option)
                                        expanded = false
                                    },
                                    modifier = Modifier.fillMaxWidth(),
                                ) {
                                    Text(label(option), modifier = Modifier.fillMaxWidth())
                                }
                            }
                        }
                    },
                    confirmButton = {
                        TextButton(shape = GhostButtonShape, onClick = { expanded = false }) { Text("Close") }
                    },
                )
            }
            DropdownMenu(expanded = expanded && !useDialog, onDismissRequest = { expanded = false }) {
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
}

internal fun formatInterval(seconds: Int): String = when {
    seconds < 60 -> "$seconds seconds"
    seconds % 60 == 0 -> "${seconds / 60} minute" + if (seconds / 60 > 1) "s" else ""
    else -> "$seconds seconds"
}

internal fun formatTypographyNumber(value: Float): String =
    String.format(Locale.ROOT, "%.2f", value).trimEnd('0').trimEnd('.')
