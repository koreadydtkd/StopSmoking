package hys.hmonkeyys.stopsmoking.data.repository.readpost

import hys.hmonkeyys.stopsmoking.data.db.dao.ReadPostDao
import hys.hmonkeyys.stopsmoking.data.entity.ReadPostEntity
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultReadPostRepository(
    private val readPostDao: ReadPostDao,
    private val ioDispatcher: CoroutineDispatcher
) : ReadPostRepository {

    override suspend fun wasReadThisPost(documentId: String): Int = withContext(ioDispatcher) {
        readPostDao.wasReadThisPost(documentId)
    }

    override suspend fun insertReadPost(readPost: ReadPostEntity) = withContext(ioDispatcher) {
        readPostDao.insertReadPost(readPost)
    }

    override suspend fun deleteReadPost(documentId: String) = withContext(ioDispatcher) {
        readPostDao.deleteReadPost(documentId)
    }

}