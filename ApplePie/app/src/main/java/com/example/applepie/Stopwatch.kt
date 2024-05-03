package com.example.applepie

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView
import androidx.annotation.RequiresApi
import com.example.applepie.StopwatchTimer
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
 * Use the [Stopwatch.newInstance] factory method to
 * create an instance of this fragment.
 */
class Stopwatch : Fragment() {
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
        val rootView = inflater.inflate(R.layout.fragment_stopwatch, container, false)

        settingButton = rootView.findViewById(R.id.setting_btn)

        studyToggle = rootView.findViewById(R.id.stopwatch_layout_2)
        pomodoroButton = rootView.findViewById(R.id.pomodoro_btn)

        undoButton = rootView.findViewById(R.id.undo_btn)
        playButton = rootView.findViewById(R.id.play_btn)
        pauseButton = rootView.findViewById(R.id.pause_btn)

        stopwatchTimeText = rootView.findViewById(R.id.stopwatch_time_text)

        start_time = preferenceManager.getStartTime()!!

        updateTimeText()
        updateButton()
        handleButtonClick()

        return rootView
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleButtonClick(){
        Log.i("data",pomodoroButton.isEnabled.toString())
        pomodoroButton.setOnClickListener {

            (activity as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Pomodoro()).addToBackStack(null).commit()
        }

        undoButton.setOnClickListener {
            // TODO:  add study timer to firebase, reset timer
            storeTimeInFirebase()
            StopwatchTimer.stopTimer()
            val stopIntent = Intent(context, MusicService::class.java)
            context?.stopService(stopIntent)
        }

        pauseButton.setOnClickListener {
            // TODO:  pause timer
            storeTimeInFirebase()
            StopwatchTimer.pauseTimer()
        }

        playButton.setOnClickListener {
            // TODO:  start timer
            if (StopwatchTimer.isPause()) {
                StopwatchTimer.resumeTimer()
            } else{
                if (preferenceManager.getMusicStatus() == true) {
                    val playIntent = Intent(context, MusicService::class.java).apply {
                        putExtra("ACTION", "PLAY")
                    }
                    context?.startService(playIntent)
                }
            }

            start_time = getCurrentDateTime()
            preferenceManager.setStartTime(start_time)
            StopwatchTimer.startTimer()
        }

        settingButton.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudySetting()).addToBackStack("study_setting").commit()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun storeTimeInFirebase(){
        val index = preferenceManager.getIndex()

        end_time = getCurrentDateTime()

        Log.i("stopwatch", "$start_time $end_time")
        FirebaseManager.updateStopwatch(index, DateTime(end_time, start_time))
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        StopwatchTimer.setUpdateTimeTextCallback {
            updateTimeText()
        }

        StopwatchTimer.setUpdateButtonTextCallback {
            updateButton()
        }
    }

    fun updateTimeText(){
        val formattedTime = StopwatchTimer.getFormattedTime()

        stopwatchTimeText.text = formattedTime
    }
    fun updateButton(){
        Log.i("timer","update btn")
        if (StopwatchTimer.isStop()){
            playButton.visibility = View.VISIBLE
            pauseButton.visibility = View.GONE
            undoButton.visibility = View.GONE
            pomodoroButton.isEnabled = true
            return
        }
        if (StopwatchTimer.isPause()) {
            playButton.visibility = View.VISIBLE
            pauseButton.visibility = View.GONE
            undoButton.visibility = View.VISIBLE
            pomodoroButton.isEnabled = false
            return
        }
        if (StopwatchTimer.isRunning()) {
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
            undoButton.visibility = View.VISIBLE
            pomodoroButton.isEnabled = false
            return
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
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
         * @return A new instance of fragment Stopwatch.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            Stopwatch().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    private var param1: String? = null
    private var param2: String? = null

    private lateinit var preferenceManager: PreferenceManager

    private lateinit var settingButton: Button

    private lateinit var studyToggle: LinearLayout
    private lateinit var pomodoroButton: Button

    private lateinit var stopwatchTimeText: TextView

    private lateinit var undoButton: Button
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button

    private lateinit var start_time: String
    private lateinit var end_time: String
}



