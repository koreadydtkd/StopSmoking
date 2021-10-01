package hys.hmonkeyys.stopsmoking.extension

import java.text.DecimalFormat

/** Int 10000 -> 10,000원 으로 변환 */
fun Int.toCommaWon() = DecimalFormat("###,###").format(this).plus("원")