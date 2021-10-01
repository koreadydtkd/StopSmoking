package hys.hmonkeyys.stopsmoking.activitys.community

import hys.hmonkeyys.stopsmoking.data.entity.CommunityModel


sealed class  CommunityState {
    object Initialize : CommunityState()

    data class PostFetchSuccess(
        val communityList: MutableList<CommunityModel>
    ) : CommunityState()

    data class PostMoreFetchSuccess(
        val communityMoreList: MutableList<CommunityModel>
    ) : CommunityState()
}