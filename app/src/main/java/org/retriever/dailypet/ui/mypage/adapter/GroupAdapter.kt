package org.retriever.dailypet.ui.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemFamilyBinding
import org.retriever.dailypet.model.mypage.GroupDetailItem

class GroupAdapter : ListAdapter<GroupDetailItem, GroupAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(val binding: ItemFamilyBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupDetailItem) {
            binding.profileNickName.text = item.groupRoleName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemFamilyBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<GroupDetailItem>() {
            override fun areItemsTheSame(oldItem: GroupDetailItem, newItem: GroupDetailItem): Boolean {
                return oldItem.groupRoleName == newItem.groupRoleName
            }

            override fun areContentsTheSame(oldItem: GroupDetailItem, newItem: GroupDetailItem): Boolean {
                return oldItem == newItem
            }

        }
    }

}