package hys.hmonkeyys.stopsmoking.data.entity

data class CommentModel(
    var uid: String,
    val writer: String,
    val date: Long,
    val comment: String,
) {
    constructor() : this("", "", 0L, "")
}
