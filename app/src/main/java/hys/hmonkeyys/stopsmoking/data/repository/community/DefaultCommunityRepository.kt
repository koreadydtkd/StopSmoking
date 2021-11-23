package hys.hmonkeyys.stopsmoking.data.repository.community

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import hys.hmonkeyys.stopsmoking.data.api.CommunityApi
import hys.hmonkeyys.stopsmoking.model.CommentModel
import hys.hmonkeyys.stopsmoking.model.CommunityModel
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultCommunityRepository(
    private val communityApi: CommunityApi,
    private val ioDispatcher: CoroutineDispatcher,
) : CommunityRepository {

    override suspend fun getCommunityList(): Pair<List<CommunityModel>, QuerySnapshot> = withContext(ioDispatcher) {
        communityApi.getCommunityList()
    }

    override suspend fun getMoreCommunityList(
        startSnapshot: DocumentSnapshot
    ): Pair<List<CommunityModel>, QuerySnapshot> = withContext(ioDispatcher) {
        communityApi.getMoreCommunityList(startSnapshot)
    }

    override suspend fun getSelectedCategoryCommunityList(
        fieldName: String,
        fieldValue: String,
    ): Pair<List<CommunityModel>, QuerySnapshot> = withContext(ioDispatcher) {
        communityApi.getSelectedCategoryCommunityList(fieldName, fieldValue)
    }

    override suspend fun getMoreSelectedCategoryCommunityList(
        startSnapshot: DocumentSnapshot,
        fieldName: String,
        fieldValue: String,
    ): Pair<List<CommunityModel>, QuerySnapshot> = withContext(ioDispatcher) {
        communityApi.getMoreSelectedCategoryCommunityList(startSnapshot, fieldName, fieldValue)
    }

    override suspend fun insertCommunityPost(communityModel: CommunityModel): Boolean = withContext(ioDispatcher) {
        communityApi.insertCommunityPost(communityModel)
    }

    override suspend fun deletePost(documentId: String) = withContext(ioDispatcher) {
        communityApi.deletePost(documentId)
    }


    override suspend fun getCommentList(documentId: String): List<CommentModel> = withContext(ioDispatcher) {
        communityApi.getCommentList(documentId)
    }

    override suspend fun addViewsCount(documentId: String) = withContext(ioDispatcher) {
        communityApi.addViewsCount(documentId)
    }

    override suspend fun insertComment(documentId: String, commentModel: CommentModel): Boolean = withContext(ioDispatcher) {
        communityApi.insertComment(documentId, commentModel)
    }

    override suspend fun deleteComment(documentId: String, commentDocumentId: String): Boolean = withContext(ioDispatcher) {
        communityApi.deleteComment(documentId, commentDocumentId)
    }
}