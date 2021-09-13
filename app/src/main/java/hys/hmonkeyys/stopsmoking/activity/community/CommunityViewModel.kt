package hys.hmonkeyys.stopsmoking.activity.community

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.Query
import hys.hmonkeyys.stopsmoking.activity.BaseViewModel
import hys.hmonkeyys.stopsmoking.data.entity.CommunityModel
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_ALL
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class CommunityViewModel(
    private val communityDB: CollectionReference,
) : BaseViewModel() {

    private var _communityStateLiveData = MutableLiveData<CommunityState>()
    val communityLiveData: LiveData<CommunityState> = _communityStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _communityStateLiveData.postValue(CommunityState.Initialize)
    }


    /** 게시물 가져오기
     * 모두 -> 모든 카테고리 가져오기
     * 선택 카테고리 -> 선택한 카테고리 별 가져오기
     * */
    fun fetchPostList(postCategory: String) {
        if (postCategory == NO_SMOKING_ALL) {
            communityDB
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(LIST_LIMIT)
                .get()
                .addOnSuccessListener {
                    val communityList = it.toObjects(CommunityModel::class.java)
                    _communityStateLiveData.postValue(CommunityState.PostFetchSuccess(communityList))
                }
                .addOnFailureListener {
                    _communityStateLiveData.postValue(CommunityState.PostFetchSuccess(null))
                    FirebaseCrashlytics.getInstance().recordException(it)
                }
        } else {
            communityDB
                .whereEqualTo("category", postCategory)
                .orderBy("date", Query.Direction.DESCENDING)
                .limit(LIST_LIMIT)
                .get()
                .addOnSuccessListener {
                    val communityList = it.toObjects(CommunityModel::class.java)
                    _communityStateLiveData.postValue(CommunityState.PostFetchSuccess(communityList))
                }
                .addOnFailureListener {
                    _communityStateLiveData.postValue(CommunityState.PostFetchSuccess(null))
                    FirebaseCrashlytics.getInstance().recordException(it)
                }
        }


    }

    companion object {
        private const val LIST_LIMIT = 10L
    }
}