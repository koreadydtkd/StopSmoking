package hys.hmonkeyys.stopsmoking.di

import android.content.Context
import androidx.room.Room
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hys.hmonkeyys.stopsmoking.data.db.ReadPostDatabase
import hys.hmonkeyys.stopsmoking.data.db.dao.ReadPostDao
import hys.hmonkeyys.stopsmoking.utils.Constant

internal fun createReadPostDB(context: Context): ReadPostDatabase {
    return Room.databaseBuilder(context, ReadPostDatabase::class.java, ReadPostDatabase.DB_NAME).build()
}
internal fun readPostDao(database: ReadPostDatabase): ReadPostDao {
    return database.readPostDao()
}