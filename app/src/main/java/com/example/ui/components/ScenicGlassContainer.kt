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
// Upper 50%: Sky (آسمان - Azure to Sky Blue)
// Lower 50%: Meadow (دشت سبز - Fresh Grass Green to Deep Meadow)
val LightScenicLandscapeBrush = Brush.verticalGradient(
    colorStops = arrayOf(
        0.0f to Color(0xFF0284C7),  // Sky Top: Deep Sky Blue
        0.30f to Color(0xFF38BDF8), // Mid Sky: Azure Blue
        0.48f to Color(0xFFBAE6FD), // Atmosphere Horizon: Soft Light Cyan Sky
        0.52f to Color(0xFF86EFAC), // Horizon Grass Meadow Edge: Soft Fresh Green
        0.70f to Color(0xFF22C55E), // Mid Meadow: Fresh Grass Green
        1.0f to Color(0xFF15803D)   // Bottom Meadow: Deep Lush Field Green
    )
)

val DarkScenicLandscapeBrush = Brush.verticalGradient(
    colorStops = arrayOf(
        0.0f to Color(0xFF0F172A),  // Night Sky Top: Deep Dark Slate Blue
        0.30f to Color(0xFF0369A1), // Mid Night Sky: Deep Cyan Indigo
        0.48f to Color(0xFF0C4A6E), // Horizon Line: Dusk Cyan Glow
        0.52f to Color(0xFF064E3B), // Horizon Field: Dark Emerald
        0.70f to Color(0xFF14532D), // Mid Night Meadow: Forest Meadow Green
        1.0f to Color(0xFF052E16)   // Bottom Field: Deep Dark Green
    )
)

@Composable
fun ScenicGlassContainer(
    isGlassMode: Boolean,
    isDark: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    if (isGlassMode) {
        val backgroundBrush = if (isDark) DarkScenicLandscapeBrush else LightScenicLandscapeBrush
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(backgroundBrush)
        ) {
            content()
        }
    } else {
        Box(modifier = modifier.fillMaxSize()) {
            content()
        }
    }
}
