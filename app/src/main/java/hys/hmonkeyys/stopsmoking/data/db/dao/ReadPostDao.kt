package hys.hmonkeyys.stopsmoking.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import hys.hmonkeyys.stopsmoking.data.entity.ReadPost

@Dao
interface ReadPostDao {

    // 읽은 게시물인지 확인
    @Query("SELECT COUNT(*) FROM `read-post` WHERE document_id LIKE :documentId")
    suspend fun wasReadThisPost(documentId: String): Int

    // 데이터 삽입
    @Insert
    suspend fun insertReadPost(readPost: ReadPost)

    // 데이터 삭제 (1개)
    @Query("DELETE FROM `read-post` WHERE document_id LIKE :documentId")
    suspend fun deleteReadPost(documentId: String)
}