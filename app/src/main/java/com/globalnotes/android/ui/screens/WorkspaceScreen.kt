package com.globalnotes.android.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import coil.compose.AsyncImage
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.globalnotes.android.viewmodel.Note
import com.globalnotes.android.viewmodel.NoteViewModel
import com.globalnotes.android.ui.theme.*

private data class Category(val label: String, val icon: ImageVector)

private val categories = listOf(
    Category("All", Icons.Filled.Apps),
    Category("Work", Icons.Filled.Work),
    Category("Personal", Icons.Filled.Person),
    Category("Writing", Icons.Filled.Create),
    Category("Design", Icons.Filled.Palette),
    Category("Travel", Icons.Filled.Flight)
)

@Composable
fun WorkspaceScreen(
    viewModel: NoteViewModel,
    photoUrl: String? = null,
    userDisplayName: String? = null,
    onNoteClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    var selectedCategory by remember { mutableStateOf("All") }

    val allNotes = viewModel.notes.filter { !it.isArchived }

    val displayedNotes = remember(selectedCategory, allNotes) {
        if (selectedCategory == "All") allNotes
        else allNotes.filter { it.folder.equals(selectedCategory, ignoreCase = true) }
    }

    val wordsDisplay = remember(allNotes) {
        val total = allNotes.sumOf { note ->
            (note.title + " " + note.content).split("\\s+".toRegex()).count { it.isNotBlank() }
        }
        if (total >= 1000) "${total / 1000}k" else total.toString()
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(WorkspaceBg)
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding(),
            contentPadding = PaddingValues(
                start = 12.dp, end = 12.dp, top = 0.dp, bottom = 100.dp
            ),
            horizontalArrangement = Arrangement.spacedBy(10.dp),
            verticalItemSpacing = 10.dp
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                WorkspaceHeader(
                    photoUrl        = photoUrl,
                    userDisplayName = userDisplayName,
                    onMenuClick     = onMenuClick
                )
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                CategoryChips(
                    selected = selectedCategory,
                    onSelect = { selectedCategory = it }
                )
            }

            item(span = StaggeredGridItemSpan.FullLine) {
                StatsRow(noteCount = allNotes.size, wordCount = wordsDisplay)
            }

            items(displayedNotes, key = { it.id }) { note ->
                WorkspaceNoteCard(
                    note = note,
                    onClick = {
                        viewModel.selectNote(note.id)
                        onNoteClick()
                    }
                )
            }
        }

        FloatingActionButton(
            onClick = {
                viewModel.addNote()
                onNoteClick()
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 20.dp, bottom = 28.dp)
                .navigationBarsPadding(),
            containerColor = WorkspaceAmber,
            contentColor = Color.White,
            shape = RoundedCornerShape(20.dp)
        ) {
            Icon(
                Icons.Filled.Edit,
                contentDescription = "New Note",
                modifier = Modifier.size(22.dp)
            )
        }
    }
}

@Composable
private fun WorkspaceHeader(
    photoUrl: String?,
    userDisplayName: String?,
    onMenuClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = onMenuClick, modifier = Modifier.size(40.dp)) {
            Icon(
                Icons.Filled.Menu,
                contentDescription = "Menu",
                tint = WorkspaceTextPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        Text(
            "My Workspace",
            modifier = Modifier.weight(1f),
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 20.sp
            ),
            color = WorkspaceTextPrimary,
            textAlign = TextAlign.Center
        )

        IconButton(onClick = {}, modifier = Modifier.size(40.dp)) {
            Icon(
                Icons.Filled.Search,
                contentDescription = "Search",
                tint = WorkspaceTextPrimary,
                modifier = Modifier.size(22.dp)
            )
        }

        if (photoUrl != null) {
            AsyncImage(
                model            = photoUrl,
                contentDescription = "Profile",
                contentScale     = ContentScale.Crop,
                modifier         = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
            )
        } else {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFD4C5B0)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text  = userDisplayName?.firstOrNull()?.uppercaseChar()?.toString() ?: "U",
                    style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF8C7B6A)
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))
    }
}

@Composable
private fun CategoryChips(selected: String, onSelect: (String) -> Unit) {
    Row(
        modifier = Modifier
            .horizontalScroll(rememberScrollState())
            .padding(horizontal = 12.dp)
            .padding(bottom = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        categories.forEach { category ->
            val isSelected = category.label == selected
            Surface(
                onClick = { onSelect(category.label) },
                shape = RoundedCornerShape(50),
                color = if (isSelected) WorkspaceAmber else WorkspaceCardBg,
                shadowElevation = if (isSelected) 0.dp else 1.dp,
                modifier = Modifier.height(36.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        category.icon,
                        contentDescription = null,
                        tint = if (isSelected) Color.White else WorkspaceTextSecondary,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        category.label,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                        ),
                        color = if (isSelected) Color.White else WorkspaceTextSecondary
                    )
                }
            }
        }
    }
}

@Composable
private fun StatsRow(noteCount: Int, wordCount: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp)
            .padding(bottom = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        StatCard(value = noteCount.toString(), label = "NOTES", modifier = Modifier.weight(1f))
        StatCard(value = wordCount, label = "WORDS", modifier = Modifier.weight(1f))
        StatCard(value = "2m", label = "ACTIVE", modifier = Modifier.weight(1f))
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(16.dp),
        color = WorkspaceCardBg,
        shadowElevation = 1.dp
    ) {
        Column(
            modifier = Modifier.padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                value,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = WorkspaceAmber,
                    fontSize = 22.sp
                )
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = WorkspaceTextSecondary,
                    letterSpacing = 1.sp
                )
            )
        }
    }
}

@Composable
private fun WorkspaceNoteCard(note: Note, onClick: () -> Unit) {
    val tagColor = tagColorFor(note.folder)

    Surface(
        onClick = onClick,
        shape = RoundedCornerShape(16.dp),
        color = WorkspaceCardBg,
        shadowElevation = 1.dp,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            // Colored accent bar at top
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(5.dp)
                    .background(
                        tagColor,
                        RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                    )
            )

            Column(modifier = Modifier.padding(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Text(
                        text = if (note.title.isEmpty()) "Untitled" else note.title,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold
                        ),
                        color = WorkspaceTextPrimary,
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(end = 4.dp)
                    )
                    Icon(
                        Icons.Filled.Star,
                        contentDescription = null,
                        tint = if (note.isFavorite) WorkspaceAmber
                               else WorkspaceTextSecondary.copy(alpha = 0.35f),
                        modifier = Modifier.size(16.dp)
                    )
                }

                if (note.content.isNotEmpty()) {
                    Text(
                        text = note.content,
                        style = MaterialTheme.typography.bodySmall,
                        color = WorkspaceTextSecondary,
                        maxLines = 4,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(top = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = tagColor.copy(alpha = 0.12f)
                    ) {
                        Text(
                            text = note.folder,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Medium
                            ),
                            color = tagColor,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                        )
                    }
                    Text(
                        text = note.time,
                        style = MaterialTheme.typography.labelSmall,
                        color = WorkspaceTextSecondary
                    )
                }
            }
        }
    }
}

private fun tagColorFor(folder: String): Color = when (folder.lowercase()) {
    "work"     -> WorkspaceTagWork
    "personal" -> WorkspaceTagPersonal
    "writing"  -> WorkspaceTagWriting
    "design"   -> WorkspaceTagDesign
    "travel"   -> WorkspaceTagTravel
    else       -> WorkspaceTextSecondary
}
