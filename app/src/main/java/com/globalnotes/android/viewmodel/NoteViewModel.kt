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
    val backgroundColor: Color = Color.White
)

class NoteViewModel : ViewModel() {
    private val _notes = mutableStateListOf<Note>(
        Note(title = "Product Vision 2024", content = "The future of document collaboration...", time = "10:30 AM", folder = "Work"),
        Note(title = "Marketing Strategy", content = "Campaign themes: Simplicity, Speed...", time = "Yesterday", folder = "Work"),
        Note(title = "Groceries", content = "Milk, Eggs, Bread, Avocados...", time = "Feb 24", folder = "Personal"),
        Note(title = "Mobile App Feedback", content = "Users want more offline features...", time = "Feb 22", isFavorite = true),
        Note(title = "Meeting Notes", content = "Discussed the new roadmap...", time = "Feb 20")
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
