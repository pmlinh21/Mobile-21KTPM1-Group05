package com.example.applepie

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.widget.AppCompatAutoCompleteTextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.Task

class SearchActivity : AppCompatActivity() {
    private lateinit var searchATCTV: AutoCompleteTextView
    private lateinit var searchResultRV: RecyclerView
    private lateinit var cancelButton: Button
    private lateinit var tasks: List<Task>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val intent = intent

        setContentView(R.layout.activity_search)

        tasks = FirebaseManager.getUserTask()

        setupCancelButton()
        setupSearchResultRecyclerView()
        setupSearchAutoCompleteTextView()
    }

    private fun setupCancelButton() {
        cancelButton = findViewById<Button>(R.id.cancel_button)
        cancelButton.setOnClickListener {
            finish()
        }
    }

    private fun setupSearchAutoCompleteTextView() {
        searchATCTV = findViewById<AppCompatAutoCompleteTextView>(R.id.search_auto_complete_text_view) as AutoCompleteTextView
        val autoCompleteAdapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_single_choice, tasks.map { it.title })
        searchATCTV.setAdapter(autoCompleteAdapter)
        searchATCTV.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
                if (searchATCTV.text.isEmpty()) {
                    val adapter = searchResultRV.adapter as TaskRecyclerAdapter
                    adapter.setTasks(listOf())
                } else {
                    val searchResult = tasks.filter { it.title.lowercase().contains(searchATCTV.text.toString().lowercase()) }
                    val adapter = searchResultRV.adapter as TaskRecyclerAdapter
                    adapter.setTasks(searchResult)
                }
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if (searchATCTV.text.isEmpty()) {
                    val adapter = searchResultRV.adapter as TaskRecyclerAdapter
                    adapter.setTasks(listOf())
                } else {
                    val searchResult = tasks.filter { it.title.lowercase().contains(searchATCTV.text.toString().lowercase()) }
                    val adapter = searchResultRV.adapter as TaskRecyclerAdapter
                    adapter.setTasks(searchResult)
                }
            }
        })
    }

    private fun setupSearchResultRecyclerView() {
        searchResultRV = findViewById<RecyclerView>(R.id.search_result_recycler_view)

        val adapter = TaskRecyclerAdapter(this, tasks)
        searchResultRV.adapter = adapter
        searchResultRV.layoutManager = LinearLayoutManager(this)

//        adapter.onItemClick = { task ->
//            parentFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, ListOfTasks.newInstance(tasks.indexOf(task)))
//                .addToBackStack(null)
//                .commit()
//        }
    }
    override fun onResume() {
        super.onResume()
        updateTaskLists()
    }

    private fun updateTaskLists() {
        tasks = FirebaseManager.getUserTask()
        if (searchATCTV.text.isEmpty()) {
            val adapter = searchResultRV.adapter as TaskRecyclerAdapter
            adapter.setTasks(listOf())
        } else {
            val searchResult = tasks.filter { it.title.lowercase().contains(searchATCTV.text.toString().lowercase()) }
            val adapter = searchResultRV.adapter as TaskRecyclerAdapter
            adapter.setTasks(searchResult)
        }
    }
}