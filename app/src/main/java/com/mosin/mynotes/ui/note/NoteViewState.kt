package com.mosin.mynotes.ui.note

import com.mosin.mynotes.model.Note
import com.mosin.mynotes.ui.base.BaseViewState

class NoteViewState(note: Note? = null, error: Throwable? = null) :
        BaseViewState<Note?>(note, error)
