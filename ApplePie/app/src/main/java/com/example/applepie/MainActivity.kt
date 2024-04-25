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
import androidx.viewpager2.widget.ViewPager2
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.TaskList
import com.example.applepie.model.Task
import com.example.applepie.model.User
import com.example.tablayout.MainViewPagerAdapter
import com.google.firebase.database.DatabaseError
import java.util.Locale


class MainActivity : AppCompatActivity() {

    private lateinit var preferenceManager: PreferenceManager
    private lateinit var username: String
    private lateinit var mainViewPager: ViewPager2
    private lateinit var mainViewPagerAdapter: MainViewPagerAdapter

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
    }

    private fun setupMainViewPager() {
        mainViewPager = findViewById(R.id.main_view_pager)
        mainViewPagerAdapter = MainViewPagerAdapter(this)

        mainViewPager.adapter = mainViewPagerAdapter
        mainViewPager.offscreenPageLimit = 3
        mainViewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        mainViewPager.currentItem = 1
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
//                    setUI()
                    setupMainViewPager()
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
//                    setUI()
                    setupMainViewPager()
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

    private var index: Int = -1
    private var isUserListDataReceived = false
    private var isUserTaskDataReceived = false


}