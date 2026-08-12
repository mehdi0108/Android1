package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// Scenic Landscape Gradient Brush:
// Upper 50%: Sky (آسمان - Azure to Bright Sky Blue & Atmospheric Glow)
// Lower 50%: Meadow (دشت سبز - Fresh Grass Green to Lush Emerald Field)
val LightScenicLandscapeBrush = Brush.verticalGradient(
    colorStops = arrayOf(
        0.00f to Color(0xFF0284C7), // Sky Top: Deep Sky Azure
        0.25f to Color(0xFF38BDF8), // Upper Sky: Bright Sky Blue
        0.48f to Color(0xFFBAE6FD), // Horizon Sky: Soft Cyan Atmosphere
        0.52f to Color(0xFF86EFAC), // Horizon Meadow: Fresh Soft Green
        0.75f to Color(0xFF22C55E), // Lower Meadow: Vivid Grass Green
        1.00f to Color(0xFF15803D)  // Meadow Bottom: Deep Lush Meadow Green
    )
)

val DarkScenicLandscapeBrush = Brush.verticalGradient(
    colorStops = arrayOf(
        0.00f to Color(0xFF0F172A), // Dark Sky Top: Night Slate Blue
        0.25f to Color(0xFF0369A1), // Upper Sky: Deep Azure Sky
        0.48f to Color(0xFF0C4A6E), // Horizon Sky: Dusk Atmospheric Glow
        0.52f to Color(0xFF064E3B), // Horizon Field: Deep Emerald Grass
        0.75f to Color(0xFF14532D), // Lower Field: Forest Meadow Green
        1.00f to Color(0xFF052E16)  // Meadow Bottom: Deep Lush Night Field
    )
)

@Composable
fun ScenicGlassContainer(
    isGlassMode: Boolean = true,
    isDark: Boolean = true,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    val backgroundBrush = if (isDark) DarkScenicLandscapeBrush else LightScenicLandscapeBrush
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(backgroundBrush)
    ) {
        content()
    }
}

