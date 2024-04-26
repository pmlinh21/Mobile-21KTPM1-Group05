package com.example.applepie

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.Toast
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import es.dmoral.toasty.Toasty
import java.util.Calendar


class CreateTaskFragment : BottomSheetDialogFragment() {
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_create_task, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val bottomSheet: View = view.parent as View
        bottomSheet.backgroundTintMode = PorterDuff.Mode.CLEAR;
        bottomSheet.backgroundTintList = ColorStateList.valueOf(Color.TRANSPARENT);
        bottomSheet.setBackgroundColor(Color.TRANSPARENT);


        preferenceManager = PreferenceManager(this.activity)
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val userRef = databaseReference.child(preferenceManager.getIndex().toString())
        Log.i("index", preferenceManager.getIndex().toString())

        tieTitle = view.findViewById(R.id.tie_title)
        tieDescription = view.findViewById(R.id.tie_description)
        tieDuedate = view.findViewById(R.id.tie_duedate)
        tieTime = view.findViewById(R.id.tie_time)
        spnPriority = view.findViewById(R.id.spn_priority)
        spnList = view.findViewById(R.id.spn_list)
        tieAttachment = view.findViewById(R.id.tie_attachment)
        btnCreateTask = view.findViewById(R.id.btn_createTask)
        btnCancel = view.findViewById(R.id.btn_cancel)

        tieDuedate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    val dat = "$year-${monthOfYear + 1}-$dayOfMonth"
                    tieDuedate.setText(dat)
                },
                year,
                month,
                day
            )
            datePickerDialog.show()
        }

        tieTime.setOnClickListener {
            val timePickerDialog = TimePickerDialog(
                requireContext(),
                { _, selectedHour, selectedMinute ->
                    val selectedTime = String.format("%02d:%02d", selectedHour, selectedMinute)
                    tieTime.setText(selectedTime)
                },
                hourOfDay,
                minute,
                true
            )
            timePickerDialog.show()
        }

        val spinnerItems = listOf("None", "Low", "Medium", "High")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spnPriority.adapter = adapter

        val taskLists = FirebaseManager.getUserList()

        val listNames = mutableListOf<String>()
        val listNameToIdMap = mutableMapOf<String, Int>()
        for (taskList in taskLists) {
            val listName = taskList.list_name
            if (listName.isNotEmpty()) {
                listNames.add(listName)
                listNameToIdMap[listName] = taskList.id_list
            }
        }

        val adt = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listNames)
        adt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spnList.adapter = adt

        btnCreateTask.setOnClickListener {
            val title = tieTitle.text.toString()
            val description = tieDescription.text.toString()
            val duedate = tieDuedate.text.toString()
            val time = tieTime.text.toString()
            val priority = spnPriority.selectedItem.toString()
            val listName = spnList.selectedItem.toString()
            val idList = listNameToIdMap[listName]
            val attachment = tieAttachment.text.toString()

            val newTask = Task(
                id_task = -1,
                id_list = idList!!,
                description = description,
                due_datetime = duedate + ' ' + time,
                link = attachment,
                priority = priority,
                title = title
            )
            FirebaseManager.addNewTask(newTask)

            Toasty.success(requireContext(), "Create task successfully!", Toast.LENGTH_SHORT, true).show()
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private lateinit var tieTitle: TextInputEditText
    private lateinit var tieDescription: TextInputEditText
    private lateinit var tieDuedate: TextInputEditText
    private lateinit var tieTime: TextInputEditText
    private lateinit var spnPriority: Spinner
    private lateinit var spnList: Spinner
    private lateinit var tieAttachment: TextInputEditText
    private lateinit var btnCreateTask: Button
    private lateinit var btnCancel: Button

    private var myCalendar: Calendar = Calendar.getInstance()
    private var year = myCalendar.get(Calendar.YEAR)
    private var month = myCalendar.get(Calendar.MONTH)
    private var day = myCalendar.get(Calendar.DAY_OF_MONTH)
    private var hourOfDay = myCalendar.get(Calendar.HOUR_OF_DAY)
    private var minute = myCalendar.get(Calendar.MINUTE)

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var databaseReference: DatabaseReference
}