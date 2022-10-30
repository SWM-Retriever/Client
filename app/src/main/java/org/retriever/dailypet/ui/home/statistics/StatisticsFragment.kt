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
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentStatisticsBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.statistics.ContributionItem
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.home.statistics.adapter.SelectedStatisticsAdapter
import org.retriever.dailypet.ui.home.statistics.adapter.UnSelectedStatisticsAdapter
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular
import java.util.*

class StatisticsFragment : BaseFragment<FragmentStatisticsBinding>() {

    private val statisticsViewModel by activityViewModels<StatisticsViewModel>()

    private val args: StatisticsFragmentArgs by navArgs()

    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private val familyId = GlobalApplication.prefs.familyId
    private var petId = -1
    private var petName = ""

    private lateinit var selectedStatisticsAdapter: SelectedStatisticsAdapter
    private lateinit var unSelectedStatisticsAdapter: UnSelectedStatisticsAdapter

    private lateinit var list: List<ContributionItem>

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentStatisticsBinding {
        return FragmentStatisticsBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getArgsItem()
        callApi()
        observeStatisticsResponse()
        initAdapter()
        buttonClick()
    }

    private fun getArgsItem() {
        petId = args.petId
        petName = args.petName
    }

    private fun callApi() {
        //TODO 날짜 로직 생각해야해
        statisticsViewModel.getContributionDetailList(familyId, petId, "2022-10-23", "2022-10-30", jwt)
    }

    private fun observeStatisticsResponse() {
        statisticsViewModel.contributionResponse.observe(viewLifecycleOwner) { response ->
            when(response){
                is Resource.Loading ->{
                    showProgressCircular(binding.progressCircular)
                }
                is Resource.Success ->{
                    hideProgressCircular(binding.progressCircular)

                    list = response.data?.contributionDetailList ?: emptyList()
                    initWinnerView(list.getOrNull(0))
                    selectedStatisticsAdapter.submitList(list.getOrNull(0)?.careInfoDetailList)

                    if (list.size >= 2) {
                        val subList = list.subList(1, list.size)
                        unSelectedStatisticsAdapter.submitList(subList.toMutableList())
                    }
                }
                is Resource.Error->{
                    hideProgressCircular(binding.progressCircular)
                }
            }
        }
    }

    private fun initWinnerView(item: ContributionItem?) {
        item?.let {
            initTitleTextView(it.rank, it.familyRoleName)

            binding.winnerPercentageChart.setProgress(it.contributionPercent, true)
        }
    }

    private fun initTitleTextView(rank: Int, name: String) {
        val builder = SpannableStringBuilder()

        val firstSpanned = SpannableString("${rank}등인 ")
        val secondSpanned = SpannableString(name).apply {
            setSpan(
                ForegroundColorSpan(Color.parseColor("#EB6E7A")),
                0,
                name.length,
                Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
            )
        }
        val thirdSpanned = SpannableString("가 얼마나 기여했을까요?")

        builder.append(firstSpanned).append(secondSpanned).append(thirdSpanned)

        binding.whoIsTheWinnerText.text = builder
    }

    private fun initAdapter() = with(binding) {
        selectedStatisticsAdapter = SelectedStatisticsAdapter()

        winnerRecyclerview.apply {
            adapter = selectedStatisticsAdapter
            layoutManager = GridLayoutManager(requireContext(), MAX_COLUMNS)
        }

        unSelectedStatisticsAdapter = UnSelectedStatisticsAdapter()

        statisticsRecyclerview.apply {
            adapter = unSelectedStatisticsAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        unSelectedStatisticsAdapter.onItemClick = {
            Collections.swap(list, 0, it + 1)
            statisticsViewModel.changeContributionResponse(list)
        }
    }

    private fun buttonClick() = with(binding) {
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