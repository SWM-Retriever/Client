package org.retriever.dailypet.ui.signup.pet

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SearchView
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import org.retriever.dailypet.GlobalApplication
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.BottomSheetBreedBinding
import org.retriever.dailypet.model.Resource
import org.retriever.dailypet.model.signup.pet.Breed
import org.retriever.dailypet.util.hideProgressCircular
import org.retriever.dailypet.util.showProgressCircular

@AndroidEntryPoint
class BreedBottomSheet(val itemClick: (Breed) -> Unit) : BottomSheetDialogFragment() {

    private val petViewModel by activityViewModels<PetViewModel>()

    private var _binding: BottomSheetBreedBinding? = null
    private val binding get() = _binding!!

    private val jwt = GlobalApplication.prefs.jwt ?: ""

    private lateinit var breedAdapter: BreedAdapter

    private var petType = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setStyle(STYLE_NORMAL, R.style.BottomSheetDialogStyle)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        _binding = BottomSheetBreedBinding.inflate(inflater, container, false)

        petType = arguments?.getString("petType") ?: ""

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        initAdapter()
        initData()
        initSearchEditText()
        buttonClick()
    }

    private fun initAdapter() {
        breedAdapter = BreedAdapter()

        binding.breedRecyclerview.apply {
            adapter = breedAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }

        breedAdapter.onItemClick = {
            itemClick(it)
            dismiss()
        }
    }

    private fun initData() = with(binding) {
        petViewModel.getPetBreedList(petType, jwt)

        petViewModel.petBreedList.observe(viewLifecycleOwner) { response ->
            when (response) {
                is Resource.Loading -> {
                    showProgressCircular(progressCircular)
                }
                is Resource.Success -> {
                    hideProgressCircular(progressCircular)
                    breedAdapter.modifyList(response.data?.petKindList ?: listOf())
                }
                is Resource.Error -> {
                    hideProgressCircular(progressCircular)
                }
            }
        }
    }

    private fun initSearchEditText() {
        binding.breedSearchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(query: String?): Boolean {
                breedAdapter.filter(query)
                return true
            }
        })
    }

    private fun buttonClick() {
        binding.exitButton.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        _binding = null

        super.onDestroyView()
    }

}