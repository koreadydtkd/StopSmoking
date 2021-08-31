package hys.hmonkeyys.stopsmoking.activity

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.crashlytics.FirebaseCrashlytics
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.APP_DEFAULT_KEY
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.FIREBASE_IMAGE_URL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.IS_REGISTRATION

class IntroActivity : AppCompatActivity() {
    private val spf: SharedPreferences by lazy {
        getSharedPreferences(APP_DEFAULT_KEY, Context.MODE_PRIVATE)
    }

    private val storage: FirebaseStorage by lazy { Firebase.storage }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_intro)

        storage.reference.child(IMAGE_URL_PATH).child(IMAGE_NAME).downloadUrl
            .addOnSuccessListener { uri ->
                spf.edit().putString(FIREBASE_IMAGE_URL, uri.toString()).apply()
                goNextActivity()
            }.addOnFailureListener {
                FirebaseCrashlytics.getInstance().recordException(it)
                goNextActivity()
            }
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

    companion object {
        private const val IMAGE_URL_PATH = "stopsmoking"
        private const val IMAGE_NAME = "icon.png"
    }
}