package hys.hmonkeyys.stopsmoking.activitys.intro

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.TextView
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.layout
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.constraintlayout.compose.ConstraintLayout
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.activitys.BaseActivity
import hys.hmonkeyys.stopsmoking.activitys.BaseComponentActivity
import hys.hmonkeyys.stopsmoking.activitys.main.MainActivity
import hys.hmonkeyys.stopsmoking.activitys.registration.RegistrationActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class IntroActivity : BaseComponentActivity<IntroViewModel>() {
    override val viewModel: IntroViewModel by viewModel()

    var enabled = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            InitText()
            WaitText()
        }

//        setContentView(R.layout.activity_intro)

        showDelayTextView()
    }

    override fun observeData() {
        viewModel.introLiveData.observe(this) {
            when (it) {
                is IntroState.GetImageUrlForKakaoLink -> {
                    goNext()
                }
            }
        }
    }

    /** 잠시 기다려달라는 텍스트뷰 보여주기 */
    private fun showDelayTextView() {
        try {
            Handler(mainLooper).postDelayed({
//                findViewById<TextView>(R.id.delayTextView).visibility = View.VISIBLE


            }, 500)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    /**
     * 처음이면 등록화면
     * 아니면 메인화면
     * */
    private fun goNext() {
//        if (viewModel.isFirstTime()) {
//            goNextActivity(this, MainActivity::class.java, 400, true)
//        } else {
//            goNextActivity(this, RegistrationActivity::class.java, 400, true)
//        }
    }
}

// ================================ Compose Start ============================

@Preview
@Composable
private fun InitText() {

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = stringResource(R.string.holding_it_in_well),
            color = colorResource(id = R.color.main_color),
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 2.sp,
        )
    }
}

@Composable
private fun WaitText() {

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 24.dp),
        verticalArrangement = Arrangement.Bottom,
        horizontalAlignment = Alignment.CenterHorizontally,
        ) {
        Text(
            text = stringResource(R.string.wait),
            fontSize = 12.sp,
        )
    }

}