package org.retriever.dailypet.ui.signup.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.ItemFamilyBinding

class FindGroupAdapter(
    private val familyList: List<String>
) : RecyclerView.Adapter<FindGroupAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemFamilyBinding) : RecyclerView.ViewHolder(binding.root) {
        val nickName = binding.profileNickName
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val view = inflater.inflate(R.layout.item_family, parent, false)
        return ViewHolder(ItemFamilyBinding.bind(view))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = familyList[position]
        holder.nickName.text = item
    }

    override fun getItemCount(): Int = familyList.size

}