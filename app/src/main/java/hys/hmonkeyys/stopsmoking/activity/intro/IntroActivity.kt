package hys.hmonkeyys.stopsmoking.activity.intro

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.TextView
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

        showDelayTextView()
    }

    override fun observeData() {
        viewModel.introLiveData.observe(this) {
            when (it) {
                is IntroState.GetImageUrlForKakaoLink -> {
                    goNext()
                }
            }
        }
    }

    /** 1초 후 안넘어가면 텍스트뷰 보여주기 */
    private fun showDelayTextView() {
        try {
            Handler(mainLooper).postDelayed({
                findViewById<TextView>(R.id.delayTextView).visibility = View.VISIBLE
            }, 1000)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 처음이면 등록화면
     * 아니면 메인화면
     * */
    private fun goNext() {
        if (viewModel.isFirstTime()) {
            goNextActivity(this, MainActivity::class.java, 700, true)
        } else {
            goNextActivity(this, RegistrationActivity::class.java, 700, true)
        }
    }
}