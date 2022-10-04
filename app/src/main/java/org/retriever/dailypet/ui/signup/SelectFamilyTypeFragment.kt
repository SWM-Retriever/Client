package org.retriever.dailypet.ui.signup

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.core.content.ContextCompat
import androidx.navigation.findNavController
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentSelectFamilyTypeBinding
import org.retriever.dailypet.ui.base.BaseFragment

class SelectFamilyTypeFragment : BaseFragment<FragmentSelectFamilyTypeBinding>() {

    private lateinit var onBackCallBack: OnBackPressedCallback

    private var alone = false
    private var group = false

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentSelectFamilyTypeBinding {
        return FragmentSelectFamilyTypeBinding.inflate(inflater, container, false)
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        initCallBack()
    }

    private fun initCallBack(){
        onBackCallBack = object : OnBackPressedCallback(true){
            override fun handleOnBackPressed() {
                activity?.finish()
            }
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClick()
    }

    private fun buttonClick() = with(binding){

        btnSelectAlone.setOnClickListener{
            alone = true
            group = false
            btnSelectAlone.isSelected = true
            btnSelectGroup.isSelected = false
            imgAlone.setColorFilter(ContextCompat.getColor(requireContext(), R.color.main_blue))
            imgGroup.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            btnSelectType.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
            btnSelectType.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }

        binding.btnSelectGroup.setOnClickListener{
            alone = false
            group = true
            btnSelectAlone.isSelected = false
            btnSelectGroup.isSelected = true
            imgAlone.setColorFilter(ContextCompat.getColor(requireContext(), R.color.grey))
            imgGroup.setColorFilter(ContextCompat.getColor(requireContext(), R.color.main_blue))
            btnSelectType.background = ContextCompat.getDrawable(requireContext(), R.drawable.blue_button)
            btnSelectType.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        /* 선택 */
        btnSelectType.setOnClickListener{
            if(alone){
                //TODO 가족생성 로직 처리하자
                root.findNavController().navigate(R.id.action_selectFamilyTypeFragment_to_createPetFragment)
            }
            else if(group){
                root.findNavController().navigate(R.id.action_selectFamilyTypeFragment_to_familyEntranceFragment)
            }
            else{
                Toast.makeText(requireContext(), "양육 유형을 선택해주세요", Toast.LENGTH_SHORT).show()
            }
        }



    }

    override fun onDetach() {
        super.onDetach()

        onBackCallBack.remove()
    }

}