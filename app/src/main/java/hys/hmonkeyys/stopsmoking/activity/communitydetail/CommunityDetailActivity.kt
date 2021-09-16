package hys.hmonkeyys.stopsmoking.activity.communitydetail

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AlertDialog
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.activity.BaseActivity
import hys.hmonkeyys.stopsmoking.activity.community.CommunityActivity.Companion.COMMUNITY_DETAIL_KEY
import hys.hmonkeyys.stopsmoking.activity.communitydetail.adapter.CommentAdapter
import hys.hmonkeyys.stopsmoking.data.entity.CommentModel
import hys.hmonkeyys.stopsmoking.data.entity.CommunityModel
import hys.hmonkeyys.stopsmoking.databinding.ActivityCommunityDetailBinding
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_FAIL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_SUCCESS
import hys.hmonkeyys.stopsmoking.utils.Utility
import hys.hmonkeyys.stopsmoking.utils.Utility.hideKeyboardAndCursor
import hys.hmonkeyys.stopsmoking.utils.Utility.showSnackBar
import hys.hmonkeyys.stopsmoking.utils.convertTimeStampToDateFormat
import hys.hmonkeyys.stopsmoking.utils.setOnDuplicatePreventionClickListener
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class CommunityDetailActivity : BaseActivity<CommunityDetailViewModel>() {

    private val binding: ActivityCommunityDetailBinding by lazy { ActivityCommunityDetailBinding.inflate(layoutInflater) }
    override val viewModel: CommunityDetailViewModel by viewModel()

    private var communityModel: CommunityModel? = null
    private lateinit var adapter: CommentAdapter

    private var isMyPost = false
    private lateinit var documentId: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
    }

    override fun observeData() {
        viewModel.communityDetailLiveData.observe(this) {
            when (it) {
                is CommunityDetailState.Initialized -> {
                    getIntentAndSetView()
                    initViews()
                    initRecyclerView()
                    viewModel.fetchComments(documentId)
                }

                is CommunityDetailState.FetChCommentList -> {
                    setCommentList(it.commentList)
                }
            }
        }
    }

    /** 전달 받은 Intent 데이터 뷰에 셋팅 */
    private fun getIntentAndSetView() {
        communityModel = intent.getParcelableExtra<CommunityModel>(COMMUNITY_DETAIL_KEY)
        communityModel?.let {
            isMyPost = viewModel.isMyPost(it.writer)
            documentId = it.uid

            binding.writerTextView.text = if (isMyPost) {
                getString(R.string.from_writer_me)
            } else {
                Utility.changePartialTextColor(
                    getString(R.string.from_writer, it.writer),
                    getColor(R.color.black),
                    0,
                    it.writer.length
                )
            }

            binding.titleTextView.text = it.title
            binding.dateTextView.text = it.date.convertTimeStampToDateFormat()
            binding.contentsTextView.text = it.contents
            binding.viewCountTextView.text = "${it.viewsCount}"
            binding.categoryTextView.text = when (it.category) {
                NO_SMOKING_SUCCESS -> "금연 성공담"
                NO_SMOKING_FAIL -> "금연 실패담"
                else -> "잡담"
            }

            // 조회수 추가
            viewModel.addViewsCount(it)
        }
    }

    /** 뷰 초기화 */
    private fun initViews() {
        // cancel button
        binding.cancelView.setOnDuplicatePreventionClickListener {
            finish()
        }

        // 자신이 게시한 글인지에 따라 삭제버튼 분기 처리
        if (isMyPost) {
            binding.deleteButton.visibility = View.VISIBLE
            binding.deleteButton.setOnDuplicatePreventionClickListener {
                showDeleteDialog()
            }
        } else {
            binding.deleteButton.visibility = View.GONE
        }

        // 댓글 등록 버튼
        binding.commentRegisterImageView.setOnDuplicatePreventionClickListener {
            checkComment(binding.commentEditText.text.toString())
        }
    }

    /** 삭제 다이얼로그 띄우기 */
    private fun showDeleteDialog() {
        AlertDialog.Builder(this)
            .setMessage("삭제 하시겠습니까 ?")
            .setPositiveButton("삭제") { dialog, _ ->
                if (::documentId.isInitialized) {
                    viewModel.deletePost(documentId)
                } else {
                    viewModel.deletePost("")
                }
                dialog.dismiss()
                finish()
            }
            .setNegativeButton("취소") { dialog, _ ->
                dialog.dismiss()
            }
            .create()
            .show()
    }

    /** 예외 확인 후 등록 */
    private fun checkComment(commentText: String) {
        if (commentText.isEmpty()) {
            showSnackBar(binding.root, getString(R.string.input_comment))
            return
        }

        binding.commentEditText.setText("")
        currentFocus?.let {
            hideKeyboardAndCursor(this, it)
        }

        // 댓글 등록
        viewModel.registerComment(documentId ,commentText)
    }

    /** RecyclerView 초기화 */
    private fun initRecyclerView() {
        adapter = CommentAdapter(emptyList(), "") {
            viewModel.deleteComment(documentId, it.uid)
            showSnackBar(binding.root, "삭제되었습니다.")
        }
        binding.recyclerView.adapter = adapter
    }

    /** RecyclerView Adapter List Setting */
    private fun setCommentList(commentList: List<CommentModel>) {
        if (commentList.isEmpty()) {
            binding.noCommentTextView.visibility = View.VISIBLE
        } else {
            binding.noCommentTextView.visibility = View.GONE
        }
        adapter.setList(commentList, viewModel.getNickName())
    }
}