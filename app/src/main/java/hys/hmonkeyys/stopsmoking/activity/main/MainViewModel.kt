package hys.hmonkeyys.stopsmoking.activity.main

import android.content.SharedPreferences
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import hys.hmonkeyys.stopsmoking.activity.BaseViewModel
import hys.hmonkeyys.stopsmoking.utils.AppShareKey
import hys.hmonkeyys.stopsmoking.utils.Utility.dDayCalculation
import hys.hmonkeyys.stopsmoking.utils.toCommaWon
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import java.util.*

internal class MainViewModel(
    private val spf: SharedPreferences,
) : BaseViewModel() {

    private var _mainStateLiveData = MutableLiveData<MainState>()
    val mainLiveData: LiveData<MainState> = _mainStateLiveData

    private var elapsedDate = 0
    private var inputAmountOfSmoking = 0

    override fun fetchData(): Job = viewModelScope.launch {
        elapsedDate = getDDay()
        inputAmountOfSmoking = spf.getInt(AppShareKey.AMOUNT_OF_SMOKING_PER_DAY, 0)

        _mainStateLiveData.postValue(MainState.Initialize)
    }

    /** 오늘 날짜, 입력한 날짜로 d-day 계산 */
    fun getDDay(): Int {
        val stopSmokingDate = spf.getString(AppShareKey.STOP_SMOKING_DATE, "") ?: ""
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
        val inputPrice = spf.getInt(AppShareKey.TOBACCO_PRICE, 0)
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
                imageUrl = spf.getString(AppShareKey.FIREBASE_IMAGE_URL, "") ?: "",
                link = Link(
                    webUrl = "https://developers.kakao.com",
                    mobileWebUrl = "https://developers.kakao.com"
                )
            ),
            buttons = listOf(
                Button(
                    "금연 시작하기",
                    Link(
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
    fun getNickName(): String = spf.getString(AppShareKey.NICK_NAME, "OOO") ?: "OOO"

    /** 각오 가져오기 */
    fun getMyResolution(): String = spf.getString(AppShareKey.MY_RESOLUTION, "") ?: ""

    companion object {
        private const val TAG = "SS_MainViewModel"
        private const val DAY = 86400000 // ->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
        private const val ONE_CIGARETTE_LIFE_SPAN = 11
    }
}