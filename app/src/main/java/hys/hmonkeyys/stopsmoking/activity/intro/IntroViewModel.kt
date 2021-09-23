package hys.hmonkeyys.stopsmoking.activity.intro

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import hys.hmonkeyys.stopsmoking.activity.BaseViewModel
import hys.hmonkeyys.stopsmoking.utils.AppShareKey
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.IS_REGISTRATION
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class IntroViewModel(
    private val spf: SharedPreferences,
) : BaseViewModel() {

    private var _introStateLiveData = MutableLiveData<IntroState>()
    val introLiveData: LiveData<IntroState> = _introStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        getKakaoLinkImageUrl()
    }

    /** 카카오링크 공유 시 전달할 이미지 가져오기 */
    private fun getKakaoLinkImageUrl() {
        Firebase.storage.reference.child(IMAGE_URL_PATH).child(IMAGE_NAME).downloadUrl
            .addOnSuccessListener { uri ->
                spf.edit().putString(AppShareKey.FIREBASE_IMAGE_URL, uri.toString()).apply()
                _introStateLiveData.postValue(IntroState.GetImageUrlForKakaoLink)
            }.addOnFailureListener {
                FirebaseCrashlytics.getInstance().recordException(it)
                _introStateLiveData.postValue(IntroState.GetImageUrlForKakaoLink)
            }

    }

    /** App 처음 실행 여부 */
    fun isFirstTime() = spf.getBoolean(IS_REGISTRATION, false)

    companion object {
        private const val TAG = "SS_IntroViewModel"
        private const val IMAGE_URL_PATH = "stopsmoking"
        private const val IMAGE_NAME = "icon.png"
    }
}