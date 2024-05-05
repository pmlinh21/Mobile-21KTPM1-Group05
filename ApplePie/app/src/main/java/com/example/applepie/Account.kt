package com.example.applepie

import android.app.AlertDialog
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.NumberPicker
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.DateTime
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar
import java.util.Date
import java.util.Locale
import kotlin.properties.Delegates

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
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment

        val rootView = inflater.inflate(R.layout.fragment_account, container, false)

        subscriptionDescription = rootView.findViewById(R.id.subscription_description)
        subscriptionButton = rootView.findViewById(R.id.subscription_btn)
        logoutButton = rootView.findViewById(R.id.logout_btn)
        usernameInput = rootView.findViewById(R.id.username_input)
        emailInput = rootView.findViewById(R.id.email_input)
        currentStreakText = rootView.findViewById(R.id.current_streak_text)
        longestStreakText = rootView.findViewById(R.id.longest_streak_text)
        calendar = rootView.findViewById(R.id.calendarView)

        preferenceManager = PreferenceManager(this.activity)

        getStudyTime()
        setUI()
        handleEventListener()

        extractMonthYearFromCalendar(calendar.date)

        calendar.setOnDateChangeListener { view, year, month, dayOfMonth ->
            val readableMonth = month + 1
            Log.i("CalendarView", "Selected - Year: $year, Month: $readableMonth")


        }

        return rootView
    }

    private fun extractMonthYearFromCalendar(dateMillis: Long) {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = dateMillis

        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH) + 1
        Log.i("CalendarView", "Currently viewing - Year: $year, Month: $month")
    }

    private fun getStudyTime(){
        pomodoroTime = FirebaseManager.getUserPomodoro()?: listOf()
        stopwatchTime = FirebaseManager.getUserStopwatch()?: listOf()

        val allTimes: List<DateTime> = pomodoroTime + stopwatchTime

        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        val totalSecondsByDate: Map<LocalDate, Long> = allTimes
            .groupBy { LocalDateTime.parse(it.start_time, dateTimeFormatter).toLocalDate() }
            .mapValues { (_, times) ->
                times.sumOf { calculateSeconds(it.start_time, it.end_time, dateTimeFormatter) }
            }
            .filter { (_, totalSeconds) -> totalSeconds > 60 }

        // Print the results
        totalSecondsByDate.forEach { (date, totalSeconds) ->
            Log.i("streak","Date: $date, Total Seconds: $totalSeconds")
        }
    }

    fun calculateSeconds(start: String, end: String, dateTimeFormatter: DateTimeFormatter): Long {
        val startTime = LocalDateTime.parse(start, dateTimeFormatter)
        val endTime = LocalDateTime.parse(end, dateTimeFormatter)
        return ChronoUnit.SECONDS.between(startTime, endTime)
    }

    private fun setUI(){
        usernameInput.setText(FirebaseManager.getUserInfo().username)
        emailInput.setText(FirebaseManager.getUserInfo().email)
        currentStreakText.text = FirebaseManager.getUserInfo().current_streak.toString()
        longestStreakText.text = FirebaseManager.getUserInfo().longest_streak.toString()
        if (FirebaseManager.getUserInfo().isPremium) {
            subscriptionDescription.text = getString(R.string.premium_description_paid)
            subscriptionButton.visibility = View.GONE
        } else {
            subscriptionDescription.setText(R.string.premium_description_free)
            subscriptionButton.isEnabled = true
            subscriptionButton.setText(R.string.premium_button)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleEventListener(){
        subscriptionButton.setOnClickListener {
            val subscribeActivity = Intent(this.activity, SubscribeActivity::class.java)
            startActivity(subscribeActivity)
        }

        logoutButton.setOnClickListener {
            // TODO:  delete index from preferences
            val builder = AlertDialog.Builder(this.activity)
            builder.setMessage("Are you sure you want to log out?")
                .setCancelable(false)
                .setPositiveButton("Yes") { dialog, id ->
                    if (!StopwatchTimer.isStop()){
                        storeStopwatchTimeInFirebase()
                        StopwatchTimer.stopTimer()
                    }

                    if (!PomodoroTimer.isStop()) {
                        storePomodoroTimeInFirebase()
                        PomodoroTimer.stopTimer()
                    }

                    val stopIntent = Intent(context, MusicService::class.java)
                    context?.stopService(stopIntent)

                    preferenceManager.removeData()

                    val loginActivity = Intent(this.activity, LoginActivity::class.java)
                    startActivity(loginActivity)
                }
                .setNegativeButton("Cancel") { dialog, id ->
                    // Dismiss the dialog
                    dialog.dismiss()
                }
            val alert = builder.create()
            alert.show()
        }
    }

    fun storeStopwatchTimeInFirebase(){
        val index = preferenceManager.getIndex()

        val start_time = preferenceManager.getStartTime()!!
        val end_time = getCurrentDateTime()

        Log.i("stopwatch", "$start_time $end_time")
        FirebaseManager.updateStopwatch(index, DateTime(end_time, start_time))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun storePomodoroTimeInFirebase(){
        if (PomodoroTimer.getMode() == "Focus"){
            val index = preferenceManager.getIndex()
            val start_time = preferenceManager.getStartTime()!!
            val end_time = getCurrentDateTime()

            Log.i("pomodoro", "$start_time $end_time")
            FirebaseManager.updatePomodoro(index, DateTime(end_time, start_time))
        }
    }

    fun getCurrentDateTime(): String {

        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
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

    private lateinit var subscriptionDescription: TextView
    private lateinit var subscriptionButton: Button
    private lateinit var logoutButton: Button

    private lateinit var usernameInput: EditText
    private lateinit var emailInput: EditText

    private lateinit var currentStreakText: TextView
    private lateinit var longestStreakText: TextView

    private lateinit var calendar: CalendarView

    private lateinit var pomodoroTime: List<DateTime>
    private lateinit var stopwatchTime: List<DateTime>
    private lateinit var preferenceManager: PreferenceManager
}