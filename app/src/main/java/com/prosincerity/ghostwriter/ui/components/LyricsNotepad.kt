package com.prosincerity.ghostwriter.ui.components

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardCapitalization
import com.prosincerity.ghostwriter.data.LyricTextSettings

/**
 * The state-hoisted lyrics editing surface.
 *
 * Keeping the notepad layout here leaves [EditorScreen][com.prosincerity.ghostwriter.ui.screens.EditorScreen]
 * responsible for persistence and screen-level behavior while this component can later arrange optional
 * syllable and bar-count gutters around the text field.
 */
@Composable
internal fun LyricsNotepad(
    lyrics: String,
    onLyricsChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    textSettings: LyricTextSettings = LyricTextSettings(),
) {
    val textStyle = textSettings.toTextStyle(MaterialTheme.typography.bodyLarge)
    TextField(
        value = lyrics,
        onValueChange = onLyricsChange,
        modifier = modifier,
        placeholder = { Text("Start writing...", style = textStyle, modifier = Modifier.fillMaxWidth()) },
        textStyle = textStyle,
        keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Sentences),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.background,
            unfocusedContainerColor = MaterialTheme.colorScheme.background,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        ),
    )
}
