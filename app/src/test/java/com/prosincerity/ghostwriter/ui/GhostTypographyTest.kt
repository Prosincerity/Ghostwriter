package com.prosincerity.ghostwriter.ui

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.prosincerity.ghostwriter.R
import com.prosincerity.ghostwriter.ui.theme.GhostTypography
import org.junit.Assert.assertEquals
import org.junit.Test

class GhostTypographyTest {
    @Test
    fun everyUiTextRole_usesBundledRobotoFaces() {
        val expectedFamily = FontFamily(
            Font(R.font.roboto_regular, FontWeight.Normal),
            Font(R.font.roboto_medium, FontWeight.Medium),
            Font(R.font.roboto_bold, FontWeight.Bold),
        )
        val styles = with(GhostTypography) {
            listOf(
                displayLarge, displayMedium, displaySmall,
                headlineLarge, headlineMedium, headlineSmall,
                titleLarge, titleMedium, titleSmall,
                bodyLarge, bodyMedium, bodySmall,
                labelLarge, labelMedium, labelSmall,
            )
        }
        styles.forEachIndexed { index, style ->
            assertEquals("UI text role $index", expectedFamily, style.fontFamily)
        }
    }
}
