package com.mosin.mynotes.viewModel

import com.mosin.mynotes.model.note.Note
import com.mosin.mynotes.model.note.NoteResult
import com.mosin.mynotes.model.repository.Repository
import com.mosin.mynotes.ui.base.BaseViewModel
import kotlinx.coroutines.channels.consumeEach
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class MainViewModel(private val repository: Repository) :
        BaseViewModel<List<Note>?>() {

    private val notesChannel by lazy {
        runBlocking { repository.getNotes() }
    }

    init {
        launch {
            notesChannel.consumeEach { result ->
                when (result) {
                    is NoteResult.Success<*> -> setData(result.data as? List<Note>)
                    is NoteResult.Error -> setError(result.error)
                }
            }
        }
    }

    public override fun onCleared() {
        notesChannel.cancel()
        super.onCleared()
    }
}

