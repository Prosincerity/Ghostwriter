package com.prosincerity.ghostwriter.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// Ghostwriter deliberately keeps its studio palette in both device appearance modes.
// Define every surface role so cards and popups never inherit light-theme defaults.
internal val GhostColorScheme = darkColorScheme(
    primary = GhostPrimary,
    onPrimary = GhostBackground,
    primaryContainer = Color(0xFF382016),
    onPrimaryContainer = Color(0xFFFFB59B),
    inversePrimary = Color(0xFFAE3200),
    secondary = GhostSecondary,
    onSecondary = GhostBackground,
    secondaryContainer = Color(0xFF292038),
    onSecondaryContainer = Color(0xFFD6C4FF),
    tertiary = Color(0xFFC4ABFF),
    onTertiary = Color(0xFF24143C),
    tertiaryContainer = Color(0xFF36254C),
    onTertiaryContainer = Color(0xFFE9DDFF),
    background = GhostBackground,
    onBackground = GhostText,
    surface = GhostSurface,
    onSurface = GhostText,
    surfaceVariant = Color(0xFF252529),
    onSurfaceVariant = GhostMutedText,
    surfaceTint = Color.Transparent,
    inverseSurface = GhostText,
    inverseOnSurface = GhostSurface,
    surfaceBright = Color(0xFF303036),
    surfaceDim = GhostBackground,
    surfaceContainerLowest = GhostBackground,
    surfaceContainerLow = Color(0xFF0D0D0F),
    surfaceContainer = GhostSurface,
    surfaceContainerHigh = Color(0xFF1E1E22),
    surfaceContainerHighest = Color(0xFF252529),
    outline = Color(0xFF73737D),
    outlineVariant = GhostBorder,
    error = Color(0xFFFF8A80),
    onError = Color(0xFF350005),
    errorContainer = Color(0xFF4A171B),
    onErrorContainer = Color(0xFFFFDAD6),
    scrim = GhostBackground,
    primaryFixed = Color(0xFFFFDACE),
    primaryFixedDim = Color(0xFFFFB59B),
    onPrimaryFixed = Color(0xFF350D00),
    onPrimaryFixedVariant = Color(0xFF713000),
    secondaryFixed = Color(0xFFEBDCFF),
    secondaryFixedDim = Color(0xFFD6C4FF),
    onSecondaryFixed = Color(0xFF231038),
    onSecondaryFixedVariant = Color(0xFF493467),
    tertiaryFixed = Color(0xFFE9DDFF),
    tertiaryFixedDim = Color(0xFFC4ABFF),
    onTertiaryFixed = Color(0xFF24143C),
    onTertiaryFixedVariant = Color(0xFF493467),
)

@Composable
fun GhostwriterTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = GhostColorScheme,
        typography = GhostTypography,
        shapes = GhostShapes,
        content = content,
    )
}
