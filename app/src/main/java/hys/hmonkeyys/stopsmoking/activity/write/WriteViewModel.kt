package hys.hmonkeyys.stopsmoking.activity.write

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hys.hmonkeyys.stopsmoking.activity.BaseViewModel
import hys.hmonkeyys.stopsmoking.data.entity.CommunityModel
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.DB_Community
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NICK_NAME
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class WriteViewModel(
    private val spf: SharedPreferences,
    private val communityDB: CollectionReference
) : BaseViewModel() {

    private var _writeStateLiveData = MutableLiveData<WriteState>()
    val writeStateLiveData: LiveData<WriteState> = _writeStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _writeStateLiveData.postValue(WriteState.Initialize)
    }

    /** 닉네임 확인 후 글 등록 */
    fun registerPost(title: String, category: String, contents: String) {
        val nickName = spf.getString(NICK_NAME, null)
        if (nickName == null) {
            _writeStateLiveData.postValue(WriteState.RegisterSuccess(false))
            return
        }

        val communityPost = CommunityModel(title, category, nickName, contents, System.currentTimeMillis())

        communityDB.add(communityPost)
            .addOnSuccessListener {
                _writeStateLiveData.postValue(WriteState.RegisterSuccess(true))
            }
            .addOnFailureListener {
                _writeStateLiveData.postValue(WriteState.RegisterSuccess(false))
                FirebaseCrashlytics.getInstance().recordException(it)
            }
    }
}