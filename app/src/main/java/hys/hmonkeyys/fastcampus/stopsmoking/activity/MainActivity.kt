package hys.hmonkeyys.fastcampus.stopsmoking.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import hys.hmonkeyys.fastcampus.stopsmoking.R
import hys.hmonkeyys.fastcampus.stopsmoking.utils.AppShareKey.Companion.EDIT
import hys.hmonkeyys.fastcampus.stopsmoking.utils.AppShareKey.Companion.SHARED_PREFERENCES_KEY
import hys.hmonkeyys.fastcampus.stopsmoking.utils.AppShareKey.Companion.STOP_SMOKING_DATE
import hys.hmonkeyys.fastcampus.stopsmoking.databinding.ActivityMainBinding
import hys.hmonkeyys.fastcampus.stopsmoking.utils.AppShareKey.Companion.AMOUNT_OF_SMOKING_PER_DAY
import hys.hmonkeyys.fastcampus.stopsmoking.utils.AppShareKey.Companion.MY_RESOLUTION
import hys.hmonkeyys.fastcampus.stopsmoking.utils.AppShareKey.Companion.NICK_NAME
import hys.hmonkeyys.fastcampus.stopsmoking.utils.AppShareKey.Companion.TOBACCO_PRICE
import hys.hmonkeyys.fastcampus.stopsmoking.utils.setOnDuplicatePreventionClickListener
import hys.hmonkeyys.fastcampus.stopsmoking.utils.toCommaWon
import java.lang.Exception
import java.util.*

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val spf: SharedPreferences by lazy { getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE) }

    private var elapsedDate = 0

    private var backPressTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initDDayTextView()
        initViews()
        initAdmob()
    }

    /** 금연 일 수 초기화 */
    private fun initDDayTextView() {
        val stopSmokingDate = spf.getString(STOP_SMOKING_DATE, "") ?: ""
        val dDay = stopSmokingDate.split("-")

        elapsedDate = getDDay(dDay[0].toInt(), dDay[1].toInt(), dDay[2].toInt())
        binding.dDayTextView.text = getString(R.string.stop_smoking_d_day, elapsedDate)
    }

    /** 각 뷰 초기화 */
    private fun initViews() {
        binding.editCardView.setOnDuplicatePreventionClickListener {
            goEditActivity()
        }

        binding.shareButton.setOnDuplicatePreventionClickListener {
            // todo 카카오 로그인 팝업 띄우고 로그인 되면 공유하도록
        }

        binding.bodyChangesLayout.setOnDuplicatePreventionClickListener {
            startActivity(Intent(this, BodyChangesActivity::class.java))
        }

        binding.communityLayout.setOnDuplicatePreventionClickListener {
            showSnackBar("현재 준비중입니다. 조금만 기다려주세요.")
        }

        val nickName = spf.getString(NICK_NAME, "OOO") ?: "OOO"
        binding.titleTextView.text = getString(R.string.main_title, nickName)

        binding.myResolutionTextView.text = spf.getString(MY_RESOLUTION, "") ?: ""

        val inputAmountOfSmoking = spf.getInt(AMOUNT_OF_SMOKING_PER_DAY, 0)
        binding.increasedLifespanTextView.text = getIncreasedLifespan(inputAmountOfSmoking)
        binding.saveMoneyTextView.text = getTobaccoPrice(inputAmountOfSmoking)
    }

    /** 수정(등록) 화면으로 이동 - 등록화면 재 활용 */
    private fun goEditActivity() {
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.putExtra(EDIT, true)
        startActivity(intent)
        finish()
    }

    /** 1개비 11분 기준으로 늘어난 수명 계산 */
    private fun getIncreasedLifespan(inputAmountOfSmoking: Int): String {
        val oneDay = inputAmountOfSmoking * 11 * elapsedDate
        val day = oneDay / 1440      // 하루
        val hour = oneDay / 60       // 시간
        val minute = oneDay % 60     // 분

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

    // 오늘 날짜, 입력한 날짜로 d-day 계산
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

    private fun showSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        val time = System.currentTimeMillis()
        if (time - backPressTime > ONE_POINT_FIVE_SECOND) {
            showSnackBar(getString(R.string.snack_backward_finish))
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