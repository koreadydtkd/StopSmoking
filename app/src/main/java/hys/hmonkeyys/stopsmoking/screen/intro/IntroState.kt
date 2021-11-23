package hys.hmonkeyys.stopsmoking.screen.intro

sealed class IntroState {

    object Initialize: IntroState()

    object GetImageUrlForKakaoLink : IntroState()


}