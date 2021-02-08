package com.mosin.mynotes.viewModel

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.Observer
import com.mosin.mynotes.model.note.Note
import com.mosin.mynotes.model.note.NoteResult
import com.mosin.mynotes.model.repository.Repository
import com.mosin.mynotes.ui.base.BaseViewModel
import com.mosin.mynotes.ui.main.MainViewState

class MainViewModel(private val repository: Repository) :
        BaseViewModel<List<Note>?, MainViewState>() {

    private val notesObserver = object : Observer<NoteResult> {
        override fun onChanged(t: NoteResult?) {
            if (t == null) return

            when (t) {
                is NoteResult.Success<*> -> {
                    viewStateLiveData.value = MainViewState(notes = t.data as? List<Note>)
                }
                is NoteResult.Error -> {
                    viewStateLiveData.value = MainViewState(error = t.error)
                }
            }
        }
    }
    private val repositoryNotes = repository.getNotes()

    init {
        viewStateLiveData.value = MainViewState()
        repositoryNotes.observeForever(notesObserver)
    }

    @VisibleForTesting(otherwise = VisibleForTesting.PROTECTED)
    public override fun onCleared() {
        repositoryNotes.removeObserver(notesObserver)
    }
}

