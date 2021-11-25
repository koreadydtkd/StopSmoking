package hys.hmonkeyys.stopsmoking.data.repository.user

import com.google.firebase.auth.FirebaseUser

interface UserRepository {

    suspend fun getCurrentUser(): FirebaseUser?

    suspend fun getUserUid(): String?

}