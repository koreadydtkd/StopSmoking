package hys.hmonkeyys.stopsmoking.screen.main.community.detail

import hys.hmonkeyys.stopsmoking.model.CommentModel

sealed class CommunityDetailState {
    object Initialized : CommunityDetailState()

    object Loading : CommunityDetailState()

    object CommentRegisterSuccess : CommunityDetailState()

    object CommentRegisterFailure : CommunityDetailState()

    data class FetChCommentList(
        val commentList: List<CommentModel>,
    ) : CommunityDetailState()

    object CommentDeleteSuccess : CommunityDetailState()

    object CommentDeleteFailure : CommunityDetailState()
}
