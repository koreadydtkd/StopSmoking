package hys.hmonkeyys.stopsmoking.ui

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.Job

internal abstract class BaseViewModel : ViewModel() {

    abstract fun fetchData(): Job

}
