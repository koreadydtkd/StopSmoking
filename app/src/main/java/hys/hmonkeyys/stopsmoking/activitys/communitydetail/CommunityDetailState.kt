package hys.hmonkeyys.stopsmoking.activitys.communitydetail

import hys.hmonkeyys.stopsmoking.data.entity.CommentModel

sealed class CommunityDetailState {
    object Initialized : CommunityDetailState()

    data class FetChCommentList(
        val commentList: List<CommentModel>
    ) : CommunityDetailState()
}
