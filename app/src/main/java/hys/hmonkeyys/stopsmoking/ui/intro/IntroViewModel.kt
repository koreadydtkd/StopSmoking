package hys.hmonkeyys.stopsmoking.ui.intro

import android.net.ConnectivityManager
import androidx.core.content.ContextCompat.getSystemService
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import hys.hmonkeyys.stopsmoking.ui.BaseViewModel
import hys.hmonkeyys.stopsmoking.data.preference.PreferenceManager
import hys.hmonkeyys.stopsmoking.ui.main.MainState
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.FIREBASE_IMAGE_URL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.IS_REGISTRATION
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class IntroViewModel(
    private val preferenceManager: PreferenceManager,
) : BaseViewModel() {

    private var _introStateLiveData = MutableLiveData<IntroState>()
    val introLiveData: LiveData<IntroState> = _introStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _introStateLiveData.postValue(IntroState.Initialize)
    }

    /** 카카오링크 공유 시 전달할 이미지 가져오기 */
    fun getKakaoLinkImageUrl() {
        Firebase.storage.reference.child(IMAGE_URL_PATH).child(IMAGE_NAME).downloadUrl
            .addOnSuccessListener { uri ->
                preferenceManager.putString(FIREBASE_IMAGE_URL, uri.toString())
                _introStateLiveData.postValue(IntroState.GetImageUrlForKakaoLink)
            }.addOnFailureListener {
                FirebaseCrashlytics.getInstance().recordException(it)
                _introStateLiveData.postValue(IntroState.GetImageUrlForKakaoLink)
            }
    }

    /** App 처음 실행 여부 */
    fun isFirstTime() = preferenceManager.getBoolean(IS_REGISTRATION)

    companion object {
//        private const val TAG = "SS_IntroViewModel"
        private const val IMAGE_URL_PATH = "stopsmoking"
        private const val IMAGE_NAME = "icon.png"
    }
}