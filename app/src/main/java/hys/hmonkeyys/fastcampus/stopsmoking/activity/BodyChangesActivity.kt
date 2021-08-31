package hys.hmonkeyys.fastcampus.stopsmoking.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import hys.hmonkeyys.fastcampus.stopsmoking.R

class BodyChangesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_changes)

        findViewById<View>(R.id.cancelView).setOnClickListener {
            finish()
        }
    }

}