package hys.hmonkeyys.stopsmoking.activity.intro

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.activity.BaseActivity
import hys.hmonkeyys.stopsmoking.activity.main.MainActivity
import hys.hmonkeyys.stopsmoking.activity.registration.RegistrationActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class IntroActivity : BaseActivity<IntroViewModel>() {
    override val viewModel: IntroViewModel by viewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)
    }

    override fun observeData() {
        viewModel.introLiveData.observe(this) {
            when (it) {
                is IntroState.GetImageUrlForKakaoLink -> {
                    if (viewModel.isFirstTime()) {
                        goNextActivity(MainActivity::class.java)
                    } else {
                        goNextActivity(RegistrationActivity::class.java)
                    }
                }

            }
        }
    }

    private fun <T> goNextActivity(clazz: Class<T>) {
        Handler(mainLooper).postDelayed({
            startActivity(Intent(this, clazz))
            finish()
        }, 1200)
    }

}