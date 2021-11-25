package hys.hmonkeyys.stopsmoking.data.api

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.tasks.await

class CheckUserFireAuthApi(
    private val firebaseAuth: FirebaseAuth
): CheckUserApi {

    override suspend fun getCurrentUser(): FirebaseUser? {
        return firebaseAuth.currentUser
    }

    override suspend fun getUserUid(): String? {
        return firebaseAuth.signInAnonymously().await().user?.uid
    }
}