package com.globalnotes.android.ui.screens

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globalnotes.android.ui.theme.*

@Composable
fun LoadingScreen(onFinish: () -> Unit) {
    var progress by remember { mutableStateOf(0f) }
    
    // Simulate loading progress
    LaunchedEffect(Unit) {
        val duration = 3000L
        val startTime = System.currentTimeMillis()
        while (System.currentTimeMillis() - startTime < duration) {
            progress = ((System.currentTimeMillis() - startTime).toFloat() / duration).coerceIn(0f, 1f)
            kotlinx.coroutines.delay(16)
        }
        progress = 1f
        kotlinx.coroutines.delay(500)
        onFinish()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.radialGradient(
                    colors = listOf(LoadingBackgroundStart, LoadingBackgroundEnd),
                    radius = 2000f
                )
            ),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // App Logo Placeholder (with glow)
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.padding(bottom = 48.dp)
            ) {
                // Glow effect
                Box(
                    modifier = Modifier
                        .size(120.dp)
                        .blur(40.dp)
                        .background(LoadingAccent.copy(alpha = 0.2f), RoundedCornerShape(32.dp))
                )
                
                // Icon Box
                Surface(
                    modifier = Modifier
                        .size(110.dp)
                        .clip(RoundedCornerShape(28.dp)),
                    color = Color(0xFF32261F).copy(alpha = 0.8f)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Outlined.Public,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = LoadingAccent
                        )
                        Icon(
                            imageVector = Icons.Outlined.Search,
                            contentDescription = null,
                            modifier = Modifier.size(24.dp).padding(start = 24.dp, top = 24.dp),
                            tint = LoadingAccent
                        )
                    }
                }
            }

            // App Name
            Text(
                text = "Global Notes",
                style = MaterialTheme.typography.headlineLarge.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 42.sp,
                    letterSpacing = 1.sp
                ),
                color = LoadingText
            )
            Text(
                text = "Workspace",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontFamily = FontFamily.Serif,
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp,
                    letterSpacing = 2.sp
                ),
                color = LoadingText,
                modifier = Modifier.padding(bottom = 12.dp)
            )

            // Tagline
            Text(
                text = "Organize ideas. Work smarter.",
                style = MaterialTheme.typography.bodyLarge,
                color = LoadingText.copy(alpha = 0.6f),
                modifier = Modifier.padding(bottom = 100.dp)
            )

            // Progress Section
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 48.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Text(
                        text = "LOADING",
                        style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                        color = LoadingAccent,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.labelLarge,
                        color = LoadingText.copy(alpha = 0.5f)
                    )
                }
                
                LinearProgressIndicator(
                    progress = progress,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = LoadingAccent,
                    trackColor = Color.White.copy(alpha = 0.1f)
                )
            }
        }
        
        // Version info
        Text(
            text = "VERSION 2.4.0",
            style = MaterialTheme.typography.labelSmall,
            color = LoadingText.copy(alpha = 0.3f),
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 48.dp)
        )
    }
}
