package org.retriever.dailypet.ui.home.statistics

import android.graphics.Color
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.SpannableStringBuilder
import android.text.style.ForegroundColorSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentStatisticsBinding
import org.retriever.dailypet.model.statistics.GroupItem
import org.retriever.dailypet.model.statistics.WinnerItem
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.home.statistics.adapter.GroupAdapter
import org.retriever.dailypet.ui.home.statistics.adapter.WinnerAdapter

class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>() {

    private lateinit var winnerAdapter: WinnerAdapter
    private lateinit var groupAdapter: GroupAdapter

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentStatisticsBinding {
        return FragmentStatisticsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initWinnerView()
        initAdapter()
        buttonClick()
    }

    private fun initWinnerView() {
        initTitleTextView()

        binding.winnerPercentageChart.setProgress(60f, true)
        binding.winnerPercentageChart.isSaveEnabled = false
    }

    private fun initTitleTextView() {
        val builder = SpannableStringBuilder()

        val firstSpanned = SpannableString("1등인 ")
        val secondSpanned = SpannableString("엄마").apply {
            setSpan(
                ForegroundColorSpan(Color.parseColor("#EB6E7A")),
                0,
                2,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        val thirdSpanned = SpannableString("가 얼마나 기여했을까요?")

        builder.append(firstSpanned).append(secondSpanned).append(thirdSpanned)

        binding.whoIsTheWinnerText.text = builder
    }

    private fun initAdapter() = with(binding) {
        winnerAdapter = WinnerAdapter()

        winnerRecyclerview.apply {
            adapter = winnerAdapter
            layoutManager = GridLayoutManager(requireContext(), MAX_COLUMNS)
        }

        winnerAdapter.submitList(
            listOf(
                WinnerItem("산책", 3, 2),
                WinnerItem("목욕", 4, 2),
                WinnerItem("양치질", 5, 2),
                WinnerItem("목욕", 4, 2),
                WinnerItem("양치질", 5, 2),
            )
        )

        groupAdapter = GroupAdapter()

        statisticsRecyclerview.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        groupAdapter.submitList(
            listOf(
                GroupItem("아들", 15f),
                GroupItem("아빠", 25f),
                GroupItem("형", 20f),
            )
        )
    }

    private fun buttonClick() = with(binding){
        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }

        statisticsDetailButton.setOnClickListener {
            root.findNavController().navigate(R.id.action_statisticsFragment_to_detailStatisticsFragment)
        }
    }

    companion object {
        private const val MAX_COLUMNS = 2
    }

}