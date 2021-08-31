package hys.hmonkeyys.stopsmoking.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kakao.sdk.link.LinkClient
import com.kakao.sdk.template.model.Button
import com.kakao.sdk.template.model.Content
import com.kakao.sdk.template.model.FeedTemplate
import com.kakao.sdk.template.model.Link
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.activity.registration.RegistrationActivity
import hys.hmonkeyys.stopsmoking.databinding.ActivityMainBinding
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.AMOUNT_OF_SMOKING_PER_DAY
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.APP_DEFAULT_KEY
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.EDIT
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.FIREBASE_IMAGE_URL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.MY_RESOLUTION
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NICK_NAME
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.STOP_SMOKING_DATE
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.TOBACCO_PRICE
import hys.hmonkeyys.stopsmoking.utils.Utility
import hys.hmonkeyys.stopsmoking.utils.setOnDuplicatePreventionClickListener
import hys.hmonkeyys.stopsmoking.utils.toCommaWon
import java.util.*

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val spf: SharedPreferences by lazy { getSharedPreferences(APP_DEFAULT_KEY, Context.MODE_PRIVATE) }

    private var elapsedDate = 0

    private var backPressTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initTextViews()
        initViews()
        initAdmob()
    }

    /** 금연 일 수 초기화 */
    private fun initTextViews() {
        // 상단 d-day TextView
        val stopSmokingDate = spf.getString(STOP_SMOKING_DATE, "") ?: ""
        val dDay = stopSmokingDate.split("-")
        elapsedDate = getDDay(dDay[0].toInt(), dDay[1].toInt(), dDay[2].toInt())
        binding.dDayTextView.text = getString(R.string.stop_smoking_d_day, elapsedDate)

        // 이름 부분 색상 변경
        val nickName = spf.getString(NICK_NAME, "OOO") ?: "OOO"
        binding.titleTextView.text = Utility.changePartialTextColor(
            getString(R.string.main_title, nickName),
            getColor(R.color.black),
            0,
            nickName.length
        )

        binding.myResolutionTextView.text = spf.getString(MY_RESOLUTION, "") ?: ""

        val inputAmountOfSmoking = spf.getInt(AMOUNT_OF_SMOKING_PER_DAY, 0)
        binding.cigarettesSavedTextView.text = getString(R.string.main_save_tobacco, inputAmountOfSmoking * elapsedDate)
        binding.increasedLifespanTextView.text = getIncreasedLifespan(inputAmountOfSmoking)
        binding.saveMoneyTextView.text = getTobaccoPrice(inputAmountOfSmoking)
    }

    /** 각 뷰 초기화 */
    private fun initViews() {
        binding.editCardView.setOnDuplicatePreventionClickListener {
            goEditActivity()
        }

        binding.shareButton.setOnDuplicatePreventionClickListener {
            shareKakaoLink()
        }

        binding.bodyChangesLayout.setOnDuplicatePreventionClickListener {
            startActivity(Intent(this, BodyChangesActivity::class.java))
        }

        binding.communityLayout.setOnDuplicatePreventionClickListener {
            showSnackBar("현재 준비중입니다. 조금만 기다려주세요.")
        }
    }

    /** 수정(등록) 화면으로 이동 - 등록화면 재 활용 */
    private fun goEditActivity() {
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.putExtra(EDIT, true)
        startActivity(intent)
        finish()
    }

    /** 카카오 링크 공유 */
    private fun shareKakaoLink() {
        val keyHash = com.kakao.sdk.common.util.Utility.getKeyHash(this)
        Log.e(TAG, "Hash Key: $keyHash")

        // 피드 메시지 보내기
        // 카카오톡 설치여부 확인
        try {
            if (LinkClient.instance.isKakaoLinkAvailable(this)) {
                // 카카오톡으로 카카오링크 공유 가능
                LinkClient.instance.defaultTemplate(this, getDefaultFeed()) { linkResult, error ->
                    if (error != null) {
                        showSnackBar(getString(R.string.message_kakao_link_send_fail))
                    } else if (linkResult != null) {
                        startActivity(linkResult.intent)

                        // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                        Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                        Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                    }
                }
            } else {
                showSnackBar(getString(R.string.message_kakao_not_install))
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            showSnackBar(getString(R.string.message_kakao_error))
            e.printStackTrace()
        }

    }

    /** 1개비 11분 기준으로 늘어난 수명 계산 */
    private fun getIncreasedLifespan(inputAmountOfSmoking: Int): String {
        val oneDay = inputAmountOfSmoking * 11 * elapsedDate

        val day = oneDay / 1440                 // 하루
        val hour = (oneDay % 1440) / 60         // 시간 - 하루를 나눈 후 나머지로 계산
        val minute = oneDay % 60                // 분

        return "${day}일 ${hour}시간 ${minute}분"
    }

    /** 입력한 담배값에 1개비 금액을 구한 후 하루에 피우는 개비 만큼 곱하기 */
    private fun getTobaccoPrice(inputAmountOfSmoking: Int): String {
        val inputPrice = spf.getInt(TOBACCO_PRICE, 0)
        val onePrice = inputPrice / 20

        return (onePrice * inputAmountOfSmoking * elapsedDate).toCommaWon()
    }

    /** 하단 배너광고 초기화 */
    private fun initAdmob() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()

        binding.adView.apply {
            loadAd(adRequest)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.i(TAG, "광고가 문제 없이 로드됨 onAdLoaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    Log.e(TAG, "광고 로드에 문제 발생 onAdFailedToLoad ${error.message}")
                }
            }
        }
    }

    /** 오늘 날짜, 입력한 날짜로 d-day 계산 */
    private fun getDDay(year: Int, month: Int, day: Int): Int {
        return try {
            val todayCal = Calendar.getInstance()
            val dDayCal = Calendar.getInstance()

            // D-day의 날짜를 입력
            dDayCal[year, month - 1] = day

            val today = todayCal.timeInMillis / DAY
            val dDay = dDayCal.timeInMillis / DAY

            // 오늘 날짜에서 d day 날짜를 빼기
            val count = today - dDay

            // 오늘 부터 시작이면 1일 차
            count.toInt() + 1
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }

    /** 카카오 링크 피드 */
    private fun getDefaultFeed(): FeedTemplate {
        return FeedTemplate(
            content = Content(
                title = "금연 시작",
                description = "#금연 #${elapsedDate}일차 #다짐 #같이",
                imageUrl = spf.getString(FIREBASE_IMAGE_URL, "") ?: "",
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

    /** 스낵바 띄우기 */
    private fun showSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        val time = System.currentTimeMillis()
        if (time - backPressTime > ONE_POINT_FIVE_SECOND) {
            showSnackBar(getString(R.string.message_backward_finish))
            backPressTime = time
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TAG = "SS_MainActivity"
        private const val DAY = 86400000 // ->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
        private const val ONE_POINT_FIVE_SECOND = 1500L
    }
}