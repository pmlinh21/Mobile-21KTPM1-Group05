package com.example.applepie

import android.os.Bundle
import android.os.Handler
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.TaskList
import com.example.applepie.model.Task
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DailyReport.newInstance] factory method to
 * create an instance of this fragment.
 */
class DailyReport : Fragment() {
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
        val rootView = inflater.inflate(R.layout.fragment_daily_report, container, false)

        progressBar = rootView.findViewById(R.id.progress_bar)
        progressText = rootView.findViewById(R.id.progress_text)
        taskRecyclerView = rootView.findViewById(R.id.recyclerView)

        val lists = FirebaseManager.getUserList()?: listOf()
        var tasksList = FirebaseManager.getUserTask()?: listOf()

        val currentTime = Calendar.getInstance().time
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val appDate = sdf.format(currentTime)

        // Lấy những task có due_datetime sau hoặc bằng today (task chưa quá hạn)
        tasksList = tasksList.filter { task ->
            val taskDueDate_1 = task.due_datetime.substring(0, 10)
            (taskDueDate_1 == appDate) ?: false
        }.sortedByDescending { task ->
            sdf.parse(task.due_datetime)
        }

        adapter = TaskListAdapter(requireContext(), tasksList, lists)

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                val percentageDone = adapter.getPercentageDone().toInt()
                progressBar.progress = percentageDone
                progressText.text = "$percentageDone%"
            }
        }, 200)


        if (tasksList.isEmpty()) {
            taskText = rootView.findViewById(R.id.task_text)
            taskText.text = "There are no tasks for today"
            taskRecyclerView.visibility = View.GONE
        } else {
            taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
            taskRecyclerView.adapter = adapter
        }

        val totalTasks = tasksList.size

        total_tasks = rootView.findViewById(R.id.total_tasks)
        total_tasks.text = "Total tasks: $totalTasks"
        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment DailyReport.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            DailyReport().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var progressBar: ProgressBar
    private lateinit var progressText: TextView
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var adapter: TaskListAdapter
    private  lateinit var taskText: TextView
    private lateinit var total_tasks: TextView
}