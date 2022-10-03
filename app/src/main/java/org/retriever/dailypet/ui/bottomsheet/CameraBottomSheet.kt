package org.retriever.dailypet.ui.bottomsheet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.BottomSheetCameraBinding

class CameraBottomSheet(val itemClick: (Int) -> Unit) : BottomSheetDialogFragment() {

    private lateinit var binding: BottomSheetCameraBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomSheetCameraBinding.inflate(inflater, container, false)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        buttonClick()
    }

    private fun buttonClick() = with(binding) {
        exitButton.setOnClickListener {
            dismiss()
        }

        capturePhotoTextview.setOnClickListener {
            itemClick(0)
            dismiss()
        }

        getPhotoTextview.setOnClickListener {
            itemClick(1)
            dismiss()
        }
    }

}