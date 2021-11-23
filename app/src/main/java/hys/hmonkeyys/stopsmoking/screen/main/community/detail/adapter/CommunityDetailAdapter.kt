package hys.hmonkeyys.stopsmoking.screen.main.community.detail.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import hys.hmonkeyys.stopsmoking.model.CommentModel
import hys.hmonkeyys.stopsmoking.databinding.ItemCommentBinding
import hys.hmonkeyys.stopsmoking.extension.convertTimeStampToDateFormat
import hys.hmonkeyys.stopsmoking.extension.setOnDuplicatePreventionClickListener

class CommunityDetailAdapter(
    private var commentList: List<CommentModel>,
    private var nickName: String?,
    val onItemDeleteListener: (CommentModel) -> Unit
) : RecyclerView.Adapter<CommunityDetailAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CommentModel) {
            binding.writerTextView.text = comment.writer
            binding.dateTextView.text = comment.date.convertTimeStampToDateFormat()
            binding.commentTextView.text = comment.comment

            binding.deleteButton.isVisible = comment.writer == nickName

            binding.deleteButton.setOnDuplicatePreventionClickListener {
                onItemDeleteListener(comment)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        return CommentViewHolder(ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentList[position])
    }

    override fun getItemCount(): Int = commentList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setList(commentList: List<CommentModel>, nickName: String?) {
        this.commentList = commentList
        this.nickName = nickName
        notifyDataSetChanged()
    }
}