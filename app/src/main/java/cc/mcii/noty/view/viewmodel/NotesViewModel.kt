package cc.mcii.noty.view.viewmodel

import android.util.Log
import androidx.lifecycle.viewModelScope
import cc.mcii.noty.core.preference.PreferenceManager
import cc.mcii.noty.core.repository.NotyNoteRepository
import cc.mcii.noty.core.task.NotyTaskManager
import cc.mcii.noty.core.task.TaskState.CANCELLED
import cc.mcii.noty.core.task.TaskState.COMPLETED
import cc.mcii.noty.core.task.TaskState.FAILED
import cc.mcii.noty.core.task.TaskState.SCHEDULED
import cc.mcii.noty.di.LocalRepository
import cc.mcii.noty.store.StateStore
import cc.mcii.noty.view.state.MutableNotesState
import cc.mcii.noty.view.state.NotesState
import cc.mcii.noty.view.state.mutable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    @LocalRepository private val notyNoteRepository: NotyNoteRepository,
    private val preferenceManager: PreferenceManager,
    private val notyTaskManager: NotyTaskManager
) : BaseViewModel<NotesState>() {

    private val stateStore = StateStore(initialState = NotesState.initialState.mutable())

    override val state: StateFlow<NotesState> = stateStore.state

    private var syncJob: Job? = null

    init {
        observeNotes()
        syncNotes()
    }

    fun syncNotes() {
        if (syncJob?.isActive == true) return

        syncJob = viewModelScope.launch {
            val taskId = notyTaskManager.syncNotes()
            try {
                notyTaskManager.observeTask(taskId).collect { taskState ->
                    when (taskState) {
                        SCHEDULED -> setState { isLoading = true }
                        COMPLETED, CANCELLED -> setState { isLoading = false }
                        FAILED -> setState {
                            isLoading = false
                            error = "笔记加载失败"
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e(TAG, "无法获取任务状态 ID:'$taskId'")
            }
        }
    }


    suspend fun isDarkModeEnabled() = preferenceManager.uiModeFlow.first()

    fun setDarkMode(enable: Boolean) {
        viewModelScope.launch {
            preferenceManager.setDarkMode(enable)
        }
    }

    private fun observeNotes() {
        notyNoteRepository.getAllNotes()
            .distinctUntilChanged()
            .onEach { response ->
                response.onSuccess { notes ->
                    setState {
                        this.isLoading = false
                        this.notes = notes
                    }
                }.onFailure { message ->
                    setState {
                        isLoading = false
                        error = message
                    }
                }
            }.onStart { setState { isLoading = true } }
            .launchIn(viewModelScope)
    }

    private fun setState(update: MutableNotesState.() -> Unit) = stateStore.setState(update)

    companion object {
        private const val TAG = "NotesViewModel"
    }
}
