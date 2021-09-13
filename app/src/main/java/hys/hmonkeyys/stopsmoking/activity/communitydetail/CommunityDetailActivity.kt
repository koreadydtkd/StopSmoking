package hys.hmonkeyys.stopsmoking.activity.communitydetail

import android.os.Bundle
import hys.hmonkeyys.stopsmoking.activity.BaseActivity
import hys.hmonkeyys.stopsmoking.activity.community.CommunityActivity.Companion.COMMUNITY_DETAIL_KEY
import hys.hmonkeyys.stopsmoking.data.entity.CommunityModel
import hys.hmonkeyys.stopsmoking.databinding.ActivityCommunityDetailBinding
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class CommunityDetailActivity : BaseActivity<CommunityDetailViewModel>() {

    private val binding: ActivityCommunityDetailBinding by lazy { ActivityCommunityDetailBinding.inflate(layoutInflater) }
    override val viewModel: CommunityDetailViewModel by viewModel()

    private var communityModel: CommunityModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun observeData() {
        viewModel.communityDetailLiveData.observe(this) {
            when (it) {
                CommunityDetailState.Initialized -> {
                    initIntent()
                }
            }
        }
    }

    /** 전달 받은 Intent 데이터 뷰에 셋팅 */
    private fun initIntent() {
        communityModel = intent.getParcelableExtra<CommunityModel>(COMMUNITY_DETAIL_KEY)
        communityModel?.let {
            it.title
            it.date
            it.category
            it.contents
            it.writer

            // todo 디테일 뷰에 뿌려주기
        }
    }
}