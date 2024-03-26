package com.example.applepie

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

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
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val rootView = inflater.inflate(R.layout.fragment_pomodoro, container, false)

        stopwatchButton = rootView.findViewById(R.id.stopwatch_btn)
        undoButton = rootView.findViewById(R.id.undo_btn)
        playButton = rootView.findViewById(R.id.play_btn)
        forwardButton = rootView.findViewById(R.id.forward_btn)
        settingButton = rootView.findViewById(R.id.setting_btn)

        stopwatchButton.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, Stopwatch()).addToBackStack(null).commit()
        }

        undoButton.setOnClickListener {
            // TODO:  add study timer to firebase, reset timer
        }

        playButton.setOnClickListener {
            // TODO:  start timer
        }

        forwardButton.setOnClickListener {
            // TODO:  start timer for break or study
        }

        settingButton.setOnClickListener {
            (activity as AppCompatActivity).supportFragmentManager.beginTransaction().replace(R.id.fragment_container, StudySetting()).addToBackStack("study_setting").commit()
        }

        return rootView
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

    private lateinit var stopwatchButton: Button
    private lateinit var undoButton: Button
    private lateinit var playButton: Button
    private lateinit var forwardButton: Button
    private lateinit var settingButton: Button
}