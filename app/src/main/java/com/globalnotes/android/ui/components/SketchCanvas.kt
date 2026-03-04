package com.globalnotes.android.ui.components

import android.content.Context
import android.graphics.Bitmap
import android.net.Uri
import android.view.MotionEvent
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.input.pointer.pointerInteropFilter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.FileProvider
import com.globalnotes.android.ui.theme.WorkspaceAmber
import java.io.File
import java.io.FileOutputStream

// ── Data model for a single stroke ───────────────────────────────────────────
private data class DrawingPath(
    val path: Path,
    val color: Color,
    val strokeWidth: Float,
    val brushType: BrushType
)

private enum class BrushType {
    PEN, MARKER, HIGHLIGHTER
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SketchCanvas(
    onDismiss: () -> Unit,
    onInsert: (Uri) -> Unit
) {
    val context = LocalContext.current
    val density = LocalDensity.current

    var paths by remember { mutableStateOf(mutableListOf<DrawingPath>()) }
    var undoneStack by remember { mutableStateOf(mutableListOf<DrawingPath>()) }
    var currentPath by remember { mutableStateOf<Path?>(null) }

    // Tool state
    var selectedBrush by remember { mutableStateOf(BrushType.PEN) }
    var selectedColor by remember { mutableStateOf(Color(0xFFB8860B)) } // Default amber
    var strokeWidth by remember { mutableStateOf(4f) }

    // Dotted background color
    val dotColor = Color(0xFFD4D4D4)
    val bgColor = Color(0xFFF5F5F5)

    // Function to save sketch as bitmap and return Uri
    val saveSketchAsImage: () -> Uri? = {
        try {
            val width = 1080
            val height = 1920
            val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
            val canvas = android.graphics.Canvas(bitmap)

            // Draw background
            val bgPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#F5F5F5")
            }
            canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), bgPaint)

            // Draw dotted pattern
            val dotPaint = android.graphics.Paint().apply {
                color = android.graphics.Color.parseColor("#D4D4D4")
                style = android.graphics.Paint.Style.FILL
            }
            val dotSpacing = 24 * density.density
            var y = dotSpacing
            while (y < height) {
                var x = dotSpacing
                while (x < width) {
                    canvas.drawCircle(x, y, 2f, dotPaint)
                    x += dotSpacing
                }
                y += dotSpacing
            }

            // Draw paths
            paths.forEach { drawingPath ->
                val paint = android.graphics.Paint().apply {
                    this.color = drawingPath.color.toArgb()
                    strokeWidth = drawingPath.strokeWidth
                    style = android.graphics.Paint.Style.STROKE
                    strokeCap = android.graphics.Paint.Cap.ROUND
                    strokeJoin = android.graphics.Paint.Join.ROUND
                    if (drawingPath.brushType == BrushType.HIGHLIGHTER) {
                        alpha = (255 * 0.4f).toInt()
                    }
                }
                canvas.drawPath(drawingPath.path.asAndroidPath(), paint)
            }

            // Save to file
            val file = File(context.cacheDir, "sketch_${System.currentTimeMillis()}.png")
            FileOutputStream(file).use { out ->
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
            }

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(bgColor)
            .statusBarsPadding()
    ) {
        // ── Top Bar ───────────────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.TopCenter),
            color = Color.White,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onDismiss) {
                    Icon(
                        Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color(0xFF333333)
                    )
                }
                Text(
                    "Add Sketch",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 18.sp
                    ),
                    color = Color(0xFF333333)
                )
                Button(
                    onClick = {
                        val uri = saveSketchAsImage()
                        if (uri != null) {
                            onInsert(uri)
                        }
                        onDismiss()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = WorkspaceAmber,
                        contentColor = Color.White
                    ),
                    shape = RoundedCornerShape(20.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp, vertical = 8.dp)
                ) {
                    Text("Insert", fontWeight = FontWeight.SemiBold)
                }
            }
        }

        // ── Canvas (with dotted background) ──────────────────────────────────
        Canvas(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 64.dp, bottom = 200.dp)
                .pointerInteropFilter { event ->
                    when (event.action) {
                        MotionEvent.ACTION_DOWN -> {
                            currentPath = Path().apply {
                                moveTo(event.x, event.y)
                            }
                            true
                        }
                        MotionEvent.ACTION_MOVE -> {
                            currentPath?.lineTo(event.x, event.y)
                            true
                        }
                        MotionEvent.ACTION_UP -> {
                            currentPath?.let {
                                paths.add(
                                    DrawingPath(
                                        path = it,
                                        color = selectedColor,
                                        strokeWidth = strokeWidth,
                                        brushType = selectedBrush
                                    )
                                )
                                undoneStack.clear()
                            }
                            currentPath = null
                            true
                        }
                        else -> false
                    }
                }
        ) {
            // Draw dotted grid background
            val dotSpacing = 24.dp.toPx()
            var y = dotSpacing
            while (y < size.height) {
                var x = dotSpacing
                while (x < size.width) {
                    drawCircle(
                        color = dotColor,
                        radius = 2f,
                        center = Offset(x, y)
                    )
                    x += dotSpacing
                }
                y += dotSpacing
            }

            // Draw all completed paths
            paths.forEach { drawingPath ->
                val alpha = if (drawingPath.brushType == BrushType.HIGHLIGHTER) 0.4f else 1f
                drawPath(
                    path = drawingPath.path,
                    color = drawingPath.color.copy(alpha = alpha),
                    style = Stroke(
                        width = drawingPath.strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }

            // Draw current path being drawn
            currentPath?.let { path ->
                val alpha = if (selectedBrush == BrushType.HIGHLIGHTER) 0.4f else 1f
                drawPath(
                    path = path,
                    color = selectedColor.copy(alpha = alpha),
                    style = Stroke(
                        width = strokeWidth,
                        cap = StrokeCap.Round,
                        join = StrokeJoin.Round
                    )
                )
            }
        }

        // ── Bottom Toolbar ───────────────────────────────────────────────────
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .align(Alignment.BottomCenter),
            color = Color.White,
            shadowElevation = 4.dp
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .padding(vertical = 16.dp)
            ) {
                // Row 1: Brush tools + Undo/Redo/Delete
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Pen tool
                        ToolButton(
                            icon = Icons.Default.Edit,
                            isSelected = selectedBrush == BrushType.PEN,
                            onClick = {
                                selectedBrush = BrushType.PEN
                                strokeWidth = 4f
                            }
                        )
                        // Marker tool
                        ToolButton(
                            icon = Icons.Default.Brush,
                            isSelected = selectedBrush == BrushType.MARKER,
                            onClick = {
                                selectedBrush = BrushType.MARKER
                                strokeWidth = 8f
                            }
                        )
                        // Highlighter tool
                        ToolButton(
                            icon = Icons.Default.Create,
                            isSelected = selectedBrush == BrushType.HIGHLIGHTER,
                            onClick = {
                                selectedBrush = BrushType.HIGHLIGHTER
                                strokeWidth = 12f
                            }
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Undo
                        IconButton(
                            onClick = {
                                if (paths.isNotEmpty()) {
                                    undoneStack.add(paths.removeAt(paths.lastIndex))
                                }
                            },
                            enabled = paths.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.Undo,
                                contentDescription = "Undo",
                                tint = if (paths.isNotEmpty()) Color(0xFF333333) else Color(0xFFCCCCCC)
                            )
                        }
                        // Redo
                        IconButton(
                            onClick = {
                                if (undoneStack.isNotEmpty()) {
                                    paths.add(undoneStack.removeAt(undoneStack.lastIndex))
                                }
                            },
                            enabled = undoneStack.isNotEmpty()
                        ) {
                            Icon(
                                Icons.Default.Redo,
                                contentDescription = "Redo",
                                tint = if (undoneStack.isNotEmpty()) Color(0xFF333333) else Color(0xFFCCCCCC)
                            )
                        }
                        // Delete/Clear
                        IconButton(
                            onClick = {
                                paths.clear()
                                undoneStack.clear()
                            }
                        ) {
                            Icon(
                                Icons.Default.Delete,
                                contentDescription = "Clear",
                                tint = Color(0xFFE53935)
                            )
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    color = Color(0xFFEEEEEE),
                    thickness = 1.dp
                )

                // Row 2: Color swatches
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val colors = listOf(
                        Color(0xFF333333),       // Black
                        Color(0xFFB8860B),       // Amber/Gold
                        Color(0xFF6B8E23),       // Olive green
                        Color(0xFFB22222),       // Dark red
                    )

                    colors.forEach { color ->
                        ColorSwatch(
                            color = color,
                            isSelected = selectedColor == color,
                            onClick = { selectedColor = color }
                        )
                    }

                    // Add color button (dashed circle)
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .border(
                                width = 2.dp,
                                color = Color(0xFFCCCCCC),
                                shape = CircleShape
                            )
                            .clickable { /* TODO: Color picker dialog */ },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            Icons.Default.Add,
                            contentDescription = "Add color",
                            tint = Color(0xFF999999),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Row 3: Stroke width slider
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Icon(
                        Icons.Default.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(12.dp),
                        tint = Color(0xFF999999)
                    )
                    Slider(
                        value = strokeWidth,
                        onValueChange = { strokeWidth = it },
                        valueRange = 2f..24f,
                        modifier = Modifier.weight(1f),
                        colors = SliderDefaults.colors(
                            thumbColor = WorkspaceAmber,
                            activeTrackColor = WorkspaceAmber
                        )
                    )
                    Icon(
                        Icons.Default.Circle,
                        contentDescription = null,
                        modifier = Modifier.size(24.dp),
                        tint = Color(0xFF333333)
                    )
                }
            }
        }
    }
}

@Composable
private fun ToolButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(44.dp)
            .clip(RoundedCornerShape(10.dp))
            .background(if (isSelected) Color(0xFFF0F0F0) else Color.Transparent)
            .clickable { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            icon,
            contentDescription = null,
            tint = if (isSelected) WorkspaceAmber else Color(0xFF666666),
            modifier = Modifier.size(24.dp)
        )
    }
}

@Composable
private fun ColorSwatch(
    color: Color,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(40.dp)
            .clip(CircleShape)
            .background(color)
            .then(
                if (isSelected) {
                    Modifier.border(3.dp, Color.White, CircleShape)
                        .border(4.dp, color, CircleShape)
                } else Modifier
            )
            .clickable { onClick() }
    )
}
