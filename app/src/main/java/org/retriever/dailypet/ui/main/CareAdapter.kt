package org.retriever.dailypet.ui.main

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import androidx.viewpager2.adapter.FragmentViewHolder

class CareAdapter(fragment: FragmentActivity) : FragmentStateAdapter(fragment) {

    var fragments: ArrayList<Fragment> = ArrayList()

    fun getItem(position: Int): Fragment {
        return fragments[position]
    }

    fun refreshFragment(index: Int, fragment: Fragment) {
        fragments[index] = fragment
    }

    override fun onBindViewHolder(
        holder: FragmentViewHolder,
        position: Int,
        payloads: MutableList<Any>
    ) {
//        val fragment  = fragments[position]
//        fragment.let{
//            if(it is CareFragment){
//                it.update()
//            }
//        }
        super.onBindViewHolder(holder, position, payloads)
    }

    override fun getItemCount(): Int {
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        return fragments[position]
    }

    fun addFragment(fragment: Fragment) {
        fragments.add(fragment)
        notifyItemInserted(fragments.size - 1)
    }

    fun removeFragment() {
        fragments.removeLast()
        notifyItemRemoved(fragments.size)
    }
}