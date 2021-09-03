package hys.hmonkeyys.stopsmoking.activity.intro

import android.content.SharedPreferences
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
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

    fun isFirstTime() = spf.getBoolean(IS_REGISTRATION, false)

    /** FCM token 가져오기 */
    fun getFCMToken() {
        FirebaseMessaging.getInstance().token
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    saveToken(task.result)
                }
//                _introStateLiveData.postValue(IntroState.GetTokenSuccess)

            }.addOnFailureListener {
                FirebaseCrashlytics.getInstance().recordException(it)
//                _introStateLiveData.postValue(IntroState.GetTokenSuccess)
            }
    }

    /** 토큰 값 저장 */
    private fun saveToken(token: String?) {
        token?.let {
            Log.i(TAG, it)
//            spf.edit().putString(FCM_TOKEN, it).apply()
//            saveTokenInFirebase(token)
        }
    }

    companion object {
        private const val TAG = "SS_IntroViewModel"
        private const val IMAGE_URL_PATH = "stopsmoking"
        private const val IMAGE_NAME = "icon.png"
    }
}