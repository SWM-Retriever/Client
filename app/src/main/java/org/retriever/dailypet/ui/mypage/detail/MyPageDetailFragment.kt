package org.retriever.dailypet.ui.mypage.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.databinding.FragmentMyPageDetailBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.mypage.GroupDetailItem
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.mypage.adapter.GroupAdapter
import org.retriever.dailypet.ui.mypage.adapter.PetAdapter
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class MyPageDetailFragment : BaseFragment<FragmentMyPageDetailBinding>() {

    private val myPageDetailViewModel by activityViewModels<MyPageDetailViewModel>()

    private lateinit var groupAdapter: GroupAdapter
    private lateinit var petAdapter: PetAdapter

    private val familyId = GlobalApplication.prefs.familyId
    private val jwt = GlobalApplication.prefs.jwt ?: ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyPageDetailBinding {
        return FragmentMyPageDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        callApi()
        initCircularProgress()
        initGroupAdapter()
        initPetAdapter()
        buttonClick()
        observePetDetailResponse()
    }

    private fun callApi() {
        myPageDetailViewModel.getPetList(familyId, jwt)
    }

    private fun initCircularProgress() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initGroupAdapter() {
        groupAdapter = GroupAdapter()

        binding.groupMemberRecyclerview.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        groupAdapter.submitList(
            listOf(
                GroupDetailItem("abc", "김시진"),
                GroupDetailItem("abc", "김시훈"),
                GroupDetailItem("abc", "김시존"),
            )
        )
    }

    private fun initPetAdapter() {
        petAdapter = PetAdapter()

        binding.petMemberRecyclerview.apply {
            adapter = petAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        petAdapter.onItemClick = { petDetailItem ->
            val action = MyPageDetailFragmentDirections.actionMyPageDetailFragmentToMyPagePetDetailFragment(petDetailItem)
            binding.root.findNavController().navigate(action)
        }
    }

    private fun buttonClick() = with(binding) {
        petAddButton.setOnClickListener {
            val action = MyPageDetailFragmentDirections.actionMyPageDetailFragmentToCreatePetFragment2(true)
            binding.root.findNavController().navigate(action)
        }

        exitButton.setOnClickListener {
            (activity as MyPageDetailActivity).finish()
        }
    }

    private fun observePetDetailResponse() = with(binding) {
        myPageDetailViewModel.petDetailResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    petAdapter.submitList(response.data?.petInfoDetailList)
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                }
            }
        }
    }

}