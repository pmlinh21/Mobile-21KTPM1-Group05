package com.example.applepie

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Typeface
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CalendarView
import android.widget.EditText
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.TextView
import com.example.applepie.database.DataUpdateListener
import androidx.annotation.RequiresApi
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.DateTime
import com.example.applepie.model.Task
import com.example.applepie.model.User
import com.kizitonwose.calendar.core.CalendarDay
import com.kizitonwose.calendar.core.CalendarMonth
import com.kizitonwose.calendar.core.DayPosition
import com.kizitonwose.calendar.core.daysOfWeek
import com.kizitonwose.calendar.core.nextMonth
import com.kizitonwose.calendar.core.previousMonth
import com.kizitonwose.calendar.view.MonthDayBinder
import com.kizitonwose.calendar.view.MonthHeaderFooterBinder
import com.kizitonwose.calendar.view.ViewContainer
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.YearMonth
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_INDEX_USER = "indexUser"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Account.newInstance] factory method to
 * create an instance of this fragment.
 */
class Account : Fragment(), DataUpdateListener {
    // TODO: Rename and change types of parameters
    private var indexUser: Int? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            indexUser = it.getInt(ARG_INDEX_USER)
            param2 = it.getString(ARG_PARAM2)
        }
        preferenceManager = PreferenceManager(requireContext())

    }
    @SuppressLint("MissingInflatedId")
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

        getStudyTime()

        val continuousRanges = countContinuousDateRanges(selectedDates)

        val longestStreak = continuousRanges.maxOrNull() ?: 0
        val currentStreak = continuousRanges.lastOrNull() ?: 0

        longestStreakText.text = longestStreak.toString()
        currentStreakText.text = currentStreak.toString()

        userInfo = FirebaseManager.getUserInfo()
        if (longestStreak > userInfo.longest_streak || currentStreak != userInfo.current_streak) {
            val updateUser = User(
                email = userInfo.email,
                id_user = userInfo.id_user,
                isPremium = userInfo.isPremium,
                password = userInfo.password,
                reminder_duration = userInfo.reminder_duration,
                username = userInfo.username,
                longest_streak = longestStreak,
                current_streak = currentStreak,
                code = userInfo.code
            )
            Log.i("index", preferenceManager.getIndex().toString())
            FirebaseManager.updateUserInfo(preferenceManager.getIndex(), updateUser)
        }

        val daysOfWeek = daysOfWeek()
        val currentMonth = YearMonth.now()
        val startMonth = currentMonth.minusMonths(200)
        val endMonth = currentMonth.plusMonths(200)

        configureBinders(rootView, daysOfWeek)
        calendarView = rootView.findViewById<com.kizitonwose.calendar.view.CalendarView>(R.id.exFiveCalendar)
        calendarView.setup(startMonth, endMonth, daysOfWeek.first())
        calendarView.scrollToMonth(currentMonth)

        calendarView.monthScrollListener = { month ->
            rootView.findViewById<TextView>(R.id.exFiveMonthYearText).text = month.yearMonth.displayText()

            selectedDate?.let {
                selectedDate = null
                calendarView.notifyDateChanged(it)
//                updateAdapterForDate(null)
            }
        }

        val nextMonthButton = rootView.findViewById<ImageView>(R.id.exFiveNextMonthImage)
        nextMonthButton.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.nextMonth)
            }
        }

        val previousMonthButton = rootView.findViewById<ImageView>(R.id.exFivePreviousMonthImage)
        previousMonthButton.setOnClickListener {
            calendarView.findFirstVisibleMonth()?.let {
                calendarView.smoothScrollToMonth(it.yearMonth.previousMonth)
            }
        }

        preferenceManager = PreferenceManager(this.activity)
        FirebaseManager.addDataUpdateListener(this)

        setUI()
        handleEventListener()

        return rootView
    }

    private fun getStudyTime(){
        val dateTimeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")

        pomodoroTime = FirebaseManager.getUserPomodoro() ?: listOf()
        stopwatchTime = FirebaseManager.getUserStopwatch() ?: listOf()

        val allTimes: List<DateTime> = pomodoroTime + stopwatchTime

        val totalSecondsByDate = allTimes
            .groupBy { LocalDateTime.parse(it.start_time, dateTimeFormatter).toLocalDate() }
            .mapValues { (_, times) ->
                times.sumOf { calculateSeconds(it.start_time, it.end_time, dateTimeFormatter) }
            }
            .filter { (_, totalSeconds) -> totalSeconds > 60 }

        totalSecondsByDate.forEach { (date: LocalDate, totalSeconds: Long) ->
            Log.i("streak", "Date: $date, Total Seconds: $totalSeconds")
        }
        selectedDates = totalSecondsByDate.keys.toList()
    }

    private fun countContinuousDateRanges(dates: List<LocalDate>): List<Int> {
        if (dates.isEmpty()) return emptyList()

        val sortedDates = dates.sorted()
        val continuousRanges = mutableListOf<Int>()
        var currentRangeLength = 1

        for (i in 1 until sortedDates.size) {
            if (sortedDates[i] == sortedDates[i - 1].plusDays(1)) {
                currentRangeLength++
            } else {
                continuousRanges.add(currentRangeLength)
                currentRangeLength = 1
            }
        }

        continuousRanges.add(currentRangeLength)

        return continuousRanges
    }
    private fun configureBinders(rootView: View, daysOfWeek: List<DayOfWeek>) {
        val calendarView = rootView.findViewById<com.kizitonwose.calendar.view.CalendarView>(R.id.exFiveCalendar)
        // Container for each day view in the calendar
        class DayViewContainer(view: View) : ViewContainer(view) {
            lateinit var day: CalendarDay
            val textView = view.findViewById<TextView>(R.id.exFiveDayText)
            val layout = view.findViewById<View>(R.id.exFiveDayLayout)
        }

        calendarView.dayBinder = object : MonthDayBinder<DayViewContainer> {
            override fun create(view: View) = DayViewContainer(view)
            override fun bind(container: DayViewContainer, data: CalendarDay) {
                container.day = data
                val context = container.textView.context
                container.textView.text = data.date.dayOfMonth.toString()

                val frameLayout = container.layout.findViewById<FrameLayout>(R.id.exFiveDayFrame)

                if (data.position == DayPosition.MonthDate) {

                    if (selectedDates.contains(data.date)) {
                        container.textView.setTextColorRes(R.color.green)
                        frameLayout.setBackgroundResource(R.color.light_light_green)
                    }


                } else {
                    container.textView.setTextColorRes(R.color.black)
                    frameLayout.setBackgroundResource(R.color.white)
                }
            }
        }

        class MonthViewContainer(view: View) : ViewContainer(view) {
            val legendLayout = view.findViewById<View>(R.id.legendLayout)
        }

        val typeFace = Typeface.create("sans-serif", Typeface.BOLD)
        calendarView.monthHeaderBinder = object : MonthHeaderFooterBinder<MonthViewContainer> {
            override fun create(view: View) = MonthViewContainer(view)

            override fun bind(container: MonthViewContainer, data: CalendarMonth) {
                // Setup each header day text if we have not done that already.
                val containerLayout = container.legendLayout as ViewGroup
                if (containerLayout.tag == null) {
                    containerLayout.tag = data.yearMonth
                    for (index in 0 until containerLayout.childCount) {
                        val child = containerLayout.getChildAt(index)
                        if (child is TextView) {
                            child.text = daysOfWeek.getOrNull(index)?.displayText(uppercase = true) ?: ""
                            child.setTextColorRes(R.color.green)
                            child.setTextSize(TypedValue.COMPLEX_UNIT_SP, 12f)
                            child.typeface = typeFace
                        }
                    }
                }
            }
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
    private lateinit var userInfo: User

    private lateinit var selectedDates: List<LocalDate>
    private var selectedDate: LocalDate? = null
    private lateinit var calendarView: com.kizitonwose.calendar.view.CalendarView

    private lateinit var pomodoroTime: List<DateTime>
    private lateinit var stopwatchTime: List<DateTime>
    private lateinit var preferenceManager: PreferenceManager
    override fun updateData() {
        setUI()
    }
}