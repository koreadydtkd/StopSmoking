package hys.hmonkeyys.stopsmoking.ui.write

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.firestore.CollectionReference
import hys.hmonkeyys.stopsmoking.ui.BaseViewModel
import hys.hmonkeyys.stopsmoking.data.entity.CommunityModel
import hys.hmonkeyys.stopsmoking.data.preference.PreferenceManager
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NICK_NAME
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class WriteViewModel(
    private val preferenceManager: PreferenceManager,
    private val communityDB: CollectionReference
) : BaseViewModel() {

    private var _writeStateLiveData = MutableLiveData<WriteState>()
    val writeStateLiveData: LiveData<WriteState> = _writeStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _writeStateLiveData.postValue(WriteState.Initialize)
    }

    /** 닉네임 확인 후 글 등록 */
    fun registerPost(title: String, category: String, contents: String) {
        val nickName = preferenceManager.getString(NICK_NAME)
        if (nickName == null) {
            _writeStateLiveData.postValue(WriteState.RegisterSuccess(false))
            return
        }

        val communityPost = CommunityModel("" ,title, category, nickName, contents, System.currentTimeMillis(), 0L)

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