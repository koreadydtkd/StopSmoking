package hys.hmonkeyys.stopsmoking.screen.intro

import android.os.Handler
import android.util.Log
import androidx.core.view.isVisible
import hys.hmonkeyys.stopsmoking.databinding.ActivityIntroBinding
import hys.hmonkeyys.stopsmoking.screen.BaseActivity
import hys.hmonkeyys.stopsmoking.screen.main.MainActivity
import hys.hmonkeyys.stopsmoking.screen.registration.RegistrationActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.isNetworkConnecting
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class IntroActivity : BaseActivity<IntroViewModel, ActivityIntroBinding>() {

    override val viewModel: IntroViewModel by viewModel()
    override fun getViewBinding(): ActivityIntroBinding = ActivityIntroBinding.inflate(layoutInflater)

    override fun observeData() {
        viewModel.introLiveData.observe(this) {
            when (it) {
                is IntroState.Initialize -> checkCurrentNetwork()

                is IntroState.GetImageUrlForKakaoLink -> goNext()
            }
        }
    }

    /** 인터넷 연결 여부 확인 */
    private fun checkCurrentNetwork() {
        isNetworkConnecting(this)?.let {
            showDelayTextView()

            // 익명 가입했는지 확인
            viewModel.checkCurrentUser()
        } ?: kotlin.run {
            goNext()
        }
    }

    /** 잠시 기다려달라는 텍스트뷰 보여주기 */
    private fun showDelayTextView() {
        try {
            Handler(mainLooper).postDelayed({
                binding.delayTextView.isVisible = true
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
        val className = if (viewModel.isFirstTime()) {
            RegistrationActivity::class.java
        } else {
            MainActivity::class.java
        }
        goNextActivity(this, className, DELAY_MILLIS, true)
    }

    companion object {
        private const val DELAY_MILLIS = 400L
    }
}