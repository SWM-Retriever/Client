package org.retriever.dailypet.ui.home.statistics.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemUnselectedStatisticsBinding
import org.retriever.dailypet.model.statistics.ContributionItem

class UnSelectedStatisticsAdapter : ListAdapter<ContributionItem, UnSelectedStatisticsAdapter.ViewHolder>(diffUtil) {

    var onItemClick: ((Int) -> Unit)? = null

    class ViewHolder(val binding: ItemUnselectedStatisticsBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(item: ContributionItem) {
            binding.groupPercentageChart.setProgress(item.contributionPercent, true)
            binding.groupNameText.text = item.familyRoleName
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemUnselectedStatisticsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(position)
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ContributionItem>() {
            override fun areItemsTheSame(oldItem: ContributionItem, newItem: ContributionItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ContributionItem, newItem: ContributionItem): Boolean {
                return oldItem == newItem
            }

        }
    }

}