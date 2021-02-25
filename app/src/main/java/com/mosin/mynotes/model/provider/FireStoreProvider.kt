package com.mosin.mynotes.model.provider

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.*
import com.mosin.mynotes.model.auth.NoAuthException
import com.mosin.mynotes.model.note.Note
import com.mosin.mynotes.model.note.NoteResult
import com.mosin.mynotes.model.auth.User
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.channels.ReceiveChannel
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

private const val NOTES_COLLECTION = "notes"
private const val USERS_COLLECTION = "users"

class FireStoreProvider(
        private val firebaseAuth: FirebaseAuth,
        private val db: FirebaseFirestore
) : IRemoteDataProvider {

    private val currentUser
        get() = firebaseAuth.currentUser

    override suspend fun subscribeToAllNotes(): ReceiveChannel<NoteResult> =
            Channel<NoteResult>(Channel.CONFLATED).apply {
                var registration: ListenerRegistration? = null
                try {
                    registration = getUserNotesCollection()
                            .addSnapshotListener { snapShot, e ->
                                val value = e?.let {
                                    NoteResult.Error(it)
                                } ?: snapShot?.let {
                                    val notes = it.documents.map { document ->
                                        document.toObject(Note::class.java)
                                    }
                                    NoteResult.Success(notes)
                                }
                                value?.let { offer(it) }
                            }
                } catch (e: Throwable) {
                    offer(NoteResult.Error(e))
                }
                invokeOnClose {
                    registration?.remove()
                }
            }

    override suspend fun getNoteById(id: String): Note =
            suspendCoroutine { continuation ->
                try {
                    getUserNotesCollection().document(id).get()
                            .addOnSuccessListener {
                                continuation.resume(it.toObject(Note::class.java)!!)
                            }.addOnFailureListener {
                                continuation.resumeWithException(it)
                            }
                } catch (e: Throwable) {
                    continuation.resumeWithException(e)
                }
            }

    override suspend fun saveNote(note: Note): Note =
            suspendCoroutine { continuation ->
                try {
                    getUserNotesCollection().document(note.id)
                            .set(note)
                            .addOnSuccessListener {
                                continuation.resume(note)
                            }.addOnFailureListener {
                                continuation.resumeWithException(it)
                            }
                } catch (e: Throwable) {
                    continuation.resumeWithException(e)
                }
            }


    override suspend fun getCurrentUser(): User =
            suspendCoroutine { continuation ->
                currentUser?.let { firebaseUser ->
                    continuation.resume(User(firebaseUser.displayName ?: "",
                            firebaseUser.email ?: ""))
                } ?: continuation.resumeWithException(NoAuthException())
            }

    override suspend fun deleteNote(noteId: String): Note? =
            suspendCoroutine { continuation ->
                try {
                    getUserNotesCollection()
                            .document(noteId)
                            .delete()
                            .addOnSuccessListener {
                                continuation.resume(null)
                            }.addOnFailureListener {
                                continuation.resumeWithException(it)
                            }
                } catch (e: Throwable) {
                    continuation.resumeWithException(e)
                }
            }

    private fun getUserNotesCollection() = currentUser?.let { fireBaseUser ->
        db.collection(USERS_COLLECTION)
                .document(fireBaseUser.uid)
                .collection(NOTES_COLLECTION)
    } ?: throw NoAuthException()
}
