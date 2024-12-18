package cc.mcii.noty.utils.validator

object NoteValidator {
    fun isValidNote(title: String, note: String) = (title.isNotBlank() && note.isNotBlank())
}
