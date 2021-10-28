package hys.hmonkeyys.stopsmoking.ui.registration

import android.os.Bundle
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.ui.BaseActivity
import hys.hmonkeyys.stopsmoking.ui.main.MainActivity
import hys.hmonkeyys.stopsmoking.databinding.ActivityRegistrationBinding
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.EDIT
import hys.hmonkeyys.stopsmoking.utils.Utility.getDatePicker
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.showSnackBar
import hys.hmonkeyys.stopsmoking.widget.WidgetProvider
import org.koin.androidx.viewmodel.ext.android.viewModel
import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.content.Intent
import android.util.Log
import androidx.core.view.isVisible
import hys.hmonkeyys.stopsmoking.extension.setOnDuplicatePreventionClickListener
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.WIDGET_UPDATE


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
                    binding.progressBar.isVisible = false
                    nickNameCheckResult(it.hasNickName)
                }

                // 수정 완료에 대한 처리
                is RegistrationState.EditInformation -> {
                    binding.progressBar.isVisible = false
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

        // 등록 버튼 클릭
        binding.stopSmokingButton.setOnDuplicatePreventionClickListener {
            // 기본 예외 처리 후 등록
            inputInformationException(
                binding.nickNameEditText.text.toString().trim(),
                binding.amountOfSmokingEditText.text.toString(),
                binding.tobaccoPriceEditText.text.toString(),
                binding.myResolutionEditText.text.toString()
            )
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
        when (hasNickName) {
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

    /** 입력 정보 예외 처리 */
    private fun inputInformationException(nickName: String, amountOfSmoking: String, tobaccoPrice: String, myResolution: String) {

        when {
            // 닉네임 예외 처리
            nickName.isEmpty() -> {
                showSnackBar(binding.root, getString(R.string.message_nick_name))
                return
            }
            nickName.contains(" ") -> {
                showSnackBar(binding.root, getString(R.string.message_input_space))
                return
            }
            nickName.length < 2 || nickName.length > 10 -> {
                showSnackBar(binding.root, getString(R.string.message_nick_name_length))
                return
            }

            // 하루 흡연량 예외 처리
            amountOfSmoking.isEmpty() -> {
                showSnackBar(binding.root, getString(R.string.message_amount_of_smoking))
                return
            }
            amountOfSmoking.length > 3 -> {
                showSnackBar(binding.root, getString(R.string.message_amount_of_smoking_length))
                return
            }
            amountOfSmoking.toInt() < 1 -> {
                showSnackBar(binding.root, getString(R.string.message_zero_input_exception, getString(R.string.amount_of_smoking)))
                return
            }

            // 담배 가격 예외 처리
            tobaccoPrice.isEmpty() -> {
                showSnackBar(binding.root, getString(R.string.message_tobacco_price))
                return
            }
            tobaccoPrice.length > 6 -> {
                showSnackBar(binding.root, getString(R.string.message_tobacco_price_length))
                return
            }
            tobaccoPrice.toInt() < 1 -> {
                showSnackBar(binding.root, getString(R.string.message_zero_input_exception, getString(R.string.tobacco_price)))
                return
            }

            // 각오 예외 처리
            myResolution.isEmpty() -> {
                showSnackBar(binding.root, getString(R.string.message_my_resolution))
                return
            }
            myResolution.length > 20 -> {
                showSnackBar(binding.root, getString(R.string.message_my_resolution_length))
                return
            }
        }

        // 수정에서 들어온 경우에 따라 분기 처리
        val inputDate = getDatePicker(binding.datePicker.year, binding.datePicker.month, binding.datePicker.dayOfMonth)
        noSmokingRegistration(inputDate, nickName, amountOfSmoking.toInt(), tobaccoPrice.toInt(), myResolution)
    }

    /**
     * 수정인 경우 금연정보 수정 및 위젯 업데이트.
     * 등록인 경우 닉네임 체크 후 저장
     * */
    private fun noSmokingRegistration(inputDate: String, nickName: String, amountOfSmoking: Int, tobaccoPrice: Int, myResolution: String) {
        binding.progressBar.isVisible = true

        if (isEdit) {
            viewModel.saveInfo(inputDate, amountOfSmoking, tobaccoPrice, myResolution)
            updateWidget()
        } else {
            viewModel.checkForDuplicateNicknamesAndSaveInfo(
                isFirst = true,
                date = inputDate,
                nickName = nickName,
                amountOfSmoking = amountOfSmoking,
                tobaccoPrice = tobaccoPrice,
                myResolution = myResolution
            )
        }
    }

    /** 위젯 업데이트 */
    private fun updateWidget() {
        try {
            val updateWidgetIntent = Intent(this, WidgetProvider::class.java)
            updateWidgetIntent.action = WIDGET_UPDATE

            val pendingIntent = PendingIntent.getBroadcast(this, 0, updateWidgetIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            pendingIntent.send()
        } catch (e: CanceledException) {
            Log.e(TAG, "Widget Error ... $e")
        }
    }

    /** 수정에서 온 경우 메인으로 이동 */
    override fun onBackPressed() {
        if (isEdit) {
            goNextActivity(this, MainActivity::class.java, 0, true)
        } else {
            super.onBackPressed()
        }
    }

    companion object {
        private const val TAG = "SS_RegistrationActivity"
    }
}