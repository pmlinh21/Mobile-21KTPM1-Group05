package com.example.applepie

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import java.text.SimpleDateFormat
import java.util.Calendar

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

        val lists = FirebaseManager.getUserList()?: listOf()
        var tasksList = FirebaseManager.getUserTask()?: listOf()

        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val appDate = sdf.format(currentTime)

        // Lấy những task chưa quá hạn trong tuần
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(appDate)

        calendar.setFirstDayOfWeek(Calendar.MONDAY)

        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val firstDayOfWeek = calendar.time

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val lastDayOfWeek = calendar.time

        tasksList = tasksList.filter { task ->
            val taskDueDate = sdf.parse(task.due_datetime)
            // Kiểm tra xem due_datetime của task có nằm trong tuần không
            taskDueDate in firstDayOfWeek..lastDayOfWeek
        }.sortedByDescending { task ->
            sdf.parse(task.due_datetime)
        }

        taskRecyclerView = rootView.findViewById(R.id.recyclerView)
        adapter = TaskListAdapter(requireContext(), tasksList, lists)

        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskRecyclerView.adapter = adapter

        val barChart: BarChart = rootView.findViewById(R.id.barChart)
        barChart.axisRight.setDrawLabels(false)

        barChart.axisLeft.setDrawGridLines(false)
        barChart.axisRight.setDrawGridLines(false)
        barChart.xAxis.setDrawGridLines(false)

        val taskCountByDay = mutableMapOf<String, Float>()

        val daysOfWeek = mutableListOf<String>()
        calendar.time = firstDayOfWeek
        while (calendar.time <= lastDayOfWeek) {
            daysOfWeek.add(sdf.format(calendar.time))
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }

        for (day in daysOfWeek) {
            taskCountByDay[day] = 0f
        }

        // Đếm số lượng task cho mỗi ngày
        for (task in tasksList) {
            val dueDateTime = task.due_datetime.substring(0, 10)
            for (day in daysOfWeek) {
                if (dueDateTime == day) {
                    taskCountByDay[day] = taskCountByDay.getValue(day) + 1
                }
            }
        }

        // Tạo danh sách các BarEntry từ số lượng task theo ngày
        val entries = ArrayList<BarEntry>()
        for ((index, day) in daysOfWeek.withIndex()) {
            val taskCount = taskCountByDay.getValue(day)
            entries.add(BarEntry(index.toFloat(), taskCount))
        }

        barChart.xAxis.textSize = 13f
        barChart.axisLeft.textSize = 12f

        val yAxis: YAxis = barChart.axisLeft
        yAxis.setDrawGridLines(false)
        yAxis.setDrawAxisLine(false)
        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = 12f
        yAxis.setLabelCount(6)
        yAxis.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })

        val dataSet = BarDataSet(entries, "Day of the week")

        val colorsList = listOf(Color.parseColor("#319F43"), Color.parseColor("#C6E9C7"))
        dataSet.colors = colorsList

        val barData = BarData(dataSet)
        barData.barWidth = 0.6f
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

        val doneTaskCountByDay = mutableMapOf<String, Float>()

        for (day in daysOfWeek) {
            doneTaskCountByDay[day] = 0f
        }

        // Đếm số lượng task done cho mỗi ngày
        for (task in tasksList) {
            val dueDateTime = task.due_datetime.substring(0, 10)
            for (day in daysOfWeek) {
                if (dueDateTime == day) {
                    if (task.isDone) {
                        doneTaskCountByDay[day] = doneTaskCountByDay.getValue(day) + 1
                    }
                }
            }
        }

        // Tạo danh sách các BarEntry từ số lượng task done theo ngày
        val percentages = mutableListOf<Float>()
        for (day in daysOfWeek) {
            val doneTaskCount = doneTaskCountByDay.getValue(day)
            val totalTaskCount = taskCountByDay.getValue(day)
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

        barChartDone.xAxis.textSize = 13f
        barChartDone.axisLeft.textSize = 12f

        val yAxis_1: YAxis = barChartDone.axisLeft
        yAxis_1.setDrawGridLines(false)
        yAxis_1.setDrawAxisLine(false)
        yAxis_1.axisMinimum = 0f
        yAxis_1.axisMaximum = 100f
        yAxis_1.setLabelCount(10)
        yAxis_1.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })

        val dataSetDone = BarDataSet(entriesDone, "Completed Tasks")

        val colorsListDone = listOf(Color.parseColor("#C6E9C7"), Color.parseColor("#C6E9C7"))
        dataSetDone.colors = colorsListDone

        val barDataDone = BarData(dataSetDone)
        barDataDone.barWidth = 0.6f
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
//    private val tasksList = ArrayList<Task>()
//    private val lists = ArrayList<TaskList>()
    private val xValues = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
}

