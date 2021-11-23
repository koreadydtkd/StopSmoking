package hys.hmonkeyys.stopsmoking.data.api

import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.*
import com.google.firebase.firestore.ktx.toObject
import hys.hmonkeyys.stopsmoking.model.CommentModel
import hys.hmonkeyys.stopsmoking.model.CommunityModel
import hys.hmonkeyys.stopsmoking.utils.Constant.COMMENT_COLLECTION_PATH
import hys.hmonkeyys.stopsmoking.utils.Constant.DB_Community
import hys.hmonkeyys.stopsmoking.utils.Constant.FIELD_DATE
import hys.hmonkeyys.stopsmoking.utils.Constant.LIST_LIMIT
import kotlinx.coroutines.tasks.await

class CommunityFireStoreApi(
    private val firebaseFirestore: FirebaseFirestore,
) : CommunityApi {

    override suspend fun getCommunityList(): Pair<List<CommunityModel>, QuerySnapshot> {
        val querySnapshot: QuerySnapshot

        val communityList = firebaseFirestore.collection(DB_Community)
            .orderBy(FIELD_DATE, Query.Direction.DESCENDING)
            .limit(LIST_LIMIT)
            .get()
            .await()
            .also { querySnapshot = it }
            .map { it.toObject<CommunityModel>() }

        return Pair(communityList, querySnapshot)
    }

    override suspend fun getMoreCommunityList(startSnapshot: DocumentSnapshot): Pair<List<CommunityModel>, QuerySnapshot> {
        val querySnapshot: QuerySnapshot

        val communityList = firebaseFirestore.collection(DB_Community)
            .orderBy(FIELD_DATE, Query.Direction.DESCENDING)
            .startAfter(startSnapshot)
            .limit(LIST_LIMIT)
            .get()
            .await()
            .also { querySnapshot = it }
            .map { it.toObject<CommunityModel>() }

        return Pair(communityList, querySnapshot)
    }

    override suspend fun getSelectedCategoryCommunityList(fieldName: String, fieldValue: String): Pair<List<CommunityModel>, QuerySnapshot> {
        val querySnapshot: QuerySnapshot

        val selectedCommunityList = firebaseFirestore.collection(DB_Community)
            .whereEqualTo(fieldName, fieldValue)
            .orderBy(FIELD_DATE, Query.Direction.DESCENDING)
            .limit(LIST_LIMIT)
            .get()
            .await()
            .also { querySnapshot = it }
            .map { it.toObject<CommunityModel>() }

        return Pair(selectedCommunityList, querySnapshot)
    }

    override suspend fun getMoreSelectedCategoryCommunityList(
        startSnapshot: DocumentSnapshot,
        fieldName: String,
        fieldValue: String,
    ): Pair<List<CommunityModel>, QuerySnapshot> {
        val querySnapshot: QuerySnapshot

        val selectedCommunityList = firebaseFirestore.collection(DB_Community)
            .whereEqualTo(fieldName, fieldValue)
            .orderBy(FIELD_DATE, Query.Direction.DESCENDING)
            .startAfter(startSnapshot)
            .limit(LIST_LIMIT)
            .get()
            .await()
            .also { querySnapshot = it }
            .map { it.toObject<CommunityModel>() }

        return Pair(selectedCommunityList, querySnapshot)
    }

    override suspend fun insertCommunityPost(communityModel: CommunityModel): Boolean {
        return try {
            firebaseFirestore.collection(DB_Community)
                .add(communityModel)
                .await()
            true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }
    }

    override suspend fun deletePost(documentId: String) {
        firebaseFirestore.collection(DB_Community).document(documentId).delete()
    }


    ////////// 댓글 관련  //////////
    override suspend fun getCommentList(documentId: String): List<CommentModel> {
        val documents: MutableList<DocumentSnapshot>

        val commentList = firebaseFirestore.collection(DB_Community).document(documentId).collection(COMMENT_COLLECTION_PATH)
            .orderBy("date", Query.Direction.DESCENDING)
            .limit(LIST_LIMIT)
            .get()
            .await()
            .also { documents = it.documents }
            .map { it.toObject<CommentModel>() }

        commentList.mapIndexed { index, commentModel ->
            commentModel.uid = documents[index].id
        }

        return commentList
    }

    override suspend fun addViewsCount(documentId: String) {
        firebaseFirestore.collection(DB_Community).document(documentId).update("viewsCount", FieldValue.increment(1L))
    }

    override suspend fun insertComment(documentId: String, commentModel: CommentModel): Boolean {
        return try {
            firebaseFirestore.collection(DB_Community).document(documentId).collection(COMMENT_COLLECTION_PATH).add(commentModel)
            true
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            false
        }
    }

    override suspend fun deleteComment(documentId: String, commentDocumentId: String): Boolean {
        return try {
            firebaseFirestore.collection(DB_Community).document(documentId).collection(COMMENT_COLLECTION_PATH)
                .document(commentDocumentId)
                .delete()
            true
        } catch (e: Exception) {
            false
        }
    }
}