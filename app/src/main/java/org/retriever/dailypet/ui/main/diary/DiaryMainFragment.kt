package org.retriever.dailypet.ui.main.diary

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
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

class DiaryMainFragment : BaseFragment<FragmentDiaryMainBinding>() {

    private val diaryViewModel by activityViewModels<DiaryViewModel>()

    private lateinit var diaryAdapter : DiaryAdapter

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryMainBinding {
        return FragmentDiaryMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        getDiaryList()
        observeDiaryListResponse()
        floatingActionButtonClick()
    }

    private fun getDiaryList(){
        val familyId = GlobalApplication.prefs.familyId
        val jwt = GlobalApplication.prefs.jwt ?: ""
        diaryViewModel.getDiaryList(familyId, jwt)
    }

    private fun observeDiaryListResponse() {
        diaryViewModel.diaryListResponse.observe(viewLifecycleOwner){response ->
            when(response){
                is Resource.Loading ->{

                }
                is Resource.Success->{
                    val diaryList = response.data?.diaryList ?: listOf()
                    checkDiaryList(diaryList)
                }
                is Resource.Error ->{

                }
            }
        }
    }

    private fun checkDiaryList(list : List<DiaryItem>){
        if(list.isEmpty()){
            showEmptyDiaryLayout()
        }else{
            showNotEmptyDiaryLayout()
            initDiaryAdapter()
            diaryAdapter.submitList(list)
            galleryButtonClick()
        }
    }

    private fun showEmptyDiaryLayout() = with(binding){
        emptyDiaryLayout.root.visibility = View.VISIBLE
        notEmptyDiaryLayout.root.visibility = View.GONE
    }

    private fun showNotEmptyDiaryLayout() = with(binding){
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
            binding.root.findNavController().navigate(R.id.action_diaryMainFragment_to_diaryDetailFragment)
        }
    }

    private fun floatingActionButtonClick() = with(binding){
        floatingAddButton.setOnClickListener {
            root.findNavController().navigate(R.id.action_diaryMainFragment_to_diaryRegisterFragment)
        }
    }

    private fun galleryButtonClick() = with(binding){
        notEmptyDiaryLayout.galleryButton.setOnClickListener {
            root.findNavController().navigate(R.id.action_diaryMainFragment_to_diaryGalleryFragment)
        }
    }

}