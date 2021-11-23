package hys.hmonkeyys.stopsmoking.data.repository.linkimage

import android.net.Uri
import hys.hmonkeyys.stopsmoking.data.api.LinkImageApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultLinkImageRepository(
    private val linkImageApi: LinkImageApi,
    private val dispatcher: CoroutineDispatcher
) : LinkImageRepository {

    override suspend fun getImageUrlForKakaoLink(): Uri? = withContext(dispatcher) {
        linkImageApi.getImageUrlForKakaoLink()
    }
}