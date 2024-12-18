package cc.mcii.noty.view.viewmodel

import androidx.lifecycle.viewModelScope
import cc.mcii.noty.core.model.NotyTask
import cc.mcii.noty.core.repository.NotyNoteRepository
import cc.mcii.noty.core.task.NotyTaskManager
import cc.mcii.noty.di.LocalRepository
import cc.mcii.noty.store.StateStore
import cc.mcii.noty.utils.validator.NoteValidator
import cc.mcii.noty.view.state.AddNoteState
import cc.mcii.noty.view.state.MutableAddNoteState
import cc.mcii.noty.view.state.mutable
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddNoteViewModel @Inject constructor(
    @LocalRepository private val noteRepository: NotyNoteRepository,
    private val notyTaskManager: NotyTaskManager
) : BaseViewModel<AddNoteState>() {

    private val stateStore = StateStore(AddNoteState.initialState.mutable())

    override val state: StateFlow<AddNoteState> = stateStore.state

    private var job: Job? = null

    fun setTitle(title: String) {
        setState { this.title = title }
        validateNote()
    }

    fun setNote(note: String) {
        setState { this.note = note }
        validateNote()
    }

    fun add() {
        job?.cancel()
        job = viewModelScope.launch {
            val title = state.value.title.trim()
            val note = state.value.note.trim()

            setState { isAdding = true }

            val result = noteRepository.addNote(title, note)

            result.onSuccess { noteId ->
                scheduleNoteCreate(noteId)
                setState {
                    isAdding = false
                    added = true
                }
            }.onFailure { message ->
                setState {
                    isAdding = false
                    added = false
                    errorMessage = message
                }
            }
        }
    }

    private fun scheduleNoteCreate(noteId: String) =
        notyTaskManager.scheduleTask(NotyTask.create(noteId))

    private fun validateNote() {
        val isValid = NoteValidator.isValidNote(currentState.title, currentState.note)
        setState { showSave = isValid }
    }

    fun resetState() {
        setState {
            note = ""
            title = ""
            note = ""
            showSave = false
            isAdding = false
            added = false
            errorMessage = null
        }
    }

    private fun setState(update: MutableAddNoteState.() -> Unit) = stateStore.setState(update)
}
