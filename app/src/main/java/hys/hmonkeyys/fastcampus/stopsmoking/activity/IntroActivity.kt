package hys.hmonkeyys.fastcampus.stopsmoking.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import hys.hmonkeyys.fastcampus.stopsmoking.R
import hys.hmonkeyys.fastcampus.stopsmoking.utils.AppShareKey.Companion.IS_REGISTRATION
import hys.hmonkeyys.fastcampus.stopsmoking.utils.AppShareKey.Companion.SHARED_PREFERENCES_KEY

class IntroActivity : AppCompatActivity() {
    private val spf: SharedPreferences by lazy {
        getSharedPreferences(SHARED_PREFERENCES_KEY, Context.MODE_PRIVATE)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        goNextActivity()
    }

    private fun goNextActivity() {
        val isRegistration = spf.getBoolean(IS_REGISTRATION, false)

        Handler(mainLooper).postDelayed({
            if(isRegistration) {
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                startActivity(Intent(this, RegistrationActivity::class.java))
            }
            finish()
        }, 1200)
    }
}