package hys.hmonkeyys.stopsmoking.screen.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.IS_REGISTRATION
import hys.hmonkeyys.stopsmoking.data.repository.linkimage.LinkImageRepository
import hys.hmonkeyys.stopsmoking.screen.BaseViewModel
import hys.hmonkeyys.stopsmoking.utils.Constant.FIREBASE_IMAGE_URL
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class IntroViewModel(
    private val linkImageRepository: LinkImageRepository,
    private val pref: AppPreferenceManager
) : BaseViewModel() {

    private var _introStateLiveData = MutableLiveData<IntroState>()
    val introLiveData: LiveData<IntroState> = _introStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _introStateLiveData.postValue(IntroState.Initialize)
    }

    /** 카카오링크 공유 시 전달할 이미지 가져오기 */
    fun getKakaoLinkImageUrl() = viewModelScope.launch {
        linkImageRepository.getImageUrlForKakaoLink()?.let {
            pref.setString(FIREBASE_IMAGE_URL, it.toString())
        }
        _introStateLiveData.postValue(IntroState.GetImageUrlForKakaoLink)
    }

    /** App 처음 실행 여부 */
    fun isFirstTime() = pref.getIsFirst(IS_REGISTRATION)

    companion object {
//        private const val TAG = "SS_IntroViewModel"
    }
}