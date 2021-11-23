package hys.hmonkeyys.stopsmoking.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Network
import android.os.Handler
import android.os.Looper
import android.text.Spannable

import android.text.style.ForegroundColorSpan

import android.text.SpannableStringBuilder
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.google.android.material.snackbar.Snackbar
import android.util.TypedValue
import androidx.core.content.ContextCompat.getSystemService
import java.util.*


object Utility {
    // (24 * 60 * 60 * 1000) 24시간 60분 60초 * (ms초 -> 초 변환 1000)
    private const val DAY = 86400000

    /** network 연결 여부 확인 */
    fun isNetworkConnecting(context: Context): Network? = context.getSystemService(ConnectivityManager::class.java).activeNetwork

    /** 일부 텍스트 색상 변경 */
    fun changePartialTextColor(text: String, color: Int, start: Int, end: Int): SpannableStringBuilder {
        val ssb = SpannableStringBuilder(text)
        ssb.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ssb
    }

    /** 금연 시작날짜 가져오기 */
    fun getDatePicker(year: Int, inputMonth: Int, inputDay: Int): String {
        val month = "%02d".format(inputMonth + 1)
        val day = "%02d".format(inputDay)
        return "${year}-${month}-$day"
    }

    /** 스낵바 띄우기 */
    fun snackBar(view: View, text: String) {
        Snackbar.make(view, text, Snackbar.LENGTH_SHORT).show()
    }

    /** 다음 화면으로 이동 - 딜레이, 액티비티 종료 여부 확인 */
    fun <T> goNextActivity(activity: Activity, clazz: Class<T>, delayMillis: Long, isFinish: Boolean = false) {
        Handler(Looper.getMainLooper()).postDelayed({
            activity.startActivity(Intent(activity, clazz))
            if (isFinish) {
                activity.finish()
            }
        }, delayMillis)
    }

    /** 키보드, 커서 숨기기 */
    fun hideKeyboardAndCursor(context: Context, view: View) {
        val inputMethodManager = context.getSystemService(Context.INPUT_METHOD_SERVICE) as? InputMethodManager
        inputMethodManager?.hideSoftInputFromWindow(view.windowToken, 0) // 키보드 숨기기
        view.clearFocus() // 커서 숨기기
    }

    /** dp Turn px */
    fun dp2px(context: Context, dpVal: Float): Int {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, dpVal, context.resources.displayMetrics).toInt()
    }

    /** 오늘 날짜, 입력한 날짜로 d-day 계산 */
    fun dDayCalculation(stopSmokingDate: String): Int {
        val dDayList = stopSmokingDate.split("-")

        val year = dDayList[0].toInt()
        val month = dDayList[1].toInt()
        val day = dDayList[2].toInt()

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


}

