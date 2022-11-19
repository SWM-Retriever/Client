package org.retriever.dailypet.ui.signup.findgroup

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.retriever.dailypet.databinding.ItemMyPageDetailBinding
import org.retriever.dailypet.model.signup.group.FindGroupMember

class FindGroupAdapter : ListAdapter<FindGroupMember, FindGroupAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(val binding: ItemMyPageDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FindGroupMember) {
            binding.profileNickName.text = item.nickName
            val imageUrl = item.profileImageUrl
            if(imageUrl.isNotEmpty()){
                binding.profilePhotoImageview.load(imageUrl)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMyPageDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<FindGroupMember>() {
            override fun areItemsTheSame(oldItem: FindGroupMember, newItem: FindGroupMember): Boolean {
                return oldItem.memberId == newItem.memberId
            }
            override fun areContentsTheSame(oldItem: FindGroupMember, newItem: FindGroupMember): Boolean {
                return oldItem == newItem
            }
        }
    }

}