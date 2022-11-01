package org.retriever.dailypet.ui.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.ItemMyPageDetailBinding
import org.retriever.dailypet.model.mypage.GroupMember

class GroupAdapter : ListAdapter<GroupMember, GroupAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(val binding: ItemMyPageDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupMember) {
            binding.profileNickName.text = item.familyRoleName
            binding.profilePhotoImageview.load(item.profileImageUrl)

        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMyPageDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<GroupMember>() {
            override fun areItemsTheSame(oldItem: GroupMember, newItem: GroupMember): Boolean {
                return oldItem.memberId == newItem.memberId
            }

            override fun areContentsTheSame(oldItem: GroupMember, newItem: GroupMember): Boolean {
                return oldItem == newItem
            }

        }
    }

}