package hys.hmonkeyys.stopsmoking.model

data class CommentModel(
    var uid: String,
    val writer: String,
    val date: Long,
    val comment: String,
) {
    constructor() : this("", "", 0L, "")
}
