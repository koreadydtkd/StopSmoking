package hys.hmonkeyys.stopsmoking.activitys.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import hys.hmonkeyys.stopsmoking.activitys.BaseViewModel
import hys.hmonkeyys.stopsmoking.data.entity.CommunityModel
import hys.hmonkeyys.stopsmoking.data.preference.PreferenceManager
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NICK_NAME
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_ALL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_FAIL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_MY_POST
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_OTHER
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_SUCCESS
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlin.Exception

internal class CommunityViewModel(
    private val communityDB: CollectionReference,
    private val preferenceManager: PreferenceManager,
) : BaseViewModel() {

    private var _communityStateLiveData = MutableLiveData<CommunityState>()
    val communityLiveData: LiveData<CommunityState> = _communityStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _communityStateLiveData.postValue(CommunityState.Initialize)
    }

    // 마지막 Snapshot
    private lateinit var lastAllValue: DocumentSnapshot
    private lateinit var lastMyPostValue: DocumentSnapshot
    private lateinit var lastNoSmokingSuccessValue: DocumentSnapshot
    private lateinit var lastNoSmokingFailValue: DocumentSnapshot
    private lateinit var lastOtherValue: DocumentSnapshot

    /** 게시물 가져오기
     * 전체 -> 모든 카테고리 가져오기
     * 내가 쓴 글 -> 내가 작성한 글만 가져오기
     * 선택 카테고리 -> 선택한 카테고리 별 가져오기
     * */
    fun fetchPostList(postCategory: String) {
        when (postCategory) {
            // 전체
            NO_SMOKING_ALL -> {
                fetchListInFirebase("", "", postCategory)
            }

            // 내가 쓴 글
            NO_SMOKING_MY_POST -> {
                val nickName = preferenceManager.getString(NICK_NAME)
                nickName?.let { nick ->
                    fetchListInFirebase(FIELD_WRITER, nick, postCategory)
                }
            }

            // 해당 카테고리
            else -> {
                fetchListInFirebase(FIELD_CATEGORY, postCategory, postCategory)
            }
        }
    }

    /** Firebase Post Data 가져오기 */
    private fun fetchListInFirebase(fieldName: String, fieldValue: String, postCategory: String) {
        if (fieldName.isEmpty() && fieldValue.isEmpty()) {
            communityDB
                .orderBy(FIELD_DATE, Query.Direction.DESCENDING)
                .limit(LIST_LIMIT)
                .get()
                .addOnSuccessListener {
                    fetchSuccess(postCategory, it)
                }
                .addOnFailureListener {
                    fetchFail(it)
                }
        } else {
            communityDB
                .whereEqualTo(fieldName, fieldValue)
                .orderBy(FIELD_DATE, Query.Direction.DESCENDING)
                .limit(LIST_LIMIT)
                .get()
                .addOnSuccessListener {
                    fetchSuccess(postCategory, it)
                }
                .addOnFailureListener {
                    fetchFail(it)
                }
        }
    }

    /** 게시물 추가로 가져오기
     * 전체 -> 모든 카테고리 가져오기
     * 내가 쓴 글 -> 내가 작성한 글만 가져오기
     * 선택 카테고리 -> 선택한 카테고리 별 가져오기
     * */
    fun fetchMorePostList(postCategory: String) {
        when (postCategory) {
            NO_SMOKING_ALL -> {
                fetchMoreListInFirebase("", "", postCategory, lastAllValue)
            }

            NO_SMOKING_MY_POST -> {
                val nickName = preferenceManager.getString(NICK_NAME)
                nickName?.let { nick ->
                    fetchMoreListInFirebase(FIELD_WRITER, nick, postCategory, lastMyPostValue)
                }
            }

            NO_SMOKING_SUCCESS -> {
                fetchMoreListInFirebase(FIELD_CATEGORY, postCategory, postCategory, lastNoSmokingSuccessValue)
            }

            NO_SMOKING_FAIL -> {
                fetchMoreListInFirebase(FIELD_CATEGORY, postCategory, postCategory, lastNoSmokingFailValue)
            }

            NO_SMOKING_OTHER -> {
                fetchMoreListInFirebase(FIELD_CATEGORY, postCategory, postCategory, lastOtherValue)
            }
        }
    }

    /** Firebase Post Data 추가로 가져오기 */
    private fun fetchMoreListInFirebase(fieldName: String, fieldValue: String, postCategory: String, lastValue: DocumentSnapshot) {
        if (fieldName.isEmpty() && fieldValue.isEmpty()) {
            communityDB
                .orderBy(FIELD_DATE, Query.Direction.DESCENDING)
                .startAfter(lastValue)
                .limit(LIST_LIMIT)
                .get()
                .addOnSuccessListener {
                    fetchSuccess(postCategory, it, true)
                }
                .addOnFailureListener {
                    fetchFail(it)
                }
        } else {
            communityDB
                .whereEqualTo(fieldName, fieldValue)
                .orderBy(FIELD_DATE, Query.Direction.DESCENDING)
                .startAfter(lastValue)
                .limit(LIST_LIMIT)
                .get()
                .addOnSuccessListener {
                    fetchSuccess(postCategory, it, true)
                }
                .addOnFailureListener {
                    fetchFail(it)
                }
        }
    }

    /** 가져오기 성공 */
    private fun fetchSuccess(postCategory: String, snapshot: QuerySnapshot, isMore: Boolean = false) {
        // 더 이상 가져올 게 업는 경우에 대한 예외 처리
        if (snapshot.size() == 0) {
            if (isMore) {
                _communityStateLiveData.postValue(CommunityState.PostMoreFetchSuccess(mutableListOf()))
            } else {
                _communityStateLiveData.postValue(CommunityState.PostFetchSuccess(mutableListOf()))
            }
            return
        }

        // 마지막 값 셋팅
        setLastValue(postCategory, snapshot)

        // 데이터 가져온 후 uid(DocumentId) 셋팅
        val communityList = snapshot.toObjects(CommunityModel::class.java)
        communityList.mapIndexed { index, communityModel ->
            communityModel.uid = snapshot.documents[index].id
        }

        if (isMore) {
            _communityStateLiveData.postValue(CommunityState.PostMoreFetchSuccess(communityList))
        } else {
            _communityStateLiveData.postValue(CommunityState.PostFetchSuccess(communityList))
        }
    }

    /** 선택 카테고리 마지막 값 세팅 */
    private fun setLastValue(postCategory: String, snapshot: QuerySnapshot) {
        when (postCategory) {
            // 모두 선택
            NO_SMOKING_ALL -> {
                lastAllValue = snapshot.documents[snapshot.size() - 1]
            }

            // 내가 쓴 글 선택
            NO_SMOKING_MY_POST -> {
                lastMyPostValue = snapshot.documents[snapshot.size() - 1]
            }

            // 금연 성공담 선택
            NO_SMOKING_SUCCESS -> {
                lastNoSmokingSuccessValue = snapshot.documents[snapshot.size() - 1]
            }

            // 금연 실패담 선택
            NO_SMOKING_FAIL -> {
                lastNoSmokingFailValue = snapshot.documents[snapshot.size() - 1]
            }

            // 잡담 선택
            NO_SMOKING_OTHER -> {
                lastOtherValue = snapshot.documents[snapshot.size() - 1]
            }
        }
    }

    /** 가져오기 실패 */
    private fun fetchFail(e: Exception) {
        _communityStateLiveData.postValue(CommunityState.PostFetchSuccess(mutableListOf()))
        FirebaseCrashlytics.getInstance().recordException(e)
    }

    companion object {
//        private const val TAG = "SS_CommunityViewModel"
        private const val LIST_LIMIT = 5L

        private const val FIELD_WRITER = "writer"
        private const val FIELD_CATEGORY = "category"
        private const val FIELD_DATE = "date"
    }
}