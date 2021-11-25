package hys.hmonkeyys.stopsmoking.screen.main.community.detail

import androidx.appcompat.app.AlertDialog
import androidx.core.view.isGone
import androidx.core.view.isInvisible
import androidx.core.view.isVisible
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.screen.BaseActivity
import hys.hmonkeyys.stopsmoking.screen.main.community.detail.adapter.CommunityDetailAdapter
import hys.hmonkeyys.stopsmoking.model.CommentModel
import hys.hmonkeyys.stopsmoking.model.CommunityModel
import hys.hmonkeyys.stopsmoking.databinding.ActivityCommunityDetailBinding
import hys.hmonkeyys.stopsmoking.extension.convertTimeStampToDateFormat
import hys.hmonkeyys.stopsmoking.extension.setOnDuplicatePreventionClickListener
import hys.hmonkeyys.stopsmoking.screen.main.community.CommunityActivity.Companion.COMMUNITY_DETAIL_KEY
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_FAIL
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_SUCCESS
import hys.hmonkeyys.stopsmoking.utils.Utility.changePartialTextColor
import hys.hmonkeyys.stopsmoking.utils.Utility.hideKeyboardAndCursor
import hys.hmonkeyys.stopsmoking.utils.Utility.snackBar
import org.koin.androidx.viewmodel.ext.android.viewModel

internal class CommunityDetailActivity : BaseActivity<CommunityDetailViewModel, ActivityCommunityDetailBinding>() {

    override val viewModel: CommunityDetailViewModel by viewModel()
    override fun getViewBinding(): ActivityCommunityDetailBinding = ActivityCommunityDetailBinding.inflate(layoutInflater)

    private lateinit var adapter: CommunityDetailAdapter

    private var isMyPost = false
    private lateinit var documentId: String

    /** 뷰 초기화 */
    override fun initViews() = with(binding) {
        // 전달받은 Intent 초기화
        getIntentAndSetView()

        // cancel button
        viewCancel.setOnDuplicatePreventionClickListener {
            finish()
        }

        // 자신이 게시한 글인지에 따라 삭제버튼 분기 처리
        deleteButton.setOnDuplicatePreventionClickListener {
            showDeleteDialog()
        }

        // 댓글 등록 버튼
        commentRegisterImageView.setOnDuplicatePreventionClickListener {
            checkComment(commentEditText.text.toString())
        }

        // 리사이클러뷰 초기화
        adapter = CommunityDetailAdapter(emptyList(), "") {
            viewModel.deleteComment(documentId, it.uid)
        }
        recyclerView.adapter = adapter
    }

    /** 전달 받은 Intent 데이터 뷰에 셋팅 */
    private fun getIntentAndSetView() = with(binding) {
        intent.getParcelableExtra<CommunityModel>(COMMUNITY_DETAIL_KEY)?.let { communityModel ->
            isMyPost = viewModel.isMyPost(communityModel.writer)
            documentId = communityModel.uid
            deleteButton.isVisible = isMyPost

            writerTextView.text = if (isMyPost) {
                getString(R.string.from_writer_me)
            } else {
                changePartialTextColor(
                    getString(R.string.from_writer, communityModel.writer),
                    getColor(R.color.black),
                    0,
                    communityModel.writer.length
                )
            }

            titleTextView.text = communityModel.title
            dateTextView.text = communityModel.date.convertTimeStampToDateFormat()
            contentsTextView.text = communityModel.contents
            viewCountTextView.text = "${communityModel.viewsCount}"
            categoryTextView.text = when (communityModel.category) {
                NO_SMOKING_SUCCESS -> "금연 성공담"
                NO_SMOKING_FAIL -> "금연 실패담"
                else -> "잡담"
            }

            // 조회수 추가
            viewModel.addViewsCount(communityModel.uid)
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
            snackBar(binding.root, getString(R.string.input_comment))
            return
        }

        binding.commentEditText.setText("")
        currentFocus?.let { hideKeyboardAndCursor(this, it) }

        // 댓글 등록
        viewModel.registerComment(documentId ,commentText)
    }

    override fun observeData() {
        viewModel.communityDetailLiveData.observe(this) {
            when (it) {
                is CommunityDetailState.Initialized -> viewModel.fetchComments(documentId)

                is CommunityDetailState.Loading -> binding.progressBar.isVisible = true

                is CommunityDetailState.FetChCommentList -> setCommentList(it.commentList)

                is CommunityDetailState.CommentRegisterFailure -> snackBar(binding.root, getString(R.string.comment_failure))

                is CommunityDetailState.CommentRegisterSuccess -> viewModel.fetchComments(documentId)

                is CommunityDetailState.CommentDeleteFailure -> snackBar(binding.root, getString(R.string.comment_delete_failure))

                is CommunityDetailState.CommentDeleteSuccess -> commentDeleteSuccess()
            }
        }
    }

    /** RecyclerView Adapter List Setting */
    private fun setCommentList(commentList: List<CommentModel>) {
        binding.noCommentTextView.isVisible = commentList.isEmpty()
        binding.recyclerView.isInvisible = commentList.isEmpty()
        binding.progressBar.isGone = true

        adapter.setList(commentList, viewModel.getNickName())
    }

    /** fetch Comments after Show snackBar */
    private fun commentDeleteSuccess() {
        snackBar(binding.root, getString(R.string.comment_delete_success))
        viewModel.fetchComments(documentId)
    }
}