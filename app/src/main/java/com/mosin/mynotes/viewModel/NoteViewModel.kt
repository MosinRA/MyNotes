package com.mosin.mynotes.viewModel

import androidx.lifecycle.ViewModel
import com.mosin.mynotes.databinding.ActivityNoteBinding
import com.mosin.mynotes.model.Note
import com.mosin.mynotes.model.Repository
import java.util.*

class NoteViewModel(private val repository: Repository = Repository) : ViewModel() {

    private var pendingNote: Note? = null
    private lateinit var ui: ActivityNoteBinding
    private val id: String = UUID.randomUUID().toString()

    fun saveChanges(note: Note) {
        pendingNote = note
    }

    override fun onCleared() {
        if (pendingNote != null) {
            UUID.randomUUID().toString()
            repository.saveNote(pendingNote!!)
        }
    }

    fun getId(): String {
        return id
    }
}