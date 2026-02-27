package com.globalnotes.android.ui.components

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mail
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MailWriterPanel(
    isVisible: Boolean,
    onClose: () -> Unit
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { it }) + fadeIn(),
        exit = slideOutHorizontally(targetOffsetX = { it }) + fadeOut(),
        modifier = Modifier.fillMaxHeight().width(320.dp).padding(16.dp)
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            shape = RoundedCornerShape(16.dp),
            shadowElevation = 12.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Mail Writer", style = MaterialTheme.typography.titleMedium)
                    IconButton(onClick = onClose) { Icon(Icons.Default.Close, contentDescription = "Close") }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
                    shape = RoundedCornerShape(12.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Draft Preview", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            "Subject: Product Vision 2024\n\nHi team, I wanted to share my latest thoughts on the product vision for the upcoming year...",
                            style = MaterialTheme.typography.bodySmall,
                            maxLines = 6
                        )
                    }
                }

                Spacer(modifier = Modifier.weight(1f))

                Button(
                    onClick = { },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEA4335)) // Gmail Red
                ) {
                    Icon(Icons.Default.Mail, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Draft in Gmail")
                }
            }
        }
    }
}
