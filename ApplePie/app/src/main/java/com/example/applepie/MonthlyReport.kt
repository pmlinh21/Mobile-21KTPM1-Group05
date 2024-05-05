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
import java.util.Calendar
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

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

        val lists = FirebaseManager.getUserList()?: listOf()
        var tasksList = FirebaseManager.getUserTask()?: listOf()

        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val appDate = sdf.format(currentTime)

        // Lấy những task chưa quá hạn trong tháng
        val calendar = Calendar.getInstance()
        calendar.time = sdf.parse(appDate)
        calendar.set(Calendar.DAY_OF_MONTH, 1)

        val firstDayOfMonth = calendar.time

        calendar.add(Calendar.MONTH, 1)
        calendar.add(Calendar.DAY_OF_MONTH, -1)
        val lastDayOfMonth = calendar.time

        tasksList = tasksList.filter { task ->
            val taskDueDate = sdf.parse(task.due_datetime)
            // Kiểm tra xem due_datetime của task có nằm trong tháng không
            taskDueDate in firstDayOfMonth..lastDayOfMonth
        }.sortedWith(compareByDescending<Task> { task ->
            task.due_datetime.substring(0, 10)
        }.thenByDescending { task ->
            task.due_datetime.substring(11)
        })

        originTaskList = tasksList

        taskText = rootView.findViewById(R.id.task_text)
        if (tasksList.isEmpty()) {
            taskText.text = "There are no tasks for this month"
            taskRecyclerView.visibility = View.GONE
        } else {
            taskText.visibility = View.GONE
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

        val taskCountByWeek = mutableMapOf<Int, Float>()
        for (i in 0 until 4) {
            taskCountByWeek[i] = 0f
        }
        // Lấy ngày đầu của tuần đầu tiên
        calendar.time = firstDayOfMonth
        val firstWeekFirstDay = calendar.time

        // Tìm ngày cuối của tuần đầu tiên
        calendar.add(Calendar.DAY_OF_MONTH, 6)
        val firstWeekLastDay = calendar.time

        // Lấy ngày đầu của tuần thứ hai
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val secondWeekFirstDay = calendar.time
        //Log.d("secondWeekFirstDay: ", secondWeekFirstDay.toString())

        // Tìm ngày cuối của tuần thứ hai
        calendar.add(Calendar.DAY_OF_MONTH, 6)
        val secondWeekLastDay = calendar.time
        //Log.d("secondWeekLastDay: ", secondWeekLastDay.toString())

        // Lấy ngày đầu của tuần thứ ba
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val thirdWeekFirstDay = calendar.time
        //Log.d("thirdWeekFirstDay: ", thirdWeekFirstDay.toString())

        // Tìm ngày cuối của tuần thứ ba
        calendar.add(Calendar.DAY_OF_MONTH, 6)
        val thirdWeekLastDay = calendar.time
        //Log.d("thirdWeekLastDay: ", thirdWeekLastDay.toString())

        // Lấy ngày đầu của tuần thứ tư
        calendar.add(Calendar.DAY_OF_MONTH, 1)
        val fourthWeekFirstDay = calendar.time
        //Log.d("fourthWeekFirstDay: ", fourthWeekFirstDay.toString())

        // Đếm số lượng task cho mỗi tuần
        for (task in tasksList) {
            val dueDateTime = sdf.parse(task.due_datetime.substring(0, 10)) ?: continue
            //Log.d("dueDateTime: ", dueDateTime.toString())

            if (dueDateTime in firstWeekFirstDay..firstWeekLastDay) {
                taskCountByWeek[0] = (taskCountByWeek[0] ?: 0f) + 1
            } else if (dueDateTime in secondWeekFirstDay..secondWeekLastDay) {
                taskCountByWeek[1] = (taskCountByWeek[1] ?: 0f) + 1
            } else if (dueDateTime in thirdWeekFirstDay..thirdWeekLastDay) {
                taskCountByWeek[2] = (taskCountByWeek[2] ?: 0f) + 1
            } else {
                taskCountByWeek[3] = (taskCountByWeek[3] ?: 0f) + 1
            }
        }
        //Log.d("taskCountByWeek: ", taskCountByWeek.toString())

        // Tạo danh sách các BarEntry từ số lượng task theo ngày
        val entries = ArrayList<BarEntry>()
        for ((week, taskCount) in taskCountByWeek) {
            entries.add(BarEntry(week.toFloat(), taskCount))
        }

        //Log.d("entries: ", entries.toString())

        barChart.xAxis.textSize = 14f
        barChart.axisLeft.textSize = 13f

        val maxTaskCount = taskCountByWeek.values.maxOrNull() ?: 0f

        val yAxis: YAxis = barChart.axisLeft
        yAxis.setDrawGridLines(false)
        yAxis.setDrawAxisLine(false)

        yAxis.axisMinimum = 0f
        yAxis.axisMaximum = maxTaskCount + 4f

        val maxLabelCount = if (yAxis.axisMaximum < 8) 3 else 6

        yAxis.setLabelCount(maxLabelCount)

        yAxis.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })

        val dataSet = BarDataSet(entries, "Week of month")

        //val colorsList = listOf(Color.parseColor("#319F43"), Color.parseColor("#C6E9C7"))
        //dataSet.colors = colorsList

        val colorsList = mutableListOf<Int>()
        for (taskCount in taskCountByWeek) {
            val color = if (taskCount.value >= 20f) {
                Color.parseColor("#319F43")
            } else {
                Color.parseColor("#C6E9C7")
            }
            colorsList.add(color)
        }
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

                var tasksForSelectedWeek: List<Task>
                if(index == 0){
                    tasksForSelectedWeek = tasksList.filter { task ->
                        val taskDueDate = sdf.parse(task.due_datetime.substring(0, 10)) ?: return@filter false
                        taskDueDate in firstWeekFirstDay..firstWeekLastDay
                    }
                }
                else if(index == 1){
                    tasksForSelectedWeek = tasksList.filter { task ->
                        val taskDueDate = sdf.parse(task.due_datetime.substring(0, 10)) ?: return@filter false
                        taskDueDate in secondWeekFirstDay..secondWeekLastDay
                    }
                }
                else if(index == 2){
                    tasksForSelectedWeek = tasksList.filter { task ->
                        val taskDueDate = sdf.parse(task.due_datetime.substring(0, 10)) ?: return@filter false
                        taskDueDate in thirdWeekFirstDay..thirdWeekLastDay
                    }
                }
                else{
                    tasksForSelectedWeek = tasksList.filter { task ->
                        val taskDueDate = sdf.parse(task.due_datetime.substring(0, 10)) ?: return@filter false
                        taskDueDate in fourthWeekFirstDay..lastDayOfMonth
                    }
                }
                Log.d("tasksForSelectedWeek: ", tasksForSelectedWeek.toString())

                updateRecyclerView(tasksForSelectedWeek)
            }

            override fun onNothingSelected() {
                // Handle case when nothing is selected, if needed
            }
        })

        // Bar chart for % done
        val barChartDone: BarChart = rootView.findViewById(R.id.barChart_1)
        barChartDone.axisRight.setDrawLabels(false)

        barChartDone.axisLeft.setDrawGridLines(false)
        barChartDone.axisRight.setDrawGridLines(false)
        barChartDone.xAxis.setDrawGridLines(false)

        val doneTaskCountByWeek = mutableMapOf<Int, Float>()

        for (i in 0 until 4) {
            doneTaskCountByWeek[i] = 0f
        }

        // Đếm số lượng task done cho mỗi week
        for (task in tasksList) {
            val dueDateTime = sdf.parse(task.due_datetime.substring(0, 10)) ?: continue
            //Log.d("task: ", task.toString())

            if (dueDateTime in firstWeekFirstDay..firstWeekLastDay) {
                if(task.isDone)
                    doneTaskCountByWeek[0] = (doneTaskCountByWeek[0] ?: 0f) + 1
            } else if (dueDateTime in secondWeekFirstDay..secondWeekLastDay) {
                if(task.isDone)
                    doneTaskCountByWeek[1] = (doneTaskCountByWeek[1] ?: 0f) + 1
            } else if (dueDateTime in thirdWeekFirstDay..thirdWeekLastDay) {
                if(task.isDone)
                    doneTaskCountByWeek[2] = (doneTaskCountByWeek[2] ?: 0f) + 1
            } else {
                if(task.isDone)
                    doneTaskCountByWeek[3] = (doneTaskCountByWeek[3] ?: 0f) + 1
            }
        }
        //Log.d("doneTaskCountByWeek: ", doneTaskCountByWeek.toString())

        // Tạo danh sách các BarEntry từ số lượng task done theo week
        val percentages = mutableListOf<Float>()
        for (i in 0 until 4) {
            val doneTaskCount = doneTaskCountByWeek[i] ?: 0f
            val totalTaskCount = taskCountByWeek[i] ?: 0f
            val percentage = if (totalTaskCount != 0f) {
                doneTaskCount / totalTaskCount * 100
            } else {
                0f // Để tránh chia cho 0
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
        yAxis_1.axisMinimum = 0f
        yAxis_1.axisMaximum = 100f
        yAxis_1.setLabelCount(5)
        yAxis_1.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString()
            }
        })

        val dataSetDone = BarDataSet(entriesDone, "Completed Tasks")

        val colorsListDone = mutableListOf<Int>()
        for (percentage in percentages) {
            val color = if (percentage >= 75f) {
                Color.parseColor("#319F43")
            } else {
                Color.parseColor("#C6E9C7")
            }
            colorsListDone.add(color)
        }
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

        barChartDone.setExtraOffsets(0f,0f,0f,15f)

        barChartDone.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        barChartDone.xAxis.position = XAxis.XAxisPosition.BOTTOM
        barChartDone.xAxis.granularity = 1f
        barChartDone.xAxis.isGranularityEnabled = true

        barChartDone.renderer = RoundedBarChart(barChartDone, barChartDone.animator, barChartDone.viewPortHandler)

        viewAllButton = rootView.findViewById(R.id.task_all)
        viewAllButton.setOnClickListener {
            adapter.updateData(originTaskList)
            if (originTaskList.isEmpty()) {
                taskText.text = "There are no tasks for this month"
                taskText.visibility = View.VISIBLE
            } else {
                taskText.visibility = View.GONE
            }
        }

        // study time
        val pomodoroTimeByDay = mutableMapOf<Int, Long>()
        val stopwatchTimeByDay = mutableMapOf<Int, Long>()

        for (i in 0 until 4) {
            pomodoroTimeByDay[i] = 0L
            stopwatchTimeByDay[i] = 0L
        }

        val pomodoroTimes = FirebaseManager.getUserPomodoro() ?: listOf()
        val stopwatchTimes = FirebaseManager.getUserStopwatch() ?: listOf()

        val timeBarChart: BarChart = rootView.findViewById(R.id.timeBarChart)

        timeText = rootView.findViewById(R.id.yAxisLabel_2)
        if (pomodoroTimes.isEmpty() && stopwatchTimes.isEmpty()) {
            timeText.text = "Start a Pomodoro or Stopwatch session to see your progress here"
            timeBarChart.visibility = View.GONE
        } else {
            timeText.visibility = View.GONE
        }

        Log.d("firstWeekFirstDay: ", firstWeekFirstDay.toString())
        Log.d("firstWeekLastDay: ", firstWeekLastDay.toString())

        val sdf_1 = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val dateTimeFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

        for (pomodoro in pomodoroTimes) {
            val startDate = dateTimeFormat.parse(pomodoro.start_time)
            val endDate = dateTimeFormat.parse(pomodoro.end_time)
            Log.d("startDate: ", startDate.toString())

            if(startDate in firstWeekFirstDay..firstWeekLastDay){
                val durationInSeconds = (endDate.time - startDate.time) / 1000
                pomodoroTimeByDay[0] = pomodoroTimeByDay.getOrDefault(0, 0L) + durationInSeconds
            }
            else if(startDate in secondWeekFirstDay..secondWeekLastDay){
                val durationInSeconds = (endDate.time - startDate.time) / 1000
                pomodoroTimeByDay[1] = pomodoroTimeByDay.getOrDefault(1, 0L) + durationInSeconds
            }
            else if(startDate in thirdWeekFirstDay..thirdWeekLastDay){
                val durationInSeconds = (endDate.time - startDate.time) / 1000
                pomodoroTimeByDay[1] = pomodoroTimeByDay.getOrDefault(1, 0L) + durationInSeconds
            }
            else {
                val durationInSeconds = (endDate.time - startDate.time) / 1000
                pomodoroTimeByDay[1] = pomodoroTimeByDay.getOrDefault(1, 0L) + durationInSeconds
            }
        }
        Log.d("pomodoroTimeByDay: ", pomodoroTimeByDay.toString())


        for (stopwatch in stopwatchTimes) {
            val startDate = dateTimeFormat.parse(stopwatch.start_time)
            val endDate = dateTimeFormat.parse(stopwatch.end_time)

            if(startDate in firstWeekFirstDay..firstWeekLastDay){
                val durationInSeconds = (endDate.time - startDate.time) / 1000
                stopwatchTimeByDay[0] = stopwatchTimeByDay.getOrDefault(0, 0L) + durationInSeconds
            }
            else if(startDate in secondWeekFirstDay..secondWeekLastDay){
                val durationInSeconds = (endDate.time - startDate.time) / 1000
                stopwatchTimeByDay[1] = stopwatchTimeByDay.getOrDefault(1, 0L) + durationInSeconds
            }
            else if(startDate in thirdWeekFirstDay..thirdWeekLastDay){
                val durationInSeconds = (endDate.time - startDate.time) / 1000
                stopwatchTimeByDay[1] = stopwatchTimeByDay.getOrDefault(1, 0L) + durationInSeconds
            }
            else {
                val durationInSeconds = (endDate.time - startDate.time) / 1000
                stopwatchTimeByDay[1] = stopwatchTimeByDay.getOrDefault(1, 0L) + durationInSeconds
            }

        }
        Log.d("stopwatchTimeByDay: ", stopwatchTimeByDay.toString())

        val pomodoroEntries = ArrayList<BarEntry>()
        val stopwatchEntries = ArrayList<BarEntry>()

        for ((index, timeInSeconds) in pomodoroTimeByDay) {
            pomodoroEntries.add(BarEntry(index.toFloat(), timeInSeconds.toFloat()))
        }

        for ((index, timeInSeconds) in stopwatchTimeByDay) {
            stopwatchEntries.add(BarEntry(index.toFloat() + 0.4f, timeInSeconds.toFloat()))
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

        //val maxLabelCount_1 = if (yAxis_2.axisMaximum < 5000) 5 else

        yAxis_2.setLabelCount(5)

//        yAxis_2.setValueFormatter(object : ValueFormatter() {
//            override fun getFormattedValue(value: Float): String {
//                val totalSeconds = value.toInt()
//                val hours = totalSeconds / 3600
//                val remainingMinutes = (totalSeconds % 3600) / 60
//
//                return if (hours > 0) {
//                    "${hours}h"
//                } else {
//                    "${remainingMinutes}m"
//                }
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
        timeBarChart.groupBars(-0.5f, 0.1f, 0.1f)

        timeBarChart.xAxis.valueFormatter = IndexAxisValueFormatter(xValues)
        timeBarChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        timeBarChart.xAxis.granularity = 1.15f
        timeBarChart.xAxis.isGranularityEnabled = true
        timeBarChart.invalidate()

        return rootView
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
    private lateinit var taskText: TextView
    private lateinit var viewAllButton: Button
    private lateinit var originTaskList: List<Task>
    private val xValues = listOf("Week1", "Week2", "Week3", "Week4")
    private  lateinit var timeText: TextView
}