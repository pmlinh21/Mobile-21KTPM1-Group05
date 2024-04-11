package com.example.applepie

import android.graphics.Color
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.model.Task
import com.example.applepie.model.TaskList
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
 * Use the [MonthlyReport.newInstance] factory method to
 * create an instance of this fragment.
 */
class MonthlyReport : Fragment() {
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
        val rootView = inflater.inflate(R.layout.fragment_monthly_report, container, false)

        taskRecyclerView = rootView.findViewById(R.id.recyclerView)
        adapter = TaskListAdapter(requireContext(), tasksList, lists)

        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskRecyclerView.adapter = adapter

        lists.add(TaskList(1, 0, "", "Mobile"))
        lists.add(TaskList(2, 0, "", "SoftwareDesign"))
        lists.add(TaskList(3, 0, "", "IELTS"))
        lists.add(TaskList(4, 0, "", "ML"))

        //W1
        tasksList.add(Task("", "10:00 PM Week1", 1, 45, false, "", "", "Project proposal"))
        tasksList.add(Task("", "9:00 PM Week1", 1, 44, false, "", "", "Writing report"))
        tasksList.add(Task("", "7:00 PM Week1", 3, 43, false, "", "", "IELTS Writing"))
        tasksList.add(Task("", "5:00 PM Week1", 4, 42, true, "", "", "Exercise4"))
        tasksList.add(Task("", "11:00 AM Week1", 1, 41, true, "", "", "Fix errors"))
        tasksList.add(Task("", "10:00 AM Week1", 1, 40, false, "", "", "Handle signup logic"))
        tasksList.add(Task("", "10:00 AM Week1", 1, 39, false, "", "", "Handle login logic"))
        tasksList.add(Task("", "10:00 AM Week1", 1, 38, true, "", "", "Design signup UI"))
        tasksList.add(Task("", "10:00 AM Week1", 1, 37, true, "", "", "Design login UI"))
        tasksList.add(Task("", "10:00 AM Week1", 1, 36, true, "", "", "Design homepage UI"))

        // W2
        tasksList.add(Task("", "11:59 PM Week2", 1, 35, true, "", "", "W01 - Kotlin"))
        tasksList.add(Task("", "10:00 PM Week2", 1, 34, true, "", "", "UI Learning"))
        tasksList.add(Task("", "10:00 PM Week2", 1, 33, true, "", "", "Menu demo"))
        tasksList.add(Task("", "07:00 PM Week2", 1, 32, true, "", "", "Action bar"))
        tasksList.add(Task("", "12:00 AM Week2", 1, 31, true, "", "", "Activity learning"))
        tasksList.add(Task("", "12:00 AM Week2", 1, 30, true, "", "", "Fragment learning"))

        tasksList.add(Task("", "11:59 PM Week2", 1, 29, true,"", "", "W03 - UI + Auto layout"))
        tasksList.add(Task("", "11:59 AM Week2", 3, 28, true,"", "", "IELTS Writing"))
        tasksList.add(Task("", "11:59 AM Week2", 3, 27, false,"", "", "IELTS Speaking"))

        // W3
        tasksList.add(Task("", "11:59 PM Week3", 3, 26, true,"", "", "IELTS Listening ex1"))
        tasksList.add(Task("", "11:59 PM Week3", 3, 25, true,"", "", "IELTS Listening ex2"))
        tasksList.add(Task("", "11:59 PM Week3", 3, 24, true,"", "", "IELTS Listening ex3"))
        tasksList.add(Task("", "11:59 PM Week3", 3, 23, true,"", "", "IELTS Listening ex4"))
        tasksList.add(Task("", "11:59 PM Week3", 3, 22, true,"", "", "IELTS Reading ex1"))
        tasksList.add(Task("", "11:59 PM Week3", 3, 21, true,"", "", "IELTS Reading ex2"))
        tasksList.add(Task("", "11:59 PM Week3", 3, 20, true,"", "", "IELTS Reading ex3"))

        tasksList.add(Task("", "11:59 PM Week3", 3, 19, true,"", "", "IELTS Speaking ex1"))
        tasksList.add(Task("", "11:59 PM Week3", 3, 18, true,"", "", "IELTS Speaking ex2"))

        // W4
        tasksList.add(Task("", "10:00 PM Week4", 4, 17, true,"", "", "Exercise3"))
        tasksList.add(Task("", "10:00 PM Week4", 4, 16, true,"", "", "Homework3"))
        tasksList.add(Task("", "10:00 PM Week4", 2, 15, true,"", "", "Report SD1"))
        tasksList.add(Task("", "10:00 PM Week4", 2, 14, true,"", "", "Report SD2"))
        tasksList.add(Task("", "10:00 PM Week4", 2, 13, false,"", "", "Report SD3"))
        tasksList.add(Task("", "10:00 PM Week4", 2, 12, true,"", "", "Design Layout"))
        tasksList.add(Task("", "10:00 PM Week4", 2, 11, true,"", "", "Design Layout"))
        tasksList.add(Task("", "10:00 PM Week4", 2, 10, true,"", "", "Design Layout"))
        tasksList.add(Task("", "10:00 PM Week4", 2, 9, true,"", "", "Design Layout"))
        tasksList.add(Task("", "10:00 PM Week4", 2, 8, false,"", "", "Design Layout"))
        tasksList.add(Task("", "10:00 PM Week4", 2, 7, true,"", "", "Design Layout"))
        tasksList.add(Task("", "10:00 PM Week4", 2, 6, true,"", "", "Design Layout"))

        tasksList.add(Task("", "10:00 PM Week4", 2, 5, true,"", "", "Design Layout"))
        tasksList.add(Task("", "9:00 PM Week4", 2, 4, false,"", "", "Handle login with google function"))
        tasksList.add(Task("", "9:00 PM Week4", 2, 3, true,"", "", "Handle signup function"))
        tasksList.add(Task("", "9:00 PM Week4", 2, 2, true,"", "", "Handle login function"))
        tasksList.add(Task("", "9:00 PM Week4", 2, 1, true,"", "", "Handle popup function"))

        val barChart: BarChart = rootView.findViewById(R.id.barChart)
        barChart.axisRight.setDrawLabels(false)

        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)
        barChart.xAxis.setDrawGridLines(false)

        val taskCountByWeek = mutableMapOf<String, Float>()

        for (week in xValues) {
            taskCountByWeek[week] = 0f
        }

        // Đếm số lượng task cho mỗi tuần
        for (task in tasksList) {
            val dueDateTime = task.due_datetime
            for (week in xValues) {
                if (dueDateTime.contains(week)) {
                    taskCountByWeek[week] = taskCountByWeek.getValue(week) + 1
                }
            }
        }

        // Tạo danh sách các BarEntry từ số lượng task theo ngày
        val entries = ArrayList<BarEntry>()
        for ((index, week) in xValues.withIndex()) {
            val taskCount = taskCountByWeek.getValue(week)
            entries.add(BarEntry(index.toFloat(), taskCount))
        }

        barChart.xAxis.textSize = 14f
        barChart.axisLeft.textSize = 13f


        val yAxis: YAxis = barChart.axisLeft
        yAxis.setDrawGridLines(false)
        yAxis.setDrawAxisLine(false)
        yAxis.axisMaximum = 0f
        yAxis.axisMaximum = 30f
        yAxis.setLabelCount(8)
        yAxis.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })

        val dataSet = BarDataSet(entries, "Week of month")

        val colorsList = listOf(Color.parseColor("#319F43"), Color.parseColor("#C6E9C7"))
        dataSet.colors = colorsList

        val barData = BarData(dataSet)
        barData.barWidth = 0.5f
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

        val barChartDone: BarChart = rootView.findViewById(R.id.barChart_1)
        barChartDone.axisRight.setDrawLabels(false)

        barChartDone.axisLeft.setDrawGridLines(false)
        barChartDone.axisRight.setDrawGridLines(false)
        barChartDone.xAxis.setDrawGridLines(false)

        val doneTaskCountByWeek = mutableMapOf<String, Float>()

        for (week in xValues) {
            doneTaskCountByWeek[week] = 0f
        }

        // Đếm số lượng task done cho mỗi week
        for (task in tasksList) {
            val dueDateTime = task.due_datetime
            for (week in xValues) {
                if (dueDateTime.contains(week)) {
                    if (task.isDone) {
                        doneTaskCountByWeek[week] = doneTaskCountByWeek.getValue(week) + 1
                    }
                }
            }
        }

        // Tạo danh sách các BarEntry từ số lượng task done theo week
        val percentages = mutableListOf<Float>()
        for (week in xValues) {
            val doneTaskCount = doneTaskCountByWeek.getValue(week)
            val totalTaskCount = taskCountByWeek.getValue(week)
            val percentage = if (totalTaskCount != 0f) {
                (doneTaskCount / totalTaskCount) * 100
            } else {
                0f
            }
            percentages.add(percentage)
        }

        val entriesDone = ArrayList<BarEntry>()
        for ((index, percentage) in percentages.withIndex()) {
            entriesDone.add(BarEntry(index.toFloat(), percentage))
        }

        barChartDone.xAxis.textSize = 14f
        barChartDone.axisLeft.textSize = 13f

        val yAxis_1: YAxis = barChartDone.axisLeft
        yAxis_1.setDrawGridLines(false)
        yAxis_1.setDrawAxisLine(false)
        yAxis_1.axisMaximum = 0f
        yAxis_1.axisMaximum = 100f
        yAxis_1.setLabelCount(5)
        yAxis_1.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })

        val dataSetDone = BarDataSet(entriesDone, "Completed Tasks")

        val colorsListDone = listOf(Color.parseColor("#C6E9C7"), Color.parseColor("#C6E9C7"))
        dataSetDone.colors = colorsListDone

        val barDataDone = BarData(dataSetDone)
        barDataDone.barWidth = 0.5f
        barChartDone.data = barDataDone
        barDataDone.setValueTextSize(12f)

        barDataDone.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() + "%"
            }
        })

        barChartDone.description.isEnabled = false
        barChartDone.legend.isEnabled = false;
        barChartDone.invalidate()

        barChartDone.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        barChartDone.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChartDone.xAxis.granularity = 1f
        barChartDone.xAxis.isGranularityEnabled = true

        barChartDone.renderer = RoundedBarChart(barChartDone, barChartDone.animator, barChartDone.viewPortHandler)

        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment MonthlyReport.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            MonthlyReport().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var adapter: TaskListAdapter
    private val tasksList = ArrayList<Task>()
    private val lists = ArrayList<TaskList>()
    private val xValues = listOf("Week1", "Week2", "Week3", "Week4")
}