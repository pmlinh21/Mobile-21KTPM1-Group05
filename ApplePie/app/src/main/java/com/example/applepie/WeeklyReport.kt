package com.example.applepie

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.Lists
import com.example.applepie.database.Task
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [WeeklyReport.newInstance] factory method to
 * create an instance of this fragment.
 */
class WeeklyReport : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_weekly_report, container, false)

        taskRecyclerView = rootView.findViewById(R.id.recyclerView)
        adapter = TaskListAdapter(requireContext(), tasksList, lists)

        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskRecyclerView.adapter = adapter

        lists.add(Lists(1, "", "", "Mobile"))
        lists.add(Lists(2, "", "", "SoftwareDesign"))

        tasksList.add(Task("", "10:00 AM Saturday", 1, 1, false, "", "", "Project Proposal"))
        tasksList.add(Task("", "10:00 PM Saturday", 1, 2, true, "", "", "W01 - Kotlin"))
        tasksList.add(Task("", "11:59 AM Friday", 1, 3, false,"", "", "W03 - UI + Auto layout"))
        tasksList.add(Task("", "8:00 PM Monday", 2, 3, false,"", "", "Design Layout"))
        tasksList.add(Task("", "9:00 PM Monday", 2, 3, true,"", "", "Handle login function"))

        val barChart: BarChart = rootView.findViewById(R.id.barChart)
        barChart.axisRight.setDrawLabels(false)

        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)
        barChart.xAxis.setDrawGridLines(false)

        val entries = ArrayList<BarEntry>()
        entries.add(BarEntry(0f, 10f))
        entries.add(BarEntry(1f, 14f))
        entries.add(BarEntry(2f, 8f))
        entries.add(BarEntry(3f, 18f))
        entries.add(BarEntry(4f, 10f))
        entries.add(BarEntry(5f, 8f))
        entries.add(BarEntry(6f, 6f))

        barChart.xAxis.textSize = 13f
        barChart.axisLeft.textSize = 12f


        val yAxis: YAxis = barChart.axisLeft
        yAxis.setDrawGridLines(false)
        yAxis.axisMaximum = 0f
        yAxis.axisMaximum = 20f
        yAxis.setLabelCount(10)
        yAxis.valueFormatter = IntValueFormatter()

        val dataSet = BarDataSet(entries, "Day of the week")

        val colorsList = listOf(Color.parseColor("#319F43"), Color.parseColor("#C6E9C7"))
        dataSet.colors = colorsList

        val barData = BarData(dataSet)
        barData.barWidth = 0.8f
        barChart.data = barData
        barData.setValueTextSize(12f)

        barData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })

        barChart.description.isEnabled = false
        barChart.legend.isEnabled = false;
        barChart.invalidate()

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f
        barChart.xAxis.isGranularityEnabled = true

        barChart.renderer = RoundedBarChart(barChart, barChart.animator, barChart.viewPortHandler)
        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment WeeklyReport.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            WeeklyReport().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var adapter: TaskListAdapter
    private val tasksList = ArrayList<Task>()
    private val lists = ArrayList<Lists>()
    private val xValues = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
}

class IntValueFormatter : ValueFormatter() {
    override fun getFormattedValue(value: Float): String {
        return value.toInt().toString()
    }
}