package hys.hmonkeyys.stopsmoking.ui.main

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.util.Log
import android.view.View
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.kakao.sdk.link.LinkClient
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.ui.BaseActivity
import hys.hmonkeyys.stopsmoking.ui.community.CommunityActivity
import hys.hmonkeyys.stopsmoking.ui.registration.RegistrationActivity
import hys.hmonkeyys.stopsmoking.databinding.ActivityMainBinding
import hys.hmonkeyys.stopsmoking.extension.setOnDuplicatePreventionClickListener
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.EDIT
import hys.hmonkeyys.stopsmoking.utils.Utility
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.showSnackBar
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
                    initSmokingCessationInformation()
                    initViews()
                    initAdmob()
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        // 이름 부분 색상 변경 및 금연에 대한 응원 메세지 랜덤
        val nickName = viewModel.getNickName()
        binding.titleTextView.text = Utility.changePartialTextColor(
            getRandomText(nickName),
            getColor(R.color.black),
            0,
            nickName.length
        )
    }

    /** 금연 정보 초기화(TextView) */
    private fun initSmokingCessationInformation() = with(binding) {
        startDayTextView.text = viewModel.getStartDay() ?: "데이터를 가져오지 못했습니다."

        // 상단 d-day
        dDayTextView.text = getString(R.string.stop_smoking_d_day, viewModel.getDDay())

        // 각오
        myResolutionTextView.text = viewModel.getMyResolution()

        // 안피운 담배
        cigarettesSavedTextView.text = getString(R.string.main_save_tobacco, viewModel.getCigaretteCount())

        // 늘어난 수명
        increasedLifespanTextView.text = viewModel.getIncreasedLifespan()

        // 절약한 금액
        saveMoneyTextView.text = viewModel.getTobaccoPrice()
    }

    /** 각 뷰 초기화 */
    private fun initViews() {
        // 수정 버튼
        binding.editCardView.setOnDuplicatePreventionClickListener {
            goEditActivity()
        }

        // 카카오 링크 공유 버튼
        binding.shareButton.setOnDuplicatePreventionClickListener {
            shareKakaoLink()
        }

        // 금연 후 신체변화 다이얼로그 띄우 버튼
        binding.bodyChangesButton.setOnDuplicatePreventionClickListener {
            showBodyChangesDialog()
        }

        // 담소 버튼
        binding.communityLayout.setOnDuplicatePreventionClickListener {
            goNextActivity(this, CommunityActivity::class.java, 0)
        }
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

    /** 수정(등록) 화면으로 이동 - 'RegistrationActivity' 재 활용 */
    private fun goEditActivity() {
        val intent = Intent(this, RegistrationActivity::class.java)
        intent.putExtra(EDIT, true)
        startActivity(intent)
        finish()
    }

    /** 금연 후 신체변화 dialog 띄우기 */
    private fun showBodyChangesDialog() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.dialog_body_changes)
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.show()

        dialog.findViewById<View>(R.id.cancelView).setOnClickListener {
            dialog.dismiss()
        }
        dialog.findViewById<View>(R.id.checkButton).setOnClickListener {
            dialog.dismiss()
        }
    }

    /** 카카오 링크 공유 */
    private fun shareKakaoLink() {
        binding.progressBar.visibility = View.VISIBLE

        try {
//            val keyHash = com.kakao.sdk.common.util.Utility.getKeyHash(this)
//            Log.e(TAG, "Hash Key: $keyHash")

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
                    binding.progressBar.visibility = View.GONE
                }

            } else {
                showSnackBar(binding.root, getString(R.string.message_kakao_not_install))
                binding.progressBar.visibility = View.GONE
            }
        } catch (e: Exception) {
            FirebaseCrashlytics.getInstance().recordException(e)
            showSnackBar(binding.root ,getString(R.string.message_kakao_error))
            binding.progressBar.visibility = View.GONE
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