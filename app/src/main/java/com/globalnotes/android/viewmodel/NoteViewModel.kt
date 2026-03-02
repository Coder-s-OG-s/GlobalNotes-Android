package com.globalnotes.android.viewmodel

import androidx.compose.runtime.*
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ListenerRegistration
import com.google.firebase.firestore.Query
import java.text.SimpleDateFormat
import java.util.*

data class Note(
    val id: String = UUID.randomUUID().toString(),
    val title: String = "",
    val content: String = "",
    val time: String = "Just now",
    val isFavorite: Boolean = false,
    val isArchived: Boolean = false,
    val folder: String = "Personal",
    val createdAt: Long = System.currentTimeMillis()
)

class NoteViewModel : ViewModel() {

    private val db   = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    private val _notes = mutableStateListOf<Note>()
    val notes: List<Note> get() = _notes

    private val _storedFolders = mutableStateListOf<String>()
    val storedFolders: List<String> get() = _storedFolders

    var selectedNoteId by mutableStateOf<String?>(null)
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
            "Archived"  -> _notes.filter { it.isArchived }
            else        -> _notes.filter { it.folder == currentFilter && !it.isArchived }
        }
    }

    private var listenerReg: ListenerRegistration? = null
    private var foldersListenerReg: ListenerRegistration? = null
    private var authStateListener: FirebaseAuth.AuthStateListener? = null

    init {
        // React to sign-in AND sign-out automatically, even across user switches
        authStateListener = FirebaseAuth.AuthStateListener { firebaseAuth ->
            val uid = firebaseAuth.currentUser?.uid
            if (uid != null) {
                startListening(uid)
            } else {
                listenerReg?.remove()
                listenerReg = null
                foldersListenerReg?.remove()
                foldersListenerReg = null
                _notes.clear()
                _storedFolders.clear()
                selectedNoteId = null
            }
        }
        auth.addAuthStateListener(authStateListener!!)
    }

    private fun startListening(uid: String) {
        listenerReg?.remove()
        listenerReg = db.collection("users").document(uid)
            .collection("notes")
            .orderBy("createdAt", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, _ ->
                snapshot ?: return@addSnapshotListener
                _notes.clear()
                _notes.addAll(snapshot.documents.mapNotNull { doc ->
                    try {
                        val createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis()
                        Note(
                            id          = doc.id,
                            title       = doc.getString("title") ?: "",
                            content     = doc.getString("content") ?: "",
                            time        = createdAt.toRelativeTime(),
                            isFavorite  = doc.getBoolean("isFavorite") ?: false,
                            isArchived  = doc.getBoolean("isArchived") ?: false,
                            folder      = doc.getString("folder") ?: "Personal",
                            createdAt   = createdAt
                        )
                    } catch (e: Exception) { null }
                })
                if (selectedNoteId == null) {
                    selectedNoteId = _notes.firstOrNull()?.id
                }
            }

        foldersListenerReg?.remove()
        foldersListenerReg = db.collection("users").document(uid)
            .collection("folders")
            .addSnapshotListener { snapshot, _ ->
                snapshot ?: return@addSnapshotListener
                _storedFolders.clear()
                _storedFolders.addAll(snapshot.documents.mapNotNull { it.getString("name") })
            }
    }

    override fun onCleared() {
        super.onCleared()
        authStateListener?.let { auth.removeAuthStateListener(it) }
        listenerReg?.remove()
        foldersListenerReg?.remove()
    }

    // ── Selection / Filter ───────────────────────────────────────────────────

    fun selectNote(id: String) { selectedNoteId = id }

    fun updateFilter(filter: String) { currentFilter = filter }

    // ── CRUD (all writes go to Firestore; snapshot listener updates _notes) ──

    fun addNote() {
        val uid = auth.currentUser?.uid ?: return
        val id  = UUID.randomUUID().toString()
        val now = System.currentTimeMillis()
        val data = hashMapOf(
            "title"     to "Untitled Note",
            "content"   to "",
            "isFavorite" to false,
            "isArchived" to false,
            "folder"    to "Personal",
            "createdAt" to now
        )
        db.collection("users").document(uid).collection("notes").document(id).set(data)
        selectedNoteId = id
    }

    fun updateNoteContent(id: String, newContent: String) {
        userNotes()?.document(id)?.update("content", newContent)
    }

    fun updateNoteTitle(id: String, newTitle: String) {
        userNotes()?.document(id)?.update("title", newTitle)
    }

    fun toggleFavorite(id: String) {
        val note = _notes.find { it.id == id } ?: return
        userNotes()?.document(id)?.update("isFavorite", !note.isFavorite)
    }

    fun deleteNote(id: String) {
        if (selectedNoteId == id) {
            selectedNoteId = _notes.firstOrNull { it.id != id }?.id
        }
        userNotes()?.document(id)?.delete()
    }

    fun addFolder(name: String) {
        val uid = auth.currentUser?.uid ?: return
        val trimmed = name.trim()
        if (trimmed.isEmpty()) return
        val existing = (_storedFolders + _notes.map { it.folder }).map { it.lowercase() }
        if (existing.contains(trimmed.lowercase())) return
        db.collection("users").document(uid).collection("folders")
            .add(hashMapOf("name" to trimmed, "createdAt" to System.currentTimeMillis()))
    }

    // ── Helper ───────────────────────────────────────────────────────────────

    private fun userNotes() = auth.currentUser?.uid?.let { uid ->
        db.collection("users").document(uid).collection("notes")
    }
}

private fun Long.toRelativeTime(): String {
    val diff = System.currentTimeMillis() - this
    return when {
        diff < 60_000          -> "Just now"
        diff < 3_600_000       -> "${diff / 60_000}m ago"
        diff < 86_400_000      -> "${diff / 3_600_000}h ago"
        diff < 7 * 86_400_000  -> "${diff / 86_400_000}d ago"
        else -> SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(this))
    }
}
