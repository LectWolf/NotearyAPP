package cc.mcii.noty.repository.local

import cc.mcii.noty.core.model.Note
import cc.mcii.noty.core.repository.Either
import cc.mcii.noty.core.repository.NotyNoteRepository
import cc.mcii.noty.data.local.dao.NotesDao
import cc.mcii.noty.data.local.entity.NoteEntity
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transform
import javax.inject.Inject

/**
 * Source of data of notes from from local database
 */
class NotyLocalNoteRepository @Inject constructor(
    private val notesDao: NotesDao
) : NotyNoteRepository {

    override fun getNoteById(noteId: String): Flow<Note> = notesDao.getNoteById(noteId)
        .filterNotNull()
        .map { Note(it.noteId, it.title, it.note, it.created, it.isPinned) }

    override fun getAllNotes(): Flow<Either<List<Note>>> = notesDao.getAllNotes()
        .map { notes -> notes.map { Note(it.noteId, it.title, it.note, it.created, it.isPinned) } }
        .transform { notes -> emit(Either.success(notes)) }
        .catch { emit(Either.success(emptyList())) }

    override suspend fun addNote(
        title: String,
        note: String
    ): Either<String> = runCatching {
        val tempNoteId = NotyNoteRepository.generateTemporaryId()
        notesDao.addNote(
            NoteEntity(
                noteId = tempNoteId,
                title = title,
                note = note,
                created = System.currentTimeMillis(),
                isPinned = false
            )
        )
        Either.success(tempNoteId)
    }.getOrDefault(Either.error("无法新建笔记"))

    override suspend fun addNotes(notes: List<Note>) = notes.map {
        NoteEntity(it.id, it.title, it.note, it.created, it.isPinned)
    }.let {
        notesDao.addNotes(it)
    }

    override suspend fun updateNote(
        noteId: String,
        title: String,
        note: String
    ): Either<String> = runCatching {
        notesDao.updateNoteById(noteId, title, note)
        Either.success(noteId)
    }.getOrDefault(Either.error("无法更新笔记"))

    override suspend fun deleteNote(noteId: String): Either<String> = runCatching {
        notesDao.deleteNoteById(noteId)
        Either.success(noteId)
    }.getOrDefault(Either.error("无法删除笔记"))

    override suspend fun pinNote(noteId: String, isPinned: Boolean): Either<String> = runCatching {
        notesDao.updateNotePin(noteId, isPinned)
        Either.success(noteId)
    }.getOrDefault(Either.error("无法置顶笔记"))

    override suspend fun deleteAllNotes() = notesDao.deleteAllNotes()

    override suspend fun updateNoteId(oldNoteId: String, newNoteId: String) =
        notesDao.updateNoteId(oldNoteId, newNoteId)
}
