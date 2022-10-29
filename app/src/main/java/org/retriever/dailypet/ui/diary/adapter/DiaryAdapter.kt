package org.retriever.dailypet.ui.diary.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemDateBinding
import org.retriever.dailypet.databinding.ItemDiaryBinding
import org.retriever.dailypet.model.diary.DiaryItem

class DiaryAdapter : ListAdapter<DiaryItem, RecyclerView.ViewHolder>(diffUtil) {

    var onItemClick : ((DiaryItem) -> Unit)? = null

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
        val diffUtil = object : DiffUtil.ItemCallback<DiaryItem>() {
            override fun areItemsTheSame(oldItem: DiaryItem, newItem: DiaryItem): Boolean {
                return oldItem.diaryId == newItem.diaryId
            }

            override fun areContentsTheSame(oldItem: DiaryItem, newItem: DiaryItem): Boolean {
                return oldItem == newItem
            }

        }
    }

}

enum class ViewType(name: String) {
    DATE("DATE"),
    DIARY("DIARY"),
}