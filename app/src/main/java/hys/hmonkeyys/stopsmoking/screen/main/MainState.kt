package hys.hmonkeyys.stopsmoking.screen.main

sealed class MainState {

    object Initialize: MainState()

    object HaveUid: MainState()

    object NoHaveUid: MainState()
}
