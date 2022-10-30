package org.retriever.dailypet.ui.home.statistics

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.databinding.FragmentDetailStatisticsBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.home.statistics.adapter.DetailStatisticsAdapter
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class DetailStatisticsFragment : BaseFragment<FragmentDetailStatisticsBinding>() {

    private val statisticsViewModel by activityViewModels<StatisticsViewModel>()

    private val args: StatisticsFragmentArgs by navArgs()

    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private val familyId = GlobalApplication.prefs.familyId
    private var petId = -1
    private var petName = ""

    private lateinit var detailStatisticsAdapter: DetailStatisticsAdapter

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDetailStatisticsBinding {
        return FragmentDetailStatisticsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArgsItem()
        initAdapter()
        buttonClick()
        callApi()
        observeDetailContributionResponse()

    }

    private fun getArgsItem() {
        petId = args.petId
        petName = args.petName
    }

    private fun initAdapter() {
        detailStatisticsAdapter = DetailStatisticsAdapter()

        binding.detailStatisticsRecyclerview.apply {
            adapter = detailStatisticsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun buttonClick() {
        binding.backButton.setOnClickListener {
            binding.root.findNavController().popBackStack()
        }
    }

    private fun callApi() {
        statisticsViewModel.getGraphList(familyId, petId, jwt)
    }

    private fun observeDetailContributionResponse() = with(binding) {
        statisticsViewModel.detailContributionResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    detailStatisticsAdapter.submitList(response.data?.graphList)
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                }
            }
        }
    }

}