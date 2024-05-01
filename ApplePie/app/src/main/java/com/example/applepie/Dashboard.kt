package com.example.applepie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.compose.ui.text.capitalize
import androidx.core.view.ViewCompat.canScrollVertically
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.google.android.material.divider.MaterialDividerItemDecoration
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.Locale

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Dashboard.newInstance] factory method to
 * create an instance of this fragment.
 */
class Dashboard : Fragment() {
    private lateinit var todayTV: TextView
    private lateinit var searchBtn: Button
    private lateinit var addListTV: TextView
    private lateinit var listRV: RecyclerView
    private lateinit var highPriorityTV: TextView
    private lateinit var highPriorityRV: RecyclerView
    private lateinit var viewTodayTask: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var viewAllTask: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var todayCountTV: TextView
    private lateinit var allCountTV: TextView

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
        return inflater.inflate(R.layout.fragment_dashboard, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupUI(view)

        setupSearchBtn(view)
        setupListRV(view)
        setupAddListTV(view)

        setupHighPriorityRV(view)

        setupViewTodayTask(view)
        setupViewAllTask(view)
    }

    override fun onResume() {
        super.onResume()
        updateTaskLists()
        updateHighPriorityTasks()
    }

    private fun setupUI(view: View) {
        searchBtn = view.findViewById<Button>(R.id.search_button)
        todayTV = view.findViewById<TextView>(R.id.today_text_view)
        val day = LocalDate.now().dayOfMonth
        val month = LocalDate.now().month.toString().lowercase().replaceFirstChar { it.uppercase() }
        val today = "Today, $day $month"
        todayTV.text = today

        todayCountTV = view.findViewById(R.id.today_count_text_view)
        allCountTV = view.findViewById(R.id.all_count_text_view)

        val tasks = FirebaseManager.getUserTask()
        val todayTasks = tasks.filter { !it.isDone && it.due_datetime.split(" ")[0] == LocalDate.now().toString().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"))}
        todayCountTV.text = todayTasks.size.toString()

        val undoneTasks = tasks.filter { !it.isDone }
        allCountTV.text = undoneTasks.size.toString()
    }

    private fun setupSearchBtn(view: View) {
        searchBtn = view.findViewById<Button>(R.id.search_button)
        searchBtn.setOnClickListener {
            val searchActivity = Intent(requireContext(), SearchActivity::class.java)
            startActivity(searchActivity)
        }
    }

    private fun setupListRV(view: View) {
        listRV = view.findViewById<RecyclerView>(R.id.list_recycler_view)

        val taskLists = FirebaseManager.getUserList()
        val adapter = ListRecyclerAdapter(requireContext(), taskLists)
        listRV.adapter = adapter
        listRV.layoutManager = LinearLayoutManager(requireContext())

        listRV.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                MaterialDividerItemDecoration.VERTICAL
            ).apply {
                isLastItemDecorated = false
                dividerColor = resources.getColor(R.color.light_grey)
            }
        )

        adapter.onItemClick = { taskList ->
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ListOfTasks.newInstance(taskLists.indexOf(taskList)))
                .addToBackStack(null)
                .commit()
        }
    }

     private fun updateTaskLists() {
        val taskLists = FirebaseManager.getUserList()
        val adapter = listRV.adapter as ListRecyclerAdapter
        adapter.setLists(taskLists)
    }

    private fun setupAddListTV(view: View) {
        addListTV = view.findViewById(R.id.add_list_text_view)
        addListTV.setOnClickListener {
            val createListFragment = CreateListFragment()
            createListFragment.show(parentFragmentManager, createListFragment.tag)
        }
    }

    private fun setupHighPriorityRV(view: View) {
        highPriorityTV = view.findViewById(R.id.high_priority_text_view)
        highPriorityRV = view.findViewById(R.id.high_priority_recycler_view)

        val tasks = FirebaseManager.getUserTask()
        val lists = FirebaseManager.getUserList()

        val highPriorityTasks = tasks.filter { it.priority == "high" && !it.isDone }
                                     .sortedByDescending { it.due_datetime }

        if (highPriorityTasks.isEmpty()) {
            highPriorityTV.visibility = View.INVISIBLE
            highPriorityRV.visibility = View.INVISIBLE
            return
        } else {
            highPriorityTV.visibility = View.VISIBLE
            highPriorityRV.visibility = View.VISIBLE
        }

        for (task in highPriorityTasks) {
            val matchingList = lists.find { it.id_list == task.id_list }
            task.listName = matchingList?.list_name ?: "Unknown List"
            task.list_color = matchingList?.list_color ?: -1
        }

        val adapter = PriorityRecyclerAdapter(requireContext(), highPriorityTasks)
        highPriorityRV.adapter = adapter
        highPriorityRV.layoutManager = LinearLayoutManager(requireContext())

        highPriorityRV.addItemDecoration(
            MaterialDividerItemDecoration(
                requireContext(),
                MaterialDividerItemDecoration.VERTICAL
            ).apply {
                isLastItemDecorated = false
                dividerColor = resources.getColor(R.color.light_grey)
            }
        )

//        highPriorityRV.layoutManager = object: LinearLayoutManager(requireContext()) { override fun canScrollVertically() = false }

//        adapter.onItemClick = { task ->
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, TaskDetails.newInstance(task.id_task))
//                .addToBackStack(null)
//                .commit()
//        }
    }

    private fun updateHighPriorityTasks() {

        val highPriorityTasks = FirebaseManager.getHighPriorityUndoneTasks()

        if (highPriorityTasks.isEmpty()) {
            highPriorityTV.visibility = View.INVISIBLE
            highPriorityRV.visibility = View.INVISIBLE
            return
        } else {
            highPriorityTV.visibility = View.VISIBLE
            highPriorityRV.visibility = View.VISIBLE
        }

        val adapter = highPriorityRV.adapter as PriorityRecyclerAdapter
        adapter.setTasks(highPriorityTasks)
    }

    private fun setupViewTodayTask(view: View) {
        viewTodayTask = view.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewTodayTask)

        viewTodayTask.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ViewTodayTasks.newInstance("", ""))
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupViewAllTask(view: View) {
        viewAllTask = view.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAllTask)

        viewAllTask.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ViewAllTask.newInstance("", ""))
                .addToBackStack(null)
                .commit()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Dashboard.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Dashboard().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

}