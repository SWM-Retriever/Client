package org.retriever.dailypet.ui.main.diary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemDateBinding
import org.retriever.dailypet.databinding.ItemDiaryBinding
import org.retriever.dailypet.model.diary.Diary

class DiaryAdapter : ListAdapter<Diary, RecyclerView.ViewHolder>(diffUtil) {

    var onItemClick : ((Diary) -> Unit)? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == ViewType.DATE.ordinal) {
            DateViewHolder(ItemDateBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        } else {
            DiaryViewHolder(ItemDiaryBinding.inflate(LayoutInflater.from(parent.context), parent, false))
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        when (currentList[position].viewType) {
            ViewType.DATE.name -> {
                (holder as DateViewHolder).bind(currentList[position])
            }
            ViewType.DIARY.name -> {
                (holder as DiaryViewHolder).bind(currentList[position])
                holder.itemView.setOnClickListener {
                    onItemClick?.invoke(currentList[holder.adapterPosition])
                }
            }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (ViewType.DATE.name == currentList[position].viewType) 0 else 1
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Diary>() {
            override fun areItemsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem.diaryId == newItem.diaryId
            }

            override fun areContentsTheSame(oldItem: Diary, newItem: Diary): Boolean {
                return oldItem == newItem
            }

        }
    }

}

enum class ViewType(name: String) {
    DATE("DATE"),
    DIARY("DIARY"),
}