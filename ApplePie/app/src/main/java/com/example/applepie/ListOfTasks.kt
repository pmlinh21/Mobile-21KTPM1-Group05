package com.example.applepie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.TaskList

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "listIndex"

/**
 * A simple [Fragment] subclass.
 * Use the [ListOfTasks.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListOfTasks : Fragment() {
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
        return inflater.inflate(R.layout.fragment_list_of_tasks, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listIndex = arguments?.getInt(ARG_PARAM1) ?: -1
        listInfo = FirebaseManager.getUserList()[listIndex]

        listNameTV = view.findViewById(R.id.list_name_text_view)
        listNameTV.text = listInfo.list_name

        backButton = view.findViewById(R.id.back_button)
        moreButton = view.findViewById(R.id.more_button)

        setupBackButton()
        setupMoreButton()
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
        fun newInstance(param1: Int) =
            ListOfTasks().apply {
                arguments = Bundle().apply {
                    putInt(ARG_PARAM1, param1)
                }
            }
    }

    private lateinit var listInfo: TaskList
    private var listIndex: Int = -1

    private lateinit var listNameTV: TextView
    private lateinit var backButton: Button
    private lateinit var moreButton: Button
}