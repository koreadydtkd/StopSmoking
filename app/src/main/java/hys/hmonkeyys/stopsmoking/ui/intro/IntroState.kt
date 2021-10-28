package hys.hmonkeyys.stopsmoking.ui.intro

sealed class IntroState {
    object Initialize: IntroState()

    object GetImageUrlForKakaoLink : IntroState()
}