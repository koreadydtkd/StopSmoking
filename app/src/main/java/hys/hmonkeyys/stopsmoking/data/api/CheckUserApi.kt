package hys.hmonkeyys.stopsmoking.data.api

import com.google.firebase.auth.FirebaseUser

interface CheckUserApi {

    suspend fun getCurrentUser(): FirebaseUser?

    suspend fun getUserUid(): String?

}