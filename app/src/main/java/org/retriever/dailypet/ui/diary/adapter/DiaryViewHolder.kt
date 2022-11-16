package org.retriever.dailypet.ui.diary.adapter

import android.opengl.Visibility
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.retriever.dailypet.databinding.ItemDiaryBinding
import org.retriever.dailypet.model.diary.DiaryItem

class DiaryViewHolder(private val binding : ItemDiaryBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item : DiaryItem) = with(binding){
        writerNickNameText.text = item.authorNickName
        diaryContentText.text = item.diaryText
        if(item.authorImageUrl?.isNotEmpty() == true){
            writerCircleImage.load(item.authorImageUrl)
        }
        if(!item.diaryImageUrl.isNullOrEmpty()){
            diaryImage.load(item.diaryImageUrl )
        } else{
            diaryImageCardView.visibility = View.GONE
        }
    }

}