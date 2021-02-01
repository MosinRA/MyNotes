package com.mosin.mynotes.ui.main

import com.mosin.mynotes.model.Note
import com.mosin.mynotes.ui.base.BaseViewState

class MainViewState(val notes: List<Note>? = null, error: Throwable? = null) :
        BaseViewState<List<Note>?>(notes, error)