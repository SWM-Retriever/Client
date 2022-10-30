package org.retriever.dailypet.ui.statistics.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemGroupStatisticsBinding
import org.retriever.dailypet.model.statistics.GroupItem

class GroupAdapter : ListAdapter<GroupItem, GroupAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(val binding: ItemGroupStatisticsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: GroupItem) {
            binding.groupPercentageChart.setProgress(item.percent, true)
            binding.groupNameText.text = item.groupName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemGroupStatisticsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<GroupItem>() {
            override fun areItemsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
                return oldItem.groupName == newItem.groupName
            }

            override fun areContentsTheSame(oldItem: GroupItem, newItem: GroupItem): Boolean {
                return oldItem == newItem
            }

        }
    }

}