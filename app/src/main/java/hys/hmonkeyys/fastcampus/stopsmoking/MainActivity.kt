package hys.hmonkeyys.fastcampus.stopsmoking

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
import hys.hmonkeyys.fastcampus.stopsmoking.SharedPreferencesKey.Companion.SHARED_PREFERENCES_KEY
import hys.hmonkeyys.fastcampus.stopsmoking.SharedPreferencesKey.Companion.STOP_SMOKING_DATE
import hys.hmonkeyys.fastcampus.stopsmoking.databinding.ActivityMainBinding
import java.lang.Exception
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    private val spf: SharedPreferences by lazy { getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE) }

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

        val elapsedDate = getDDay(dDay[0].toInt(), dDay[1].toInt(), dDay[2].toInt())
        binding.dDayTextView.text = getString(R.string.stop_smoking_d_day, elapsedDate)
    }

    /** 각 뷰 초기화 */
    private fun initViews() {

        binding.editCardView.setOnDuplicatePreventionClickListener {
            // todo 수정 화면으로 이동
            val intent = Intent(this, RegistrationActivity::class.java)
            intent.putExtra("edit", true)
            startActivity(intent)
            finish()
        }

        binding.bodyChangesLayout.setOnDuplicatePreventionClickListener {

        }

        binding.communityLayout.setOnDuplicatePreventionClickListener {

        }
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

    companion object {
        private const val TAG = "SS_MainActivity"
        private const val DAY = 86400000 // ->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
    }
}