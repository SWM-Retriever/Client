package org.retriever.dailypet.ui.signup

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import org.json.JSONArray
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCreationCompleteBinding
import org.retriever.dailypet.model.signup.pet.Pet
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.hideProgressCircular

class CreationCompleteFragment : BaseFragment<FragmentCreationCompleteBinding>() {

    private var invitationCode = ""
    private var nickname =""
    private var groupName =""
    private var familyId = -1
    private var familyType =""
    private lateinit var petIdList : List<Pet>

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentCreationCompleteBinding {
        return FragmentCreationCompleteBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initProgressCircular()
        initInfo()
        buttonClick()
    }

    private fun initInfo() = with(binding) {
        val args: CreationCompleteFragmentArgs by navArgs()
        val petResponse = args.petResponse

        groupNameText.text = petResponse.familyName
        groupNicknameText.text = petResponse.nickName
        var petString = petResponse.petList[0].petName
        if (petResponse.petList.size > 1) {
            for (i in 1 until petResponse.petList.size) {
                petString += ", " + petResponse.petList[i].petName
            }
        }
        groupPetNameText.text = petString

        saveSharedPreference(
            petResponse.nickName,
            petResponse.familyId,
            petResponse.familyName,
            petResponse.invitationCode,
            petResponse.groupType,
            petResponse.profileImageUrl,
            petResponse.petList
        )
    }

    private fun initProgressCircular() {
        hideProgressCircular(binding.progressCircular)
    }

    private fun buttonClick() = with(binding) {

        petAddButton.setOnClickListener {
            root.findNavController().popBackStack()
        }

        careStartButton.setOnClickListener {
            root.findNavController().navigate(R.id.action_creationCompleteFragment_to_mainActivity)
        }

        groupInviteButton.setOnClickListener {
            //TODO 그룹초대 로직 구현
            onShareClicked()

        }
    }

    private fun onShareClicked() {
        val message = "[반려하루]\n$nickname 님이 $groupName 그룹의 초대장을 보냈어요\n" + R.string.invitation_message_text.toString()
        val code = message + invitationCode

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
        petIdList: List<Pet>
    ) {
        val petIdListJsonArray = JSONArray()
        petIdList.forEach { id ->
            petIdListJsonArray.put(id)
        }

        GlobalApplication.prefs.apply {
            this.nickname = nickName
            this.familyId = familyId
            this.groupName = familyName
            this.invitationCode = invitationCode
            this.groupType = groupType
            this.profileImageUrl = profileImageUrl
            this.petIdList = petIdListJsonArray.toString()
        }
    }

}