package hys.hmonkeyys.stopsmoking.screen.main.community.write

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hys.hmonkeyys.stopsmoking.data.api.CommunityApi
import hys.hmonkeyys.stopsmoking.screen.BaseViewModel
import hys.hmonkeyys.stopsmoking.model.CommunityModel
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.NICK_NAME
import hys.hmonkeyys.stopsmoking.data.repository.community.CommunityRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class WriteViewModel(
    private val communityRepository: CommunityRepository,
    private val pref: AppPreferenceManager,
) : BaseViewModel() {

    private var _writeStateLiveData = MutableLiveData<WriteState>()
    val writeStateLiveData: LiveData<WriteState> = _writeStateLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _writeStateLiveData.postValue(WriteState.Initialize)
    }

    private fun getNickName(): String? = pref.getString(NICK_NAME)

    /** 닉네임 확인 후 글 등록 */
    fun registerPost(title: String, category: String, contents: String) = viewModelScope.launch {
        getNickName()?.let { nick ->
            val isSuccess = communityRepository
                .insertCommunityPost(CommunityModel("", title, category, nick, contents, System.currentTimeMillis(), 0L))

            if (isSuccess) {
                _writeStateLiveData.postValue(WriteState.RegistrationSuccess)
            } else {
                _writeStateLiveData.postValue(WriteState.RegistrationFailed)
            }
        } ?: kotlin.run {
            _writeStateLiveData.postValue(WriteState.RegistrationFailed)
        }
    }
}