package hys.hmonkeyys.stopsmoking.screen.main.community.write

import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.core.view.isVisible
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.screen.BaseActivity
import hys.hmonkeyys.stopsmoking.databinding.ActivityWriteBinding
import hys.hmonkeyys.stopsmoking.extension.setOnDuplicatePreventionClickListener
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_FAIL
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_OTHER
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_SUCCESS
import hys.hmonkeyys.stopsmoking.utils.Utility.snackBar
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class WriteActivity : BaseActivity<WriteViewModel, ActivityWriteBinding>(), AdapterView.OnItemSelectedListener {

    override val viewModel: WriteViewModel by viewModel()
    override fun getViewBinding(): ActivityWriteBinding = ActivityWriteBinding.inflate(layoutInflater)

    private var selectedCategoryName = ""

    /** 뷰 초기화 */
    override fun initViews() = with(binding) {
        // 취소 버튼
        cancelView.setOnDuplicatePreventionClickListener {
            finish()
        }

        // 글 쓰기 버튼
        checkButton.setOnDuplicatePreventionClickListener {
            checkExceptionBeforeRegister()
        }
    }

    /** 등록 전 예외 확인 */
    private fun checkExceptionBeforeRegister() {
        val title = binding.titleEditText.text.toString().trim()
        val contents = binding.contentsEditText.text.toString()

        val exceptionMessage = when {
            title.length < 2 -> Pair("제목", 2)
            contents.length < 10 -> Pair("내용", 10)
            else -> null
        }

        if (exceptionMessage != null) {
            snackBar(binding.root, getString(R.string.message_one_letter_or_more, exceptionMessage.first, exceptionMessage.second))
            return
        }

        binding.progressBar.isVisible = true
        viewModel.registerPost(title, selectedCategoryName, contents)
    }

    override fun observeData() {
        viewModel.writeStateLiveData.observe(this) {
            when (it) {
                is WriteState.Initialize -> initSpinner()

                is WriteState.RegistrationSuccess -> writeSuccess()

                is WriteState.RegistrationFailed -> writeFail()
            }
        }
    }

    /** Spinner 초기화 */
    private fun initSpinner() {
        ArrayAdapter.createFromResource(this, R.array.category_array_write, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
        }
        binding.categorySpinner.onItemSelectedListener = this
    }

    /** 쓰기 성공 */
    private fun writeSuccess() {
        snackBar(binding.root, getString(R.string.message_upload_success))
        Handler(mainLooper).postDelayed({ finish() }, 350)
        binding.progressBar.isGone = true
    }

    /** 쓰기 실패 */
    private fun writeFail() {
        snackBar(binding.root, getString(R.string.message_upload_fail))
        binding.progressBar.isGone = true
    }

    /** Spinner 아이템 클릭 시 */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        val item = parent?.getItemAtPosition(position)
        when (item.toString()) {
            "금연 성공담" -> selectedCategoryName = NO_SMOKING_SUCCESS
            "금연 실패담" -> selectedCategoryName = NO_SMOKING_FAIL
            "잡담" -> selectedCategoryName = NO_SMOKING_OTHER
        }
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}
}