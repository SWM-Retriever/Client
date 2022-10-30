package org.retriever.dailypet.ui.home.statistics.adapter

import android.graphics.Color
import android.graphics.Typeface
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.text.style.RelativeSizeSpan
import android.text.style.StyleSpan
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.ItemWinnerBinding
import org.retriever.dailypet.model.statistics.WinnerItem

class WinnerAdapter : ListAdapter<WinnerItem, WinnerAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(val binding: ItemWinnerBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: WinnerItem) {
            val builder = SpannableStringBuilder()

            val firstSpanned = SpannableString("${item.itemName}을 ${item.totalCount}회 중에\n")
            val secondSpanned = SpannableString(item.itemCount.toString()).apply {
                setSpan(
                    ForegroundColorSpan(Color.parseColor("#EB6E7A")),
                    0,
                    1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                setSpan(
                    StyleSpan(Typeface.BOLD),
                    0,
                    1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )

                setSpan(
                    RelativeSizeSpan(1.5f),
                    0,
                    1,
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
                )
            }
            val thirdSpanned = SpannableString("회")

            builder.append(firstSpanned).append(secondSpanned).append(thirdSpanned)

            binding.winnerText.text = builder
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemWinnerBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<WinnerItem>() {
            override fun areItemsTheSame(oldItem: WinnerItem, newItem: WinnerItem): Boolean {
                return oldItem.itemName == newItem.itemName
            }

            override fun areContentsTheSame(oldItem: WinnerItem, newItem: WinnerItem): Boolean {
                return oldItem == newItem
            }

        }
    }

}