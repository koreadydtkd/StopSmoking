package hys.hmonkeyys.stopsmoking

import android.app.Application
import hys.hmonkeyys.stopsmoking.di.appModule
import hys.hmonkeyys.stopsmoking.di.viewModelModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

class StopSmokingApplication: Application() {
    override fun onCreate() {
        super.onCreate()

        startKoin {
            androidLogger()
            androidContext(this@StopSmokingApplication)
            modules(appModule + viewModelModule)
        }

    }
}