package com.mosin.mynotes.ui

import com.mosin.mynotes.model.Note

class MainViewState(val notes: List<Note>? = null, error: Throwable? = null) :
        BaseViewState<List<Note>?>(notes, error)