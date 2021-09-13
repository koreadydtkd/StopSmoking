package hys.hmonkeyys.stopsmoking.activity.communitydetail

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hys.hmonkeyys.stopsmoking.activity.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class CommunityDetailViewModel(

) : BaseViewModel() {

    private var _communityDetailLiveData = MutableLiveData<CommunityDetailState>()
    val communityDetailLiveData: LiveData<CommunityDetailState> = _communityDetailLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _communityDetailLiveData.postValue(CommunityDetailState.Initialized)
    }

    fun fetchComments() {
        // todo 댓글 목록 가져오기
    }
}