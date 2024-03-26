package com.example.applepie

import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initUI()
    }

    fun initUI(){
        homeButton = findViewById(R.id.home_icon)
        studyButton = findViewById(R.id.study_icon)
        createTaskButton = findViewById(R.id.create_task_icon)
        reportButton = findViewById(R.id.report_icon)
        accountButton = findViewById(R.id.account_icon)

//        homeButton.setOnClickListener {
//            changeTabColor(homeButton!!, homeTextView!!)
//            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, LoginFragment()).addToBackStack(null).commit()
//        }

        studyButton.setOnClickListener {
            handleMenubarClick(studyButton)
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, Stopwatch()).addToBackStack(null).commit()
        }

//        createTaskButton.setOnClickListener {
//            changeTabColor(searchButton!!, searchTextView!!)
//            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, SearchFragment()).addToBackStack(null).commit()
//        }

//        reportButton.setOnClickListener {
//            changeTabColor(gameButton!!, gameTextView!!)
//            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, FlashlightFragment()).addToBackStack(null).commit()
//        }

        accountButton.setOnClickListener {
//            changeTabColor(profileButton!!, profileTextView!!)
            supportFragmentManager.beginTransaction().replace(R.id.frame_layout, Account()).addToBackStack(null).commit()
        }
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    fun handleMenubarClick(button: Button){
        homeButton.background = getDrawable(R.drawable.ic_home)
        studyButton.background = getDrawable(R.drawable.ic_study)
        createTaskButton.background = getDrawable(R.drawable.ic_create_task)
        reportButton.background = getDrawable(R.drawable.ic_report)
        accountButton.background = getDrawable(R.drawable.ic_account)

        homeButton.setBackgroundColor(Color.TRANSPARENT)
        studyButton.setBackgroundColor(Color.TRANSPARENT)
        createTaskButton.setBackgroundColor(Color.TRANSPARENT)
        reportButton.setBackgroundColor(Color.TRANSPARENT)
        accountButton.setBackgroundColor(Color.TRANSPARENT)

        button.setBackgroundColor(this.resources.getColor(R.color.light_green)) // Replace with your color resource name

        if (button == homeButton) {
            homeButton.background = getDrawable(R.drawable.ic_home)

        }
        else if (button == studyButton) {
            studyButton.background = getDrawable(R.drawable.ic_study)

        }
        else if (button == createTaskButton) {
            createTaskButton.background = getDrawable(R.drawable.ic_create_task)

        }
        else if (button == reportButton) {
            reportButton.background = getDrawable(R.drawable.ic_report)

        }
        else if (button == accountButton) {
            accountButton.background = getDrawable(R.drawable.ic_account)
        }
    }

    private lateinit var homeButton: Button
    private lateinit var studyButton: Button
    private lateinit var createTaskButton: Button
    private lateinit var reportButton: Button
    private lateinit var accountButton: Button
}