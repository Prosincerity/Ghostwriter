package com.prosincerity.ghostwriter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val GhostDarkScheme = darkColorScheme(
    primary = GhostAccent,
    onPrimary = GhostBackground,
    background = GhostBackground,
    onBackground = GhostText,
    surface = GhostSurface,
    onSurface = GhostText,
    secondary = GhostAccentDim,
)

private val GhostLightScheme = lightColorScheme(
    primary = GhostAccentDim,
    background = Color.White,
)

@Composable
fun GhostwriterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // The app is designed dark-first; light mode is a fallback, not the focus.
    val colorScheme = if (darkTheme) GhostDarkScheme else GhostLightScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GhostTypography,
        content = content,
    )
}
