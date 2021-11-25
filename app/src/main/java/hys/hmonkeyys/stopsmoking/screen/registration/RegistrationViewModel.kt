package hys.hmonkeyys.stopsmoking.screen.registration

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import hys.hmonkeyys.stopsmoking.data.api.NickNameApi
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.AMOUNT_OF_SMOKING_PER_DAY
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.IS_REGISTRATION
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.NICK_NAME
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.STOP_SMOKING_DATE
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.TOBACCO_PRICE
import hys.hmonkeyys.stopsmoking.screen.BaseViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class RegistrationViewModel(
    private val nickNameApi: NickNameApi,
    private val pref: AppPreferenceManager,
) : BaseViewModel() {

    private var _registrationLiveData = MutableLiveData<RegistrationState>()
    val registrationLiveData: LiveData<RegistrationState> = _registrationLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _registrationLiveData.postValue(RegistrationState.Initialized)
    }

    /** 저장한 금연 관련 정보 가져오기 */
    fun getDefaultData() {
        val date = pref.getString(STOP_SMOKING_DATE) ?: "2021-01-01"
        val dateArray = date.split("-")

        val nickName = pref.getString(NICK_NAME) ?: "OOO"
        val amountOfSmoking = pref.getInt(AMOUNT_OF_SMOKING_PER_DAY)
        val tobaccoPrice = pref.getInt(TOBACCO_PRICE)

        _registrationLiveData.postValue(RegistrationState.StoredValue(dateArray, nickName, amountOfSmoking, tobaccoPrice))
    }

    /** 닉네임 중복 확인 후 금연 정보 저장 */
    fun checkForDuplicateNickname(
        isFirst: Boolean,
        date: String,
        nickName: String,
        amountOfSmoking: Int,
        tobaccoPrice: Int,
    ) = viewModelScope.launch {
        // 닉네음 중복 확인
        val hasNickName = nickNameApi.checkForDuplicateNickname(nickName)
        if (hasNickName) {
            _registrationLiveData.postValue(RegistrationState.CheckNickName(false))
        } else {
            // 중복 없음. 정보 저장
            nickNameApi.saveNickName(nickName)
            savePref(isFirst, date, nickName, amountOfSmoking, tobaccoPrice)
            _registrationLiveData.postValue(RegistrationState.CheckNickName(true))
        }
    }

    /** 금연정보 pref 에 저장 */
    private fun savePref(
        isFirst: Boolean,
        date: String,
        nickName: String,
        amountOfSmoking: Int,
        tobaccoPrice: Int
    ) {
        pref.apply {
            setIsFirst(IS_REGISTRATION, isFirst)
            setString(STOP_SMOKING_DATE, date)
            setString(NICK_NAME, nickName)
            setInt(AMOUNT_OF_SMOKING_PER_DAY, amountOfSmoking)
            setInt(TOBACCO_PRICE, tobaccoPrice)
        }
    }

    /** 수정인 경우 금연 정보만 저장 */
    fun saveInfo(datePicker: String, amountOfSmoking: Int, tobaccoPrice: Int) {
        pref.apply {
            setString(STOP_SMOKING_DATE, datePicker)
            setInt(AMOUNT_OF_SMOKING_PER_DAY, amountOfSmoking)
            setInt(TOBACCO_PRICE, tobaccoPrice)
        }

        _registrationLiveData.postValue(RegistrationState.EditInformation)
    }
}