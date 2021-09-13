package hys.hmonkeyys.stopsmoking.activity.write

sealed class WriteState {
    object Initialize : WriteState()

    data class RegisterSuccess(
        val isSuccess: Boolean,
    ) : WriteState()
}
