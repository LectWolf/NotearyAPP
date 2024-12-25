package cc.mcii.noty.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import cc.mcii.noty.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface NotesDao {

    /**
     * The return type of this method is nullable because internally it throws an error if
     * entity doesn't exist.
     *
     * Official docs says
     *
     * * When the return type is Flow<T>, querying an empty table throws a null pointer exception.
     * * When the return type is Flow<T?>, querying an empty table emits a null value.
     * * When the return type is Flow<List<T>>, querying an empty table emits an empty list.
     *
     * Refer: https://developer.android.com/reference/androidx/room/Query
     */
    @Query("SELECT * FROM notes WHERE noteId = :noteId")
    fun getNoteById(noteId: String): Flow<NoteEntity?>

    @Query("SELECT * FROM notes ORDER BY isPinned = 1 DESC, updated DESC")
    fun getAllNotes(): Flow<List<NoteEntity>>

    @Insert
    suspend fun addNote(note: NoteEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addNotes(notes: List<NoteEntity>)

    @Query("UPDATE notes SET title = :title, note = :note, updated = :updated WHERE noteId = :noteId")
    suspend fun updateNoteById(noteId: String, title: String, note: String,updated: Long)

    @Query("DELETE FROM notes WHERE noteId = :noteId")
    suspend fun deleteNoteById(noteId: String)

    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()

    @Query("UPDATE notes SET isPinned = :isPinned WHERE noteId = :noteId")
    suspend fun updateNotePin(noteId: String, isPinned: Boolean)
}
