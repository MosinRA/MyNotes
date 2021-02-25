package com.mosin.mynotes.ui.note

import com.mosin.mynotes.model.note.Note
import com.mosin.mynotes.ui.base.BaseViewState

class NoteViewState(data: Data = Data(), error: Throwable? = null) :
        BaseViewState<NoteViewState.Data>(data, error) {

    data class Data(val isDeleted: Boolean = false, val note: Note? = null)
}
