package hys.hmonkeyys.stopsmoking.data.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "read-post")
data class ReadPostEntity(
    @PrimaryKey val uid: Int?,
    @ColumnInfo(name = "document_id") val documentId: String?
)
