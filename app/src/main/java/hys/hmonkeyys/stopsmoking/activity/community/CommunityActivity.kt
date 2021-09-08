package hys.hmonkeyys.stopsmoking.activity.community

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.databinding.ActivityCommunityBinding

class CommunityActivity : AppCompatActivity(), AdapterView.OnItemSelectedListener {

    private val binding: ActivityCommunityBinding by lazy { ActivityCommunityBinding.inflate(layoutInflater) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        initViews()
        initSpinner()
    }

    /** 뷰 초기화 */
    private fun initViews() {
        binding.cancelView.setOnClickListener {
            finish()
        }

        binding.writeButton.setOnClickListener {
            // todo 글쓰기 화면으로 이동
        }
    }

    /** Spinner 초기화 */
    private fun initSpinner() {
        ArrayAdapter.createFromResource(this, R.array.category_array, android.R.layout.simple_spinner_item).also { adapter ->
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            binding.categorySpinner.adapter = adapter
        }
        binding.categorySpinner.onItemSelectedListener = this
    }


    // todo ViewModel 로 이동. 카테고리에 맞게 데이터 가져오기
    private fun fetchPostList(postCategory: String) {
        // todo 받은 데이터 리사이클러뷰에 뿌려주기
    }

    /** Spinner 아이템 클릭 시
     * 중복 클릭 방지
     * */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        view?.isClickable = false

        val item = parent?.getItemAtPosition(position)
        when (item.toString()) {
            "금연 성공담" -> fetchPostList(NO_SMOKING_SUCCESS)
            "금연 실패담" -> fetchPostList(NO_SMOKING_FAIL)
            "잡담" -> fetchPostList(NO_SMOKING_OTHER)
        }

        Handler(mainLooper).postDelayed({
            view?.isClickable = true
        }, 500)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        private const val NO_SMOKING_SUCCESS = "no_smoking_success"
        private const val NO_SMOKING_FAIL = "no_smoking_fail"
        private const val NO_SMOKING_OTHER = "no_smoking_other"
    }
}