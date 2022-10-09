package org.retriever.dailypet.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.BottomSheetAddProfileBinding

@AndroidEntryPoint
class AddProfileBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetAddProfileBinding
    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private var invitationCode = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomSheetAddProfileBinding.inflate(inflater, container, false)

        invitationCode = arguments?.getString("invitationCode") ?: ""

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initData()
        buttonClick()
    }

    private fun initData() = with(binding){
        petViewModel.getPetBreedList(petType, jwt)

        petViewModel.petBreedList.observe(viewLifecycleOwner){response ->
            when(response){
                is Resource.Loading ->{
                    showProgressCircular(progressCircular)
                }
                is Resource.Success ->{
                    hideProgressCircular(progressCircular)
                    breedAdapter.modifyList(response.data?.petKindList ?: listOf())
                    val nextIntent = Intent(requireContext(), MainActivity::class.java)
                   nextIntent.putExtra("FamilyName", groupName)
                   startActivity(nextIntent) // 가족 구성원 등록 페이지로 이동
                }
                is Resource.Error ->{
                    hideProgressCircular(progressCircular)
                }
            }
        }
    }

    private fun buttonClick() = with(binding) {
        groupNameCheckText.setOnClickListener {
            submitCheck()
        }

        enterGroupButton.setOnClickListener {
            enterGroup()
        }

        exitButton.setOnClickListener {
            dismiss()
        }
    }

    private fun submitCheck() {

    }

    private fun enterGroup() {

    }
}