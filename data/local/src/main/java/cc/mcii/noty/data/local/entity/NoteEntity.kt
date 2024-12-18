package cc.mcii.noty.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(

    @PrimaryKey(autoGenerate = false)
    val noteId: String,
    val title: String,
    val note: String,
    val created: Long,
    val isPinned: Boolean
)
