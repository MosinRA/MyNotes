package com.mosin.mynotes.model.provider

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.mosin.mynotes.model.auth.NoAuthException
import com.mosin.mynotes.model.note.Note
import com.mosin.mynotes.model.note.NoteResult
import com.mosin.mynotes.model.auth.User

private const val NOTES_COLLECTION = "notes"
private const val USERS_COLLECTION = "users"

class FireStoreProvider(
        private val firebaseAuth: FirebaseAuth,
        private val db: FirebaseFirestore
) : IRemoteDataProvider {

    private val TAG = "${FireStoreProvider::class.java.simpleName} :"

    private val currentUser
        get() = firebaseAuth.currentUser

    override fun subscribeToAllNotes(): LiveData<NoteResult> =
            MutableLiveData<NoteResult>().apply {
                try {
                    getUserNotesCollection().addSnapshotListener { snapshot, e ->
                        value = e?.let { NoteResult.Error(it) }
                                ?: snapshot?.let { query ->
                                    val notes = query.documents.map {
                                        it.toObject(Note::class.java)
                                    }
                                    NoteResult.Success(notes)
                                }
                    }
                } catch (e: Throwable) {
                    value = NoteResult.Error(e)
                }
            }

    override fun getNoteById(id: String): LiveData<NoteResult> =
            MutableLiveData<NoteResult>().apply {
                try {
                    getUserNotesCollection().document(id)
                            .get()
                            .addOnSuccessListener { snapshot ->
                                value = NoteResult.Success(snapshot.toObject(Note::class.java))
                            }.addOnFailureListener { exception ->
                                value = NoteResult.Error(exception)
                            }
                } catch (e: Throwable) {
                    value = NoteResult.Error(e)
                }
            }

    override fun saveNote(note: Note): LiveData<NoteResult> =
            MutableLiveData<NoteResult>().apply {
                try {
                    getUserNotesCollection().document(note.id)
                            .set(note)
                            .addOnSuccessListener {
                                Log.d(TAG, "Заметка $note сохранена")
                                value = NoteResult.Success(note)
                            }
                            .addOnFailureListener {
                                OnFailureListener { exception ->
                                    Log.d(TAG, "Ошибка сохранения заметки: $note, " +
                                            "сообщение: ${exception.message}")
                                    value = NoteResult.Error(exception)
                                }
                            }
                } catch (e: Throwable) {
                    value = NoteResult.Error(e)
                }
            }

    override fun getCurrentUser(): LiveData<User?> =
            MutableLiveData<User?>().apply {
                value = currentUser?.let {
                    User(it.displayName ?: "",
                            it.email ?: "")
                }
            }

    override fun deleteNote(noteId: String): LiveData<NoteResult> =
            MutableLiveData<NoteResult>().apply {
                try {
                    getUserNotesCollection()
                            .document(noteId)
                            .delete()
                            .addOnSuccessListener {
                                value = NoteResult.Success(null)
                            }
                            .addOnFailureListener {
                                value = NoteResult.Error(it)
                            }
                } catch (e: Throwable) {
                    value = NoteResult.Error(e)
                }
            }

    private fun getUserNotesCollection() = currentUser?.let { fireBaseUser ->
        db.collection(USERS_COLLECTION)
                .document(fireBaseUser.uid)
                .collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()
}
