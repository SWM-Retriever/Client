package org.retriever.dailypet.ui.mypage.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemPetBinding
import org.retriever.dailypet.model.mypage.PetDetailItem

class PetAdapter : ListAdapter<PetDetailItem, PetAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(val binding: ItemPetBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: PetDetailItem) {
            binding.petNameText.text = item.petName
            binding.petBirthText.text = item.birthDate
            binding.petBreedText.text = item.petKind
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemPetBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
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