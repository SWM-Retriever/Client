package org.retriever.dailypet.interfaces

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.recyclerview.widget.RecyclerView
import org.retriever.dailypet.BreedSearchDialog
import org.retriever.dailypet.R
import org.retriever.dailypet.models.Breed
/*class BreedListAdapter(var breeds: ArrayList<Breed>, var con: Context) :
    RecyclerView.Adapter<BreedListAdapter.ViewHolder>(), Filterable {*/
       /* val TAG = "BREED LIST ADAPTER"
        var filteredBreeds = ArrayList<Breed>()
        var itemFilter = ItemFilter()
        private lateinit var binding : ActivityCreatePetBinding

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var textTitle: TextView
            var textSub: TextView

            init {
                textTitle = itemView.findViewById(R.id.text_breedItem)
                textSub = itemView.findViewById(R.id.text_descriptionItem)

                itemView.setOnClickListener {

//                    AlertDialog.Builder(con).apply {
//                        val position = adapterPosition
//                        val breed = filteredBreeds[position]
//                        setTitle("선택한 종이 맞습니까?")
//                        setMessage("\n종 이름 : "+ breed.name)
//                        setPositiveButton("네", DialogInterface.OnClickListener { _ , _ ->
//                            binding.editTextBreed.setText(breed.name)
//                            BreedSearchDialog(con){}.dismiss()
//
//
//
//                        })
//                        setNegativeButton("아니요", DialogInterface.OnClickListener { dialog, _ ->
//                            dialog.cancel()
//                        })
//                        show()
//                    }
                }
            }
        }

        init {
            filteredBreeds.addAll(breeds)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val con = parent.context
            val inflater = con.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            binding = ActivityCreatePetBinding.inflate(inflater)
            val view = inflater.inflate(R.layout.item_breed, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val breed: Breed = filteredBreeds[position]
            Log.e(TAG, breed.name)
            holder.textTitle.text = breed.name
            holder.textSub.text = breed.description
        }

        override fun getItemCount(): Int {
            return filteredBreeds.size
        }

        //-- filter
        override fun getFilter(): Filter {
            return itemFilter
        }

        inner class ItemFilter : Filter() {
            override fun performFiltering(charSequence: CharSequence): FilterResults {
                val filterString = charSequence.toString()
                val results = FilterResults()
                Log.d(TAG, "charSequence : $charSequence")

                //검색이 필요없을 경우를 위해 원본 배열을 복제
                val filteredList: ArrayList<Breed> = ArrayList<Breed>()
                //공백제외 아무런 값이 없을 경우 -> 원본 배열
                if (filterString.trim { it <= ' ' }.isEmpty()) {
                    results.values = breeds
                    results.count = breeds.size
                    return results
                    //공백제외 2글자 이인 경우 -> 이름으로만 검색
                } else if (filterString.trim { it <= ' ' }.length <= 2) {
                    for (breed in breeds) {
                        if (breed.name.contains(filterString)) {
                            filteredList.add(breed)
                        }
                    }
                    //그 외의 경우(공백제외 2글자 초과) -> 이름, 설명으로 검색
                } else {
                    for (breed in breeds) {
                        if (breed.name.contains(filterString) || breed.description.contains(filterString)) {
                            filteredList.add(breed)
                        }
                    }
                }
                results.values = filteredList
                results.count = filteredList.size

                return results
            }

            @SuppressLint("NotifyDataSetChanged")
            override fun publishResults(charSequence: CharSequence?, filterResults: FilterResults) {
                filteredBreeds.clear()
                filteredBreeds.addAll(filterResults.values as ArrayList<Breed>)
                notifyDataSetChanged()
            }
        }*/
//}