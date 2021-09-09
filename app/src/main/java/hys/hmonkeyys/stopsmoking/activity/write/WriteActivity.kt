package hys.hmonkeyys.stopsmoking.activity.write

import android.os.Bundle
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.activity.BaseActivity
import hys.hmonkeyys.stopsmoking.databinding.ActivityWriteBinding
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_FAIL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_OTHER
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_SUCCESS
import hys.hmonkeyys.stopsmoking.utils.Utility.showSnackBar
import hys.hmonkeyys.stopsmoking.utils.setOnDuplicatePreventionClickListener
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
                WriteState.NickNameError -> showSnackBar(binding.root, "예기치 못한 오류가 발생했습니다.\n잠시 후 다시 시도해주세요.")
                is WriteState.RegisterSuccess -> {
                    if (it.isSuccess) {
                        showSnackBar(binding.root, "업로드 성공")
                        Handler(mainLooper).postDelayed({ finish() }, 300)
                    } else {
                        showSnackBar(binding.root, "업로드 실패\n잠시 후 다시 시도해주세요.")
                    }

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

    /** 등록 전 예외 학인 */
    private fun checkExceptionBeforeRegister() {
        val title = binding.titleEditText.text.toString().trim()
        val contents = binding.contentsEditText.text.toString()

        when {
            title.length < 2 -> {
                showSnackBar(binding.root, "제목은 최소 1자 이상 입력해주세요.")
                return
            }

            contents.length < 10 -> {
                showSnackBar(binding.root, "내용은 최소 10자 이상 입력해주세요.")
                return
            }
        }

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