package com.example.applepie

import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.DataUpdateListener
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.TaskList


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "listIndex"

/**
 * A simple [Fragment] subclass.
 * Use the [ListDetail.newInstance] factory method to
 * create an instance of this fragment.
 */
class ListDetail : Fragment(), DataUpdateListener {
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
        return inflater.inflate(R.layout.fragment_list_detail, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        listIndex = arguments?.getInt(ARG_PARAM1) ?: -1

        listNameTV = view.findViewById(R.id.list_name_text_view)
        backButton = view.findViewById(R.id.back_button)
        moreButton = view.findViewById(R.id.more_button)

        taskRV = view.findViewById(R.id.task_recycler_view)
        taskRV.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

        FirebaseManager.addDataUpdateListener(this)

//        taskRV.adapter = TaskListAdapter1(requireContext(), tasksList, lists)
        displayData()
        setupBackButton()
        setupMoreButton()
    }

    private fun setupBackButton() {
        backButton.setOnClickListener {
            parentFragmentManager.popBackStack()
        }
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun setupMoreButton() {
        val popupMenu = PopupMenu(requireContext(), moreButton)
        popupMenu.inflate(R.menu.popup_menu)

        popupMenu.setForceShowIcon(true)

        popupMenu.setOnMenuItemClickListener {
            when (it.itemId) {
                R.id.edit_list -> {
                    val editListFragment = EditListFragment()
                    editListFragment.show(parentFragmentManager, editListFragment.tag)
                }
                R.id.delete_list -> {

                }
            }
            true
        }

        moreButton.setOnClickListener {
            popupMenu.show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirebaseManager.removeDataUpdateListener(this)
    }

    override fun updateData() {
        displayData()
    }

    private fun displayData(){
        listInfo = FirebaseManager.getUserList()[listIndex]
        listNameTV.text = listInfo.list_name

        val lists = FirebaseManager.getUserList()?: listOf()
        val tasksList = FirebaseManager.getUserTask().filter { it.id_list == listInfo.id_list }
        taskRV.adapter = TaskListAdapter1(requireContext(), tasksList, lists)
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
            ListDetail().apply {
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
    private lateinit var taskRV: RecyclerView
}