package org.retriever.dailypet.ui.main

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.FragmentCareBinding
import org.retriever.dailypet.model.main.Care
import org.retriever.dailypet.util.ArrayListAdapter

class CareFragment : Fragment(), View.OnClickListener{
    private lateinit var binding: FragmentCareBinding
    private var TOTAL = 1
    private var CUR = 0
    private val eDay : List<String> = listOf("SUN", "MON", "TUE", "WED", "THU", "FRI", "SAT")
    private val kDay : List<String> = listOf("일", "월", "화", "수", "목", "금", "토")

    fun newInstance(newCare : Care) : CareFragment {
        val fragment = CareFragment()
        val arrayListAdapter = ArrayListAdapter()
        val args = Bundle()
        args.putString("name", newCare.careName)
        args.putInt("totalCnt", newCare.totalCareCount)
        args.putInt("curCnt", newCare.currentCount)
        args.putStringArrayList("period", arrayListAdapter.stringListFromJson(newCare.dayOfWeeks))
        args.putParcelableArrayList("log", arrayListAdapter.checkListFromJson(newCare.checkList))
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
        val name = arguments?.getString("name") ?: ""
        val period = (arguments?.getStringArrayList("period") ?: "") as ArrayList<String>
        val log = arguments?.getStringArrayList("log") ?: ""
        val totalCnt = arguments?.getInt("totalCnt") ?: 0
        val curCnt = arguments?.getInt("curCnt") ?: 0
        var weekdays = ""
        for(i in 0 until 7){
            val day = eDay[i]
            for(j in 0 until period.size){
                if(day == period[j]){
                    weekdays += "${kDay[i]} "
                }
            }
        }
        init(name, weekdays, "", totalCnt, curCnt)
        setOnClickListener()
    }

    @SuppressLint("SetTextI18n")
    private fun init(name: String, weekdays: String, log: String, totalCnt: Int, curCnt: Int) = with(binding){
        textCareTitle.text = name
        textCareCnt.text = curCnt.toString() + "회/" + totalCnt.toString() + "회"
        textLog.text = log
        periodTitleText.text = weekdays
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
            R.id.check_button -> {
                activity?.let{
                    increaseProgress()
                }
            }
            R.id.cancel_text -> {
                activity?.let{
                    decreaseProgress()
                }
            }
        }
    }
}