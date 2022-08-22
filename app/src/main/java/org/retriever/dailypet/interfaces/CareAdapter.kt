package org.retriever.dailypet.interfaces
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.viewpager2.adapter.FragmentStateAdapter
import org.retriever.dailypet.fragments.CareFragment

class CareAdapter(fragment: Fragment) : FragmentStateAdapter(fragment) {

    var fragments : ArrayList<Fragment> = ArrayList()
    override fun getItemCount(): Int{
        return fragments.size
    }

    override fun createFragment(position: Int): Fragment {
        // Return a NEW fragment instance in createFragment(int)
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
