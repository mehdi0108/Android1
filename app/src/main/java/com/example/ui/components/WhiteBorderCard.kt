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
    borderColor: Color = MaterialTheme.colorScheme.outline,
    borderWidth: Dp = 2.dp,
    containerColor: Color = MaterialTheme.colorScheme.surface,
    isGlassMode: Boolean = false,
    content: @Composable ColumnScope.() -> Unit
) {
    val finalContainerColor = if (isGlassMode) {
        Color.White.copy(alpha = 0.28f)
    } else {
        containerColor
    }

    val finalBorderColor = if (isGlassMode) {
        Color.White.copy(alpha = 0.85f)
    } else {
        borderColor
    }

    val finalBorderWidth = if (isGlassMode) 2.dp else borderWidth

    if (onClick != null) {
        Card(
            onClick = onClick,
            modifier = modifier,
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = finalContainerColor),
            border = BorderStroke(finalBorderWidth, finalBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isGlassMode) 1.dp else 4.dp),
            content = content
        )
    } else {
        Card(
            modifier = modifier,
            shape = RoundedCornerShape(18.dp),
            colors = CardDefaults.cardColors(containerColor = finalContainerColor),
            border = BorderStroke(finalBorderWidth, finalBorderColor),
            elevation = CardDefaults.cardElevation(defaultElevation = if (isGlassMode) 1.dp else 4.dp),
            content = content
        )
    }
}
