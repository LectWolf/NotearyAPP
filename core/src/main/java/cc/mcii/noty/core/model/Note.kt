package cc.mcii.noty.core.model

data class Note(
    val id: String,
    val title: String,
    val note: String,
    val created: Long,
    val isPinned: Boolean = false
)
