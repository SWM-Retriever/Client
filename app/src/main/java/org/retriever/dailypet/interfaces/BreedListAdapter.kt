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
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.DialogSearchBreedBinding
import org.retriever.dailypet.models.Breed

class BreedListAdapter(var breeds: ArrayList<Breed>, var con: Context) :
    RecyclerView.Adapter<BreedListAdapter.ViewHolder>(), Filterable {
        var TAG = "BREED LIST ADAPTER"
        var filteredBreeds = ArrayList<Breed>()
        var itemFilter = ItemFilter()

        inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
            var text_title: TextView
            var text_sub: TextView

            init {
                text_title = itemView.findViewById(R.id.text_breedItem)
                text_sub = itemView.findViewById(R.id.text_descriptionItem)

                itemView.setOnClickListener {
                    AlertDialog.Builder(con).apply {
                        var position = adapterPosition
                        var breed = filteredBreeds[position]
                        setTitle(breed.breed)
                        setMessage(breed.description)
                        setPositiveButton("OK", DialogInterface.OnClickListener { dialog, which ->
                            Toast.makeText(con, "OK Button Click", Toast.LENGTH_SHORT).show()
                        })
                        show()
                    }
                }
            }
        }

        init {
            filteredBreeds.addAll(breeds)
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
            val con = parent.context
            val inflater = con.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val view = inflater.inflate(R.layout.dialog_search_breed, parent, false)
            return ViewHolder(view)
        }

        override fun onBindViewHolder(holder: ViewHolder, position: Int) {
            val breed: Breed = filteredBreeds[position]
            holder.text_title.text = breed.breed
            holder.text_sub.text = breed.description
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
                        if (breed.breed.contains(filterString)) {
                            filteredList.add(breed)
                        }
                    }
                    //그 외의 경우(공백제외 2글자 초과) -> 이름/전화번호로 검색
                } else {
                    for (breed in breeds) {
                        if (breed.breed.contains(filterString) || breed.description.contains(filterString)) {
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
        }


}