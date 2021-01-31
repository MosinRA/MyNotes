package com.mosin.mynotes.model

import androidx.lifecycle.MutableLiveData

object Repository {

    private val notesLiveData = MutableLiveData<List<Note>>()

    private val remoteDataProvider: IRemoteDataProvider = FireStoreProvider()

    fun getNotes() = remoteDataProvider.subscribeToAllNotes()
    fun saveNote(note: Note) = remoteDataProvider.saveNote(note)
    fun getNoteById(id: String) = remoteDataProvider.getNoteById(id)
    fun getCurrentUser() = remoteDataProvider.getCurrentUser()
}