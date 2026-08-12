package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.content.Intent
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
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
import com.example.ui.components.ScenicGlassContainer
import com.example.ui.components.WhiteBorderCard
import com.example.ui.theme.FontUtils
import com.example.ui.theme.THEME_LIGHT_CYAN
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

    val activeTheme = userSettings.themeMode

    ScenicGlassContainer(
        themeMode = activeTheme
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
                val currentIndex = allStories.indexOfFirst { it.id == story.id }
                val prevStory = if (currentIndex > 0) allStories[currentIndex - 1] else null
                val nextStory = if (currentIndex in 0 until allStories.size - 1) allStories[currentIndex + 1] else null

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues)
                        .verticalScroll(rememberScrollState())
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Center Image section with Next and Previous buttons beside it
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Previous Button (قبلی)
                        OutlinedButton(
                            onClick = { prevStory?.let { viewModel.loadStoryDetail(it.id) } },
                            enabled = prevStory != null,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("prev_story_button"),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.NavigateNext,
                                    contentDescription = "قبلی",
                                    modifier = Modifier.size(20.dp)
                                )
                                Spacer(modifier = Modifier.width(2.dp))
                                Text("قبلی", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                            }
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Middle Image Badge (تصویر وسط صفحه)
                        Box(
                            modifier = Modifier
                                .size(88.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Color(0xFF38BDF8), Color(0xFF0284C7))
                                    )
                                )
                                .padding(3.dp)
                                .clip(CircleShape)
                                .background(Color.White)
                                .padding(2.dp)
                                .clip(CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Image(
                                painter = painterResource(id = R.drawable.app_icon_fg),
                                contentDescription = "تصویر داستان",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(10.dp))

                        // Next Button (بعدی)
                        Button(
                            onClick = { nextStory?.let { viewModel.loadStoryDetail(it.id) } },
                            enabled = nextStory != null,
                            shape = RoundedCornerShape(14.dp),
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp)
                                .testTag("next_story_button"),
                            contentPadding = PaddingValues(horizontal = 4.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.Center
                            ) {
                                Text("بعدی", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Spacer(modifier = Modifier.width(2.dp))
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.NavigateBefore,
                                    contentDescription = "بعدی",
                                    modifier = Modifier.size(20.dp)
                                )
                            }
                        }
                    }

                    // Story Text Card Container (کادر نوشته)
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
                            // Category Tag
                            val categoryTitle = when (story.category) {
                                "EDUCATIONAL" -> "داستان آموزنده"
                                "NASRUDDIN" -> "داستان ملانصرالدین"
                                "SHAHNAMEH" -> "داستان شاهنامه"
                                "JOKE" -> "جک و لطیفه"
                                "RIDDLE" -> "چیستان"
                                "FACT" -> "دانستنی‌ها"
                                else -> "متن"
                            }
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

                            Spacer(modifier = Modifier.height(14.dp))

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
                                Spacer(modifier = Modifier.height(20.dp))

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

                            Spacer(modifier = Modifier.height(24.dp))

                            // Copy Text Button INSIDE the text card (داخل کادر نوشته)
                            Button(
                                onClick = {
                                    copyToClipboard(context, story.title, story.content)
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                                    contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                                ),
                                shape = RoundedCornerShape(14.dp),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("detail_copy_text_inside_card")
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.ContentCopy,
                                        contentDescription = "کپی متن",
                                        modifier = Modifier.size(18.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = "کپی متن",
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 14.sp
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

private fun shareText(context: Context, title: String, content: String) {
    val shareIntent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, title)
        putExtra(Intent.EXTRA_TEXT, "$title\n\n$content\n\n- از برنامه دنیای سرگرمی")
    }
    context.startActivity(Intent.createChooser(shareIntent, "اشتراک‌گذاری در شبکه های اجتماعی"))
}

private fun copyToClipboard(context: Context, title: String, content: String) {
    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
    val clip = ClipData.newPlainText("Story", "$title\n\n$content")
    clipboard.setPrimaryClip(clip)
    Toast.makeText(context, "متن در حافظه کپی شد", Toast.LENGTH_SHORT).show()
}
