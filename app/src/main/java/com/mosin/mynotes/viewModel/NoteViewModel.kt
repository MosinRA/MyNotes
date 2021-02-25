package com.mosin.mynotes.viewModel

import com.mosin.mynotes.model.note.Note
import com.mosin.mynotes.model.note.NoteResult
import com.mosin.mynotes.model.repository.Repository
import com.mosin.mynotes.ui.base.BaseViewModel
import com.mosin.mynotes.ui.note.NoteViewState

class NoteViewModel(private val repository: Repository) :
        BaseViewModel<NoteViewState.Data, NoteViewState>() {

    private val currentNote: Note?
        get() = viewStateLiveData.value?.data?.note

    fun saveChanges(note: Note) {
        viewStateLiveData.value = NoteViewState(NoteViewState.Data(note = note))
    }

    override fun onCleared() {
        currentNote?.let { repository.saveNote(it) }
    }

    fun loadNote(noteId: String) {
        repository.getNoteById(noteId).observeForever { t ->
            t?.let { noteResult ->
                viewStateLiveData.value = when (noteResult) {
                    is NoteResult.Success<*> ->
                        NoteViewState(NoteViewState.Data(note = noteResult.data as? Note))
                    is NoteResult.Error ->
                        NoteViewState(error = noteResult.error)
                }

            }
        }
    }

    fun deleteNote() {
        currentNote?.let {
            repository.deleteNote(it.id).observeForever { result ->
                result?.let { noteResult ->
                    viewStateLiveData.value = when (noteResult) {
                        is NoteResult.Success<*> ->
                            NoteViewState(NoteViewState.Data(isDeleted = true))
                        is NoteResult.Error ->
                            NoteViewState(error = noteResult.error)
                    }
                }

            }
        }
    }
}