package com.prosincerity.ghostwriter.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable

private val GhostDarkScheme = darkColorScheme(
    primary = GhostPrimary,
    onPrimary = GhostBackground,
    primaryContainer = GhostSurface,
    onPrimaryContainer = GhostPrimary,
    secondary = GhostSecondary,
    onSecondary = GhostBackground,
    secondaryContainer = GhostSurface,
    onSecondaryContainer = GhostSecondary,
    background = GhostBackground,
    onBackground = GhostText,
    surface = GhostSurface,
    onSurface = GhostText,
    surfaceVariant = GhostSurface,
    onSurfaceVariant = GhostText,
    outline = GhostBorder,
    outlineVariant = GhostBorder,
)

private val GhostLightScheme = lightColorScheme(
    primary = GhostPrimary,
    onPrimary = GhostBackground,
    primaryContainer = GhostSurface,
    onPrimaryContainer = GhostPrimary,
    secondary = GhostSecondary,
    onSecondary = GhostBackground,
    secondaryContainer = GhostSurface,
    onSecondaryContainer = GhostSecondary,
    background = GhostBackground,
    onBackground = GhostText,
    surface = GhostSurface,
    onSurface = GhostText,
    surfaceVariant = GhostSurface,
    onSurfaceVariant = GhostText,
    outline = GhostBorder,
    outlineVariant = GhostBorder,
)

@Composable
fun GhostwriterTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    // Keep the supplied dark palette consistent even when the device uses light mode.
    val colorScheme = if (darkTheme) GhostDarkScheme else GhostLightScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = GhostTypography,
        content = content,
    )
}
