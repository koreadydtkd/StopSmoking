package hys.hmonkeyys.stopsmoking.room

import androidx.room.Database
import androidx.room.RoomDatabase
import hys.hmonkeyys.stopsmoking.room.dao.ReadPostDao
import hys.hmonkeyys.stopsmoking.room.entity.ReadPost

@Database(entities = [ReadPost::class], version = 1, exportSchema = false)
abstract class ReadPostDatabase : RoomDatabase() {
    abstract fun readPostDao(): ReadPostDao

    companion object {
        const val DB_NAME = "read-post-database"
    }
}