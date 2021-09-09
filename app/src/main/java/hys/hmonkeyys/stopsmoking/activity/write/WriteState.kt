package hys.hmonkeyys.stopsmoking.activity.write

sealed class WriteState {
    object Initialize : WriteState()

    object NickNameError : WriteState()

    data class RegisterSuccess(
        val isSuccess: Boolean,
    ) : WriteState()
}
