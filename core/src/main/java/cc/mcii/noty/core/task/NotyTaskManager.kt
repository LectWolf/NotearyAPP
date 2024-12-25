package cc.mcii.noty.core.task

import cc.mcii.noty.core.model.NotyTask
import kotlinx.coroutines.flow.Flow
import java.util.*
import javax.inject.Singleton

@Singleton
interface NotyTaskManager {
    /**
     * Schedules a task for syncing notes.
     *
     * @return Unique work ID
     */
    fun syncNotes(): UUID

    /**
     * Retrieves the state of a task
     *
     * @param taskId Unique work ID
     * @return Nullable (in case task not exists) task state
     */
    fun getTaskState(taskId: UUID): TaskState?

    /**
     * Returns Flowable task state of a specific task
     *
     * @param taskId Unique work ID
     * @return Flow of task state
     */
    fun observeTask(taskId: UUID): Flow<TaskState>

    /**
     * Aborts/Stops all scheduled (ongoing) tasks
     */
    fun abortAllTasks()

    /**
     * Generates task ID from note ID
     */
    fun getTaskIdFromNoteId(noteId: String) = noteId
}
