package hys.hmonkeyys.stopsmoking.screen.main.community.write

sealed class WriteState {
    object Initialize : WriteState()

    object RegistrationSuccess : WriteState()

    object RegistrationFailed : WriteState()

}
