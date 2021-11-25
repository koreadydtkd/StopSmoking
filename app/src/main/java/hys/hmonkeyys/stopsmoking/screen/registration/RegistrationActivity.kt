package hys.hmonkeyys.stopsmoking.screen.registration

import android.annotation.SuppressLint
import android.app.PendingIntent
import android.app.PendingIntent.CanceledException
import android.content.Intent
import androidx.core.view.isGone
import androidx.core.view.isVisible
import com.google.firebase.crashlytics.FirebaseCrashlytics
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.databinding.ActivityRegistrationBinding
import hys.hmonkeyys.stopsmoking.extension.setOnDuplicatePreventionClickListener
import hys.hmonkeyys.stopsmoking.screen.BaseActivity
import hys.hmonkeyys.stopsmoking.screen.main.MainActivity
import hys.hmonkeyys.stopsmoking.utils.Constant.EDIT
import hys.hmonkeyys.stopsmoking.utils.Constant.WIDGET_UPDATE
import hys.hmonkeyys.stopsmoking.utils.Utility.getDatePicker
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.snackBar
import hys.hmonkeyys.stopsmoking.widget.WidgetProvider
import org.koin.androidx.viewmodel.ext.android.viewModel


internal class RegistrationActivity : BaseActivity<RegistrationViewModel, ActivityRegistrationBinding>() {

    override val viewModel: RegistrationViewModel by viewModel()
    override fun getViewBinding(): ActivityRegistrationBinding = ActivityRegistrationBinding.inflate(layoutInflater)

    private var isEdit = false

    /** 각 뷰 초기화 */
    override fun initViews() = with(binding) {
        // 최대 오늘날짜 까지 선택 가능
        datePicker.maxDate = System.currentTimeMillis()

        // 취소 버튼
        viewCancel.setOnDuplicatePreventionClickListener {
            onBackPressed()
        }

        // 기본 예외 처리 후 등록
        stopSmokingButton.setOnDuplicatePreventionClickListener {
            inputInformationException()
        }
    }

    override fun observeData() {
        viewModel.registrationLiveData.observe(this) {
            when (it) {
                // 초기화
                is RegistrationState.Initialized -> cameInFromMain()

                // 처음 금연 정보 등록할 때 닉네임 체크 후 기본 정보 저장
                is RegistrationState.CheckNickName -> nickNameCheckResult(it.hasNickName)

                // 수정하기 위해 들어온 경우 기본값 셋팅
                is RegistrationState.StoredValue -> setData(it.dateArray,
                    it.nickName, /*it.myResolution,*/
                    it.amountOfSmoking,
                    it.tobaccoPrice)

                // 수정 완료에 대한 처리
                is RegistrationState.EditInformation -> onBackPressed()
            }
        }
    }

    /** 메인에서 수정하기 위해 들어왔는지 확인 */
    private fun cameInFromMain() {
        isEdit = intent.getBooleanExtra(EDIT, false)
        if (isEdit) {
            viewModel.getDefaultData()
            binding.titleTextView.text = getString(R.string.edit)
        }
        binding.viewCancel.isVisible = isEdit
    }

    /** 수정하기 위해 넘어온 경우 셋팅 */
    private fun setData(
        dateArray: List<String>,
        nickName: String,
//        myResolution: String,
        amountOfSmoking: Int,
        tobaccoPrice: Int,
    ) = with(binding) {
        datePicker.updateDate(dateArray[0].toInt(), dateArray[1].toInt() - 1, dateArray[2].toInt())
        nickNameEditText.setText(nickName)
        nickNameEditText.isEnabled = false
        nickNameTextField.hint = getString(R.string.edit_nick_name)
//        myResolutionEditText.setText(myResolution)

        if (amountOfSmoking > 0 && tobaccoPrice > 0) {
            amountOfSmokingEditText.setText("$amountOfSmoking")
            tobaccoPriceEditText.setText("$tobaccoPrice")
        }

        stopSmokingButton.text = getString(R.string.edit)
    }

    /** 닉네임 중복확인 결과에 따른 분기처리 */
    private fun nickNameCheckResult(hasNickName: Boolean) {
        binding.progressBar.isGone = true

        when (hasNickName) {
            true -> goNextActivity(this, MainActivity::class.java, 0, true)
            false -> snackBar(binding.root, getString(R.string.message_reduplication))
        }
    }

    /** 입력 정보 예외 처리 */
    private fun inputInformationException() = with(binding) {
        val nickName = nickNameEditText.text.toString().trim()
        val amountOfSmoking = amountOfSmokingEditText.text.toString()
        val tobaccoPrice = tobaccoPriceEditText.text.toString()

        val exceptionMessage = when {
            // 닉네임 예외 처리
            nickName.isEmpty() -> getString(R.string.message_nick_name)
            nickName.contains(" ") -> getString(R.string.message_input_space)
            nickName.length < 2 || nickName.length > 10 -> getString(R.string.message_nick_name_length)

            // 하루 흡연량 예외 처리
            amountOfSmoking.isEmpty() -> getString(R.string.message_amount_of_smoking)
            amountOfSmoking.length > 3 -> getString(R.string.message_amount_of_smoking_length)
            amountOfSmoking.toInt() < 1 -> getString(R.string.message_zero_input_exception)

            // 담배 가격 예외 처리
            tobaccoPrice.isEmpty() -> getString(R.string.message_tobacco_price)
            tobaccoPrice.length > 6 -> getString(R.string.message_tobacco_price_length)
            tobaccoPrice.toInt() < 1 -> getString(R.string.message_zero_input_exception)

            else -> ""
        }

        if (exceptionMessage.isNotEmpty()) {
            snackBar(root, exceptionMessage)
            return
        }

        // 등록
        val inputDate = getDatePicker(datePicker.year, datePicker.month, datePicker.dayOfMonth)
        noSmokingRegistration(inputDate, nickName, amountOfSmoking.toInt(), tobaccoPrice.toInt()/*, myResolution*/)
    }

    /**
     * 수정인 경우 금연정보 수정 및 위젯 업데이트.
     * 등록인 경우 닉네임 체크 후 저장
     * */
    private fun noSmokingRegistration(inputDate: String, nickName: String, amountOfSmoking: Int, tobaccoPrice: Int) {
        binding.progressBar.isVisible = true

        if (isEdit) {
            viewModel.saveInfo(inputDate, amountOfSmoking, tobaccoPrice)
            updateWidget()
        } else {
            viewModel.checkForDuplicateNickname(
                isFirst = false,
                date = inputDate,
                nickName = nickName,
                amountOfSmoking = amountOfSmoking,
                tobaccoPrice = tobaccoPrice,
            )
        }
    }

    /** 위젯 업데이트 */
    @SuppressLint("UnspecifiedImmutableFlag")
    private fun updateWidget() {
        try {
            val updateWidgetIntent = Intent(this, WidgetProvider::class.java)
            updateWidgetIntent.action = WIDGET_UPDATE

            val pendingIntent = PendingIntent.getBroadcast(this, 0, updateWidgetIntent, PendingIntent.FLAG_CANCEL_CURRENT)
            pendingIntent.send()
        } catch (e: CanceledException) {
            FirebaseCrashlytics.getInstance().recordException(e)
        }
    }

    /** 수정에서 온 경우 메인으로 이동 */
    override fun onBackPressed() {
        if (isEdit) {
            binding.progressBar.isGone = true
            goNextActivity(this, MainActivity::class.java, 0, true)
        } else {
            super.onBackPressed()
        }
    }

    companion object {
//        private const val TAG = "SS_RegistrationActivity"
    }
}