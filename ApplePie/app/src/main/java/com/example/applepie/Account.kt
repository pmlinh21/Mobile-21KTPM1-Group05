package com.example.applepie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.TextView
import com.example.applepie.database.FirebaseManager

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_INDEX_USER = "indexUser"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Account.newInstance] factory method to
 * create an instance of this fragment.
 */
class Account : Fragment() {
    // TODO: Rename and change types of parameters
    private var indexUser: Int? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            indexUser = it.getInt(ARG_INDEX_USER)
            param2 = it.getString(ARG_PARAM2)
        }
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(R.layout.fragment_account, container, false)

        subscriptionButton = rootView.findViewById(R.id.subscription_btn)
        reminderButton = rootView.findViewById(R.id.reminder_btn)
        logoutButton = rootView.findViewById(R.id.logout_btn)
        usernameInput = rootView.findViewById(R.id.username_input)
        emailInput = rootView.findViewById(R.id.email_input)
        currentStreakText = rootView.findViewById(R.id.current_streak_text)
        longestStreakText = rootView.findViewById(R.id.longest_streak_text)
        calendar = rootView.findViewById(R.id.calendarView)

        setUI()
        handleEventListener()

        return rootView
    }

    fun setUI(){
        usernameInput.setText(FirebaseManager.getUserInfo().username)
        emailInput.setText(FirebaseManager.getUserInfo().email)
    }

    fun handleEventListener(){
        subscriptionButton.setOnClickListener {
            // TODO: go to subscription fragment
        }

        reminderButton.setOnClickListener {
            // TODO:  pop up input field for user to change
        }

        logoutButton.setOnClickListener {
            // TODO:  delete index from preferences
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment account.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(indexUser: Int, param2: String) =
            Account().apply {
                arguments = Bundle().apply {
                    putInt(ARG_INDEX_USER, indexUser)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var subscriptionButton: Button
    private lateinit var reminderButton: Button
    private lateinit var logoutButton: Button

    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText

    private lateinit var currentStreakText: TextView
    private lateinit var longestStreakText: TextView

    private lateinit var calendar: CalendarView
}