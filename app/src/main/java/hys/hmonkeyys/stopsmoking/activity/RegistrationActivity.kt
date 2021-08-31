package hys.hmonkeyys.stopsmoking.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.core.content.edit
import androidx.core.view.isVisible
import com.google.android.material.snackbar.Snackbar
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.AMOUNT_OF_SMOKING_PER_DAY
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.EDIT
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.IS_REGISTRATION
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.MY_RESOLUTION
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NICK_NAME
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.STOP_SMOKING_DATE
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.TOBACCO_PRICE
import hys.hmonkeyys.stopsmoking.databinding.ActivityRegistrationBinding
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.APP_DEFAULT_KEY

class RegistrationActivity : AppCompatActivity() {

    private val binding: ActivityRegistrationBinding by lazy { ActivityRegistrationBinding.inflate(layoutInflater) }
    private val spf: SharedPreferences by lazy { getSharedPreferences(APP_DEFAULT_KEY, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        val isEdit = intent.getBooleanExtra(EDIT, false)
        if (isEdit) {
            setDefaultData()
            binding.cancelView.isVisible = true
        } else {
            binding.cancelView.isVisible = false
        }

        initViews()
    }

    /** 수정에서 들어온 경우 셋팅 */
    private fun setDefaultData() {
        val nickName = spf.getString(NICK_NAME, "")
        val amountOfSmoking = spf.getInt(AMOUNT_OF_SMOKING_PER_DAY, 0)
        val tobaccoPrice = spf.getInt(TOBACCO_PRICE, 0)
        val myResolution = spf.getString(MY_RESOLUTION, "")


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
            goMainActivity()
        }

        // 기본 예외 처리 후 등록
        binding.stopSmokingButton.setOnClickListener {
            if (binding.nickNameEditText.text.toString().isBlank()) {
                showSnackBar(getString(R.string.message_nick_name))
                return@setOnClickListener
            }

            if (binding.amountOfSmokingEditText.text.isNullOrEmpty()) {
                showSnackBar(getString(R.string.message_amount_of_smoking))
                return@setOnClickListener
            }

            if (binding.tobaccoPriceEditText.text.isNullOrEmpty()) {
                showSnackBar(getString(R.string.message_tobacco_price))
                return@setOnClickListener
            }

            if (binding.myResolutionEditText.text.toString().isBlank()) {
                showSnackBar(getString(R.string.message_my_resolution))
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
            showSnackBar(getString(R.string.message_input_space))
            return
        }

        // 기본 값 0 입력 체크
        if (amountOfSmoking < 1 || tobaccoPrice < 1) {
            showSnackBar(getString(R.string.message_zero_input_exception))
            return
        }

        // todo 정보 저장하기 전 닉네임 중복확인 후 다음 프로세스

        // 금연 관련 정보 저장
        spf.edit {
            putBoolean(IS_REGISTRATION, true)
            putString(STOP_SMOKING_DATE, getDatePicker())
            putString(NICK_NAME, nickName)
            putInt(AMOUNT_OF_SMOKING_PER_DAY, amountOfSmoking)
            putInt(TOBACCO_PRICE, tobaccoPrice)
            putString(MY_RESOLUTION, myResolution)
        }

        goMainActivity()
    }

    /** 금연 시작날짜 가져오기 */
    private fun getDatePicker(): String {
        val year = binding.datePicker.year
        val month = "%02d".format(binding.datePicker.month + 1)
        val day = "%02d".format(binding.datePicker.dayOfMonth)
        return "${year}-${month}-$day"
    }

    /** 메인 화면으로 이동 */
    private fun goMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    /** 스낵바 띄우기 */
    private fun showSnackBar(text: String) {
        Snackbar.make(binding.root, text, Snackbar.LENGTH_SHORT).show()
    }

    override fun onBackPressed() {
        goMainActivity()
    }
}