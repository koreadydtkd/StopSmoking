package hys.hmonkeyys.stopsmoking.ui.community

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.ui.BaseActivity
import hys.hmonkeyys.stopsmoking.ui.adapter.CommunityAdapter
import hys.hmonkeyys.stopsmoking.ui.community.detail.CommunityDetailActivity
import hys.hmonkeyys.stopsmoking.ui.write.WriteActivity
import hys.hmonkeyys.stopsmoking.databinding.ActivityCommunityBinding
import hys.hmonkeyys.stopsmoking.extension.setOnDuplicatePreventionClickListener
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_ALL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_FAIL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_MY_POST
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_OTHER
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_SUCCESS
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.showSnackBar
import org.koin.androidx.viewmodel.ext.android.viewModel


internal class CommunityActivity : BaseActivity<CommunityViewModel>(), AdapterView.OnItemSelectedListener {
    private val binding: ActivityCommunityBinding by lazy { ActivityCommunityBinding.inflate(layoutInflater) }
    override val viewModel: CommunityViewModel by viewModel()

    private lateinit var communityAdapter: CommunityAdapter

    private lateinit var currentItemId: String

    override fun observeData() {
        viewModel.communityLiveData.observe(this) {
            when (it) {
                is CommunityState.Initialize -> {
                    initViews()
                    initRecyclerView()
                    initSpinner()
                    initAdmob()
                }
                is CommunityState.PostFetchSuccess -> {
                    binding.progressBar.visibility = View.GONE

                    if (it.communityList.isEmpty()) {
                        binding.recyclerView.visibility = View.INVISIBLE
                        binding.noListTextView.visibility = View.VISIBLE
                    } else {
                        binding.noListTextView.visibility = View.GONE
                        binding.recyclerView.visibility = View.VISIBLE
                        communityAdapter.setList(it.communityList)
                    }

                }

                is CommunityState.PostMoreFetchSuccess -> {
                    binding.progressBar.visibility = View.GONE
                    if (it.communityMoreList.isEmpty()) {
                        showSnackBar(binding.root, getString(R.string.fetched_all_data))
                    } else {
                        communityAdapter.addList(it.communityMoreList)
                    }

                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun onResume() {
        super.onResume()

        // 최초 조회 후에 계속 불러오도록
        if (::currentItemId.isInitialized) {
            viewModel.fetchPostList(currentItemId)
        }
    }

    /** 뷰 초기화 */
    private fun initViews() {
        // 취소 버튼
        binding.cancelView.setOnDuplicatePreventionClickListener {
            finish()
        }

        binding.noticeTextView.setOnDuplicatePreventionClickListener {
            // todo 공지사항 내용 팝업 알림
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

        binding.recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                try {
                    // 화면에 보이는 마지막 아이템의 position
                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

                    // adapter 아이템의 총 개수 -1
                    val itemTotalCount = recyclerView.adapter!!.itemCount - 1

                    // 스크롤이 끝에 도달했는지 확인
                    if (binding.recyclerView.canScrollVertically(1).not() && lastVisibleItemPosition == itemTotalCount) {
                        // 추가 리스트 가져오기
                        binding.progressBar.visibility = View.VISIBLE
                        viewModel.fetchMorePostList(currentItemId)
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
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
            "전체" -> currentItemId = NO_SMOKING_ALL
            "금연 성공담" -> currentItemId = NO_SMOKING_SUCCESS
            "금연 실패담" -> currentItemId = NO_SMOKING_FAIL
            "잡담" -> currentItemId = NO_SMOKING_OTHER
            "내가 쓴 글" -> currentItemId = NO_SMOKING_MY_POST
        }
        viewModel.fetchPostList(currentItemId)

        Handler(mainLooper).postDelayed({
            view?.isClickable = true
        }, 350)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    companion object {
        private const val TAG = "SS_CommunityActivity"

        const val COMMUNITY_DETAIL_KEY = "community_detail_key"
    }
}