package com.example.applepie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

        taskRecyclerView = rootView.findViewById(R.id.recyclerView)
        adapter = TaskListAdapter1(requireContext(), tasksList, lists)

        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskRecyclerView.adapter = adapter

        lists.add(TaskList(1, 0, "", "Mobile"))
        lists.add(TaskList(2, 0, "", "SoftwareDesign"))

        tasksList.add(Task("", "10:00 AM", 1, 1, false, "", "", "Project Proposal"))
        tasksList.add(Task("", "10:00 PM", 1, 2, true, "", "", "W01 - Kotlin"))
        tasksList.add(Task("", "11:59 AM", 1, 3, false,"", "", "W03 - UI + Auto layout"))
        tasksList.add(Task("", "8:00 PM", 2, 3, false,"", "", "Design Layout"))
        tasksList.add(Task("", "9:00 PM", 2, 3, true,"", "", "Handle login function"))


        return rootView
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
    private val tasksList = ArrayList<Task>()
    private val lists = ArrayList<TaskList>()
}