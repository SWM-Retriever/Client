package org.retriever.dailypet.ui.signup

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCreationCompleteBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.home.care.HomeMainFragmentDirections
import org.retriever.dailypet.ui.login.LoginActivity
import org.retriever.dailypet.ui.signup.pet.PetViewModel
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular
import java.util.ArrayList

class CreationCompleteFragment : BaseFragment<FragmentCreationCompleteBinding>() {

    private val petViewModel by activityViewModels<PetViewModel>()
    private val jwt = GlobalApplication.prefs.jwt ?: ""
    private var petNameList: MutableList<String> = mutableListOf()
    private val familyId = GlobalApplication.prefs.familyId
    private var nickName = ""
    private var groupName = ""
    private var invitationCode = ""
    private var progressList: ArrayList<String> = arrayListOf("프로필","그룹","반려동물")
    private lateinit var onBackCallBack: OnBackPressedCallback

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreationCompleteBinding {
        return FragmentCreationCompleteBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initCallBack()
        initProgressCircular()
        initPetList()
        getPetList()
        buttonClick()
    }

    private fun initCallBack() {
        onBackCallBack = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                // Do Nothing
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(viewLifecycleOwner, onBackCallBack)
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun getPetList(){
        petViewModel.getPetList(familyId, jwt)
    }

    private fun initPetList() = with(binding) {
        petViewModel.getPetListResponse.observe(viewLifecycleOwner) { event ->
            event.getContentIfNotHandled()?.let { response ->
                when (response) {
                    is Resource.Loading -> {
                        showProgressCircular(progressCircular)
                    }
                    is Resource.Success -> {
                        hideProgressCircular(progressCircular)
                        val petResponse = response.data?.petInfoDetailList
                        petNameList.clear()

                        petResponse?.forEach { pet ->
                            petNameList.add(pet.petName)
                        }
                        initInfo()
                    }
                    is Resource.Error -> {
                        hideProgressCircular(progressCircular)
                    }
                }
            }
        }
    }

    private fun initInfo() = with(binding) {
        val args: CreationCompleteFragmentArgs by navArgs()
        val petResponse = args.petResponse
        var isAlone = false

        nickName = petResponse.nickName
        groupName = petResponse.familyName ?: ""
        invitationCode = petResponse.invitationCode ?: ""
        if(groupName.isEmpty()){
            groupName = "${nickName}의 1인그룹"
            isAlone = true
        }
        else{
            groupName = petResponse.familyName ?: "${nickName}의 1인그룹"
        }
        groupNameText.text = groupName
        groupNicknameText.text = nickName
        var petString = petNameList[0]
        if (petNameList.size > 1) {
            for (i in 1 until petNameList.size) {
                petString += ", " + petNameList[i]
            }
        }
        groupPetNameText.text = petString
        if(isAlone){
            groupInviteButton.visibility = View.INVISIBLE
            inviteCommentText.visibility = View.INVISIBLE
        }

        saveSharedPreference(
            nickName,
            petResponse.familyId,
            groupName,
            invitationCode,
            petResponse.groupType,
            petResponse.profileImageUrl,
        )
    }

    private fun buttonClick() = with(binding) {

        petAddButton.setOnClickListener {
            root.findNavController().navigate(R.id.action_creationCompleteFragment_to_createPetFragment)
        }

        careStartButton.setOnClickListener {
            val action = CreationCompleteFragmentDirections.actionCreationCompleteFragmentToMainActivity()
            root.findNavController().navigate(action)
        }

        groupInviteButton.setOnClickListener {
            onShareClicked()
        }
    }

    private fun onShareClicked() {
        val code = getString(R.string.invitation_message_text, nickName, groupName, invitationCode)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, code)
        startActivity(Intent.createChooser(intent, "초대코드 공유하기"))
    }

    private fun saveSharedPreference(
        nickName: String,
        familyId: Int,
        familyName: String,
        invitationCode: String,
        groupType: String,
        profileImageUrl: String,
    ) {
        GlobalApplication.prefs.apply {
            this.nickname = nickName
            this.familyId = familyId
            this.groupName = familyName
            this.invitationCode = invitationCode
            this.groupType = groupType
            this.profileImageUrl = profileImageUrl
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        onBackCallBack.remove()
    }

}