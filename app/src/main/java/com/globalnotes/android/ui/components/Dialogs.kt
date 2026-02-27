package com.globalnotes.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareSheet(onDismiss: () -> Unit) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = rememberModalBottomSheetState()
    ) {
        Column(modifier = Modifier.padding(16.dp).padding(bottom = 32.dp)) {
            Text("Share Note", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
            
            ShareActionItem("Share on WhatsApp", Icons.Default.Chat, Color(0xFF25D366))
            ShareActionItem("Share via Email", Icons.Default.Email, Color(0xFFEA4335))
            ShareActionItem("Copy Text", Icons.Default.ContentCopy, Color.Gray)
            
            Spacer(modifier = Modifier.height(24.dp))
            
            // QR Code Placeholder
            Box(
                modifier = Modifier.size(150.dp).align(Alignment.CenterHorizontally).background(Color.White).border(1.dp, Color.LightGray),
                contentAlignment = Alignment.Center
            ) {
                Icon(Icons.Default.QrCode2, contentDescription = null, modifier = Modifier.size(100.dp), tint = Color.Black)
            }
        }
    }
}

@Composable
fun ShareActionItem(text: String, icon: androidx.compose.ui.graphics.vector.ImageVector, color: Color) {
    Row(
        modifier = Modifier.fillMaxWidth().clickable { }.padding(vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, contentDescription = null, tint = color)
        Spacer(modifier = Modifier.width(16.dp))
        Text(text)
    }
}

@Composable
fun RecordAudioDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(24.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Text("Record Audio", style = MaterialTheme.typography.titleMedium)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                // Animated Waveform Placeholder
                Row(modifier = Modifier.height(40.dp), verticalAlignment = Alignment.CenterVertically) {
                    repeat(10) { 
                        Box(modifier = Modifier.width(4.dp).height(20.dp).background(MaterialTheme.colorScheme.primary, CircleShape).padding(horizontal = 2.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                    }
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text("0:05", style = MaterialTheme.typography.bodyLarge, color = MaterialTheme.colorScheme.primary)
                
                Spacer(modifier = Modifier.height(32.dp))
                
                IconButton(
                    onClick = { },
                    modifier = Modifier.size(64.dp).background(Color.Red, CircleShape)
                ) {
                    Icon(Icons.Default.Stop, contentDescription = "Stop", tint = Color.White)
                }
                
                Spacer(modifier = Modifier.height(32.dp))
                
                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    TextButton(onClick = onDismiss) { Text("Cancel") }
                    Button(onClick = { }) { Text("Insert Audio") }
                }
            }
        }
    }
}

@Composable
fun InsertShapeDialog(onDismiss: () -> Unit) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(shape = RoundedCornerShape(16.dp), color = MaterialTheme.colorScheme.surface) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("Insert Shape", style = MaterialTheme.typography.titleMedium, modifier = Modifier.padding(bottom = 16.dp))
                
                val shapes = listOf(
                    Icons.Default.Rectangle to "Rectangle",
                    Icons.Default.Circle to "Circle",
                    Icons.Default.ChangeHistory to "Triangle",
                    Icons.Default.Star to "Star",
                    Icons.Default.ArrowForward to "Arrow",
                    Icons.Default.HorizontalRule to "Line"
                )
                
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.height(200.dp)
                ) {
                    items(shapes) { (icon, name) ->
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            modifier = Modifier.clickable { }.padding(12.dp)
                        ) {
                            Icon(icon, contentDescription = name, modifier = Modifier.size(32.dp))
                            Text(name, fontSize = 10.sp, modifier = Modifier.padding(top = 4.dp))
                        }
                    }
                }
            }
        }
    }
}
