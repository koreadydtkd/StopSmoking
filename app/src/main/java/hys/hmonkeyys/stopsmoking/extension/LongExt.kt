package hys.hmonkeyys.stopsmoking.extension

import android.text.format.DateFormat

/** Long -> 시간 패턴으로 변경 */
fun Long.convertTimeStampToDateFormat(): String = DateFormat.format("yyyy-MM-dd HH:mm:ss", this).toString()