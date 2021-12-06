package hys.hmonkeyys.stopsmoking.screen.intro

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.IS_REGISTRATION
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.USER_UID
import hys.hmonkeyys.stopsmoking.data.repository.linkimage.LinkImageRepository
import hys.hmonkeyys.stopsmoking.data.repository.user.UserRepository
import hys.hmonkeyys.stopsmoking.screen.BaseViewModel
import hys.hmonkeyys.stopsmoking.utils.Constant.FIREBASE_IMAGE_URL
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class IntroViewModel(
    private val userRepository: UserRepository,
    private val linkImageRepository: LinkImageRepository,
    private val pref: AppPreferenceManager,
) : BaseViewModel() {

    private var _introStateLiveData = MutableLiveData<IntroState>()
    val introLiveData: LiveData<IntroState> = _introStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _introStateLiveData.postValue(IntroState.Initialize)
    }

    /** App 처음 실행 여부 */
    fun isFirstTime() = pref.getIsFirst(IS_REGISTRATION)

    /** 가입여부 확인 */
    fun checkCurrentUser() = viewModelScope.launch {
        val userUid = pref.getString(USER_UID)
        userUid?.let {
            // 링크 공유 시 전달할 이미지 가져오기
            getKakaoLinkImageUrl()
        } ?: kotlin.run {
            anonymousSignup()
        }
    }

    /** 익명 가입 - 성공, 실패 여부 상관없이 카카오링크 이미지 가져오기 */
    private fun anonymousSignup() = viewModelScope.launch {
        userRepository.getUserUid()?.let {
            pref.setString(USER_UID, it)
        }
        getKakaoLinkImageUrl()
    }

    /** 카카오링크 공유 시 전달할 이미지 가져오기 */
    private fun getKakaoLinkImageUrl() = viewModelScope.launch {
        linkImageRepository.getImageUrlForKakaoLink()?.let {
            pref.setString(FIREBASE_IMAGE_URL, it.toString())
        }
        _introStateLiveData.postValue(IntroState.GetImageUrlForKakaoLink)
    }

    companion object {
//        private const val TAG = "SS_IntroViewModel"
    }
}