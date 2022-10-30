package org.retriever.dailypet.ui.statistics.adapter

import android.graphics.Color
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.ItemDetailStatisticsChartBinding
import org.retriever.dailypet.model.statistics.CareItem
import org.retriever.dailypet.model.statistics.DetailStaticsItem
import org.retriever.dailypet.ui.statistics.BarChartMarkerView
import kotlin.random.Random

class DetailStatisticsChartViewHolder(private val binding: ItemDetailStatisticsChartBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DetailStaticsItem) {
        val color = getRandomColor()
        initBarChart(binding.barChart, color)
        setData(binding.barChart, color, item.careList)
    }

    private fun initBarChart(barChart: BarChart, color: Int) {
        barChart.run {
            setDrawGridBackground(false)
            setDrawBarShadow(false)
            setDrawBorders(false)
            setScaleEnabled(false)

            val description = Description()
            description.isEnabled = false
            this.description = description

            animateX(1000)
            animateY(1000)

            axisLeft.run {
                setDrawLabels(false)
                setDrawGridLines(false)
                setDrawAxisLine(false)
            }

            xAxis.run {
                setDrawAxisLine(false)
                setDrawGridLines(false)
                position = XAxis.XAxisPosition.BOTTOM

                granularity = 1f
            }

            axisRight.isEnabled = false
            legend.isEnabled = false

            val marker = BarChartMarkerView(binding.root.context, R.layout.barchart_marker_view, color)
            this.marker = marker
        }

    }

    private fun setData(barChart: BarChart, color: Int, careList: List<CareItem>) {

        val valueList = ArrayList<BarEntry>()
        val nameList = ArrayList<String>()
        val title = "그래프"

        careList.forEachIndexed { index, careItem ->
            valueList.add(BarEntry(index.toFloat(), careItem.careCount))
            nameList.add(careItem.groupRoleName)
        }

        val barDataSet = BarDataSet(valueList, title)

        val startColor = ContextCompat.getColor(binding.root.context, R.color.barchart_start_color)
        val endColor = ContextCompat.getColor(binding.root.context, R.color.barchart_end_color)
        barDataSet.setGradientColor(startColor, endColor)

        barDataSet.highLightColor = color
        barDataSet.highLightAlpha = 255
        barDataSet.setDrawValues(false)

        val data = BarData(barDataSet)
        barChart.data = data
        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(nameList)
        barChart.axisLeft.axisMaximum = barChart.yChartMax + 1f
        barChart.axisRight.axisMaximum = barChart.yChartMax + 1f

        barChart.invalidate()
    }

    private fun getRandomColor(): Int {
        val rnd = Random.Default

        return Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256))
    }

}