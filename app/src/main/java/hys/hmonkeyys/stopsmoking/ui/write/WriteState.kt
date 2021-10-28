package hys.hmonkeyys.stopsmoking.ui.write

sealed class WriteState {
    object Initialize : WriteState()

    data class RegisterSuccess(
        val isSuccess: Boolean,
    ) : WriteState()
}
