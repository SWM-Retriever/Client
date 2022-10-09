package org.retriever.dailypet.ui.main.diary.adapter

import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemDateBinding
import org.retriever.dailypet.model.diary.Diary

class DateViewHolder(private val binding : ItemDateBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item : Diary){
        binding.viewtype.text = item.viewType
    }

}