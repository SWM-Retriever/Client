package org.retriever.dailypet.ui.statistics.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemDetailStatisticsChartBinding
import org.retriever.dailypet.databinding.ItemDetailStatisticsTitleBinding
import org.retriever.dailypet.model.statistics.DetailStaticsItem

class DetailStatisticsAdapter : ListAdapter<DetailStaticsItem, RecyclerView.ViewHolder>(diffUtil) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ViewType.TITLE.ordinal) {
            DetailStatisticsTitleViewHolder(ItemDetailStatisticsTitleBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            DetailStatisticsChartViewHolder(ItemDetailStatisticsChartBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (currentList[position].viewType) {
            ViewType.TITLE.name -> {
                (holder as DetailStatisticsTitleViewHolder).bind(currentList[position])
            }
            ViewType.CHART.name -> {
                (holder as DetailStatisticsChartViewHolder).bind(currentList[position])
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (ViewType.TITLE.name == currentList[position].viewType) 0 else 1
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<DetailStaticsItem>() {
            override fun areItemsTheSame(oldItem: DetailStaticsItem, newItem: DetailStaticsItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: DetailStaticsItem, newItem: DetailStaticsItem): Boolean {
                return oldItem == newItem
            }

        }
    }

}

enum class ViewType(name: String) {
    TITLE("TITLE"),
    CHART("CHART"),
}