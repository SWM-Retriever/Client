package org.retriever.dailypet.ui.main.diary.adapter

import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemDateBinding
import org.retriever.dailypet.model.diary.DiaryItem

class DateViewHolder(private val binding : ItemDateBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item : DiaryItem){
        binding.dateText.text = item.date
    }

}