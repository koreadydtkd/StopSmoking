package hys.hmonkeyys.stopsmoking.ui.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hys.hmonkeyys.stopsmoking.data.entity.CommentModel
import hys.hmonkeyys.stopsmoking.databinding.ItemCommentBinding
import hys.hmonkeyys.stopsmoking.extension.convertTimeStampToDateFormat
import hys.hmonkeyys.stopsmoking.extension.setOnDuplicatePreventionClickListener

class CommentAdapter(
    private var commentList: List<CommentModel>,
    private var nickName: String?,
    val onItemDeleteListener: (CommentModel) -> Unit
) : RecyclerView.Adapter<CommentAdapter.CommentViewHolder>() {

    inner class CommentViewHolder(private val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(comment: CommentModel) {
            binding.writerTextView.text = comment.writer
            binding.dateTextView.text = comment.date.convertTimeStampToDateFormat()
            binding.commentTextView.text = comment.comment

            if(comment.writer == nickName) {
                binding.deleteButton.visibility = View.VISIBLE
                binding.deleteButton.setOnDuplicatePreventionClickListener {
                    onItemDeleteListener(comment)
                }
            } else {
                binding.deleteButton.visibility = View.GONE
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CommentViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CommentViewHolder, position: Int) {
        holder.bind(commentList[position])
    }

    override fun getItemCount(): Int = commentList.size

    fun setList(commentList: List<CommentModel>, nickName: String?) {
        this.commentList = commentList
        this.nickName = nickName
        notifyDataSetChanged()
    }
}