package org.retriever.dailypet.ui.signup.pet

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemBreedBinding
import org.retriever.dailypet.model.signup.pet.Breed

class BreedAdapter : ListAdapter<Breed, BreedAdapter.ViewHolder>(diffUtil) {

    var onItemClick: ((Breed) -> Unit)? = null

    private var unfilteredList = listOf<Breed>()

    inner class ViewHolder(val binding: ItemBreedBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: Breed) {
            binding.breedTitle.text = item.petKindName
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemBreedBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
        holder.itemView.setOnClickListener {
            onItemClick?.invoke(currentList[holder.adapterPosition])
        }
    }

    fun modifyList(list : List<Breed>){
        unfilteredList = list
        submitList(list)
    }

    fun filter(query : CharSequence?){
        val list = mutableListOf<Breed>()

        if(!query.isNullOrEmpty()){
            unfilteredList.forEach { breed ->
                if(breed.petKindName.contains(query)){
                    list.add(breed)
                }
            }
        }else{
            list.addAll(unfilteredList)
        }

        submitList(list)
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<Breed>() {
            override fun areItemsTheSame(oldItem: Breed, newItem: Breed): Boolean {
                return oldItem.petKindId == newItem.petKindId
            }

            override fun areContentsTheSame(oldItem: Breed, newItem: Breed): Boolean {
                return oldItem == newItem
            }

        }
    }

}