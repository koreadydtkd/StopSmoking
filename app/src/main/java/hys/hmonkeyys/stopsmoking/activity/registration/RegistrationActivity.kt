package hys.hmonkeyys.stopsmoking.activity.registration

import android.os.Bundle
import androidx.core.view.isVisible
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.activity.BaseActivity
import hys.hmonkeyys.stopsmoking.activity.main.MainActivity
import hys.hmonkeyys.stopsmoking.databinding.ActivityRegistrationBinding
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.EDIT
import hys.hmonkeyys.stopsmoking.utils.Utility.getDatePicker
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.showSnackBar
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class RegistrationActivity : BaseActivity<RegistrationViewModel>() {
    private val binding: ActivityRegistrationBinding by lazy { ActivityRegistrationBinding.inflate(layoutInflater) }
    override val viewModel: RegistrationViewModel by viewModel()

    private var isEdit = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun observeData() {
        viewModel.registrationLiveData.observe(this) {
            when (it) {
                is RegistrationState.Initialized -> {
                    cameInFromMain()
                    initViews()
                }
                is RegistrationState.StoredValue -> {
                    setDefaultData(it.dateArray, it.nickName, it.myResolution, it.amountOfSmoking, it.tobaccoPrice)
                }
            }
        }
    }

    /** 메인에서 수정하기 위해 들어왔는지 확인 */
    private fun cameInFromMain() {
        isEdit = intent.getBooleanExtra(EDIT, false)
        if (isEdit) {
            viewModel.getDefaultData()
        }
        binding.cancelView.isVisible = isEdit
    }

    /** 기본 셋팅 */
    private fun setDefaultData(dateArray: List<String>, nickName: String, myResolution: String, amountOfSmoking: Int, tobaccoPrice: Int) {
        binding.datePicker.updateDate(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt())
        binding.nickNameEditText.setText(nickName)
        binding.myResolutionEditText.setText(myResolution)

        if (amountOfSmoking > 0 && tobaccoPrice > 0) {
            binding.amountOfSmokingEditText.setText("$amountOfSmoking")
            binding.tobaccoPriceEditText.setText("$tobaccoPrice")
        }

        binding.stopSmokingButton.text = getString(R.string.edit)
    }

    /** 각 뷰 초기화 */
    private fun initViews() {
        // 최대 오늘날짜 까지 선택 가능
        binding.datePicker.maxDate = System.currentTimeMillis()

        // 수정으로 들어온 경우 취소 버튼
        binding.cancelView.setOnClickListener {
            onBackPressed()
        }

        // 기본 예외 처리 후 등록
        binding.stopSmokingButton.setOnClickListener {
            if (binding.nickNameEditText.text.toString().isBlank()) {
                showSnackBar(binding.root, getString(R.string.message_nick_name))
                return@setOnClickListener
            }

            if (binding.amountOfSmokingEditText.text.isNullOrEmpty()) {
                showSnackBar(binding.root, getString(R.string.message_amount_of_smoking))
                return@setOnClickListener
            }

            if (binding.tobaccoPriceEditText.text.isNullOrEmpty()) {
                showSnackBar(binding.root, getString(R.string.message_tobacco_price))
                return@setOnClickListener
            }

            if (binding.myResolutionEditText.text.toString().isBlank()) {
                showSnackBar(binding.root, getString(R.string.message_my_resolution))
                return@setOnClickListener
            }

            // 금연 정보 등록
            noSmokingRegistration()
        }
    }

    /** 금연 정보 등록 */
    private fun noSmokingRegistration() {
        val nickName = binding.nickNameEditText.text.toString()
        val amountOfSmoking = binding.amountOfSmokingEditText.text.toString().toInt()
        val tobaccoPrice = binding.tobaccoPriceEditText.text.toString().toInt()
        val myResolution = binding.myResolutionEditText.text.toString()

        // 띄어쓰기 입력 체크
        if (nickName.contains(" ")) {
            showSnackBar(binding.root, getString(R.string.message_input_space))
            return
        }

        // 기본 값 0 입력 체크
        if (amountOfSmoking < 1 || tobaccoPrice < 1) {
            showSnackBar(binding.root, getString(R.string.message_zero_input_exception))
            return
        }

        // todo 정보 저장하기 전 닉네임 중복확인 후 다음 프로세스

        viewModel.saveNoSmokingInformation(
            true,
            getDatePicker(binding.datePicker.year, binding.datePicker.month, binding.datePicker.dayOfMonth),
            nickName,
            amountOfSmoking,
            tobaccoPrice,
            myResolution
        )

        goNextActivity(this, MainActivity::class.java, 0, true)
    }

    override fun onBackPressed() {
        if (isEdit) {
            goNextActivity(this, MainActivity::class.java, 0, true)
        } else {
            super.onBackPressed()
        }

    }
}