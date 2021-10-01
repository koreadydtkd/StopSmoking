package hys.hmonkeyys.stopsmoking.activitys.community.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import hys.hmonkeyys.stopsmoking.R
import hys.hmonkeyys.stopsmoking.data.entity.CommunityModel
import hys.hmonkeyys.stopsmoking.databinding.ItemCommunityBinding
import hys.hmonkeyys.stopsmoking.extension.convertTimeStampToDateFormat
import hys.hmonkeyys.stopsmoking.extension.setOnDuplicatePreventionClickListener
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_FAIL
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_OTHER
import hys.hmonkeyys.stopsmoking.utils.AppShareKey.Companion.NO_SMOKING_SUCCESS

class CommunityAdapter(
    private var communityList: MutableList<CommunityModel> = mutableListOf<CommunityModel>(),
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
                    binding.imageView.setImageResource(R.drawable.ic_connect_without_contact_24)
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

    fun setList(communityList: MutableList<CommunityModel>) {
        this.communityList = communityList
        notifyDataSetChanged()
    }

    fun addList(communityList: MutableList<CommunityModel>) {
        communityList.forEach {
            this.communityList.add(it)
        }
        notifyDataSetChanged()
    }

}