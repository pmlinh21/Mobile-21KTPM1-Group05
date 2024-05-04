package com.example.applepie

import PomodoroTimer
import android.annotation.SuppressLint
import android.app.Dialog
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import android.view.Gravity
import android.view.ViewGroup
import android.view.Window
import com.example.applepie.database.DataUpdateListener
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.TaskList
import com.example.applepie.model.Task
import com.example.applepie.model.User
import com.google.firebase.database.DatabaseError
import java.util.Locale


class MainActivity : AppCompatActivity() {
    private lateinit var homeButton: Button
    private lateinit var studyButton: Button
    private lateinit var createTaskButton: Button
    private lateinit var reportButton: Button
    private lateinit var accountButton: Button

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var username: String

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
        index = preferenceManager.getIndex()!!
        Log.i("index", index.toString())

        getInfoFromFirebase()
        setLanguage()
        setLayout()
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
                    setUI()
                }

                override fun onError(error: DatabaseError) {
                    // Handle error
                }
            })

            FirebaseManager.setUserTask(object : FirebaseManager.DataCallback<List<Task>> {
                override fun onDataReceived(data: List<Task>) {
                    // Handle received user list data
                    Log.i("data", FirebaseManager.getUserTask().toString())

                    isUserTaskDataReceived = true
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
            FirebaseManager.setUserReminder(index)
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


    private var index: Int = -1
    private var isUserListDataReceived = false
    private var isUserTaskDataReceived = false
    private var isInitApp = true

}