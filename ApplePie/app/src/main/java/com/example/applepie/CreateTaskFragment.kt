package com.example.applepie

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
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
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.Task
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import es.dmoral.toasty.Toasty
import java.time.LocalDateTime
import java.util.Calendar


interface TimePickerListener {
    fun onTimePicked(duration: Int)
}


class CreateTaskFragment : BottomSheetDialogFragment() {
    private var timePickerListener: TimePickerListener? = null

    fun setTimePickerListener(listener: TimePickerListener) {
        this.timePickerListener = listener
    }

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
        tieReminder = view.findViewById(R.id.tie_reminder)
        tieAttachment = view.findViewById(R.id.tie_attachment)
        btnCreateTask = view.findViewById(R.id.btn_createTask)
        btnCancel = view.findViewById(R.id.btn_cancel)

        // format date: yyyy-MM-dd
        tieDuedate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
//                    val dat = String.format("%02d-%02d-%02d", year, monthOfYear + 1, dayOfMonth)
//                    tieDuedate.setText(dat)
                    val formattedMonth = (monthOfYear + 1).toString().padStart(2, '0')
                    val formattedDay = dayOfMonth.toString().padStart(2, '0')
                    val formattedDate = "$year-$formattedMonth-$formattedDay"
                    tieDuedate.setText(formattedDate)
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

        tieReminder.setOnClickListener {
            showTimePickerDialog()
        }

        val spinnerItems = listOf("None", "Low", "Medium", "High")

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spnPriority.adapter = adapter

        val taskLists = FirebaseManager.getUserList()

        val listNames = mutableListOf<String>()
        val listNameToIdMap = mutableMapOf<String, String>()
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

//        Log.i("duration", duration.toString())

        btnCreateTask.setOnClickListener {
            val title = tieTitle.text.toString()
            val description = tieDescription.text.toString()
            val duedate = tieDuedate.text.toString()
            val time = tieTime.text.toString()
            val priority = spnPriority.selectedItem.toString().lowercase()
            val listName = spnList.selectedItem.toString()
            val idList = listNameToIdMap[listName]
            val attachment = tieAttachment.text.toString()

            val newTask = Task(
                id_task = LocalDateTime.now().toString().replace("-", "").replace("T", "").replace(":", "").split(".")[0],
                id_list = idList!!,
                description = description,
                due_datetime = duedate + ' ' + time,
                link = attachment,
                priority = priority,
                title = title,
                reminder = duration
            )
            FirebaseManager.addNewTask(newTask)

//            Toasty.success(requireContext(), "Duration: " + duration, Toast.LENGTH_SHORT, true).show()

            Toasty.success(requireContext(), "Create task successfully!", Toast.LENGTH_SHORT, true).show()
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun showTimePickerDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_time_picker, null)
        val hourPicker = dialogView.findViewById<NumberPicker>(R.id.hourPicker)
        val minutePicker = dialogView.findViewById<NumberPicker>(R.id.minutePicker)

        hourPicker.minValue = 0
        hourPicker.maxValue = 23

        minutePicker.minValue = 0
        minutePicker.maxValue = 59

        val builder = AlertDialog.Builder(requireContext())
        builder.setView(dialogView)
        builder.setPositiveButton("OK") { dialog, _ ->
            val selectedHour = hourPicker.value
            val selectedMinute = minutePicker.value
            duration = selectedHour * 60 + selectedMinute

            timePickerListener?.onTimePicked(duration)
            val reminderText = "$selectedHour:$selectedMinute"
            tieReminder.setText(reminderText)

            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()

        val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
    }

    private lateinit var tieTitle: TextInputEditText
    private lateinit var tieDescription: TextInputEditText
    private lateinit var tieDuedate: TextInputEditText
    private lateinit var tieTime: TextInputEditText
    private lateinit var spnPriority: Spinner
    private lateinit var spnList: Spinner
    private lateinit var tieReminder: TextInputEditText
    private lateinit var tieAttachment: TextInputEditText
    private lateinit var btnCreateTask: Button
    private lateinit var btnCancel: Button

    private var myCalendar: Calendar = Calendar.getInstance()
    private var year = myCalendar.get(Calendar.YEAR)
    private var month = myCalendar.get(Calendar.MONTH)
    private var day = myCalendar.get(Calendar.DAY_OF_MONTH)
    private var hourOfDay = myCalendar.get(Calendar.HOUR_OF_DAY)
    private var minute = myCalendar.get(Calendar.MINUTE)
    private var duration = 0

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var databaseReference: DatabaseReference
}