package hys.hmonkeyys.stopsmoking.activitys.write

sealed class WriteState {
    object Initialize : WriteState()

    data class RegisterSuccess(
        val isSuccess: Boolean,
    ) : WriteState()
}
