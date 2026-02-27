package com.globalnotes.android.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun NotesListPanel(
    modifier: Modifier = Modifier,
    onNoteClick: () -> Unit = {},
    onMenuClick: () -> Unit = {}
) {
    Column(
        modifier = modifier
            .fillMaxHeight()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Top Header
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("All Notes", style = MaterialTheme.typography.titleLarge)
            IconButton(onClick = { /* New Note */ }) {
                Icon(Icons.Default.AddCircle, contentDescription = "New", tint = MaterialTheme.colorScheme.primary)
            }
        }

        // Search Bar
        OutlinedTextField(
            value = "",
            onValueChange = {},
            modifier = Modifier.padding(horizontal = 16.dp).fillMaxWidth().height(48.dp),
            placeholder = { Text("Search notes...", fontSize = 14.sp) },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(18.dp)) },
            shape = RoundedCornerShape(12.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedBorderColor = Color.Transparent,
                focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
            )
        )

        // Filter Chips
        LazyRow(
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(listOf("All", "Work", "Personal", "Ideas", "To-do")) { filter ->
                FilterChip(
                    selected = filter == "All",
                    onClick = { },
                    label = { Text(filter, fontSize = 12.sp) },
                    shape = RoundedCornerShape(16.dp),
                    colors = FilterChipDefaults.filterChipColors(
                        selectedContainerColor = MaterialTheme.colorScheme.primary,
                        selectedLabelColor = Color.White
                    ),
                    border = null
                )
            }
        }

        // Notes List
        LazyColumn(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(1.dp)
        ) {
            items(dummyNotes) { note ->
                NoteRow(note, onNoteClick)
            }
        }
    }
}

@Composable
fun NoteRow(note: DummyNote, onClick: () -> Unit) {
    val isSelected = note.title == "Product Vision 2024" // Just for visual demo

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else Color.Transparent)
            .clickable { onClick() }
            .padding(16.dp)
    ) {
        Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
            Text(
                text = note.title,
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = if (isSelected) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground
            )
            Text(note.time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.4f))
        }
        Text(
            text = note.preview,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(top = 4.dp)
        )
    }
}

data class DummyNote(val title: String, val preview: String, val time: String)
val dummyNotes = listOf(
    DummyNote("Product Vision 2024", "The future of document collaboration is here. We need to focus on...", "10:30 AM"),
    DummyNote("Marketing Strategy", "Campaign themes: Simplicity, Speed, Security. Focus on enterprise...", "Yesterday"),
    DummyNote("Groceries", "Milk, Eggs, Bread, Avocados, Coffee beans, Pasta, Sauce...", "Feb 24"),
    DummyNote("Mobile App Feedback", "Users want more offline features and better tablet support...", "Feb 22"),
    DummyNote("Meeting Notes", "Discussed the new roadmap and sync issues with Supabase...", "Feb 20")
)
