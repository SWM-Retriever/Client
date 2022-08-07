package org.retriever.dailypet

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.SearchView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.databinding.DialogSearchBreedBinding
import org.retriever.dailypet.interfaces.BreedListAdapter
import org.retriever.dailypet.models.Breed

class BreedSearchDialog(
    context: Context,
    private val okCallback: (String) -> Unit,
) : Dialog(context) { // 뷰를 띄워야하므로 Dialog 클래스는 context를 인자로 받는다.

    private lateinit var binding: DialogSearchBreedBinding
    val TAG = "SEARCH DIALOG"
    lateinit var recyclerView: RecyclerView
    lateinit var breedListAdapter: BreedListAdapter
    lateinit var breeds: ArrayList<Breed>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DialogSearchBreedBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        recyclerView = binding.recycleBreed
        initViews()
    }

    private fun initViews() = with(binding) {
        // 뒤로가기 버튼, 빈 화면 터치를 통해 dialog가 사라지지 않도록
        setCancelable(false)

        // background를 투명하게
        window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))

        // OK Button 클릭에 대한 Callback 처리. 이 부분은 상황에 따라 자유롭게!
        btnSearch.setOnClickListener {
            if (editTextBreed.text.isNullOrBlank()) {
                Toast.makeText(context, "품종을 입력해주세요", Toast.LENGTH_SHORT).show()
            } else {
                okCallback(editTextBreed.text.toString())
                dismiss()
            }
        }
        searchViewBreed.setOnQueryTextListener(searchViewTextListener)
        breeds = tempBreeds()
        setAdapter()

    }

    var searchViewTextListener: SearchView.OnQueryTextListener =
        object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(s: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(s: String?): Boolean {
                return false
            }
        }

    fun setAdapter(){
        //리사이클러뷰에 리사이클러뷰 어댑터 부착
        recyclerView.layoutManager = LinearLayoutManager(context)
        breedListAdapter = BreedListAdapter(breeds, context)
        recyclerView.adapter = breedListAdapter
    }

    fun tempBreeds(): ArrayList<Breed> {
        var tempBreeds = ArrayList<Breed>()
        tempBreeds.add(Breed( "리트리버","온순한 성격"))
        tempBreeds.add(Breed( "말티즈", "하얀 강아지"))

        return tempBreeds
    }
}