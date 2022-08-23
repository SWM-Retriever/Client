package org.retriever.dailypet.fragments

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCareBinding
import org.retriever.dailypet.models.Care

class CareFragment : Fragment(), View.OnClickListener{
    private val TAG = "CARE_FRAGMENT"
    private lateinit var binding: FragmentCareBinding
    private lateinit var care : Care

    fun newInstance(newCare : Care) : CareFragment{
        val fragment = CareFragment()
//        care = newCare
//        Log.e(TAG,"newInstance ${care.name}")

        val args = Bundle()
        args.putString("name", newCare.name)
        args.putInt("totalCnt", newCare.totalCnt)
        args.putInt("curCnt", newCare.curCnt)
        args.putString("period", newCare.period)
        args.putString("log", newCare.log)
        fragment.arguments = args
        return fragment
    }
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCareBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val name = arguments?.getString("name")!!
        val period = arguments?.getString("period")!!
        val log = arguments?.getString("log")!!
        val totalCnt = arguments?.getInt("totalCnt")!!
        val curCnt = arguments?.getInt("curCnt")!!
        init(name, period, log, totalCnt, curCnt)
        setOnClickListener()
    }


    private fun init(name : String, period : String, log : String, totalCnt : Int, curCnt : Int) = with(binding){
        textCareTitle.text = name
        textCareCnt.text = totalCnt.toString()
        textLog.text = log
        textPeriod.text = period
        val percent = curCnt.toDouble() / totalCnt.toDouble()
        progressbar.progress = (percent * 100).toInt()
        Log.e(TAG, percent.toString())
    }

    private fun setOnClickListener() {
//        binding.button.setOnClickListener(this)
//        binding.button.text = name
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.button -> {
                activity?.let{
                    Toast.makeText(context, "", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}