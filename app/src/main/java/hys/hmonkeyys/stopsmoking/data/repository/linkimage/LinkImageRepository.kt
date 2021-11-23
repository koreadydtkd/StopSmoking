package hys.hmonkeyys.stopsmoking.data.repository.linkimage

import android.net.Uri

interface LinkImageRepository {

    suspend fun getImageUrlForKakaoLink(): Uri?

}