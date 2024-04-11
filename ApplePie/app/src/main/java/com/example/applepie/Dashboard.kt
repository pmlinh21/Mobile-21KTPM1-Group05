package com.example.applepie

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.TaskList
import com.example.studentmanagementv4.ListRecyclerAdapter

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

        setupListRV()
        setupAddListTV()

        val viewTodayTask = view.findViewById<androidx.constraintlayout.widget.ConstraintLayout>(R.id.viewTodayTask)
        viewTodayTask.setOnClickListener {
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ViewTodayTasks.newInstance("", ""))
                .addToBackStack(null)
                .commit()
        }
    }

    private fun setupListRV() {
        val taskLists = FirebaseManager.getUserList()
        val adapter = ListRecyclerAdapter(requireContext(), taskLists)
        listRV.adapter = adapter
        listRV.layoutManager = LinearLayoutManager(requireContext())

        val itemDecoration: RecyclerView.ItemDecoration = DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        listRV.addItemDecoration(itemDecoration)

        adapter.onItemClick = { taskList ->
//            val intent = Intent(this, EditActivity::class.java)
//            intent.putExtra("id", student.id)
//            startActivity(intent)
        }
    }

     fun updateTaskLists() {
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

    override fun onResume() {
        super.onResume()
        updateTaskLists()
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

    private lateinit var addListTV: TextView
    private lateinit var listRV: androidx.recyclerview.widget.RecyclerView
}