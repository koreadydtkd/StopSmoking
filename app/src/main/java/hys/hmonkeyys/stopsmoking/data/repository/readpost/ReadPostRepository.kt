package hys.hmonkeyys.stopsmoking.data.repository.readpost

import hys.hmonkeyys.stopsmoking.data.entity.ReadPostEntity

interface ReadPostRepository {

    suspend fun wasReadThisPost(documentId: String): Int

    suspend fun insertReadPost(readPost: ReadPostEntity)

    suspend fun deleteReadPost(documentId: String)

}