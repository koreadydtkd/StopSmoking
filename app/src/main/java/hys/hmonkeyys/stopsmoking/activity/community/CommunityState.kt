package hys.hmonkeyys.stopsmoking.activity.community

import hys.hmonkeyys.stopsmoking.data.entity.CommunityModel


sealed class  CommunityState {
    object Initialize : CommunityState()

    data class PostFetchSuccess(
        val communityList: List<CommunityModel>? = null
    ) : CommunityState()
}