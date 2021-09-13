package hys.hmonkeyys.stopsmoking.activity.communitydetail

import android.os.Bundle
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.activity.BaseActivity
import hys.hmonkeyys.stopsmoking.activity.community.CommunityActivity.Companion.COMMUNITY_DETAIL_KEY
import hys.hmonkeyys.stopsmoking.data.entity.CommunityModel
import hys.hmonkeyys.stopsmoking.databinding.ActivityCommunityDetailBinding
import hys.hmonkeyys.stopsmoking.utils.AppShareKey
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_FAIL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_OTHER
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_SUCCESS
import hys.hmonkeyys.stopsmoking.utils.Utility
import hys.hmonkeyys.stopsmoking.utils.convertTimeStampToDateFormat
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
                    getIntentAndSetView()
                    initViews()
                }
            }
        }
    }

    /** 전달 받은 Intent 데이터 뷰에 셋팅 */
    private fun getIntentAndSetView() {
        communityModel = intent.getParcelableExtra<CommunityModel>(COMMUNITY_DETAIL_KEY)
        communityModel?.let {
            binding.writerTextView.text = Utility.changePartialTextColor(
                getString(R.string.from_writer, it.writer),
                getColor(R.color.black),
                0,
                it.writer.length
            )

            binding.titleTextView.text = it.title
            binding.dateTextView.text = it.date.convertTimeStampToDateFormat()
            binding.contentsTextView.text = it.contents
            binding.viewCountTextView.text = "${1014}"
            binding.categoryTextView.text = when (it.category) {
                NO_SMOKING_SUCCESS -> "금연 성공담"
                NO_SMOKING_FAIL -> "금연 실패담"
                else -> "잡담"
            }
        }
    }

    /** 뷰 초기화 */
    private fun initViews() {
        // cancel button
        binding.cancelView.setOnClickListener {
            finish()
        }
    }

}