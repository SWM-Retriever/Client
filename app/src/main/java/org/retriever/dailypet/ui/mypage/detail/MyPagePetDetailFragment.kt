package org.retriever.dailypet.ui.mypage.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ListPopupWindow
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import coil.load
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentMyPagePetDetailBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.mypage.PetDetailItem
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class MyPagePetDetailFragment : BaseFragment<FragmentMyPagePetDetailBinding>() {

    private lateinit var popUpWindow: ListPopupWindow
    private val popUpWindowItems = listOf(MODIFY, DELETE)

    private val familyId = GlobalApplication.prefs.familyId
    private val jwt = GlobalApplication.prefs.jwt ?: ""

    private val myPageDetailViewModel by activityViewModels<MyPageDetailViewModel>()

    private val petDetailItem by lazy {
        myPageDetailViewModel.clickPetDetailItem
    }

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentMyPagePetDetailBinding {
        return FragmentMyPagePetDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCircularProgress()
        initView()
        buttonClick()
        initPopUp()
        observeDeleteResponse()
    }

    private fun initCircularProgress() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun initView() = with(binding) {
        petDetailItem?.let {
            if (it.profileImageUrl.isNotBlank()) {
                petCircleImage.load(it.profileImageUrl)
            }

            petNameText.text = it.petName
            petKindText.text = if (it.petType == "DOG") "강아지" else "고양이"
            petSexText.text = if (it.gender == "MALE") "수컷" else "암컷"
            petBirthText.text = it.birthDate
            petBreedText.text = it.petKind
            petWeightText.text = getString(R.string.weight_text, it.weight)
            petNeutralText.text = if (it.isNeutered) "완료" else "미실시"
            petRegisterNumText.text = it.registerNumber.ifBlank { "없음" }
        }

    }

    private fun buttonClick() = with(binding) {
        backButton.setOnClickListener {
            root.findNavController().popBackStack()
        }

        petMoreButton.setOnClickListener {
            popUpWindow.show()
        }
    }

    private fun initPopUp() {
        popUpWindow = ListPopupWindow(requireContext(), null, com.airbnb.lottie.R.attr.listPopupWindowStyle)
        popUpWindow.anchorView = binding.petMoreButton

        val adapter = ArrayAdapter(requireContext(), R.layout.item_drop_down, popUpWindowItems)
        popUpWindow.setAdapter(adapter)

        popUpWindow.setOnItemClickListener { _, _, _, id ->
            if (id == 0L) {
                binding.root.findNavController().navigate(R.id.action_myPagePetDetailFragment_to_petModifyFragment)
            } else {
                deletePet()
            }
            popUpWindow.dismiss()
        }

    }

    private fun deletePet() {
        myPageDetailViewModel.deletePet(jwt, familyId, petDetailItem?.petId ?: -2)
    }

    private fun observeDeleteResponse() = with(binding) {
        myPageDetailViewModel.deletePetResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        root.findNavController().popBackStack()
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                        Toast.makeText(requireContext(), response.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    companion object {
        private const val MODIFY = "수정하기"
        private const val DELETE = "삭제하기"
    }

}