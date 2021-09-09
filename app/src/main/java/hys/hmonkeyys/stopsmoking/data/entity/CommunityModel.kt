package hys.hmonkeyys.stopsmoking.data.entity

data class CommunityModel(
    val title: String,
    val category: String,
    val writer: String,
    val contents: String,
    val date: Long
) {
    constructor() : this("", "", "", "" , 0L)
}
