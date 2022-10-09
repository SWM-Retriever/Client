package org.retriever.dailypet.ui.main.diary

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.navArgs
import org.retriever.dailypet.databinding.FragmentDiaryDetailBinding
import org.retriever.dailypet.ui.base.BaseFragment

class DiaryDetailFragment : BaseFragment<FragmentDiaryDetailBinding>() {

    override fun getFragmentBinding(inflater: LayoutInflater, container: ViewGroup?): FragmentDiaryDetailBinding {
        return FragmentDiaryDetailBinding.inflate(inflater, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val args : DiaryDetailFragmentArgs by navArgs()
        Log.d("ABC", args.diaryItem.toString())
    }

}