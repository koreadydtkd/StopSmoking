package hys.hmonkeyys.fastcampus.stopsmoking

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.content.edit
import hys.hmonkeyys.fastcampus.stopsmoking.SharedPreferencesKey.Companion.AMOUNT_OF_SMOKING_PER_DAY
import hys.hmonkeyys.fastcampus.stopsmoking.SharedPreferencesKey.Companion.IS_REGISTRATION
import hys.hmonkeyys.fastcampus.stopsmoking.SharedPreferencesKey.Companion.MY_RESOLUTION
import hys.hmonkeyys.fastcampus.stopsmoking.SharedPreferencesKey.Companion.SHARED_PREFERENCES_KEY
import hys.hmonkeyys.fastcampus.stopsmoking.SharedPreferencesKey.Companion.STOP_SMOKING_DATE
import hys.hmonkeyys.fastcampus.stopsmoking.SharedPreferencesKey.Companion.TOBACCO_PRICE
import hys.hmonkeyys.fastcampus.stopsmoking.databinding.ActivityRegistrationBinding

class RegistrationActivity : AppCompatActivity() {

    private val binding: ActivityRegistrationBinding by lazy { ActivityRegistrationBinding.inflate(layoutInflater) }
    private val spf: SharedPreferences by lazy { getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
    }

    /** 각 뷰 초기화 */
    private fun initViews() {
        binding.stopSmokingButton.setOnClickListener {
            if(binding.amountOfSmokingEditText.text.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.toast_amount_of_smoking), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.tobaccoPriceEditText.text.isNullOrEmpty()) {
                Toast.makeText(this, getString(R.string.toast_tobacco_price), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if(binding.myResolutionEditText.text.toString().isBlank()) {
                Toast.makeText(this, getString(R.string.toast_my_resolution), Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // 금연 정보 등록
            noSmokingRegistration()
        }
    }

    /** 금연 정보 등록 */
    private fun noSmokingRegistration() {
        val amountOfSmoking = binding.amountOfSmokingEditText.text.toString().toInt()
        val tobaccoPrice = binding.tobaccoPriceEditText.text.toString().toInt()
        val myResolution = binding.myResolutionEditText.text.toString()

        // 기본 값 0 입력 체크
        if(amountOfSmoking < 1 || tobaccoPrice < 1) {
            Toast.makeText(this, getString(R.string.toast_zero_input_exception), Toast.LENGTH_SHORT).show()
            return
        }

        // 금연 관련 정보 저장
        spf.edit {
            putBoolean(IS_REGISTRATION, true)
            putString(STOP_SMOKING_DATE, getDatePicker())
            putInt(AMOUNT_OF_SMOKING_PER_DAY, amountOfSmoking)
            putInt(TOBACCO_PRICE, tobaccoPrice)
            putString(MY_RESOLUTION, myResolution)
        }

        // 메인 화면으로 이동
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
}