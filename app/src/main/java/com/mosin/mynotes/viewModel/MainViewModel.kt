package com.mosin.mynotes.viewModel

import androidx.lifecycle.Observer
import com.mosin.mynotes.model.Note
import com.mosin.mynotes.model.NoteResult
import com.mosin.mynotes.model.Repository
import com.mosin.mynotes.ui.BaseViewModel
import com.mosin.mynotes.ui.MainViewState

class MainViewModel(val repository: Repository = Repository) :
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

    override fun onCleared() {
        repositoryNotes.removeObserver(notesObserver)
    }
}

