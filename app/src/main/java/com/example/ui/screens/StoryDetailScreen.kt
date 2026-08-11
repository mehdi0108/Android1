package com.example.ui.screens

import android.app.Activity
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.ActivityInfo
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.NavigateBefore
import androidx.compose.material.icons.automirrored.filled.NavigateNext
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material.icons.filled.Lightbulb
import androidx.compose.material.icons.filled.ScreenRotation
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.StayCurrentLandscape
import androidx.compose.material.icons.filled.StayCurrentPortrait
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.CoinBalanceHeaderBadge
import com.example.ui.components.ScenicGlassContainer
import com.example.ui.components.WhiteBorderCard
import com.example.ui.theme.FontUtils
import com.example.viewmodel.AppViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StoryDetailScreen(
    storyId: Int,
    viewModel: AppViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current
    val currentStory by viewModel.currentDetailStory.collectAsStateWithLifecycle()
    val allStories by viewModel.storiesList.collectAsStateWithLifecycle()
    val userSettings by viewModel.userSettings.collectAsStateWithLifecycle()

    var showAnswer by remember { mutableStateOf(false) }
    var isLandscape by remember { mutableStateOf(false) }

    val activity = remember(context) { context.findActivity() }

    LaunchedEffect(isLandscape) {
        activity?.requestedOrientation = if (isLandscape) {
            ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE
        } else {
            ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        }
    }

    DisposableEffect(Unit) {
        onDispose {
            activity?.requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED
        }
    }

    LaunchedEffect(storyId) {
        viewModel.loadStoryDetail(storyId)
        showAnswer = false
    }

    val story = currentStory

    val customFontFamily = FontUtils.getFontFamily(userSettings.fontFamily)
    val customFontColor = FontUtils.parseHexColor(
        userSettings.fontColorHex,
        defaultColor = MaterialTheme.colorScheme.onSurface
    )

    val isDark = userSettings.themeMode == "DARK"

    ScenicGlassContainer(
        isGlassMode = userSettings.isGlassMode,
        isDark = isDark
    ) {
        Scaffold(
            topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = story?.title ?: "جزئیات متن",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        maxLines = 1
                    )
                },
                navigationIcon = {
                    IconButton(
                        onClick = onBack,
                        modifier = Modifier.testTag("detail_back_button")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "بازگشت"
                        )
                    }
                },
                actions = {
                    CoinBalanceHeaderBadge(
                        viewModel = viewModel,
                        coins = userSettings.coins,
                        isDark = isDark
                    )

                    Spacer(modifier = Modifier.width(4.dp))

                    IconButton(
                        onClick = {
                            isLandscape = !isLandscape
                            val msg = if (isLandscape) "حالت افقی فعال شد" else "حالت عمودی فعال شد"
                            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                        },
                        modifier = Modifier.testTag("detail_orientation_button")
                    ) {
                        Icon(
                            imageVector = if (isLandscape) Icons.Default.StayCurrentLandscape else Icons.Default.StayCurrentPortrait,
                            contentDescription = "چرخش صفحه (عمودی/افقی)"
                        )
                    }
                    if (story != null) {
                        IconButton(
                            onClick = { viewModel.toggleFavorite(story) },
                            modifier = Modifier.testTag("detail_favorite_button")
                        ) {
                            Icon(
                                imageVector = if (story.isFavorite) Icons.Default.Favorite else Icons.Default.FavoriteBorder,
                                contentDescription = "علاقه‌مندی",
                                tint = if (story.isFavorite) Color(0xFFE11D48) else Color.White
                            )
                        }
                        IconButton(
                            onClick = {
                                shareText(context, story.title, story.content)
                            },
                            modifier = Modifier.testTag("detail_share_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.Share,
                                contentDescription = "اشتراک‌گذاری"
                            )
                        }
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = if (userSettings.isGlassMode) Color.White.copy(alpha = 0.22f) else MaterialTheme.colorScheme.primary,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White,
                    actionIconContentColor = Color.White
                )
            )
        },
        bottomBar = {
            if (story != null && allStories.isNotEmpty()) {
                val currentIndex = allStories.indexOfFirst { it.id == story.id }
                val prevStory = if (currentIndex > 0) allStories[currentIndex - 1] else null
                val nextStory = if (currentIndex in 0 until allStories.size - 1) allStories[currentIndex + 1] else null

                Surface(
                    color = if (userSettings.isGlassMode) Color.White.copy(alpha = 0.25f) else MaterialTheme.colorScheme.surface,
                    tonalElevation = if (userSettings.isGlassMode) 1.dp else 8.dp,
                    border = if (userSettings.isGlassMode) androidx.compose.foundation.BorderStroke(1.dp, Color.White.copy(alpha = 0.8f)) else androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        OutlinedButton(
                            onClick = {
                                prevStory?.let { viewModel.loadStoryDetail(it.id) }
                            },
                            enabled = prevStory != null,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                                    contentDescription = "قبلی"
                                )
                                Text("قبلی")
                            }
                        }

                        Button(
                            onClick = {
                                copyToClipboard(context, story.title, story.content)
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primaryContainer,
                                contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "کپی",
                                    modifier = Modifier.size(16.dp)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("کپی متن")
                            }
                        }

                        OutlinedButton(
                            onClick = {
                                nextStory?.let { viewModel.loadStoryDetail(it.id) }
                            },
                            enabled = nextStory != null,
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text("بعدی")
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                                    contentDescription = "بعدی"
                                )
                            }
                        }
                    }
                }
            }
        },
        containerColor = if (userSettings.isGlassMode) Color.Transparent else MaterialTheme.colorScheme.background
    ) { paddingValues ->
        if (story == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text(text = "در حال بارگذاری...")
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp)
            ) {
                WhiteBorderCard(
                    modifier = Modifier.fillMaxWidth(),
                    containerColor = MaterialTheme.colorScheme.surface,
                    borderColor = MaterialTheme.colorScheme.outline,
                    borderWidth = 2.dp,
                    isGlassMode = userSettings.isGlassMode
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // Title
                        Text(
                            text = story.title,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                fontFamily = customFontFamily,
                                fontSize = (userSettings.fontSizeSp + 4).sp
                            ),
                            color = customFontColor,
                            textAlign = TextAlign.Start
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        // Category Tag and Orientation Switcher Row
                        val categoryTitle = when (story.category) {
                            "NASRUDDIN" -> "داستان ملانصرالدین"
                            "SHAHNAMEH" -> "داستان شاهنامه"
                            "JOKE" -> "جک و لطیفه"
                            "RIDDLE" -> "چیستان"
                            "FACT" -> "دانستنی‌ها"
                            else -> "متن"
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(8.dp))
                                    .background(MaterialTheme.colorScheme.primaryContainer)
                                    .padding(horizontal = 12.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = categoryTitle,
                                    color = MaterialTheme.colorScheme.onPrimaryContainer,
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }

                            OutlinedButton(
                                onClick = {
                                    isLandscape = !isLandscape
                                    val msg = if (isLandscape) "حالت افقی فعال شد" else "حالت عمودی فعال شد"
                                    Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
                                },
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier
                                    .height(34.dp)
                                    .testTag("detail_orientation_chip")
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isLandscape) Icons.Default.StayCurrentLandscape else Icons.Default.StayCurrentPortrait,
                                        contentDescription = null,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = if (isLandscape) "صفحه افقی" else "صفحه عمودی",
                                        fontSize = 12.sp,
                                        fontWeight = FontWeight.Medium
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Full Content Text
                        Text(
                            text = story.content,
                            fontFamily = customFontFamily,
                            fontSize = userSettings.fontSizeSp.sp,
                            lineHeight = (userSettings.fontSizeSp * 1.6f).sp,
                            color = customFontColor,
                            textAlign = TextAlign.Start
                        )

                        // Riddle Answer Reveal Option
                        if (story.category == "RIDDLE" && !story.answer.isNullOrEmpty()) {
                            Spacer(modifier = Modifier.height(24.dp))

                            Button(
                                onClick = { showAnswer = !showAnswer },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFFD97706),
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(12.dp),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Lightbulb,
                                        contentDescription = "پاسخ",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = if (showAnswer) "مخفی کردن پاسخ" else "مشاهده پاسخ چیستان",
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }

                            AnimatedVisibility(
                                visible = showAnswer,
                                enter = fadeIn() + slideInVertically()
                            ) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(top = 12.dp)
                                        .clip(RoundedCornerShape(12.dp))
                                        .background(Color(0xFFFEF3C7))
                                        .padding(16.dp)
                                ) {
                                    Text(
                                        text = "پاسخ: ${story.answer}",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = (userSettings.fontSizeSp + 1).sp,
                                        color = Color(0xFF92400E)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
}

private fun shareText(context: Context, title: String, content: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, "$title\n\n$content\n\n- از برنامه جک و داستان ملانصرالدین")
    }
    context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری در شبکه های اجتماعی"))
}

private fun copyToClipboard(context: Context, title: String, content: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Story", "$title\n\n$content")
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "متن در حافظه کپی شد", Toast.LENGTH_SHORT).show()
}

private fun Context.findActivity(): Activity? {
    var ctx = this
    while (ctx is ContextWrapper) {
        if (ctx is Activity) return ctx
        ctx = ctx.baseContext
    }
    return null
}
