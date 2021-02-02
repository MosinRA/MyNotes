package com.mosin.mynotes.model.provider

import androidx.lifecycle.LiveData
import com.mosin.mynotes.model.note.Note
import com.mosin.mynotes.model.note.NoteResult
import com.mosin.mynotes.model.auth.User

interface IRemoteDataProvider {

    fun subscribeToAllNotes(): LiveData<NoteResult>
    fun getNoteById(id: String): LiveData<NoteResult>
    fun saveNote(note: Note): LiveData<NoteResult>
    fun getCurrentUser(): LiveData<User?>
    fun deleteNote(noteId: String): LiveData<NoteResult>
}