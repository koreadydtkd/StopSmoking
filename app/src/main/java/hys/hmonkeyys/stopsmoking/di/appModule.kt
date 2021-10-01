package hys.hmonkeyys.stopsmoking.di

import android.app.Activity
import android.content.Context
import androidx.room.Room
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import hys.hmonkeyys.stopsmoking.activitys.community.CommunityViewModel
import hys.hmonkeyys.stopsmoking.activitys.communitydetail.CommunityDetailViewModel
import hys.hmonkeyys.stopsmoking.activitys.intro.IntroViewModel
import hys.hmonkeyys.stopsmoking.activitys.main.MainViewModel
import hys.hmonkeyys.stopsmoking.activitys.registration.RegistrationViewModel
import hys.hmonkeyys.stopsmoking.activitys.write.WriteViewModel
import hys.hmonkeyys.stopsmoking.data.db.ReadPostDatabase
import hys.hmonkeyys.stopsmoking.data.db.dao.ReadPostDao
import hys.hmonkeyys.stopsmoking.data.preference.PreferenceManager
import hys.hmonkeyys.stopsmoking.data.preference.SharedPreferenceManager
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.APP_DEFAULT_KEY
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.DB_Community
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.DB_NICKNAME
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val appModule = module {

    single { Dispatchers.Main }
    single { Dispatchers.IO }

    // SharedPreFences
    single { androidContext().getSharedPreferences(APP_DEFAULT_KEY, Activity.MODE_PRIVATE) }
    single<PreferenceManager> { SharedPreferenceManager(get()) }

    // Firebase NickName DB(실시간) 생성
    single { createNickNameDB() }

    // Firebase NickName DB(Cloud) 생성
    single { createCommunityDB() }

    // Room DB 'ReadPost' DB 생성
    single { createReadPostDB(androidApplication()) }
    single { readPostDao(get()) }
}

internal val viewModelModule = module {

    viewModel { IntroViewModel(get()) }

    viewModel { RegistrationViewModel(get(), get()) }

    viewModel { MainViewModel(get()) }

    viewModel { CommunityViewModel(get(), get()) }

    viewModel { WriteViewModel(get(), get()) }

    viewModel { CommunityDetailViewModel(get(), get(), get()) }
}

internal fun createNickNameDB(): DatabaseReference {
    return Firebase.database.reference.child(DB_NICKNAME)
}

internal fun createCommunityDB(): CollectionReference {
    return Firebase.firestore.collection(DB_Community)
}

internal fun createReadPostDB(context: Context): ReadPostDatabase {
    return Room.databaseBuilder(context, ReadPostDatabase::class.java, ReadPostDatabase.DB_NAME).build()
}
internal fun readPostDao(database: ReadPostDatabase): ReadPostDao {
    return database.readPostDao()
}

/**
 * module { ... } : 키워드로 주입받고자 하는 객체의 집합
 * single { ... } : 앱이 실행되는 동안 계속 유지되는 싱글톤 객체를 생성
 * factory { ... } : 요청할 때 마다 매번 새로운 객체를 생성
 * get() : 컴포넌트 내에서 알맞은 의존성을 주입
 */