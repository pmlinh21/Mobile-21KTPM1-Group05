package com.example.applepie

import android.annotation.SuppressLint
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.PorterDuff
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import android.graphics.drawable.Drawable;
import android.util.Log
import com.example.applepie.database.FirebaseManager
import java.util.Locale
import kotlin.properties.Delegates


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        setLanguage()
        setUI()

        getInfoFromPreference()
        getInfoFromFirebase()
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun handleNavbarClick(button: Button){

        // Change the button icon
//        val drawable = ContextCompat.getDrawable(this, R.drawable.ic_home)
//        val newColor = ContextCompat.getColor(this, R.color.green)
//        drawable!!.setColorFilter(newColor, PorterDuff.Mode.SRC_IN)
//        button.setCompoundDrawablesWithIntrinsicBounds(null, null, null, getDrawable(R.drawable.ic_home))
//
//        if (button == homeButton) {
//            homeButton.background = getDrawable(R.drawable.bg_button_active)
//        }
//        else if (button == studyButton) {
//            studyButton.background = getDrawable(R.drawable.bg_button_active)
//        }
//        else if (button == createTaskButton) {
//            createTaskButton.background = getDrawable(R.drawable.bg_button_active)
//        }
//        else if (button == reportButton) {
//            reportButton.background = getDrawable(R.drawable.ic_report)
//        }
//        else if (button == accountButton) {
//            accountButton.background = getDrawable(R.drawable.ic_account)
//        }

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
            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Stopwatch()).addToBackStack(null).commit()
        }

        createTaskButton.setOnClickListener {
            handleNavbarClick(createTaskButton)
//            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, SearchFragment()).addToBackStack(null).commit()
        }

        reportButton.setOnClickListener {
            handleNavbarClick(reportButton)
//            supportFragmentManager.beginTransaction().replace(R.id.fragment_container, FlashlightFragment()).addToBackStack(null).commit()
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

            FirebaseManager.setUserInfo()
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