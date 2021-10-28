package hys.hmonkeyys.stopsmoking.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import kotlinx.coroutines.Job

internal abstract class BaseComponentActivity<VM : BaseViewModel> : ComponentActivity() {

    abstract val viewModel: VM

    private lateinit var fetchJob: Job

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        fetchJob = viewModel.fetchData()
        observeData()
    }

    abstract fun observeData()

    override fun onDestroy() {
        if (fetchJob.isActive) {
            fetchJob.cancel()
        }

        super.onDestroy()
    }

}