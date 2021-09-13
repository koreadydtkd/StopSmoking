package hys.hmonkeyys.stopsmoking.activity.community

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.activity.BaseActivity
import hys.hmonkeyys.stopsmoking.activity.community.adapter.CommunityAdapter
import hys.hmonkeyys.stopsmoking.activity.communitydetail.CommunityDetailActivity
import hys.hmonkeyys.stopsmoking.activity.write.WriteActivity
import hys.hmonkeyys.stopsmoking.databinding.ActivityCommunityBinding
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_ALL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_FAIL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_OTHER
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_SUCCESS
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
import hys.hmonkeyys.stopsmoking.utils.setOnDuplicatePreventionClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel


internal class CommunityActivity : BaseActivity<CommunityViewModel>(), AdapterView.OnItemSelectedListener {

    private val binding: ActivityCommunityBinding by lazy { ActivityCommunityBinding.inflate(layoutInflater) }
    override val viewModel: CommunityViewModel by viewModel()

    private lateinit var communityAdapter: CommunityAdapter

    override fun observeData() {
        viewModel.communityLiveData.observe(this) {
            when(it) {
                is CommunityState.Initialize -> {
                    initViews()
                    initRecyclerView()
                    initSpinner()
                    initAdmob()
                }
                is CommunityState.PostFetchSuccess -> {
                    binding.progressBar.visibility = View.GONE
                    communityAdapter.submitList(it.communityList)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    /** 뷰 초기화 */
    private fun initViews() {
        // 취소 버튼
        binding.cancelView.setOnDuplicatePreventionClickListener {
            finish()
        }

        // 글 쓰기 버튼
        binding.writeButton.setOnDuplicatePreventionClickListener {
            goNextActivity(this, WriteActivity::class.java, 0L)
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

    /** RecyclerView 초기화 */
    private fun initRecyclerView() {
        communityAdapter = CommunityAdapter {
            startActivity(
                Intent(this, CommunityDetailActivity::class.java).apply {
                    putExtra(COMMUNITY_DETAIL_KEY, it)
                }
            )
        }

        binding.recyclerView.adapter = communityAdapter
    }

    /** 하단 배너광고 초기화 */
    private fun initAdmob() {
        MobileAds.initialize(this)
        val adRequest = AdRequest.Builder().build()

        binding.adView.apply {
            loadAd(adRequest)
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    super.onAdLoaded()
                    Log.i(TAG, "onAdLoaded")
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                    Log.e(TAG, "광고 로드에 문제 발생 onAdFailedToLoad ${error.message}")
                }
            }
        }
    }

    /** Spinner 아이템 클릭 시
     * 중복 클릭 방지
     * */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        view?.isClickable = false
        binding.progressBar.visibility = View.VISIBLE

        val item = parent?.getItemAtPosition(position)
        when (item.toString()) {
            "모두" -> viewModel.fetchPostList(NO_SMOKING_ALL)
            "금연 성공담" -> viewModel.fetchPostList(NO_SMOKING_SUCCESS)
            "금연 실패담" -> viewModel.fetchPostList(NO_SMOKING_FAIL)
            "잡담" -> viewModel.fetchPostList(NO_SMOKING_OTHER)
        }

        Handler(mainLooper).postDelayed({
            view?.isClickable = true
        }, 300)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        private const val TAG = "SS_CommunityActivity"

        const val COMMUNITY_DETAIL_KEY = "community_detail_key"
    }
}