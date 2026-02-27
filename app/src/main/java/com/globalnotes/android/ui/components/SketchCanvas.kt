package com.globalnotes.android.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

@Composable
fun SketchCanvas(onDismiss: () -> Unit) {
    var paths by remember { mutableStateOf(mutableListOf<Path>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    Box(modifier = Modifier.fillMaxSize().background(Color.White)) {
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .pointerInput(Unit) {
                    detectDragGestures(
                        onDragStart = { offset ->
                            currentPath = Path().apply { moveTo(offset.x, offset.y) }
                        },
                        onDrag = { change, dragAmount ->
                            currentPath?.lineTo(change.position.x, change.position.y)
                            // Force recomposition
                            val path = currentPath
                            currentPath = null
                            currentPath = path
                        },
                        onDragEnd = {
                            currentPath?.let { paths.add(it) }
                            currentPath = null
                        }
                    )
                }
        ) {
            paths.forEach { path ->
                drawPath(path, Color.Black, style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
            currentPath?.let { path ->
                drawPath(path, Color.Black, style = Stroke(width = 4f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            }
        }

        // Toolbar
        Surface(
            modifier = Modifier.fillMaxWidth().align(Alignment.TopCenter),
            color = Color.Transparent
        ) {
            Row(modifier = Modifier.padding(16.dp), horizontalArrangement = Arrangement.SpaceBetween) {
                Row {
                    IconButton(onClick = { paths.clear() }) { Icon(Icons.Default.Delete, contentDescription = "Clear") }
                    IconButton(onClick = { /* Undo */ }) { Icon(Icons.Default.Undo, contentDescription = "Undo") }
                }
                Row {
                    Button(onClick = onDismiss, colors = ButtonDefaults.buttonColors(containerColor = Color.LightGray)) { Text("Cancel", color = Color.Black) }
                    Spacer(modifier = Modifier.width(8.dp))
                    Button(onClick = { }) { Text("Insert") }
                }
            }
        }
    }
}
