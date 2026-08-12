package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun WhiteBorderCard(
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
    borderColor: Color = Color.White.copy(alpha = 0.8f),
    borderWidth: Dp = 1.5.dp,
    containerColor: Color = Color.White.copy(alpha = 0.22f),
    isGlassMode: Boolean = true,
    content: @Composable ColumnScope.() -> Unit
) {
    // Frosted Glass (حالت مات شیشه‌ای) Effect
    val finalContainerColor = if (isGlassMode) {
        if (containerColor == MaterialTheme.colorScheme.surface) Color.White.copy(alpha = 0.22f) else containerColor
    } else {
        containerColor
    }

    val finalBorderColor = if (isGlassMode) {
        if (borderColor == MaterialTheme.colorScheme.outline) Color.White.copy(alpha = 0.8f) else borderColor
    } else {
        borderColor
    }

    val finalBorderWidth = if (isGlassMode) 1.5.dp else borderWidth
    val shape = RoundedCornerShape(20.dp)

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = finalContainerColor),
            border = BorderStroke(finalBorderWidth, finalBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = shape,
            colors = CardDefaults.cardColors(containerColor = finalContainerColor),
            border = BorderStroke(finalBorderWidth, finalBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            content = content
        )
    }
}
