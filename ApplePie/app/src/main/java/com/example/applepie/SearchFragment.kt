package com.example.applepie

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.Task
import com.google.android.material.divider.MaterialDividerItemDecoration

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [SearchFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class SearchFragment : Fragment() {
    private lateinit var searchATCTV: AutoCompleteTextView
    private lateinit var searchResultRV: RecyclerView
    private lateinit var tasks: List<Task>

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
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        tasks = FirebaseManager.getUserTask()
        setupSearchResultRecyclerView(view)
        setupSearchAutoCompleteTextView(view)
    }

    private fun setupSearchAutoCompleteTextView(view: View) {
        searchATCTV = view.findViewById<AppCompatAutoCompleteTextView>(R.id.search_auto_complete_text_view) as AutoCompleteTextView
        val autoCompleteAdapter = ArrayAdapter<String>(requireContext(), android.R.layout.simple_list_item_single_choice, tasks.map { it.title })
        searchATCTV.setAdapter(autoCompleteAdapter)
        searchATCTV.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (searchATCTV.text.isEmpty()) {
                    val adapter = searchResultRV.adapter as TaskRecyclerAdapter
                    adapter.setTasks(listOf())
//                    if (isRecyclerView) {
//                        val adapter = studentRV.adapter as StudentRecyclerAdapter
//                        adapter.setStudents(students)
//                    } else {
//                        val adapter = studentGV.adapter as StudentGridAdapter
//                        adapter.setStudents(students)
//                    }
                } else {
                    val searchResult = tasks.filter { it.title.lowercase().contains(searchATCTV.text.toString().lowercase()) }
                    val adapter = searchResultRV.adapter as TaskRecyclerAdapter
                    adapter.setTasks(searchResult)
//                    val searchResult = students.filter { it.name.lowercase().contains(searchATCTV.text.toString().lowercase()) }
//                    if (isRecyclerView) {
//                        val adapter = studentRV.adapter as StudentRecyclerAdapter
//                        adapter.setStudents(searchResult)
//                    } else {
//                        val adapter = studentGV.adapter as StudentGridAdapter
//                        adapter.setStudents(searchResult)
//                    }
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (searchATCTV.text.isEmpty()) {
                    val adapter = searchResultRV.adapter as TaskRecyclerAdapter
                    adapter.setTasks(listOf())
//                    if (isRecyclerView) {
//                        val adapter = studentRV.adapter as StudentRecyclerAdapter
//                        adapter.setStudents(students)
//                    } else {
//                        val adapter = studentGV.adapter as StudentGridAdapter
//                        adapter.setStudents(students)
//                    }
                } else {
                    val searchResult = tasks.filter { it.title.lowercase().contains(searchATCTV.text.toString().lowercase()) }
                    val adapter = searchResultRV.adapter as TaskRecyclerAdapter
                    adapter.setTasks(searchResult)

//                    val searchResult = students.filter { it.name.lowercase().contains(searchATCTV.text.toString().lowercase()) }
//                    if (isRecyclerView) {
//                        val adapter = studentRV.adapter as StudentRecyclerAdapter
//                        adapter.setStudents(searchResult)
//                    } else {
//                        val adapter = studentGV.adapter as StudentGridAdapter
//                        adapter.setStudents(searchResult)
//                    }
                }
            }
        })
    }

    private fun setupSearchResultRecyclerView(view: View) {
        searchResultRV = view.findViewById<RecyclerView>(R.id.search_result_recycler_view)

        val adapter = TaskRecyclerAdapter(requireContext(), listOf())
        searchResultRV.adapter = adapter
        searchResultRV.layoutManager = LinearLayoutManager(requireContext())

//        adapter.onItemClick = { task ->
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, ListOfTasks.newInstance(tasks.indexOf(task)))
//                .addToBackStack(null)
//                .commit()
//        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment SearchFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            SearchFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}