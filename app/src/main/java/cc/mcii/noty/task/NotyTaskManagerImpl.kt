package cc.mcii.noty.task

import androidx.lifecycle.asFlow
import androidx.work.Data
import androidx.work.ExistingWorkPolicy
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkInfo.State
import androidx.work.WorkManager
import cc.mcii.noty.core.model.NotyTask
import cc.mcii.noty.core.task.NotyTaskManager
import cc.mcii.noty.core.task.TaskState
import cc.mcii.noty.utils.ext.putEnum
import cc.mcii.noty.worker.NotySyncWorker
import cc.mcii.noty.worker.NotyTaskWorker
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformWhile
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class NotyTaskManagerImpl @Inject constructor(
    private val workManager: WorkManager
) : NotyTaskManager {

    override fun syncNotes(): UUID {
        val notySyncWorker = OneTimeWorkRequestBuilder<NotySyncWorker>()
            .build()

        workManager.enqueueUniqueWork(
            SYNC_TASK_NAME,
            ExistingWorkPolicy.REPLACE,
            notySyncWorker
        )

        return notySyncWorker.id
    }

    override fun scheduleTask(notyTask: NotyTask): UUID {
        val notyTaskWorker = OneTimeWorkRequestBuilder<NotyTaskWorker>()
            .setInputData(generateData(notyTask))
            .build()

        workManager.enqueueUniqueWork(
            getTaskIdFromNoteId(notyTask.noteId),
            ExistingWorkPolicy.REPLACE,
            notyTaskWorker
        )

        return notyTaskWorker.id
    }

    override fun getTaskState(taskId: UUID): TaskState? = runCatching {
        workManager.getWorkInfoById(taskId)
            .get()
            .let { mapWorkInfoStateToTaskState(it.state) }
    }.getOrNull()

    override fun observeTask(taskId: UUID): Flow<TaskState> {
        return workManager.getWorkInfoByIdLiveData(taskId)
            .asFlow()
            .map { mapWorkInfoStateToTaskState(it.state) }
            .transformWhile { taskState ->
                emit(taskState)
                !taskState.isTerminalState
            }.distinctUntilChanged()
    }

    override fun abortAllTasks() {
        workManager.cancelAllWork()
    }

    private fun mapWorkInfoStateToTaskState(state: State): TaskState = when (state) {
        State.ENQUEUED, State.RUNNING, State.BLOCKED -> TaskState.SCHEDULED
        State.CANCELLED -> TaskState.CANCELLED
        State.FAILED -> TaskState.FAILED
        State.SUCCEEDED -> TaskState.COMPLETED
    }

    private fun generateData(notyTask: NotyTask) = Data.Builder()
        .putString(NotyTaskWorker.KEY_NOTE_ID, notyTask.noteId)
        .putEnum(NotyTaskWorker.KEY_TASK_TYPE, notyTask.action)
        .build()

    companion object {
        const val SYNC_TASK_NAME = "NotyTaskManagerImpl"
    }
}
