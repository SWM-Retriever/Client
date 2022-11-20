package org.retriever.dailypet.ui.mypage.detail

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentMyPageDetailBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.diary.DiaryItem
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.login.LoginActivity
import org.retriever.dailypet.ui.mypage.adapter.GroupAdapter
import org.retriever.dailypet.ui.mypage.adapter.PetAdapter
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class MyPageDetailFragment : BaseFragment<FragmentMyPageDetailBinding>() {

    private val myPageDetailViewModel by activityViewModels<MyPageDetailViewModel>()

    private lateinit var groupAdapter: GroupAdapter
    private lateinit var petAdapter: PetAdapter
    private var delegateDialog: MaterialAlertDialogBuilder? = null
    private val nickname = GlobalApplication.prefs.nickname ?: ""
    private val groupName = GlobalApplication.prefs.groupName ?: ""
    private val invitationCode = GlobalApplication.prefs.invitationCode ?: ""
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
        observeGroupResponse()
        observePetDetailResponse()
        observeRecentDiaryResponse()
        observePatchLeaderResponse()
    }

    private fun callApi() {
        myPageDetailViewModel.getPetList(familyId, jwt)
        myPageDetailViewModel.getGroupInfo(familyId, jwt)
        myPageDetailViewModel.getRecentDiary(familyId, jwt)
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

        groupAdapter.onItemClick = { groupItem ->
            showDelegateDialog(groupItem.memberId, groupItem.familyRoleName)
        }
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

        memberAddButton.setOnClickListener {
            onShareClicked()
        }

        petAddButton.setOnClickListener {
            val action = MyPageDetailFragmentDirections.actionMyPageDetailFragmentToCreatePetFragment2(true)
            binding.root.findNavController().navigate(action)
        }

        exitButton.setOnClickListener {
            (activity as MyPageDetailActivity).finish()
        }
    }

    private fun showDelegateDialog(memberId : Int, familyRoleName: String) {
        delegateDialog = MaterialAlertDialogBuilder(requireContext(), R.style.CustomMaterialAlertDialog)
            .setTitle("그룹장 위임")
            .setMessage("$familyRoleName 에게 그룹장을 위임하시겠습니까?")
            .setNegativeButton(getString(R.string.dialog_no_text)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.dialog_yes_text)) { _, _ ->
                myPageDetailViewModel.patchLeader(familyId, memberId , jwt)
            }
        delegateDialog?.show()
    }

    private fun observeGroupResponse() = with(binding) {
        myPageDetailViewModel.groupResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    groupAdapter.submitList(response.data?.familyMemberList)
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                }
            }
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

    private fun observeRecentDiaryResponse() = with(binding) {
        myPageDetailViewModel.getRecentDiaryResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    itemRecentDiary.root.visibility = View.VISIBLE
                    itemNotRecentDiary.root.visibility = View.INVISIBLE
                    initRecentDiaryCardView(response.data)
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                    itemRecentDiary.root.visibility = View.INVISIBLE
                    itemNotRecentDiary.root.visibility = View.VISIBLE
                }
            }
        }
    }

    private fun observePatchLeaderResponse() = with(binding) {
        myPageDetailViewModel.patchLeaderResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    callApi()
                    initGroupAdapter()
                    Toast.makeText(requireContext(), "그룹장 위임에 성공하였습니다", Toast.LENGTH_SHORT).show()
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                    if(response.code == CANNOT_PATCH_LEADER){
                        Toast.makeText(requireContext(), "위임 권한이 없거나\n위임가능한 그룹원이 아닙니다", Toast.LENGTH_SHORT).show()
                    }
                    else{
                        Toast.makeText(requireContext(), response.message, Toast.LENGTH_SHORT).show()

                    }
                }
            }
        }
    }

    private fun initRecentDiaryCardView(diaryItem: DiaryItem?) = with(binding.itemRecentDiary) {
        diaryItem?.let {
            writerNickNameText.text = it.authorNickName
            diaryContentText.text = it.diaryText
            if (!it.authorImageUrl.isNullOrEmpty()) {
                writerCircleImage.load(it.authorImageUrl)
            }
            if (!it.diaryImageUrl.isNullOrEmpty()) {
                diaryImage.visibility = View.VISIBLE
                diaryImage.load(it.diaryImageUrl)
            } else {
                diaryImage.visibility = View.GONE
            }
        }
    }

    private fun onShareClicked() {
        val code = getString(R.string.invitation_message_text, nickname, groupName, invitationCode)
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, code)
        startActivity(Intent.createChooser(intent, "초대코드 공유하기"))
    }

    companion object {
        private const val CANNOT_PATCH_LEADER = 403
    }
}