package org.retriever.dailypet.ui.diary.adapter

import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemDiaryBinding
import org.retriever.dailypet.model.diary.DiaryItem

class DiaryViewHolder(private val binding : ItemDiaryBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item : DiaryItem){
        binding.writerNickNameText.text = item.authorNickName
        binding.diaryContentText.text = item.diaryText
    }

}