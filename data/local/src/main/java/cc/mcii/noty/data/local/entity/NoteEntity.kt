package cc.mcii.noty.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "notes")
data class NoteEntity(

    @PrimaryKey(autoGenerate = false)
    val noteId: String,
    val title: String,
    val note: String,
    val isPinned: Boolean,
    val created: Long,
    val updated: Long   // 更新时间，使用时间戳
)
