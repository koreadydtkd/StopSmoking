package hys.hmonkeyys.stopsmoking.model

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class CommunityModel(
    var uid: String,
    val title: String,
    val category: String,
    val writer: String,
    val contents: String,
    val date: Long,
    val viewsCount: Long,
) : Parcelable {
    constructor() : this("","", "", "", "" , 0L, 0L)
}
