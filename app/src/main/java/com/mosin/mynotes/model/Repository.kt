package com.mosin.mynotes.model

object Repository {

    private val notes: List<Note>

    init {
        notes = listOf("Название заметки", "Текст заметки")
    }
}