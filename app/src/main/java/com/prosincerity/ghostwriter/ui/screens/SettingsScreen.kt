package com.prosincerity.ghostwriter.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import com.prosincerity.ghostwriter.data.LyricFont
import com.prosincerity.ghostwriter.data.LyricFontFamily
import com.prosincerity.ghostwriter.data.LyricTextAlignment
import com.prosincerity.ghostwriter.data.LyricTextSettings
import com.prosincerity.ghostwriter.data.SystemFontCatalog
import com.prosincerity.ghostwriter.ui.components.toTextStyle
import com.prosincerity.ghostwriter.ui.theme.GhostButtonShape
import java.util.Locale
import kotlin.math.roundToInt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.prosincerity.ghostwriter.data.Settings as AppSettings

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

            Spacer(Modifier.height(24.dp))

            Text("Lyric pad", style = MaterialTheme.typography.titleMedium)
            Text(
                "Applies to all projects. Uses fonts built into your device. Text size follows Android's font size setting.",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
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
                value = lyricTextSettings.letterSpacingSp,
                valueRange = LyricTextSettings.LETTER_SPACING_RANGE,
                valueLabel = "${formatTypographyNumber(lyricTextSettings.letterSpacingSp)} sp",
                onValueChange = { updateLyricTextSettings(lyricTextSettings.copy(letterSpacingSp = it)) },
            )
            SettingsDropdownRow(
                description = "Text alignment",
                options = LyricTextAlignment.entries,
                selected = lyricTextSettings.alignment,
                label = { it.label },
                onSelect = { updateLyricTextSettings(lyricTextSettings.copy(alignment = it)) },
            )
            Text("Preview", style = MaterialTheme.typography.labelMedium, modifier = Modifier.padding(top = 8.dp))
            Surface(
                shape = GhostButtonShape,
                color = MaterialTheme.colorScheme.surfaceVariant,
                modifier = Modifier.fillMaxWidth().padding(top = 4.dp),
            ) {
                Text(
                    "Find the rhythm in the words\nLet the next line carry the beat",
                    style = lyricTextSettings.toTextStyle(MaterialTheme.typography.bodyLarge),
                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                )
            }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                shape = GhostButtonShape,
                onClick = onOpenDictionaryDownloads,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("Download Dictionaries")
            }

            Spacer(Modifier.height(24.dp))

            OutlinedButton(
                shape = GhostButtonShape,
                onClick = onOpenAbout,
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text("About Ghostwriter")
            }
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
) {
    val interactionSource = remember { MutableInteractionSource() }
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
        Slider(
            value = value,
            onValueChange = { onValueChange((it * 100).roundToInt() / 100f) },
            valueRange = valueRange,
            interactionSource = interactionSource,
            thumb = {
                SliderDefaults.Thumb(
                    interactionSource = interactionSource,
                    thumbSize = DpSize(12.dp, 12.dp),
                )
            },
            track = { state ->
                SliderDefaults.Track(
                    sliderState = state,
                    modifier = Modifier.height(4.dp),
                    drawStopIndicator = null,
                    thumbTrackGapSize = 2.dp,
                    trackInsideCornerSize = 2.dp,
                )
            },
            modifier = Modifier.weight(0.6f).semantics { contentDescription = description },
        )
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
                        TextButton(onClick = { expanded = false }) { Text("Close") }
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
