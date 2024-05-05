package com.example.applepie

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.Task
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.components.YAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter
import com.github.mikephil.charting.highlight.Highlight
import com.github.mikephil.charting.listener.OnChartValueSelectedListener
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

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
        return inflater.inflate(R.layout.fragment_weekly_report, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val lists = FirebaseManager.getUserList()?: listOf()
        var tasksList = FirebaseManager.getUserTask()?: listOf()

        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val appDate = sdf.format(currentTime)

        // Lấy những task chưa quá hạn trong tuần
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(appDate)!!

        calendar.firstDayOfWeek = Calendar.MONDAY

        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)
        val firstDayOfWeek = calendar.time

        calendar.add(Calendar.DAY_OF_WEEK, 6)
        val lastDayOfWeek = calendar.time

        tasksList = tasksList.filter { task ->
            val taskDueDate = sdf.parse(task.due_datetime)
            // Kiểm tra xem due_datetime của task có nằm trong tuần không
            taskDueDate in firstDayOfWeek..lastDayOfWeek
        }.sortedWith(compareByDescending<Task> { task ->
            task.due_datetime.substring(0, 10)
        }.thenByDescending { task ->
            task.due_datetime.substring(11)
        })

        originTaskList = tasksList
        Log.d("originTaskList", originTaskList.toString())
        taskText = view.findViewById(R.id.task_text)
        taskRecyclerView = view.findViewById(R.id.recyclerView)

        if (tasksList.isEmpty()) {
            taskText.text = "There are no tasks for this week"
            taskRecyclerView.visibility = View.INVISIBLE
        } else {
            taskText.visibility = View.GONE
        }

        adapter = TaskListAdapter(requireContext(), originTaskList, lists)

        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskRecyclerView.adapter = adapter

        val barChart: BarChart = view.findViewById(R.id.barChart)
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

        val maxTaskCount = taskCountByDay.values.maxOrNull() ?: 0f

        val yAxis: YAxis = barChart.axisLeft
        yAxis.setDrawGridLines(false)
        yAxis.setDrawAxisLine(false)

        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = maxTaskCount + 3f

        val maxLabelCount = if (yAxis.axisMaximum < 5) 3 else 5

        yAxis.setLabelCount(maxLabelCount)

        yAxis.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })

        val dataSet = BarDataSet(entries, "Day of the week")

        val colorsList = mutableListOf<Int>()
        for (taskCount in taskCountByDay) {
            val color = if (taskCount.value >= 4f) {
                Color.parseColor("#319F43")
            } else {
                Color.parseColor("#C6E9C7")
            }
            colorsList.add(color)
        }
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
        barChart.setExtraOffsets(0f,0f,0f,15f)

        barChart.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChart.xAxis.granularity = 1f
        barChart.xAxis.isGranularityEnabled = true

        barChart.renderer = RoundedBarChart(barChart, barChart.animator, barChart.viewPortHandler)

        barChart.setOnChartValueSelectedListener(object : OnChartValueSelectedListener {
            override fun onValueSelected(e: Entry?, h: Highlight?) {
                if (e == null) return

                val index = e.x.toInt()
                Log.d("index: ", index.toString())

                val calendar = Calendar.getInstance()
                calendar.time = firstDayOfWeek
                calendar.add(Calendar.DAY_OF_WEEK, index)

                val selectedDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(calendar.time)
                Log.d("selectedDate: ", selectedDate)

                val tasksForSelectedDay = tasksList.filter { task ->
                    task.due_datetime.startsWith(selectedDate)
                }
                Log.d("tasksForSelectedDay: ", tasksForSelectedDay.toString())
                updateRecyclerView(tasksForSelectedDay)
            }

            override fun onNothingSelected() {
                // Handle case when nothing is selected, if needed
            }
        })

        // Bar chart for %done
        val barChartDone: BarChart = view.findViewById(R.id.barChart_1)
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
        yAxis_1.setLabelCount(5)
        yAxis_1.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        }

        val dataSetDone = BarDataSet(entriesDone, "Completed Tasks")

        val colorsListDone = mutableListOf<Int>()
        for (percentage in percentages) {
            val color = if (percentage >= 75f) {
                Color.parseColor("#319F43") // Màu xanh lá cây nếu % done >= 75%
            } else {
                Color.parseColor("#C6E9C7") // Màu xanh nước biển nếu % done < 75%
            }
            colorsListDone.add(color)
        }
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

        barChartDone.setExtraOffsets(0f,0f,0f,15f)

        barChartDone.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        barChartDone.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChartDone.xAxis.granularity = 1f
        barChartDone.xAxis.isGranularityEnabled = true

        barChartDone.renderer = RoundedBarChart(barChartDone, barChartDone.animator, barChartDone.viewPortHandler)

        // View all button
        viewAllButton = view.findViewById(R.id.task_all)
        viewAllButton.setOnClickListener {
            adapter.updateData(originTaskList)
            if (originTaskList.isEmpty()) {
                taskText.text = "There are no tasks for this week"
                taskText.visibility = View.VISIBLE
            } else {
                taskText.visibility = View.GONE
            }
        }

        // Study time
        val pomodoroTimeByDay = mutableMapOf<String, Long>()
        val stopwatchTimeByDay = mutableMapOf<String, Long>()

        for (day in daysOfWeek) {
            pomodoroTimeByDay[day] = 0L
            stopwatchTimeByDay[day] = 0L
        }

        val pomodoroTimes = FirebaseManager.getUserPomodoro() ?: listOf()
        val stopwatchTimes = FirebaseManager.getUserStopwatch() ?: listOf()

        val timeBarChart: BarChart = view.findViewById(R.id.timeBarChart)

        timeText = view.findViewById(R.id.yAxisLabel_2)
        if (pomodoroTimes.isEmpty() && stopwatchTimes.isEmpty()) {
            timeText.text = "Start a Pomodoro or Stopwatch session to see your progress here"
            timeBarChart.visibility = View.GONE
        } else {
            timeText.visibility = View.GONE
        }
//        Log.d("firstDayOfWeek: ", firstDayOfWeek.toString())
//        Log.d("lastDayOfWeek: ", lastDayOfWeek.toString())

        val sdf_1 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        for (pomodoro in pomodoroTimes) {
            val startDate = dateTimeFormat.parse(pomodoro.start_time)
            val endDate = dateTimeFormat.parse(pomodoro.end_time)
            val startDay = sdf_1.format(startDate)

            for (day in daysOfWeek) {
                if (startDay == day) {
                    val durationInSeconds = (endDate.time - startDate.time) / 1000
                    pomodoroTimeByDay[day] = pomodoroTimeByDay.getOrDefault(day, 0L) + durationInSeconds
                }
            }
        }
        //Log.d("pomodoroTimeByDay: ", pomodoroTimeByDay.toString())


        for (stopwatch in stopwatchTimes) {
            val startDate = dateTimeFormat.parse(stopwatch.start_time)
            val endDate = dateTimeFormat.parse(stopwatch.end_time)
            val startDay = sdf_1.format(startDate)

            for (day in daysOfWeek) {
                if (startDay == day) {
                    val durationInSeconds = (endDate.time - startDate.time) / 1000
                    stopwatchTimeByDay[day] = stopwatchTimeByDay.getOrDefault(day, 0L) + durationInSeconds
                }
            }
        }
        //Log.d("stopwatchTimeByDay: ", stopwatchTimeByDay.toString())

        val pomodoroEntries = ArrayList<BarEntry>()
        val stopwatchEntries = ArrayList<BarEntry>()

        daysOfWeek.forEachIndexed { index, day ->
            pomodoroEntries.add(BarEntry(index.toFloat(), pomodoroTimeByDay[day]!!.toFloat()))
            stopwatchEntries.add(BarEntry(index.toFloat() + 0.4f, stopwatchTimeByDay[day]!!.toFloat()))
        }

        val pomodoroDataSet = BarDataSet(pomodoroEntries, "Pomodoro").apply {
            color = Color.parseColor("#319F43")
        }
        val stopwatchDataSet = BarDataSet(stopwatchEntries, "Stopwatch").apply {
            color = Color.parseColor("#C6E9C7")
        }

        val timeBarData = BarData(pomodoroDataSet, stopwatchDataSet)
        timeBarData.setValueTextSize(12f)
        timeBarData.barWidth = 0.4f

        timeBarData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                val totalSeconds = value.toInt()
                val hours = totalSeconds / 3600
                val minutes = (totalSeconds % 3600) / 60
                val seconds = totalSeconds % 60

                return when {
                    hours > 0 -> "${hours}h${minutes}m"  // Hiển thị giờ và phút nếu có giờ
                    minutes > 0 -> "${minutes}m${seconds}s"  // Hiển thị phút và giây nếu không có giờ nhưng có phút
                    else -> "${seconds}s"  // Hiển thị chỉ giây nếu thời gian dưới 1 phút
                }
            }
        })

        timeBarChart.axisRight.setDrawLabels(false)
        timeBarChart.axisLeft.setDrawLabels(false)
        timeBarChart.axisLeft.setDrawGridLines(false)
        timeBarChart.axisRight.setDrawGridLines(false)
        timeBarChart.xAxis.setDrawGridLines(false)

        timeBarChart.xAxis.textSize = 14f
        timeBarChart.axisLeft.textSize = 13f

        val yAxis_2: YAxis = timeBarChart.axisLeft
        yAxis_2.setDrawGridLines(false)
        yAxis_2.setDrawAxisLine(false)

        yAxis_2.axisMinimum = 0f
        //yAxis_2.axisMaximum = maxTime + 100f

        //val maxLabelCount_1 = if (yAxis_2.axisMaximum < 500) 5 else 8

        yAxis_2.setLabelCount(5)

//        yAxis_2.setValueFormatter(object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                return value.toInt().toString()
//            }
//        })

        timeBarChart.description.isEnabled = false
        timeBarChart.legend.isEnabled = true;
        timeBarChart.legend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        timeBarChart.legend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        timeBarChart.legend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        timeBarChart.legend.xEntrySpace = 30f
        timeBarChart.legend.textSize = 12f
        timeBarChart.legend.yOffset = 2f

        timeBarChart.setExtraOffsets(0f,5f,0f,15f)

        timeBarChart.data = timeBarData
        timeBarChart.groupBars(-0.5f, 0.15f, 0.05f)

        timeBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        timeBarChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        timeBarChart.xAxis.granularity = 1.05f
        timeBarChart.xAxis.isGranularityEnabled = true
        timeBarChart.invalidate()

    }

    private fun updateRecyclerView(tasks: List<Task>) {
        adapter.updateData(tasks)
        if (tasks.isEmpty()) {
            taskText.text = "There are no tasks for this day"
            taskText.visibility = View.VISIBLE
        } else {
            taskText.visibility = View.GONE
        }
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
    private lateinit var taskText: TextView
    private lateinit var viewAllButton: Button
    private lateinit var originTaskList: List<Task>
    private val xValues = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
    private  lateinit var timeText: TextView
}

