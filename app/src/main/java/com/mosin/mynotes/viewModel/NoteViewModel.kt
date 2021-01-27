package com.mosin.mynotes.viewModel

import androidx.lifecycle.Observer
import com.mosin.mynotes.model.Note
import com.mosin.mynotes.model.NoteResult
import com.mosin.mynotes.model.Repository
import com.mosin.mynotes.ui.BaseViewModel
import com.mosin.mynotes.ui.NoteViewState

class NoteViewModel(val repository: Repository = Repository) :
        BaseViewModel<Note?, NoteViewState>() {

    private var pendingNote: Note? = null

    fun saveChanges(note: Note) {
        pendingNote = note
    }

    override fun onCleared() {
        if (pendingNote != null) {
            repository.saveNote(pendingNote!!)
        }
    }

    fun loadNote(noteId: String) {
        repository.getNoteById(noteId).observeForever(object :
                Observer<NoteResult> {
            override fun onChanged(t: NoteResult?) {
                if (t == null) return

                when (t) {
                    is NoteResult.Success<*> ->
                        viewStateLiveData.value = NoteViewState(note = t.data as? Note)
                    is NoteResult.Error ->
                        viewStateLiveData.value = NoteViewState(error = t.error)
                }
            }
        })
    }
}