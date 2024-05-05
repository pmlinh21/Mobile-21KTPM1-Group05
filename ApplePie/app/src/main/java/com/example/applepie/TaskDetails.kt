package com.example.applepie

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.applepie.database.DataUpdateListener
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.Task


private const val ARG_PARAM1 = "taskId"
/**
 * A simple [Fragment] subclass.
 * Use the [TaskDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskDetails : Fragment(), DataUpdateListener {
    // TODO: Rename and change types of parameters
    private var param1: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_task_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseManager.addDataUpdateListener(this)

        taskId = arguments?.getString(ARG_PARAM1).toString()

        listNameTV = view.findViewById(R.id.list_text)

        taskNameTV = view.findViewById(R.id.task_text)

        dueDateTV = view.findViewById(R.id.date_text)

        dueTimeTV = view.findViewById(R.id.time_text)

        descriptionTV = view.findViewById(R.id.task_description)

        attachmentTV = view.findViewById(R.id.attachment_text)

//        val webView = view.findViewById<WebView>(R.id.webView)
        attachmentTV.setOnClickListener {
//            webView.settings.javaScriptEnabled = true
//            webView.settings.setSupportZoom(true)
//            webView.loadUrl(taskInfo.link)
            val uri = Uri.parse(link)
            val intent = Intent(Intent.ACTION_VIEW, uri)
            startActivity(intent)
        }

        backButton = view.findViewById(R.id.back_btn)
        editButton = view.findViewById(R.id.edit_btn)

        displayData()
        setupBackButton()
        setupEditButton(taskInfo)
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupEditButton(taskInfo: Task) {
        editButton.setOnClickListener {
//            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CreateTask()).addToBackStack(null).commit()
            val editTaskFragment = EditTaskFragment(taskInfo)
            editTaskFragment.show(parentFragmentManager, editTaskFragment.tag)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirebaseManager.removeDataUpdateListener(this)
    }

    override fun updateData() {
        displayData()
    }

    fun displayData(){
        taskInfo = FirebaseManager.getUserTask().filter { it.id_task == taskId }[0]
        val listInfo = FirebaseManager.getUserList().filter { it.id_list == taskInfo.id_list }[0]
        listNameTV.text = listInfo.list_name

        taskNameTV.text = taskInfo.title

        val dueDateTime = taskInfo.due_datetime.split(" ")

        dueDateTV.text = dueDateTime[0]


        dueTimeTV.text = dueDateTime[1]


        descriptionTV.text = taskInfo.description

        link = taskInfo.link
//        Toast.makeText(requireContext(), taskInfo.link, Toast.LENGTH_SHORT).show()
        val htmlString = "<a href=\"$link\">$link</a>"
        attachmentTV.text = Html.fromHtml(htmlString)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment ListOfTasks.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(taskId: String) =
            TaskDetails().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, taskId)
                }
            }
    }

    private lateinit var taskInfo: Task
    private var taskId: String = ""

    private lateinit var listNameTV: TextView
    private lateinit var taskNameTV: TextView

    private lateinit var dueDateTV: TextView
    private lateinit var dueTimeTV: TextView
    private lateinit var descriptionTV: TextView
    private lateinit var link: String
    private lateinit var attachmentTV: TextView
    private lateinit var backButton: Button
    private lateinit var editButton: ImageView
}