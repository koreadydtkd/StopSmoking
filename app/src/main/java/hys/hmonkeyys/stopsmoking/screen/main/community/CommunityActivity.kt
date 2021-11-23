package hys.hmonkeyys.stopsmoking.screen.main.community

import android.content.Intent
import android.os.Handler
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.firebase.crashlytics.FirebaseCrashlytics
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.model.CommunityModel
import hys.hmonkeyys.stopsmoking.screen.BaseActivity
import hys.hmonkeyys.stopsmoking.screen.main.community.adapter.CommunityAdapter
import hys.hmonkeyys.stopsmoking.screen.main.community.detail.CommunityDetailActivity
import hys.hmonkeyys.stopsmoking.databinding.ActivityCommunityBinding
import hys.hmonkeyys.stopsmoking.extension.setOnDuplicatePreventionClickListener
import hys.hmonkeyys.stopsmoking.screen.main.community.write.WriteActivity
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_ALL
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_FAIL
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_MY_POST
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_OTHER
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_SUCCESS
import hys.hmonkeyys.stopsmoking.utils.Utility.goNextActivity
import hys.hmonkeyys.stopsmoking.utils.Utility.snackBar
import org.koin.androidx.viewmodel.ext.android.viewModel


internal class CommunityActivity : BaseActivity<CommunityViewModel, ActivityCommunityBinding>(), AdapterView.OnItemSelectedListener {

    override val viewModel: CommunityViewModel by viewModel()
    override fun getViewBinding(): ActivityCommunityBinding = ActivityCommunityBinding.inflate(layoutInflater)

    private lateinit var communityAdapter: CommunityAdapter

    private lateinit var currentItemId: String

    /** 뷰 초기화 */
    override fun initViews() = with(binding) {
        // 취소 버튼
        cancelView.setOnDuplicatePreventionClickListener {
            finish()
        }

        noticeTextView.setOnDuplicatePreventionClickListener {
            // todo 공지사항 내용 팝업 알림
        }

        // 글 쓰기 버튼
        writeButton.setOnDuplicatePreventionClickListener {
            goNextActivity(this@CommunityActivity, WriteActivity::class.java, 0L)
        }

        initSpinner()
        initRecyclerView()
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
    private fun initRecyclerView() = with(binding) {
        communityAdapter = CommunityAdapter {
            goDetailActivity(it)
        }
        recyclerView.adapter = communityAdapter

        recyclerView.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                try {
                    // 화면에 보이는 마지막 아이템의 position
                    val lastVisibleItemPosition =
                        (recyclerView.layoutManager as LinearLayoutManager).findLastCompletelyVisibleItemPosition()

                    // adapter 아이템의 총 개수 -1
                    recyclerView.adapter?.let {
                        val itemTotalCount = it.itemCount - 1

                        // 스크롤이 끝에 도달했는지 확인
                        if (recyclerView.canScrollVertically(1).not() && lastVisibleItemPosition == itemTotalCount) {
                            progressBar.isVisible = true
                            viewModel.fetchPostList(currentItemId, true)
                        }
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                    FirebaseCrashlytics.getInstance().recordException(e)
                }
            }
        })
    }

    override fun observeData() {
        viewModel.communityLiveData.observe(this) {
            when (it) {
                is CommunityState.Initialize -> initAdmob()

                is CommunityState.PostFetchSuccess -> setPostList(it.communityList)

                is CommunityState.PostMoreFetchSuccess -> addPostList(it.communityList)

                is CommunityState.PostFetchAll -> fetchedAll()
            }
        }
    }

    /** 게시물 데이터 셋팅 */
    private fun setPostList(communityList: List<CommunityModel>) {
        binding.progressBar.isGone = true

        if (communityList.isEmpty()) {
            binding.recyclerView.isInvisible = true
            binding.noListTextView.isVisible = true
        } else {
            binding.noListTextView.isGone = true
            binding.recyclerView.isVisible = true
            communityAdapter.setList(communityList)
        }
    }

    /** 더 가져온 데이터 리스트 추가 */
    private fun addPostList(communityList: List<CommunityModel>) {
        binding.progressBar.isGone = true
        communityAdapter.addList(communityList)
    }

    /** 데이터 모두 가져옴 */
    private fun fetchedAll() {
        binding.progressBar.isGone = true
        snackBar(binding.root, getString(R.string.fetched_all_data))
    }

    /** Detail Activity 로 이동 */
    private fun goDetailActivity(communityModel: CommunityModel) {
        startActivity(
            Intent(this, CommunityDetailActivity::class.java).apply {
                putExtra(COMMUNITY_DETAIL_KEY, communityModel)
            }
        )
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
                }

                override fun onAdFailedToLoad(error: LoadAdError) {
                    super.onAdFailedToLoad(error)
                }
            }
        }
    }

    /** Spinner 아이템 클릭 시
     * 중복 클릭 방지
     * */
    override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
        view?.isClickable = false
        binding.progressBar.isVisible = true

        val item = parent?.getItemAtPosition(position)
        currentItemId = when (item.toString()) {
            "전체" -> NO_SMOKING_ALL
            "금연 성공담" -> NO_SMOKING_SUCCESS
            "금연 실패담" -> NO_SMOKING_FAIL
            "잡담" -> NO_SMOKING_OTHER
            "내가 쓴 글" -> NO_SMOKING_MY_POST
            else -> NO_SMOKING_ALL
        }
        viewModel.fetchPostList(currentItemId)

        Handler(mainLooper).postDelayed({
            view?.isClickable = true
        }, 250)
    }

    override fun onNothingSelected(parent: AdapterView<*>?) {}

    override fun onResume() {
        super.onResume()

        // 최초 조회 후에 계속 불러오도록
        if (::currentItemId.isInitialized) {
            viewModel.fetchPostList(currentItemId)
        }
    }

    companion object {
        //        private const val TAG = "SS_CommunityActivity"
        const val COMMUNITY_DETAIL_KEY = "community_detail_key"
    }
}