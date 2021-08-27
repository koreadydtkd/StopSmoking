package hys.hmonkeyys.fastcampus.stopsmoking

import android.content.Context
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
        initAdmob()
    }

    /** 금연 일 수 초기화 */
    private fun initDDayTextView() {
        val stopSmokingDate = spf.getString(STOP_SMOKING_DATE, "") ?: ""
        val dDay = stopSmokingDate.split("-")

        val d = getDDay(dDay[0].toInt(), dDay[1].toInt(), dDay[2].toInt())
        binding.dDayTextView.text = getString(R.string.stop_smoking_text, d)
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


    private fun getCurrentDate(): String {
        val mDate = Date(System.currentTimeMillis())
        val simpleDate = SimpleDateFormat("yyyy-MM-dd")
        return simpleDate.format(mDate)
    }

    companion object {
        private const val TAG = "SS_MainActivity"
        private const val DAY = 86400000 //->(24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초->초 변환 1000)
    }

    private fun getDDay(year: Int, month: Int, day: Int): Int {
        return try {
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd")
            val todayCal = Calendar.getInstance() //오늘날자 가져오기
            val dDayCal = Calendar.getInstance() //오늘날자를 가져와 변경시킴

            dDayCal[year, month - 1] = day // D-day의 날짜를 입력

            Log.e("테스트", simpleDateFormat.format(todayCal.time).toString() + "")
            Log.e("테스트", simpleDateFormat.format(dDayCal.time).toString() + "")

            val today = todayCal.timeInMillis / DAY
            val dDay = dDayCal.timeInMillis / DAY

            // 오늘 날짜에서 d day 날짜를 빼기
            val count = today - dDay
            count.toInt()
        } catch (e: Exception) {
            e.printStackTrace()
            -1
        }
    }
}