package org.retriever.dailypet.ui.main.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentDiaryMainBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.diary.DiaryItem
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.main.diary.adapter.DiaryAdapter
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

class DiaryMainFragment : BaseFragment<FragmentDiaryMainBinding>() {

    private val diaryViewModel by activityViewModels<DiaryViewModel>()

    private lateinit var diaryAdapter: DiaryAdapter

    private val familyId = GlobalApplication.prefs.familyId
    private val jwt = GlobalApplication.prefs.jwt ?: ""

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryMainBinding {
        return FragmentDiaryMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDiaryList()
        observeDiaryListResponse()
        initDiaryAdapter()
        galleryButtonClick()
        floatingActionButtonClick()
    }

    private fun getDiaryList() {
        diaryViewModel.getDiaryList(familyId, jwt)
    }

    private fun observeDiaryListResponse() {
        diaryViewModel.diaryListResponse.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(binding.progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(binding.progressCircular)
                    val diaryList = response.data?.diaryList ?: listOf()
                    checkDiaryList(diaryList)
                }
                is Resource.Error -> {
                    hideProgressCircular(binding.progressCircular)
                    Toast.makeText(requireContext(), "다이어리를 읽어오는데 실패하였습니다.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun checkDiaryList(list: List<DiaryItem>) {
        if (list.isEmpty()) {
            showEmptyDiaryLayout()
        } else {
            showNotEmptyDiaryLayout()
            diaryAdapter.submitList(list)
        }
    }

    private fun showEmptyDiaryLayout() = with(binding) {
        emptyDiaryLayout.root.visibility = View.VISIBLE
        notEmptyDiaryLayout.root.visibility = View.GONE
    }

    private fun showNotEmptyDiaryLayout() = with(binding) {
        emptyDiaryLayout.root.visibility = View.GONE
        notEmptyDiaryLayout.root.visibility = View.VISIBLE
    }

    private fun initDiaryAdapter() {
        diaryAdapter = DiaryAdapter()
        binding.notEmptyDiaryLayout.diaryRecyclerview.apply {
            adapter = diaryAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        }

        diaryAdapter.onItemClick = {
            val action = DiaryMainFragmentDirections.actionDiaryMainFragmentToDiaryDetailFragment(it)
            binding.root.findNavController().navigate(action)
        }
    }

    private fun floatingActionButtonClick() = with(binding) {
        floatingAddButton.setOnClickListener {
            val emptyItem = DiaryItem(
                viewType = "",
                date = "",
                diaryId = -1,
                authorImageUrl = "",
                authorNickName = "",
                diaryImageUrlList = emptyList(),
                diaryText = "",
            )
            val action = DiaryMainFragmentDirections.actionDiaryMainFragmentToDiaryRegisterFragment(emptyItem)
            root.findNavController().navigate(action)
        }
    }

    private fun galleryButtonClick() = with(binding) {
        notEmptyDiaryLayout.galleryButton.setOnClickListener {
            root.findNavController().navigate(R.id.action_diaryMainFragment_to_diaryGalleryFragment)
        }
    }

}