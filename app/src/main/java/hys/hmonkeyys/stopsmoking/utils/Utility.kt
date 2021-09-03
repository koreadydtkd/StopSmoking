package hys.hmonkeyys.stopsmoking.utils

import android.app.Activity
import android.content.Intent
import android.os.Handler
import android.os.Looper
import android.text.Spannable

import android.text.style.ForegroundColorSpan

import android.text.SpannableStringBuilder
import android.view.View
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.DB_FCM_TOKEN


object Utility {

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
    fun showSnackBar(view: View, text: String) {
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

    /** Firebase DB 에 token 값 저장 */
    /*fun saveTokenInFirebase(token: String) {
        Firebase.database.reference.child(DB_FCM_TOKEN).push().setValue(token)
    }*/
}

