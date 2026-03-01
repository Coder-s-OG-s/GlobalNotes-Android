package com.globalnotes.android.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import androidx.compose.ui.graphics.Color
import com.globalnotes.android.ui.theme.*
import java.util.UUID

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val content: String,
    val time: String,
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val folder: String = "Personal",
    val accentColor: Color = Color(0xFF2196F3),
    val backgroundColor: Color = Color.White
)

class NoteViewModel : ViewModel() {
    private val _notes = mutableStateListOf<Note>(
        Note(
            title = "Project Alpha",
            content = "Meeting notes regarding the new Q4 roadmap. Key decisions were made about product direction and team responsibilities.",
            time = "2m ago", folder = "Work",
            accentColor = Color(0xFF2196F3)
        ),
        Note(
            title = "Novel Ideas",
            content = "The protagonist discovers a hidden door in the old library. It leads to a world untouched by time...",
            time = "1d ago", folder = "Writing",
            accentColor = Color(0xFFD97706)
        ),
        Note(
            title = "Moodboard",
            content = "Inspiration for the living room redesign. Focus on warm neutrals and natural materials.",
            time = "1h ago", folder = "Design",
            isFavorite = true, accentColor = Color(0xFFD97706)
        ),
        Note(
            title = "Q3 Review",
            content = "Prepare slide deck for board meeting.",
            time = "2d ago", folder = "Work",
            accentColor = Color(0xFF5856D6)
        ),
        Note(
            title = "Grocery List",
            content = "• Milk\n• Eggs\n• Artisan bread\n• Espresso beans",
            time = "3h ago", folder = "Personal",
            accentColor = Color(0xFF30B0C7)
        ),
        Note(
            title = "Trip Plans",
            content = "Hiking trails in the Pacific Northwest. Check permits for Mt Rainier and Olympic NP.",
            time = "5d ago", folder = "Travel",
            accentColor = Color(0xFF34C759)
        ),
        Note(
            title = "Marketing Strategy",
            content = "Campaign themes: Simplicity, Speed, Trust. Q4 launch plan with social media focus and influencer partnerships.",
            time = "Yesterday", folder = "Work",
            accentColor = Color(0xFF007AFF)
        ),
        Note(
            title = "Meeting Notes",
            content = "Discussed the new roadmap for Q1. Action items assigned to team leads. Follow up by Friday.",
            time = "Feb 20", folder = "Work",
            accentColor = Color(0xFFFF9500)
        )
    )
    val notes: List<Note> get() = _notes

    var selectedNoteId by mutableStateOf<String?>(_notes[0].id)
        private set

    var currentFilter by mutableStateOf("All Notes")
        private set

    val selectedNote by derivedStateOf {
        _notes.find { it.id == selectedNoteId }
    }

    val filteredNotes by derivedStateOf {
        when (currentFilter) {
            "All Notes" -> _notes.filter { !it.isArchived }
            "Favorites" -> _notes.filter { it.isFavorite && !it.isArchived }
            "Archived" -> _notes.filter { it.isArchived }
            else -> _notes.filter { it.folder == currentFilter && !it.isArchived }
        }
    }

    fun selectNote(id: String) {
        selectedNoteId = id
    }

    fun updateFilter(filter: String) {
        currentFilter = filter
    }

    fun updateNoteContent(id: String, newContent: String) {
        val index = _notes.indexOfFirst { it.id == id }
        if (index != -1) {
            _notes[index] = _notes[index].copy(content = newContent)
        }
    }

    fun updateNoteTitle(id: String, newTitle: String) {
        val index = _notes.indexOfFirst { it.id == id }
        if (index != -1) {
            _notes[index] = _notes[index].copy(title = newTitle)
        }
    }

    fun toggleFavorite(id: String) {
        val index = _notes.indexOfFirst { it.id == id }
        if (index != -1) {
            _notes[index] = _notes[index].copy(isFavorite = !_notes[index].isFavorite)
        }
    }

    fun deleteNote(id: String) {
        _notes.removeIf { it.id == id }
        if (selectedNoteId == id) {
            selectedNoteId = _notes.firstOrNull()?.id
        }
    }

    fun addNote() {
        val newNote = Note(title = "Untitled Note", content = "", time = "Just now")
        _notes.add(0, newNote)
        selectedNoteId = newNote.id
    }
}
