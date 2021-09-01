package hys.hmonkeyys.stopsmoking.activity.intro

import android.os.Bundle
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.activity.BaseActivity
import hys.hmonkeyys.stopsmoking.activity.main.MainActivity
import hys.hmonkeyys.stopsmoking.activity.registration.RegistrationActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
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
                        goNextActivity(this, MainActivity::class.java, 1000, true)
                    } else {
                        goNextActivity(this, RegistrationActivity::class.java, 1000, true)
                    }
                }

            }
        }
    }

}