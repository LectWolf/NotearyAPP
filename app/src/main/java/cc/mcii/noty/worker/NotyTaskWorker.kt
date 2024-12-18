package cc.mcii.noty.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cc.mcii.noty.core.model.Note
import cc.mcii.noty.core.model.NotyTaskAction
import cc.mcii.noty.core.repository.Either
import cc.mcii.noty.core.repository.NotyNoteRepository
import cc.mcii.noty.di.LocalRepository
import cc.mcii.noty.utils.ext.getEnum
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.flow.first

@HiltWorker
class NotyTaskWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @LocalRepository private val localNoteRepository: NotyNoteRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        if (runAttemptCount >= MAX_RUN_ATTEMPTS) return Result.failure()

        val noteId = getNoteId()

        return when (getTaskAction()) {
            NotyTaskAction.CREATE -> addNote(noteId)
            NotyTaskAction.UPDATE -> updateNote(noteId)
            NotyTaskAction.DELETE -> deleteNote(noteId)
            NotyTaskAction.PIN -> pinNote(noteId)
        }
    }

    private suspend fun addNote(tempNoteId: String): Result {
        val note = fetchLocalNote(tempNoteId)
        val response = localNoteRepository.addNote(note.title, note.note)
        return if (response is Either.Success) Result.success() else Result.retry()
    }

    private suspend fun updateNote(noteId: String): Result {
        val note = fetchLocalNote(noteId)
        val response = localNoteRepository.updateNote(note.id, note.title, note.note)
        return if (response is Either.Success) Result.success() else Result.retry()
    }

    private suspend fun deleteNote(noteId: String): Result {
        val response = localNoteRepository.deleteNote(noteId)
        return if (response is Either.Success) Result.success() else Result.retry()
    }

    private suspend fun pinNote(noteId: String): Result {
        val note = fetchLocalNote(noteId)
        val response = localNoteRepository.pinNote(noteId, note.isPinned)
        return if (response is Either.Success) Result.success() else Result.retry()
    }

    private suspend fun fetchLocalNote(noteId: String): Note =
        localNoteRepository.getNoteById(noteId).first()

    private fun getNoteId(): String = inputData.getString(KEY_NOTE_ID)
        ?: throw IllegalStateException("$KEY_NOTE_ID should be provided as input data.")

    private fun getTaskAction(): NotyTaskAction = inputData.getEnum<NotyTaskAction>(KEY_TASK_TYPE)
        ?: throw IllegalStateException("$KEY_TASK_TYPE should be provided as input data.")

    companion object {
        const val MAX_RUN_ATTEMPTS = 3
        const val KEY_NOTE_ID = "note_id"
        const val KEY_TASK_TYPE = "noty_task_type"
    }
}
