package org.retriever.dailypet.ui.main.diary

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.squareup.moshi.Moshi
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentDiaryMainBinding
import org.retriever.dailypet.model.diary.Diary
import org.retriever.dailypet.model.diary.DiaryList
import org.retriever.dailypet.ui.base.BaseFragment
import org.retriever.dailypet.ui.main.diary.adapter.DiaryAdapter
import java.io.IOException

class DiaryMainFragment : BaseFragment<FragmentDiaryMainBinding>() {

    private lateinit var diaryList: List<Diary>

    private lateinit var diaryAdapter : DiaryAdapter

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryMainBinding {
        return FragmentDiaryMainBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        //diaryList = listOf()
        getStoreItemList()
        if(diaryList.isEmpty()){
           showEmptyDiaryLayout()
        }else{
            showNotEmptyDiaryLayout()
            initDiaryAdapter()
            diaryAdapter.submitList(diaryList)
            galleryButtonClick()
        }
        floatingActionButtonClick()
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

    private fun getStoreItemList() {
        val json = getJsonDataFromAsset(requireContext()) ?: ""
        val moshi = Moshi.Builder().build()
        val adapter = moshi.adapter(DiaryList::class.java)
        diaryList = adapter.fromJson(json)?.diaryList ?: listOf()
    }

    private fun getJsonDataFromAsset(context: Context): String? {
        val jsonString: String
        try {
            jsonString = context.assets.open("test.json").bufferedReader().use {
                it.readText()
            }
        } catch (ioException: IOException) {
            ioException.printStackTrace()
            return null
        }

        return jsonString
    }

}