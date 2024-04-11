package com.example.applepie

import PomodoroTimer
import android.annotation.SuppressLint
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.util.Log
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.TaskList
import com.example.applepie.model.Task
import com.example.applepie.model.User
import com.google.firebase.database.DatabaseError
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var username: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_main)

        preferenceManager = PreferenceManager(this)
        username = preferenceManager.getUsername().toString()

        getInfoFromPreference()
        getInfoFromFirebase()

        if (preferenceManager.isLogin() == false) {
            val loginActivity = Intent(this, LoginActivity::class.java)
            startActivity(loginActivity)
            finish()
        }

        setLanguage()
//        setUI()
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

        button.background = getDrawable(R.drawable.bg_button_active)
    }

    fun setUI(){
        homeButton = findViewById(R.id.home_icon)
        studyButton = findViewById(R.id.study_icon)
        createTaskButton = findViewById(R.id.create_task_icon)
        reportButton = findViewById(R.id.report_icon)
        accountButton = findViewById(R.id.account_icon)

        supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Dashboard()).addToBackStack(null).commit()

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
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, CreateTask()).addToBackStack(null).commit()
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

    fun getInfoFromPreference(){
    // TODO: get index of the specific user in firebase from preference
    }

    fun getInfoFromFirebase(){
        index = 0

        if (index != -1){
            Log.i("UserInfo","0")
            FirebaseManager.setUserRef(0)
            FirebaseManager.setUserInfoRef(0)
            FirebaseManager.setUserListsRef(0)
            FirebaseManager.setUserTasksRef(0)

            FirebaseManager.setUserList(object : FirebaseManager.DataCallback<List<TaskList>> {
                override fun onDataReceived(data: List<TaskList>) {
                    // Handle received user list data
                    Log.i("data", FirebaseManager.getUserList().toString())

                    setUI()
                }

                override fun onError(error: DatabaseError) {
                    // Handle error
                }
            })

            FirebaseManager.setUserInfo(object : FirebaseManager.DataCallback<User> {
                override fun onDataReceived(data: User) {
                    // Handle received user list data
                    Log.i("data", FirebaseManager.getUserInfo().toString())
                }

                override fun onError(error: DatabaseError) {
                    // Handle error
                }
            })

            FirebaseManager.setUserTask(object : FirebaseManager.DataCallback<List<Task>> {
                override fun onDataReceived(data: List<Task>) {
                    // Handle received user list data
                    Log.i("data", FirebaseManager.getUserTask().toString())
                }

                override fun onError(error: DatabaseError) {
                    // Handle error
                }
            })

            FirebaseManager.setUserAllowedNotiApp(0)
        }
    }

    fun setLanguage(){
        Locale.setDefault(Locale.ENGLISH)
        val config = Configuration()
        config.locale = Locale.ENGLISH
        baseContext.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }

    private var index: Int = -1

    private lateinit var homeButton: Button
    private lateinit var studyButton: Button
    private lateinit var createTaskButton: Button
    private lateinit var reportButton: Button
    private lateinit var accountButton: Button
}