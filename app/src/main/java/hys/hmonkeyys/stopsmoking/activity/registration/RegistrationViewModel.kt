package hys.hmonkeyys.stopsmoking.activity.registration

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
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
    private val sharedPreferences: SharedPreferences
) : BaseViewModel() {

    private var _registrationLiveData = MutableLiveData<RegistrationState>()
    val registrationLiveData: LiveData<RegistrationState> = _registrationLiveData

    override fun fetchData(): Job = viewModelScope.launch {
        _registrationLiveData.postValue(RegistrationState.Initialized)
    }

    fun saveNoSmokingInformation(
        isFirst: Boolean,
        datePicker: String,
        nickName: String,
        amountOfSmoking: Int,
        tobaccoPrice: Int,
        myResolution: String
    ) {
        sharedPreferences.edit {
            putBoolean(IS_REGISTRATION, isFirst)
            putString(STOP_SMOKING_DATE, datePicker)
            putString(NICK_NAME, nickName)
            putInt(AMOUNT_OF_SMOKING_PER_DAY, amountOfSmoking)
            putInt(TOBACCO_PRICE, tobaccoPrice)
            putString(MY_RESOLUTION, myResolution)
        }
    }
}