package com.example.applepie

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import android.widget.TextView

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_stopwatch, container, false)

        pomodoroButton = rootView.findViewById(R.id.pomodoro_btn)
        undoButton = rootView.findViewById(R.id.undo_btn)
        playButton = rootView.findViewById(R.id.play_btn)
        pauseButton = rootView.findViewById(R.id.pause_btn)
        settingButton = rootView.findViewById(R.id.setting_btn)
        stopwatchTimeText = rootView.findViewById(R.id.stopwatch_time_text)

        undoButton.visibility = View.GONE
        pauseButton.visibility = View.GONE

        pomodoroButton.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Pomodoro()).addToBackStack(null).commit()
        }

        undoButton.setOnClickListener {
            // TODO:  add study timer to firebase, reset timer
            timerManager.resetTimer()
            timerManager.stopTimer()
            updateTimeText()
            
            undoButton.visibility = View.GONE
            playButton.visibility = View.VISIBLE
            pauseButton.visibility = View.GONE
        }

        pauseButton.setOnClickListener {
            // TODO:  pause timer
            timerManager.pauseTimer()

            playButton.visibility = View.VISIBLE
            pauseButton.visibility = View.GONE
        }

        playButton.setOnClickListener {
            // TODO:  start timer
            if (timerManager.getPause()){
                timerManager.resumeTimer()
            }
            timerManager.startTimer()

            undoButton.visibility = View.VISIBLE
            playButton.visibility = View.GONE
            pauseButton.visibility = View.VISIBLE
        }

        settingButton.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudySetting()).addToBackStack("study_setting").commit()
        }

        timerManager = TimerManager()
        timerManager.setUpdateCallback { updateTimeText() }

        return rootView
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        timerManager = TimerManager()
        timerManager.setUpdateCallback { updateTimeText() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        timerManager.stopTimer()
    }

    private fun updateTimeText() {
        val formattedTime = timerManager.getFormattedTime()
        // Update your UI with formattedTime
        stopwatchTimeText.text = formattedTime
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

    private lateinit var pomodoroButton: Button
    private lateinit var undoButton: Button
    private lateinit var playButton: Button
    private lateinit var pauseButton: Button
    private lateinit var settingButton: Button
    private lateinit var stopwatchTimeText: TextView
    private lateinit var timerManager: TimerManager
}



