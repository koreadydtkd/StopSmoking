package hys.hmonkeyys.stopsmoking.activitys.communitydetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.Query
import hys.hmonkeyys.stopsmoking.activitys.BaseViewModel
import hys.hmonkeyys.stopsmoking.data.entity.CommentModel
import hys.hmonkeyys.stopsmoking.data.entity.CommunityModel
import hys.hmonkeyys.stopsmoking.data.db.dao.ReadPostDao
import hys.hmonkeyys.stopsmoking.data.entity.ReadPost
import hys.hmonkeyys.stopsmoking.data.preference.PreferenceManager
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NICK_NAME
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class CommunityDetailViewModel(
    private val communityDB: CollectionReference,
    private val preferenceManager: PreferenceManager,
    private val dao: ReadPostDao,
) : BaseViewModel() {

    private var _communityDetailLiveData = MutableLiveData<CommunityDetailState>()
    val communityDetailLiveData: LiveData<CommunityDetailState> = _communityDetailLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _communityDetailLiveData.postValue(CommunityDetailState.Initialized)
    }

    /** 읽었던 글이 아닌 경우에만 조회수 추가 */
    fun addViewsCount(communityModel: CommunityModel) {
        viewModelScope.launch {
            val documentId = communityModel.uid
            if (wasRead(documentId).not()) {
                // 조회수 추가
                communityDB.document(documentId).update("viewsCount", FieldValue.increment(1L))

                // 내가 읽은 게시물 DB 에 추가
                dao.insertReadPost(ReadPost(null, documentId))
            }
        }
    }

    /** 읽었는지 확인
     * 1 이상이면 봤었던 글
     * */
    private suspend fun wasRead(documentId: String): Boolean {
        return dao.wasReadThisPost(documentId) > 0
    }

    /** NickName 으로 내가 작성한 글인지 */
    fun isMyPost(writer: String): Boolean {
        val nickName = getNickName()
        nickName?.let {
            if (writer == it) {
                return true
            }
        }
        return false
    }

    /** Nick Name 반환 */
    fun getNickName(): String? = preferenceManager.getString(NICK_NAME)

    /** 게시글 삭제(Firebase DB, Room DB) */
    fun deletePost(documentId: String) {
        if (documentId.isEmpty().not()) {
            viewModelScope.launch {
                communityDB.document(documentId).delete()
                dao.deleteReadPost(documentId)
            }
        }

    }

    /** 댓글 목록 가져오기 */
    fun fetchComments(documentId: String) {
        communityDB.document(documentId).collection(COMMENT_COLLECTION_PATH)
            .orderBy("date", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null) {
                    _communityDetailLiveData.postValue(CommunityDetailState.FetChCommentList(emptyList()))
                    FirebaseCrashlytics.getInstance().recordException(error)
                    return@addSnapshotListener
                }

                value?.let {
                    val commentList = it.toObjects(CommentModel::class.java)
                    commentList.mapIndexed { index, commentModel ->
                        commentModel.uid = it.documents[index].id
                    }
                    _communityDetailLiveData.postValue(CommunityDetailState.FetChCommentList(commentList))
                }
            }
    }

    /** 댓글 등록 */
    fun registerComment(documentId: String, comment: String) {
        val nickName = getNickName()
        nickName?.let {
            communityDB.document(documentId).collection(COMMENT_COLLECTION_PATH)
                .add(
                    CommentModel(
                        "",
                        it,
                        System.currentTimeMillis(),
                        comment
                    )
                )
        }
    }

    /** 댓글 삭제 */
    fun deleteComment(documentId: String, commentDocumentId: String) {
        communityDB.document(documentId).collection(COMMENT_COLLECTION_PATH).document(commentDocumentId).delete()
    }

    companion object {
        private const val COMMENT_COLLECTION_PATH = "comment"
    }
}