package org.retriever.dailypet.ui.mypage

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.databinding.FragmentMyPagePetBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.mypage.adapter.PetAdapter
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class MyPagePetFragment : BaseFragment<FragmentMyPagePetBinding>() {

    private val myPageViewModel by activityViewModels<MyPageViewModel>()

    private lateinit var petAdapter: PetAdapter

    private val familyId = GlobalApplication.prefs.familyId
    private val jwt = GlobalApplication.prefs.jwt ?: ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyPagePetBinding {
        return FragmentMyPagePetBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCircularProgress()
        initAdapter()
        observePetDetailResponse()
    }

    private fun initCircularProgress() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initAdapter() {
        petAdapter = PetAdapter()

        binding.petRecyclerview.apply {
            adapter = petAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        petAdapter.onItemClick = { petDetailItem ->
            val action = MyPagePetFragmentDirections.actionMyPagePetFragmentToMyPagePetDetailFragment(petDetailItem)
            binding.root.findNavController().navigate(action)
        }
    }

    private fun observePetDetailResponse() = with(binding) {
        myPageViewModel.getPetList(familyId, jwt)

        myPageViewModel.petDetailResponse.observe(viewLifecycleOwner) { response ->
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