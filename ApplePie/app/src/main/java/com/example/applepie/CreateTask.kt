package com.example.applepie

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.DialogInterface
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.Task
import com.example.applepie.model.User
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import es.dmoral.toasty.Toasty
import java.util.Calendar
import kotlin.properties.Delegates

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [CreateTask.newInstance] factory method to
 * create an instance of this fragment.
 */
class CreateTask : Fragment() {
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
        val rootView = inflater.inflate(R.layout.fragment_create_task, container, false)

        preferenceManager = PreferenceManager(this.activity)
        databaseReference = FirebaseDatabase.getInstance().getReference("users")
        val userRef = databaseReference.child(preferenceManager.getIndex().toString())
        Log.i("index", preferenceManager.getIndex().toString())

        tieTitle = rootView.findViewById(R.id.tie_title)
        tieDescription = rootView.findViewById(R.id.tie_description)
        tieDuedate = rootView.findViewById(R.id.tie_duedate)
        tieTime = rootView.findViewById(R.id.tie_time)
        spnPriority = rootView.findViewById(R.id.spn_priority)
        spnList = rootView.findViewById(R.id.spn_list)
        tieAttachment = rootView.findViewById(R.id.tie_attachment)
        btnCreateTask = rootView.findViewById(R.id.btn_createTask)

        tieDuedate.setOnClickListener {
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                { view, year, monthOfYear, dayOfMonth ->
                    val dat = (dayOfMonth.toString() + "/" + (monthOfYear + 1) + "/" + year)
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

        val listNames = mutableListOf<String>()

        userRef.child("lists").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (listSnapshot in snapshot.children) {
                    val listName = listSnapshot.child("list_name").getValue(String::class.java)
                    if (listName != null) {
                        listNames.add(listName)
                    }
                }

                val adt = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, listNames)
                adt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)

                spnList.adapter = adt
//                val lists = listNames.toTypedArray()
//                val builder = AlertDialog.Builder(requireContext())
//                builder.setTitle("Choose a list")
//                    .setItems(lists) { _: DialogInterface?, which: Int ->
//                        spnList.setSelection(which)
//                    }
//
//                val dialog = builder.create()
//                dialog.show()
            }

            override fun onCancelled(error: DatabaseError) {
            }
        })

        btnCreateTask.setOnClickListener {
            val title = tieTitle.text.toString()
            val description = tieDescription.text.toString()
            val duedate = tieDuedate.text.toString()
            val time = tieTime.text.toString()
            val priority = spnPriority.selectedItem.toString()
            val list = spnList.selectedItem.toString()
            val attachment = tieAttachment.text.toString()

            databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val user = Task(description = description, due_datetime = duedate + time, link = attachment, priority = priority, title = title)
                    userRef.child("tasks").setValue(user).addOnCompleteListener { dbTask ->
                        if (dbTask.isSuccessful) {
//                            Toasty.success(this@CreateTask, "Create task successfully", Toast.LENGTH_SHORT, true).show()
//                            finish()
                        } else {
//                            Toasty.error(this@RegisterActivity, "Failed to add user to database", Toast.LENGTH_SHORT, true).show()
                        }
                    }
                }

                override fun onCancelled(databaseError: DatabaseError) {
//                    Toasty.error(this@RegisterActivity, databaseError.message, Toast.LENGTH_SHORT, true).show()
                }
            })
        }

        return rootView
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Dashboard.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            CreateTask().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
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

    private var myCalendar: Calendar = Calendar.getInstance()
    private var year = myCalendar.get(Calendar.YEAR)
    private var month = myCalendar.get(Calendar.MONTH)
    private var day = myCalendar.get(Calendar.DAY_OF_MONTH)
    private var hourOfDay = myCalendar.get(Calendar.HOUR_OF_DAY)
    private var minute = myCalendar.get(Calendar.MINUTE)

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var databaseReference: DatabaseReference
}