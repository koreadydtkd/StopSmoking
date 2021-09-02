package hys.hmonkeyys.stopsmoking.activity.registration

import android.os.Bundle
import androidx.core.content.edit
import androidx.core.view.isVisible
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.activity.BaseActivity
import hys.hmonkeyys.stopsmoking.activity.main.MainActivity
import hys.hmonkeyys.stopsmoking.databinding.ActivityRegistrationBinding
import hys.hmonkeyys.stopsmoking.utils.AppShareKey
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.EDIT
import hys.hmonkeyys.stopsmoking.utils.Utility.getDatePicker
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.showSnackBar
import hys.hmonkeyys.stopsmoking.utils.setOnDuplicatePreventionClickListener
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
                // 초기화
                is RegistrationState.Initialized -> {
                    cameInFromMain()
                    initViews()
                }

                // 수정하기 위해 들어온 경우 기본값 셋팅
                is RegistrationState.StoredValue -> {
                    setDefaultData(it.dateArray, it.nickName, it.myResolution, it.amountOfSmoking, it.tobaccoPrice)
                }

                // 처음 금연 정보 등록할 때 닉네임 체크 후 기본 정보 저장
                is RegistrationState.CheckNickName -> {
                    nickNameCheckResult(it.hasNickName)
                }

                // 수정 완료에 대한 처리
                is RegistrationState.EditInformation -> {
                    onBackPressed()
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

    /** 각 뷰 초기화 */
    private fun initViews() {
        // 최대 오늘날짜 까지 선택 가능
        binding.datePicker.maxDate = System.currentTimeMillis()

        // 수정으로 들어온 경우 취소 버튼
        binding.cancelView.setOnDuplicatePreventionClickListener {
            onBackPressed()
        }

        // 기본 예외 처리 후 등록
        binding.stopSmokingButton.setOnDuplicatePreventionClickListener {
            if (binding.nickNameEditText.text.toString().isBlank()) {
                showSnackBar(binding.root, getString(R.string.message_nick_name))
                return@setOnDuplicatePreventionClickListener
            }

            if (binding.amountOfSmokingEditText.text.isNullOrEmpty()) {
                showSnackBar(binding.root, getString(R.string.message_amount_of_smoking))
                return@setOnDuplicatePreventionClickListener
            }

            if (binding.tobaccoPriceEditText.text.isNullOrEmpty()) {
                showSnackBar(binding.root, getString(R.string.message_tobacco_price))
                return@setOnDuplicatePreventionClickListener
            }

            if (binding.myResolutionEditText.text.toString().isBlank()) {
                showSnackBar(binding.root, getString(R.string.message_my_resolution))
                return@setOnDuplicatePreventionClickListener
            }

            // 금연 정보 등록
            noSmokingRegistration()
        }
    }

    /** 기본 셋팅 */
    private fun setDefaultData(dateArray: List<String>, nickName: String, myResolution: String, amountOfSmoking: Int, tobaccoPrice: Int) {
        binding.datePicker.updateDate(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt())
        binding.nickNameEditText.setText(nickName)
        binding.nickNameEditText.isEnabled = false
        binding.nickNameTextField.hint = getString(R.string.edit_nick_name)
        binding.myResolutionEditText.setText(myResolution)

        if (amountOfSmoking > 0 && tobaccoPrice > 0) {
            binding.amountOfSmokingEditText.setText("$amountOfSmoking")
            binding.tobaccoPriceEditText.setText("$tobaccoPrice")
        }

        binding.stopSmokingButton.text = getString(R.string.edit)
    }

    /** 닉네임 중복확인 결과에 따른 분기처리 */
    private fun nickNameCheckResult(hasNickName: Boolean?) {
        when(hasNickName) {
            null -> {
                showSnackBar(binding.root, getString(R.string.error))
            }
            true -> {
                goNextActivity(this, MainActivity::class.java, 0, true)
            }
            false -> {
                showSnackBar(binding.root, getString(R.string.message_reduplication))
            }
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
        } else if (nickName.length < 2) {
            showSnackBar(binding.root, getString(R.string.message_length))
            return
        }

        // 기본 값 0 입력 체크
        if (amountOfSmoking < 1 || tobaccoPrice < 1) {
            showSnackBar(binding.root, getString(R.string.message_zero_input_exception))
            return
        }

        // 수정인 경우 금연정도만 수정.
        // 등록인 경우 닉네임 체크 후 저장
        if (isEdit) {
            viewModel.saveInfo(
                getDatePicker(binding.datePicker.year, binding.datePicker.month, binding.datePicker.dayOfMonth),
                amountOfSmoking,
                tobaccoPrice,
                myResolution
            )
        } else {
            viewModel.checkForDuplicateNicknamesAndSaveInfo(
                true,
                getDatePicker(binding.datePicker.year, binding.datePicker.month, binding.datePicker.dayOfMonth),
                nickName,
                amountOfSmoking,
                tobaccoPrice,
                myResolution
            )
        }

    }

    override fun onBackPressed() {
        if (isEdit) {
            goNextActivity(this, MainActivity::class.java, 0, true)
        } else {
            super.onBackPressed()
        }
    }

}