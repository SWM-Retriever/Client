package org.retriever.dailypet.ui.home.statistics.adapter

import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemDetailStatisticsTitleBinding
import org.retriever.dailypet.model.statistics.DetailContributionItem

class DetailStatisticsTitleViewHolder(private val binding: ItemDetailStatisticsTitleBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DetailContributionItem) {
        binding.titleText.text = item.careName
    }

}