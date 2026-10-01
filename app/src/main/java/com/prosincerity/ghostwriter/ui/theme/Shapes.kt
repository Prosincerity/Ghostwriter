package com.prosincerity.ghostwriter.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Apply explicitly: Material 3 button defaults use a full (pill) shape.
internal val GhostButtonShape = RoundedCornerShape(14.dp)

internal val GhostShapes = Shapes(
    extraSmall = RoundedCornerShape(8.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(12.dp),
    large = RoundedCornerShape(16.dp),
    extraLarge = RoundedCornerShape(20.dp),
)
