package com.prosincerity.ghostwriter.ui

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
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

    @Test
    fun uiTypography_keepsReadableBodyAndStrongerScreenTitles() {
        assertEquals(16.sp, GhostTypography.bodyLarge.fontSize)
        assertEquals(24.sp, GhostTypography.bodyLarge.lineHeight)
        assertEquals(FontWeight.Normal, GhostTypography.bodyLarge.fontWeight)
        assertEquals(22.sp, GhostTypography.titleLarge.fontSize)
        assertEquals(28.sp, GhostTypography.titleLarge.lineHeight)
        assertEquals(FontWeight.Bold, GhostTypography.titleLarge.fontWeight)
        assertEquals(FontWeight.Medium, GhostTypography.labelLarge.fontWeight)
    }
}
