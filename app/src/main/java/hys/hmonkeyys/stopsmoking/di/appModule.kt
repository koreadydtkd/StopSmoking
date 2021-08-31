package hys.hmonkeyys.stopsmoking.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.ktx.Firebase
import com.google.firebase.ktx.initialize
import com.kakao.sdk.common.KakaoSdk
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.activity.intro.IntroViewModel
import hys.hmonkeyys.stopsmoking.activity.registration.RegistrationViewModel
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.APP_DEFAULT_KEY
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val appModule = module {
    single { Dispatchers.Main }
    single { Dispatchers.IO }

    // Firebase 초기화
    single { Firebase.initialize(androidApplication()) }

    // Kakao SDK 초기화
    single { KakaoSdk.init(androidApplication(), androidContext().getString(R.string.kakao_native_key)) }

    // SharedPreFences 초기화
    single { sharedPreferences(androidApplication()) }
}

internal val viewModelModule = module {
    viewModel { IntroViewModel(get()) }
    viewModel { RegistrationViewModel(get()) }
}

internal fun sharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(APP_DEFAULT_KEY, Context.MODE_PRIVATE)
}