package org.retriever.dailypet.fragments

import android.annotation.SuppressLint
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
    private var TOTAL = 1
    private var CUR = 0

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

    @SuppressLint("SetTextI18n")
    private fun init(name : String, period : String, log : String, totalCnt : Int, curCnt : Int) = with(binding){
        textCareTitle.text = name
        textCareCnt.text = curCnt.toString() + "회/" + totalCnt.toString() + "회"
        textLog.text = log
        textPeriod.text = period
        val percent = curCnt.toDouble() / totalCnt.toDouble()
        binding.progressbar.progress = (percent * 100).toInt()

        TOTAL = totalCnt
        CUR = curCnt
    }

    @SuppressLint("SetTextI18n")
    private fun increaseProgress(){
        CUR++
        if(CUR > TOTAL) CUR = TOTAL
        val percent = CUR.toDouble() / TOTAL.toDouble()
        binding.textCareCnt.text = CUR.toString() + "회/" + TOTAL.toString() + "회"
        binding.progressbar.progress = (percent * 100).toInt()
    }

    @SuppressLint("SetTextI18n")
    private fun decreaseProgress(){
        CUR--
        if(CUR < 0) CUR = 0
        val percent = CUR.toDouble() / TOTAL.toDouble()
        binding.textCareCnt.text = CUR.toString() + "회/" + TOTAL.toString() + "회"
        binding.progressbar.progress = (percent * 100).toInt()
    }

    private fun setOnClickListener() {
        binding.btnCheck.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_check -> {
                activity?.let{
                    increaseProgress()
                }
            }
            R.id.btn_cancel -> {
                activity?.let{
                    decreaseProgress()
                }
            }
        }
    }
}