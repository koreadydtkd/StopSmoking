package hys.hmonkeyys.stopsmoking.room.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "read-post")
data class ReadPost(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name = "document_id") val documentId: String?
)
