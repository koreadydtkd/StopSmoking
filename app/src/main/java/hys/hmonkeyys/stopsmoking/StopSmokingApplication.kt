package hys.hmonkeyys.stopsmoking

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.kakao.sdk.common.KakaoSdk

class StopSmokingApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        // Firebase 초기화
        Firebase.initialize(this)

        // Kakao SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_native_key))
    }
}