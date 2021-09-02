package hys.hmonkeyys.stopsmoking.activity.registration

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.getValue
import hys.hmonkeyys.stopsmoking.activity.BaseViewModel
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.AMOUNT_OF_SMOKING_PER_DAY
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.IS_REGISTRATION
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.MY_RESOLUTION
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NICK_NAME
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.STOP_SMOKING_DATE
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.TOBACCO_PRICE
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class RegistrationViewModel(
    private val nickNameDB: DatabaseReference,
    private val spf: SharedPreferences,
) : BaseViewModel() {

    private var _registrationLiveData = MutableLiveData<RegistrationState>()
    val registrationLiveData: LiveData<RegistrationState> = _registrationLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _registrationLiveData.postValue(RegistrationState.Initialized)
    }

    /** 저장한 금연 관련 정보 가져오기 */
    fun getDefaultData() {
        val date = spf.getString(STOP_SMOKING_DATE, "2021-01-01") ?: "2021-01-01"
        val dateArray = date.split("-")

        val nickName = spf.getString(NICK_NAME, "OOO") ?: "OOO"
        val amountOfSmoking = spf.getInt(AMOUNT_OF_SMOKING_PER_DAY, 0)
        val tobaccoPrice = spf.getInt(TOBACCO_PRICE, 0)
        val myResolution = spf.getString(MY_RESOLUTION, "각오를 입력하세요.") ?: "각오를 입력하세요."

        _registrationLiveData.postValue(
            RegistrationState.StoredValue(dateArray, nickName, amountOfSmoking, tobaccoPrice, myResolution)
        )
    }

    /** 닉네임 중복 확인 후 금연 정보 저장 */
    fun checkForDuplicateNicknamesAndSaveInfo(
        isFirst: Boolean,
        datePicker: String,
        nickName: String,
        amountOfSmoking: Int,
        tobaccoPrice: Int,
        myResolution: String,
    ) {
        var hasNickname = false
        nickNameDB.get()
            .addOnSuccessListener {
                it.children.forEach { snapshot ->
                    val userNickName = snapshot.getValue<String>()
                    userNickName ?: return@addOnSuccessListener

                    if (userNickName == nickName) {
                        hasNickname = true
                    }
                }

                if (hasNickname.not()) {
                    // 중복 없음. 서버에 저장
                    nickNameDB.push().setValue(nickName)

                    // spf 저장
                    spf.edit {
                        putBoolean(IS_REGISTRATION, isFirst)
                        putString(STOP_SMOKING_DATE, datePicker)
                        putString(NICK_NAME, nickName)
                        putInt(AMOUNT_OF_SMOKING_PER_DAY, amountOfSmoking)
                        putInt(TOBACCO_PRICE, tobaccoPrice)
                        putString(MY_RESOLUTION, myResolution)
                    }

                    _registrationLiveData.postValue(RegistrationState.CheckNickName(true))
                } else {
                    _registrationLiveData.postValue(RegistrationState.CheckNickName(false))
                }
            }.addOnFailureListener {
                FirebaseCrashlytics.getInstance().recordException(it)
                _registrationLiveData.postValue(RegistrationState.CheckNickName(null))
            }
    }

    /** 수정인 경우 금연 정보만 저장 */
    fun saveInfo(datePicker: String, amountOfSmoking: Int, tobaccoPrice: Int, myResolution: String) {
        spf.edit {
            putString(STOP_SMOKING_DATE, datePicker)
            putInt(AMOUNT_OF_SMOKING_PER_DAY, amountOfSmoking)
            putInt(TOBACCO_PRICE, tobaccoPrice)
            putString(MY_RESOLUTION, myResolution)
        }
        _registrationLiveData.postValue(RegistrationState.EditInformation)
    }
}