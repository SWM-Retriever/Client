package org.retriever.dailypet.ui.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import coil.load
import org.retriever.dailypet.databinding.ItemMyPageDetailBinding
import org.retriever.dailypet.model.mypage.PetDetailItem

class PetAdapter : ListAdapter<PetDetailItem, PetAdapter.ViewHolder>(diffUtil) {

    var onItemClick: ((PetDetailItem) -> Unit)? = null

    class ViewHolder(val binding: ItemMyPageDetailBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PetDetailItem) {
            binding.profileNickName.text = item.petName
            binding.profilePhotoImageview.load(item.profileImageUrl)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemMyPageDetailBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentList[holder.adapterPosition])
        }
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<PetDetailItem>() {
            override fun areItemsTheSame(oldItem: PetDetailItem, newItem: PetDetailItem): Boolean {
                return oldItem.petId == newItem.petId
            }

            override fun areContentsTheSame(oldItem: PetDetailItem, newItem: PetDetailItem): Boolean {
                return oldItem == newItem
            }

        }
    }

}