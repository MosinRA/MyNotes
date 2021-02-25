package com.mosin.mynotes.model.repository

import com.mosin.mynotes.model.note.Note
import com.mosin.mynotes.model.provider.IRemoteDataProvider

class Repository(private val remoteDataProvider: IRemoteDataProvider) {

    suspend fun getNotes() = remoteDataProvider.subscribeToAllNotes()
    suspend fun saveNote(note: Note) = remoteDataProvider.saveNote(note)
    suspend fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)
    suspend fun getCurrentUser() = remoteDataProvider.getCurrentUser()
    suspend fun deleteNote(noteId: String) = remoteDataProvider.deleteNote(noteId)
}