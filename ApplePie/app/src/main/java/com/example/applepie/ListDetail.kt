package com.example.applepie

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.PopupMenu
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.view.menu.MenuBuilder
import androidx.appcompat.view.menu.MenuPopupHelper
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.applepie.database.DataUpdateListener
import com.example.applepie.database.FirebaseManager
import com.example.applepie.model.TaskList
import es.dmoral.toasty.Toasty


// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "listId"

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

        listId = param1!!

        FirebaseManager.addDataUpdateListener(this)

        listNameTV = view.findViewById(R.id.list_name_text_view)
        backButton = view.findViewById(R.id.back_button)
        moreButton = view.findViewById(R.id.more_button)

        taskRV = view.findViewById(R.id.task_recycler_view)
        taskRV.layoutManager = LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)

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
                    val editListFragment = EditListFragment(listInfo)
                    editListFragment.show(parentFragmentManager, editListFragment.tag)
                }
                R.id.delete_list -> {
                    val builder = AlertDialog.Builder(context)
                    builder.setTitle("Confirm Deletion")
                    builder.setMessage("Are you sure you want to delete this list? All tasks in this list will be deleted as well.")

                    builder.setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }

                    builder.setPositiveButton("Delete") { dialog, _ ->
                        val tasksInList = FirebaseManager.getUserTask().filter { it.id_list == listInfo.id_list }
                        for (task in tasksInList) {
                            FirebaseManager.deleteTask(task.id_task)
                        }
                        FirebaseManager.deleteList(listInfo.id_list)
                        Toasty.success(requireContext(), "List deleted", Toasty.LENGTH_SHORT).show()
                        parentFragmentManager.popBackStack()
                    }
                    val dialog = builder.create()
                    dialog.show()
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
        listInfo = FirebaseManager.getUserList().find { it.id_list == listId }!!
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
         * @return A new instance of fragment ListOfTasks.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String) =
            ListDetail().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                }
            }
    }

    private lateinit var listInfo: TaskList
    private var listId: String = ""

    private lateinit var listNameTV: TextView
    private lateinit var backButton: Button
    private lateinit var moreButton: Button
    private lateinit var taskRV: RecyclerView
}