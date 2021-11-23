package hys.hmonkeyys.stopsmoking.data.api

import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import hys.hmonkeyys.stopsmoking.model.CommentModel
import hys.hmonkeyys.stopsmoking.model.CommunityModel

interface CommunityApi {

    suspend fun getCommunityList(): Pair<List<CommunityModel>, QuerySnapshot>

    suspend fun getMoreCommunityList(startSnapshot: DocumentSnapshot): Pair<List<CommunityModel>, QuerySnapshot>

    suspend fun getSelectedCategoryCommunityList(
        fieldName: String,
        fieldValue: String,
    ): Pair<List<CommunityModel>, QuerySnapshot>

    suspend fun getMoreSelectedCategoryCommunityList(
        startSnapshot: DocumentSnapshot,
        fieldName: String,
        fieldValue: String,
    ): Pair<List<CommunityModel>, QuerySnapshot>

    suspend fun insertCommunityPost(communityModel: CommunityModel): Boolean

    suspend fun deletePost(documentId: String)


    suspend fun getCommentList(documentId: String): List<CommentModel>

    suspend fun addViewsCount(documentId: String)

    suspend fun insertComment(documentId: String, commentModel: CommentModel): Boolean

    suspend fun deleteComment(documentId: String, commentDocumentId: String): Boolean
}