package com.prosincerity.ghostwriter.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.prosincerity.ghostwriter.R

// Bundled faces keep UI typography consistent across devices and available offline.
internal val RobotoFontFamily = FontFamily(
    Font(R.font.roboto_regular, FontWeight.Normal),
    Font(R.font.roboto_medium, FontWeight.Medium),
    Font(R.font.roboto_bold, FontWeight.Bold),
)

private val defaultTypography = Typography()

val GhostTypography = Typography(
    displayLarge = defaultTypography.displayLarge.copy(fontFamily = RobotoFontFamily),
    displayMedium = defaultTypography.displayMedium.copy(fontFamily = RobotoFontFamily),
    displaySmall = defaultTypography.displaySmall.copy(fontFamily = RobotoFontFamily),
    headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = RobotoFontFamily),
    headlineMedium = defaultTypography.headlineMedium.copy(
        fontFamily = RobotoFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 28.sp, lineHeight = 34.sp, letterSpacing = (-0.4).sp,
    ),
    headlineSmall = defaultTypography.headlineSmall.copy(
        fontFamily = RobotoFontFamily, fontWeight = FontWeight.Bold,
        fontSize = 24.sp, lineHeight = 30.sp, letterSpacing = (-0.2).sp,
    ),
    bodyLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp,
    ),
    bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = RobotoFontFamily),
    bodySmall = defaultTypography.bodySmall.copy(fontFamily = RobotoFontFamily),
    titleLarge = TextStyle(
        fontFamily = RobotoFontFamily,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp,
        letterSpacing = (-0.2).sp,
    ),
    titleMedium = defaultTypography.titleMedium.copy(
        fontFamily = RobotoFontFamily, fontWeight = FontWeight.Medium, letterSpacing = 0.sp,
    ),
    titleSmall = defaultTypography.titleSmall.copy(fontFamily = RobotoFontFamily),
    labelLarge = defaultTypography.labelLarge.copy(fontFamily = RobotoFontFamily),
    labelMedium = defaultTypography.labelMedium.copy(fontFamily = RobotoFontFamily),
    labelSmall = defaultTypography.labelSmall.copy(fontFamily = RobotoFontFamily),
)
