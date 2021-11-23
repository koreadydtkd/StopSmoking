package hys.hmonkeyys.stopsmoking.data.api

import android.net.Uri

interface LinkImageApi {

    suspend fun getImageUrlForKakaoLink(): Uri?

}