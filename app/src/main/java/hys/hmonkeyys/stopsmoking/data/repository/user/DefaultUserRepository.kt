package hys.hmonkeyys.stopsmoking.data.repository.user

import com.google.firebase.auth.FirebaseUser
import hys.hmonkeyys.stopsmoking.data.api.CheckUserApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultUserRepository(
    private val checkUserApi: CheckUserApi,
    private val ioDispatcher: CoroutineDispatcher,
): UserRepository {

    override suspend fun getCurrentUser(): FirebaseUser? = withContext(ioDispatcher) {
        checkUserApi.getCurrentUser()
    }

    override suspend fun getUserUid(): String? = withContext(ioDispatcher) {
        checkUserApi.getUserUid()
    }
}