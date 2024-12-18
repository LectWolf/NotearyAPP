package cc.mcii.noty.view.state

import androidx.compose.runtime.Immutable
import dev.shreyaspatil.mutekt.core.annotations.GenerateMutableModel

@GenerateMutableModel
@Immutable
interface AddNoteState : State {
    val title: String
    val note: String
    val showSave: Boolean
    val isAdding: Boolean
    val added: Boolean
    val errorMessage: String?

    companion object {
        val initialState = AddNoteState(
            title = "",
            note = "",
            showSave = false,
            isAdding = false,
            added = false,
            errorMessage = null
        )
    }
}
