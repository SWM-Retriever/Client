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
import org.retriever.dailypet.databinding.ItemSelectedStatisticsBinding
import org.retriever.dailypet.model.statistics.ContributionCareItem

class SelectedStatisticsAdapter : ListAdapter<ContributionCareItem, SelectedStatisticsAdapter.ViewHolder>(diffUtil) {

    class ViewHolder(val binding: ItemSelectedStatisticsBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(item: ContributionCareItem) {
            val builder = SpannableStringBuilder()

            val firstSpanned = SpannableString("${item.careName}을 ${item.totalCareCount}회 중에\n")
            val secondSpanned = SpannableString(item.myCareCount.toString()).apply {
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
        return ViewHolder(ItemSelectedStatisticsBinding.inflate(LayoutInflater.from(parent.context), parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    companion object {
        val diffUtil = object : DiffUtil.ItemCallback<ContributionCareItem>() {
            override fun areItemsTheSame(oldItem: ContributionCareItem, newItem: ContributionCareItem): Boolean {
                return oldItem == newItem
            }

            override fun areContentsTheSame(oldItem: ContributionCareItem, newItem: ContributionCareItem): Boolean {
                return oldItem == newItem
            }

        }
    }

}