package cc.mcii.noty.core.repository

import cc.mcii.noty.core.model.Note
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Singleton

/**
 * Repository for notes.
 */
@Singleton
interface NotyNoteRepository {

    /**
     * Returns a note
     *
     * @param noteId A note ID.
     */
    fun getNoteById(noteId: String): Flow<Note>

    /**
     * Returns all notes.
     */
    fun getAllNotes(): Flow<Either<List<Note>>>

    /**
     * Adds a new note
     *
     * @param title Title of a note
     * @param note Body of a note
     */
    suspend fun addNote(title: String, note: String): Either<String>

    /**
     * Adds a list of notes. Replaces notes if already exists
     */
    suspend fun addNotes(notes: List<Note>)

    /**
     * Updates a new note having ID [noteId]
     *
     * @param noteId The Note ID
     * @param title Title of a note
     * @param note Body of a note
     */
    suspend fun updateNote(
        noteId: String,
        title: String,
        note: String
    ): Either<String>

    /**
     * Deletes a new note having ID [noteId]
     */
    suspend fun deleteNote(noteId: String): Either<String>

    /**
     * Pins/unpins a note having ID [noteId] based on [isPinned]
     */
    suspend fun pinNote(noteId: String, isPinned: Boolean): Either<String>

    /**
     * Deletes all notes.
     */
    suspend fun deleteAllNotes()

    companion object {
        private const val PREFIX_LOCAL_NOTE_ID = "LOCAL"
        fun generateLocalId() = "$PREFIX_LOCAL_NOTE_ID-${UUID.randomUUID()}"
        fun isLocalNote(noteId: String) = noteId.startsWith(PREFIX_LOCAL_NOTE_ID)
    }
}
