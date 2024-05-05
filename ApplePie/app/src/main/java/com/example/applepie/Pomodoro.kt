package com.example.applepie

import PomodoroTimer
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.example.applepie.database.FirebaseManager
import com.example.applepie.database.PreferenceManager
import com.example.applepie.model.DateTime
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [Pomodoro.newInstance] factory method to
 * create an instance of this fragment.
 */
class Pomodoro : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }

        preferenceManager = PreferenceManager(requireContext())
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_pomodoro, container, false)

        stopwatchButton = rootView.findViewById(R.id.stopwatch_btn)
        undoButton = rootView.findViewById(R.id.undo_btn)
        playButton = rootView.findViewById(R.id.play_btn)
        pauseButton = rootView.findViewById(R.id.pause_btn)
        forwardButton = rootView.findViewById(R.id.forward_btn)
        settingButton = rootView.findViewById(R.id.setting_btn)
        pomodoroTimeText = rootView.findViewById(R.id.pomodoro_time_text)
        pomodoroModeText = rootView.findViewById(R.id.pomodoro_mode_text)
        progressBar = rootView.findViewById(R.id.progressBar)
        progressBar.max = 100

        start_time = preferenceManager.getStartTime()!!

        updateTimeText()
        updateModeText()
        updateButton()
        handleButtonClick()

        return rootView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleButtonClick(){
        stopwatchButton.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Stopwatch()).addToBackStack(null).commit()
        }

        settingButton.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudySetting()).addToBackStack("study_setting").commit()
        }

        undoButton.setOnClickListener {
            // TODO: save firebase, reset timer
            storeTimeInFirebase()
            PomodoroTimer.stopTimer()

            if (preferenceManager.getMusicStatus() == true){
                val stopIntent = Intent(context, MusicService::class.java)
                context?.stopService(stopIntent)
            }

        }

        playButton.setOnClickListener {
            // TODO:  start timer
            if (PomodoroTimer.isPause()){
                PomodoroTimer.resumeTimer()
            }

            else{
                if (preferenceManager.getMusicStatus() == true) {
                    val playIntent = Intent(context, MusicService::class.java).apply {
                        putExtra("ACTION", "PLAY")
                        putExtra("MUSIC_RES_ID", FirebaseManager.getUserMusic().resourceId)
                    }
                    context?.startService(playIntent)
                }
            }

            setStartTime()
            PomodoroTimer.startTimer()
        }

        forwardButton.setOnClickListener {
            // TODO:  start timer for break or study, save firebase

            storeTimeInFirebase()
            PomodoroTimer.switchMinute()
            PomodoroTimer.stopTimer()
        }

        pauseButton.setOnClickListener {
            // TODO:  pause timer for break or study

            storeTimeInFirebase()
            PomodoroTimer.pauseTimer()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun setStartTime(){
        if (PomodoroTimer.getMode() == "Focus"){
            start_time = getCurrentDateTime()
            preferenceManager.setStartTime(start_time)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun storeTimeInFirebase(){
        if (PomodoroTimer.getMode() == "Focus"){
            val index = preferenceManager.getIndex()

            end_time = getCurrentDateTime()

            Log.i("pomodoro", "$start_time $end_time")
            FirebaseManager.updatePomodoro(index, DateTime(end_time, start_time))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        PomodoroTimer.setUpdateTimeTextCallback { updateTimeText() }
        PomodoroTimer.setUpdateButtonCallback { updateButton() }
        PomodoroTimer.setUpdateModeTextCallback (::updateModeText)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getCurrentDateTime(): String {

        val currentDateTime = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        return currentDateTime.format(formatter)
    }
    private fun updateTimeText() {
        val formattedTime = PomodoroTimer.getFormattedTime()
        pomodoroTimeText.text = formattedTime

        progressBar.progress = PomodoroTimer.getProgressValue()
    }

    private fun updateModeText() {
        val mode = PomodoroTimer.getMode()
        pomodoroModeText.text = mode
    }
    private fun updateButton(){
        if (PomodoroTimer.isTimeOver()){
            undoButton.visibility = View.VISIBLE
            playButton.visibility = View.GONE
            pauseButton.visibility = View.GONE
            forwardButton.visibility = View.VISIBLE
            stopwatchButton.isEnabled = true
            return
        }
        if (PomodoroTimer.isRunning()){
            undoButton.visibility = View.VISIBLE
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
            forwardButton.visibility = View.VISIBLE
            stopwatchButton.isEnabled = false
            return
        }
        if (PomodoroTimer.isPause()){
            undoButton.visibility = View.VISIBLE
            playButton.visibility = View.VISIBLE
            pauseButton.visibility = View.GONE
            forwardButton.visibility = View.VISIBLE
            stopwatchButton.isEnabled = false
            return
        }
        if (PomodoroTimer.isStop()){
            undoButton.visibility = View.GONE
            playButton.visibility = View.VISIBLE
            pauseButton.visibility = View.GONE
            forwardButton.visibility = View.VISIBLE
            stopwatchButton.isEnabled = true
            return
        }

    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment Pomodoro.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Pomodoro().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private lateinit var preferenceManager: PreferenceManager

    private lateinit var stopwatchButton: Button
    private lateinit var undoButton: Button
    private lateinit var playButton: Button
    private lateinit var forwardButton: Button
    private lateinit var settingButton: Button
    private lateinit var pomodoroTimeText: TextView
    private lateinit var pomodoroModeText: TextView
    private lateinit var pauseButton: Button
    private lateinit var progressBar: ProgressBar

    private lateinit var start_time: String
    private lateinit var end_time: String
}