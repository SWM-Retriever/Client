package org.retriever.dailypet.ui.home.statistics.adapter

import android.graphics.Color
import android.util.Log
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Description
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import org.retriever.dailypet.R
import org.retriever.dailypet.databinding.ItemDetailStatisticsChartBinding
import org.retriever.dailypet.model.statistics.DetailCareItem
import org.retriever.dailypet.model.statistics.DetailContributionItem
import org.retriever.dailypet.ui.home.statistics.BarChartMarkerView
import kotlin.random.Random

class DetailStatisticsChartViewHolder(private val binding: ItemDetailStatisticsChartBinding) : RecyclerView.ViewHolder(binding.root) {

    fun bind(item: DetailContributionItem) {
        val color = getRandomColor()
        initBarChart(binding.barChart, color)
        setData(binding.barChart, color, item.careCountList ?: emptyList())
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

    private lateinit var maxEntry: BarEntry

    private fun setData(barChart: BarChart, color: Int, careList: List<DetailCareItem>) {
            Log.d("ABC", careList.toString())
        val valueList = ArrayList<BarEntry>()
        val nameList = ArrayList<String>()
        val title = "그래프"

        var max = -1f
        careList.forEachIndexed { index, careItem ->
            valueList.add(BarEntry(index.toFloat(), careItem.careCount))
            nameList.add(careItem.familyRoleName)

            if (max < careItem.careCount) {
                maxEntry = valueList[index]
                max = careItem.careCount
            }
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

        val highlight = Highlight(maxEntry.x, maxEntry.y, 0)
        highlight.dataIndex = 0
        barChart.highlightValue(highlight)

        barChart.invalidate()
    }

    private fun getRandomColor(): Int {
        val rnd = Random.Default

        val baseColor = Color.WHITE
        val baseRed = Color.red(baseColor)
        val baseGreen = Color.green(baseColor)
        val baseBlue = Color.blue(baseColor)

        val red = (baseRed + rnd.nextInt(256)) / 2
        val green = (baseGreen + rnd.nextInt(256)) / 2
        val blue = (baseBlue + rnd.nextInt(256)) / 2

        return Color.rgb(red, green, blue)
    }

}