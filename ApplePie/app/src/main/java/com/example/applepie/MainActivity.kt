package com.example.applepie

import PomodoroTimer
import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.example.applepie.database.DataUpdateListener
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.TaskList
import com.example.applepie.model.Task
import com.example.applepie.model.User
import com.google.firebase.database.DatabaseError
import java.time.Duration
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Date
import java.util.Locale

class MainActivity() : AppCompatActivity() {
    private lateinit var homeButton: Button
    private lateinit var studyButton: Button
    private lateinit var createTaskButton: Button
    private lateinit var reportButton: Button
    private lateinit var accountButton: Button

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var username: String

    companion object {
        private const val CHANNEL_ID = "reminder_channel"
        private const val CHANNEL_NAME = "Task Reminders"

        @RequiresApi(Build.VERSION_CODES.O)
        @SuppressLint("ServiceCast", "ScheduleExactAlarm")
        fun scheduleNotification(context: Context, dateTime: Date, notificationId: Int, content: String) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            Log.d("reminder", System.currentTimeMillis().toString())
            if (dateTime.time > System.currentTimeMillis()) {
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    putExtra(Notification.EXTRA_NOTIFICATION_ID, notificationId)
                    putExtra("notificationContent", content)
                }
                val pendingIntent = PendingIntent.getBroadcast(
                        context,
                        notificationId,
                        intent,
                        PendingIntent.FLAG_CANCEL_CURRENT  or PendingIntent.FLAG_IMMUTABLE
                    )

                Log.i("AlarmManager 3", dateTime.time.toString())

                try {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        dateTime.time,
                        pendingIntent
                    )
                } catch (e: Exception) {
                    Log.e("AlarmManager", "Error setting alarm: ${e.message}")
                    e.printStackTrace()
                }
            } else {
                Log.w("AlarmManager", "Scheduled time is in the past. Notification not scheduled.")
            }
        }

        fun cancelReminderNoti(context: Context, id_task: String, type: String) {
            val notificationId = Math.abs(id_task.hashCode())
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
            val intent = Intent(context, AlarmReceiver::class.java)

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                notificationId,
                intent,
                 PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
            )

            pendingIntent?.let {
                alarmManager.cancel(it)
            }
        }

        @RequiresApi(Build.VERSION_CODES.O)
        fun makeReminderNoti(context: Context, newDateTime: String, newDuration: Int, id_task: String, title: String){
            val newReminderTime = calculateReminderTime(newDateTime, newDuration)
            if (newReminderTime != null) {
                val formatReminderTime = Date.from(newReminderTime.atZone(ZoneId.systemDefault()).toInstant())
                val notificationId = Math.abs(id_task.hashCode())
                Log.i("reminder-make", id_task)
                Log.i("reminder-make", notificationId.toString())

                scheduleNotification(
                    context,
                    formatReminderTime,
                    notificationId,
                    "Your task $title is soon due"
                )
            }

        }

        fun calculateReminderTime(dateTimeStr: String, durationInMinutes: Int): LocalDateTime? {
            try {
                val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")
                val dateTime = LocalDateTime.parse(dateTimeStr, formatter)

                // Create a duration using the provided minutes
                val duration = Duration.ofMinutes(durationInMinutes.toLong())

                // Subtract the duration from the date time
                return dateTime.minus(duration)
            } catch (e: Exception) {
                // Handle parsing errors
                e.printStackTrace()
            }
            return null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        preferenceManager = PreferenceManager(this)

        if (preferenceManager.isLogin() == false ) {
            val loginActivity = Intent(this, LoginActivity::class.java)
            startActivity(loginActivity)
            finish()
            return
        }

        username = preferenceManager.getUsername().toString()
        index = preferenceManager.getIndex()
        Log.i("index", index.toString())

        getInfoFromFirebase()
        setLanguage()
        setLayout()
        createNotificationChannel()

    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun handleNavbarClick(button: Button){
        // TODO: Change the button icon to @color/green

        // Change the button background
        homeButton.setBackgroundColor(Color.TRANSPARENT)
        studyButton.setBackgroundColor(Color.TRANSPARENT)
        createTaskButton.setBackgroundColor(Color.TRANSPARENT)
        reportButton.setBackgroundColor(Color.TRANSPARENT)
        accountButton.setBackgroundColor(Color.TRANSPARENT)

        button.background = resources.getDrawable(R.drawable.bg_button_active)
    }

    private fun setLayout(){
        homeButton = findViewById(R.id.home_icon)
        studyButton = findViewById(R.id.study_icon)
        createTaskButton = findViewById(R.id.create_task_icon)
        reportButton = findViewById(R.id.report_icon)
        accountButton = findViewById(R.id.account_icon)

        homeButton.setOnClickListener {
            handleNavbarClick(homeButton)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Dashboard()).addToBackStack(null).commit()
        }

        studyButton.setOnClickListener {
            handleNavbarClick(studyButton)
            if (!PomodoroTimer.isStop())
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Pomodoro()).addToBackStack(null).commit()
            else
                supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Stopwatch()).addToBackStack(null).commit()
        }

        createTaskButton.setOnClickListener {
            handleNavbarClick(createTaskButton)
//            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CreateTask()).addToBackStack(null).commit()
            val createTaskFragment = CreateTaskFragment()
            createTaskFragment.show(supportFragmentManager, createTaskFragment.tag)
        }

        reportButton.setOnClickListener {
            handleNavbarClick(reportButton)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Report()).addToBackStack(null).commit()
        }

        accountButton.setOnClickListener {
            handleNavbarClick(accountButton)
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Account()).addToBackStack(null).commit()
        }
    }

    private fun setUI(){
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        Log.i("current fragment", currentFragment.toString())
        if (currentFragment is DataUpdateListener) {
            currentFragment.updateData() // This calls updateData on whichever fragment is currently displayed
        } else if (currentFragment == null) {
            // If no fragment is currently displayed, launch the Dashboard
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, Dashboard())
                .commit()
        }
    }

    private fun getInfoFromFirebase(){
        if (index != -1){
            Log.i("UserInfo",index.toString())
            FirebaseManager.setUserRef(index)
            FirebaseManager.setUserInfoRef(index)
            FirebaseManager.setUserListsRef(index)
            FirebaseManager.setUserTasksRef(index)

            FirebaseManager.setUserInfo(object : FirebaseManager.DataCallback<User> {
                override fun onDataReceived(data: User) {
                    // Handle received user list data
                    Log.i("data", FirebaseManager.getUserInfo().toString())
                    isUserInfoDataReceived = true
                    if (isUserListDataReceived && isUserTaskDataReceived)
                    setUI()
                }

                override fun onError(error: DatabaseError) {
                    // Handle error
                }
            })

            FirebaseManager.setUserList(object : FirebaseManager.DataCallback<List<TaskList>> {
                override fun onDataReceived(data: List<TaskList>) {
                    // Handle received user list data
                    Log.i("data", FirebaseManager.getUserList().toString())

                    isUserListDataReceived = true
                    if (isUserInfoDataReceived && isUserTaskDataReceived)
                    setUI()
                }

                override fun onError(error: DatabaseError) {
                    // Handle error
                }
            })

            FirebaseManager.setUserTask(object : FirebaseManager.DataCallback<List<Task>> {
                override fun onDataReceived(data: List<Task>) {
                    // Handle received user list data
                    data.forEachIndexed { index, task ->
                        Log.i("FirebaseManager", "Task $index: $task")
                    }

                    isUserTaskDataReceived = true
                    if (isUserInfoDataReceived && isUserListDataReceived)
                    setUI()
                }

                override fun onError(error: DatabaseError) {
                    // Handle error
                }
            })

            FirebaseManager.setUserPomodoro(index)
            FirebaseManager.setUserStopwatch(index)
            FirebaseManager.setUserBlockNotiApp(index)
            FirebaseManager.setUserMusic(index)
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = "Notifications for task reminder"
            }
            // Register the channel with the system
            val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun setLanguage(){
        Locale.setDefault(Locale.ENGLISH)
        val config = Configuration()
        config.locale = Locale.ENGLISH
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    override fun onDestroy() {
        super.onDestroy()
        val stopIntent = Intent(baseContext, MusicService::class.java)
        baseContext.stopService(stopIntent)
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // Go to task detail
        Log.d("dsafa", requestCode.toString())
        if (resultCode == AppCompatActivity.RESULT_OK) {
            val result = data?.getStringExtra("taskId")
            val fragment = TaskDetails.newInstance(result!!)
            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(null)
                .commit()
        }
    }

    private var index: Int = -1
    private var isUserInfoDataReceived = false
    private var isUserListDataReceived = false
    private var isUserTaskDataReceived = false
}