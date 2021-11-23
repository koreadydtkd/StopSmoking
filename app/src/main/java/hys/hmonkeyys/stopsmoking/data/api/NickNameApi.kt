package hys.hmonkeyys.stopsmoking.data.api

interface NickNameApi {

    suspend fun checkForDuplicateNickname(nickName: String): Boolean

    suspend fun saveNickName(nickName: String)

}