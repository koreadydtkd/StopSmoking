package hys.hmonkeyys.stopsmoking.utils

import android.os.Handler
import android.os.Looper
import android.text.format.DateFormat
import android.view.View
import java.text.DecimalFormat

/** 두번 클릭 방지 */
fun View.setOnDuplicatePreventionClickListener(OnDuplicatePreventionClick: () -> Unit) {
    this.setOnClickListener {
        it.isEnabled = false
        OnDuplicatePreventionClick()

        // 0.5초 후 다시 클릭 가능
        Handler(Looper.getMainLooper()).postDelayed({ it.isEnabled = true }, 500)
    }
}

/** Int 10000 -> 10,000원 으로 변환 */
fun Int.toCommaWon() = DecimalFormat("###,###").format(this).plus("원")


fun Long.convertTimeStampToDateFormat(): String = DateFormat.format("yyyy-MM-dd HH:mm:ss", this).toString()
