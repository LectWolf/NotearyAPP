package cc.mcii.noty.view.state

import androidx.compose.runtime.Immutable
import cc.mcii.noty.core.model.Note
import dev.shreyaspatil.mutekt.core.annotations.GenerateMutableModel

@GenerateMutableModel
@Immutable
interface NotesState : State {
    val isLoading: Boolean
    val notes: List<Note>
    val error: String?
    val isUserLoggedIn: Boolean?

    companion object {
        val initialState = NotesState(
            isLoading = false,
            notes = emptyList(),
            error = null,
            isUserLoggedIn = null
        )
    }
}
