package com.prosincerity.ghostwriter.ui

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.luminance
import com.prosincerity.ghostwriter.ui.theme.GhostColorScheme
import org.junit.Assert.assertTrue
import org.junit.Test

class GhostThemeContrastTest {
    @Test
    fun textRemainsReadableAcrossStudioSurfacesAndSelectedControls() {
        val theme = GhostColorScheme
        val pairs = listOf(
            theme.onBackground to theme.background,
            theme.onSurface to theme.surfaceContainer,
            theme.onSurfaceVariant to theme.surfaceContainerHighest,
            theme.onPrimary to theme.primary,
            theme.onSecondary to theme.secondary,
            theme.onPrimaryContainer to theme.primaryContainer,
            theme.onSecondaryContainer to theme.secondaryContainer,
            theme.error to theme.surfaceContainer,
            theme.onErrorContainer to theme.errorContainer,
            theme.onPrimaryFixedVariant to theme.primaryFixedDim,
            theme.onSecondaryFixedVariant to theme.secondaryFixedDim,
            theme.onTertiaryFixedVariant to theme.tertiaryFixedDim,
        )
        for ((text, background) in pairs) {
            val contrast = contrast(text, background)
            assertTrue("Text contrast $contrast is below 4.5:1 for $text on $background", contrast >= 4.5f)
        }
    }

    private fun contrast(first: Color, second: Color): Float {
        val a = first.luminance()
        val b = second.luminance()
        return (maxOf(a, b) + 0.05f) / (minOf(a, b) + 0.05f)
    }
}
