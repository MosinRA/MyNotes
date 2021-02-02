package com.mosin.mynotes.model.repository

import com.mosin.mynotes.model.note.Note
import com.mosin.mynotes.model.provider.IRemoteDataProvider

class Repository(private val remoteDataProvider: IRemoteDataProvider) {

    fun getNotes() = remoteDataProvider.subscribeToAllNotes()
    fun saveNote(note: Note) = remoteDataProvider.saveNote(note)
    fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)
    fun getCurrentUser() = remoteDataProvider.getCurrentUser()
    fun deleteNote(noteId: String) = remoteDataProvider.deleteNote(noteId)
}