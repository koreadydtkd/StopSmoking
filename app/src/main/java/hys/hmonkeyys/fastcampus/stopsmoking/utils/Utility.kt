package hys.hmonkeyys.fastcampus.stopsmoking.utils

import android.graphics.Color
import android.text.Spannable

import android.text.style.ForegroundColorSpan

import android.text.SpannableStringBuilder


object Utility {

    /** 일부 텍스트 색상 변경 */
    fun changePartialTextColor(text: String, color: Int, start: Int, end: Int): SpannableStringBuilder {
        val ssb = SpannableStringBuilder(text)
        ssb.setSpan(ForegroundColorSpan(color), start, end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        return ssb
    }
}

