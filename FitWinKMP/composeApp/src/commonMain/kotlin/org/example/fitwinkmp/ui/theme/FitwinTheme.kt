package org.example.fitwinkmp.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

object FitwinColors {
    val Background = Color(0xFF131313)
    val Surface = Color(0xFF131313)
    val SurfaceContainer = Color(0xFF1F1F1F)
    val SurfaceContainerLow = Color(0xFF1B1B1B)
    val SurfaceContainerHigh = Color(0xFF2A2A2A)
    val SurfaceContainerHighest = Color(0xFF353535)
    val PrimaryContainer = Color(0xFFFFD700)
    val Primary = Color(0xFFFFF6DF)
    val OnPrimary = Color(0xFF3A3000)
    val OnPrimaryContainer = Color(0xFF705E00)
    val OnSurface = Color(0xFFE2E2E2)
    val OnSurfaceVariant = Color(0xFFD0C6AB)
    val Outline = Color(0xFF999077)
    val OutlineVariant = Color(0xFF4D4732)
    val Secondary = Color(0xFF9FCAFF)
    val Error = Color(0xFFFFB4AB)
    
    // Macro Colors
    val MacroProtein = Color(0xFFFFD700)
    val MacroCarbs = Color(0xFF88B1FF)
    val MacroFats = Color(0xFFE2E2E2)
    val MacroFiber = Color(0xFF999077)
}

private val FitwinColorScheme = darkColorScheme(
    primary = FitwinColors.PrimaryContainer,
    onPrimary = FitwinColors.OnPrimary,
    primaryContainer = FitwinColors.PrimaryContainer,
    onPrimaryContainer = FitwinColors.OnPrimaryContainer,
    secondary = FitwinColors.Secondary,
    background = FitwinColors.Background,
    surface = FitwinColors.Surface,
    onSurface = FitwinColors.OnSurface,
    onSurfaceVariant = FitwinColors.OnSurfaceVariant,
    outline = FitwinColors.Outline,
    outlineVariant = FitwinColors.OutlineVariant,
    error = FitwinColors.Error,
)

@Composable
fun FitwinTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = FitwinColorScheme,
        content = content
    )
}
