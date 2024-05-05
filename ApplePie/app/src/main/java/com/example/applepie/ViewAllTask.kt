package com.example.applepie

import android.graphics.Typeface
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.Task
import com.example.applepie.model.TaskList
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.firstDayOfWeekFromLocale
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.YearMonth
import java.util.Calendar
import java.util.Date

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewAllTask.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewAllTask : Fragment() {
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
        val rootView = inflater.inflate(R.layout.fragment_view_all_task, container, false)

        lists = FirebaseManager.getUserList()?: listOf()
        tasksList = FirebaseManager.getUserTask()?: listOf()
        originalTasksList = tasksList

        taskRecyclerView = rootView.findViewById(R.id.exFiveRv)

        var tempTaskList: List<Task> = emptyList()
        adapter = TaskCalendarAdapter(requireContext(), tempTaskList, lists)

        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskRecyclerView.adapter = adapter

        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)

        configureBinders(rootView, daysOfWeek)

        val calendarView = rootView.findViewById<com.kizitonwose.calendar.view.CalendarView>(R.id.exFiveCalendar)
        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        calendarView.monthScrollListener = { month ->
            rootView.findViewById<TextView>(R.id.exFiveMonthYearText).text = month.yearMonth.displayText()

            selectedDate?.let {
                // Clear selection if we scroll to a new month.
                selectedDate = null
                calendarView.notifyDateChanged(it)
                updateAdapterForDate(null)
            }
        }

        val nextMonthButton = rootView.findViewById<ImageView>(R.id.exFiveNextMonthImage)
        nextMonthButton.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        val previousMonthButton = rootView.findViewById<ImageView>(R.id.exFivePreviousMonthImage)
        previousMonthButton.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }
        return rootView
    }

    private fun updateAdapterForDate(date: LocalDate?) {
        Log.d("date: ", date.toString())
        val newTaskList = originalTasksList.filter { task ->
            val taskDueDate = LocalDate.parse(task.due_datetime.substring(0, 10))
            taskDueDate == date
        }.sortedWith(compareByDescending<Task> { it.due_datetime.substring(11) })
        Log.d("newTaskList: ", newTaskList.toString())
        adapter.updateData(newTaskList)
    }

    private fun getPriorityColor(priority: String): Int {
        return when (priority) {
            "none" -> R.color.none_color
            "low" -> R.color.low_color
            "medium" -> R.color.medium_color
            "high" -> R.color.high_color
            else -> R.color.none_color
        }
    }
    private fun configureBinders(rootView: View, daysOfWeek: List<DayOfWeek>) {
        val calendarView = rootView.findViewById<com.kizitonwose.calendar.view.CalendarView>(R.id.exFiveCalendar)
        // Container for each day view in the calendar
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = view.findViewById<TextView>(R.id.exFiveDayText)
            val layout = view.findViewById<View>(R.id.exFiveDayLayout)
            init {
                view.setOnClickListener {
                    if (day.position == DayPosition.MonthDate) {
                        //Log.d("Click: ", selectedDate.toString())
                        if (selectedDate != day.date) {
                            val oldDate = selectedDate
                            selectedDate = day.date
                            //Log.d("Click after: ", selectedDate.toString())
                            updateAdapterForDate(day.date)
                            calendarView.notifyDateChanged(day.date)
                            // You can notify the CalendarView to update here if needed
                            oldDate?.let {
                                // Notify the CalendarView to update the old date if needed
                                calendarView.notifyDateChanged(it)
                            }
                        }
                    }
                }
            }
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val context = container.textView.context
                container.textView.text = data.date.dayOfMonth.toString()

                val TopView = container.layout.findViewById<View>(R.id.exFiveDayTop)
                val BottomView = container.layout.findViewById<View>(R.id.exFiveDayBottom)

                TopView.background = null
                BottomView.background = null

                if (data.position == DayPosition.MonthDate) {
                    //Log.d("selectedDate: ", selectedDate.toString())

                    container.textView.setTextColorRes(R.color.black)
                    container.layout.setBackgroundResource(if (selectedDate == data.date) R.drawable.select_bg else 0)

                    tasksList = originalTasksList.filter { task ->
                        val taskDueDate = LocalDate.parse(task.due_datetime.substring(0, 10))
                        taskDueDate == data.date
                    }.sortedWith(compareByDescending<Task> { it.due_datetime.substring(11) })

                    //Log.d("tasksList: ", tasksList.toString())
                    if (tasksList.isNotEmpty()) {
//                        adapter = TaskCalendarAdapter(requireContext(), tasksList, lists)
//                        taskRecyclerView.adapter = adapter
                        //adapter.updateData(tasksList)
                        //taskRecyclerView.adapter = adapter
                        val priorityColor = getPriorityColor(tasksList[0].priority)
                        if (tasksList.size == 1) {
                            BottomView.setBackgroundColor(context.getColorCompat(priorityColor))
                        } else {
                            val priorityColor_1 = getPriorityColor(tasksList[1].priority)
                            BottomView.setBackgroundColor(context.getColorCompat(priorityColor_1))
                            TopView.setBackgroundColor(context.getColorCompat(priorityColor))
                        }
                    }
                } else {
                    container.textView.setTextColorRes(R.color.grey)
                    container.layout.background = null
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = view.findViewById<View>(R.id.legendLayout)
        }

        val typeFace = Typeface.create("sans-serif", Typeface.BOLD)
        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)

            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                val containerLayout = container.legendLayout as ViewGroup
                if (containerLayout.tag == null) {
                    containerLayout.tag = data.yearMonth
                    for (index in 0 until containerLayout.childCount) {
                        val child = containerLayout.getChildAt(index)
                        if (child is TextView) {
                            child.text = daysOfWeek.getOrNull(index)?.displayText(uppercase = true) ?: ""
                            child.setTextColorRes(R.color.green)
                            child.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                            child.typeface = typeFace
                        }
                    }
                }
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ViewAllTask.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewAllTask().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var adapter: TaskCalendarAdapter
    private lateinit var tasksList: List<Task>
    private lateinit var lists: List<TaskList>
    private var selectedDate: LocalDate? = null
    private lateinit var originalTasksList: List<Task>
}