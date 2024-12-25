package cc.mcii.noty.core.model

data class Note(
    val id: String,
    val title: String,
    val note: String,
    val isPinned: Boolean = false,
    val created: Long,
    val update: Long,
)
