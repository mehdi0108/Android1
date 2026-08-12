package com.example.ui.screens

import android.app.Activity
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.SportsEsports
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.R
import com.example.ui.components.CoinBalanceHeaderBadge
import com.example.ui.components.DarkScenicLandscapeBrush
import com.example.ui.components.LightScenicLandscapeBrush
import com.example.ui.components.WhiteBorderCard
import com.example.viewmodel.AppViewModel

@Composable
fun HomeScreen(
    viewModel: AppViewModel,
    onNavigateToContent: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToAbout: () -> Unit,
    onNavigateToQuizGames: () -> Unit
) {
    val context = LocalContext.current
    val activity = context as? Activity
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()
    val isDark = userSettings.themeMode == "DARK"

    var showExitDialog by remember { mutableStateOf(false) }

    // Background linear gradient: Scenic Landscape Sky/Meadow Brush
    val screenBackgroundBrush = if (isDark) DarkScenicLandscapeBrush else LightScenicLandscapeBrush

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(screenBackgroundBrush),
        color = Color.Transparent
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(screenBackgroundBrush)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(horizontal = 24.dp, vertical = 24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Action Bar: Theme Switcher Toggle & Coin Balance Badge
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        IconButton(
                            onClick = {
                                val nextMode = if (isDark) "LIGHT" else "DARK"
                                viewModel.updateThemeMode(nextMode)
                                val msg = if (nextMode == "DARK") "تم شب فعال شد" else "تم روز فعال شد"
                                Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                            },
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.2f))
                                .testTag("home_theme_toggle")
                        ) {
                            Icon(
                                imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
                                contentDescription = "تغییر تم",
                                tint = Color.White
                            )
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(12.dp))
                                .background(Color.White.copy(alpha = 0.15f))
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = if (isDark) "حالت شب" else "حالت روز",
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )
                        }
                    }

                    // Persistent Coin Balance Header Indicator
                    CoinBalanceHeaderBadge(
                        viewModel = viewModel,
                        coins = userSettings.coins,
                        isDark = isDark
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Header & Title
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // App Logo Display
                    Box(
                        modifier = Modifier
                            .size(140.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.linearGradient(
                                    colors = listOf(
                                        Color(0xFF38BDF8),
                                        Color(0xFF0284C7)
                                    )
                                )
                            )
                            .padding(4.dp)
                            .clip(CircleShape)
                            .background(Color.White)
                            .padding(4.dp)
                            .clip(CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.app_icon_fg),
                            contentDescription = "لوگوی برنامه دنیای سرگرمی",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(20.dp))

                    Text(
                        text = "دنیای سرگرمی",
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF38BDF8),
                            fontSize = 32.sp
                        ),
                        textAlign = TextAlign.Center
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = "مجموعه‌ای خنده‌دار، سرگرم‌کننده و علمی با داستان‌ها و دانستنی‌های جذاب",
                        style = MaterialTheme.typography.bodyLarge.copy(
                            color = Color(0xFFCBD5E1),
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        ),
                        textAlign = TextAlign.Center
                    )
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Menu Options Cards
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Option 1: ورود به برنامه
                    MenuOptionCard(
                        title = "ورود به برنامه",
                        subtitle = "مشاهده جک‌ها، چیستان‌ها، داستان‌ها و دانستنی‌ها",
                        icon = Icons.Default.MenuBook,
                        isDark = isDark,
                        isGlassMode = true,
                        containerColor = if (isDark) Color(0xFF0F172A).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.28f),
                        titleColor = if (isDark) Color.White else Color(0xFF0F172A),
                        subtitleColor = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        iconBgColor = if (isDark) Color(0xFF0284C7).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.5f),
                        iconTint = Color(0xFF38BDF8),
                        borderColor = Color.White.copy(alpha = 0.8f),
                        testTag = "enter_app_button",
                        onClick = onNavigateToContent
                    )

                    // Option 2: مسابقه آنلاین و بازی‌ها
                    MenuOptionCard(
                        title = "مسابقه آنلاین و بازی‌ها 🎮",
                        subtitle = "چیستان ۱ دقیقه‌ای، حدس کلمه، اسم و فامیل، مار و پله، منچ و دوز با جایزه سکه‌ای",
                        icon = Icons.Default.SportsEsports,
                        isDark = isDark,
                        isGlassMode = true,
                        containerColor = if (isDark) Color(0xFF0F172A).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.28f),
                        titleColor = if (isDark) Color.White else Color(0xFF0F172A),
                        subtitleColor = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        iconBgColor = if (isDark) Color(0xFFCA8A04).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.5f),
                        iconTint = Color(0xFFFACC15),
                        borderColor = Color.White.copy(alpha = 0.8f),
                        testTag = "quiz_games_button",
                        onClick = onNavigateToQuizGames
                    )

                    // Option 2: تنظیمات
                    MenuOptionCard(
                        title = "تنظیمات",
                        subtitle = "تغییر سایز فونت، نوع فونت، رنگ، تم و حالت شیشه‌ای",
                        icon = Icons.Default.Settings,
                        isDark = isDark,
                        isGlassMode = true,
                        containerColor = if (isDark) Color(0xFF0F172A).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.28f),
                        titleColor = if (isDark) Color.White else Color(0xFF0F172A),
                        subtitleColor = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        iconBgColor = if (isDark) Color(0xFF0369A1).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.5f),
                        iconTint = Color(0xFF38BDF8),
                        borderColor = Color.White.copy(alpha = 0.8f),
                        testTag = "settings_button",
                        onClick = onNavigateToSettings
                    )

                    // Option 3: درباره برنامه
                    MenuOptionCard(
                        title = "درباره برنامه",
                        subtitle = "شناسنامه برنامه و راه ارتباطی با سازنده",
                        icon = Icons.Default.Info,
                        isDark = isDark,
                        isGlassMode = true,
                        containerColor = if (isDark) Color(0xFF0F172A).copy(alpha = 0.45f) else Color.White.copy(alpha = 0.28f),
                        titleColor = if (isDark) Color.White else Color(0xFF0F172A),
                        subtitleColor = if (isDark) Color(0xFFE2E8F0) else Color(0xFF1E293B),
                        iconBgColor = if (isDark) Color(0xFF475569).copy(alpha = 0.3f) else Color.White.copy(alpha = 0.5f),
                        iconTint = Color.White,
                        borderColor = Color.White.copy(alpha = 0.8f),
                        testTag = "about_button",
                        onClick = onNavigateToAbout
                    )
                }

                Spacer(modifier = Modifier.height(32.dp))

                // Option 4: خروج از برنامه
                OutlinedButton(
                    onClick = { showExitDialog = true },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(54.dp)
                        .testTag("exit_app_button"),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.outlinedButtonColors(
                        contentColor = Color(0xFFEF4444)
                    ),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFEF4444).copy(alpha = 0.6f))
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ExitToApp,
                            contentDescription = "خروج",
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "خروج از برنامه",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // Exit Dialog Confirmation
            if (showExitDialog) {
                AlertDialog(
                    onDismissRequest = { showExitDialog = false },
                    title = {
                        Text(
                            text = "خروج از برنامه",
                            fontWeight = FontWeight.Bold,
                            color = if (isDark) Color.White else Color(0xFF0F172A)
                        )
                    },
                    text = {
                        Text(
                            text = "آیا مطمئن هستید که می‌خواهید از برنامه خارج شوید؟",
                            color = if (isDark) Color(0xFFCBD5E1) else Color(0xFF475569)
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showExitDialog = false
                                activity?.finish()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFFDC2626)
                            )
                        ) {
                            Text("خروج", fontWeight = FontWeight.Bold, color = Color.White)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showExitDialog = false }) {
                            Text("انصراف", color = if (isDark) Color(0xFF94A3B8) else Color(0xFF64748B))
                        }
                    },
                    containerColor = if (isDark) Color(0xFF1E293B) else Color.White,
                    shape = RoundedCornerShape(20.dp)
                )
            }
        }
    }
}

@Composable
private fun MenuOptionCard(
    title: String,
    subtitle: String,
    icon: ImageVector,
    isDark: Boolean,
    isGlassMode: Boolean = false,
    containerColor: Color,
    titleColor: Color,
    subtitleColor: Color,
    iconBgColor: Color,
    iconTint: Color,
    borderColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    WhiteBorderCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(testTag),
        onClick = onClick,
        containerColor = containerColor,
        borderColor = borderColor,
        borderWidth = 2.dp,
        isGlassMode = isGlassMode
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(18.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(iconBgColor),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = title,
                        tint = iconTint,
                        modifier = Modifier.size(26.dp)
                    )
                }

                Spacer(modifier = Modifier.width(14.dp))

                Column {
                    Text(
                        text = title,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = titleColor
                    )

                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = subtitle,
                        fontSize = 12.sp,
                        color = subtitleColor
                    )
                }
            }

            Icon(
                imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                contentDescription = "ورود",
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}
