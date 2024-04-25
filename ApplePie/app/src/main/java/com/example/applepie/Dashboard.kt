package com.example.applepie

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.view.ViewCompat.canScrollVertically
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.google.android.material.divider.MaterialDividerItemDecoration

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
    private lateinit var addListTV: TextView
    private lateinit var listRV: androidx.recyclerview.widget.RecyclerView
    private lateinit var highPriorityRV: androidx.recyclerview.widget.RecyclerView
    private lateinit var viewTodayTask: androidx.constraintlayout.widget.ConstraintLayout
    private lateinit var viewAllTask: androidx.constraintlayout.widget.ConstraintLayout

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

        listRV = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.list_recycler_view)
        addListTV = view.findViewById<TextView>(R.id.add_list_text_view)

        highPriorityRV = view.findViewById<androidx.recyclerview.widget.RecyclerView>(R.id.high_priority_recycler_view)

        viewTodayTask = view.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewTodayTask)
        viewAllTask = view.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewAllTask)

        setupListRV()
        setupAddListTV()

        setupHighPriorityRV()

        setupViewTodayTask()
        setupViewAllTask()
    }

    override fun onResume() {
        super.onResume()
        updateTaskLists()
    }

    private fun setupListRV() {
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
        val taskLists = FirebaseManager.getUserList()?: listOf()
        val adapter = listRV.adapter as ListRecyclerAdapter
        adapter.setLists(taskLists)
    }

    private fun setupAddListTV() {
        addListTV.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, AddList()) // replace current fragment with new fragment
                .addToBackStack(null) // Optional: Add transaction to back stack for navigation back
                .commit()
        }
    }

    private fun setupHighPriorityRV() {
        val tasks = FirebaseManager.getUserTask()
        val lists = FirebaseManager.getUserList()

        tasks.filter { it.priority == "high" && !it.isDone }
            .sortedByDescending { it.due_datetime }

        for (task in tasks) {
            val matchingList = lists.find { it.id_list == task.id_list }
            task.listName = matchingList?.list_name ?: "Unknown List"
            task.list_color = matchingList?.list_color ?: -1
            Log.d("Task", task.toString())
        }

        val adapter = PriorityRecyclerAdapter(requireContext(), tasks)
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

    private fun setupViewTodayTask() {
        viewTodayTask.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ViewTodayTasks.newInstance("", ""))
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupViewAllTask() {
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