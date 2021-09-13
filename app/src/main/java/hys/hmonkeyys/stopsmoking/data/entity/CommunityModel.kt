package hys.hmonkeyys.stopsmoking.data.entity

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CommunityModel(
    val title: String,
    val category: String,
    val writer: String,
    val contents: String,
    val date: Long
) : Parcelable {
    constructor() : this("", "", "", "" , 0L)
}
