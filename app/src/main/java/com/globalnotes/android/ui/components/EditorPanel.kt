package com.globalnotes.android.ui.components

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globalnotes.android.viewmodel.NoteViewModel
import com.globalnotes.android.ui.theme.*

@Composable
fun EditorPanel(
    modifier: Modifier = Modifier,
    viewModel: NoteViewModel,
    onBackClick: () -> Unit = {}
) {
    val note = viewModel.selectedNote
    
    // We use a local state for immediate typing feedback, then sync to VM
    var titleState by remember(note?.id) { mutableStateOf(TextFieldValue(note?.title ?: "")) }
    var contentState by remember(note?.id) { mutableStateOf(TextFieldValue(note?.content ?: "")) }

    val wordCount by remember {
        derivedStateOf {
            contentState.text.split("\\s+".toRegex()).filter { it.isNotEmpty() }.size
        }
    }

    val scrollState = rememberScrollState()

    if (note == null) {
        Box(modifier = modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("Select a note to start editing", color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
        return
    }

    Scaffold(
        topBar = { 
            EditorTopBar(
                onBackClick = onBackClick,
                isFavorite = note.isFavorite,
                onFavoriteToggle = { viewModel.toggleFavorite(note.id) },
                onDelete = { viewModel.deleteNote(note.id) }
            ) 
        },
        bottomBar = {
            Column {
                FormattingToolbar()
                Spacer(modifier = Modifier.navigationBarsPadding())
            }
        },
        containerColor = Color.Transparent,
        modifier = modifier.fillMaxSize().imePadding()
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(horizontal = 32.dp, vertical = 24.dp)
        ) {
            // Title
            BasicTextField(
                value = titleState,
                onValueChange = { 
                    titleState = it
                    viewModel.updateNoteTitle(note.id, it.text)
                },
                textStyle = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.onSurface
                ),
                modifier = Modifier.fillMaxWidth(),
                decorationBox = { innerTextField ->
                    if (titleState.text.isEmpty()) {
                        Text(
                            "Untitled", 
                            style = MaterialTheme.typography.headlineMedium, 
                            fontWeight = FontWeight.ExtraBold,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                    }
                    innerTextField()
                }
            )

            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(vertical = 12.dp)) {
                AppIcon(Icons.Default.AccessTime, contentDescription = null, size = 12.dp, tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    "Last edited ${note.time} • $wordCount words",
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f)
                )
            }

            Divider(modifier = Modifier.padding(vertical = 12.dp), thickness = 0.5.dp, color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))

            // Content
            BasicTextField(
                value = contentState,
                onValueChange = { 
                    contentState = it
                    viewModel.updateNoteContent(note.id, it.text)
                },
                textStyle = MaterialTheme.typography.bodyLarge.copy(
                    lineHeight = 28.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.8f)
                ),
                modifier = Modifier.fillMaxWidth().fillMaxHeight().padding(bottom = 100.dp),
                decorationBox = { innerTextField ->
                    if (contentState.text.isEmpty()) {
                        Text(
                            "Start typing your thoughts...", 
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                        )
                    }
                    innerTextField()
                }
            )
        }
    }
}

@Composable
private fun AppIcon(icon: ImageVector, contentDescription: String?, size: androidx.compose.ui.unit.`Dp`, tint: Color) {
    androidx.compose.material3.Icon(icon, contentDescription, modifier = Modifier.size(size), tint = tint)
}

@Composable
fun EditorTopBar(
    onBackClick: () -> Unit,
    isFavorite: Boolean,
    onFavoriteToggle: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth().height(64.dp).padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBackClick) { Icon(Icons.Default.ArrowBackIosNew, contentDescription = "Back", modifier = Modifier.size(18.dp)) }
            Spacer(modifier = Modifier.width(8.dp))
            Text("Edit Note", style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold)
        }
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onFavoriteToggle) { 
                AppIcon(
                    icon = if (isFavorite) Icons.Default.Star else Icons.Default.StarOutline, 
                    contentDescription = "Favorite", 
                    size = 20.dp,
                    tint = if (isFavorite) Color(0xFFFFD700) else MaterialTheme.colorScheme.onSurface
                ) 
            }
            IconButton(onClick = { }) { Icon(Icons.Outlined.Share, contentDescription = "Share") }
            IconButton(onClick = onDelete) { Icon(Icons.Outlined.Delete, contentDescription = "Delete") }
            
            Button(
                onClick = { },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(12.dp),
                contentPadding = PaddingValues(horizontal = 16.dp),
                modifier = Modifier.padding(start = 12.dp).height(36.dp)
            ) {
                Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp), tint = Color.White)
                Spacer(modifier = Modifier.width(8.dp))
                Text("AI", fontSize = 13.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
fun FormattingToolbar() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(0.5.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        LazyRow(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ToolbarIcon(Icons.Default.FormatBold)
                    ToolbarIcon(Icons.Default.FormatItalic)
                    ToolbarIcon(Icons.Default.FormatUnderlined)
                    ToolbarIcon(Icons.Default.FormatListBulleted)
                }
            }
            item { VerticalDivider(modifier = Modifier.height(20.dp), thickness = 1.dp) }
            item {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    ColorDot(Color.Black)
                    ColorDot(Color.Red)
                    ColorDot(Color.Blue)
                    ColorDot(Color(0xFF4CAF50))
                }
            }
        }
    }
}

@Composable
fun ToolbarIcon(icon: ImageVector) {
    Icon(icon, contentDescription = null, modifier = Modifier.size(20.dp), tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f))
}

@Composable
fun ColorDot(color: Color) {
    Box(modifier = Modifier.size(18.dp).clip(CircleShape).background(color).border(1.dp, Color.LightGray.copy(alpha = 0.3f), CircleShape))
}
