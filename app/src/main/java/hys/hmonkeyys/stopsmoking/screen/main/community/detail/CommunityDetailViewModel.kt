package hys.hmonkeyys.stopsmoking.screen.main.community.detail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.FieldValue
import hys.hmonkeyys.stopsmoking.screen.BaseViewModel
import hys.hmonkeyys.stopsmoking.model.CommentModel
import hys.hmonkeyys.stopsmoking.model.CommunityModel
import hys.hmonkeyys.stopsmoking.data.entity.ReadPostEntity
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.NICK_NAME
import hys.hmonkeyys.stopsmoking.data.repository.community.CommunityRepository
import hys.hmonkeyys.stopsmoking.data.repository.readpost.ReadPostRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class CommunityDetailViewModel(
    private val communityRepository: CommunityRepository,
    private val readPostRepository: ReadPostRepository,
    private val pref: AppPreferenceManager,
) : BaseViewModel() {

    private var _communityDetailLiveData = MutableLiveData<CommunityDetailState>()
    val communityDetailLiveData: LiveData<CommunityDetailState> = _communityDetailLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _communityDetailLiveData.postValue(CommunityDetailState.Initialized)
    }

    /** 읽었던 글이 아닌 경우에만 조회수 추가 */
    fun addViewsCount(documentId: String) = viewModelScope.launch {
        // 봤던 게시물인지 체크
        val wasRead = readPostRepository.wasReadThisPost(documentId) > 0

        if (wasRead.not()) {
            // 조회수 추가
            communityRepository.addViewsCount(documentId)

            // 내가 읽은 게시물 DB 에 추가
            readPostRepository.insertReadPost(ReadPostEntity(null, documentId))
        }
    }

    /** NickName 으로 내가 작성한 글인지 */
    fun isMyPost(writer: String): Boolean {
        getNickName()?.let {
            if (writer == it) return true
        }
        return false
    }

    /** Nick Name 반환 */
    fun getNickName(): String? = pref.getString(NICK_NAME)

    /** 게시글 삭제(Firebase DB, Room DB) */
    fun deletePost(documentId: String) = viewModelScope.launch {
        if (documentId.isEmpty().not()) {
            communityRepository.deletePost(documentId)
            readPostRepository.deleteReadPost(documentId)
        }
    }

    /** 댓글 목록 가져오기 */
    fun fetchComments(documentId: String) = viewModelScope.launch {
        _communityDetailLiveData.postValue(CommunityDetailState.Loading)

        val commentList = communityRepository.getCommentList(documentId)
        if (commentList.isNullOrEmpty()) {
            _communityDetailLiveData.postValue(CommunityDetailState.FetChCommentList(emptyList()))
        } else {
            _communityDetailLiveData.postValue(CommunityDetailState.FetChCommentList(commentList))
        }
    }

    /** 댓글 등록 */
    fun registerComment(documentId: String, comment: String) = viewModelScope.launch {
        getNickName()?.let { nick ->
            val isSuccess = communityRepository.insertComment(documentId,
                CommentModel(
                    uid = "",
                    writer = nick,
                    date = System.currentTimeMillis(),
                    comment = comment
                )
            )

            if (isSuccess) {
                _communityDetailLiveData.postValue(CommunityDetailState.CommentRegisterSuccess)
            } else {
                _communityDetailLiveData.postValue(CommunityDetailState.CommentRegisterFailure)
            }
        }
    }

    /** 댓글 삭제 */
    fun deleteComment(documentId: String, commentDocumentId: String) = viewModelScope.launch {
        val isSuccess = communityRepository.deleteComment(documentId, commentDocumentId)

        if (isSuccess) {
            _communityDetailLiveData.postValue(CommunityDetailState.CommentDeleteSuccess)
        } else {
            _communityDetailLiveData.postValue(CommunityDetailState.CommentDeleteFailure)
        }
    }
}