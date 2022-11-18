package org.retriever.dailypet.ui.mypage.detail

import android.content.Intent
import android.opengl.Visibility
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import coil.load
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentMyPageDetailBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.diary.DiaryItem
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.mypage.MyPageMainFragmentDirections
import org.retriever.dailypet.ui.mypage.adapter.GroupAdapter
import org.retriever.dailypet.ui.mypage.adapter.PetAdapter
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class MyPageDetailFragment : BaseFragment<FragmentMyPageDetailBinding>() {

    private val myPageDetailViewModel by activityViewModels<MyPageDetailViewModel>()

    private lateinit var groupAdapter: GroupAdapter
    private lateinit var petAdapter: PetAdapter

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

        memberAddButton.setOnClickListener{
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

    private fun initRecentDiaryCardView(diaryItem: DiaryItem?) = with(binding.itemRecentDiary) {
        diaryItem?.let {
            writerNickNameText.text = it.authorNickName
            diaryContentText.text = it.diaryText
            if(!it.authorImageUrl.isNullOrEmpty()){
                writerCircleImage.load(it.authorImageUrl)
            }
            if(!it.diaryImageUrl.isNullOrEmpty()) {
                diaryImageCardView.visibility = View.VISIBLE
                diaryImage.load(it.diaryImageUrl)
            } else{
                diaryImageCardView.visibility = View.GONE
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

}