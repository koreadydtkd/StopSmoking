package hys.hmonkeyys.stopsmoking.screen.main.community

import hys.hmonkeyys.stopsmoking.model.CommunityModel


sealed class CommunityState {
    object Initialize : CommunityState()

    data class PostFetchSuccess(
        val communityList: List<CommunityModel>,
    ) : CommunityState()

    data class PostMoreFetchSuccess(
        val communityList: List<CommunityModel>,
    ) : CommunityState()

    object NoPost : CommunityState()

    object PostFetchAll : CommunityState()
}