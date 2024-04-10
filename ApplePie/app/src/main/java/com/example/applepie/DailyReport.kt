package com.example.applepie

import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.Task

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

        val handler = Handler()
        handler.postDelayed(object : Runnable {
            override fun run() {
                if (i <= 100) {
                    progressText.text = "$i%"
                    progressBar.progress = i
                    i++
                    handler.postDelayed(this, 200)
                } else {
                    handler.removeCallbacks(this)
                }
            }
        }, 200)

        taskRecyclerView = rootView.findViewById(R.id.recyclerView)
        adapter = TaskListAdapter(requireContext(), tasksList)

        taskRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        taskRecyclerView.adapter = adapter

        tasksList.add(Task("", "10:00 AM", 1, 1, false, "", "", "Project Proposal"))
        tasksList.add(Task("", "10:00 PM", 1, 2, true, "", "", "W01 - Kotlin"))
        tasksList.add(Task("", "11:59 AM", 1, 3, false,"", "", "W03 - UI + Auto layout"))
        tasksList.add(Task("", "8:00 PM", 2, 3, false,"", "", "Design Layout"))
        tasksList.add(Task("", "9:00 PM", 2, 3, true,"", "", "Handle Login Logic"))

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
    private var i = 0
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var adapter: TaskListAdapter
    private val tasksList = ArrayList<Task>()
}