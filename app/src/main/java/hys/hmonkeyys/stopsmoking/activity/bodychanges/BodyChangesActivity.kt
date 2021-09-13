package hys.hmonkeyys.stopsmoking.activity.bodychanges

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.utils.setOnDuplicatePreventionClickListener

class BodyChangesActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_body_changes)

        findViewById<View>(R.id.cancelView).setOnDuplicatePreventionClickListener {
            finish()
        }
    }

}