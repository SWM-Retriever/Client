package org.retriever.dailypet.ui.signup

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.findNavController
import androidx.navigation.fragment.navArgs
import com.google.firebase.dynamiclinks.DynamicLink
import com.google.firebase.dynamiclinks.FirebaseDynamicLinks
import com.google.firebase.dynamiclinks.ktx.dynamicLinks
import com.google.firebase.ktx.Firebase
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCreationCompleteBinding
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.util.DynamicLinksUtil
import org.retriever.dailypet.util.hideProgressCircular

class CreationCompleteFragment : BaseFragment<FragmentCreationCompleteBinding>() {
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
        groupNicknameText.text = petResponse.familyRoleName
        var petString = petResponse.petList[0].petName
        if (petResponse.petList.size > 1) {
            for (i in 1 until petResponse.petList.size) {
                petString += ", " + petResponse.petList[i].petName
            }
        }
        groupPetNameText.text = petString
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
        val link = "https://dailypet.page.link/invite"

        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, link)
        startActivity(Intent.createChooser(intent, "Share Link"))
    }




}