package hys.hmonkeyys.stopsmoking.data.api

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ktx.getValue
import hys.hmonkeyys.stopsmoking.utils.Constant
import kotlinx.coroutines.tasks.await

class NickNameFirebaseApi(
    private val firebaseDatabase: FirebaseDatabase
) : NickNameApi {

    override suspend fun checkForDuplicateNickname(nickName: String): Boolean {
        var hasNickName = false

        firebaseDatabase
            .reference
            .child(Constant.DB_NICKNAME)
            .get()
            .await()
            .children.forEach { snapshot ->
                snapshot.getValue<String>()?.let {
                    if(nickName == it) {
                        hasNickName = true
                    }
                }
            }

        return hasNickName
    }

    override suspend fun saveNickName(nickName: String) {
        firebaseDatabase
            .reference
            .child(Constant.DB_NICKNAME)
            .push()
            .setValue(nickName)
    }
}