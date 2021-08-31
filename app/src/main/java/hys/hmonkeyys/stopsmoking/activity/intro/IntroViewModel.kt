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
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class IntroViewModel(
    private val sharedPreferences: SharedPreferences
) : BaseViewModel() {

    private var _introStateLiveData = MutableLiveData<IntroState>()
    val introLiveData: LiveData<IntroState> = _introStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        getKakaoLinkImageUrl()
    }

    private fun getKakaoLinkImageUrl() {
        Firebase.storage.reference.child(IMAGE_URL_PATH).child(IMAGE_NAME).downloadUrl
            .addOnSuccessListener { uri ->
                sharedPreferences.edit().putString(AppShareKey.FIREBASE_IMAGE_URL, uri.toString()).apply()
                _introStateLiveData.postValue(IntroState.GetImageUrlForKakaoLink)
            }.addOnFailureListener {
                FirebaseCrashlytics.getInstance().recordException(it)
                _introStateLiveData.postValue(IntroState.GetImageUrlForKakaoLink)
            }
    }

    fun isFirstTime() = sharedPreferences.getBoolean(AppShareKey.IS_REGISTRATION, false)

    companion object {
        private const val IMAGE_URL_PATH = "stopsmoking"
        private const val IMAGE_NAME = "icon.png"
    }
}