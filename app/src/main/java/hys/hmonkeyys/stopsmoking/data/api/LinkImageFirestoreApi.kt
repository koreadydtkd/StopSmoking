package hys.hmonkeyys.stopsmoking.data.api

import android.net.Uri
import com.google.firebase.storage.FirebaseStorage
import hys.hmonkeyys.stopsmoking.utils.Constant.IMAGE_NAME
import hys.hmonkeyys.stopsmoking.utils.Constant.IMAGE_URL_PATH
import kotlinx.coroutines.tasks.await

class LinkImageFirestoreApi(
    private val firebaseStorage: FirebaseStorage
) : LinkImageApi {

    override suspend fun getImageUrlForKakaoLink(): Uri? {
        return firebaseStorage
            .reference
            .child(IMAGE_URL_PATH)
            .child(IMAGE_NAME)
            .downloadUrl
            .await()
    }
}


