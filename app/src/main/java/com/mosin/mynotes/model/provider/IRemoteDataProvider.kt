package com.mosin.mynotes.model.provider

import com.mosin.mynotes.model.note.Note
import com.mosin.mynotes.model.note.NoteResult
import com.mosin.mynotes.model.auth.User
import kotlinx.coroutines.channels.ReceiveChannel

interface IRemoteDataProvider {

    suspend fun subscribeToAllNotes(): ReceiveChannel<NoteResult>
    suspend fun getNoteById(id: String): Note
    suspend fun saveNote(note: Note): Note
    suspend fun getCurrentUser(): User
    suspend fun deleteNote(noteId: String): Note?
}