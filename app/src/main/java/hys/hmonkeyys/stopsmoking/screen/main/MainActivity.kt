package hys.hmonkeyys.stopsmoking.screen.main

import android.content.Intent
import android.view.animation.AlphaAnimation
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kakao.sdk.link.LinkClient
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.databinding.ActivityMainBinding
import hys.hmonkeyys.stopsmoking.extension.setOnDuplicatePreventionClickListener
import hys.hmonkeyys.stopsmoking.screen.BaseActivity
import hys.hmonkeyys.stopsmoking.screen.dialog.BodyChangeDialog
import hys.hmonkeyys.stopsmoking.screen.main.community.CommunityActivity
import hys.hmonkeyys.stopsmoking.screen.registration.RegistrationActivity
import hys.hmonkeyys.stopsmoking.utils.Constant.EDIT
import hys.hmonkeyys.stopsmoking.utils.Utility
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.snackBar
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class MainActivity : BaseActivity<MainViewModel, ActivityMainBinding>() {

    override val viewModel: MainViewModel by viewModel()
    override fun getViewBinding(): ActivityMainBinding = ActivityMainBinding.inflate(layoutInflater)

    private val nickName: String by lazy { viewModel.getNickName() }

    // 투명도 효과
    private val anim: AlphaAnimation by lazy {
        AlphaAnimation(0F, 1F).apply {
            duration = 500
            repeatCount = 2
        }
    }

    private var backPressTime = 0L

    /** 각 뷰 초기화 */
    override fun initViews() = with(binding) {
        // 수정 버튼
        imageViewEdit.setOnDuplicatePreventionClickListener {
            goEditActivity()
        }

        // 카카오 링크 공유 버튼
        imageViewShare.setOnDuplicatePreventionClickListener {
            shareKakaoLink()
        }

        // 금연 후 신체변화 다이얼로그 띄우 버튼
        layoutBodyChanges.setOnDuplicatePreventionClickListener {
            BodyChangeDialog().show(supportFragmentManager, "")
        }

        // Uid 확인 후 있는 경우에만 이동
        layoutCommunity.setOnDuplicatePreventionClickListener {
            progressBar.isVisible = true
            viewModel.getCurrentUser()
        }
    }

    /** 수정(등록) 화면으로 이동 - 'RegistrationActivity' 재 활용 */
    private fun goEditActivity() {
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.putExtra(EDIT, true)
        startActivity(intent)
        finish()
    }

    /** 카카오 링크 공유 */
    private fun shareKakaoLink() {
        binding.progressBar.isVisible = true

        try {
//            val keyHash = com.kakao.sdk.common.util.Utility.getKeyHash(this)
//            Log.e(TAG, "Hash Key: $keyHash")

            // 카카오톡 설치여부 확인
            if (LinkClient.instance.isKakaoLinkAvailable(this)) {

                // 카카오톡으로 카카오링크 공유 가능
                LinkClient.instance.defaultTemplate(this, viewModel.getDefaultFeed()) { linkResult, error ->
                    if (error != null) {
                        snackBar(binding.root, getString(R.string.message_kakao_link_send_fail))
                    } else if (linkResult != null) {
                        startActivity(linkResult.intent)

                        // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
//                        Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
//                        Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                    }
                }
            } else {
                snackBar(binding.root, getString(R.string.message_kakao_not_install))
            }
        } catch (e: Exception) {
            snackBar(binding.root, getString(R.string.message_kakao_error))
            FirebaseCrashlytics.getInstance().recordException(e)
            e.printStackTrace()
        } finally {
            binding.progressBar.isGone = true
        }
    }

    override fun observeData() {
        viewModel.mainLiveData.observe(this) {
            when (it) {
                is MainState.Initialize -> {
                    initSmokingCessationInformation()
                    initAdmob()
                }

                is MainState.HaveUid -> existUid()

                is MainState.NoHaveUid -> noneUid()
            }
        }
    }

    /** 금연 정보 초기셋팅 */
    private fun initSmokingCessationInformation() = with(binding) {
        // 금연 시작 date
        startDayTextView.text = viewModel.getStartDay() ?: getString(R.string.failed_get_data)

        // 상단 d-day
        textViewDDay.text = getString(R.string.stop_smoking_d_day, viewModel.getDDay())

        // 안피운 담배
        textViewCigarettesSaved.text = getString(R.string.main_save_tobacco, viewModel.getCigaretteCount())

        // 늘어난 수명
        textViewIncreasedLifespan.text = viewModel.getIncreasedLifespan()

        // 절약한 금액
        textViewSaveMoney.text = viewModel.getTobaccoPrice()
    }

    /** 하단 배너광고 초기화 */
    private fun initAdmob() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()
        binding.adView.loadAd(adRequest)
    }

    /** Uid 있음, 글 쓰기 및 읽기 가능 */
    private fun existUid() {
        binding.progressBar.isGone = true
        goNextActivity(this, CommunityActivity::class.java, 0)
    }

    /** Uid 없음, 글 쓰기 및 읽기 불가 */
    private fun noneUid() {
        binding.progressBar.isGone = true
        snackBar(binding.root, getString(R.string.error))
    }

    override fun onResume() {
        super.onResume()

        // 이름 부분 색상 변경 및 금연에 대한 응원 메세지 랜덤
        binding.textViewUserName.text = Utility.changePartialTextColor(
            getRandomText(nickName),
            getColor(R.color.black),
            0,
            nickName.length
        )

        // 깜빡이는 효과 start
        startAnimation()
    }

    /** 랜덤 텍스트 가져오기 */
    private fun getRandomText(nickName: String): String {
        return when ((1..5).random()) {
            1 -> getString(R.string.main_title1, nickName)
            2 -> getString(R.string.main_title2, nickName)
            3 -> getString(R.string.main_title3, nickName)
            4 -> getString(R.string.main_title4, nickName)
            else -> getString(R.string.main_title5, nickName)
        }
    }

    /** 공유하기 버튼 깜빡이는 효과 */
    private fun startAnimation() {
        binding.labelShare.startAnimation(anim)
        binding.imageViewShare.startAnimation(anim)
    }

    override fun onPause() {
        super.onPause()

        // 애니메이션 제거
        binding.labelShare.clearAnimation()
        binding.imageViewShare.clearAnimation()
    }

    override fun onBackPressed() {
        val time = System.currentTimeMillis()
        if (time - backPressTime > ONE_POINT_FIVE_SECOND) {
            snackBar(binding.root, getString(R.string.message_backward_finish))
            backPressTime = time
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        //        private const val TAG = "SS_MainActivity"
        private const val ONE_POINT_FIVE_SECOND = 1500L
    }
}