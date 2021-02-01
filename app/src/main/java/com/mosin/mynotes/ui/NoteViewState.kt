package com.mosin.mynotes.ui

import com.mosin.mynotes.model.Note

class NoteViewState(note: Note? = null, error: Throwable? = null) :
        BaseViewState<Note?>(note, error)
