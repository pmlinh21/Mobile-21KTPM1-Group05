package com.example.applepie

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.content.res.ColorStateList
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.NumberPicker
import android.widget.Spinner
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.example.applepie.database.DataUpdateListener
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.Task
import com.example.applepie.model.TaskList
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import es.dmoral.toasty.Toasty
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Locale


interface TimePickerListener {
    fun onTimePicked(duration: Int)
}


class EditTaskFragment(taskInfo: Task) : BottomSheetDialogFragment(), DataUpdateListener {
    private var timePickerListener: TimePickerListener? = null
    private var taskInfo: Task = taskInfo

    fun setTimePickerListener(listener: TimePickerListener) {
        this.timePickerListener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_edit_task, container, false)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        FirebaseManager.addDataUpdateListener(this)

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
        btnSaveTask = view.findViewById(R.id.btn_saveTask)
        btnCancel = view.findViewById(R.id.btn_cancel)



        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, spinnerItems)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

        spnPriority.adapter = adapter

        tieTitle.setText(taskInfo.title)
        tieDescription.setText(taskInfo.description)
        val dueDateTime = taskInfo.due_datetime.split(" ")
        tieDuedate.setText(dueDateTime[0])
        tieTime.setText(dueDateTime[1])

        val priorityIndex = spinnerItems.indexOf(taskInfo.priority.capitalize())
//        Toast.makeText(requireContext(), "Priority: " + taskInfo.priority, Toast.LENGTH_SHORT).show()
        if (priorityIndex != -1) {
            spnPriority.setSelection(priorityIndex)
        }

        val reminderHour = taskInfo.reminder / 60
        val reminderMinute = taskInfo.reminder % 60
        val reminderTime = String.format(Locale.getDefault(), "%02d:%02d", reminderHour, reminderMinute)
        Log.i("reminderTime", reminderTime)
        tieReminder.setText(reminderTime)
        tieAttachment.setText(taskInfo.link)


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

        taskLists = FirebaseManager.getUserList()

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

        val position = listNames.indexOfFirst { listNameToIdMap[it] == taskInfo.id_list }
        if (position != -1) {
            spnList.setSelection(position)
        } else {
            Log.d("Spinner", "id_list ${taskInfo.id_list} not found")
        }

//        Log.i("duration", duration.toString())

        btnSaveTask.setOnClickListener {
            val title = tieTitle.text.toString()
            val description = tieDescription.text.toString()
            val duedate = tieDuedate.text.toString()
            val time = tieTime.text.toString()
            val priority = spnPriority.selectedItem.toString().lowercase()
            val listName = spnList.selectedItem.toString()
            val idList = listNameToIdMap[listName]
            val attachment = tieAttachment.text.toString()

            val updatedTask = Task(
                id_task = taskInfo.id_task,
                id_list = idList!!,
                description = description,
                due_datetime = duedate + ' ' + time,
                link = attachment,
                priority = priority,
                title = title,
                reminder = duration
            )
            FirebaseManager.updateTask(updatedTask)

            if (duration > 0)
                MainActivity.makeReminderNoti(requireContext(), "$duedate $time", duration, taskInfo.id_task, title)

            Toasty.success(requireContext(), "Updated task successfully!", Toast.LENGTH_SHORT, true).show()
            dismiss()
        }

        btnCancel.setOnClickListener {
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        FirebaseManager.removeDataUpdateListener(this)
    }

    override fun updateData() {
        displayData(spinnerItems)
    }

    fun displayData(spinnerItems: List<String>){
        tieTitle.setText(taskInfo.title)
        tieDescription.setText(taskInfo.description)
        val dueDateTime = taskInfo.due_datetime.split(" ")
        tieDuedate.setText(dueDateTime[0])
        tieTime.setText(dueDateTime[1])

        val priorityIndex = spinnerItems.indexOf(taskInfo.priority.capitalize())
//        Toast.makeText(requireContext(), "Priority: " + taskInfo.priority, Toast.LENGTH_SHORT).show()
        if (priorityIndex != -1) {
            spnPriority.setSelection(priorityIndex)
        }

        val reminderHour = taskInfo.reminder / 60
        val reminderMinute = taskInfo.reminder % 60
        val reminderTime = String.format(Locale.getDefault(), "%02d:%02d", reminderHour, reminderMinute)
        Log.i("reminderTime", reminderTime)
        tieReminder.setText(reminderTime)
        tieAttachment.setText(taskInfo.link)

        val listNames = mutableListOf<String>()
        val listNameToIdMap = mutableMapOf<String, String>()
        for (taskList in taskLists) {
            val listName = taskList.list_name
            if (listName.isNotEmpty()) {
                listNames.add(listName)
                listNameToIdMap[listName] = taskList.id_list
            }
        }
        val position = listNames.indexOfFirst { listNameToIdMap[it] == taskInfo.id_list }
        if (position != -1) {
            spnList.setSelection(position)
        } else {
            Log.d("Spinner", "id_list ${taskInfo.id_list} not found")
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
            val reminderText = String.format("%02d:%02d", selectedHour, selectedMinute)
            tieReminder.setText(reminderText)

            dialog.dismiss()
        }
        builder.setNegativeButton("Cancel") { dialog, _ ->
            dialog.dismiss()
        }
        val dialog = builder.create()
        dialog.show()

        val negativeButton = dialog.getButton(DialogInterface.BUTTON_NEGATIVE)
        negativeButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))

        val positiveButton = dialog.getButton(DialogInterface.BUTTON_POSITIVE)
        positiveButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.green))
    }

    private lateinit var tieTitle: TextInputEditText
    private lateinit var tieDescription: TextInputEditText
    private lateinit var tieDuedate: TextInputEditText
    private lateinit var tieTime: TextInputEditText
    private lateinit var spnPriority: Spinner
    private lateinit var spnList: Spinner
    private lateinit var tieReminder: TextInputEditText
    private lateinit var tieAttachment: TextInputEditText
    private lateinit var btnSaveTask: Button
    private lateinit var btnCancel: Button

    private var myCalendar: Calendar = Calendar.getInstance()
    private var year = myCalendar.get(Calendar.YEAR)
    private var month = myCalendar.get(Calendar.MONTH)
    private var day = myCalendar.get(Calendar.DAY_OF_MONTH)
    private var hourOfDay = myCalendar.get(Calendar.HOUR_OF_DAY)
    private var minute = myCalendar.get(Calendar.MINUTE)
    private var duration = taskInfo.reminder

    private val spinnerItems = listOf("None", "Low", "Medium", "High")
    private lateinit var taskLists: List<TaskList>

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var databaseReference: DatabaseReference
}