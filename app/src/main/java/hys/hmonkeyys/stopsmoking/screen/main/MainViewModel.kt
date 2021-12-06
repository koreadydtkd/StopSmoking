package hys.hmonkeyys.stopsmoking.screen.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.AMOUNT_OF_SMOKING_PER_DAY
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.MY_RESOLUTION
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.NICK_NAME
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.STOP_SMOKING_DATE
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager.Companion.TOBACCO_PRICE
import hys.hmonkeyys.stopsmoking.data.repository.user.UserRepository
import hys.hmonkeyys.stopsmoking.extension.toCommaWon
import hys.hmonkeyys.stopsmoking.screen.BaseViewModel
import hys.hmonkeyys.stopsmoking.utils.Constant.FIREBASE_IMAGE_URL
import hys.hmonkeyys.stopsmoking.utils.Utility.dDayCalculation
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

internal class MainViewModel(
    private val userRepository: UserRepository,
    private val pref: AppPreferenceManager
) : BaseViewModel() {

    private var _mainStateLiveData = MutableLiveData<MainState>()
    val mainLiveData: LiveData<MainState> = _mainStateLiveData

    private var elapsedDate = 0
    private var inputAmountOfSmoking = 0

    override fun fetchData(): Job = viewModelScope.launch {
        elapsedDate = getDDay()
        inputAmountOfSmoking = pref.getInt(AMOUNT_OF_SMOKING_PER_DAY)

        _mainStateLiveData.postValue(MainState.Initialize)
    }

    /** 유저아이디 확인 */
    fun getCurrentUser() = viewModelScope.launch {
        userRepository.getCurrentUser()?.let {
            _mainStateLiveData.postValue(MainState.HaveUid)
        } ?: kotlin.run {
            _mainStateLiveData.postValue(MainState.NoHaveUid)
        }
    }

    /** 시작 날짜 */
    fun getStartDay(): String? = pref.getString(STOP_SMOKING_DATE)

    /** 오늘 날짜, 입력한 날짜로 d-day 계산 */
    fun getDDay(): Int {
        val stopSmokingDate = getStartDay() ?: return -1
        return dDayCalculation(stopSmokingDate)
    }

    /** 1개비 11분 기준으로 늘어난 수명 계산 */
    fun getIncreasedLifespan(): String {
        val oneDay = inputAmountOfSmoking * ONE_CIGARETTE_LIFE_SPAN * elapsedDate

        val day = oneDay / 1440                 // 하루
        val hour = (oneDay % 1440) / 60         // 시간 - 하루를 나눈 후 나머지로 계산
        val minute = oneDay % 60                // 분

        return "${day}일 ${hour}시간 ${minute}분"
    }

    /** 입력한 담배값에 1개비 금액을 구한 후 하루에 피우는 개비 만큼 곱하기 */
    fun getTobaccoPrice(): String {
        val inputPrice = pref.getInt(TOBACCO_PRICE)
        val onePrice = inputPrice / 20

        return (onePrice * inputAmountOfSmoking * elapsedDate).toCommaWon()
    }

    /** 카카오 링크 피드 */
    fun getDefaultFeed(): FeedTemplate {
        // todo 원하는 태그 넣도록
        return FeedTemplate(
            content = Content(
                title = "금연 시작",
                description = "#금연 #${elapsedDate}일차 #다짐 #같이",
                imageUrl = pref.getString(FIREBASE_IMAGE_URL) ?: "",
                link = Link(
                    webUrl = KAKAO_URL,
                    mobileWebUrl = KAKAO_URL
                )
            ),
            buttons = listOf(
                Button(
                    title = "금연 시작하기",
                    link = Link(
                        androidExecutionParams = mapOf("key1" to "value1", "key2" to "value2"),
                        iosExecutionParams = mapOf("key1" to "value1", "key2" to "value2")
                    )
                )
            )
        )
    }

    /** 안피운 담배 개비 구하기 */
    fun getCigaretteCount(): Int = inputAmountOfSmoking * elapsedDate

    /** 닉네임 가져오기 */
    fun getNickName(): String = pref.getString(NICK_NAME) ?: "Error"

    companion object {
//        private const val TAG = "SS_MainViewModel"
        private const val ONE_CIGARETTE_LIFE_SPAN = 11
        private const val KAKAO_URL = "https://developers.kakao.com"
    }
}