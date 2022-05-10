package hys.hmonkeyys.stopsmoking

import android.app.Application
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.kakao.sdk.common.KakaoSdk
import hys.hmonkeyys.stopsmoking.di.appModule
import hys.hmonkeyys.stopsmoking.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin
import org.koin.core.logger.Level

class StopSmokingApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        // 초기화
        initSDK()

        startKoin {
            androidLogger(
                if (BuildConfig.DEBUG) Level.ERROR else Level.NONE
            )
            androidContext(this@StopSmokingApplication)
            modules(appModule + viewModelModule)
        }

    }

    private fun initSDK() {
        // Firebase 초기화
        Firebase.initialize(this)

        // Kakao SDK 초기화
        KakaoSdk.init(this, getString(R.string.kakao_native_key))
    }
}