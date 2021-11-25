package hys.hmonkeyys.stopsmoking.screen.main.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.NICK_NAME
import hys.hmonkeyys.stopsmoking.data.repository.community.CommunityRepository
import hys.hmonkeyys.stopsmoking.model.CommunityModel
import hys.hmonkeyys.stopsmoking.screen.BaseViewModel
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_ALL
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_MY_POST
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class CommunityViewModel(
    private val communityRepository: CommunityRepository,
    private val pref: AppPreferenceManager,
) : BaseViewModel() {

    private var _communityStateLiveData = MutableLiveData<CommunityState>()
    val communityLiveData: LiveData<CommunityState> = _communityStateLiveData

    private var lastItem: DocumentSnapshot? = null

    override fun fetchData(): Job = viewModelScope.launch {
        _communityStateLiveData.postValue(CommunityState.Initialize)
    }

    /** 게시물 가져오기
     * 전체 -> 모든 카테고리 가져오기
     * 내가 쓴 글 -> 내가 작성한 글만 가져오기
     * 선택 카테고리 -> 선택한 카테고리 별 가져오기
     * */
    fun fetchPostList(postCategory: String, isMore: Boolean = false) {
        pref.getString(NICK_NAME)?.let {
            val pair = when (postCategory) {
                // 전체
                NO_SMOKING_ALL -> Pair("", "")

                // 내가 쓴 글
                NO_SMOKING_MY_POST -> Pair(FIELD_WRITER, it)

                // 해당 카테고리
                else -> Pair(FIELD_CATEGORY, postCategory)
            }

            if (isMore) {
                fetchMoreListInFirebase(pair.first, pair.second)
            } else {
                lastItem = null
                fetchListInFirebase(pair.first, pair.second)
            }
        }
    }

    /** Firebase Post Data 가져오기 */
    private fun fetchListInFirebase(fieldName: String, fieldValue: String) = viewModelScope.launch {
        val communityPair = if (fieldName.isEmpty() && fieldValue.isEmpty()) {
            communityRepository.getCommunityList()
        } else {
            communityRepository.getSelectedCategoryCommunityList(fieldName, fieldValue)
        }
        convertList(communityPair)
    }

    /** Firebase Post Data 더 가져오기 */
    private fun fetchMoreListInFirebase(fieldName: String, fieldValue: String) = viewModelScope.launch {
        lastItem?.let {
            val communityMorePair = if (fieldName.isEmpty() && fieldValue.isEmpty()) {
                communityRepository.getMoreCommunityList(it)
            } else {
                communityRepository.getMoreSelectedCategoryCommunityList(it, fieldName, fieldValue)
            }
            convertList(communityMorePair, true)
        }
    }

    /** 리스트에 document id 추가 */
    private fun convertList(communityPair: Pair<List<CommunityModel>, QuerySnapshot>, isMore: Boolean = false) {
        val communityList = communityPair.first
        val querySnapshot = communityPair.second

        // 가져올 데이터가 없으면 return
        if (querySnapshot.size() == 0) {
            if (isMore) {
                _communityStateLiveData.postValue(CommunityState.PostFetchAll)
            } else {
                _communityStateLiveData.postValue(CommunityState.NoPost)
            }
            return
        }

        // 각 데이터에 document id 추가
        communityList.mapIndexed { index, communityModel ->
            communityModel.uid = querySnapshot.documents[index].id
        }

        // 추가로 불러오기 위해 마지막 값 저장
        lastItem = querySnapshot.documents[querySnapshot.size() - 1]

        if (isMore) {
            _communityStateLiveData.postValue(CommunityState.PostMoreFetchSuccess(communityList))
        } else {
            _communityStateLiveData.postValue(CommunityState.PostFetchSuccess(communityList))
        }
    }

    companion object {
//        private const val TAG = "SS_CommunityViewModel"

        private const val FIELD_WRITER = "writer"
        private const val FIELD_CATEGORY = "category"
    }
}