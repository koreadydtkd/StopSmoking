package hys.hmonkeyys.stopsmoking.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import hys.hmonkeyys.stopsmoking.data.api.*
import hys.hmonkeyys.stopsmoking.screen.main.community.CommunityViewModel
import hys.hmonkeyys.stopsmoking.screen.main.community.detail.CommunityDetailViewModel
import hys.hmonkeyys.stopsmoking.screen.intro.IntroViewModel
import hys.hmonkeyys.stopsmoking.screen.main.MainViewModel
import hys.hmonkeyys.stopsmoking.screen.registration.RegistrationViewModel
import hys.hmonkeyys.stopsmoking.screen.main.community.write.WriteViewModel
import hys.hmonkeyys.stopsmoking.data.preference.AppPreferenceManager
import hys.hmonkeyys.stopsmoking.data.repository.community.CommunityRepository
import hys.hmonkeyys.stopsmoking.data.repository.community.DefaultCommunityRepository
import hys.hmonkeyys.stopsmoking.data.repository.linkimage.DefaultLinkImageRepository
import hys.hmonkeyys.stopsmoking.data.repository.linkimage.LinkImageRepository
import hys.hmonkeyys.stopsmoking.data.repository.nickname.DefaultNickNameRepository
import hys.hmonkeyys.stopsmoking.data.repository.nickname.NickNameRepository
import hys.hmonkeyys.stopsmoking.data.repository.readpost.DefaultReadPostRepository
import hys.hmonkeyys.stopsmoking.data.repository.readpost.ReadPostRepository
import hys.hmonkeyys.stopsmoking.data.repository.user.DefaultUserRepository
import hys.hmonkeyys.stopsmoking.data.repository.user.UserRepository
import kotlinx.coroutines.Dispatchers
import org.koin.android.ext.koin.androidApplication
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

internal val appModule = module {

    single { Dispatchers.Main }
    single { Dispatchers.IO }

    single { FirebaseAuth.getInstance() }
    single { Firebase.storage }
    single { Firebase.database }
    single { Firebase.firestore }

    // SharedPreFences
    single { AppPreferenceManager(androidApplication()) }

    // Room DB 'ReadPost' DB ??????
    single { createReadPostDB(androidApplication()) }
    single { readPostDao(get()) }

    // Api
    single<CheckUserApi> { CheckUserFireAuthApi(get()) }
    single<LinkImageApi> { LinkImageFirestoreApi(get()) }
    single<NickNameApi> { NickNameFirebaseApi(get()) }
    single<CommunityApi> { CommunityFireStoreApi(get()) }

    // Repository(network)
    single<UserRepository> { DefaultUserRepository(get(), get()) }
    single<LinkImageRepository> { DefaultLinkImageRepository(get(), get()) }
    single<NickNameRepository> { DefaultNickNameRepository(get(), get()) }
    single<CommunityRepository> { DefaultCommunityRepository(get(), get()) }

    // Repository(room)
    single<ReadPostRepository> { DefaultReadPostRepository(get(), get()) }
}

internal val viewModelModule = module {

    viewModel { IntroViewModel(get(), get(), get()) }

    viewModel { RegistrationViewModel(get(), get()) }

    viewModel { MainViewModel(get(), get()) }

    viewModel { CommunityViewModel(get(), get()) }

    viewModel { WriteViewModel(get(), get()) }

    viewModel { CommunityDetailViewModel(get(), get(), get()) }
}



/**
 * module { ... } : ???????????? ??????????????? ?????? ????????? ??????
 * single { ... } : ?????? ???????????? ?????? ?????? ???????????? ????????? ????????? ??????
 * factory { ... } : ????????? ??? ?????? ?????? ????????? ????????? ??????
 * get() : ???????????? ????????? ????????? ???????????? ??????
 */