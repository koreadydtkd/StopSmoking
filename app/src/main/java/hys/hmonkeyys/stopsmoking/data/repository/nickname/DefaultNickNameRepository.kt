package hys.hmonkeyys.stopsmoking.data.repository.nickname

import hys.hmonkeyys.stopsmoking.data.api.NickNameApi
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class DefaultNickNameRepository(
    private val nickNameApi: NickNameApi,
    private val ioDispatcher: CoroutineDispatcher
): NickNameRepository {

    override suspend fun checkForDuplicateNickname(nickName: String): Boolean = withContext(ioDispatcher) {
        nickNameApi.checkForDuplicateNickname(nickName)
    }

    override suspend fun saveNickName(nickName: String) {
        nickNameApi.saveNickName(nickName)
    }
}