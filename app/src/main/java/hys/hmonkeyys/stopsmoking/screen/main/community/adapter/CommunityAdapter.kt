package hys.hmonkeyys.stopsmoking.screen.main.community.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.model.CommunityModel
import hys.hmonkeyys.stopsmoking.databinding.ItemCommunityBinding
import hys.hmonkeyys.stopsmoking.extension.convertTimeStampToDateFormat
import hys.hmonkeyys.stopsmoking.extension.setOnDuplicatePreventionClickListener
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_FAIL
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_OTHER
import hys.hmonkeyys.stopsmoking.utils.Constant.NO_SMOKING_SUCCESS

class CommunityAdapter(
    private var communityList: MutableList<CommunityModel> = mutableListOf(),
    val onCommunityItemClickListener: (CommunityModel) -> Unit
): RecyclerView.Adapter<CommunityAdapter.ViewHolder>() {

    inner class ViewHolder(private val binding: ItemCommunityBinding): RecyclerView.ViewHolder(binding.root) {
        fun bind(community: CommunityModel) {
            when(community.category) {
                NO_SMOKING_SUCCESS -> {
                    binding.imageView.setImageResource(R.drawable.ic_no_smoking_success)
                }
                NO_SMOKING_FAIL -> {
                    binding.imageView.setImageResource(R.drawable.ic_no_smoking_fail)
                }
                NO_SMOKING_OTHER -> {
                    binding.imageView.setImageResource(R.drawable.ic_connect)
                }
            }

            binding.titleTextView.text = community.title
            binding.writerTextView.text = community.writer
            binding.dateTextView.text = community.date.convertTimeStampToDateFormat()
            binding.viewsCountTextView.text = "${community.viewsCount}"

            binding.root.setOnDuplicatePreventionClickListener {
                onCommunityItemClickListener(community)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemCommunityBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(communityList[position])
    }

    override fun getItemCount(): Int = communityList.size

    @SuppressLint("NotifyDataSetChanged")
    fun setList(communityList: List<CommunityModel>) {
        this.communityList = communityList.toMutableList()
        notifyDataSetChanged()
    }

    @SuppressLint("NotifyDataSetChanged")
    fun addList(communityList: List<CommunityModel>) {
        this.communityList.addAll(communityList.toMutableList())
        notifyDataSetChanged()
    }

}