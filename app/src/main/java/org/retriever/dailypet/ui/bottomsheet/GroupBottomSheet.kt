package org.retriever.dailypet.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.BottomSheetGroupBinding
import org.retriever.dailypet.model.mypage.GroupDetailItem
import org.retriever.dailypet.ui.mypage.adapter.GroupAdapter

class GroupBottomSheet : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetGroupBinding

    private lateinit var groupAdapter: GroupAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomSheetGroupBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initAdapter()
        exitButtonClick()
    }

    private fun initAdapter() {
        groupAdapter = GroupAdapter()

        binding.groupRecyclerview.apply {
            adapter = groupAdapter
            layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.HORIZONTAL, false)
        }

        groupAdapter.submitList(
            listOf(
                GroupDetailItem("abc", "김시진"),
                GroupDetailItem("abc", "김시훈"),
                GroupDetailItem("abc", "김시존"),
            )
        )
    }

    private fun exitButtonClick() {
        binding.exitButton.setOnClickListener {
            dismiss()
        }
    }
}