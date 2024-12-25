package cc.mcii.noty.view.state

import androidx.compose.runtime.Immutable
import dev.shreyaspatil.mutekt.core.annotations.GenerateMutableModel

@GenerateMutableModel
@Immutable
interface NoteDetailState : State {
    val isLoading: Boolean
    val title: String?
    val note: String?
    val time: Long?
    val isPinned: Boolean
    val showSave: Boolean
    val finished: Boolean
    val error: String?

    companion object {
        val initialState = NoteDetailState(
            isLoading = false,
            title = null,
            note = null,
            time = null,
            isPinned = false,
            showSave = false,
            finished = false,
            error = null
        )
    }
}
