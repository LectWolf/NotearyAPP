package cc.mcii.noty.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import cc.mcii.noty.data.local.dao.NotesDao
import cc.mcii.noty.data.local.entity.NoteEntity

@Database(
    entities = [NoteEntity::class],
    version = DatabaseMigrations.DB_VERSION
)
abstract class NotyDatabase : RoomDatabase() {

    abstract fun getNotesDao(): NotesDao

    companion object {
        private const val DB_NAME = "noty_database"

        @Volatile
        private var INSTANCE: NotyDatabase? = null

        fun getInstance(context: Context): NotyDatabase {
            val tempInstance = INSTANCE
            if (tempInstance != null) {
                return tempInstance
            }

            synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    NotyDatabase::class.java,
                    DB_NAME
                ).addMigrations(*DatabaseMigrations.MIGRATIONS).build()

                INSTANCE = instance
                return instance
            }
        }
    }
}
