package hys.hmonkeyys.fastcampus.stopsmoking

import android.os.Handler
import android.os.Looper
import android.view.View

/** 두번 클릭 방지 */
fun View.setOnDuplicatePreventionClickListener(OnDuplicatePreventionClick: () -> Unit) {
    this.setOnClickListener {
        it.isEnabled = false
        OnDuplicatePreventionClick()

        // 0.5초 후 다시 클릭 가능
        Handler(Looper.getMainLooper()).postDelayed({ it.isEnabled = true }, 500)
    }
}