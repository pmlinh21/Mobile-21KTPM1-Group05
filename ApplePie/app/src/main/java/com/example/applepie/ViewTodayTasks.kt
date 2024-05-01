package com.example.applepie

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.Task
import com.example.applepie.model.TaskList
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewTodayTasks.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewTodayTasks : Fragment() {
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
        val rootView = inflater.inflate(R.layout.fragment_view_today_tasks, container, false)

        today = rootView.findViewById(R.id.today)

        val currentTime = Calendar.getInstance().time

        val sdf = SimpleDateFormat("MMMM d yyyy", Locale.ENGLISH)
        val formattedTime = sdf.format(currentTime)

        today.text = formattedTime

        val lists = FirebaseManager.getUserList()?: listOf()
        tasksList = FirebaseManager.getUserTask()?: listOf()
        originalTasksList = FirebaseManager.getUserTask()?: listOf()

        val sdf_1 = SimpleDateFormat("yyyy-MM-dd")
        val appDate = sdf_1.format(currentTime)

        var selectedDate = appDate

        // Lấy những task có due_datetime bằng today (task chưa quá hạn)
        tasksList = tasksList.filter { task ->
            val taskDueDate = task.due_datetime.split(" ")[0]
            (taskDueDate == appDate) ?: false
        }.sortedWith(compareByDescending<Task> { it.due_datetime.split(" ")[1] })

        taskText = rootView.findViewById(R.id.task_text)

        if (tasksList.isEmpty()) {
            taskText.text = "There are no tasks for today"
        } else {
            taskText.visibility = View.GONE
        }

        taskRecyclerView = rootView.findViewById(R.id.recyclerView)
        adapter = TaskListAdapter1(requireContext(), tasksList, lists)

        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskRecyclerView.adapter = adapter

        previousDateButton = rootView.findViewById(R.id.previous_date)
        nextDateButton = rootView.findViewById(R.id.next_date)
        nextDateButton.visibility = View.INVISIBLE

        previousDateButton.setOnClickListener {
            selectedDate = decreaseDate(selectedDate)

            val formattedSelectedDate = formatSelectedDate(selectedDate)
            today.text = formattedSelectedDate

            selectedDate = formatDate(selectedDate)

            updateTaskListForSelectedDate(selectedDate)
            nextDateButton.visibility = View.VISIBLE
        }

        nextDateButton.setOnClickListener {
            selectedDate = increaseDate(selectedDate)

            val formattedSelectedDate = formatSelectedDate(selectedDate)
            today.text = formattedSelectedDate

            selectedDate = formatDate(selectedDate)
            updateTaskListForSelectedDate(selectedDate)

            if (selectedDate == appDate) {
                nextDateButton.visibility = View.INVISIBLE
            } else {
                nextDateButton.visibility = View.VISIBLE
            }
        }
        return rootView
    }

    private fun formatDate(selectedDate: String): String {
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val date = sdf.parse(selectedDate)

        val formattedDate = SimpleDateFormat("yyyy-MM-dd", Locale.ENGLISH).format(date)
        return formattedDate
    }

    private fun formatSelectedDate(selectedDate: String): String {
        val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.ENGLISH)
        val date = sdf.parse(selectedDate)

        val formattedDate = SimpleDateFormat("MMM dd yyyy", Locale.ENGLISH).format(date)
        return formattedDate
    }

    private fun decreaseDate(selectedDate: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.parse(selectedDate)
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DATE, -1)
        val previousDate = calendar.time

        val formattedPreviousDate = SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH).format(previousDate)
        return formattedPreviousDate
    }

    private fun increaseDate(selectedDate: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val currentDate = sdf.parse(selectedDate)
        val calendar = Calendar.getInstance()
        calendar.time = currentDate
        calendar.add(Calendar.DATE, 1)
        val nextDate = calendar.time

        val formattedPreviousDate = SimpleDateFormat("EEE MMM d HH:mm:ss z yyyy", Locale.ENGLISH).format(nextDate)
        return formattedPreviousDate
    }

    private fun updateTaskListForSelectedDate(selectedDate: String) {
        val newTaskList = originalTasksList.filter { task ->
            val taskDueDate = task.due_datetime.substring(0, 10)
            taskDueDate == selectedDate
        }.sortedWith(compareByDescending<Task> { it.due_datetime.substring(11) })

        adapter.updateData(newTaskList)

        if (newTaskList.isEmpty()) {
            taskText.text = "There are no tasks for this date"
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
         * @return A new instance of fragment ViewTodayTasks.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ViewTodayTasks().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var today: TextView
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var adapter: TaskListAdapter1
    private lateinit var previousDateButton: Button
    private lateinit var nextDateButton: Button
    private lateinit var taskText: TextView
    private lateinit var tasksList: List<Task>
    private lateinit var originalTasksList: List<Task>
}