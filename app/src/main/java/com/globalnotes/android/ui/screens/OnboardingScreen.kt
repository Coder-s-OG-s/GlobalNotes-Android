package com.globalnotes.android.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globalnotes.android.R
import com.globalnotes.android.ui.theme.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

data class OnboardingSlide(
    val imageRes: Int,
    val titlePart1: String,
    val titlePart2: String,
    val description: String
)

@Composable
fun OnboardingScreen(onBack: () -> Unit = {}, onFinish: () -> Unit) {
    val slides = listOf(
        OnboardingSlide(
            imageRes = R.drawable.ai_writing,
            titlePart1 = "AI-powered",
            titlePart2 = "writing",
            description = "Drafts, summaries, mail — AI does the heavy lifting so you can focus on ideas."
        ),
        OnboardingSlide(
            imageRes = R.drawable.smart_org,
            titlePart1 = "Smart",
            titlePart2 = "organization",
            description = "Folders, tags, and intelligent linking keep your workspace perfectly structured."
        ),
        OnboardingSlide(
            imageRes = R.drawable.search_sync,
            titlePart1 = "Universal",
            titlePart2 = "search & sync",
            description = "Find anything in seconds and access your thoughts across all your devices."
        )
    )

    val pagerState = rememberPagerState(pageCount = { slides.size })
    val scope = rememberCoroutineScope()

    // Auto-scroll logic
    LaunchedEffect(Unit) {
        while (true) {
            delay(4000)
            if (pagerState.currentPage < slides.size - 1) {
                pagerState.animateScrollToPage(pagerState.currentPage + 1)
            } else {
                pagerState.animateScrollToPage(0)
            }
        }
    }

    Surface(
        color = OnboardingBackground,
        modifier = Modifier.fillMaxSize()
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Top Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = OnboardingTitle,
                        modifier = Modifier.size(24.dp)
                    )
                }
                TextButton(onClick = onFinish) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnboardingSubtitle,
                    )
                }
            }

            // Pager content
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f)
            ) { index ->
                val slide = slides[index]
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Spacer(modifier = Modifier.height(40.dp))
                    
                    // Image Section
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.size(320.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(280.dp)
                                .clip(RoundedCornerShape(80.dp))
                                .background(Color(0xFFF7F2EB))
                        )
                        Image(
                            painter = painterResource(id = slide.imageRes),
                            contentDescription = null,
                            modifier = Modifier
                                .size(240.dp)
                                .clip(RoundedCornerShape(32.dp)),
                            contentScale = ContentScale.Crop
                        )
                    }

                    Spacer(modifier = Modifier.height(32.dp))

                    // Text Content
                    Text(
                        text = slide.titlePart1,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontSize = 32.sp
                        ),
                        color = OnboardingTitle
                    )
                    Text(
                        text = slide.titlePart2,
                        style = MaterialTheme.typography.headlineLarge.copy(
                            fontFamily = FontFamily.Serif,
                            fontWeight = FontWeight.Bold,
                            fontStyle = FontStyle.Italic,
                            fontSize = 32.sp
                        ),
                        color = OnboardingButton
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = slide.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = OnboardingSubtitle,
                        textAlign = TextAlign.Center,
                        lineHeight = 24.sp,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }

            // Page Indicator
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                repeat(slides.size) { index ->
                    val isActive = pagerState.currentPage == index
                    if (isActive) {
                        Box(
                            modifier = Modifier
                                .width(32.dp)
                                .height(8.dp)
                                .clip(RoundedCornerShape(4.dp))
                                .background(OnboardingDotActive)
                        )
                    } else {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(OnboardingDotInactive)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Buttons
            Button(
                onClick = onFinish,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(64.dp),
                shape = RoundedCornerShape(32.dp),
                colors = ButtonDefaults.buttonColors(containerColor = OnboardingButton)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = if (pagerState.currentPage == slides.size - 1) "Get Started" else "Next",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = null,
                        tint = Color.White
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Text(
                text = "Sign In",
                style = MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.SemiBold),
                color = OnboardingTitle,
                modifier = Modifier.clickable { }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
