package com.example.applepie

import android.content.Intent
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.webkit.WebView
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.core.net.toUri
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.Task
import com.example.applepie.model.TaskList
import java.util.ArrayList

private const val ARG_PARAM1 = "taskIndex"
private const val ARG_PARAM2 = "listName"
private const val ARG_PARAM3 = "idList"
/**
 * A simple [Fragment] subclass.
 * Use the [TaskDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
class TaskDetails : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    private var param3: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
            param3 = it.getString(ARG_PARAM3)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
//        Toast.makeText(requireContext(), "TaskDetails", Toast.LENGTH_SHORT).show()
        return inflater.inflate(R.layout.fragment_task_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        taskIndex = arguments?.getInt(ARG_PARAM1) ?: -1
        taskInfo = FirebaseManager.getUserTask().filter { it.id_list == param3 }[taskIndex]

        listNameTV = view.findViewById(R.id.list_text)
        listNameTV.text = arguments?.getString(ARG_PARAM2)

        taskNameTV = view.findViewById(R.id.task_text)
        taskNameTV.text = taskInfo.title

        val dueDateTime = taskInfo.due_datetime.split(" ")

        dueDateTV = view.findViewById(R.id.date_text)
        dueDateTV.text = dueDateTime[0]

        dueTimeTV = view.findViewById(R.id.time_text)
        dueTimeTV.text = dueDateTime[1]

        descriptionTV = view.findViewById(R.id.task_description)
        descriptionTV.text = taskInfo.description

        attachmentTV = view.findViewById(R.id.attachment_text)
        val link = taskInfo.link
        Toast.makeText(requireContext(), taskInfo.link, Toast.LENGTH_SHORT).show()
        val htmlString = "<a href=\"$link\">$link</a>"
        attachmentTV.text = Html.fromHtml(htmlString)
        val webView = view.findViewById<WebView>(R.id.webView)
        attachmentTV.setOnClickListener {
            webView.settings.javaScriptEnabled = true
            webView.settings.setSupportZoom(true)
            webView.loadUrl(taskInfo.link)
        }

//        backButton = view.findViewById(R.id.back_button)


//        setupBackButton()
//        setupMoreButton()
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    private fun setupMoreButton() {
        moreButton.setOnClickListener {

        }
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
        fun newInstance(idList: String, param1: Int, param2: String) =
            TaskDetails().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM3, idList)
                    putInt(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var taskInfo: Task
    private var taskIndex: Int = -1

    private lateinit var listNameTV: TextView
    private lateinit var taskNameTV: TextView
    private lateinit var dueDateTV: TextView
    private lateinit var dueTimeTV: TextView
    private lateinit var descriptionTV: TextView
    private lateinit var attachmentTV: TextView
    private lateinit var backButton: Button
    private lateinit var moreButton: Button
}