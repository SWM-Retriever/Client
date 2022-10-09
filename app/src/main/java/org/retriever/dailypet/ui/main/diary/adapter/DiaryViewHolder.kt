package org.retriever.dailypet.ui.main.diary.adapter

import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemDiaryBinding
import org.retriever.dailypet.model.diary.Diary

class DiaryViewHolder(private val binding : ItemDiaryBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item : Diary){
        binding.viewtype.text = item.viewType
    }

}