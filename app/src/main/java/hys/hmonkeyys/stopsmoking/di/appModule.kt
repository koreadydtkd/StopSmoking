package hys.hmonkeyys.stopsmoking.di

import android.content.Context
import android.content.SharedPreferences
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hys.hmonkeyys.stopsmoking.activity.community.CommunityViewModel
import hys.hmonkeyys.stopsmoking.activity.communitydetail.CommunityDetailViewModel
import hys.hmonkeyys.stopsmoking.activity.intro.IntroViewModel
import hys.hmonkeyys.stopsmoking.activity.main.MainViewModel
import hys.hmonkeyys.stopsmoking.activity.registration.RegistrationViewModel
import hys.hmonkeyys.stopsmoking.activity.write.WriteViewModel
import hys.hmonkeyys.stopsmoking.utils.AppShareKey
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.APP_DEFAULT_KEY
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.DB_Community
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.DB_NICKNAME
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val appModule = module {

    single { Dispatchers.Main }
    single { Dispatchers.IO }

    // SharedPreFences 초기화
    single { initSharedPreferences(androidApplication()) }

    // Firebase NickName DB 초기화
    single { initNickNameDB() }

    single { initCommunityDB() }
}

internal val viewModelModule = module {
    viewModel { IntroViewModel(get()) }
    viewModel { RegistrationViewModel(get(), get()) }
    viewModel { MainViewModel(get()) }
    viewModel { CommunityViewModel(get()) }
    viewModel { WriteViewModel(get(), get()) }
    viewModel { CommunityDetailViewModel(get()) }
}

internal fun initNickNameDB(): DatabaseReference {
    return Firebase.database.reference.child(DB_NICKNAME)
}

internal fun initCommunityDB(): CollectionReference {
    return Firebase.firestore.collection(DB_Community)
}

internal fun initSharedPreferences(context: Context): SharedPreferences {
    return context.getSharedPreferences(APP_DEFAULT_KEY, Context.MODE_PRIVATE)
}