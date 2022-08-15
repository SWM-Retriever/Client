package org.retriever.dailypet.fragments

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.retriever.dailypet.LoginActivity
import org.retriever.dailypet.R
import org.retriever.dailypet.RegisterCareActivity
import org.retriever.dailypet.databinding.FragmentHomeBinding

class HomeFragment : Fragment(), View.OnClickListener{
    private val TAG = "HomeFragment"
    private lateinit var binding: FragmentHomeBinding
    fun newInstance() : HomeFragment{
        return HomeFragment()
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setOnClickListener()
    }

    private fun setOnClickListener() {
        binding.btnRegisterCare.setOnClickListener(this)

    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_register_care -> {
                activity?.let{
                    val intent = Intent(context, RegisterCareActivity::class.java)
                    startActivity(intent)
                }
            }
        }
    }
}