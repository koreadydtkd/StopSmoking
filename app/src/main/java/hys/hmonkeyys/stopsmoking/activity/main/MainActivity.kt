package hys.hmonkeyys.stopsmoking.activity.main

import android.content.Intent
import android.os.Bundle
import android.util.Log
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kakao.sdk.link.LinkClient
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.activity.BaseActivity
import hys.hmonkeyys.stopsmoking.activity.bodychanges.BodyChangesActivity
import hys.hmonkeyys.stopsmoking.activity.registration.RegistrationActivity
import hys.hmonkeyys.stopsmoking.databinding.ActivityMainBinding
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.EDIT
import hys.hmonkeyys.stopsmoking.utils.Utility
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.showSnackBar
import hys.hmonkeyys.stopsmoking.utils.setOnDuplicatePreventionClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class MainActivity : BaseActivity<MainViewModel>() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }
    override val viewModel: MainViewModel by viewModel()

    private var backPressTime = 0L

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun observeData() {
        viewModel.mainLiveData.observe(this) {
            when(it) {
                is MainState.Initialize -> {
                    initTextViews()
                    initViews()
                    initAdmob()
                }
            }
        }
    }

    /** 금연 일 수 초기화 */
    private fun initTextViews() {
        // 상단 d-day
        binding.dDayTextView.text = getString(R.string.stop_smoking_d_day, viewModel.getDDay())

        // 이름 부분 색상 변경
        val nickName = viewModel.getNickName()
        binding.titleTextView.text = Utility.changePartialTextColor(
            getString(R.string.main_title, nickName),
            getColor(R.color.black),
            0,
            nickName.length
        )

        // 각오
        binding.myResolutionTextView.text = viewModel.getMyResolution()

        // 안피운 담배
        binding.cigarettesSavedTextView.text = getString(R.string.main_save_tobacco, viewModel.getCigaretteCount())

        // 늘어난 수명
        binding.increasedLifespanTextView.text = viewModel.getIncreasedLifespan()

        // 절약한 금액
        binding.saveMoneyTextView.text = viewModel.getTobaccoPrice()
    }

    /** 각 뷰 초기화 */
    private fun initViews() {
        binding.editCardView.setOnDuplicatePreventionClickListener {
            goEditActivity()
        }

        binding.shareButton.setOnDuplicatePreventionClickListener {
            shareKakaoLink()
        }

        binding.bodyChangesLayout.setOnDuplicatePreventionClickListener {
            goNextActivity(this, BodyChangesActivity::class.java, 0)
        }

        binding.communityLayout.setOnDuplicatePreventionClickListener {
            showSnackBar(binding.root, "현재 준비중입니다. 조금만 기다려주세요.")
        }
    }

    /** 수정(등록) 화면으로 이동 - 등록화면 재 활용 */
    private fun goEditActivity() {
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.putExtra(EDIT, true)
        startActivity(intent)
        finish()
    }

    /** 카카오 링크 공유 */
    private fun shareKakaoLink() {
        val keyHash = com.kakao.sdk.common.util.Utility.getKeyHash(this)
        Log.e(TAG, "Hash Key: $keyHash")

        try {
            // 카카오톡 설치여부 확인
            if (LinkClient.instance.isKakaoLinkAvailable(this)) {
                // 카카오톡으로 카카오링크 공유 가능
                LinkClient.instance.defaultTemplate(this, viewModel.getDefaultFeed()) { linkResult, error ->
                    if (error != null) {
                        showSnackBar(binding.root, getString(R.string.message_kakao_link_send_fail))
                    } else if (linkResult != null) {
                        startActivity(linkResult.intent)

                        // 카카오링크 보내기에 성공했지만 아래 경고 메시지가 존재할 경우 일부 컨텐츠가 정상 동작하지 않을 수 있습니다.
                        Log.w(TAG, "Warning Msg: ${linkResult.warningMsg}")
                        Log.w(TAG, "Argument Msg: ${linkResult.argumentMsg}")
                    }
                }
            } else {
                showSnackBar(binding.root, getString(R.string.message_kakao_not_install))
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            showSnackBar(binding.root ,getString(R.string.message_kakao_error))
            e.printStackTrace()
        }

    }

    /** 하단 배너광고 초기화 */
    private fun initAdmob() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()

        binding.adView.apply {
            loadAd(adRequest)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.i(TAG, "onAdLoaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    Log.e(TAG, "광고 로드에 문제 발생 onAdFailedToLoad ${error.message}")
                }
            }
        }
    }

    override fun onBackPressed() {
        val time = System.currentTimeMillis()
        if (time - backPressTime > ONE_POINT_FIVE_SECOND) {
            showSnackBar(binding.root, getString(R.string.message_backward_finish))
            backPressTime = time
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TAG = "SS_MainActivity"
        private const val ONE_POINT_FIVE_SECOND = 1500L
    }
}