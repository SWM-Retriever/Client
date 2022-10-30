package org.retriever.dailypet.ui.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.retriever.dailypet.databinding.FragmentDetailStatisticsBinding
import org.retriever.dailypet.model.statistics.CareItem
import org.retriever.dailypet.model.statistics.DetailStaticsItem
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.statistics.adapter.DetailStatisticsAdapter

class DetailStatisticsFragment : BaseFragment<FragmentDetailStatisticsBinding>() {

    private lateinit var detailStatisticsAdapter: DetailStatisticsAdapter

    private val careList = listOf(
        CareItem("아빠", 1f),
        CareItem("엄마", 3f),
        CareItem("오빠", 2f),
        CareItem("언니", 5f),
        CareItem("나", 4f)
    )

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDetailStatisticsBinding {
        return FragmentDetailStatisticsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        buttonClick()
    }

    private fun initAdapter() {
        detailStatisticsAdapter = DetailStatisticsAdapter()

        binding.detailStatisticsRecyclerview.apply {
            adapter = detailStatisticsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        detailStatisticsAdapter.submitList(
            listOf(
                DetailStaticsItem("TITLE", "산책", emptyList()),
                DetailStaticsItem("CHART", "산책", careList),
                DetailStaticsItem("TITLE", "목욕", emptyList()),
                DetailStaticsItem("CHART", "목욕", careList),
                DetailStaticsItem("TITLE", "양치질", emptyList()),
                DetailStaticsItem("CHART", "양치질", careList),
            )
        )
    }

    private fun buttonClick() {
        binding.backButton.setOnClickListener {
            binding.root.findNavController().popBackStack()
        }
    }

}