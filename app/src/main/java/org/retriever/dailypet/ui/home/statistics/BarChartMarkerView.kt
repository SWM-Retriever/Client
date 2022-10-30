package org.retriever.dailypet.ui.home.statistics

import android.content.Context
import android.graphics.Canvas
import android.widget.TextView
import androidx.core.graphics.BlendModeColorFilterCompat
import androidx.core.graphics.BlendModeCompat
import com.github.mikephil.charting.components.MarkerView
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.highlight.Highlight
import org.retriever.dailypet.R

class BarChartMarkerView(context: Context?, layoutResource: Int, val color: Int) : MarkerView(context, layoutResource) {

    private var markerText: TextView = findViewById(R.id.marker_text)

    override fun draw(canvas: Canvas?) {
        canvas!!.translate(-(width / 2).toFloat(), -height.toFloat())

        super.draw(canvas)
    }

    override fun refreshContent(e: Entry?, highlight: Highlight?) {
        markerText.text = context.getString(R.string.barchart_marker_text, e?.y?.toInt())

        markerText.background.colorFilter = BlendModeColorFilterCompat.createBlendModeColorFilterCompat(color, BlendModeCompat.SRC_ATOP)

        super.refreshContent(e, highlight)
    }
}