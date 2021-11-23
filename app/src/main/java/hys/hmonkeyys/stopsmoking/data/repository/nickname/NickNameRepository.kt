package hys.hmonkeyys.stopsmoking.data.repository.nickname

interface NickNameRepository {

    suspend fun checkForDuplicateNickname(nickName: String): Boolean

    suspend fun saveNickName(nickName: String)

}