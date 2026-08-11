package com.example.ui.components

import android.widget.Toast
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.MonetizationOn
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.viewmodel.AppViewModel
import kotlinx.coroutines.delay

val RainbowGradientColors = listOf(
    Color(0xFFFF0055), // Red / Magenta
    Color(0xFFFF7A00), // Orange
    Color(0xFFFFE600), // Gold / Yellow
    Color(0xFF00FF88), // Spring Green
    Color(0xFF00F0FF), // Cyan
    Color(0xFF3366FF), // Royal Blue
    Color(0xFF9D00FF), // Violet
    Color(0xFFFF0055)  // Loop back
)

@Composable
fun RainbowRewardTimerBadge(
    viewModel: AppViewModel,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var remainingMillis by remember { mutableStateOf(viewModel.getRemainingClaimTimeMillis()) }

    LaunchedEffect(Unit) {
        while (true) {
            remainingMillis = viewModel.getRemainingClaimTimeMillis()
            delay(1000L)
        }
    }

    val rainbowBrush = remember {
        Brush.horizontalGradient(colors = RainbowGradientColors)
    }

    val isReady = remainingMillis <= 0L
    val timeFormatted = if (isReady) {
        "🎁 جایزه"
    } else {
        val totalSec = remainingMillis / 1000
        val hours = totalSec / 3600
        val minutes = (totalSec % 3600) / 60
        val seconds = totalSec % 60
        String.format(java.util.Locale.US, "%02d:%02d:%02d", hours, minutes, seconds)
    }

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(20.dp),
        color = Color(0xFF0F172A).copy(alpha = 0.88f),
        border = androidx.compose.foundation.BorderStroke(
            width = 1.8.dp,
            brush = rainbowBrush
        ),
        modifier = modifier.testTag("header_rainbow_timer_badge")
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = 9.dp, vertical = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Schedule,
                contentDescription = "ساعت‌شمار جایزه",
                tint = if (isReady) Color(0xFF00FF88) else Color(0xFF00F0FF),
                modifier = Modifier.size(15.dp)
            )

            Spacer(modifier = Modifier.width(4.dp))

            Text(
                text = timeFormatted,
                fontSize = 11.5.sp,
                fontWeight = FontWeight.ExtraBold,
                color = if (isReady) Color(0xFF00FF88) else Color(0xFFFDE047)
            )
        }
    }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun CoinBalanceHeaderBadge(
    viewModel: AppViewModel,
    coins: Int,
    isDark: Boolean = true,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showCoinsModal by remember { mutableStateOf(false) }

    // Pulsing scale factor when coins update
    val scale by animateFloatAsState(
        targetValue = 1f,
        animationSpec = spring(),
        label = "coinScale"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        modifier = modifier
    ) {
        // Rainbow 7-color Countdown Timer Badge
        RainbowRewardTimerBadge(
            viewModel = viewModel,
            onClick = { showCoinsModal = true }
        )

        // Main Coin Balance Badge
        Surface(
            onClick = { showCoinsModal = true },
            shape = RoundedCornerShape(20.dp),
            color = if (isDark) Color(0xFF1E293B) else Color(0xFFFEF08A),
            border = androidx.compose.foundation.BorderStroke(
                width = 1.5.dp,
                color = Color(0xFFEAB308)
            ),
            modifier = Modifier
                .scale(scale)
                .testTag("header_coin_balance_badge")
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(22.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    Color(0xFFFDE047),
                                    Color(0xFFCA8A04)
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.MonetizationOn,
                        contentDescription = "سکه‌ها",
                        tint = Color(0xFF713F12),
                        modifier = Modifier.size(16.dp)
                    )
                }

                Spacer(modifier = Modifier.width(6.dp))

                AnimatedContent(
                    targetState = coins,
                    transitionSpec = {
                        if (targetState > initialState) {
                            (slideInVertically { height -> -height } + fadeIn()) togetherWith
                                    (slideOutVertically { height -> height } + fadeOut())
                        } else {
                            (slideInVertically { height -> height } + fadeIn()) togetherWith
                                    (slideOutVertically { height -> -height } + fadeOut())
                        }
                    },
                    label = "coinsAnimation"
                ) { targetCoins ->
                    Text(
                        text = "$targetCoins",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = if (isDark) Color(0xFFFDE047) else Color(0xFF854D0E)
                    )
                }
            }
        }
    }

    if (showCoinsModal) {
        val canClaim = viewModel.canClaimDailyReward()

        AlertDialog(
            onDismissRequest = { showCoinsModal = false },
            title = {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Color(0xFFFEF08A)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.MonetizationOn,
                            contentDescription = "سکه",
                            tint = Color(0xFFCA8A04),
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Text(
                        text = "کیف پول سکه 🪙",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            },
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Balance card
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(16.dp))
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFFCA8A04),
                                        Color(0xFFEAB308)
                                    )
                                )
                            )
                            .padding(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = "موجودی فعلی شما",
                                    fontSize = 12.sp,
                                    color = Color.White.copy(alpha = 0.9f)
                                )
                                Text(
                                    text = "$coins سکه",
                                    fontSize = 24.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = Color.White
                                )
                            }

                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = "ستاره",
                                tint = Color.White.copy(alpha = 0.8f),
                                modifier = Modifier.size(36.dp)
                            )
                        }
                    }

                    // Daily Reward Banner
                    Surface(
                        shape = RoundedCornerShape(14.dp),
                        color = if (canClaim) Color(0xFFDCFCE7) else MaterialTheme.colorScheme.surfaceVariant,
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (canClaim) Color(0xFF16A34A) else Color.Transparent
                        ),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "پاداش روزانه (۱۵ سکه)",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = if (canClaim) Color(0xFF15803D) else MaterialTheme.colorScheme.onSurface
                                )
                                Text(
                                    text = if (canClaim) "هم‌اکنون آماده دریافت است! 🎁" else "امروز دریافت شده (۲۴ ساعت بعدی)",
                                    fontSize = 11.sp,
                                    color = if (canClaim) Color(0xFF166534) else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                                )
                            }

                            Button(
                                onClick = {
                                    if (canClaim) {
                                        val success = viewModel.claimDailyReward()
                                        if (success) {
                                            Toast.makeText(context, "🎁 ۱۵ سکه دریافت شد!", Toast.LENGTH_SHORT).show()
                                        }
                                    }
                                },
                                enabled = canClaim,
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF16A34A)
                                ),
                                shape = RoundedCornerShape(10.dp),
                                modifier = Modifier.testTag("modal_claim_daily_button")
                            ) {
                                Text(
                                    text = if (canClaim) "دریافت" else "دریافت شد",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )
                            }
                        }
                    }

                    // How to earn tip
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedCornerShape(10.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                            .padding(10.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.EmojiEvents,
                            contentDescription = "مسابقات",
                            tint = Color(0xFFEAB308),
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "با پیروزی در مسابقات آنلاین و بازی‌های ۱ دقیقه‌ای ۱۰۰ سکه جایزه بگیرید!",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f),
                            lineHeight = 16.sp
                        )
                    }
                }
            },
            confirmButton = {
                TextButton(
                    onClick = { showCoinsModal = false },
                    modifier = Modifier.testTag("close_coin_modal_button")
                ) {
                    Text("بستن", fontWeight = FontWeight.Bold)
                }
            },
            containerColor = MaterialTheme.colorScheme.surface,
            shape = RoundedCornerShape(20.dp)
        )
    }
}
