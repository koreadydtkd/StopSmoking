package hys.hmonkeyys.stopsmoking.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import hys.hmonkeyys.stopsmoking.data.db.dao.ReadPostDao
import hys.hmonkeyys.stopsmoking.data.entity.ReadPostEntity

@Database(entities = [ReadPostEntity::class], version = 1, exportSchema = false)
abstract class ReadPostDatabase : RoomDatabase() {
    abstract fun readPostDao(): ReadPostDao

    companion object {
        const val DB_NAME = "read-post-database"
    }
}