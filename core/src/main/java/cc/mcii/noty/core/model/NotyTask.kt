package cc.mcii.noty.core.model

class NotyTask private constructor(val noteId: String, val action: NotyTaskAction) {
    companion object {
        fun create(noteId: String) = NotyTask(noteId, NotyTaskAction.CREATE)
        fun update(noteId: String) = NotyTask(noteId, NotyTaskAction.UPDATE)
        fun delete(noteId: String) = NotyTask(noteId, NotyTaskAction.DELETE)
        fun pin(noteId: String) = NotyTask(noteId, NotyTaskAction.PIN)
    }
}

enum class NotyTaskAction {
    CREATE, UPDATE, DELETE, PIN
}
