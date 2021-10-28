package hys.hmonkeyys.stopsmoking.ui.intro

import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.TextView
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.ui.BaseComponentActivity
import hys.hmonkeyys.stopsmoking.ui.main.MainActivity
import hys.hmonkeyys.stopsmoking.ui.registration.RegistrationActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class IntroActivity : BaseComponentActivity<IntroViewModel>() {
    override val viewModel: IntroViewModel by viewModel()

    private val currentNetwork by lazy { getSystemService(ConnectivityManager::class.java).activeNetwork }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_intro)

        showDelayTextView()
    }

    override fun observeData() {
        viewModel.introLiveData.observe(this) {
            when (it) {
                is IntroState.Initialize -> {
                    if(currentNetwork == null) goNext() else viewModel.getKakaoLinkImageUrl()
                }
                is IntroState.GetImageUrlForKakaoLink -> {
                    goNext()
                }
            }
        }
    }

    /** 잠시 기다려달라는 텍스트뷰 보여주기 */
    private fun showDelayTextView() {
        try {
            Handler(mainLooper).postDelayed({
                findViewById<TextView>(R.id.delayTextView).visibility = View.VISIBLE
            }, 500)
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
            goNextActivity(this, MainActivity::class.java, 400, true)
        } else {
            goNextActivity(this, RegistrationActivity::class.java, 400, true)
        }
    }
}