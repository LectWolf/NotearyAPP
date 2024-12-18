package cc.mcii.noty.worker

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import cc.mcii.noty.core.repository.NotyNoteRepository
import cc.mcii.noty.di.LocalRepository
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject

@HiltWorker
class NotySyncWorker @AssistedInject constructor(
    @Assisted appContext: Context,
    @Assisted workerParams: WorkerParameters,
    @LocalRepository private val localNoteRepository: NotyNoteRepository
) : CoroutineWorker(appContext, workerParams) {

    override suspend fun doWork(): Result {
        return syncNotes()
    }

    private suspend fun syncNotes(): Result {
        return try {
            // 本地笔记读取
            localNoteRepository.addNotes(emptyList());


            Result.success()
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure()
        }
    }
}
