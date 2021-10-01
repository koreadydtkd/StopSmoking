package hys.hmonkeyys.stopsmoking.activitys.write

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.activitys.BaseActivity
import hys.hmonkeyys.stopsmoking.databinding.ActivityWriteBinding
import hys.hmonkeyys.stopsmoking.extension.setOnDuplicatePreventionClickListener
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_FAIL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_OTHER
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_SUCCESS
import hys.hmonkeyys.stopsmoking.utils.Utility.showSnackBar
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class WriteActivity : BaseActivity<WriteViewModel>(), AdapterView.OnItemSelectedListener {

    private val binding: ActivityWriteBinding by lazy { ActivityWriteBinding.inflate(layoutInflater) }
    override val viewModel: WriteViewModel by viewModel()

    private var selectedCategoryName = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun observeData() {
        viewModel.writeStateLiveData.observe(this) {
            when (it) {
                is WriteState.Initialize -> {
                    initViews()
                    initSpinner()
                }

                is WriteState.RegisterSuccess -> {
                    if (it.isSuccess) {
                        showSnackBar(binding.root, getString(R.string.message_upload_success))
                        Handler(mainLooper).postDelayed({ finish() }, 350)
                    } else {
                        showSnackBar(binding.root, getString(R.string.message_upload_fail))
                    }
                    binding.progressBar.visibility = View.GONE
                }
            }
        }
    }

    /** 뷰 초기화 */
    private fun initViews() {
        // 취소 버튼
        binding.cancelView.setOnDuplicatePreventionClickListener {
            finish()
        }

        // 글 쓰기 버튼
        binding.checkButton.setOnDuplicatePreventionClickListener {
            checkExceptionBeforeRegister()
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

    /** 등록 전 예외 확인 */
    private fun checkExceptionBeforeRegister() {
        val title = binding.titleEditText.text.toString().trim()
        val contents = binding.contentsEditText.text.toString()

        when {
            title.length < 2 -> {
                showSnackBar(binding.root, getString(R.string.message_one_letter_or_more, "제목", 2))
                return
            }

            contents.length < 10 -> {
                showSnackBar(binding.root, getString(R.string.message_one_letter_or_more, "내용", 10))
                return
            }
        }

        binding.progressBar.visibility = View.VISIBLE
        viewModel.registerPost(title, selectedCategoryName, contents)
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